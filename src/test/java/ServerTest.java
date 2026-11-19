
import static org.junit.jupiter.api.Assertions.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServerTest {

    @TempDir
    Path tempDir;

    @Test
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

                SyncData data = new SyncData();

                output.writeObject(data);
                output.flush();

                SyncData response = (SyncData) input.readObject();

                assertNotNull(response);
            }

            future.get(3, TimeUnit.SECONDS);

            // Comprobamos que se ha guardado el archivo.
            assertTrue(java.nio.file.Files.exists(serverFile));

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void testLoadExistingServerData() throws Exception {

        Path serverFile = tempDir.resolve("server-vault.dat");

        try (ObjectOutputStream output = new ObjectOutputStream(
                new java.io.FileOutputStream(serverFile.toFile()))) {

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



}
