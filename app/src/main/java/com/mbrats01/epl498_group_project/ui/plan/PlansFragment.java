package com.mbrats01.epl498_group_project.ui.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentMapBinding;
import com.mbrats01.epl498_group_project.databinding.FragmentPlansBinding;
import com.mbrats01.epl498_group_project.ui.map.MapFragment;
import com.mbrats01.epl498_group_project.ui.profile.ProfileFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlansFragment extends Fragment {

    private TabLayout tabLayout;
    private TextView textDateHeader, textActivitiesCount;
    private RecyclerView recyclerPlans;
    private PlansAdapter adapter;
    private FirebaseFirestore db;

    private String todayStr;
    private String tomorrowStr;
    private FragmentMapBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentPlansBinding binding =
                FragmentPlansBinding.inflate(inflater, container, false);

        binding.buttonHome.setOnClickListener(v ->
                NavHostFragment.findNavController(PlansFragment.this)
                        .navigate(R.id.action_plans_to_home_page)
        );

        binding.buttonMap.setOnClickListener(v ->
                NavHostFragment.findNavController(PlansFragment.this)
                        .navigate(R.id.action_plans_to_map_page)
        );

        binding.buttonTest.setOnClickListener(v ->
                NavHostFragment.findNavController(PlansFragment.this)
                        .navigate(R.id.action_plans_to_all_attractions)
        );

        binding.buttonProfile.setOnClickListener(v ->
                NavHostFragment.findNavController(PlansFragment.this)
                        .navigate(R.id.action_plans_to_profile)
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        tabLayout = view.findViewById(R.id.tabLayoutPlans);
        textDateHeader = view.findViewById(R.id.textDateHeader);
        textActivitiesCount = view.findViewById(R.id.textActivitiesCount);
        recyclerPlans = view.findViewById(R.id.recyclerPlans);

        adapter = new PlansAdapter();
        recyclerPlans.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerPlans.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        todayStr = sdf.format(new Date());
        tomorrowStr = sdf.format(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));

        setupTabs();
        setupClickListener();

        loadToday();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Today"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) loadToday();
                else loadTomorrow();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    private void setupClickListener() {
        adapter.setOnPlanClickListener(item -> {
            Bundle b = new Bundle();
            b.putString("placeId", (String) item.get("place_id"));
            b.putString("name", (String) item.get("name"));
            b.putString("formatted", (String) item.get("formatted"));
            b.putString("description", (String) item.get("description"));
            b.putString("website", (String) item.get("website"));
            b.putString("openingHours", (String) item.get("opening_hours"));
            b.putString("email", (String) item.get("email"));
            b.putString("phone", (String) item.get("phone"));
            b.putString("imageUrl", (String) item.get("image"));

            ArrayList<String> cats = (ArrayList<String>) item.get("categories");
            if (cats != null) {
                b.putStringArrayList("categories", cats);
            }

            b.putString("selectedTime", (String) item.get("time"));
            b.putString("selectedDate", (String) item.get("date"));

            NavHostFragment.findNavController(PlansFragment.this)
                    .navigate(R.id.action_plansFragment_to_plannedAttractionFragment, b);
        });
    }

    private void loadToday() {
        textDateHeader.setText("Today  " + todayStr);

        db.collection("schedule")
                .whereEqualTo("date", todayStr)
                .whereEqualTo("visited", false)
                .get()
                .addOnSuccessListener(q -> {

                    List<Map<String, Object>> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : q) {
                        Map<String, Object> item = doc.getData();
                        item.put("docId", doc.getId());
                        list.add(item);
                    }

                    sortByTime(list);

                    adapter.setItems(list);
                    textActivitiesCount.setText(list.size() + " activities planned");
                });
    }

    private void loadTomorrow() {
        textDateHeader.setText("Tomorrow  " + tomorrowStr);

        db.collection("schedule")
                .whereEqualTo("date", tomorrowStr)
                .whereEqualTo("visited", false)
                .get()
                .addOnSuccessListener(q -> {

                    List<Map<String, Object>> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : q) {
                        Map<String, Object> item = doc.getData();
                        item.put("docId", doc.getId());
                        list.add(item);
                    }

                    sortByTime(list);

                    adapter.setItems(list);
                    textActivitiesCount.setText(list.size() + " activities planned");
                });
    }


    private void sortByTime(List<Map<String, Object>> list) {
        list.sort((a, b) -> {
            String tA = (String) a.get("time");
            String tB = (String) b.get("time");

            boolean hasA = tA != null && !tA.trim().isEmpty();
            boolean hasB = tB != null && !tB.trim().isEmpty();

            if (!hasA && hasB) return 1;
            if (hasA && !hasB) return -1;
            if (!hasA && !hasB) return 0;

            return tA.compareTo(tB);
        });
    }
}
