/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.model.DatabaseHelper;
import app.model.User;
import app.util.ShowAlert;
import app.util.ShowTrayNotification;
import app.util.Util;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import org.apache.commons.codec.digest.DigestUtils;
import org.controlsfx.control.textfield.TextFields;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class doctorsettingsController implements Initializable {

    @FXML
    private AnchorPane pane2;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField imageUrl;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField oldPassword;
    @FXML
    private JFXPasswordField newPassword1;
    @FXML
    private JFXPasswordField newPassword2;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
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
        loadPersonalInfor();
    }

    @FXML
    private void browseImage(MouseEvent event) {
        file = chooser.showOpenDialog(util.getStage(phone));
        if (file != null) {
            imageUrl.setText(file.getPath());
        }
    }

    @FXML
    private void savePersonalInfo(ActionEvent event) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        if (validateFields() && util.validateName(firstName) && util.validateName(lastName) && util.validateName(username) && util.validateEmail(email) && util.validatePhoneNumber(phone) && databaseHelper.validatePassword(DatabaseHelper.ID, DigestUtils.shaHex(password.getText()))) {
            User user = new User(DatabaseHelper.ID, firstName.getText(), lastName.getText(), username.getText(), email.getText(), phone.getText(), "Doctor", file, DigestUtils.shaHex(password.getText()));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update user");
            alert.setHeaderText(null);
            alert.setContentText("Save changes?");
            Optional<ButtonType> opt = alert.showAndWait();
            if (opt.get() == ButtonType.OK) {
                if (databaseHelper.updateUser(user)) {
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "User information succssfully updated", NotificationType.SUCCESS);
                    password.clear();
                    loadPersonalInfor();
                } else {
                    ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to update user information", NotificationType.ERROR);
                }
            }
        }
    }

    @FXML
    private void cancelSavingOpt(ActionEvent event) {
        clearFields();
        loadPersonalInfor();
    }

    @FXML
    private void savePassword(ActionEvent event) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        if (validatePasswordFields()) {
            if (databaseHelper.validatePassword(DatabaseHelper.ID, DigestUtils.shaHex(oldPassword.getText()))) {
                if (newPassword1.getText().equals(newPassword2.getText())) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update admin");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes?");
                    Optional<ButtonType> opt = alert.showAndWait();
                    if (opt.get() == ButtonType.OK) {
                        if (databaseHelper.updatePassword(DatabaseHelper.ID, DigestUtils.shaHex(newPassword1.getText()))) {
                            ShowTrayNotification notification = new ShowTrayNotification("Message", "Admin password succssfully updated", NotificationType.SUCCESS);
                            clearPasswordFields();
                        } else {
                            ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to update Admin password", NotificationType.ERROR);
                        }
                    }
                } else {
                    ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Error", "Passwords dont match");
                    newPassword1.clear();
                    newPassword2.clear();
                }
            } else {
                ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Error", "Old password is incorrect");
            }
        }
    }

    @FXML
    private void canceSavePassword(ActionEvent event) {
        clearPasswordFields();
    }

    private Boolean validateFields() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || username.getText().isEmpty() || email.getText().isEmpty()
                || phone.getText().isEmpty() || imageUrl.getText().isEmpty() || password.getText().isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        username.clear();
        email.clear();
        phone.clear();
        imageUrl.clear();
        password.clear();
    }

    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword1.clear();
        newPassword2.clear();
    }

    private Boolean validatePasswordFields() {
        if (oldPassword.getText().isEmpty() || newPassword1.getText().isEmpty() || newPassword2.getText().isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void loadPersonalInfor() {
        byte[] content = new byte[1024];
        int size = 0;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String sql = "SELECT * FROM User WHERE Id = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, DatabaseHelper.ID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            firstName.setText(resultSet.getString(2));
            lastName.setText(resultSet.getString(3));
            username.setText(resultSet.getString(4));
            email.setText(resultSet.getString(5));
            phone.setText(resultSet.getString(6));
            inputStream = resultSet.getBinaryStream("Image");
            file = new File("doctor.png");
            outputStream = new FileOutputStream(file);
            while ((size = inputStream.read(content)) != -1) {
                outputStream.write(content, 0, size);
            }
            imageUrl.setText(file.getPath());
            doctorPanelController.pic.setFill(new ImagePattern(new Image("file:doctor.png"), 0, 0, 1, 1, true));
            doctorPanelController.userUsername.setText(resultSet.getString(4));
        } catch (SQLException ex) {
            Logger.getLogger(settingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(settingsController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException | SQLException ex) {
                Logger.getLogger(doctorsettingsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
