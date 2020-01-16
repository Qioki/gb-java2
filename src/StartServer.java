import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StartServer implements Closeable {

    private final int PORT = 8189;

    private ConsoleChat chat = null;
    private ServerSocket server = null;
    private Socket socket = null;


    public static void main(String[] args) {  new StartServer();  }


    public StartServer() {

        try {

            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");

            socket = server.accept();
            System.out.println("Клиент подключился");
            chat = new ConsoleChat(this, socket);

        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {

        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            server.close();
        }
        catch (IOException e) {  e.printStackTrace();  }
    }

}
