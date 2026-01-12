package com.mbrats01.epl498_group_project.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {

    public interface OnPlanClickListener {
        void onPlanClick(Map<String, Object> item);
    }

    private List<Map<String, Object>> items = new ArrayList<>();
    private OnPlanClickListener listener;

    public void setItems(List<Map<String, Object>> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    public void setOnPlanClickListener(OnPlanClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlansAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_plan_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlansAdapter.ViewHolder h, int pos) {
        Map<String, Object> item = items.get(pos);

        String name = (String) item.get("name");
        String time = (String) item.get("time");
        String image = (String) item.get("image");

        h.planName.setText(name);

        if (time != null && !time.trim().isEmpty()) {
            h.planTime.setText("Planned for: " + time);
        } else {
            h.planTime.setText("No time selected");
        }
        h.planTime.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(h.planImage);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlanClick(item);
        });

        h.checkVisited.setOnCheckedChangeListener(null);
        h.checkVisited.setChecked(false);

        h.checkVisited.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                String docId = (String) item.get("docId");

                FirebaseFirestore.getInstance()
                        .collection("schedule")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener(r -> {
                            items.remove(item);
                            notifyDataSetChanged();
                        });
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView planImage;
        TextView planName, planTime;
        CheckBox checkVisited;

        ViewHolder(@NonNull View v) {
            super(v);
            planImage = v.findViewById(R.id.planImage);
            planName = v.findViewById(R.id.planName);
            planTime = v.findViewById(R.id.planTime);
            checkVisited = v.findViewById(R.id.checkVisited);
        }
    }
}
