package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.gachaurant.SearchFriends;

public class FriendsFragment extends Fragment {
    ImageButton searchFriendsBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        searchFriendsBtn = view.findViewById(R.id.searchfriends);

        // Set click listener for the searchFriendsBtn
        searchFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event, e.g., navigate to a search activity
                Intent intent = new Intent(getActivity(), SearchFriends.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
