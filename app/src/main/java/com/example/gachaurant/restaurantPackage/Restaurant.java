package com.example.gachaurant.restaurantPackage;

public class Restaurant {
    private String name;
    private double latitude;
    private double longitude;
    private double rating;
    private String type;

    public Restaurant(String name, double rating, String type, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.type = type;
    }

    public double getRating() {
        return rating;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
