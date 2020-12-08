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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class viewSignsController implements Initializable {

    @FXML
    private TableView<Signs> signsTable;
    @FXML
    private TableColumn<Signs, Integer> id;
    @FXML
    private TableColumn<Signs, Integer> clientId;
    @FXML
    private TableColumn<Signs, String> name;
    @FXML
    private TableColumn<Signs, String> treatmentDate;
    @FXML
    private TableColumn<Signs, String> returningDate;
    @FXML
    private TableColumn<Signs, String> results;
    @FXML
    private TableColumn<Signs, String> prescription;
    @FXML
    private TableColumn<Signs, HBox> action;
    @FXML
    private AnchorPane pane1;
    @FXML
    private JFXTextField searchPatientTxtField;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private final ObservableList<Signs> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeColums();
        loadData();
    }

    @FXML
    private void searchPatient(KeyEvent event) {
        FilteredList<Signs> filteredList = new FilteredList<>(data, p -> true);
        searchPatientTxtField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Signs>) sign -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterToLowerCase = newValue.toLowerCase();
                if (String.valueOf(sign.getId()).toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (String.valueOf(sign.getClientId()).toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (sign.getName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (sign.getTreatmentDate().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (sign.getReturningDate().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (sign.getResult().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (sign.getPrescription().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                signsTable.setPlaceholder(new Text("No record matches your seach"));
                return false;
            });
            SortedList<Signs> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(signsTable.comparatorProperty());
            signsTable.setItems(sortedList);
        }));
    }

    private void initializeColums() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        treatmentDate.setCellValueFactory(new PropertyValueFactory<>("treatmentDate"));
        returningDate.setCellValueFactory(new PropertyValueFactory<>("returningDate"));
        results.setCellValueFactory(new PropertyValueFactory<>("result"));
        prescription.setCellValueFactory(new PropertyValueFactory<>("prescription"));
        action.setCellValueFactory(new PropertyValueFactory<>("box"));
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
                    Signs signs = (Signs) signsTable.getSelectionModel().getSelectedItem();
                    FXMLLoader xMLLoader = new FXMLLoader(getClass().getResource("/app/view/addViralSigns.fxml"));
                    Parent parent = xMLLoader.load();
                    addViralSignsController viralSignsController = (addViralSignsController) xMLLoader.getController();
                    viralSignsController.inflateUi(signs);
                    doctorPanelController.root.setCenter(parent);
                    addViralSignsController.editMode = Boolean.TRUE;
                } catch (IOException ex) {
                    Logger.getLogger(viewSignsController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(viewSignsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(viewSignsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
        hBox.getChildren().addAll(icon, icon1);
        return hBox;
    }

    private void loadData() {
        String sql = "SELECT * FROM VirtualSigns";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.add(new Signs(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getString(15), resultSet.getString(16), resultSet.getString(17), resultSet.getString(18), setActionButtons(resultSet.getInt(1))));
            }
            signsTable.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(viewSignsController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(viewSignsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class Signs {

        private final SimpleIntegerProperty id;
        private final SimpleIntegerProperty clientId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty treatmentDate;
        private final SimpleStringProperty returningDate;
        private final SimpleStringProperty result;
        private final SimpleStringProperty prescription;
        private final HBox box;

        public Signs(int id, int clientId, String name, String treatmentDate, String returningDate, String result, String prescription, HBox hBox) {
            this.id = new SimpleIntegerProperty(id);
            this.clientId = new SimpleIntegerProperty(clientId);
            this.name = new SimpleStringProperty(name);
            this.treatmentDate = new SimpleStringProperty(treatmentDate);
            this.returningDate = new SimpleStringProperty(returningDate);
            this.result = new SimpleStringProperty(result);
            this.prescription = new SimpleStringProperty(prescription);
            this.box = hBox;
        }

        public int getId() {
            return id.get();
        }

        public int getClientId() {
            return clientId.get();
        }

        public String getName() {
            return name.get();
        }

        public String getTreatmentDate() {
            return treatmentDate.get();
        }

        public String getReturningDate() {
            return returningDate.get();
        }

        public String getResult() {
            return result.get();
        }

        public String getPrescription() {
            return prescription.get();
        }

        public HBox getBox() {
            return box;
        }
    }

}
