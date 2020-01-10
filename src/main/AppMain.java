package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.User.UserAccount;

public class AppMain extends Application {

    private static AppMain appContext;
    public static boolean IsStopped = false;
    public static boolean IsLoggedIn = false;

    private PseudoServer server;
    private UserAccount userAccount;
    private ChatController chatController;


    public AppMain() {
        super();
        appContext = this;
        server = new PseudoServer();
    }

    public static AppMain getAppContext() {  // Я не нашёл как можно получить контекст
        return appContext;
    }

    public PseudoServer getServer() {
        return server;
    }
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public boolean connect(String userName, String password) {

        try {
            String token = server.connect(chatController, userName, password);
            IsLoggedIn = true;
            userAccount = new UserAccount(userName, password);
            chatController.userLoggedIn(userAccount, token);
        } catch (ConnectionError connectionError) {
            return false;
        }
        return true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat_layout.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 700, 550));
        primaryStage.setTitle("Чат");
        primaryStage.show();

        chatController = loader.getController();
        connect("User", "1234");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        IsStopped = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
