package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gachaurant.restaurantPackage.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment {

    Button checkInBtn, inventoryBtn, rewardsBtn;
    long level = 1;
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


    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData(){
        TextView levelView = getView().findViewById(R.id.level);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot value = task.getResult();
                    if(value.get("level")!=null){
                        level = (long) value.get("level");
                    }
                    levelView.setText("Level: "+level);
                }
            }
        });
    }
}