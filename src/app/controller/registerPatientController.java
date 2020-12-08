/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.DatabaseHelper;
import app.model.Patient;
import app.util.ShowAlert;
import app.util.ShowTrayNotification;
import app.util.Util;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class registerPatientController implements Initializable {
    
    @FXML
    private AnchorPane pane2;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField address;
    @FXML
    private JFXComboBox<String> district;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField city;
    @FXML
    private DatePicker dob;
    DatabaseHelper helper;
    Util util;
    int id;
    public static Boolean editMode = Boolean.FALSE;
    String phoneSugg[] = {"+26599", "+26588"};
    String citySugg[] = {"Zomba", "Lilongwe", "Mzuzu", "Blantyre"};
    String districtslist[] = {
        "Chitipa",
        "Rumphi",
        "Mzuzu",
        "Karonga",
        "Mzimba",
        "Mchinji",
        "Ntchisi",
        "Dowa",
        "Dedza",
        "Kasungu",
        "Ntcheu",
        "Salima",
        "Nkhotakota",
        "Nkhatabay",
        "Lilongwe",
        "Mangochi",
        "Machinga",
        "Zomba",
        "Balaka",
        "Neno",
        "Chiradzulu",
        "Chikhwawa",
        "Nsanje",
        "Blantyre",
        "Likoma",
        "Thyolo",
        "Phalombe",
        "Mwanza",
        "Mulanje"
    };

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        district.setItems(FXCollections.observableArrayList(districtslist));
        helper = new DatabaseHelper();
        util = new Util();
        setUserData();
        TextFields.bindAutoCompletion(phone, phoneSugg);
        TextFields.bindAutoCompletion(city, citySugg);
    }
    
    @FXML
    private void cancelSaveOperation(ActionEvent event) {
        clearCache();
    }
    
    @FXML
    private void savePatientRecord(ActionEvent event) {
        LocalDate date = LocalDate.now();
        if (isFieldsEmpty() && util.validateName(firstName) && util.validateName(lastName) && util.validateEmail(email) && util.validateName(city) && validateCity() && util.validatePhoneNumber(phone)) {
            Patient patient = new Patient(firstName.getText(), lastName.getText(), address.getText(), email.getText(), phone.getText(), city.getText(), district.getValue(), dob.getValue().toString(), date.toString());
            if (editMode) {
                patient.setId(id);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Update client");
                alert.setHeaderText(null);
                alert.setContentText("Save changes?");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    if (helper.updatePatient(patient)) {
                        ShowTrayNotification notification = new ShowTrayNotification("Message", "Client successfully updated", NotificationType.SUCCESS);
                        clearCache();
                    } else {
                        ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to update client", NotificationType.ERROR);
                    }
                    editMode = Boolean.FALSE;
                    return;
                }
            }
            if (helper.addPatient(patient)) {
                ShowTrayNotification notification = new ShowTrayNotification("Message", "Client successfully added", NotificationType.SUCCESS);
                clearCache();
            } else {
                ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to add client", NotificationType.ERROR);
            }
        }
    }
    
    private Boolean isFieldsEmpty() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || address.getText().isEmpty() || email.getText().isEmpty()
                || phone.getText().isEmpty() || city.getText().isEmpty() || district.getValue() == null || dob.getValue() == null) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private void clearCache() {
        firstName.clear();
        lastName.clear();
        address.clear();
        email.clear();
        phone.clear();
        city.clear();
        district.setValue(null);
        dob.setValue(null);
        ((TextField) dob.getEditor()).setText(null);
        editMode = Boolean.FALSE;
    }
    
    private void setUserData() {
        firstName.setUserData("first name");
        lastName.setUserData("last name");
        city.setUserData("city");
    }
    
    private Boolean validateCity() {
        if (city.getText().equalsIgnoreCase("Zomba") || city.getText().equalsIgnoreCase("Lilongwe") || city.getText().equalsIgnoreCase("Mzuzu") || city.getText().equalsIgnoreCase("Blantyre")) {
            return true;
        } else {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "City validation", "Please enter a valid city");
            return false;
        }
    }
    
    public void inflateUI(viewPatientController.Patient patient) {
        id = patient.getId();
        firstName.setText(patient.getFirstName());
        lastName.setText(patient.getLastName());
        address.setText(patient.getAddress());
        email.setText(patient.getEmail());
        phone.setText(patient.getPhone());
        city.setText(patient.getCity());
        district.setValue(patient.getDistrict());
        dob.setValue(LocalDate.parse(patient.getDob()));
    }
}
