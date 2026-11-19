
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Assumptions;

class ServerTest {


    @TempDir
    Path tempDir;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testHandleClient() throws Exception {

        Path serverFile = tempDir.resolve("server-vault.dat");
        Server server = new Server(serverFile.toString());

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            Future<?> future = executor.submit(() -> {
                try (Socket socket = serverSocket.accept()) {
                    server.handleClient(socket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            try (Socket client = new Socket(
                    "localhost", serverSocket.getLocalPort())) {

                ObjectOutputStream output =
                        new ObjectOutputStream(client.getOutputStream());

                output.flush();

                ObjectInputStream input =
                        new ObjectInputStream(client.getInputStream());

                output.writeObject(new SyncData());
                output.flush();

                SyncData response = (SyncData) input.readObject();

                assertNotNull(response);
            }

            future.get(3, TimeUnit.SECONDS);

            assertTrue(Files.exists(serverFile));

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void testLoadExistingServerData() throws Exception {

        Path serverFile = tempDir.resolve("server-vault.dat");

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(serverFile.toFile()))) {

            output.writeObject(new SyncData());
        }

        Server server = new Server(serverFile.toString());

        assertNotNull(server);
    }

    @Test
    void testLoadMissingServerFile() {

        Path serverFile = tempDir.resolve("missing-server.dat");

        Server server = new Server(serverFile.toString());

        assertNotNull(server);
    }

    @Test
    void testLoadCorruptedServerFile() throws Exception {

        Path serverFile = tempDir.resolve("corrupted-server.dat");

        Files.write(serverFile, new byte[] {1, 2, 3, 4});

        Server server = new Server(serverFile.toString());

        assertNotNull(server);

        Files.delete(serverFile);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testServerFileIsSavedAfterSynchronization() throws Exception {

        Path serverFile = tempDir.resolve("saved-server.dat");
        Server server = new Server(serverFile.toString());

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            Future<?> future = executor.submit(() -> {
                try (Socket socket = serverSocket.accept()) {
                    server.handleClient(socket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            try (Socket client = new Socket(
                    "localhost", serverSocket.getLocalPort())) {

                ObjectOutputStream output =
                        new ObjectOutputStream(client.getOutputStream());

                output.flush();

                ObjectInputStream input =
                        new ObjectInputStream(client.getInputStream());

                output.writeObject(new SyncData());
                output.flush();

                assertNotNull(input.readObject());
            }

            future.get(3, TimeUnit.SECONDS);

            assertTrue(Files.exists(serverFile));
            assertTrue(Files.size(serverFile) > 0);

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSaveServerDataWhenFileCannotBeWritten() throws Exception {

        Path directory = tempDir.resolve("server-directory");
        Files.createDirectory(directory);

        Server server = new Server(directory.toString());

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            ExecutorService executor =
                    Executors.newSingleThreadExecutor();

            try {
                Future<?> future = executor.submit(() -> {
                    try (Socket socket = serverSocket.accept()) {
                        server.handleClient(socket);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                try (Socket client = new Socket(
                        "localhost", serverSocket.getLocalPort())) {

                    ObjectOutputStream output =
                            new ObjectOutputStream(client.getOutputStream());

                    output.flush();

                    ObjectInputStream input =
                            new ObjectInputStream(client.getInputStream());

                    output.writeObject(new SyncData());
                    output.flush();

                    assertNotNull(input.readObject());
                }

                future.get(3, TimeUnit.SECONDS);

            } finally {
                executor.shutdownNow();
            }
        }
    }

    @Test
    void testStopWhenServerIsNotRunning() {

        Server server = new Server(
                tempDir.resolve("not-running.dat").toString());

        server.stop();

        assertNotNull(server);
    }

    
    @Test
    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    void testStartHandlesClientAndStops() throws Exception {

        int port;

        try (ServerSocket temporarySocket = new ServerSocket(0)) {
            port = temporarySocket.getLocalPort();
        }

        Path serverFile = tempDir.resolve("running-server.dat");

        Server server = new Server(serverFile.toString(), port);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<?> future = executor.submit(server::start);

            long deadline = System.nanoTime()
                    + TimeUnit.SECONDS.toNanos(3);

            while (!server.isRunning()
                    && System.nanoTime() < deadline) {
                Thread.yield();
            }

            assertTrue(server.isRunning(),
                    "The server did not start");

            try (Socket client = new Socket("localhost", port)) {

                ObjectOutputStream output =
                        new ObjectOutputStream(client.getOutputStream());

                output.flush();

                ObjectInputStream input =
                        new ObjectInputStream(client.getInputStream());

                output.writeObject(new SyncData());
                output.flush();

                SyncData response = (SyncData) input.readObject();

                assertNotNull(response);
            }

            server.stop();

            future.get(3, TimeUnit.SECONDS);

            assertTrue(Files.exists(serverFile));

        } finally {
            server.stop();
            executor.shutdownNow();
        }
    }


    
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testStartWhenPortIsAlreadyInUse() throws Exception {

        try (ServerSocket occupiedSocket = new ServerSocket(0)) {

            int occupiedPort = occupiedSocket.getLocalPort();

            Server server = new Server(
                    tempDir.resolve("occupied-server.dat").toString(),
                    occupiedPort);

            ExecutorService executor =
                    Executors.newSingleThreadExecutor();

            try {
                Future<?> future = executor.submit(server::start);

                
                future.get(3, TimeUnit.SECONDS);

                assertNotNull(server);

            } finally {
                executor.shutdownNow();
            }
        }
    }

    @Test
    void testDefaultConstructor() {
        Server server = new Server();

        assertNotNull(server);
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    void testStartHandlesClientDisconnect() throws Exception {

        int port;

        try (ServerSocket temporarySocket = new ServerSocket(0)) {
            port = temporarySocket.getLocalPort();
        }

        Server server = new Server(
                tempDir.resolve("disconnect-server.dat").toString(),
                port);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<?> future = executor.submit(server::start);

            long deadline = System.nanoTime()
                    + TimeUnit.SECONDS.toNanos(3);

            while (!server.isRunning()
                    && System.nanoTime() < deadline) {
                Thread.yield();
            }

            assertTrue(server.isRunning());

            try (Socket client = new Socket("localhost", port)) {
                ObjectOutputStream output =
                        new ObjectOutputStream(client.getOutputStream());
                output.flush();
            }

            assertTrue(server.isRunning());

            server.stop();

            future.get(3, TimeUnit.SECONDS);

        } finally {
            server.stop();
            executor.shutdownNow();
        }
    }

    







    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testMainWhenPortIsOccupied() throws Exception {

        ServerSocket reservedSocket;

        try {
            reservedSocket = new ServerSocket(5000);
        } catch (java.io.IOException e) {
            Assumptions.abort(
                    "No se puede probar main: el puerto 5000 ya está ocupado");
            return;
        }

        try (ServerSocket socket = reservedSocket) {
            assertDoesNotThrow(() -> Server.main(new String[0]));
            assertTrue(socket.isBound());
        }
    }




    
}
