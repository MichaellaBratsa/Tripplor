package com.mbrats01.epl498_group_project.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.mbrats01.epl498_group_project.databinding.FragmentMapBinding;
import com.mbrats01.epl498_group_project.ui.home.HomeFragment;
import com.mbrats01.epl498_group_project.ui.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    private Polyline currentRoute;
    private Marker clickedMarker;

    // Replace with your API Key
    private final String apiKey = " ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.buttonHome.setOnClickListener(v -> {
            NavHostFragment.findNavController(MapFragment.this)
                    .navigate(R.id.action_map_page_to_home_page);
        });

        //Button to go to Plans
        binding.buttonPlans.setOnClickListener(v -> {
            NavHostFragment.findNavController(MapFragment.this)
                    .navigate(R.id.action_map_page_to_plansFragment);
        });

        // Button to go to all attractions list
        binding.buttonTest.setOnClickListener(v -> {
            NavHostFragment.findNavController(MapFragment.this)
                    .navigate(R.id.action_map_page_to_all_attractions);
        });

        // Button to go to Profile
        binding.buttonProfile.setOnClickListener(v -> {
            NavHostFragment.findNavController(MapFragment.this)
                    .navigate(R.id.action_map_page_to_profile);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_page);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        enableUserLocation();
        setupInfoWindow();
        loadAttractions();
        moveToUserLocation();
    }

    // Show the current location of user with a blue dot
    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void setupInfoWindow() {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null; // keep default bubble frame
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                ImageView img = view.findViewById(R.id.infoImage);
                TextView title = view.findViewById(R.id.infoTitle);
                TextView desc = view.findViewById(R.id.infoDescription);

                title.setText(marker.getTitle());

                if (marker.getSnippet() != null) {
                    desc.setText(marker.getSnippet());
                } else {
                    desc.setText("");
                }

                if (marker.getTag() != null) {
                    String url = marker.getTag().toString();
                    Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(img);
                }

                return view;
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            clickedMarker = marker;
            marker.showInfoWindow();
            requestRoute(marker.getPosition());
            return true;
        });
    }

    // Load all attractions from database
    private void loadAttractions() {
        db.collection("attraction_sites")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {

                        Double lat = doc.getDouble("lat");
                        Double lon = doc.getDouble("lon");
                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        String imageUrl = doc.getString("image");

                        if (lat != null && lon != null) {
                            LatLng point = new LatLng(lat, lon);

                            Marker marker = googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(point)
                                            .title(name)
                                            .snippet(description)
                            );

                            if (marker != null) {
                                marker.setTag(imageUrl);
                            }
                        }
                    }
                });
    }

    // Moves the map camera to the user's current location when the map is first loaded.
    private void moveToUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMap != null) {
                LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 14f));
            }
        });
    }

    // Get the route from the user's location to the attraction
    private void requestRoute(LatLng destination) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) return;

            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());

            getRouteData(origin, destination);
            drawRoute(origin, destination);
        });
    }

    // Get the route data from the Google Maps API
    private void getRouteData(LatLng origin, LatLng destination) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // DRIVING
                String urlDriving = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origin.latitude + "," + origin.longitude
                        + "&destination=" + destination.latitude + "," + destination.longitude
                        + "&mode=driving"
                        + "&key=" + apiKey;

                // WALKING
                String urlWalking = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origin.latitude + "," + origin.longitude
                        + "&destination=" + destination.latitude + "," + destination.longitude
                        + "&mode=walking"
                        + "&key=" + apiKey;

                Request req1 = new Request.Builder().url(urlDriving).build();
                Request req2 = new Request.Builder().url(urlWalking).build();

                Response resp1 = client.newCall(req1).execute();
                Response resp2 = client.newCall(req2).execute();

                JSONObject drivingObj = new JSONObject(resp1.body().string());
                JSONObject walkingObj = new JSONObject(resp2.body().string());

                JSONObject legDriving = drivingObj.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0);

                JSONObject legWalking = walkingObj.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0);

                String driveDist = legDriving.getJSONObject("distance").getString("text");
                String driveTime = legDriving.getJSONObject("duration").getString("text");

                String walkDist = legWalking.getJSONObject("distance").getString("text");
                String walkTime = walkingObj.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("duration")
                        .getString("text");

                String info =
                        "🚶 Walking: " + walkTime + " (" + walkDist + ")\n" +
                                "🚗 Driving: " + driveTime + " (" + driveDist + ")";

                requireActivity().runOnUiThread(() -> {
                    if (clickedMarker != null) {
                        String base = clickedMarker.getTitle() + "\n\n" +
                                clickedMarker.getSnippet() + "\n\n" +
                                info;

                        clickedMarker.setSnippet(base);
                        clickedMarker.hideInfoWindow();
                        clickedMarker.showInfoWindow();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Draw the route on the map
    private void drawRoute(LatLng origin, LatLng destination) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origin.latitude + "," + origin.longitude
                        + "&destination=" + destination.latitude + "," + destination.longitude
                        + "&mode=driving"
                        + "&key=" + apiKey;

                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                String json = response.body().string();
                JSONObject obj = new JSONObject(json);

                JSONArray routes = obj.getJSONArray("routes");
                if (routes.length() == 0) return;

                String polyline = routes
                        .getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points");

                List<LatLng> points = decodePoly(polyline);

                requireActivity().runOnUiThread(() -> {
                    if (currentRoute != null) currentRoute.remove();

                    currentRoute = googleMap.addPolyline(
                            new PolylineOptions()
                                    .addAll(points)
                                    .width(10)
                                    .color(ContextCompat.getColor(requireContext(), R.color.purple_700))
                    );
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat =
                    ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng =
                    ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(
                    lat / 1E5,
                    lng / 1E5
            );
            poly.add(p);
        }

        return poly;
    }
}
