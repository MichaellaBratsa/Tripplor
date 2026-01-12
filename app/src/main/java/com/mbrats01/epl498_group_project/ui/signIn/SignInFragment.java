package com.mbrats01.epl498_group_project.ui.signIn;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;
    private EditText addUsername, addPassword;
    private String id;
    private double lon, lat;
    private Button buttonSubmit;

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    private FusedLocationProviderClient fusedLocationClient;
    private DocumentSnapshot temp_doc;

    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        addUsername = binding.addUsername;
        addPassword = binding.addPassword;
        buttonSubmit = binding.buttonSubmit;

        // Forgot Password Navigation
        binding.textForgotPassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(SignInFragment.this)
                    .navigate(R.id.action_nav_signIn_to_forgotPassword);
        });

        // Register Navigation
        binding.textRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(SignInFragment.this)
                    .navigate(R.id.action_home_page_to_register);
                });

        buttonSubmit.setOnClickListener(v -> {
            String username = addUsername.getText().toString().trim();
            String password = addPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please enter both username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean userFound = false;

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String db_email = doc.getString("email");
                            String db_username = doc.getString("username");
                            String db_password = doc.getString("password");

                            if ((username.equals(db_username) || username.equals(db_email))
                                    && password.equals(db_password)) {

                                userFound = true;
                                temp_doc = doc;

                                String uid = doc.getId();
                                saveUid(uid);

                                if (ContextCompat.checkSelfPermission(requireContext(),
                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    requestPermissionLauncher.launch(
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                    );

                                } else {
                                    getLastLocation(temp_doc);
                                }

                                break;
                            }

                        }

                        if (!userFound) {
                            Toast.makeText(getContext(),
                                    "Wrong Credentials!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),
                                "Error connecting to database",
                                Toast.LENGTH_SHORT).show();
                    });
        });

        return binding.getRoot();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && temp_doc != null) {
                    getLastLocation(temp_doc);
                } else {
                    Toast.makeText(getContext(),
                            "Location permission is required to use this feature. " +
                                    "Please enable location access in Settings to continue.",
                            Toast.LENGTH_LONG).show();
                }
            });

    private void getLastLocation(DocumentSnapshot doc) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            lon = location.getLongitude();
                            lat = location.getLatitude();

                            usersCollection.document(doc.getId())
                                    .update("lon", lon, "lat", lat)
                                    .addOnCompleteListener(task -> {
                                        NavHostFragment.findNavController(SignInFragment.this)
                                                .navigate(R.id.action_nav_signIn_to_home_page);
                                    });
                        } else {
                            NavHostFragment.findNavController(SignInFragment.this)
                                    .navigate(R.id.action_nav_signIn_to_home_page);
                        }
                    });

        } else {
            Toast.makeText(getContext(),
                    "Location permission is required to use this feature.",
                    Toast.LENGTH_LONG).show();
        }
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
