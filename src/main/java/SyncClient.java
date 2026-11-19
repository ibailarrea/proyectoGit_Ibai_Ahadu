import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncClient {

    public static SyncData synchronize(
            String host, int port, SyncData localData)
            throws IOException, ClassNotFoundException {

        try (Socket socket = new Socket(host, port)) {

            // Primero creamos y enviamos el flujo de salida.
            ObjectOutputStream output =
                    new ObjectOutputStream(socket.getOutputStream());

            output.flush();

            // Después creamos el flujo de entrada.
            ObjectInputStream input =
                    new ObjectInputStream(socket.getInputStream());

            // Enviamos nuestros datos.
            output.writeObject(localData);
            output.flush();

            // Recibimos los datos actualizados.
            SyncData receivedData = (SyncData) input.readObject();

            return receivedData;
        }
    }
}