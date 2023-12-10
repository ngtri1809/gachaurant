package com.example.gachaurant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gachaurant.adapters.RestaurantClickedCallback;
import com.example.gachaurant.adapters.RestaurantsAdapter;
import com.example.gachaurant.restaurantPackage.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckIn extends AppCompatActivity implements RestaurantClickedCallback {
    Button backButton;
    RecyclerView restaurantsRecyclerView;
    List<Restaurant> restaurants = new ArrayList<>();
    private final String TAG = CheckIn.class.getSimpleName();
    private RestaurantsAdapter adapter;
    private long level = 1;
    private long experience = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_checkin);

        backButton = findViewById(R.id.backBtn2);

        // Set click listeners for each button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getRestaurants();
    }

    private void getRestaurants() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error listening to Firestore snapshot", error);
                    return;
                }
                if(value.get("level")!=null){
                    level = (long) value.get("level");
                }
                if(value.get("experience")!=null){
                    experience = (long) value.get("experience");
                }

                if (value != null && value.get("restaurantInventory") != null){
                    restaurants.clear();
                    List<Map<String, Object>> restaurantMaps = new ArrayList<>();
                    restaurantMaps = (List<Map<String, Object>>) value.get("restaurantInventory");
                    for (Map<String, Object> restaurantMap : restaurantMaps) {
                        String name = (String) restaurantMap.get("name");
                        boolean isCheckIn;
                        if (restaurantMap.containsKey("checkIn")) {
                            isCheckIn = (boolean) restaurantMap.get("checkIn");
                        }
                        else {
                            isCheckIn = false;
                        }
                        double rating = (double) restaurantMap.get("rating");
                        String type = (String) restaurantMap.get("type");
                        double latitude = (double) restaurantMap.get("latitude");
                        double longitude = (double) restaurantMap.get("longitude");
                        String address = (String) restaurantMap.get("address");
                        Restaurant restaurant = new Restaurant(name, rating, address, type, latitude, longitude);
                        restaurant.setCheckIn(isCheckIn);
                        restaurants.add(restaurant);
                    }
                    setupRecyclerView();
                }

            }
        });
    }

    private void setupRecyclerView() {
        restaurantsRecyclerView = findViewById(R.id.restaurantsRecyclerView);
        List<Restaurant> nonCheckInRestaurants = new ArrayList<>();
        for(int i=0;i<restaurants.size();i++){
            if(!restaurants.get(i).isCheckIn()) {
                nonCheckInRestaurants.add(restaurants.get(i));
            }
        }
        adapter = new RestaurantsAdapter(this, nonCheckInRestaurants, this);
        restaurantsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void restaurantClicked(Restaurant restaurant, int position) {
        // Restaurant id, userId
        Toast.makeText(this, "Checked in at: " + restaurant.getName(), Toast.LENGTH_SHORT).show();
        saveInSharedPrefs();
        restaurant.setCheckIn(true);
        saveCheckInInfo();
        adapter.notifyDataSetChanged();
    }

    private void saveCheckInInfo(){
        experience +=10;
        if(experience>100){
            experience -= 100;
            level++;
        }
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("restaurantInventory", restaurants);
        data.put("level", level);
        data.put("experience", experience);
        docRef.update(data);
    }

    private void saveInSharedPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int checkInCount = sharedPreferences.getInt(RewardsActivity.CHECK_IN_COUNT_KEY, 0);

        checkInCount++;
        sharedPreferences.edit().putInt(RewardsActivity.CHECK_IN_COUNT_KEY, checkInCount).apply();
    }
}