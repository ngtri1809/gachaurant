package com.example.gachaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gachaurant.restaurantPackage.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantInventory extends AppCompatActivity {
    private static final String TAG = "RestaurantInventory";
    Button homeButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;
    List<Restaurant> restaurantInventory;
    LinearLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_inventory);
        //Init
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        if(user != null) {
            userId = fAuth.getCurrentUser().getUid();
        }
        restaurantInventory = new ArrayList<>();
        parentLayout = findViewById(R.id.parentLayout);
        homeButton = findViewById(R.id.homeButton);
        //geting restaurantinventory
        DocumentReference docRef = fStore.collection("users").document(userId);
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
                        if(restaurantInventory.contains(restaurant)){
                            //Level implementation
                        }
                        else
                            restaurantInventory.add(restaurant);
                        Log.d(TAG, "restaurantInventory: " + restaurantInventory.toString());
                    }
                }
                for(Restaurant res: restaurantInventory){
                    createRestaurantLayout(res);
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
    private void createRestaurantLayout(Restaurant res) {
        // Create a new RelativeLayout for each restaurant
        RelativeLayout relativeLayout = new RelativeLayout(this);

        // Set properties for the RelativeLayout (you can customize this based on your requirements)
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(15, 20, 15, 0); // Adjust margins as needed
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundResource(R.drawable.rounded_background); // Set background drawable

        // Create and configure ImageView
        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId()); // Set a unique ID
        imageView.setImageResource(R.mipmap.restaurant_foreground); // Set your image resource
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        imageParams.setMarginEnd(16); // Adjust margin as needed
        imageView.setLayoutParams(imageParams);

        // Create and configure TextView for restaurant name
        TextView restaurantNameTextView = new TextView(this);
        restaurantNameTextView.setId(View.generateViewId()); // Set a unique ID
        restaurantNameTextView.setText(res.getName()); // Set the restaurant name
        restaurantNameTextView.setTextSize(20); // Set text size
        restaurantNameTextView.setTypeface(null, Typeface.BOLD); // Set text style
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.addRule(RelativeLayout.END_OF, imageView.getId()); // Position to the right of the ImageView
        nameParams.addRule(RelativeLayout.BELOW, imageView.getId()); // Position below the ImageView
        nameParams.setMargins(0, 10, 0, 0); // Adjust margins as needed
        restaurantNameTextView.setLayoutParams(nameParams);

        // Create and configure TextView for address
        TextView addressTextView = new TextView(this);
        addressTextView.setId(View.generateViewId()); // Set a unique ID
        addressTextView.setText(res.getAddress()); // Set the restaurant address
        RelativeLayout.LayoutParams addressParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        addressParams.addRule(RelativeLayout.END_OF, imageView.getId()); // Position to the right of the ImageView
        addressParams.addRule(RelativeLayout.BELOW, restaurantNameTextView.getId()); // Position below the restaurant name TextView
        addressParams.setMargins(0, 4, 0, 0); // Adjust margins as needed
        addressTextView.setLayoutParams(addressParams);

        // Add the ImageView and TextViews to the RelativeLayout
        relativeLayout.addView(imageView);
        relativeLayout.addView(restaurantNameTextView);
        relativeLayout.addView(addressTextView);

        // Add the RelativeLayout to the parent LinearLayout
        parentLayout.addView(relativeLayout);
    }
}