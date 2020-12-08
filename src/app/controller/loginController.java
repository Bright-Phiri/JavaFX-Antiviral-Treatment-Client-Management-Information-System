/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.util.ShowTrayNotification;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class loginController implements Initializable {

    @FXML
    private ImageView imageView;
    ScheduledService scheduledService;
    ScheduledService sendMail;
    @FXML
    private BorderPane rootPane;
    public static BorderPane root;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        root = rootPane;
        Database.checkTables("User", "CREATE TABLE `art`.`User` (\n"
                + "`Id` INT NOT NULL AUTO_INCREMENT ,\n"
                + "`FirstName` VARCHAR( 40 ) NULL ,\n"
                + "`LastName` VARCHAR( 40 )  NULL ,\n"
                + "`Username` VARCHAR( 40 ) NOT NULL ,\n"
                + "`Email` VARCHAR( 40 ) NOT NULL ,\n"
                + "`Phone` VARCHAR( 20 ) NOT NULL ,\n"
                + "`Role` VARCHAR( 20 ) NULL ,\n"
                + "`Image` LONGBLOB NOT NULL ,\n"
                + "`Password` VARCHAR( 50 ) NOT NULL ,\n"
                + "PRIMARY KEY ( `Id` ) \n"
                + ") ENGINE = InnoDB;");
        Database.checkTables("Patient", "CREATE TABLE `art`.`Patient` (\n"
                + "`Id` INT NOT NULL AUTO_INCREMENT ,\n"
                + "`FirstName` VARCHAR( 40 ) NOT NULL ,\n"
                + "`LastName` VARCHAR( 40 ) NOT NULL ,\n"
                + "`Address` VARCHAR( 50 ) NOT NULL ,\n"
                + "`Email` VARCHAR( 40 ) NOT NULL ,\n"
                + "`Contact` VARCHAR( 15 ) NOT NULL ,\n"
                + "`City` VARCHAR( 25 ) NOT NULL ,\n"
                + "`District` VARCHAR( 25 ) NOT NULL ,\n"
                + "`DOB` VARCHAR( 10 ) NOT NULL ,\n"
                + "`RegisterdDate` VARCHAR( 10 ) NOT NULL ,\n"
                + "PRIMARY KEY ( `Id` )\n"
                + ") ENGINE = InnoDB;");
        Database.checkTables("VirtualSigns", "CREATE TABLE `art`.`VirtualSigns` (\n"
                + "`ID` INT NOT NULL AUTO_INCREMENT ,\n"
                + "`ClientId` INT NOT NULL ,\n"
                + "`ClientName` VARCHAR( 40 ) NOT NULL ,\n"
                + "`ClientEmail` VARCHAR( 40 ) NOT NULL ,\n"
                + "`weightLoss` FLOAT NOT NULL ,\n"
                + "`Height` FLOAT NOT NULL ,\n"
                + "`Rash` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Throat` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Gland` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Headache` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Stomach` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Pain` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Aches` VARCHAR( 4 ) NOT NULL ,\n"
                + "`Diarrhoea` VARCHAR( 4 ) NOT NULL ,\n"
                + "`TreatmentDate` VARCHAR( 15 ) NOT NULL ,\n"
                + "`ReturningDate` VARCHAR( 15 ) NOT NULL ,\n"
                + "`Result` VARCHAR( 15 ) NOT NULL ,\n"
                + "`Prescription` VARCHAR( 30 ) NOT NULL ,\n"
                + "`Notify` VARCHAR( 5 )NOT NULL ,\n"
                + "PRIMARY KEY ( `ID` )\n"
                + ") ENGINE = InnoDB;");
        try {
            VBox box = (VBox) FXMLLoader.load(getClass().getResource("/app/view/loginForm.fxml"));
            rootPane.setLeft(box);
        } catch (IOException ex) {
            Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
        }

        scheduledService = new ScheduledService() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        setImage();
                        return null;
                    }
                };
            }
        };
        scheduledService.setDelay(Duration.ONE);
        scheduledService.setRestartOnFailure(true);
        scheduledService.setPeriod(Duration.seconds(8));
        Platform.runLater(() -> {
            scheduledService.start();
        });

        sendMail = new ScheduledService() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        sendReminders();
                        return null;
                    }
                };

            }
        };
        sendMail.setDelay(Duration.minutes(1));
        sendMail.setRestartOnFailure(true);
        sendMail.setPeriod(Duration.hours(2));
        Platform.runLater(() -> {
            sendMail.start();
        });

    }

    private void setImage() {
        String image1 = "IMG_0086.JPG";
        String image2 = "IMG_0088.JPG";
        String image3 = "IMG_0085.PNG";
        String image4 = "IMG_0087.PNG";
        String image5 = "IMG_0093.PNG";
        String image6 = "IMG_0291.PNG";
        String image7 = "IMG_0292.PNG";
        String image8 = "IMG_0293.JPG";
        String image9 = "IMG_0294.JPG";
        
        String image = "";
        int number = 1 + (int) (Math.random() * 9);
        switch (number) {
            case 1: {
                image = image1;
                break;
            }
            case 2: {
                image = image2;
                break;
            }
            case 3: {
                image = image3;
                break;
            }
            case 4: {
                image = image4;
                break;
            }
            case 5: {
                image = image5;
                break;
            }
            case 6: {
                image = image6;
                break;
            }
            case 7: {
                image = image7;
                break;
            }
            case 8: {
                image = image8;
                break;
            }
            case 9: {
                image = image9;
                break;
            }
        }
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), imageView);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        imageView.setImage(new Image("/app/images/" + image + ""));
    }

    @FXML
    private void closeApp(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private void sendReminders() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        int couter = 0;
        String sql = "SELECT ID,ClientId,ClientName,ClientEmail,ReturningDate,Notify FROM VirtualSigns";
        String sql2 = "UPDATE VirtualSigns SET Notify = ? WHERE ID = ?";
        LocalDate today = LocalDate.now();
        mailController controller = new mailController();
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement2 = connection.prepareStatement(sql2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDate returnDate = LocalDate.parse(resultSet.getString("ReturningDate"));
                if (ChronoUnit.DAYS.between(returnDate, today) == 3) {
                    if (resultSet.getString("Notify").equals("False")) {
                        if (controller.checkInternetConnection()) {
                            Properties properties = new Properties();
                            properties.setProperty("mail.smtp.host", "smtp.gmail.com");
                            properties.setProperty("mail.smtp.port", "587");
                            properties.setProperty("mail.smtp.auth", "true");
                            properties.setProperty("mail.smtp.starttls.enable", "true");

                            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("kaundohassan@gmail.com", "joshua//");
                                }
                            });
                            Message mail = new MimeMessage(session);
                            mail.setFrom(new InternetAddress("kaundohassan@gmail.com", "Admin"));
                            mail.setSubject("");
                            mail.setText("Hellow "+resultSet.getString("ClientName")+"!!\nConsidering the number of drugs you had last time it shows that it will run out in 3 days to come. Be inform to come and get another prescription.\n\nSincerely Daeyang Luke Hospital");
                            mail.setSentDate(new Date());
                            Transport transport = session.getTransport("smtp");
                            transport.connect("smtp.gmail.com", "kaundohassan@gmail.com", "joshua//");
                            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(resultSet.getString("ClientEmail")));
                            mail.saveChanges();
                            transport.sendMessage(mail, mail.getAllRecipients());
                            preparedStatement2.setString(1, "True");
                            preparedStatement2.setInt(2, resultSet.getInt(1));
                            preparedStatement2.executeUpdate();
                            couter++;
                        } 
                    }
                }
            }
            if (couter == 1) {
                ShowTrayNotification notification = new ShowTrayNotification("Message", "Reminder sent to " + couter + "client", NotificationType.SUCCESS);
            }
            if (couter > 1) {
                ShowTrayNotification notification = new ShowTrayNotification("Message", "Reminder sent to " + couter + "clients", NotificationType.SUCCESS);
            }
        } catch (SQLException ex) {
            Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to send reminders to clients", NotificationType.ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
