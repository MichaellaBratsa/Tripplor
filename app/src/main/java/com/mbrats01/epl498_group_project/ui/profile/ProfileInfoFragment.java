package com.mbrats01.epl498_group_project.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;

public class ProfileInfoFragment extends Fragment {

    private TextView titleProfileInfo, infoFullName, infoUsername, infoEmail, infoPhone;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        ((AppCompatActivity) requireActivity())
                .getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        titleProfileInfo = view.findViewById(R.id.titleProfileInfo);
        infoFullName = view.findViewById(R.id.info_fullName);
        infoUsername = view.findViewById(R.id.info_username);
        infoEmail = view.findViewById(R.id.info_email);
        infoPhone = view.findViewById(R.id.info_phone);

        Button editBtn = view.findViewById(R.id.button_editProfile);
        editBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(ProfileInfoFragment.this)
                        .navigate(R.id.action_profileInfo_to_editProfile)
        );

        db = FirebaseFirestore.getInstance();
        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", null);

        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    String fullName = doc.getString("fullName");
                    String username = doc.getString("username");
                    String email = doc.getString("email");
                    Object phoneObj = doc.get("phoneNumber");

                    if (fullName == null) fullName = "";
                    if (username == null) username = "";
                    if (email == null) email = "";
                    String phone = phoneObj == null ? "" : String.valueOf(phoneObj);

                    titleProfileInfo.setText(username);
                    infoFullName.setText(fullName);
                    infoUsername.setText(username);
                    infoEmail.setText(email);
                    infoPhone.setText(phone);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileInfo_to_profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
