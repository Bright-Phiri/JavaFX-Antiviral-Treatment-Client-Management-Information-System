/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.model;

/**
 *
 * @author Bright
 */
public class Patient extends User {

    private final String address;
    private final String city;
    private final String district;
    private final String DOB;

    public Patient(String firstName, String lastName, String address, String email, String phone, String city, String district, String dob, String registerdDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailAddress = email;
        this.phone = phone;
        this.city = city;
        this.district = district;
        this.DOB = dob;
        this.addedDate = registerdDate;
    }

    public Patient(int id, String firstName, String lastName, String address, String email, String phone, String city, String district, String dob, String registeredDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailAddress = email;
        this.phone = phone;
        this.city = city;
        this.district = district;
        this.DOB = dob;
        this.addedDate = registeredDate;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getDOB() {
        return DOB;
    }

    public void setId(int id) {
        this.id = id;
    }

}
