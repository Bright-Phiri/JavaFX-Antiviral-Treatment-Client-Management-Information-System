/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.model.DatabaseHelper;
import com.jfoenix.controls.JFXToggleButton;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.chart.SmoothedChart;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class dashBoardController implements Initializable {

    @FXML
    private HBox pane1;
    @FXML
    private Gauge allUsersGauge;
    @FXML
    private HBox pane2;
    @FXML
    private HBox pane3;
    @FXML
    private VBox chartPane;
    @FXML
    private Text usersLabel;
    @FXML
    private Text studentsLabel;
    @FXML
    private Text employeesLabel;
    @FXML
    private Gauge allDoctorsGauge;
    @FXML
    private Gauge allClientsGauge;
    @FXML
    private JFXToggleButton newClientsToggleButton;
    @FXML
    private JFXToggleButton returningClientsToggleButton;
    SmoothedChart chart;
    XYChart.Series returningClient;
    XYChart.Series newclients;
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    DatabaseHelper databaseHelper;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allUsersGauge.setBarBackgroundColor(Color.web("#13ACCE"));
        allUsersGauge.setBarColor(Color.WHITE);
        allDoctorsGauge.setBarBackgroundColor(Color.web("#BE7351"));
        allDoctorsGauge.setBarColor(Color.WHITE);
        allClientsGauge.setBarBackgroundColor(Color.web("#1DC07A"));
        allClientsGauge.setBarColor(Color.WHITE);
        databaseHelper = new DatabaseHelper();
        NumberAxis axis = new NumberAxis();
        CategoryAxis axis1 = new CategoryAxis();
        axis1.setAnimated(false);
        chart = new SmoothedChart(axis1, axis);
        chart.setSmoothed(true);
        chart.setChartType(SmoothedChart.ChartType.AREA);
        chart.setInteractive(true);
        chart.setAnimated(true);
        chart.setChartPlotBackground(Color.WHITE);
        chart.setLegendVisible(false);
        chartPane.getChildren().add(chart);
        chart.toBack();
        returningClient = new XYChart.Series();
        newclients = new XYChart.Series();
        Task<Void> loadGuages = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1_000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getNumberOfAllUsers();
                        databaseHelper.populateChaert(chart, returningClient, newclients);
                        chart.setSymbolSize(newclients, 7);
                        chart.setSymbolSize(returningClient, 7);
                        chart.setSymbolFill(newclients, new Background(new BackgroundFill(Color.web("#FF9978"), new CornerRadii(10), Insets.EMPTY)));
                        chart.setSymbolFill(returningClient, new Background(new BackgroundFill(Color.web("#7371F4"), new CornerRadii(10), Insets.EMPTY)));
                        installSeriesTootTip(returningClient);
                        installSeriesTootTip(newclients);
                    }
                });
                return null;
            }
        };

        Thread thread = new Thread(loadGuages);
        thread.setDaemon(true);
        thread.start();

    }

    private void installSeriesTootTip(XYChart.Series series) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data data = (XYChart.Data) series.getData().get(i);
            data.getNode().toFront();
            Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString() + " : " + data.getYValue()));
        }
    }

    private void getNumberOfAllUsers() {
        String sql = "SELECT COUNT(*) FROM User WHERE Role != 'Admin'";
        String sql1 = "SELECT COUNT(*) FROM Patient";
        int allUsers = 0;
        int allDoctors = 0;
        int allClients = 0;
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            allDoctors = resultSet.getInt(1);
            preparedStatement.clearParameters();
            resultSet.close();
            preparedStatement = connection.prepareStatement(sql1);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            allClients = resultSet.getInt(1);
            allUsers = allClients + allDoctors;
            allUsersGauge.setMaxValue(allUsers);
            allUsersGauge.setValue(allUsers);
            allDoctorsGauge.setMaxValue(allUsers);
            allDoctorsGauge.setValue(allDoctors);
            allClientsGauge.setMaxValue(allUsers);
            allClientsGauge.setValue(allClients);
            usersLabel.setText(allUsers + " Users");
            studentsLabel.setText(allDoctors + " Doctors");
            employeesLabel.setText(allClients + " Clients");
        } catch (SQLException ex) {
            Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

     @FXML
    private void loadClientsrGraph(ActionEvent event) {
        chart.setAnimated(false);
        if (newClientsToggleButton.isSelected()) {
            chart.getData().addAll(newclients);
            chart.setAnimated(true);
        } else {
            chart.getData().remove(newclients);
            chart.setAnimated(true);
        }
    }

    @FXML
    private void loadCnewClientsGraph(ActionEvent event) {
        chart.setAnimated(false);
        if (returningClientsToggleButton.isSelected()) {
            chart.getData().addAll(returningClient);
            chart.setAnimated(true);
        } else {
            chart.getData().remove(returningClient);
            chart.setAnimated(true);
        }
    }

}
