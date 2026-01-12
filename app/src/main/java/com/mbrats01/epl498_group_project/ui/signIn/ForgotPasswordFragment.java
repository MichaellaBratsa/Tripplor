package com.mbrats01.epl498_group_project.ui.signIn;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;

public class ForgotPasswordFragment extends Fragment {

    private EditText inputIdentifier;
    private Button nextBtn;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        inputIdentifier = view.findViewById(R.id.input_identifier);
        nextBtn = view.findViewById(R.id.button_next_step);
        db = FirebaseFirestore.getInstance();

        nextBtn.setOnClickListener(v -> searchUser());

        return view;
    }

    private void searchUser() {

        String identifier = inputIdentifier.getText().toString().trim();

        if (TextUtils.isEmpty(identifier)) {
            Toast.makeText(getContext(), "Please enter an identifier", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("email", identifier)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        String uid = snap.getDocuments().get(0).getId();
                        goToReset(uid);
                    } else {
//                        searchByUsername(identifier);
                        searchByPhone(identifier);
                    }
                });
    }

//    private void searchByUsername(String identifier) {
//        db.collection("users")
//                .whereEqualTo("username", identifier)
//                .get()
//                .addOnSuccessListener(snap -> {
//                    if (!snap.isEmpty()) {
//                        String uid = snap.getDocuments().get(0).getId();
//                        goToReset(uid);
//                    } else {
//                        searchByPhone(identifier);
//                    }
//                });
//    }

    private void searchByPhone(String identifier) {
        int phone;
        try {
            phone = Integer.parseInt(identifier);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Email or Phone is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("phoneNumber", phone)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        String uid = snap.getDocuments().get(0).getId();
                        goToReset(uid);
                    } else {
                        Toast.makeText(getContext(), "Email or Phone is invalid", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToReset(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);

        Toast.makeText(getContext(), "Email/SMS sent! Check your inbox.", Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(ForgotPasswordFragment.this)
                .navigate(R.id.action_forgotPassword_to_resetPassword, bundle);
    }
}
