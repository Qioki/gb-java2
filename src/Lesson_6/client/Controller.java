package Lesson_6.client;

/*import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;


public class Controller //implements Initializable 
{
    /*@FXML
    TextArea textArea;

    @FXML
    TextField textField;*/

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADPRESS = "localhost";
    final int PORT = 8189;

    
    private Scanner scanner = null;

    //@Override
    //public void initialize(URL location, ResourceBundle resources) {
    public Controller() {
        try {
            socket = new Socket(IP_ADPRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            this.scanner = new Scanner(System.in);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/serverClosed")) break;
                            System.out.println("Received: " + str);
                            //textArea.appendText(str + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
                        socket.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void sendMsg() {
        try {
            out.writeUTF("");
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
