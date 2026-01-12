package com.mbrats01.epl498_group_project.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.ui.allAttractions.Attraction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TopAttractionAdapter extends RecyclerView.Adapter<TopAttractionAdapter.ViewHolder> {

    private final List<Attraction> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public void setData(List<Attraction> list) {

    }

    public interface OnItemClickListener {
        void onItemClick(Attraction attraction);
    }


    public TopAttractionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void TopAttractionAdapter()
    {

    }

    public void setItems(List<Attraction> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_attraction_element, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction a = items.get(position);

        holder.textName.setText(a.getName());

        if (a.getCategories() != null && !a.getCategories().isEmpty()) {
            holder.textCategory.setVisibility(View.VISIBLE);
            holder.textCategory.setText(a.getCategories().get(0));
        } else {
            holder.textCategory.setVisibility(View.GONE);
        }

        String imageUrl = a.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imageAttraction);
        } else {
            holder.imageAttraction.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAttraction;
        TextView textName;
        TextView textCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAttraction = itemView.findViewById(R.id.imageAttraction);
            textName = itemView.findViewById(R.id.textName);
            textCategory = itemView.findViewById(R.id.textCategory);
        }
    }
}
