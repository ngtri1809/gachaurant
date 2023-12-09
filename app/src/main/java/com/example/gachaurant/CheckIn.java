package com.example.gachaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gachaurant.adapters.RestaurantsAdapter;
import com.example.gachaurant.restaurantPackage.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CheckIn extends AppCompatActivity {
    Button backButton;
    RecyclerView restaurantsRecyclerView;
    List<Restaurant> restaurants = new ArrayList<>();
    private final String TAG = CheckIn.class.getSimpleName();

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
                        restaurants.add(restaurant);
                    }
                    setupRecyclerView();
                }

            }
        });
    }

    private void setupRecyclerView() {
        restaurantsRecyclerView = findViewById(R.id.restaurantsRecyclerView);
        RestaurantsAdapter adapter = new RestaurantsAdapter(this, restaurants);
        restaurantsRecyclerView.setAdapter(adapter);
    }
}