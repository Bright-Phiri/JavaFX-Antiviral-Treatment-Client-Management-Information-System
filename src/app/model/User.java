/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.model;

import java.io.File;

/**
 *
 * @author Bright
 */
public class User {

    protected int id;
    protected String addedDate;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String emailAddress;
    protected String userType;
    protected String phone;
    protected File imge;
    protected String password;
    
    public User(){
    }

    public User(String firstName, String lastName, String userName, String emailAddress, String phone, String userType, File imge, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.phone = phone;
        this.userType = userType;
        this.imge = imge;
        this.password = password;
    }

    public User(int id, String firstName, String lastName, String userName, String emailAddress, String phone, String userType, File imge, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.userType = userType;
        this.imge = imge;
        this.phone = phone;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getUserType() {
        return userType;
    }

    public String getPhone() {
        return phone;
    }

    public File getImge() {
        return imge;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImge(File imge) {
        this.imge = imge;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
