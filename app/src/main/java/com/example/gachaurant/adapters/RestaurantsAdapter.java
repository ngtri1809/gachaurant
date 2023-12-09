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

    public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
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
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView ratingTextView;
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
}
