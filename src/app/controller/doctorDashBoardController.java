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
import java.time.LocalDate;
import java.time.temporal.TemporalField;
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
public class doctorDashBoardController implements Initializable {

    @FXML
    private HBox pane1;
    @FXML
    private Text allClients;
    @FXML
    private HBox pane2;
    @FXML
    private Text newClients;
    @FXML
    private HBox pane3;
    @FXML
    private Text returningClients;
    @FXML
    private VBox chartPane;
    @FXML
    private JFXToggleButton newClientsToggleButton;
    @FXML
    private JFXToggleButton returningClientsToggleButton;
    @FXML
    private Gauge allClientsGauge;
    @FXML
    private Gauge newClientsGauge;
    @FXML
    private Gauge allReturningClientsGuage;
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
        allClientsGauge.setBarBackgroundColor(Color.web("#13ACCE"));
        allClientsGauge.setBarColor(Color.WHITE);
        newClientsGauge.setBarBackgroundColor(Color.web("#BE7351"));
        newClientsGauge.setBarColor(Color.WHITE);
        allReturningClientsGuage.setBarBackgroundColor(Color.web("#1DC07A"));
        allReturningClientsGuage.setBarColor(Color.WHITE);

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
                Thread.sleep(2_000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getNumberOfAllUsers();
                        databaseHelper.populateChaert(chart,returningClient,newclients);
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

    private void getNumberOfAllUsers() {
        String sql1 = "SELECT COUNT(*) FROM Patient";
        String sql2 = "SELECT ClientId, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= ?";
        String sql3 = "SELECT ClientId, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= ?";
        int allclients = 0;
        int newclients = 0;
        int count = 0;
        int count1 = 0;
        int returningclients = 0;
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql1);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                allclients = resultSet.getInt(1);
                if (allclients == 1) {
                    allClientsGauge.setValue(allclients);
                    allClients.setText(allclients + " Client");
                } else {
                    allClientsGauge.setValue(allclients);
                    allClients.setText(allclients + " Clients");
                }
                preparedStatement.clearParameters();
                resultSet.close();
            } else {
                allClients.setText("0 Clients");
            }
            preparedStatement.clearParameters();
            resultSet.close();
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, 3);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count++;
            }
            returningclients = count;
            if (returningclients == 0) {
                returningClients.setText("0 Returning Clients");
                preparedStatement.clearParameters();
                resultSet.close();
            } else {
                if (returningclients == 1) {
                    allReturningClientsGuage.setValue(returningclients);
                    returningClients.setText(returningclients + " Returning Client");
                } else {
                    allReturningClientsGuage.setValue(returningclients);
                    returningClients.setText(returningclients + " Returning Clients");
                }
                preparedStatement.clearParameters();
                resultSet.close();
            }
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setInt(1, 2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count1++;
            }
            newclients = count1;
            if (newclients == 0) {
                newClients.setText("0 New Clients");
                preparedStatement.clearParameters();
                resultSet.close();
            } else {
                if (newclients == 1) {
                    newClientsGauge.setValue(newclients);
                    newClients.setText(newclients + " New Client");
                } else {
                    newClientsGauge.setValue(newclients);
                    newClients.setText(newclients + " New Clients");
                }
                preparedStatement.clearParameters();
                resultSet.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(doctorDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(doctorDashBoardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
