package com.mbrats01.epl498_group_project.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentProfileBinding;
import com.mbrats01.epl498_group_project.ui.home.HomeFragment;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        ((AppCompatActivity) requireActivity())
                .getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String uid = prefs.getString("uid", null);

        if (uid != null) {
            loadUserData(uid);
        }

        binding.buttonHome.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_profile_to_home_page);
        });

        binding.buttonTest.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_profile_to_all_attractions);
        });

        // Button to go to map page -> tha allaxei
        binding.buttonMap.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_profile_to_map_page);
        });

        //Button to go to Plans -> tha allaxei
        binding.buttonPlans.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_profile_to_plansFragment);
        });

        binding.buttonProfileInfo.setOnClickListener(v ->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profile_to_profileInfo)
        );

        binding.buttonAbout.setOnClickListener(v ->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profile_to_about)
        );

        binding.textLogOut.setOnClickListener(v -> showLogoutDialog());

        return binding.getRoot();
    }

    private void loadUserData(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!isAdded() || binding == null) return;  // 🔥 FIX

                    if (doc.exists()) {
                        String username = doc.getString("username");
                        binding.textUsername.setText(username);
                    }
                });
    }


    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {

                    SharedPreferences prefs = requireActivity()
                            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

                    prefs.edit().clear().apply();

                    NavHostFragment.findNavController(ProfileFragment.this)
                            .navigate(R.id.action_profile_to_signIn);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this).navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
