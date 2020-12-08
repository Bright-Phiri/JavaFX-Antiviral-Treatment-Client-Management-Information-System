/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.util;

import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tray.notification.NotificationType;

/**
 *
 * @author Bright
 */
public class Util {

    public Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }

    public void initializeFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Choose image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Pictures"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
    }

    public Boolean validateEmail(JFXTextField field) {
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(_A-Za-z0-9-)*@[A-Za-z0-9-].+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$");
        Matcher M = p.matcher(field.getText());
        if (M.find() && M.group().equals(field.getText())) {
            return true;
        } else {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Email validation", "Please enter a valid email address");
            return false;
        }
    }

    public Boolean validatePhoneNumber(JFXTextField field) {
        Pattern p1 = Pattern.compile("[+]{1}265[8]{2}[0-9]{7}");
        Pattern p2 = Pattern.compile("[+]{1}265[9]{2}[0-9]{7}");
        Matcher m1 = p1.matcher(field.getText());
        Matcher m2 = p2.matcher(field.getText());
        if (m1.find() && m1.group().equals(field.getText()) || m2.find() && m2.group().equals(field.getText())) {
            return true;
        } else {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter a valid phone number");
            return false;
        }
    }

    public Boolean validateName(JFXTextField field) {
        Pattern p = Pattern.compile("[a-z A-Z]+");
        Matcher M = p.matcher(field.getText());
        if (M.find() && M.group().equals(field.getText())) {
            return true;
        } else {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter a valid " + field.getUserData() + "");
            return false;
        }
    }

    public void showUserStage(Node node, String fxmlUrl) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlUrl));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.hide();
            Stage stage1 = new Stage(StageStyle.DECORATED);
            stage1.setScene(new Scene(root,1283,680));
            stage1.centerOnScreen();
            stage1.setTitle("Antiviral Treatment Client Management System");
            stage1.getIcons().add(new Image("/app/images/aids_ribbon_96px.png"));
            stage1.show();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void shoNotification(String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3_000);
                    Platform.runLater(() -> {
                        ShowTrayNotification notification = new ShowTrayNotification("Message", "Welcome " + userName, NotificationType.SUCCESS);
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}
