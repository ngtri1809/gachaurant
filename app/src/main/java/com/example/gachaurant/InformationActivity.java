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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;


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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InformationActivity extends AppCompatActivity {
    private static final String TAG = "InformationActivity";
    private static final int INITIAL_DISTANCE = 1;
    private static final int REQUEST_CODE = 100;
    final String apiKey = BuildConfig.API_KEY;
    List<Restaurant> restaurantList;
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
        String[] placeTypeList = {"restaurant"};
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
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //When click on location
        locationImg.setOnClickListener(v -> getLastLocation());
        //Confirm button click
        confirmButton.setOnClickListener(v -> {
            //Loop through the array to check for the checked box
            for (int checkBoxId : checkBoxIds) {
                CheckBox checkBox = findViewById(checkBoxId);
                Log.d("Checkbox State", checkBox.getText().toString() + ": " + checkBox.isChecked());
                preference.put(checkBox.getText().toString(), checkBox.isChecked());
            }

            JSONObject requestObject = new JSONObject();
            try {
                requestObject.put("keyword", "restaurant");
                requestObject.put("location", new JSONObject().put("lat", latitude).put("lng", longitude));
                requestObject.put("radius", 5000);
                requestObject.put("type", "restaurant");
                requestObject.put("key", apiKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            restaurantList = new ArrayList<>();
            fetchNearbyPlaces(latitude, longitude, restaurantList);

            Toast.makeText(InformationActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
        });
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
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
            }).addOnFailureListener(e -> {
                // Handle the failure to get the last location
                Toast.makeText(InformationActivity.this, "Failed to get last location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }else {
            askPermission();
        }
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

    private String downloadUrl(String string) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(string);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private void fetchDataFromUrl(String url, List<Restaurant> restaurantList) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String jsonData = downloadUrl(url);
                if (jsonData != null) {
                    runOnUiThread(() -> parseJsonAndCreateRestaurantObjects(jsonData, restaurantList));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void parseJsonAndCreateRestaurantObjects(String jsonData, List<Restaurant> restaurantList) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject placeObject = resultsArray.getJSONObject(i);

                String name = placeObject.getString("name");
                double rating = placeObject.optDouble("rating", 0.0);
                String type = placeObject.getString("types");
                double lat = placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double lng = placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                String address = "";
                if (placeObject.has("vicinity")) {
                    address = placeObject.getString("vicinity");
                }
                Restaurant restaurant = new Restaurant(name, rating, address, type, lat, lng);
                Log.d(TAG, "restaurantList: " + restaurantList.toString());
                restaurantList.add(restaurant);
            }
            uploadToDatabase();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchNearbyPlaces(double latitude, double longitude, List<Restaurant> restaurantList) {
        // Build the URL for nearby places
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + latitude + "," + longitude +
                "&radius=" + Integer.toString(radius) +
                "&type=restaurant" +
                "&key=" + apiKey;

        fetchDataFromUrl(url, restaurantList);
    }
    private void uploadToDatabase() {
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        List<Map<String, Object>> restaurantMaps = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            Map<String, Object> restaurantMap = new HashMap<>();
            restaurantMap.put("name", restaurant.getName());
            restaurantMap.put("rating", restaurant.getRating());
            restaurantMap.put("address", restaurant.getAddress());
            restaurantMap.put("type", restaurant.getType());
            restaurantMap.put("latitude", restaurant.getLatitude());
            restaurantMap.put("longitude", restaurant.getLongitude());
            restaurantMaps.add(restaurantMap);
        }

        // Create map of user's info
        Map<String, Object> info = new HashMap<>();
        info.put("preference", preference);
        info.put("location", locationEt.getText().toString().trim());
        info.put("distance", radius);
        info.put("restaurantList", restaurantMaps);

        documentReference.update(info).addOnSuccessListener(unused -> {
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "onSuccess: user profile is created for " + userID + " and preference " + preference);
            Log.d(TAG, "Nearby Restaurants: " + restaurantList.toString());
        });

        Toast.makeText(InformationActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
    }
}