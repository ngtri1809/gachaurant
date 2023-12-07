package com.example.gachaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gachaurant.restaurantPackage.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RollActivity extends AppCompatActivity {
    private static final String TAG = "RollActivty";
    Button roll;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;
    List<Restaurant> restaurantList;
    List<Restaurant> restaurantInventory;
    Button homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll);
        homeButton = findViewById(R.id.homeButton);
        roll = findViewById(R.id.rollButton);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        if(user != null) {
            userId = fAuth.getCurrentUser().getUid();
        }
        restaurantList = new ArrayList<>();
        restaurantInventory = new ArrayList<>();
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error listening to Firestore snapshot", error);
                    return;
                }
                if (value != null && value.get("restaurantList") != null) {
                    List<Map<String, Object>> restaurantMaps = new ArrayList<>();
                    restaurantMaps = (List<Map<String, Object>>) value.get("restaurantList");
                    for (Map<String, Object> restaurantMap : restaurantMaps) {
                        String name = (String) restaurantMap.get("name");
                        double rating = (double) restaurantMap.get("rating");
                        String type = (String) restaurantMap.get("type");
                        double latitude = (double) restaurantMap.get("latitude");
                        double longitude = (double) restaurantMap.get("longitude");
                        String address = (String) restaurantMap.get("address");
                        Restaurant restaurant = new Restaurant(name, rating, address,type, latitude, longitude);
                        restaurantList.add(restaurant);
                        Log.d(TAG, "restaurantList: " + restaurantList.toString());
                    }
                }
                if (value != null && value.get("restaurantInventory") != null){
                    List<Map<String, Object>> restaurantMaps = new ArrayList<>();
                    restaurantMaps = (List<Map<String, Object>>) value.get("restaurantInventory");
                    for (Map<String, Object> restaurantMap : restaurantMaps) {

                        String name = (String) restaurantMap.get("name");
                        double rating = (double) restaurantMap.get("rating");
                        String type = (String) restaurantMap.get("type");
                        double latitude = (double) restaurantMap.get("latitude");
                        double longitude = (double) restaurantMap.get("longitude");
                        String address = (String) restaurantMap.get("address");
                        Restaurant restaurant = new Restaurant(name, rating, address, type, latitude, longitude);
                        restaurantInventory.add(restaurant);
                        Log.d(TAG, "restaurantInventory: " + restaurantInventory.toString());
                    }
                }
            }
        });
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant res = selectRandomRestaurant(restaurantList);
                if(restaurantInventory.contains(res)){
                    //Level implementation
                    Toast.makeText(RollActivity.this,"You rolled a duplicated restaurant: " + res.getName(),Toast.LENGTH_SHORT).show();
                }
                else{
                    restaurantInventory.add(res);
                    Toast.makeText(RollActivity.this,"You rolled" + res.getName(),Toast.LENGTH_SHORT).show();
                    updateRestaurantInventoryInFirebase();
                }
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            }
        });
    }
    public static Restaurant selectRandomRestaurant(List<Restaurant> restaurantList) {
        if (restaurantList == null || restaurantList.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(restaurantList.size());
        return restaurantList.get(randomIndex);
    }
    private void updateRestaurantInventoryInFirebase() {
        if(user != null) {
            userId = fAuth.getCurrentUser().getUid();
        }
        DocumentReference docRef = fStore.collection("users").document(userId);

        // Create map to update restaurantInventory field
        Map<String, Object> updateData = new HashMap<>();
        List<Map<String, Object>> restaurantInventoryMaps = new ArrayList<>();

        for (Restaurant restaurant : restaurantInventory) {
            Map<String, Object> restaurantMap = new HashMap<>();
            restaurantMap.put("name", restaurant.getName());
            restaurantMap.put("rating", restaurant.getRating());
            restaurantMap.put("type", restaurant.getType());
            restaurantMap.put("latitude", restaurant.getLatitude());
            restaurantMap.put("longitude", restaurant.getLongitude());
            restaurantMap.put("address", restaurant.getAddress());
            restaurantInventoryMaps.add(restaurantMap);
        }

        updateData.put("restaurantInventory", restaurantInventoryMaps);

        docRef.update(updateData)
                .addOnSuccessListener(unused -> Log.d(TAG, "RestaurantInventory updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating restaurantInventory", e));
    }
}