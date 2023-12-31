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
    List<Map<String, Object>> restaurantMaps;
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
        restaurantInventory = new ArrayList<>();
        restaurantList = new ArrayList<>();
        DocumentReference docRef = fStore.collection("users").document(userId);
        //Retrieve Restaurant
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                restaurantInventory.clear(); // restaurantInventory gets populated every times roll button is clicked (event) so we have to clear every times
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
                if (error != null) {
                    Log.e(TAG, "Error listening to Firestore snapshot", error);
                    return;
                }
                if (value != null && value.get("restaurantList") != null) {
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
            }
        });
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If there are no distance nearby
                if(restaurantList.isEmpty()){
                    Toast.makeText(RollActivity.this,"No restaurant nearby, please increase the distance",Toast.LENGTH_SHORT).show();
                }
                else{
                    Restaurant res = selectRandomRestaurant(restaurantList);
                    if(restaurantInventory.contains(res)){
                        Toast.makeText(RollActivity.this,"You rolled a duplicated restaurant: " + res.getName(),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        restaurantInventory.add(res);
                        updateRestaurantInventoryInFirebase();
                        Toast.makeText(RollActivity.this,"You rolled " + res.getName(),Toast.LENGTH_SHORT).show();
                    }
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
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("restaurantInventory", restaurantInventory);

        docRef.update(updateData)
                .addOnSuccessListener(unused -> Log.d(TAG, "RestaurantInventory updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating restaurantInventory", e));
    }
}