/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.model;

import eu.hansolo.tilesfx.chart.SmoothedChart;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.XYChart;

/**
 *
 * @author Bright
 */
public class DatabaseHelper {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    FileInputStream fis;

    public static String USERTYPE;
    public static String USERNAME;
    public static int ID;

    public Boolean userSignIn(String userName, String password) {
        String sql = "SELECT * FROM User WHERE Username = ? AND Password = ? ";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ID = resultSet.getInt(1);
                USERNAME = resultSet.getString(4);
                USERTYPE = resultSet.getString(7);
                return Boolean.TRUE;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Boolean.FALSE;
    }

    public Boolean addUser(User user) {
        String sql = "INSERT INTO User (FirstName,LastName,Username,Email,Phone,Role,Image,Password) VALUES (?,?,?,?,?,?,?,?)";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getEmailAddress());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getUserType());
            fis = new FileInputStream(user.getImge());
            preparedStatement.setBinaryStream(7, (InputStream) fis, user.getImge().length());
            preparedStatement.setString(8, user.getPassword());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | FileNotFoundException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean updateUser(User user) {
        String sql = "UPDATE User SET FirstName = ?,LastName =?,Username = ?,Email = ?,Phone  = ?, Role = ?,Image = ?,Password = ? WHERE Id = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getEmailAddress());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getUserType());
            fis = new FileInputStream(user.getImge());
            preparedStatement.setBinaryStream(7, (InputStream) fis, user.getImge().length());
            preparedStatement.setString(8, user.getPassword());
            preparedStatement.setInt(9, user.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | FileNotFoundException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean addPatient(Patient patient) {
        String sql = "INSERT INTO Patient (FirstName,LastName,Address,Email,Contact,City,District,DOB,RegisterdDate) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getFirstName());
            preparedStatement.setString(2, patient.getLastName());
            preparedStatement.setString(3, patient.getAddress());
            preparedStatement.setString(4, patient.getEmailAddress());
            preparedStatement.setString(5, patient.getPhone());
            preparedStatement.setString(6, patient.getCity());
            preparedStatement.setString(7, patient.getDistrict());
            preparedStatement.setString(8, patient.getDOB());
            preparedStatement.setString(9, patient.getAddedDate());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean updatePatient(Patient patient) {
        String sql = "UPDATE Patient SET FirstName = ?,LastName =?,Address = ?,Email = ?,Contact  = ?, City = ?,District = ?,DOB = ? WHERE Id = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getFirstName());
            preparedStatement.setString(2, patient.getLastName());
            preparedStatement.setString(3, patient.getAddress());
            preparedStatement.setString(4, patient.getEmailAddress());
            preparedStatement.setString(5, patient.getPhone());
            preparedStatement.setString(6, patient.getCity());
            preparedStatement.setString(7, patient.getDistrict());
            preparedStatement.setString(8, patient.getDOB());
            preparedStatement.setInt(9, patient.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean saveViralSigns(ViralSigns signs) {
        String sql = "INSERT INTO VirtualSigns (ClientId,ClientName,ClientEmail,weightLoss,Height,Rash,Throat,Gland,Headache,Stomach,Pain,Aches,Diarrhoea,TreatmentDate,ReturningDate,Result,Prescription,Notify) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, signs.getClientId());
            preparedStatement.setString(2, signs.getClientName());
            preparedStatement.setString(3, signs.getEmail());
            preparedStatement.setFloat(4, signs.getWeightLoss());
            preparedStatement.setFloat(5, signs.getHeight());
            preparedStatement.setString(6, signs.getRash());
            preparedStatement.setString(7, signs.getThroat());
            preparedStatement.setString(8, signs.getGland());
            preparedStatement.setString(9, signs.getHeadache());
            preparedStatement.setString(10, signs.getStomach());
            preparedStatement.setString(11, signs.getPain());
            preparedStatement.setString(12, signs.getAches());
            preparedStatement.setString(13, signs.getDiarrhoe());
            preparedStatement.setString(14, signs.getTreatmentDate());
            preparedStatement.setString(15, signs.getRertuningDate());
            preparedStatement.setString(16, signs.getResult());
            preparedStatement.setString(17, signs.getPrescription());
            preparedStatement.setString(18, signs.getNotify());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean updateViralSigns(ViralSigns signs) {
        String sql = "UPDATE VirtualSigns SET weightLoss = ?,Height = ?,Rash = ?,Throat = ?,Gland = ?,Headache = ?,Stomach = ?,Pain = ?,Aches = ?,Diarrhoea = ? WHERE ID = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setFloat(1, signs.getWeightLoss());
            preparedStatement.setFloat(2, signs.getHeight());
            preparedStatement.setString(3, signs.getRash());
            preparedStatement.setString(4, signs.getThroat());
            preparedStatement.setString(5, signs.getGland());
            preparedStatement.setString(6, signs.getHeadache());
            preparedStatement.setString(7, signs.getStomach());
            preparedStatement.setString(8, signs.getPain());
            preparedStatement.setString(9, signs.getAches());
            preparedStatement.setString(10, signs.getDiarrhoe());
            preparedStatement.setInt(11, signs.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean ifAdminExists() {
        String sql = "SELECT * FROM User WHERE Role = 'Admin'";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Boolean.TRUE;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Boolean.FALSE;
    }

    public int ifRecords() {
        int numberOfRecords = 0;
        String sql = "SELECT COUNT(*) FROM User WHERE Role = 'Admin'";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            numberOfRecords = resultSet.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return numberOfRecords;
    }

    public Boolean createAccount(Admin admin) {
        String sql = "INSERT INTO User (Username,Email,Phone,Role,Image,Password) VALUES (?,?,?,?,?,?)";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, admin.getUserName());
            preparedStatement.setString(2, admin.getEmailAddress());
            preparedStatement.setString(3, admin.getPhone());
            preparedStatement.setString(4, "Admin");
            fis = new FileInputStream(admin.getImge());
            preparedStatement.setBinaryStream(5, (InputStream) fis, admin.getImge().length());
            preparedStatement.setString(6, admin.getPassword());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | FileNotFoundException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean updateAdmin(Admin admin) {
        String sql = "UPDATE User SET Username = ?,Email = ?,Phone = ?,Image = ? WHERE Role  = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, admin.getUserName());
            preparedStatement.setString(2, admin.getEmailAddress());
            preparedStatement.setString(3, admin.getPhone());
            fis = new FileInputStream(admin.getImge());
            preparedStatement.setBinaryStream(4, (InputStream) fis, admin.getImge().length());
            preparedStatement.setString(5, "Admin");
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | FileNotFoundException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getClientFullName(int id) {
        String sql = "SELECT FirstName, LastName FROM Patient WHERE Id = ?";
        String clientFullName = "";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                clientFullName = resultSet.getString("FirstName") + " " + resultSet.getString("LastName");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return clientFullName;
    }

    public String getClientEmail(int id) {
        String sql = "SELECT Email FROM Patient WHERE Id = ?";
        String email = "";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                email = resultSet.getString("Email");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return email;
    }

    public Boolean validatePassword(int id, String password) {
        String sql = "SELECT * FROM User WHERE Id = ? AND Password  = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Boolean.TRUE;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Boolean.FALSE;
    }

    public Boolean updatePassword(int id, String passwrd) {
        String sql = "UPDATE User SET Password = ? WHERE Id = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, passwrd);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Boolean.FALSE;
    }

    public void populateChaert(SmoothedChart chart, XYChart.Series returningClient, XYChart.Series newclients) {
        String sql2 = "SELECT ClientId,TreatmentDate, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) >= ?";
        String sql3 = "SELECT ClientId,TreatmentDate, COUNT(*) FROM VirtualSigns GROUP BY  ClientId HAVING COUNT(*) <= ?";
        int januaryCounter = 0;
        int februaryCounter = 0;
        int marchCounter = 0;
        int apilrCounter = 0;
        int mayCounter = 0;
        int juneCounter = 0;
        int julyCounter = 0;
        int augustCounter = 0;
        int septembeCounter = 0;
        int octCounter = 0;
        int novemberCounter = 0;
        int decemberCounter = 0;

        int januaryCountern = 0;
        int februaryCountern = 0;
        int marchCountern = 0;
        int apilrCountern = 0;
        int mayCountern = 0;
        int juneCountern = 0;
        int julyCountern = 0;
        int augustCountern = 0;
        int septembeCountern = 0;
        int octCountern = 0;
        int novemberCountern = 0;
        int decemberCountern = 0;
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, 3);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = LocalDate.parse(resultSet.getString("TreatmentDate"));
                switch (date.getMonth()) {
                    case JANUARY:
                        januaryCounter++;
                        break;
                    case FEBRUARY:
                        februaryCounter++;
                        break;
                    case MARCH:
                        marchCounter++;
                        break;
                    case APRIL:
                        apilrCounter++;
                        break;
                    case MAY:
                        mayCounter++;
                        break;
                    case JUNE:
                        juneCounter++;
                        break;
                    case JULY:
                        julyCounter++;
                        break;
                    case AUGUST:
                        augustCounter++;
                        break;
                    case SEPTEMBER:
                        septembeCounter++;
                        break;
                    case OCTOBER:
                        octCounter++;
                        break;
                    case NOVEMBER:
                        novemberCounter++;
                        break;
                    case DECEMBER:
                        decemberCounter++;
                        break;
                }
            }

            returningClient.getData().add(new XYChart.Data<>("Jan", januaryCounter));
            returningClient.getData().add(new XYChart.Data<>("Feb", februaryCounter));
            returningClient.getData().add(new XYChart.Data<>("Mar", marchCounter));
            returningClient.getData().add(new XYChart.Data<>("Apr", apilrCounter));
            returningClient.getData().add(new XYChart.Data<>("May", mayCounter));
            returningClient.getData().add(new XYChart.Data<>("Jun", juneCounter));
            returningClient.getData().add(new XYChart.Data<>("Jul", julyCounter));
            returningClient.getData().add(new XYChart.Data<>("Aug", augustCounter));
            returningClient.getData().add(new XYChart.Data<>("Sep", septembeCounter));
            returningClient.getData().add(new XYChart.Data<>("Oct", octCounter));
            returningClient.getData().add(new XYChart.Data<>("Nov", novemberCounter));
            returningClient.getData().add(new XYChart.Data<>("Dec", decemberCounter));
            resultSet.close();
            preparedStatement.clearParameters();

            connection = Database.connect();
            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setInt(1, 2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = LocalDate.parse(resultSet.getString("TreatmentDate"));
                switch (date.getMonth()) {
                    case JANUARY:
                        januaryCountern++;
                        break;
                    case FEBRUARY:
                        februaryCountern++;
                        break;
                    case MARCH:
                        marchCountern++;
                        break;
                    case APRIL:
                        apilrCountern++;
                        break;
                    case MAY:
                        mayCountern++;
                        break;
                    case JUNE:
                        juneCountern++;
                        break;
                    case JULY:
                        julyCountern++;
                        break;
                    case AUGUST:
                        augustCountern++;
                        break;
                    case SEPTEMBER:
                        septembeCountern++;
                        break;
                    case OCTOBER:
                        octCountern++;
                        break;
                    case NOVEMBER:
                        novemberCountern++;
                        break;
                    case DECEMBER:
                        decemberCountern++;
                        break;
                }
            }

            newclients.getData().add(new XYChart.Data<>("Jan", januaryCountern));
            newclients.getData().add(new XYChart.Data<>("Feb", februaryCountern));
            newclients.getData().add(new XYChart.Data<>("Mar", marchCountern));
            newclients.getData().add(new XYChart.Data<>("Apr", apilrCountern));
            newclients.getData().add(new XYChart.Data<>("May", mayCountern));
            newclients.getData().add(new XYChart.Data<>("Jun", juneCountern));
            newclients.getData().add(new XYChart.Data<>("Jul", julyCountern));
            newclients.getData().add(new XYChart.Data<>("Aug", augustCountern));
            newclients.getData().add(new XYChart.Data<>("Sep", septembeCountern));
            newclients.getData().add(new XYChart.Data<>("Oct", octCountern));
            newclients.getData().add(new XYChart.Data<>("Nov", novemberCountern));
            newclients.getData().add(new XYChart.Data<>("Dec", decemberCountern));
            chart.getData().addAll(returningClient, newclients);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ResultSet getViralSignsData(int id) {
        String qsl = "SELECT * FROM VirtualSigns WHERE ID = ?";
        try {
            connection = Database.connect();
            preparedStatement = connection.prepareStatement(qsl);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
}
