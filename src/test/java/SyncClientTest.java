

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class SyncClientTest {

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void synchronizeSendsAndReceivesData() throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            int port = serverSocket.getLocalPort();

            ExecutorService executor =
                    Executors.newSingleThreadExecutor();

            try {
                Future<SyncData> serverResult =
                        executor.submit(() -> {

                    try (Socket socket = serverSocket.accept()) {

                        ObjectOutputStream output =
                                new ObjectOutputStream(
                                        socket.getOutputStream());

                        output.flush();

                        ObjectInputStream input =
                                new ObjectInputStream(
                                        socket.getInputStream());

                        SyncData received =
                                (SyncData) input.readObject();

                        User responseUser =
                                new User(2, "ana", "5678");

                        ArrayList<User> users = new ArrayList<>();
                        users.add(responseUser);

                        SyncData response =
                                new SyncData(users, new HashMap<>());

                        output.writeObject(response);
                        output.flush();

                        return received;
                    }
                });

                User localUser = new User(1, "ibai", "1234");

                ArrayList<User> localUsers = new ArrayList<>();
                localUsers.add(localUser);

                SyncData localData =
                        new SyncData(localUsers, new HashMap<>());

                SyncData result = SyncClient.synchronize(
                        "localhost", port, localData);

                SyncData received = serverResult.get(
                        3, TimeUnit.SECONDS);

                assertNotNull(received);
                assertEquals(1, received.getUsers().size());
                assertEquals(
                        "ibai",
                        received.getUsers().get(0).getUsername());

                assertNotNull(result);
                assertEquals(1, result.getUsers().size());
                assertEquals(
                        "ana",
                        result.getUsers().get(0).getUsername());

            } finally {
                executor.shutdownNow();
            }
        }
    }
}
