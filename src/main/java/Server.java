
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.atomic.AtomicReference;

public class Server {

    private static final Logger LOGGER =
            Logger.getLogger(Server.class.getName());

    private static final int PORT = 5000;
    private static final String SERVER_FILE = "server-vault.dat";

    private final String serverFile;
    private SyncData serverData;

    
    private volatile boolean running;
    private final AtomicReference<ServerSocket> serverSocket =
            new AtomicReference<>();

    private final int serverPort;

    
    public Server() {
        this(SERVER_FILE, PORT);
    }

    Server(String serverFile) {
        this(serverFile, PORT);
    }

    Server(String serverFile, int serverPort) {
        this.serverFile = serverFile;
        this.serverPort = serverPort;
        serverData = new SyncData();
        loadServerData();
    }

    
    private void loadServerData() {
        try {
            byte[] fileData = Files.readAllBytes(Paths.get(serverFile));

            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(fileData))) {

                serverData = (SyncData) input.readObject();
                LOGGER.info("Server data loaded.");
            }

        } catch (NoSuchFileException e) {
            LOGGER.info("No server data found. Starting empty.");

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE,
                    "Error loading server data: " + e.getMessage(), e);
        }
    }


    private void saveServerData() {
        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(serverFile))) {

            output.writeObject(serverData);
            LOGGER.info("Server data saved.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "Error saving server data: " + e.getMessage(), e);
        }
    }

    public void handleClient(Socket clientSocket)
            throws IOException, ClassNotFoundException {

        ObjectOutputStream output =
                new ObjectOutputStream(clientSocket.getOutputStream());

        output.flush();

        ObjectInputStream input =
                new ObjectInputStream(clientSocket.getInputStream());

        SyncData clientData = (SyncData) input.readObject();

        serverData.merge(clientData);
        saveServerData();

        output.writeObject(serverData);
        output.flush();

        LOGGER.info("Synchronization completed.");
    }

    public void start() {
        try (ServerSocket listeningSocket = new ServerSocket(serverPort)) {

            serverSocket.set(listeningSocket);
            running = true;

            LOGGER.info("Server started on port " + serverPort);

            acceptClients(listeningSocket);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "Server error: " + e.getMessage(), e);

        } finally {
            running = false;
            serverSocket.set(null);
        }
    }

    private void acceptClients(ServerSocket listeningSocket) {
        while (running) {
            try {
                handleNextClient(listeningSocket);

            } catch (SocketException e) {
                if (running) {
                    LOGGER.log(Level.WARNING,
                            "Socket error while accepting client.", e);
                }
                break;

            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    LOGGER.log(Level.WARNING,
                            "Client error: " + e.getMessage(), e);
                }
            }
        }
    }

    private void handleNextClient(ServerSocket listeningSocket)
            throws IOException, ClassNotFoundException {

        try (Socket clientSocket = listeningSocket.accept()) {
            LOGGER.info("Client connected.");
            handleClient(clientSocket);
        }
    }

    
    public void stop() {
        running = false;

        ServerSocket currentSocket = serverSocket.getAndSet(null);

        if (currentSocket != null) {
            try {
                currentSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING,
                        "Error closing server socket.", e);
            }
        }
    }

    boolean isRunning() {
    return running;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}