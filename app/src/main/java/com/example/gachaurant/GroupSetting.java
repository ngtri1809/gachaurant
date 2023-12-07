package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class GroupSetting extends AppCompatActivity {
    Button bkButton;
    Button createGroupBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_groupsetting);

        createGroupBtn = findViewById(R.id.createGroup);
        bkButton = findViewById(R.id.backBtn1);

        // Set click listeners for each button
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(GroupSetting.this, CreateGroup.class);
                startActivity(intent);
            }
        });

        bkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                }
        });


    }
}