/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.DatabaseHelper;
import app.model.User;
import app.util.ShowAlert;
import app.util.ShowTrayNotification;
import app.util.Util;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.commons.codec.digest.DigestUtils;
import org.controlsfx.control.textfield.TextFields;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class addUserController implements Initializable {

    @FXML
    private AnchorPane pane2;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXComboBox<String> role;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField contact;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField photoUrl;
    FileChooser chooser;
    Util util;
    File file;
    public static Boolean editMode = Boolean.FALSE;
    int id;
    String phoneSugg[] = {"+26599","+26588"};

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        role.setItems(FXCollections.observableArrayList("Doctor"));
        chooser = new FileChooser();
        util = new Util();
        util.initializeFileChooser(chooser);
        setUserData();
        TextFields.bindAutoCompletion(contact, phoneSugg);
    }

    @FXML
    private void cancelSaveUserFunction(ActionEvent event) {
        clearCache();
    }

    @FXML
    private void saveUser(ActionEvent event) {
        DatabaseHelper helper = new DatabaseHelper();
        if (ifFieldsEmpty() && util.validateName(firstName) && util.validateName(lastName) && util.validateName(username) && util.validateEmail(email) && util.validatePhoneNumber(contact)) {
            if (editMode) {
                file = new File(photoUrl.getText());
                User user = new User(id, firstName.getText(), lastName.getText(), username.getText(), email.getText(), contact.getText(), role.getValue(), file, DigestUtils.shaHex(password.getText()));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Update user");
                alert.setHeaderText(null);
                alert.setContentText("Save changes?");
                Optional<ButtonType> opt = alert.showAndWait();
                if (opt.get() == ButtonType.OK) {
                    if (helper.updateUser(user)) {
                        ShowTrayNotification notification = new ShowTrayNotification("Message", "User succssfully updated", NotificationType.SUCCESS);
                        clearCache();
                        editMode = Boolean.FALSE;
                    } else {
                        ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to add the user", NotificationType.ERROR);
                    }
                }
            } else {
                User user = new User(firstName.getText(), lastName.getText(), username.getText(), email.getText(), contact.getText(), role.getValue(), file, DigestUtils.shaHex(password.getText()));
                if (helper.addUser(user)) {
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "User succssfully added", NotificationType.SUCCESS);
                    clearCache();
                } else {
                    ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to add the user", NotificationType.ERROR);
                }
            }
        }
    }

    @FXML
    private void brosePhoto(MouseEvent event) {
        file = chooser.showOpenDialog(util.getStage(role));
        if (file != null) {
            photoUrl.setText(file.getPath());
        }
    }

    private void clearCache() {
        firstName.clear();
        lastName.clear();
        username.clear();
        email.clear();
        contact.clear();
        role.setValue(null);
        photoUrl.clear();
        password.clear();
        editMode = Boolean.FALSE;
    }

    private Boolean ifFieldsEmpty() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || username.getText().isEmpty()
                || email.getText().isEmpty() || contact.getText().isEmpty() || role.getValue() == null
                || photoUrl.getText().isEmpty() || password.getText().isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void inflateUI(viewUsersController.User user) {
        id = user.getUserId();
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        username.setText(user.getUserName());
        email.setText(user.getEmailAddress());
        contact.setText(user.getPhoneNumber());
        role.setValue(user.getUserRole());
        photoUrl.setText(user.getImage());
        password.setText(user.getPassword());
    }

    private void setUserData() {
        firstName.setUserData("first name");
        lastName.setUserData("last name");
        username.setUserData("username");
    }
}
