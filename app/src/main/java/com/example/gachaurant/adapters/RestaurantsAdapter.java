package com.example.gachaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gachaurant.R;
import com.example.gachaurant.restaurantPackage.Restaurant;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>{

    private Context context;
    private List<Restaurant> restaurants;
    private RestaurantClickedCallback restaurantClickedCallback;

    public RestaurantsAdapter(Context context, List<Restaurant> restaurants, RestaurantClickedCallback restaurantClickedCallback) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurantClickedCallback = restaurantClickedCallback;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getAddress());
        holder.ratingTextView.setText(context.getString(R.string.rating, restaurant.getRating()));
        final int pos = position;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantClickedCallback.restaurantClicked(restaurant, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView ratingTextView;
        View container;
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            container = itemView.findViewById(R.id.container);
        }
    }
}
