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
public class Admin extends User {

    public Admin(String username, String email,String phone ,File image, String password) {
        this.userName = username;
        this.emailAddress = email;
        this.phone = phone;
        this.imge = image;
        this.password = password;
    }
}
