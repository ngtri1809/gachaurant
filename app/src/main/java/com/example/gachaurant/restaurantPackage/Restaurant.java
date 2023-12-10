package com.example.gachaurant.restaurantPackage;

import java.util.Objects;

public class Restaurant {
    private String name;
    private double latitude;
    private double longitude;
    private double rating;
    private String type;
    private String address;

    private boolean checkIn;

    public Restaurant(String name, double rating, String address, String type, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.type = type;
        this.address =  address;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    public String getAddress() {
        return address;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Restaurant restaurant = (Restaurant) obj;
        return Double.compare(restaurant.rating, rating) == 0 &&
                Double.compare(restaurant.latitude, latitude) == 0 &&
                Double.compare(restaurant.longitude, longitude) == 0 &&
                name.equals(restaurant.name) &&
                type.equals(restaurant.type) &&
                address.equals(restaurant.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rating, type, latitude, longitude, address);
    }

}
