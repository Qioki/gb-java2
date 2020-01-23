package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ClientServer.Msg;

public class Main extends Application {


    private static Main appContext;
    private ChatController chatController;

    public static boolean isAuthorized = false;

    private String userLogin = "";


    final String IP_ADPRESS = "localhost";
    final int PORT = 8187;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;


    public static void main(String[] args) {
        launch(args);
    }



    public void sendToServer(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMsg(String toUser, String msg) {
        if(msg.startsWith(Msg._blacklist)) {
            String[] queryInfo = msg.split(" ", 2);
            if(queryInfo.length > 1)
                sendToServer(Msg._query + Msg._blacklist + " " + userLogin + " " + queryInfo[1]);
        }
        else
            sendToServer(Msg._msginfo + userLogin + " " + toUser + " " + msg);
    }
    public void logOut() {
        isAuthorized = false;
        sendToServer(Msg._query + Msg._logout);
    }
    public void tryToAuth(String userName, String password) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        sendToServer(Msg._query + Msg._auth + " " + userName + " " + password);
    }
    public void connect() {

        try {
            socket = new Socket(IP_ADPRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println(str);

                        if (str.startsWith(Msg._query)) {
                            String[] tokes = str.split(" ");
                            switch (tokes[1]) {
                                case Msg._authok:
                                    isAuthorized = true;
                                    userLogin = tokes[2];
                                    chatController.setAuthorized(userLogin);
                                    break;
                                case Msg._friends:
                                    String[] friendsInfo = str.split(" ", 3);
                                    if(friendsInfo.length > 2) {
                                        friendsInfo = friendsInfo[2].split(" ");
                                        chatController.updateFriendList(friendsInfo);
                                    }
                                    break;
                                case Msg._online:
                                    String[] onLineInfo = str.split(" ", 3);
                                    if(onLineInfo.length > 2) {
                                        onLineInfo = onLineInfo[2].split(" ");
                                        chatController.updateFriendList(onLineInfo);
                                    }
                                    break;

                            }
                        }
                        else if (str.startsWith(Msg._msginfo)) {
                            String[] msgInfo = str.split(" ", 4);
                            chatController.receiveMsg(msgInfo[1], msgInfo[2], msgInfo[3]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    chatController.logOut();
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/views/chat_layout.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 550, 550));
        primaryStage.setTitle("Чат");
        primaryStage.show();

        chatController = loader.getController();
    }

    public Main() {
        super();
        appContext = this;
    }
    public static Main getAppContext() {
        return appContext;
    }
}
