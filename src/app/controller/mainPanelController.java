/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.model.DatabaseHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class mainPanelController implements Initializable {

    @FXML
    private JFXButton homeBtn;
    @FXML
    private JFXButton addUserBtn;
    @FXML
    private JFXButton userListBtn;
    @FXML
    private JFXButton mailBtn;
    @FXML
    private JFXButton statisticsBtn;
    @FXML
    private JFXButton settingsBtn;
    @FXML
    private MaterialDesignIconView homeIco;
    @FXML
    private FontAwesomeIconView useIco;
    @FXML
    private FontAwesomeIconView listIco;
    @FXML
    private FontAwesomeIconView mailIco;
    @FXML
    private FontAwesomeIconView statisticsIco;
    @FXML
    private FontAwesomeIconView settingsIco;
    @FXML
    private Circle profilePhoto;
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox pane;
    @FXML
    private Text day;
    @FXML
    private Text week;
    @FXML
    private Text date;
    @FXML
    private FontAwesomeIconView logout;
    public static BorderPane root;
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    @FXML
    private Text username;
    public static Circle pic;
    public static Text adminUsername;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        root = rootPane;
        adminUsername = username;
        pic = profilePhoto;
        setProfile(DatabaseHelper.ID);
        Tooltip closeApp = new Tooltip("Sign out");
        closeApp.setStyle("-fx-font-size:11");
        closeApp.setMinSize(20, 20);
        Tooltip.install(logout, closeApp);
        JFXDepthManager.setDepth(pane, 1);
        setCenter("/app/view/dashBoard.fxml");

        ScheduledService<Void> scheduledService = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy  | hh: mm: ss aa");
                        Calendar calendar = new GregorianCalendar();
                        week.setText("WEEK " + calendar.get(Calendar.WEEK_OF_MONTH));
                        day.setText("DAY " + calendar.get(Calendar.DAY_OF_WEEK) + ",");
                        date.setText(dateFormat.format(new Date()));
                        return null;
                    }
                };
            }
        };
        scheduledService.setPeriod(Duration.seconds(1));
        Platform.runLater(()->{
            scheduledService.start();
        });
    }

    @FXML
    private void loadAddUserPanel(ActionEvent event) {
        setCenter("/app/view/addUser.fxml");
        addUserController.editMode = Boolean.FALSE;
    }

    @FXML
    private void loadUserListPanel(ActionEvent event) {
        setCenter("/app/view/viewUsers.fxml");
        addUserController.editMode = Boolean.FALSE;
    }

    @FXML
    private void loadHomePanel(ActionEvent event) {
        setCenter("/app/view/dashBoard.fxml");
    }

    @FXML
    private void loadMailPanel(ActionEvent event) {
        setCenter("/app/view/mail.fxml");
    }

    @FXML
    private void loadStatisticsPanel(ActionEvent event) {
        setCenter("/app/view/statistics.fxml");
    }

    @FXML
    private void loadSettingsPanel(ActionEvent event) {
        setCenter("/app/view/settings.fxml");
    }

    @FXML
    private void editProfile(MouseEvent event) {
        setCenter("/app/view/settings.fxml");
    }

    private void setCenter(String fxmlUrl) {
        try {
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource(fxmlUrl));
            BorderPane.setMargin(borderPane, new Insets(0, 0, 0, 10));
            rootPane.setCenter(borderPane);
        } catch (IOException ex) {
            Logger.getLogger(mainPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void signOut(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure, you want to Sign Out?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            try {
                Parent root1 = FXMLLoader.load(getClass().getResource("/app/view/login.fxml"));
                Scene scene = new Scene(root1);
                Stage stage1 = new Stage();
                stage1.setScene(scene);
                stage1.getIcons().add(new Image("/app/images/aids_ribbon_96px.png"));
                stage1.initStyle(StageStyle.UNDECORATED);
                stage1.centerOnScreen();
                stage1.show();
            } catch (IOException ex) {
                Logger.getLogger(mainPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setProfile(int id) {
        String sql = "SELECT * FROM User WHERE Id = " + id + "";
        byte[] content = new byte[1024];
        int size = 0;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            inputStream = resultSet.getBinaryStream("Image");
            outputStream = new FileOutputStream(new File("admin.png"));
            while ((size = inputStream.read(content)) != -1) {
                outputStream.write(content, 0, size);
            }
            profilePhoto.setFill(new ImagePattern(new Image("file:admin.png"), 0, 0, 1, 1, true));
            username.setText(resultSet.getString("Username"));
        } catch (SQLException | IOException ex) {
            Logger.getLogger(mainPanelController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(mainPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
