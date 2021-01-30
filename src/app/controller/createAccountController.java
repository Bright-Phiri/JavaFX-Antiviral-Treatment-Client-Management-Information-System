/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Admin;
import app.model.DatabaseHelper;
import app.util.ShowAlert;
import app.util.Util;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.apache.commons.codec.digest.DigestUtils;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class createAccountController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField pictureUrl;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField confirmPassword;
    File file;
    FileChooser chooser;
    Util util;
    String phoneSugg[] = {"+26599", "+26588"};

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        util = new Util();
        chooser = new FileChooser();
        util.initializeFileChooser(chooser);
        TextFields.bindAutoCompletion(phone, phoneSugg);
    }

    @FXML
    private void createAccount(ActionEvent event) {
       
        DatabaseHelper helper = new DatabaseHelper();
        if (validateFields() && util.validateName(username) && util.validateEmail(emailAddress) && util.validatePhoneNumber(phone)) {
            if (password.getText().equals(confirmPassword.getText())) {
                if (!helper.ifAdminExists()) {
                    Admin admin = new Admin(username.getText(), emailAddress.getText(), phone.getText(), file, DigestUtils.shaHex(password.getText()));
                    if (helper.createAccount(admin)) {
                        ShowAlert alert = new ShowAlert(Alert.AlertType.INFORMATION, "Information", "Account created");
                        loadLoginPanel(null);
                    }
                } else {
                    ShowAlert alert = new ShowAlert(Alert.AlertType.INFORMATION, "Message", "Administrator account already exists");
                }
            } else {
                ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Error", "Passwords dont match");
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void loadLoginPanel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/loginForm.fxml"));
            Parent parent = loader.load();
            loginFormController controller = (loginFormController) loader.getController();
            loginController.root.setRight(parent);
        } catch (IOException ex) {
            Logger.getLogger(createAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Boolean validateFields() {
        if (username.getText().isEmpty() || emailAddress.getText().isEmpty() || phone.getText().isEmpty() || pictureUrl.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void clearFields() {
        username.clear();
        emailAddress.clear();
        phone.clear();
        pictureUrl.clear();
        password.clear();
        confirmPassword.clear();
    }

    @FXML
    private void browsePhoto(MouseEvent event) {
        file = chooser.showOpenDialog(util.getStage(phone));
        if (file != null) {
            pictureUrl.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    private void closeApp(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
