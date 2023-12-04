package com.example.gachaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gachaurant.restaurantPackage.Restaurant;
import com.example.gachaurant.restaurantPackage.RestaurantCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InformationActivity extends AppCompatActivity {
    private static final String TAG = "InformationActivity";
    private static final int INITIAL_DISTANCE = 1;
    private static final int REQUEST_CODE = 100;
    final String apiKey = BuildConfig.API_KEY;
    private List<Restaurant> restaurantList;
    FusedLocationProviderClient fusedLocationProviderClient;
    Map<String, Boolean> preference;
    Button confirmButton;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    ProgressBar progressBar;
    EditText locationEt;
    ImageView locationImg;

    SeekBar distanceSeekBar;
    TextView distanceAmount;
    double latitude, longitude;
    int radius;
    String restaurantString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //Initialize variables
        confirmButton = findViewById(R.id.confirmButton);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        preference = new HashMap<>();
        progressBar = findViewById(R.id.progressBar2);
        locationEt = findViewById(R.id.locationEditText);
        locationImg = findViewById(R.id.locationImage);
        distanceSeekBar = findViewById(R.id.seekBar);
        distanceAmount = findViewById(R.id.distanceAmount);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        distanceSeekBar.setProgress(INITIAL_DISTANCE);
        distanceAmount.setText(String.valueOf(INITIAL_DISTANCE));

        //Create an array of all check box
        int[] checkBoxIds = {
                R.id.vietnameseCB,
                R.id.chineseCB,
                R.id.koreanCB,
                R.id.thaiCB,
                R.id.mexicanCB,
                R.id.japaneseCB,
                R.id.indianCB,
                R.id.ramenCB,
                R.id.chickenCB,
                R.id.bbqCB,
                R.id.fastFoodCB,
                R.id.italianCB
        };
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceAmount.setText(String.valueOf(progress));
                radius = progress * 1000; //in meters
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //When click on location
        locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });
        //Confirm button click
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Loop through the array to check for the checked box
                for (int checkBoxId : checkBoxIds) {
                    CheckBox checkBox = findViewById(checkBoxId);
                    Log.d("Checkbox State", checkBox.getText().toString() + ": " + checkBox.isChecked());
                    preference.put(checkBox.getText().toString(), checkBox.isChecked());
                }
                userID = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("users").document(userID);
                fetchNearbyRestaurants(latitude, longitude, new RestaurantCallback() {
                    @Override
                    public void onRestaurantListUpdated(List<Restaurant> updatedList) {
                        restaurantList = updatedList;
                    }
                });
                List<Map<String, Object>> restaurantMaps = new ArrayList<>();
                for (Restaurant restaurant : restaurantList) {
                    Map<String, Object> restaurantMap = new HashMap<>();
                    restaurantMap.put("name", restaurant.getName());
                    restaurantMap.put("address", restaurant.getAddress());
                    restaurantMap.put("latitude", restaurant.getLatitude());
                    restaurantMap.put("longitude", restaurant.getLongitude());
                    restaurantMaps.add(restaurantMap);
                }
                //Create map of user's info
                Map<String, Object> info = new HashMap<>();
                info.put("preference", preference);
                info.put("location", locationEt.getText().toString().trim());
                info.put("distance", radius);
                info.put("restaurantList", restaurantMaps);
                documentReference.update(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onSuccess: user profile is created for" + userID + "and preference" + preference);
                    }
                });
                Toast.makeText(InformationActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            }
        });
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(InformationActivity.this, Locale.getDefault());
                        List<Address> address;
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //Get address of the user
                            locationEt.setText(address.get(0).getAddressLine(0)); //+ ", " + address.get(0).getLocality()
                            latitude = address.get(0).getLatitude();
                            longitude = address.get(0).getLongitude();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }else {
            askPermission();
        }
    }



    private void fetchNearbyRestaurants(double latitude, double longitude, RestaurantCallback callback) {
        //Init restaurant List
        restaurantList = new ArrayList<>();
        // Set up the Places API client
        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        // Define a location and radius for the nearby search
        LatLng location = new LatLng(latitude, longitude);

        // Create a request for nearby places (restaurants)
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Collections.singletonList(Place.Field.NAME));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

        // Handle the response
        placeResponse.addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
            @Override
            public void onSuccess(FindCurrentPlaceResponse response) {

                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();
                    double placeLatitude = place.getLatLng().latitude;
                    double placeLongitude = place.getLatLng().longitude;
                    Log.i("Place", String.format("Place '%s' has likelihood: %f", place.getName(), placeLikelihood.getLikelihood()));
                    double distance = calculateDistance(latitude, longitude, placeLatitude, placeLongitude);
                    // Create a Restaurant object and add it to the list
                    if(distance <= radius){
                        Restaurant restaurant = new Restaurant(place.getName(), place.getAddress(), place.getLatLng().latitude, place.getLatLng().longitude);
                        restaurantList.add(restaurant);
                    }
                }
                // Notify the callback with the updated restaurantList
                callback.onRestaurantListUpdated(restaurantList);
            }
        });
        placeResponse.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Places API", "Error getting place likelihoods: " + e.getMessage());
            }
        });
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // The radius of the Earth in meters
        final double R = 6371000; // Earth radius in meters

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        double distance = R * c;

        return distance;
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(InformationActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

}


