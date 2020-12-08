/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.model.Database;
import app.util.ShowTrayNotification;
import com.jfoenix.controls.JFXComboBox;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class doctorStatisticsController implements Initializable {

    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private JFXComboBox<String> exportPdf;
    @FXML
    private JFXComboBox<String> exportWord;
    @FXML
    private PieChart pieChart;
    ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    String[] reportType = {"All Clients", "New Clients", "Returning Clients"};
    @FXML
    private AnchorPane pane1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
          Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    setChartData();
                });
                return null;
            }
        };
        new Thread(task).start();
        exportPdf.setItems(FXCollections.observableArrayList(reportType));
        exportWord.setItems(FXCollections.observableArrayList(reportType));
    } 
    
     private void setChartData() {
        String sql1 = "SELECT COUNT(*) FROM Patient";
        String sql2 = "SELECT ClientId, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= ?";
        String sql3 = "SELECT ClientId, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= ?";
        int allUsers = 0;
        int allDoctors = 0;
        int allClients = 0;
        int returningClients = 0;
        int newclients = 0;
        int count = 0;
        int count1 = 0;
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql1);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            allClients = resultSet.getInt(1);
            allUsers = allClients + allDoctors;
            preparedStatement.clearParameters();
            resultSet.close();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, 3);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count++;
            }
            returningClients = count;

            preparedStatement.clearParameters();
            resultSet.close();
            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setInt(1, 2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count1++;
            }
            newclients = count1;
            data.addAll(
                    new PieChart.Data("Clients", allClients),
                    new PieChart.Data("New Clients", newclients),
                    new PieChart.Data("Returning Clients", returningClients));
            pieChart.setData(data);
            intsallPieChaertToolTip(pieChart);
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data<>("Clients", allClients));
            series.getData().add(new XYChart.Data<>("New Clients", newclients));
            series.getData().add(new XYChart.Data<>("Returning Clients", returningClients));
            barchart.getData().add(series);
            installSeriesTootTip(series);
        } catch (SQLException ex) {
            Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     private void installSeriesTootTip(XYChart.Series series) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data data1 = (XYChart.Data) series.getData().get(i);
            data1.getNode().toFront();
            Tooltip.install(data1.getNode(), new Tooltip(data1.getXValue().toString() + " : " + data1.getYValue().toString().replace(".0", "")));
        }
    }

    private void intsallPieChaertToolTip(PieChart chart) {
        chart.getData().forEach((d) -> {
            d.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Tooltip.install(d.getNode(), new Tooltip(d.getName() + " : " + (int) d.getPieValue() + ""));
                }
            });
        });
    }

    @FXML
    private void exportToPdf(ActionEvent event) {
        String reporttype = exportPdf.getSelectionModel().getSelectedItem();
        switch (reporttype) {
            case "All Clients": {
                Thread thread = new Thread(new GenerateReport2());
                thread.setDaemon(true);
                thread.start();

                break;
            }
            case "New Clients": {
                Thread thread = new Thread(new GenerateReport3());
                thread.setDaemon(true);
                thread.start();
                break;
            }
            case "Returning Clients": {
                Thread thread = new Thread(new GenerateReport4());
                thread.setDaemon(true);
                thread.start();
                break;
            }
        }
    }

    @FXML
    private void exportToExcel(ActionEvent event) {
        String reporttype = exportWord.getSelectionModel().getSelectedItem();
        switch (reporttype) {
            case "All Clients": {
                try {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement("SELECT count(*) FROM Patient");
                    resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    int numOfRows = resultSet.getInt(1);
                    if (numOfRows == 1) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setContentText("No records to export in the System");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    } else {
                        try {
                            preparedStatement.clearParameters();
                            resultSet.close();
                            preparedStatement = connection.prepareStatement("SELECT * FROM Patient");
                            resultSet = preparedStatement.executeQuery();
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    XSSFWorkbook workbook = new XSSFWorkbook();
                                    XSSFSheet sheet = workbook.createSheet("Patients");
                                    XSSFRow header = sheet.createRow(0);
                                    header.createCell(0).setCellValue("First Name");
                                    header.createCell(1).setCellValue("Last Name");
                                    header.createCell(2).setCellValue("Address");
                                    header.createCell(3).setCellValue("Email");
                                    header.createCell(4).setCellValue("Contact");
                                    header.createCell(5).setCellValue("City");
                                    header.createCell(6).setCellValue("District");
                                    header.createCell(7).setCellValue("DOB");
                                    sheet.autoSizeColumn(1);
                                    sheet.autoSizeColumn(2);
                                    sheet.autoSizeColumn(3);
                                    sheet.autoSizeColumn(4);
                                    sheet.autoSizeColumn(5);
                                    sheet.autoSizeColumn(6);
                                    sheet.autoSizeColumn(7);
                                    sheet.autoSizeColumn(8);
                                    sheet.setZoom(150);
                                    int index = 1;
                                    while (resultSet.next()) {
                                        XSSFRow row = sheet.createRow(index);
                                        row.createCell(0).setCellValue(resultSet.getString(2));
                                        row.createCell(1).setCellValue(resultSet.getString(3));
                                        row.createCell(2).setCellValue(resultSet.getString(4));
                                        row.createCell(3).setCellValue(resultSet.getString(5));
                                        row.createCell(4).setCellValue(resultSet.getString(6));
                                        row.createCell(5).setCellValue(resultSet.getString(7));
                                        row.createCell(6).setCellValue(resultSet.getString(8));
                                        row.createCell(7).setCellValue(resultSet.getString(9));
                                        index++;
                                    }
                                    File userReport = new File(System.getProperty("user.home") + "\\Documents\\System Reports\\Clients.xlsx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(userReport);
                                    workbook.write(fileOutputStream);
                                    fileOutputStream.close();
                                    Platform.runLater(() -> {
                                        ShowTrayNotification notification = new ShowTrayNotification("Information", "Report saved to documents/System reports", NotificationType.INFORMATION);
                                    });
                                    Thread.sleep(1000);
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Open report");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Open report ?");
                                        Optional<ButtonType> optional = alert.showAndWait();
                                        if (optional.get().equals(ButtonType.OK)) {
                                            Desktop desktop = Desktop.getDesktop();
                                            try {
                                                desktop.open(userReport);

                                            } catch (IOException ex) {
                                                Logger.getLogger(doctorStatisticsController.class
                                                        .getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });
                                    return null;
                                }

                                @Override
                                protected void succeeded() {
                                    super.succeeded();
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
                                        Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            };
                            Thread thread = new Thread(task);
                            thread.setDaemon(true);
                            thread.start();

                        } catch (SQLException ex) {
                            Logger.getLogger(statisticsController.class
                                    .getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(statisticsController.class
                            .getName()).log(Level.SEVERE, null, ex);

                }
                break;
            }
            case "New Clients": {
                try {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement("SELECT * FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= 2");
                    resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    int numOfRows = resultSet.getInt(1);
                    if (numOfRows == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setContentText("No records to export in the System");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    } else {
                        try {
                            preparedStatement.clearParameters();
                            resultSet.close();
                            preparedStatement = connection.prepareStatement("SELECT * FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= 2");
                            resultSet = preparedStatement.executeQuery();
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    XSSFWorkbook workbook = new XSSFWorkbook();
                                    XSSFSheet sheet = workbook.createSheet("New Clients");
                                    XSSFRow header = sheet.createRow(0);
                                    header.createCell(0).setCellValue("Client Name");
                                    header.createCell(1).setCellValue("weight Loss");
                                    header.createCell(2).setCellValue("Height");
                                    header.createCell(3).setCellValue("Body Rash");
                                    header.createCell(4).setCellValue("Sore Throat");
                                    header.createCell(5).setCellValue("Swollen Glands");
                                    header.createCell(6).setCellValue("Headache");
                                    header.createCell(7).setCellValue("Upset Stomach");
                                    header.createCell(8).setCellValue("Muscle Pain");
                                    header.createCell(9).setCellValue("Joint Aches");
                                    header.createCell(10).setCellValue("Chronical Diarrhoea");
                                    header.createCell(11).setCellValue("Treatment Date");
                                    header.createCell(12).setCellValue("Returning Date");
                                    header.createCell(13).setCellValue("Result");
                                    sheet.autoSizeColumn(1);
                                    sheet.autoSizeColumn(2);
                                    sheet.autoSizeColumn(3);
                                    sheet.autoSizeColumn(4);
                                    sheet.autoSizeColumn(5);
                                    sheet.autoSizeColumn(6);
                                    sheet.autoSizeColumn(7);
                                    sheet.autoSizeColumn(8);
                                    sheet.autoSizeColumn(9);
                                    sheet.autoSizeColumn(10);
                                    sheet.autoSizeColumn(11);
                                    sheet.autoSizeColumn(12);
                                    sheet.autoSizeColumn(13);
                                    sheet.autoSizeColumn(14);
                                    sheet.autoSizeColumn(15);
                                    sheet.setZoom(150);
                                    int index = 1;
                                    while (resultSet.next()) {
                                        XSSFRow row = sheet.createRow(index);
                                        row.createCell(0).setCellValue(resultSet.getString(3));
                                        row.createCell(1).setCellValue(resultSet.getFloat(5));
                                        row.createCell(2).setCellValue(resultSet.getFloat(6));
                                        row.createCell(3).setCellValue(resultSet.getString(7));
                                        row.createCell(4).setCellValue(resultSet.getString(8));
                                        row.createCell(5).setCellValue(resultSet.getString(9));
                                        row.createCell(6).setCellValue(resultSet.getString(10));
                                        row.createCell(7).setCellValue(resultSet.getString(11));
                                        row.createCell(8).setCellValue(resultSet.getString(12));
                                        row.createCell(9).setCellValue(resultSet.getString(13));
                                        row.createCell(10).setCellValue(resultSet.getString(14));
                                        row.createCell(11).setCellValue(resultSet.getString(15));
                                        row.createCell(12).setCellValue(resultSet.getString(16));
                                        row.createCell(13).setCellValue(resultSet.getString(17));
                                        index++;
                                    }
                                    File userReport = new File(System.getProperty("user.home") + "\\Documents\\System Reports\\New Clients.xlsx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(userReport);
                                    workbook.write(fileOutputStream);
                                    fileOutputStream.close();
                                    Platform.runLater(() -> {
                                        ShowTrayNotification notification = new ShowTrayNotification("Information", "Report saved to documents/System reports", NotificationType.INFORMATION);
                                    });
                                    Thread.sleep(1000);
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Open report");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Open report ?");
                                        Optional<ButtonType> optional = alert.showAndWait();
                                        if (optional.get().equals(ButtonType.OK)) {
                                            Desktop desktop = Desktop.getDesktop();
                                            try {
                                                desktop.open(userReport);

                                            } catch (IOException ex) {
                                                Logger.getLogger(doctorStatisticsController.class
                                                        .getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });
                                    return null;
                                }

                                @Override
                                protected void succeeded() {
                                    super.succeeded();
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
                                        Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            };
                            Thread thread = new Thread(task);
                            thread.setDaemon(true);
                            thread.start();

                        } catch (SQLException ex) {
                            Logger.getLogger(statisticsController.class
                                    .getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(doctorStatisticsController.class
                            .getName()).log(Level.SEVERE, null, ex);

                }
                break;
            }
            case "Returning Clients": {
                try {
                    connection = Database.connect();
                    preparedStatement = connection.prepareStatement("SELECT * FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= 3");
                    resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    int numOfRows = resultSet.getInt(1);
                    if (numOfRows == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setContentText("No records to export in the System");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    } else {
                        try {
                            preparedStatement.clearParameters();
                            resultSet.close();
                            preparedStatement = connection.prepareStatement("SELECT * FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= 3");
                            resultSet = preparedStatement.executeQuery();
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    XSSFWorkbook workbook = new XSSFWorkbook();
                                    XSSFSheet sheet = workbook.createSheet("Returning Clients");
                                    XSSFRow header = sheet.createRow(0);
                                    header.createCell(0).setCellValue("Client Name");
                                    header.createCell(1).setCellValue("weight Loss");
                                    header.createCell(2).setCellValue("Height");
                                    header.createCell(3).setCellValue("Body Rash");
                                    header.createCell(4).setCellValue("Sore Throat");
                                    header.createCell(5).setCellValue("Swollen Glands");
                                    header.createCell(6).setCellValue("Headache");
                                    header.createCell(7).setCellValue("Upset Stomach");
                                    header.createCell(8).setCellValue("Muscle Pain");
                                    header.createCell(9).setCellValue("Joint Aches");
                                    header.createCell(10).setCellValue("Chronical Diarrhoea");
                                    header.createCell(11).setCellValue("Treatment Date");
                                    header.createCell(12).setCellValue("Returning Date");
                                    header.createCell(13).setCellValue("Result");
                                    sheet.autoSizeColumn(1);
                                    sheet.autoSizeColumn(2);
                                    sheet.autoSizeColumn(3);
                                    sheet.autoSizeColumn(4);
                                    sheet.autoSizeColumn(5);
                                    sheet.autoSizeColumn(6);
                                    sheet.autoSizeColumn(7);
                                    sheet.autoSizeColumn(8);
                                    sheet.autoSizeColumn(9);
                                    sheet.autoSizeColumn(10);
                                    sheet.autoSizeColumn(11);
                                    sheet.autoSizeColumn(12);
                                    sheet.autoSizeColumn(13);
                                    sheet.autoSizeColumn(14);
                                    sheet.autoSizeColumn(15);
                                    sheet.setZoom(150);
                                    int index = 1;
                                    while (resultSet.next()) {
                                        XSSFRow row = sheet.createRow(index);
                                        row.createCell(0).setCellValue(resultSet.getString(3));
                                        row.createCell(1).setCellValue(resultSet.getFloat(5));
                                        row.createCell(2).setCellValue(resultSet.getFloat(6));
                                        row.createCell(3).setCellValue(resultSet.getString(7));
                                        row.createCell(4).setCellValue(resultSet.getString(8));
                                        row.createCell(5).setCellValue(resultSet.getString(9));
                                        row.createCell(6).setCellValue(resultSet.getString(10));
                                        row.createCell(7).setCellValue(resultSet.getString(11));
                                        row.createCell(8).setCellValue(resultSet.getString(12));
                                        row.createCell(9).setCellValue(resultSet.getString(13));
                                        row.createCell(10).setCellValue(resultSet.getString(14));
                                        row.createCell(11).setCellValue(resultSet.getString(15));
                                        row.createCell(12).setCellValue(resultSet.getString(16));
                                        row.createCell(13).setCellValue(resultSet.getString(17));
                                        index++;
                                    }
                                    File userReport = new File(System.getProperty("user.home") + "\\Documents\\System Reports\\Returning Clients.xlsx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(userReport);
                                    workbook.write(fileOutputStream);
                                    fileOutputStream.close();
                                    Platform.runLater(() -> {
                                        ShowTrayNotification notification = new ShowTrayNotification("Information", "Report saved to documents/System reports", NotificationType.INFORMATION);
                                    });
                                    Thread.sleep(1000);
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Open report");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Open report ?");
                                        Optional<ButtonType> optional = alert.showAndWait();
                                        if (optional.get().equals(ButtonType.OK)) {
                                            Desktop desktop = Desktop.getDesktop();
                                            try {
                                                desktop.open(userReport);

                                            } catch (IOException ex) {
                                                Logger.getLogger(doctorStatisticsController.class
                                                        .getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });
                                    return null;
                                }

                                @Override
                                protected void succeeded() {
                                    super.succeeded();
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
                                        Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            };
                            Thread thread = new Thread(task);
                            thread.setDaemon(true);
                            thread.start();

                        } catch (SQLException ex) {
                            Logger.getLogger(doctorStatisticsController.class
                                    .getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(doctorStatisticsController.class
                            .getName()).log(Level.SEVERE, null, ex);

                }
                break;
            }
        }
    }

    public class SaveReport {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        private final String query;
        private final String reportName;
        private final String reportUrl;

        public SaveReport(String query, String reportName, String reportUrl) {
            this.query = query;
            this.reportName = reportName;
            this.reportUrl = reportUrl;
        }

        private void saveReport() {
            File reportsFolder = new File(System.getProperty("user.home") + "\\Documents\\Reports");
            if (!reportsFolder.exists()) {
                reportsFolder.mkdir();
            }
            File file = new File(System.getProperty("user.home") + "\\Documents\\Reports\\" + reportName + ".pdf");
            if (file.exists()) {
            } else {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File file1 = new File(System.getProperty("user.home") + "\\Documents\\Reports\\" + reportName + ".pdf");
            try {
                connection = Database.connect();
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                if (count == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ShowTrayNotification notification = new ShowTrayNotification("Information", "No records in System", NotificationType.INFORMATION);
                        }
                    });
                    return;
                }
                if (file.renameTo(file1)) {
                    JasperReport report = JasperCompileManager.compileReport(getClass().getResourceAsStream("/app/reports/" + reportUrl + ".jrxml"));
                    JasperPrint print = JasperFillManager.fillReport(report, null, connection);
                    JasperExportManager.exportReportToPdfFile(print, file.toString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ShowTrayNotification notification = new ShowTrayNotification("Information", "Report saved to Documents/Reports", NotificationType.INFORMATION);
                        }
                    });
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Open report");
                        alert.setHeaderText(null);
                        alert.setContentText("Open report ?");
                        Optional<ButtonType> optional = alert.showAndWait();
                        if (optional.get().equals(ButtonType.OK)) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.open(file);
                            } catch (IOException ex) {
                                Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to save the report", NotificationType.ERROR);
                        }
                    });
                }
            } catch (JRException ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowTrayNotification notification = new ShowTrayNotification("Error", "Failed to save the report", NotificationType.ERROR);
                    }
                });
            } catch (SQLException ex) {
                Logger.getLogger(statisticsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(doctorStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class GenerateReport2 extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            SaveReport report = new SaveReport("SELECT COUNT(*) FROM Patient ", "clients", "clients");
            report.saveReport();
            return null;
        }
    }

    private class GenerateReport3 extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            SaveReport report = new SaveReport("SELECT COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= 2", "newClients", "newClients");
            report.saveReport();
            return null;
        }
    }

    private class GenerateReport4 extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            SaveReport report = new SaveReport("SELECT COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= 3", "returningClients", "returningClient");
            report.saveReport();
            return null;
        }
    }
    
}
