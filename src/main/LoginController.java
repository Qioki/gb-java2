package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.User.UserAccount;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    AppMain app;
    UserAccount userAccount;
    private String[] lastUser = { "User", "1234" };

    @FXML
    TextField login_name, login_password;
    @FXML
    Label login_alert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        app = AppMain.getAppContext();

        login_name.setText(lastUser[0]);
        login_password.setText(lastUser[1]);
    }

    @FXML
    public void login(){

        if(!app.connect(login_name.getText(), login_password.getText())) {
            login_alert.setText("Неправильный логин или пароль");
            return;
        }
        userAccount = app.getUserAccount();
    }

}
