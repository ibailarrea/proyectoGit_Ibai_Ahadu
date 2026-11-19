import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncClient {

    public static SyncData synchronize(
            String host, int port, SyncData localData)
            throws IOException, ClassNotFoundException {

        try (Socket socket = new Socket(host, port)) {

            ObjectOutputStream output =
                    new ObjectOutputStream(socket.getOutputStream());

            output.flush();

            ObjectInputStream input =
                    new ObjectInputStream(socket.getInputStream());

            output.writeObject(localData);
            output.flush();

            SyncData receivedData = (SyncData) input.readObject();

            return receivedData;
        }
    }
}