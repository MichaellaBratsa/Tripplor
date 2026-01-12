package com.mbrats01.epl498_group_project.ui.signIn;


public class User {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private double lon,lat;
    private String id;

    public User( String fullName, String username, String email, String password, String phoneNumber, double lon, double lat) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.lon = lon;
        this.lat = lat;
        this.id = id;
    }

    public User( String fullName, String username, String email, String password, String phoneNumber, String id) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getId()
    {
        return this.id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

}