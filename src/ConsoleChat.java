
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleChat implements Closeable {


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner scanner = null;
    Closeable user;

    public ConsoleChat(Closeable user, Socket socket) {

        this.user = user;

        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.scanner = new Scanner(System.in);


            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println("Received: " + str);
                        if (str.equals("/end")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                } finally {
                    try {
                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0); // К сожалению не хватило времени разобраться со Scanner. Как прервать nextLine я так и не понял.

            }).start();



            new Thread(() -> {
                try {
                    while (true) {
                        String str = scanner.nextLine();
                        out.writeUTF(str);

                        if (str.equals("/end")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {

        if(!socket.isClosed()) {
            System.out.println("Соединение разорвано");
            try {
                out.close();
            } finally {
                try {
                    in.close();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        user.close();
    }
}
