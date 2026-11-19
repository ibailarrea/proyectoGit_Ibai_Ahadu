import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileNotFoundException;

public class Server {

    private static final int PORT = 5000;
    private static final String SERVER_FILE = "server-vault.dat";

    private SyncData serverData;

    public Server() {
        serverData = new SyncData();
        loadServerData();
    }

    private void loadServerData() {

        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(SERVER_FILE))) {

            serverData = (SyncData) input.readObject();
            System.out.println("Server data loaded.");

        } catch (FileNotFoundException e) {
            System.out.println("No server data found. Starting empty.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading server data: "
                    + e.getMessage());
        }
    }

    private void saveServerData() {

        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(SERVER_FILE))) {

            output.writeObject(serverData);
            System.out.println("Server data saved.");

        } catch (IOException e) {
            System.out.println("Error saving server data: "
                    + e.getMessage());
        }
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server started on port " + PORT);

            while (true) {

                System.out.println("Waiting for clients...");

                try (Socket clientSocket = serverSocket.accept()) {

                    System.out.println("Client connected.");

                    ObjectOutputStream output =
                            new ObjectOutputStream(
                                    clientSocket.getOutputStream());

                    output.flush();

                    ObjectInputStream input =
                            new ObjectInputStream(
                                    clientSocket.getInputStream());

                    SyncData clientData =
                            (SyncData) input.readObject();

                    // Combinamos los datos del cliente con los del servidor.
                    serverData.merge(clientData);

                    saveServerData();

                    // Devolvemos los datos actualizados al cliente.
                    output.writeObject(serverData);
                    output.flush();

                    System.out.println("Synchronization completed.");

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Client error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}