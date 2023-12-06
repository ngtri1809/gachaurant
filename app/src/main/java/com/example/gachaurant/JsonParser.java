package com.example.gachaurant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String, String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");
            String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //Add data to data list
                dataList.add(data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return dataList;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return parseJsonArray(jsonArray);
    }
}
//    private class PlaceTask extends AsyncTask<String, Integer, String> {
//        String data = null;
//        @Override
//        protected String doInBackground(String... strings) {
//
//            try {
//                Log.d("GetNearbyPlacesData", "doInBackground entered");
//                data = downloadUrl(strings[0]);
//            } catch (IOException e) {
//                Log.d("ErrorNearby ", e.toString());
//            }
//            Log.d("PlaceTask", data);
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            //Execute parser task
//            new ParserTask().execute(s);
//            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
//        }
//    }
//private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//        // The radius of the Earth in meters
//        final double R = 6371000; // Earth radius in meters
//
//        // Convert latitude and longitude from degrees to radians
//        double lat1Rad = Math.toRadians(lat1);
//        double lon1Rad = Math.toRadians(lon1);
//        double lat2Rad = Math.toRadians(lat2);
//        double lon2Rad = Math.toRadians(lon2);
//
//        // Calculate the differences
//        double deltaLat = lat2Rad - lat1Rad;
//        double deltaLon = lon2Rad - lon1Rad;
//
//        // Haversine formula
//        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
//                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
//                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
//
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        // Calculate the distance
//        return R * c;
//    }
//
//    private void fetchNearbyRestaurants(double latitude, double longitude) {
//        //Init restaurant List
//
//        // Set up the Places API client
//        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
//        PlacesClient placesClient = Places.createClient(this);
//
//        // Create a request for nearby places (restaurants)
//        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Collections.singletonList(Place.Field.NAME));
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            askPermission();
//            return;
//        }
//        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//        // Handle the response
//        placeResponse.addOnSuccessListener(response -> {
//            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                Place place = placeLikelihood.getPlace();
//                double placeLatitude;
//                double placeLongitude;
//
//                if (place.getLatLng() != null) {
//                    placeLatitude = place.getLatLng().latitude;
//                    placeLongitude = place.getLatLng().longitude;
//                    Log.i("Place", String.format("Place '%s' has likelihood: %f", place.getName(), placeLikelihood.getLikelihood()));
//                } else {
//                    // Handle the case where the place doesn't have a defined latitude and longitude
//                    Log.w("Place", String.format("Place '%s' doesn't have a defined latitude and longitude.", place.getName()));
//                    continue;  // Skip to the next place
//                }
//
//            }
//            Log.d(TAG, "Nearby Restaurants: " + restaurantList);
//        });
//        placeResponse.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("Places API", "Error getting place likelihoods: " + e.getMessage());
//            }
//        });
//    }