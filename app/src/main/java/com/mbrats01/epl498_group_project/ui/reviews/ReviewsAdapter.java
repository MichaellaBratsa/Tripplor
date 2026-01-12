package com.mbrats01.epl498_group_project.ui.reviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbrats01.epl498_group_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final List<Review> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public void setItems(List<Review> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review r = items.get(position);

        holder.textUserName.setText(r.getUserName());
        holder.textComment.setText(r.getComment());

        // ⭐ Stars: filled + empty using Unicode
        int rating = r.getRating();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) sb.append("★");
        for (int i = rating; i < 5; i++) sb.append("☆");
        holder.textStars.setText(sb.toString());

        // Date
        String dateStr = dateFormat.format(new Date(r.getCreatedAt()));
        holder.textDate.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textStars, textComment, textDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textStars = itemView.findViewById(R.id.textStars);
            textComment = itemView.findViewById(R.id.textComment);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}
