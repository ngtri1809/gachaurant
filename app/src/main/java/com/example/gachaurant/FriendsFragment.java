package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendsFragment extends Fragment {
    private ImageButton searchFriendsBtn;
    private TableLayout friendsTable;
    private List<String> friendList;
    private TextView friendCountTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        searchFriendsBtn = view.findViewById(R.id.searchfriends);
        friendsTable = view.findViewById(R.id.friendsTable);
        friendCountTextView = view.findViewById(R.id.friendCountTextView);

        // Initialize friend list
        friendList = Collections.synchronizedList(new ArrayList<>());

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
        Log.d("SearchFriends", "addFriendToTable: Adding friend - " + email);
        if (friendsTable != null && friendList != null) {
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

                // Update the friend count in the header
                updateFriendCount();
            }
        }
    }

    private void updateUIOnMainThread() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Perform UI-related tasks here
                    // This code will run on the main (UI) thread
                    // For example, updating a TextView
                    TextView myTextView = getActivity().findViewById(R.id.friendCountTextView);
                    myTextView.setText("Updated on UI thread");
                }
            });
        }
    }

    // Method to update the friend count in the header
    private void updateFriendCount() {
        Log.d("FriendsFragment", "updateFriendCount: Called");
        if (friendList != null && friendCountTextView != null) {
            if (!friendList.isEmpty()) {
                // If there are friends, display the first email in the list
                String firstFriendEmail = friendList.get(0);
                requireActivity().runOnUiThread(() -> {
                    friendCountTextView.setText("Friend: " + firstFriendEmail);
                    Log.d("FriendsFragment", "updateFriendCount: Friend email updated - " + firstFriendEmail);
                });
            } else {
                // If the friendList is empty, display a default message
                requireActivity().runOnUiThread(() -> {
                    friendCountTextView.setText("No Friends");
                    Log.d("FriendsFragment", "updateFriendCount: No Friends");
                });
            }
        }
    }
}