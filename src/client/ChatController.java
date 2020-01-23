package client;

import ClientServer.Msg;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class ChatController implements Initializable {

    Main app;

    private boolean isAuthorized;


    private TreeMap<String, ArrayList<UserMessage>> localStoreChats = new TreeMap<>();
    private TreeMap<String, String> friendNames = new TreeMap<>();

    private String friendNow = "";
    private String userLogin = "";

    @FXML
    TextField message_input;
    @FXML
    BorderPane login_container;
    @FXML
    ScrollPane chat_scroll;
    @FXML
    ListView<Parent> fl_container;
    @FXML
    VBox chat_container;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        app = Main.getAppContext();

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/client/views/login_layout.fxml"));
            login_container.setCenter(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chat_scroll.setFitToWidth(true);
    }



    public void setAuthorized(String login) {

        if(!userLogin.equals(login)) {
            localStoreChats.clear();
            friendNames.clear();
        }
        userLogin = login;
        isAuthorized = true;
        setupView(true);
    }

    public void sendMsg() {
        app.sendMsg(friendNow, message_input.getText());
        message_input.clear();
        message_input.requestFocus();
    }
    public void receiveMsg(String from, String to, String msg) {
        try {
            UserMessage um = new UserMessage(from, to, msg);

            if(!userLogin.equals(to) && !userLogin.equals(from)) {// значит пришло с общего чата
                localStoreChats.get(to).add(um);
                if(friendNow.equals(to))
                    messageToChatWindow(um);
            }
            else {
                localStoreChats.get(userLogin.equals(to) ? from : to).add(um);
                if (friendNow.equals(from) || friendNow.equals(to))
                    messageToChatWindow(um);
            }

        } catch (NullPointerException e) {   }
    }
    @FXML
    public void logOut() {
        isAuthorized = false;
        setupView(false);
        app.logOut();
    }

    public void setupView(boolean isAuthorized) {
        login_container.setVisible(!isAuthorized);
    }

    public void updateFriendList(String[] friendList) {

        Platform.runLater(() -> {

            fl_container.getItems().clear();

            addFriend(Msg._broadcast + ":" + Msg._broadcast);
            for (String s : friendList)
                addFriend(s);

            UserMessage um = new UserMessage(Msg._broadcast, userLogin, "Введите " + Msg._blacklist + " nick\", чтобы добавить\nили удалить из чёрного списка");
            localStoreChats.get(Msg._broadcast).add(um);

            String firstFriend = localStoreChats.firstEntry().getKey();
            selectFriend(firstFriend);
            fl_container.getSelectionModel().select(0);
        });

    }

    //Добавить друга в список друзей
    private void addFriend(String friendInfo) {

        String avatarUrl = "/client/img/avatar0.png";

        String[] info = friendInfo.split(":");
        if(info[0].equals(userLogin)) return;

        localStoreChats.putIfAbsent(info[0], new ArrayList<>());
        friendNames.putIfAbsent(info[0], info[1]);

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/client/views/friend_layout.fxml"));

            ImageView iv = (ImageView) p.lookup(".friend-avatar");
            if(iv != null) iv.setImage(new Image(avatarUrl));

            Text text = (Text) p.lookup(".friend-name");
            if(text != null) text.setText(info[1]);


            fl_container.getItems().add(p);

            p.setOnMousePressed( event -> selectFriend(info[0]) );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Выбрать друга. Добавить сообщения в окно Чата
    private void selectFriend(String friendName) {

        friendNow = friendName;

        Platform.runLater(() -> {
            try {
                chat_container.getChildren().clear();
                localStoreChats.get(friendNow).forEach(this::messageToChatWindow);

            } catch (NullPointerException e) {
                //e.printStackTrace();
                chat_container.getChildren().add(new Text("Ошибка загрузки чата"));
            }
        });

    }

    private Node messageToChatWindow(UserMessage userMsg) {

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/client/views/message_layout.fxml"));

            boolean fromUser = userMsg.fromUser.equals(userLogin);

            Text text = (Text)p.lookup(".message-from");
            if(text != null) text.setText("От: " + (fromUser ? "Я" : friendNames.get(userMsg.fromUser)) + "  " + userMsg.date.toString());

            text = (Text)p.lookup(".message-text");
            if(text != null) text.setText(userMsg.message);

            VBox vb = (VBox) p.lookup(".message-body");
            if(fromUser)
                AnchorPane.setRightAnchor(vb, 0.0);
            else
                AnchorPane.setLeftAnchor(vb, 0.0);


            Platform.runLater(() -> {
                chat_container.getChildren().add(p);
                scrollBottom();
            });

            return p;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return new Text("Ошибка");
        }
    }


    private void scrollBottom() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                chat_scroll.setVvalue(1.0);
                t.cancel();
            }
        }, 50);
    }

    private class UserMessage {

        final public String fromUser;
        final public String toUser;
        final public Date date;
        final public String message;
        final public int messageID;


        public UserMessage(String fromUser, String toUser, String message) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.date = new Date(System.currentTimeMillis());
            this.message = message;
            messageID = new Random().nextInt();
        }
        public UserMessage(String fromUser, String toUser, Date date, String message) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.date = date;
            this.message = message;
            messageID = new Random().nextInt();
        }

    }
}
