package com.example.gachaurant;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchFriends extends AppCompatActivity {
    private EditText searchEditText;
    private TextView resultTextView;
    private Button backButton;
    private Button addButton; // Added button reference
    private FirebaseFirestore db;
    private String selectedEmail;
    private FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_searchfriends);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        resultTextView = findViewById(R.id.selectedUsernameTextView);
        backButton = findViewById(R.id.backBtn3);
        addButton = findViewById(R.id.addButton);

        // Initialize FriendsFragment
        friendsFragment = new FriendsFragment();

        // Set up TextWatcher for real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Perform search when text changes
                searchUsers(editable.toString());
            }
        });

        // Set click listener for the "Add" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the addition of the selected email to your friend list
                if (selectedEmail != null) {
                    friendsFragment.addFriendToTable(selectedEmail);
                }
            }
        });

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void searchUsers(String query) {
        Log.d("Search Query", "Query: " + query);

        // Clear previous search results
        resultTextView.setText("");

        // Query Firestore for users with usernames containing the query
        CollectionReference usersCollection = db.collection("users");

        // Convert both the stored usernames and the query to lowercase for case-insensitive comparison
        usersCollection.whereEqualTo("email", query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Firestore Query", "Query successful: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            List<String> searchResults = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract the username from the document
                                String username = document.getString("userName");
                                searchResults.add(username);
                            }

                            // Update the UI with search results
                            updateSearchResults(searchResults);
                        } else {
                            Log.e("Firestore Query", "Error: " + task.getException().getMessage());
                            resultTextView.setText("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateSearchResults(List<String> searchResults) {
        // Display search results in the resultTextView
        if (searchResults.isEmpty()) {
            resultTextView.setText("No matching users found.");
            selectedEmail = null; // Reset selectedUsername if no results
        } else {
            StringBuilder resultText = new StringBuilder("Matching users found by email:\n");
            for (String username : searchResults) {
                resultText.append(username).append("\n");
            }
            resultText.append("Select a user to add as a friend.");

            // Set the first matching username as the selectedUsername (you can modify this logic as needed)
            selectedEmail = searchResults.get(0);

            // Update the TextView under the search bar with the selected username
            displaySelectedEmail();

            // Display the message in resultTextView
            resultTextView.setText(resultText.toString());
        }
    }

    // Add this method to update the TextView under the search bar with the selected username
    private void displaySelectedEmail() {
        TextView selectedUsernameTextView = findViewById(R.id.selectedUsernameTextView);

        if (selectedEmail != null) {
            selectedUsernameTextView.setText("Selected email: " + selectedEmail);
        } else {
            selectedUsernameTextView.setText(""); // Clear the TextView if no selected email
        }
    }
}
