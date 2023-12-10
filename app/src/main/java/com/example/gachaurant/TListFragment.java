package com.example.gachaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter; // Create your custom adapter

    public TListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        restaurantAdapter = new RestaurantAdapter(); // Replace with your custom adapter
        recyclerView.setAdapter(restaurantAdapter);

        // Implement drag-and-drop
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                // Implement logic for moving items in your adapter
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                restaurantAdapter.onItemMove(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle swipe if needed
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}
