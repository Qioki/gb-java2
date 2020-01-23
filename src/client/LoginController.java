package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    Main app;
    private String[] lastUser = { "login1", "pass1" };

    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label login_alert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        app = Main.getAppContext();

        loginField.setText(lastUser[0]);
        passwordField.setText(lastUser[1]);
    }

    @FXML
    public void login() {

        app.tryToAuth(loginField.getText(), passwordField.getText());
    }
}
