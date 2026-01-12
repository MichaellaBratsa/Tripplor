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

public class ResetPasswordFragment extends Fragment {

    private EditText newPass, confirmPass;
    private Button resetBtn;
    private String uid;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        newPass = view.findViewById(R.id.input_newPassword);
        confirmPass = view.findViewById(R.id.input_confirmPassword);
        resetBtn = view.findViewById(R.id.button_resetPassword);

        db = FirebaseFirestore.getInstance();
        uid = getArguments().getString("uid");

        resetBtn.setOnClickListener(v -> resetPassword());

        return view;
    }

    private void resetPassword() {

        String p1 = newPass.getText().toString().trim();
        String p2 = confirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!p1.equals(p2)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(uid)
                .update("password", p1)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Password updated!", Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(ResetPasswordFragment.this)
                            .navigate(R.id.action_resetPassword_to_nav_signIn);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
    }
}
