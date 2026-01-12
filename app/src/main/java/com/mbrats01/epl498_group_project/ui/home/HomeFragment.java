package com.mbrats01.epl498_group_project.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mbrats01.epl498_group_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.databinding.FragmentHomeBinding;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mbrats01.epl498_group_project.ui.allAttractions.Attraction;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment_Geoapify";
    // Replace with your API Key
    private static final String GEOAPIFY_API_KEY = "";

    private TopAttractionAdapter topAdapter;
    private final List<Attraction> allAttractions = new ArrayList<>();

    // Cronet
    private CronetEngine cronetEngine;
    private Executor executor;

    // Location
    private FusedLocationProviderClient fusedClient;
    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // Firebase
    private FirebaseFirestore db;
    private int counter = 0;

    private static final List<String> fallbackImages = Arrays.asList(
            "https://www.myhelsinki.fi/wp-content/uploads/2024/09/hotel_st._george_wintergardenchotel_st._george-scaled.jpg",
            "https://asset.roof.fi/2025/10/474866-kivi-1760518752576-15217.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFtzuqvqpqZgFfcQjjzxA-vKmNIYHmfmjZrQ&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQw4c7Yn2N-dJ9IwmWkJt5_7qnq6yOzG_IruA&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6l_JGAFipghpnfl3SkI1JGEz3OPjfiW2ifA&s",
            "https://www.discoveringfinland.com/wp-content/uploads/2010/08/Sea-Life-Helsinki4.jpg",
            "https://www.discoveringfinland.com/wp-content/uploads/2011/10/Luomus_kumpulan-kasvitieteellinen-puutarha_puro_Salla-Mehta%CC%88la%CC%88.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSVRNTNmm9m_uvHvIOQV7B108WAPqbrVE7Kew&s",
            "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2f/19/54/e6/you-can-organize-a-golf.jpg?w=1200&h=-1&s=1",
            "https://www.kumpulanspy.fi/wp-content/uploads/2025/08/Museomokki_vaaka-scaled.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDNkywYHf0ZBMsG7TLeezW0ICIzA_cd7sxYw&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6l_JGAFipghpnfl3SkI1JGEz3OPjfiW2ifA&s",
            "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/31/57/60/d3/the-carnival-of-light.jpg?w=900&h=500&s=1",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaCreDP7incGKgmJV8OR-Tj5yTjkFP3gVqTw&s",
            "https://www.myhelsinki.fi/nitropack_static/NPKOrqSOTiHCxREDGKxzPsCxOlwxDjYB/assets/images/optimized/rev-0d5358c/www.myhelsinki.fi/wp-content/uploads/2024/09/vallilan_siirtolapuutarha_img_0183_c_seppo_laakso_helsingin_kaupungin_aineistopankki-scaled.jpg",
            "https://www.linnanmaki.fi/wp-content/uploads/2025/04/linnanmaki-peacock-1.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcUiTOxQCT14F2djhrk5gV1TsvhBjTpawbNjo2y3grbqUWAvTQpQajrtFHwk4LZrMO_DQ&usqp=CAU",
            "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/08/2a/22/eb/finnish-national-theater.jpg?w=1200&h=-1&s=1",
            "https://www.alvaraalto.fi/wp-content/uploads/2018/03/Helsinki-kulttuuritalo-katujulkisivu-kuva-maija-holma-%C2%A9-alvar-aalto-sa%CC%88a%CC%88tio%CC%88-1.jpg",
            "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/09/2e/dd/c4/entrada-do-museum-of.jpg?w=1200&h=-1&s=1"
    );

    private FragmentHomeBinding binding;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean granted = false;
                        for (Boolean b : result.values()) {
                            if (b != null && b) {
                                granted = true;
                                break;
                            }
                        }
                        if (granted) {
                            getUserLocation();
                        } else {
                            Log.i(TAG, "Location permission denied");
                        }
                    }
            );

    public HomeFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cronet init
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(requireContext());
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();

        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Firebase init
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Button to go to all attractions list -> tha allaxei
        binding.buttonTest.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_page_to_all_attractions);
        });

        // Button to go to map page -> tha allaxei
        binding.buttonMap.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_page_to_map_page);
        });

        // Button to go to Profile
        binding.buttonProfile.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_page_to_profile);
        });

        //Button to go to Plans -> tha allaxei
        binding.buttonPlans.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_page_to_plansFragment);
        });


        // Receives top 4 attraction entries to fake top attractions
        topAdapter = new TopAttractionAdapter(attraction -> {
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

            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_page_to_attractionDetailFragment, bundle);
        });

        binding.recyclerTopAttractions.setAdapter(topAdapter);
        binding.recyclerTopAttractions.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(
                        requireContext(),
                        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                        false
                )
        );

        binding.progressTop.setVisibility(View.VISIBLE);
        binding.recyclerTopAttractions.setVisibility(View.GONE);

//        loadTopAttractions();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestLocation();  // start the chain
    }

    // Location

    private void requestLocation() {
        boolean fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {
            locationPermissionLauncher.launch(LOCATION_PERMISSIONS);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {

        boolean fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {
            Log.i(TAG, "No location permission");
            return;
        }

        fusedClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                Log.e(TAG, " REAL GPS LOCATION: lat=" + lat + " lon=" + lon);

                saveLocationToPrefs(lat, lon);
                callGeoapifyPlacesApi(lat, lon);
            } else {
                Log.e(TAG, " CurrentLocation returned null! Trying lastLocation...");

                // fallback if emulator fails
                fusedClient.getLastLocation()
                        .addOnSuccessListener(lastLoc -> {
                            if (lastLoc != null) {
                                double lat = lastLoc.getLatitude();
                                double lon = lastLoc.getLongitude();

                                Log.e(TAG, " FALLBACK lastLocation: lat=" + lat + " lon=" + lon);

                                saveLocationToPrefs(lat, lon);
                                callGeoapifyPlacesApi(lat, lon);
                            } else {
                                Log.e(TAG, " No location available from emulator");
                            }
                        });
            }
        });
    }

    private void saveLocationToPrefs(double lat, double lon) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("location_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putFloat("lat", (float) lat)
                .putFloat("lon", (float) lon)
                .apply();
    }

    // api call
    private void callGeoapifyPlacesApi(double lat, double lon) {
        if (cronetEngine == null) {
            Log.e(TAG, "CronetEngine is null");
            return;
        }

        if (db == null) return;

        // Delete previous data
        db.collection("attraction_sites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_DELETE", "Error deleting attractions: " + e.getMessage());
                });

        String url = "https://api.geoapify.com/v2/places"
                + "?categories=entertainment"
                + "&filter=circle:" + lon + "," + lat + ",5000"
                + "&bias=proximity:" + lon + "," + lat
                + "&limit=20"
                + "&apiKey=" + GEOAPIFY_API_KEY;

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url,
                new GeoapifyPlacesCallback(),
                executor
        );

        UrlRequest request = requestBuilder.build();
        request.start();
    }

    private class GeoapifyPlacesCallback extends UrlRequest.Callback {
        private static final String CB_TAG = "GeoapifyCallback";
        private final StringBuilder responseBuilder = new StringBuilder();

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(CB_TAG, "onResponseStarted");
            request.read(ByteBuffer.allocateDirect(102400));
        }

        @Override
        public void onReadCompleted(UrlRequest request,
                                    UrlResponseInfo info,
                                    ByteBuffer byteBuffer) {
            Log.i(CB_TAG, "onReadCompleted");
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            responseBuilder.append(new String(bytes));

            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(CB_TAG, "onSucceeded");
            String response = responseBuilder.toString();
            Log.i(TAG, "Geoapify response: " + response);

            parseGeoapifyResponse(response);
        }

        @Override
        public void onFailed(UrlRequest request,
                             UrlResponseInfo info,
                             CronetException error) {
            Log.e(CB_TAG, "onFailed: " + error.getMessage());
        }

        @Override
        public void onRedirectReceived(UrlRequest request,
                                       UrlResponseInfo info,
                                       String newLocationUrl) {
            request.followRedirect();
        }
    }

    // parse and save in firestore
    private void parseGeoapifyResponse(String json) {

        if (db == null) {
            Log.e(TAG, "Firestore db is null");
            hideLoading();
            return;
        }

        allAttractions.clear();

        try {
            JSONObject obj = new JSONObject(json);

            // No "features" → stop + hide loading
            if (!obj.has("features")) {
                Log.i(TAG, "No 'features' array in Geoapify response");
                hideLoading();
                return;
            }

            JSONArray features = obj.getJSONArray("features");
            Log.i(TAG, "Geoapify returned " + features.length() + " places");

            // Empty results → stop + hide loading
            if (features.length() == 0) {
                hideLoading();
                return;
            }

            // Track how many attractions have been saved
            final int totalAttractions = features.length();
            final int[] savedCount = {0};

            // Parse attractions
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject props = feature.optJSONObject("properties");
                if (props == null) continue;

                String placeId = props.optString("place_id", "");
                String name = props.optString("name", "");
                String formatted = props.optString("formatted", "");
                double lat = props.optDouble("lat", Double.NaN);
                double lon = props.optDouble("lon", Double.NaN);

                String description = props.optString("description", "");
                String website = props.optString("website", "");
                String openingHours = props.optString("opening_hours", "");

                // categories[]
                List<String> categories = new ArrayList<>();
                JSONArray catArray = props.optJSONArray("categories");
                if (catArray != null) {
                    for (int j = 0; j < catArray.length(); j++) {
                        categories.add(catArray.optString(j));
                    }
                }

                // contact
                String email = "";
                String phone = "";
                JSONObject contact = props.optJSONObject("contact");
                if (contact != null) {
                    email = contact.optString("email", "");
                    phone = contact.optString("phone", "");
                }

                String imageUrl = fallbackImages.get(counter % fallbackImages.size());
                counter++;

                // Add to list first
                Attraction a = new Attraction(
                        placeId,
                        name,
                        lat,
                        lon,
                        formatted,
                        description,
                        website,
                        openingHours,
                        new ArrayList<>(categories),
                        email,
                        phone,
                        imageUrl
                );
                allAttractions.add(a);

                // Save with callback to track completion
                saveAttractionToFirestoreWithCallback(
                        placeId,
                        name,
                        lat,
                        lon,
                        formatted,
                        description,
                        website,
                        openingHours,
                        categories,
                        email,
                        phone,
                        imageUrl,
                        () -> {
                            savedCount[0]++;
                            // Only update UI when ALL attractions are saved
                            if (savedCount[0] == totalAttractions) {
                                updateTopAttractions();
                            }
                        }
                );
            }

        } catch (JSONException e) {
            Log.e(TAG, "Geoapify JSON parse error: " + e.getMessage());
            hideLoading();
        }
    }


    private void hideLoading() {
        requireActivity().runOnUiThread(() -> {
            binding.progressTop.setVisibility(View.GONE);
            binding.recyclerTopAttractions.setVisibility(View.VISIBLE);
        });
    }



    private void updateTopAttractions() {
        if (topAdapter == null) return;

        if (!isAdded()) return;

        List<Attraction> top4 = new ArrayList<>();
        for (int i = 0; i < allAttractions.size() && i < 4; i++) {
            top4.add(allAttractions.get(i));
        }

        requireActivity().runOnUiThread(() -> {
            topAdapter.setItems(top4);
            hideLoading();
        });
    }

    private void saveAttractionToFirestoreWithCallback(String placeId,
                                                       String name,
                                                       double lat,
                                                       double lon,
                                                       String formatted,
                                                       String description,
                                                       String website,
                                                       String openingHours,
                                                       List<String> categories,
                                                       String email,
                                                       String phone,
                                                       String imageUrl,
                                                       Runnable onComplete) {

        if (db == null) return;

        Map<String, Object> attraction = new HashMap<>();
        attraction.put("place_id", placeId);
        attraction.put("name", name);
        attraction.put("lat", lat);
        attraction.put("lon", lon);
        attraction.put("formatted", formatted);
        attraction.put("description", description);
        attraction.put("website", website);
        attraction.put("opening_hours", openingHours);
        attraction.put("categories", categories);
        attraction.put("email", email);
        attraction.put("phone", phone);
        attraction.put("image", imageUrl);

        db.collection("attraction_sites")
                .document(placeId)
                .set(attraction)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Saved attraction: " + placeId);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving attraction " + placeId + ": " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }
}
