package com.mbrats01.epl498_group_project.ui.register;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentRegisterBinding;
import com.mbrats01.epl498_group_project.ui.signIn.User;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private EditText addFullName, addPhone, addEmail, addUsername, addPassword, addConfirmPassword;

    private FirebaseFirestore db;

    public RegisterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        addFullName = binding.addFullName;
        addEmail = binding.addEmail;
        addPhone = binding.addPhone;
        addUsername = binding.addUsername;
        addPassword = binding.addPassword;
        addConfirmPassword = binding.addConfirmPassword;

        binding.buttonSave.setOnClickListener(v -> saveUser());

        return binding.getRoot();
    }

    private void saveUser() {
        String fullName = addFullName.getText().toString().trim();
        String email = addEmail.getText().toString().trim();
        String phone = addPhone.getText().toString().trim();
        String username = addUsername.getText().toString().trim();
        String password = addPassword.getText().toString().trim();
        String confirmPassword = addConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create random UID
        String uid = db.collection("users").document().getId();

        User newUser = new User(fullName, username, email, password, phone, uid);

        // Save user to Firestore
        db.collection("users")
                .document(uid)
                .set(newUser)
                .addOnSuccessListener(unused -> {

                    saveUid(uid);

                    Toast.makeText(requireContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_nav_register_to_home_page);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error: info was not saved...",
                                Toast.LENGTH_LONG).show()
                );
    }

    private void saveUid(String uid) {
        requireActivity()
                .getSharedPreferences("user_prefs", requireContext().MODE_PRIVATE)
                .edit()
                .putString("uid", uid)
                .apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
