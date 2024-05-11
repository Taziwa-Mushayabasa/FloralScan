package com.example.myfloralscanapp;

import android.app.appsearch.SearchResult;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<PlantDetails> plantList;
    private LayoutInflater inflater;
    private Context context;

    // Constructor
    public SearchResultsAdapter(Context context, List<PlantDetails> plantList) {
        this.inflater = LayoutInflater.from(context);
        this.plantList = plantList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_plant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlantDetails plant = plantList.get(position);
        holder.nameTextView.setText(plant.getCommonName());
        holder.sciNameTextView.setText(plant.getScientificName());
        if (!plant.getImageUrl().isEmpty()) {
            Picasso.get().load(plant.getImageUrl()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_flower); // Fallback image
        }
    }

    @Override
    public int getItemCount() {
        Log.d("SearchAdapter", "List size: " + plantList.size());
        return Math.min(plantList.size(), 4);
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView sciNameTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textview_plant_name);
            sciNameTextView = itemView.findViewById(R.id.textview_plant_sci_name);
            imageView = itemView.findViewById(R.id.imageview_plant);
        }
    }

    // Update data method
    public void updateData(List<PlantDetails> newPlants) {
        plantList.clear();
        plantList.addAll(newPlants);
        notifyDataSetChanged();
    }
}