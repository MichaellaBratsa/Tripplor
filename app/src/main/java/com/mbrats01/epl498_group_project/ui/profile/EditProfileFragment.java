package com.mbrats01.epl498_group_project.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;

public class EditProfileFragment extends Fragment {

    private EditText editFullName, editUsername, editPhone;
    private Button saveButton;
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

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ((AppCompatActivity) requireActivity())
                .getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        editFullName = view.findViewById(R.id.edit_fullName);
        editUsername = view.findViewById(R.id.edit_username);
        editPhone = view.findViewById(R.id.edit_phoneNumber);
        saveButton = view.findViewById(R.id.button_save_changes);

        db = FirebaseFirestore.getInstance();

        loadCurrentValues();
        configureSaveButton();

        return view;
    }

    private void loadCurrentValues() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", null);

        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    String fullName = doc.getString("fullName");
                    String username = doc.getString("username");
                    Object phoneObj = doc.get("phoneNumber");

                    if (fullName != null) editFullName.setText(fullName);
                    if (username != null) editUsername.setText(username);
                    if (phoneObj != null) editPhone.setText(String.valueOf(phoneObj));
                });
    }

    private void configureSaveButton() {

        saveButton.setOnClickListener(v -> {

            String newFullName = editFullName.getText().toString().trim();
            String newUsername = editUsername.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String uid = prefs.getString("uid", null);

            if (uid == null) return;

            db.collection("users")
                    .document(uid)
                    .update(
                            "fullName", newFullName,
                            "username", newUsername,
                            "phoneNumber", newPhone
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();

                        NavHostFragment.findNavController(EditProfileFragment.this)
                                .navigate(R.id.action_editProfile_to_profileInfo);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(EditProfileFragment.this)
                    .navigate(R.id.action_editProfile_to_profileInfo);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
