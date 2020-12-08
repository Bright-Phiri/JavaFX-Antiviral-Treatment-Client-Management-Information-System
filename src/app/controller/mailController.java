/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.util.ShowTrayNotification;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class mailController implements Initializable {

    @FXML
    private AnchorPane pane2;
    @FXML
    private JFXComboBox<String> to;
    @FXML
    private JFXTextField subject;
    @FXML
    private JFXTextArea message;
    @FXML
    private Label status;
    @FXML
    private ProgressBar progressBar;
    int counter = 0;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Task<Void> mail;
    Thread thread;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        to.setItems(FXCollections.observableArrayList("Doctors"));
    }

    @FXML
    private void sendMail(ActionEvent event) {
        List<String> emails = new ArrayList<>();
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement("SELECT Email FROM User WHERE Role = ?");
            preparedStatement.setString(1, "Doctor");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                emails.add(resultSet.getString("Email"));
            }
            if (emails.isEmpty()) {
                ShowTrayNotification notification = new ShowTrayNotification("Notice", "No doctor record found", NotificationType.NOTICE);
            } else {
                mail = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (validateFields()) {
                            updateMessage("Connecting...");
                            if (checkInternetConnection()) {
                                updateMessage("Connected");
                                Properties properties = new Properties();
                                properties.setProperty("mail.smtp.host", "smtp.gmail.com");
                                properties.setProperty("mail.smtp.port", "587");
                                properties.setProperty("mail.smtp.auth", "true");
                                properties.setProperty("mail.smtp.starttls.enable", "true");

                                Session session = Session.getDefaultInstance(properties, new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication("bphiri1998@gmail.com", "karloline?");
                                    }
                                });
                                Message mail = new MimeMessage(session);
                                mail.setFrom(new InternetAddress("bphiri1998@gmail.com", "Admin"));
                                mail.setSubject(subject.getText());
                                mail.setText(message.getText());
                                mail.setSentDate(new Date());
                                Transport transport = session.getTransport("smtp");
                                transport.connect("smtp.gmail.com", "bphiri1998@gmail.com", "karloline?");
                                for (int i = 0; i < emails.size(); i++) {
                                    mail.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(i)));
                                    mail.saveChanges();
                                    transport.sendMessage(mail, mail.getAllRecipients());
                                    updateMessage("Sending...");
                                    updateProgress(i, emails.size());
                                    counter++;
                                }
                                if (counter > 0) {
                                    Platform.runLater(() -> {
                                        TrayNotification notification = new TrayNotification();
                                        notification.setAnimationType(AnimationType.SLIDE);
                                        notification.setTray("Information", "Messages sent to " + counter + " users", NotificationType.INFORMATION);
                                        notification.showAndDismiss(Duration.seconds(2));
                                    });
                                }
                            } else {
                                updateMessage("Not connected");
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        Platform.runLater(() -> {
                            TrayNotification notification = new TrayNotification();
                            notification.setAnimationType(AnimationType.SLIDE);
                            notification.setTray("Information", "Failed to send the message", NotificationType.ERROR);
                            notification.showAndDismiss(Duration.seconds(2));
                        });
                    }

                };
            }
        } catch (SQLException ex) {
            Logger.getLogger(mailController.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread = new Thread(mail);
        thread.setDaemon(true);
        thread.start();
        status.visibleProperty().bind(mail.runningProperty());
        status.textProperty().bind(mail.messageProperty());
        progressBar.visibleProperty().bind(mail.runningProperty());
        progressBar.progressProperty().bind(mail.progressProperty());
    }

    @FXML
    private void canceSendMail(ActionEvent event) {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.stop();
            }
        }
    }

    @FXML
    private void addAttachment(MouseEvent event) {
    }

    public boolean checkInternetConnection() {
        try {
            URL url = new URL("http://www.gmail.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TrayNotification notification = new TrayNotification();
                notification.setAnimationType(AnimationType.SLIDE);
                notification.setTray("Information", "Check internet connection", NotificationType.INFORMATION);
                notification.showAndDismiss(Duration.seconds(2));
            }
        });
        return false;
    }

    private boolean validateFields() {
        if (subject.getText().isEmpty() || message.getText().isEmpty() || to.getValue() == null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fields validation");
                alert.setHeaderText(null);
                alert.setContentText("Please enter in all fields");
                alert.showAndWait();
            });
            return false;
        }
        return true;
    }

}
