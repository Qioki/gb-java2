package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.User.UserAccount;
import main.User.UserMessage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ChatController implements Initializable, PseudoSocket {

    AppMain app;
    private PseudoServer server;
    private UserAccount userAccount;
    private String token = "";

    private String friendNow = "";

    private TreeMap<String, Parent> friendsNodes = new TreeMap<>();
    private HashMap<Integer, VBox> sentMessages = new HashMap<>();


    @FXML
    VBox chat_container;
    @FXML
    ScrollPane chat_scroll;
    @FXML
    ListView<Parent> fl_container;
    @FXML
    TextField message_input;
    @FXML
    BorderPane login_container;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        app = AppMain.getAppContext();
        server = app.getServer();

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/views/login_layout.fxml"));
            login_container.setCenter(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chat_scroll.setFitToWidth(true);
    }

    //  Был произведен вход с меню Входа
    public void userLoggedIn(UserAccount userAccount, String token) {

        this.userAccount = userAccount;
        this.token = token;
        setLoginWindowVisible(false);

        TreeMap<String, String> friendsInfo;
        try {   // Получаем список друзей
            friendsInfo = server.getFriendsInfo(token, userAccount.getUserName());

        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
            return;
        }

        fl_container.getItems().clear();
        friendsInfo.forEach(this::addFriend);

        String firstFriend = friendsInfo.firstEntry().getKey();
        selectFriend(firstFriend);
        fl_container.getSelectionModel().select(friendsNodes.get(firstFriend));
    }

    //Добавить друга в список друзей
    private void addFriend(String friendName, String avatar) {

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/views/friend_layout.fxml"));

            ImageView iv = (ImageView) p.lookup(".friend-avatar");
            if(iv != null) iv.setImage(new Image(avatar));

            Text text = (Text) p.lookup(".friend-name");
            if(text != null) text.setText(friendName);


            fl_container.getItems().add(p);

            p.setOnMousePressed(event -> selectFriend(friendName) );

            friendsNodes.put(friendName, p);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузить сообщения выбранного друга. Добавить сообщения в окно Чата
    private void selectFriend(String friendName) {
        if(friendNow.equals(friendName)) return;
        friendNow = friendName;
        try {
            chat_container.getChildren().clear();

            ArrayList<UserMessage> chat = server.getChat(token, userAccount.getUserName(), friendName);
            chat.forEach(this::messageToChatWindow);

        } catch (ConnectionError connectionError) {
            //connectionError.printStackTrace();
            chat_container.getChildren().add(new Text("Ошибка загрузки чата"));
        }
    }

    // Метод запускается по нажатию "Enter"
    public void sendMessage() {

        String newMsg = message_input.getText();
        if(!newMsg.isEmpty() && !friendNow.isEmpty()) {
            message_input.clear();

            UserMessage userMsg = new UserMessage(userAccount.getUserName(), friendNow, newMsg);

            Node p = messageToChatWindow(userMsg);
            VBox vb = (VBox)p.lookup(".message-body");
            if(vb != null) vb.setStyle("-fx-background-color: #eee;");
            sentMessages.put(userMsg.messageID, vb);     // Сохраняю ссылку на ноду чтобы потом ее отметить если сообщение отправилось нормально


            try {   userAccount.addMessage(friendNow, userMsg);    } catch (NullPointerException ignored) { }
            server.newMessage(token, userMsg);

        }
        message_input.requestFocus();
    }

    // Этот метод запускается с сервера. Как будто пришло новое сообщение в сокет.
    // П.С. сокетами ни разу не пользовался
    @Override
    public void newMessage(UserMessage message) {

        if(AppMain.IsStopped || !AppMain.IsLoggedIn) return;

        VBox vb = sentMessages.get(message.messageID);
        if(vb != null) {

            vb.setStyle("-fx-background-color: green3;");
            sentMessages.remove(message.messageID);

            return;
        }
        if(userAccount.isNewMessage(message) && message.fromUser.equals(friendNow)) {   // Если пришло новое сообщение от текущего друга, то рендерим сообщение
            Platform.runLater(() -> messageToChatWindow(message));
            //messageToChatWindow(message);
        }
    }

    private Node messageToChatWindow(UserMessage userMsg) {

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/views/message_layout.fxml"));

            boolean fromUser = userMsg.fromUser.equals(userAccount.getUserName());

            Text text = (Text)p.lookup(".message-from");
            if(text != null) text.setText("От: " + (fromUser ? "Я" : userMsg.fromUser) + "  " + userMsg.date.toString());

            text = (Text)p.lookup(".message-text");
            if(text != null) text.setText(userMsg.message);

            VBox vb = (VBox) p.lookup(".message-body");
            if(fromUser)
                AnchorPane.setRightAnchor(vb, 0.0);
            else
                AnchorPane.setLeftAnchor(vb, 0.0);


            chat_container.getChildren().add(p);
            scrollBottom();

            return p;
        } catch (IOException e) {
            e.printStackTrace();
            return new Text("Ошибка");
        }
    }


    private void scrollBottom() {
        Timer t = new Timer();  // Если без задержки то скроллится на предпоследнее сообщение
        t.schedule(new TimerTask() {
           @Override
           public void run() {
               chat_scroll.setVvalue(1.0);
               t.cancel();
           }
       }, 50);
    }

    @FXML
    public void tryLogin() {
        AppMain.IsLoggedIn = false;
        server.close(token);
        setLoginWindowVisible(true);
    }
    public void setLoginWindowVisible(boolean visible) {
        login_container.setVisible(visible);
    }
}
