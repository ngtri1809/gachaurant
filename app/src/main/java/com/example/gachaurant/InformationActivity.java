package com.example.gachaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    FirebaseUser user;
    Boolean toastUpdate;

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
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        radius = INITIAL_DISTANCE;
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
        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.get("preference") != null){
                    Map<String, Boolean> userPref = (Map<String, Boolean>) value.get("preference");
                    for (Map.Entry<String, Boolean> entry : userPref.entrySet()) {
                        // Ensure the value is of Boolean type
                        if (entry.getValue() instanceof Boolean) {
                            userPref.put(entry.getKey(), (Boolean) entry.getValue());
                        }
                    }
                    // Iterate over the Map
                    for (Map.Entry<String, Boolean> entry : userPref.entrySet()) {
                        String checkboxName = entry.getKey();
                        boolean isChecked = entry.getValue();

                        // Find the corresponding checkbox by name
                        int checkboxId = getCheckboxIdByName(checkboxName, checkBoxIds);
                        if (checkboxId != -1) {
                            CheckBox checkBox = findViewById(checkboxId);
                            checkBox.setChecked(isChecked);
                        }
                    }
                }
                //Update the location if the account is registered
                if (value != null && value.get("location") != null){
                    locationEt.setText((String) value.get("location"));
                    toastUpdate = true;
                }else{
                    toastUpdate = false;
                }
                //Update the distance if the account is registered
                if(value != null && value.get("distance") != null){
                    int distance = ((Long) value.get("distance")).intValue() / 1000;
                    distanceSeekBar.setProgress(distance);
                }
            }
        });
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
//        When click on location
        locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Location image clicked");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted");
                    getLastLocation();
                } else {
                    Log.d(TAG, "Location permission not granted");
                    askPermission();
                }
            }
        });
        //Confirm button click
        confirmButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            //Loop through the array to check for the checked box
            for (int checkBoxId : checkBoxIds) {
                CheckBox checkBox = findViewById(checkBoxId);
                Log.d("Checkbox State", checkBox.getText().toString() + ": " + checkBox.isChecked());
                preference.put(checkBox.getText().toString(), checkBox.isChecked());
            }
            String locationAddress = locationEt.getText().toString().trim();
            if (locationAddress.isEmpty()) {
                locationEt.setError("Location cannot be empty");
                progressBar.setVisibility(View.GONE);
            }
            else{
                getLocationFromAddress(locationAddress);
                if(latitude != 0.0 && longitude != 0.0){
                    restaurantList = new ArrayList<>();
                    fetchNearbyPlaces(latitude, longitude, restaurantList);
                    progressBar.setVisibility(View.GONE);
                }
            }
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
                askPermission();
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
        if(toastUpdate)
            Toast.makeText(InformationActivity.this, "Preference updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(InformationActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
        if(restaurantMaps != null){
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
        }
    }
    private void getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                Toast.makeText(InformationActivity.this, "No location found for the given address", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No location found for the given address");
                progressBar.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting location from address: " + e.getMessage(), e);
        }
    }
    // Helper method to get checkbox ID by name
    private int getCheckboxIdByName(String checkboxName, int[] checkBoxIds) {
        for (int checkBoxId : checkBoxIds) {
            CheckBox checkBox = findViewById(checkBoxId);
            if (checkBox.getText().toString().equals(checkboxName)) {
                return checkBoxId;
            }
        }
        return -1; // Not found
    }
}