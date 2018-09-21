package com.manik.whereisthebus;

public class ULocation {

    private double lat;
    private double longi;
    private String username;

    public ULocation(){}


    public ULocation(double lat, double longi, String username) {
        this.lat = lat;
        this.longi = longi;
        if(username == null){
            this.username = "Manik";
        }else {
            this.username = username;
        }

    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

    public String getUsername() {
        return username;
    }


}
