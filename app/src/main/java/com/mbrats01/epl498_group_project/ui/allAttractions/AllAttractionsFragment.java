package com.mbrats01.epl498_group_project.ui.allAttractions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentAllAttractionsBinding;
import com.mbrats01.epl498_group_project.databinding.FragmentHomeBinding;
import com.mbrats01.epl498_group_project.ui.home.HomeFragment;
import com.mbrats01.epl498_group_project.ui.home.TopAttractionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllAttractionsFragment extends Fragment {

    private FirebaseFirestore db;
    private FragmentAllAttractionsBinding binding;
    private TopAttractionAdapter adapter;

    private final List<String> categories = new ArrayList<>();
    private final List<Attraction> allAttractions = new ArrayList<>();
    private final List<Attraction> filteredAttractions = new ArrayList<>();

    ArrayAdapter<String> spinnerAdapter;

    public AllAttractionsFragment() {}

    @Override
    // Inflate the layout for this fragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAllAttractionsBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        adapter = new TopAttractionAdapter(attraction -> {

            Bundle bundle = new Bundle();
            bundle.putString("placeId", attraction.getPlaceId());
            bundle.putString("name", attraction.getName());
            bundle.putString("formatted", attraction.getFormatted());
            bundle.putString("description", attraction.getDescription());
            bundle.putString("website", attraction.getWebsite());
            bundle.putString("openingHours", attraction.getOpeningHours());
            bundle.putStringArray(
                    "categories",
                    attraction.getCategories().toArray(new String[0])
            );

            bundle.putString("email", attraction.getEmail());
            bundle.putString("phone", attraction.getPhone());
            bundle.putString("imageUrl", attraction.getImageUrl());

            NavHostFragment.findNavController(AllAttractionsFragment.this)
                    .navigate(R.id.action_all_attractions_to_attractionDetailFragment, bundle);
        });

        // Button to go to map page
        binding.buttonMap.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllAttractionsFragment.this)
                    .navigate(R.id.action_all_to_map_page);
        });

        // Button to go to home page
        binding.buttonHome.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllAttractionsFragment.this)
                    .navigate(R.id.action_all_to_home_page);
        });

        // Button to go to Profile
        binding.buttonProfile.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllAttractionsFragment.this)
                    .navigate(R.id.action_all_to_profile);
        });

        //Button to go to Plans
        binding.buttonPlans.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllAttractionsFragment.this)
                    .navigate(R.id.action_all_to_plansFragment);
        });

        binding.containerAttractions.setAdapter(adapter);
        binding.containerAttractions.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        binding.spinnerCategories.setAdapter(spinnerAdapter);

        binding.spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Filter attractions based on selected category
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = categories.get(position);

                filteredAttractions.clear();

                if (selected.equals("All")) {
                    filteredAttractions.addAll(allAttractions);
                } else {
                    for (Attraction a : allAttractions) {
                        if (a.getCategories() != null &&
                                !a.getCategories().isEmpty() &&
                                a.getCategories().get(0).equals(selected)) {
                            filteredAttractions.add(a);
                        }
                    }
                }

                adapter.setItems(filteredAttractions);
            }

            @Override
            // Nothing selected
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadAttractions();

        return binding.getRoot();
    }

    // Load all attractions from db
    private void loadAttractions() {

        Source source = Source.DEFAULT;

        db.collection("attraction_sites")
                .get(source)
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Toast.makeText(getContext(), "No attractions found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allAttractions.clear();
                    filteredAttractions.clear();

                    Set<String> categorySet = new HashSet<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Attraction a = doc.toObject(Attraction.class);

                        if (a != null && a.getName() != null && !a.getName().isEmpty()) {

                            String image = doc.getString("image");
                            a.setImageUrl(image);
                            List<String> catList = (List<String>) doc.get("categories");

                            if (catList != null && !catList.isEmpty()) {
                                String first = catList.get(0);
                                a.setCategories(Collections.singletonList(first));
                                categorySet.add(first);

                                Log.d("DEBUG_CAT", "Name=" + a.getName() + " | cat0=" + first);
                            }

                            allAttractions.add(a);
                        }
                    }

                    categories.clear();
                    categories.add("All");
                    categories.addAll(categorySet);

                    Log.d("DEBUG_CATEGORY", "Loaded categories=" + categories);

                    filteredAttractions.addAll(allAttractions);

                    adapter.setItems(filteredAttractions);
                    spinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load attractions.", Toast.LENGTH_SHORT).show()
                );
    }
}