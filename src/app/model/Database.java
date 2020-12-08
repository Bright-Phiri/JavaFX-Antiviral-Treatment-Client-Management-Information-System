/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.model;

import app.util.ShowAlert;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author Bright
 */
public class Database {

    private static Connection connection = null;
    private static ResultSet resultSet = null;
    private static Statement statement = null;

    public static Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/art?createDatabaseIfNotExist=true", "root", "");
            return connection;
        } catch (SQLException | ClassNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server error");
            alert.setHeaderText(null);
            alert.setContentText("Make sure the server is running");
            alert.showAndWait();
            return null;
        }
    }

    public static void checkTables(String tableName, String query) {
        try {
            
            connection = connect();
            if (connection == null){
                Platform.exit();
                System.exit(0);
            }
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, null, tableName, null);
            if (resultSet.next()) {
            } else {
                statement = connection.createStatement();
                statement.execute(query);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
