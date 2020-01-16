import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class StartClient implements Closeable {

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;

    private Socket socket;
    private ConsoleChat chat = null;



    public static void main(String[] args) {  new StartClient();  }



    public StartClient() {

        try {

            socket = new Socket(IP_ADDRESS, PORT);
            chat = new ConsoleChat(this, socket);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

    }

    @Override
    public void close() {

        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
