package com.example.gachaurant.restaurantPackage;

import java.util.List;

public interface RestaurantCallback {
    void onRestaurantListUpdated(List<Restaurant> restaurantList);
}
