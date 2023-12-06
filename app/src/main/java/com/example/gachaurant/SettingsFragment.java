package com.example.gachaurant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {

    Button accountDetailsButton;
    Button changePasswordButton;
    Button preferencesButton;
    Button groupSettingButton;
    Button logOutBtn;
    FirebaseAuth fAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Find the buttons by their IDs in the fragment's view
        accountDetailsButton = view.findViewById(R.id.accountBtn);
        changePasswordButton = view.findViewById(R.id.changepwBtn);
        preferencesButton = view.findViewById(R.id.preferenceBtn);
        groupSettingButton = view.findViewById(R.id.groupBtn);
        logOutBtn = view.findViewById(R.id.logoutbtn);

        // Set click listeners for each button
        accountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for "My Account" button within the fragment
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for "My Account" button within the fragment
                Intent intent = new Intent(getActivity(), InformationActivity.class);
                startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for "My Account" button within the fragment
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });

            fAuth = FirebaseAuth.getInstance();

            logOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fAuth.signOut();
                    Toast.makeText(getContext(), "User Logged Out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            });

        return view;
    }

}
