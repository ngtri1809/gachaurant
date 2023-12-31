package com.example.gachaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class RewardsActivity extends AppCompatActivity {
    Button backButtonReward;
    public static final String CHECK_IN_COUNT_KEY = "checkInCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        backButtonReward = findViewById(R.id.backBtn6);

        // Set click listeners for each button
        backButtonReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int checkInCount = sharedPreferences.getInt(CHECK_IN_COUNT_KEY, 0);
        Fragment rewardFragment = RewardsFragment.newInstance(checkInCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, rewardFragment);
        fragmentTransaction.commit();
    }
}