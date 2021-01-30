/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.DatabaseHelper;
import app.util.ShowAlert;
import app.util.Util;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * FXML Controller class
 *
 * @author Bright
 */
public class loginFormController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    @FXML
    private void login(ActionEvent event) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Util util = new Util();
        if (validateFields()) {
            if (databaseHelper.ifRecords() == 0) {
                ShowAlert alert = new ShowAlert(Alert.AlertType.INFORMATION, "Message", "No user account found");
                username.clear();
                password.clear();
                return;
            }
            if (databaseHelper.userSignIn(username.getText(), DigestUtils.shaHex(password.getText()))) {
                switch (DatabaseHelper.USERTYPE) {
                    case "Admin": {
                        util.showUserStage(username, "/app/view/mainPanel.fxml");
                        util.shoNotification(DatabaseHelper.USERNAME);
                        break;
                    }
                    case "Doctor": {
                        util.showUserStage(username, "/app/view/doctorPanel.fxml");
                        util.shoNotification(DatabaseHelper.USERNAME);
                        break;
                    }
                }
            } else {
                ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Error", "Wrong username or password");
            }
        }
    }

    @FXML
    private void loadCreateAccountPanel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/createAccount.fxml"));
            Parent parent = loader.load();
            createAccountController controller = (createAccountController) loader.getController();
            loginController.root.setRight(parent);
        } catch (IOException ex) {
            Logger.getLogger(loginFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Boolean validateFields() {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @FXML
    private void signIn(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){
            login(new ActionEvent());
        }
    }
    
    @FXML
    private void closeApp(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

}
