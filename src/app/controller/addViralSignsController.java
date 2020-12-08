/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.DatabaseHelper;
import app.model.ViralSigns;
import app.util.ShowAlert;
import app.util.ShowTrayNotification;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class addViralSignsController implements Initializable {

    @FXML
    private AnchorPane pane2;
    @FXML
    private JFXTextField weightLoss;
    @FXML
    private JFXTextField height;
    @FXML
    private JFXRadioButton rach1;
    @FXML
    private ToggleGroup rashG;
    @FXML
    private JFXRadioButton rach0;
    @FXML
    private JFXRadioButton throat1;
    @FXML
    private ToggleGroup throatG;
    @FXML
    private JFXRadioButton throat0;
    @FXML
    private JFXRadioButton grands1;
    @FXML
    private ToggleGroup grandG;
    @FXML
    private JFXRadioButton grands0;
    @FXML
    private JFXRadioButton headache1;
    @FXML
    private ToggleGroup headG;
    @FXML
    private JFXRadioButton headache0;
    @FXML
    private JFXRadioButton stomach1;
    @FXML
    private ToggleGroup stoG;
    @FXML
    private JFXRadioButton stomach0;
    @FXML
    private JFXRadioButton pain1;
    @FXML
    private ToggleGroup painG;
    @FXML
    private JFXRadioButton pain0;
    @FXML
    private JFXRadioButton aches1;
    @FXML
    private ToggleGroup achesG;
    @FXML
    private JFXRadioButton aches0;
    @FXML
    private JFXRadioButton diarrhoea1;
    @FXML
    private ToggleGroup diarrG;
    @FXML
    private JFXRadioButton diarrhoea0;
    @FXML
    private JFXTextField viralLoad;
    int id;
    String rash = "";
    String throat = "";
    String grands = "";
    String headache = "";
    String stomach = "";
    String pain = "";
    String aches = "";
    String diarrhoea = "";
    public static Boolean editMode = Boolean.FALSE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setRadioButtonsSelectedColor();
    }

    @FXML
    private void cancelSaveFunction(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void saveViralLoad(ActionEvent event) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        LocalDate returningDate = LocalDate.now();
        int viralload = Integer.parseInt(viralLoad.getText());
        if (validateFields()) {
            if (editMode) {
                if (viralload < 700) {
                    ViralSigns signs = new ViralSigns(id, Float.valueOf(weightLoss.getText()), Float.valueOf(height.getText()), rash, throat, grands, headache, stomach, pain, aches, diarrhoea, "Defaulter", "Second Line regimens");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update signs");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.get() == ButtonType.OK) {
                        if (databaseHelper.updateViralSigns(signs)) {
                            clearFields();
                            ShowTrayNotification notification = new ShowTrayNotification("Message", "Viral signs succssfully updated", NotificationType.SUCCESS);
                        } else {
                            ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to update the record", NotificationType.ERROR);
                        }
                    }
                }
                if (viralload >= 700) {
                    ViralSigns signs = new ViralSigns(id, Float.valueOf(weightLoss.getText()), Float.valueOf(height.getText()), rash, throat, grands, headache, stomach, pain, aches, diarrhoea, "Normal", "First Line regimens");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update signs");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.get() == ButtonType.OK) {
                        if (databaseHelper.updateViralSigns(signs)) {
                            clearFields();
                            ShowTrayNotification notification = new ShowTrayNotification("Message", "Viral signs succssfully updated", NotificationType.SUCCESS);
                        } else {
                            ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to update the record", NotificationType.ERROR);
                        }
                    }
                }
                return;
            }
            if (viralload < 700) {
                ViralSigns signs = new ViralSigns(id, databaseHelper.getClientFullName(id), databaseHelper.getClientEmail(id), Float.valueOf(weightLoss.getText()), Float.valueOf(height.getText()), rash, throat, grands, headache, stomach, pain, aches, diarrhoea, LocalDate.now().toString(), returningDate.plusDays(30).toString(), "Defaulter", "Second Line regimens", "False");
                if (databaseHelper.saveViralSigns(signs)) {
                    clearFields();
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "Viral signs succssfully added", NotificationType.SUCCESS);
                } else {
                    ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to add the record", NotificationType.ERROR);
                }
            }
            if (viralload >= 700) {
                ViralSigns signs = new ViralSigns(id, databaseHelper.getClientFullName(id), databaseHelper.getClientEmail(id), Float.valueOf(weightLoss.getText()), Float.valueOf(height.getText()), rash, throat, grands, headache, stomach, pain, aches, diarrhoea, LocalDate.now().toString(), returningDate.plusDays(30).toString(), "Normal", "First Line regimens", "False");
                if (databaseHelper.saveViralSigns(signs)) {
                    clearFields();
                    ShowTrayNotification notification = new ShowTrayNotification("Message", "Viral signs succssfully added", NotificationType.SUCCESS);
                } else {
                    ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to add the record", NotificationType.ERROR);
                }
            }
        }
    }

    public void getClientId(viewPatientController.Patient patient) {
        id = patient.getId();
    }

    private Boolean validateFields() {
        if (rach1.isSelected()) {
            rash = "Yes";
        }
        if (rach0.isSelected()) {
            rash = "No";
        }
        if (throat1.isSelected()) {
            throat = "Yes";
        }
        if (throat0.isSelected()) {
            throat = "No";
        }
        if (grands1.isSelected()) {
            grands = "Yes";
        }
        if (grands0.isSelected()) {
            grands = "No";
        }
        if (headache1.isSelected()) {
            headache = "Yes";
        }
        if (headache0.isSelected()) {
            headache = "No";
        }
        if (stomach1.isSelected()) {
            stomach = "Yes";
        }
        if (stomach0.isSelected()) {
            stomach = "No";
        }
        if (pain1.isSelected()) {
            pain = "Yes";
        }
        if (pain0.isSelected()) {
            pain = "No";
        }
        if (aches1.isSelected()) {
            aches = "Yes";
        }
        if (aches0.isSelected()) {
            aches = "No";
        }
        if (diarrhoea1.isSelected()) {
            diarrhoea = "Yes";
        }
        if (diarrhoea0.isSelected()) {
            diarrhoea = "No";
        }
        if (weightLoss.getText().isEmpty() || height.getText().isEmpty() || viralLoad.getText().isEmpty() || rash.isEmpty() || throat.isEmpty() || grands.isEmpty() || headache.isEmpty() || stomach.isEmpty() || pain.isEmpty() || aches.isEmpty() || diarrhoea.isEmpty()) {
            ShowAlert alert = new ShowAlert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void setRadioButtonsSelectedColor() {
        rach1.setSelectedColor(Color.web("#66ddff"));
        rach0.setSelectedColor(Color.web("#66ddff"));
        throat1.setSelectedColor(Color.web("#66ddff"));
        throat1.setSelectedColor(Color.web("#66ddff"));
        grands1.setSelectedColor(Color.web("#66ddff"));
        grands0.setSelectedColor(Color.web("#66ddff"));
        headache1.setSelectedColor(Color.web("#66ddff"));
        headache0.setSelectedColor(Color.web("#66ddff"));
        stomach1.setSelectedColor(Color.web("#66ddff"));
        stomach0.setSelectedColor(Color.web("#66ddff"));
        pain1.setSelectedColor(Color.web("#66ddff"));
        pain0.setSelectedColor(Color.web("#66ddff"));
        aches1.setSelectedColor(Color.web("#66ddff"));
        aches0.setSelectedColor(Color.web("#66ddff"));
        diarrhoea1.setSelectedColor(Color.web("#66ddff"));
        diarrhoea0.setSelectedColor(Color.web("#66ddff"));
    }

    private void clearFields() {
        weightLoss.clear();
        height.clear();
        rach1.setSelected(Boolean.FALSE);
        rach0.setSelected(Boolean.FALSE);
        throat1.setSelected(Boolean.FALSE);
        throat0.setSelected(Boolean.FALSE);
        grands1.setSelected(Boolean.FALSE);
        grands0.setSelected(Boolean.FALSE);
        headache1.setSelected(Boolean.FALSE);
        headache0.setSelected(Boolean.FALSE);
        stomach1.setSelected(Boolean.FALSE);
        stomach0.setSelected(Boolean.FALSE);
        pain1.setSelected(Boolean.FALSE);
        pain0.setSelected(Boolean.FALSE);
        aches1.setSelected(Boolean.FALSE);
        aches0.setSelected(Boolean.FALSE);
        diarrhoea1.setSelected(Boolean.FALSE);
        diarrhoea0.setSelected(Boolean.FALSE);
        viralLoad.clear();
    }

    public void inflateUi(viewSignsController.Signs signs) {
        id = signs.getId();
        DatabaseHelper databaseHelper = new DatabaseHelper();
        ResultSet resultSet = databaseHelper.getViralSignsData(signs.getId());
        try {
            resultSet.next();
            weightLoss.setText(String.valueOf(resultSet.getFloat("weightLoss")));
            height.setText(String.valueOf(resultSet.getFloat("Height")));
            if (resultSet.getString("Rash").equals("Yes")) {
                rach1.setSelected(true);
            } else {
                rach0.setSelected(true);
            }
            if (resultSet.getString("Throat").equals("Yes")) {
                throat1.setSelected(true);
            } else {
                throat0.setSelected(true);
            }
            if (resultSet.getString("Gland").equals("Yes")) {
                grands1.setSelected(true);
            } else {
                grands0.setSelected(true);
            }
            if (resultSet.getString("Headache").equals("Yes")) {
                headache1.setSelected(true);
            } else {
                headache0.setSelected(true);
            }
            if (resultSet.getString("Stomach").equals("Yes")) {
                stomach1.setSelected(true);
            } else {
                stomach0.setSelected(true);
            }
            if (resultSet.getString("Pain").equals("Yes")) {
                pain1.setSelected(true);
            } else {
                pain0.setSelected(true);
            }
            if (resultSet.getString("Aches").equals("Yes")) {
                aches1.setSelected(true);
            } else {
                aches0.setSelected(true);
            }
            if (resultSet.getString("Diarrhoea").equals("Yes")) {
                diarrhoea1.setSelected(true);
            } else {
                diarrhoea0.setSelected(true);
            }
            if (resultSet.getString("Diarrhoea").equals("Yes")) {
                diarrhoea1.setSelected(true);
            } else {
                diarrhoea0.setSelected(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(addViralSignsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
