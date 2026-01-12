package com.mbrats01.epl498_group_project.ui.reviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentReviewsBinding;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsFragment extends Fragment {

    private FragmentReviewsBinding binding;
    private FirebaseFirestore db;

    private ReviewsAdapter adapter;
    private final List<Review> allReviews = new ArrayList<>();

    private String placeId;  // attraction id

    public ReviewsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReviewsBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get placeId from nav args
        if (getArguments() != null) {
            placeId = getArguments().getString("placeId", "");
            Log.d("REVIEWS", "placeId = " + placeId);
        }


        adapter = new ReviewsAdapter();
        binding.recyclerReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerReviews.setAdapter(adapter);

        binding.buttonAddReview.setOnClickListener(v -> openAddReviewDialog());


        setupFilterSpinner();

        loadReviewsFromFirestore();
    }

    private void loadReviewsFromFirestore() {
        if (placeId == null || placeId.isEmpty()) return;

        db.collection("reviews")
                .whereEqualTo("attractionId", placeId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    allReviews.clear();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Review r = doc.toObject(Review.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            allReviews.add(r);
                        }
                    }
                    applyFilter();    // apply current spinner filter
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load reviews", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();// Debugging puposes
                });
    }

    // Spinner filter
    private void setupFilterSpinner() {
        binding.spinnerFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        applyFilter();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                }
        );
    }

    // Apply current filter to list
    private void applyFilter() {
        if (allReviews.isEmpty()) {
            adapter.setItems(new ArrayList<>());
            return;
        }

        int pos = binding.spinnerFilter.getSelectedItemPosition();
        int minRating;

        switch (pos) {
            case 1: minRating = 1; break;
            case 2: minRating = 2; break;
            case 3: minRating = 3; break;
            case 4: minRating = 4; break;
            case 5: minRating = 5; break;
            default: minRating = 0;
        }

        List<Review> filtered = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getRating() >= minRating) {
                filtered.add(r);
            }
        }

        adapter.setItems(filtered);
    }

    // Open dialog to add review
    private void openAddReviewDialog() {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Add Review");

        // inflate custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.add_review, null);
        builder.setView(dialogView);

        EditText editUser = dialogView.findViewById(R.id.editUserName);
        EditText editComment = dialogView.findViewById(R.id.editComment);
        EditText editRating = dialogView.findViewById(R.id.editRating); // simple number 1–5

        builder.setPositiveButton("Save", (dialog, which) -> {

            String user = editUser.getText().toString().trim();
            String comment = editComment.getText().toString().trim();
            String ratingStr = editRating.getText().toString().trim();

            if (user.isEmpty() || comment.isEmpty() || ratingStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Rating must be a number 1-5", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating < 1 || rating > 5) {
                Toast.makeText(getContext(), "Rating must be 1–5", Toast.LENGTH_SHORT).show();
                return;
            }

            saveReviewToFirestore(user, comment, rating);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Save review to Firestore
    private void saveReviewToFirestore(String user, String comment, int rating) {

        if (placeId == null || placeId.isEmpty()) {
            Toast.makeText(getContext(), "Missing attractionId", Toast.LENGTH_SHORT).show();
            return;
        }

        long now = System.currentTimeMillis();

        Map<String, Object> review = new HashMap<>();
        review.put("attractionId", placeId);
        review.put("userName", user);
        review.put("comment", comment);
        review.put("rating", rating);
        review.put("createdAt", now);

        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(getContext(), "Review added!", Toast.LENGTH_SHORT).show();
                    loadReviewsFromFirestore(); // refresh list
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_SHORT).show()
                );
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
