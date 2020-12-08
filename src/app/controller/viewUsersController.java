/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.util.ShowTrayNotification;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class viewUsersController implements Initializable {

    @FXML
    private AnchorPane pane1;
    @FXML
    private JFXTextField searchUserTxtField;
    @FXML
    private TableColumn<User, String> userId;
    @FXML
    private TableColumn<User, String> firstName;
    @FXML
    private TableColumn<User, String> lastName;
    @FXML
    private TableColumn<User, String> userName;
    @FXML
    private TableColumn<User, String> emailAddress;
    @FXML
    private TableColumn<User, String> userType;
    @FXML
    private TableColumn<User, HBox> action;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> phone;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private final ObservableList<User> data = FXCollections.observableArrayList();
    OutputStream outputStream = null;
    InputStream inputStream = null;
    int size;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        intializeColumns();
        loadUserDetails();
    }

    private void loadUserDetails() {
        data.clear();
        String sql = "SELECT * FROM User WHERE Role != 'Admin'";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.add(new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), setActionButtons(resultSet.getInt(1))));
            }
            userTable.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private HBox setActionButtons(int userId) {
        String query = "delete from User where Id = ?";
        String query1 = "select * from User where Id = ?";
        HBox hBox = new HBox(5);
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        icon.setSize("17");
        icon.setFill(Color.web("#66ddff"));
        HBox.setMargin(icon, new Insets(2, 0, 0, 0));
        icon.setId(String.valueOf(userId));
        icon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement(query1);
                    preparedStatement.setInt(1, userId);
                    resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    inputStream = resultSet.getBinaryStream("Image");
                    byte[] content = new byte[1024];
                    File file = new File("copy.png");
                    outputStream = new FileOutputStream(file);
                    while ((size = inputStream.read(content)) != -1) {
                        outputStream.write(content, 0, size);
                    }
                    User user = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), file.getPath(), resultSet.getString(9));
                    FXMLLoader xMLLoader = new FXMLLoader();
                    xMLLoader.setLocation(getClass().getResource("/app/view/addUser.fxml"));
                    Parent parent = xMLLoader.load();
                    BorderPane.setMargin(parent, new Insets(0, 0, 0, 10));
                    addUserController userController = (addUserController) xMLLoader.getController();
                    userController.inflateUI(user);
                    mainPanelController.root.setCenter(parent);
                    addUserController.editMode = Boolean.TRUE;
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        FontAwesomeIconView icon1 = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        icon1.setSize("17");
        icon1.setFill(Color.web("#FD5B73"));
        icon1.setId(String.valueOf(userId));
        icon1.setOnMouseClicked((e) -> {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete user");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete this user?");
                Optional<ButtonType> opti = alert.showAndWait();
                if (opti.get() == ButtonType.OK) {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, userId);
                    preparedStatement.executeUpdate();
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "User successfully deleted", NotificationType.SUCCESS);
                }

            } catch (SQLException ex) {
                Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(viewUsersController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadUserDetails();
            }
        }
        );
        hBox.getChildren().addAll(icon, icon1);
        return hBox;
    }

    public void intializeColumns() {
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        emailAddress.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        userType.setCellValueFactory(new PropertyValueFactory<>("userRole"));
        action.setCellValueFactory(new PropertyValueFactory<>("controlsPane"));
    }

    @FXML
    private void searchUser(KeyEvent event) {
        FilteredList<User> filteredList = new FilteredList<>(data, p -> true);
        searchUserTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super User>) user -> {
                String filterToLowerCase = newValue.toLowerCase();
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (String.valueOf(user.getUserId()).toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (user.getFirstName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (user.getLastName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (user.getUserName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                userTable.setPlaceholder(new Text("No record matches your search"));
                return false;
            });
            SortedList<User> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(userTable.comparatorProperty());
            userTable.setItems(sortedList);
        });
    }

    public class User {

        SimpleIntegerProperty userId;
        SimpleStringProperty firstName;
        SimpleStringProperty lastName;
        SimpleStringProperty userName;
        SimpleStringProperty emailAddress;
        SimpleStringProperty phoneNumber;
        SimpleStringProperty userRole;
        SimpleStringProperty password;
        SimpleStringProperty image;
        HBox controlsPane;

        public User(int userId, String firstName, String lastName, String userName, String emailAddress, String phoneNumber, String userRole, HBox controlsPane) {
            this.userId = new SimpleIntegerProperty(userId);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.userName = new SimpleStringProperty(userName);
            this.emailAddress = new SimpleStringProperty(emailAddress);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
            this.userRole = new SimpleStringProperty(userRole);
            this.controlsPane = controlsPane;
        }

        public User(int userId, String firstName, String lastName, String userName, String emailAddress, String phoneNumber, String userRole, String image, String password) {
            this.userId = new SimpleIntegerProperty(userId);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.userName = new SimpleStringProperty(userName);
            this.emailAddress = new SimpleStringProperty(emailAddress);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
            this.userRole = new SimpleStringProperty(userRole);
            this.image = new SimpleStringProperty(image);
            this.password = new SimpleStringProperty(password);
        }

        public int getUserId() {
            return userId.get();
        }

        public void setUserId(int userId) {
            this.userId.set(userId);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public String getLastName() {
            return lastName.get();
        }

        public String getUserName() {
            return userName.get();
        }

        public String getEmailAddress() {
            return emailAddress.get();
        }

        public String getPhoneNumber() {
            return phoneNumber.get();
        }

        public String getUserRole() {
            return userRole.get();
        }

        public String getPassword() {
            return password.get();
        }

        public String getImage() {
            return image.get();
        }

        public HBox getControlsPane() {
            return controlsPane;
        }
    }
}
