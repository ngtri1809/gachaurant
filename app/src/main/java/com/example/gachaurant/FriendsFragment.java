package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private ImageButton searchFriendsBtn;
    private TableLayout friendsTable;
    private List<String> friendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        searchFriendsBtn = view.findViewById(R.id.searchfriends);
        friendsTable = view.findViewById(R.id.friendsTable);

        // Initialize friend list
        friendList = new ArrayList<>();

        // Set click listener for the searchFriendsBtn
        searchFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), SearchFriends.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Method to add friend to the table
    public void addFriendToTable(String email) {
        // Check if the friend is not already in the list
        if (!friendList.contains(email)) {
            // Add friend to the list
            friendList.add(email);

            // Add friend to the table
            TableRow row = new TableRow(requireContext());
            TextView friendTextView = new TextView(requireContext());
            friendTextView.setText(email);
            row.addView(friendTextView);
            friendsTable.addView(row);
        }
    }
}
