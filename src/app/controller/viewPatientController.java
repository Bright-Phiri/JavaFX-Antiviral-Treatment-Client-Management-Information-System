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
import java.io.IOException;
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
public class viewPatientController implements Initializable {
    
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, Integer> id;
    @FXML
    private TableColumn<Patient, String> firstName;
    @FXML
    private TableColumn<Patient, String> lastName;
    @FXML
    private TableColumn<Patient, String> email;
    @FXML
    private TableColumn<Patient, String> phone;
    @FXML
    private TableColumn<Patient, String> city;
    @FXML
    private TableColumn<Patient, String> district;
    @FXML
    private TableColumn<Patient, HBox> action;
    @FXML
    private AnchorPane pane1;
    @FXML
    private JFXTextField searchPatientTxtField;
    ObservableList<Patient> data = FXCollections.observableArrayList();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initiliazeColumns();
        loadPatientData();
    }
    
    @FXML
    private void searchPatient(KeyEvent event) {
        FilteredList<Patient> filteredList = new FilteredList<>(data, p -> true);
        searchPatientTxtField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Patient>) patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterToLowerCase = newValue.toLowerCase();
                if (String.valueOf(patient.getId()).contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getFirstName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getLastName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getPhone().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getEmail().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getCity().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getDistrict().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (patient.getRegisteredDate().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                patientTable.setPlaceholder(new Text("No record matches your search"));
                return false;
            });
            SortedList<Patient> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(patientTable.comparatorProperty());
            patientTable.setItems(sortedList);
        }));
    }
    
    private void initiliazeColumns() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        city.setCellValueFactory(new PropertyValueFactory<>("city"));
        district.setCellValueFactory(new PropertyValueFactory<>("district"));
        action.setCellValueFactory(new PropertyValueFactory<>("controlsPane"));
    }
    
    private void loadPatientData() {
        data.clear();
        String sql = "SELECT * FROM Patient";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.add(new Patient(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getString(10), setActionButtons(resultSet.getInt(1))));
            }
            patientTable.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(viewPatientController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(viewPatientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private HBox setActionButtons(int userId) {
        String query = "delete from Patient where Id = ?";
        String query1 = "select * from Patient where Id = ?";
        HBox hBox = new HBox(5);
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        icon.setSize("17");
        icon.setFill(Color.web("#66ddff"));
        HBox.setMargin(icon, new Insets(2, 0, 0, 0));
        icon.setId(String.valueOf(userId));
        icon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Patient patient = (Patient) patientTable.getSelectionModel().getSelectedItem();
                    try {
                        FXMLLoader xMLLoader = new FXMLLoader(getClass().getResource("/app/view/registerPatient.fxml"));
                        Parent root = xMLLoader.load();
                        registerPatientController patientController = (registerPatientController) xMLLoader.getController();
                        patientController.inflateUI(patient);
                        BorderPane.setMargin(root, new Insets(0, 0, 0, 10));
                        doctorPanelController.root.setCenter(root);
                        registerPatientController.editMode = Boolean.TRUE;
                    } catch (IOException ex) {
                        Logger.getLogger(viewPatientController.class.getName()).log(Level.SEVERE, null, ex);
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
                alert.setContentText("Are you sure you want to delete this client record?");
                Optional<ButtonType> opti = alert.showAndWait();
                if (opti.get() == ButtonType.OK) {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, userId);
                    preparedStatement.executeUpdate();
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "Client successfully deleted", NotificationType.SUCCESS);
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
                    Logger.getLogger(viewPatientController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadPatientData();
            }
        }
        );
        
        FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.UPLOAD);
        icon2.setSize("17");
        icon2.setFill(Color.web("#66ddff"));
        icon2.setId(String.valueOf(userId));
        icon2.setOnMouseClicked((e) -> {
            Patient patient = (Patient) patientTable.getSelectionModel().getSelectedItem();
            if (patient != null) {
                try {
                    FXMLLoader xMLLoader = new FXMLLoader(getClass().getResource("/app/view/addViralSigns.fxml"));
                    Parent root = xMLLoader.load();
                    addViralSignsController controller = (addViralSignsController) xMLLoader.getController();
                    controller.getClientId(patient);
                    BorderPane.setMargin(root, new Insets(0, 0, 0, 10));
                    doctorPanelController.root.setCenter(root);
                } catch (IOException ex) {
                    Logger.getLogger(viewPatientController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hBox.getChildren().addAll(icon, icon2, icon1);
        return hBox;
    }
    
    public class Patient {
        
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty address;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty city;
        private final SimpleStringProperty district;
        private final SimpleStringProperty dob;
        private final SimpleStringProperty registeredDate;
        HBox controlsPane;
        
        public Patient(int id, String firstName, String lastName, String address, String email, String phone, String city, String district, String dob, String registeredDate, HBox box) {
            this.id = new SimpleIntegerProperty(id);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.address = new SimpleStringProperty(address);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.city = new SimpleStringProperty(city);
            this.district = new SimpleStringProperty(district);
            this.dob = new SimpleStringProperty(dob);
            this.registeredDate = new SimpleStringProperty(registeredDate);
            this.controlsPane = box;
        }
        
        public int getId() {
            return id.get();
        }
        
        public String getFirstName() {
            return firstName.get();
        }
        
        public String getLastName() {
            return lastName.get();
        }
        
        public String getAddress() {
            return address.get();
        }
        
        public String getEmail() {
            return email.get();
        }
        
        public String getPhone() {
            return phone.get();
        }
        
        public String getCity() {
            return city.get();
        }
        
        public String getDistrict() {
            return district.get();
        }
        
        public String getDob() {
            return dob.get();
        }
        
        public String getRegisteredDate() {
            return registeredDate.get();
        }
        
        public HBox getControlsPane() {
            return controlsPane;
        }
    }
}
