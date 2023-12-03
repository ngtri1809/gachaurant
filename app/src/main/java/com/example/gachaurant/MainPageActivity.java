package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.example.gachaurant.databinding.ActivityMainBinding;

import com.example.gachaurant.databinding.ActivityMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;


public class MainPageActivity extends AppCompatActivity {

    ActivityMainPageBinding binding;
    //ImageButton profileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.friends) {
                replaceFragment(new FriendsFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }else if (itemId == R.id.list) {
                replaceFragment(new ListFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            }

            return true;
        });

        //setContentView(R.layout.activity_main_page);
        //Initialize
       //profileButton = findViewById(R.id.profileButton);

       /** profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
            }
        });
        **/
        //Go to User Profile
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}