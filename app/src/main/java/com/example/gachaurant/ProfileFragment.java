package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfileFragment extends Fragment {

    Button checkInBtn, inventoryBtn, rewardsBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Find the buttons by their IDs in the fragment's view
        checkInBtn = view.findViewById(R.id.checkin);
        inventoryBtn = view.findViewById(R.id.restaurantInventory);
        rewardsBtn = view.findViewById(R.id.rewards);
        // Set click listeners for each button
        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for "My Account" button within the fragment
                Intent intent = new Intent(getActivity(), CheckIn.class);
                startActivity(intent);
            }
        });
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for "My Account" button within the fragment
                Intent intent = new Intent(getActivity(), RestaurantInventory.class);
                startActivity(intent);
            }
        });

        rewardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RewardsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}