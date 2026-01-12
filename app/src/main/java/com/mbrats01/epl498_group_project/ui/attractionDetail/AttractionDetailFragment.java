package com.mbrats01.epl498_group_project.ui.attractionDetail;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mbrats01.epl498_group_project.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttractionDetailFragment extends Fragment {

    private ImageView imageDetail;
    private TextView textNameDetail;
    private TextView textFormattedDetail;
    private TextView textDescriptionDetail;
    private TextView textWebsiteDetail;
    private TextView textOpeningDetail;
    private TextView textCategoriesDetail;
    private TextView textEmailDetail;
    private TextView textPhoneDetail;

    private TextView textTimeDetail;
    private Button buttonPickTime;
    private Button buttonAddToday;
    private Button buttonAddTomorrow;

    // Current attraction data (from Bundle)
    private String placeId;
    private String name;
    private String formatted;
    private String description;
    private String website;
    private String openingHours;
    private ArrayList<String> categories;
    private String email;
    private String phone;
    private String imageUrl;

    // Time chosen by the user (HH:mm)
    private String selectedTime = "";

    public AttractionDetailFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attraction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---- Find views ----
        imageDetail        = view.findViewById(R.id.imageDetail);
        textNameDetail     = view.findViewById(R.id.textNameDetail);
        textFormattedDetail= view.findViewById(R.id.textFormattedDetail);
        textDescriptionDetail = view.findViewById(R.id.textDescriptionDetail);
        textWebsiteDetail  = view.findViewById(R.id.textWebsiteDetail);
        textOpeningDetail  = view.findViewById(R.id.textOpeningDetail);
        textCategoriesDetail = view.findViewById(R.id.textCategoriesDetail);
        textEmailDetail    = view.findViewById(R.id.textEmailDetail);
        textPhoneDetail    = view.findViewById(R.id.textPhoneDetail);

        textTimeDetail     = view.findViewById(R.id.textTimeDetail);
        buttonPickTime     = view.findViewById(R.id.buttonPickTime);
        buttonAddToday     = view.findViewById(R.id.buttonAddToday);
        buttonAddTomorrow  = view.findViewById(R.id.buttonAddTomorrow);

        // ---- Get args from navigation ----
        Bundle args = getArguments();
        if (args != null) {
            placeId      = args.getString("placeId", "");
            name         = args.getString("name", "");
            formatted    = args.getString("formatted", "");
            description  = args.getString("description", "");
            website      = args.getString("website", "");
            openingHours = args.getString("openingHours", "");
            categories   = args.getStringArrayList("categories");
            email        = args.getString("email", "");
            phone        = args.getString("phone", "");
            imageUrl     = args.getString("imageUrl", "");
        } else {
            // no args, just avoid crash
            placeId = "";
            name = "";
            formatted = "";
            description = "";
            website = "";
            openingHours = "";
            categories = new ArrayList<>();
            email = "";
            phone = "";
            imageUrl = "";
        }


        // ---- Fill UI ----

        // Image
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageDetail);
        } else {
            imageDetail.setImageResource(R.drawable.placeholder);
        }

        // Name (hide if empty)
        if (TextUtils.isEmpty(name)) {
            textNameDetail.setVisibility(View.GONE);
        } else {
            textNameDetail.setText(name);
        }

        // Formatted address
        if (TextUtils.isEmpty(formatted)) {
            textFormattedDetail.setVisibility(View.GONE);
        } else {
            textFormattedDetail.setText(formatted);
        }

        // Description
        if (TextUtils.isEmpty(description)) {
            textDescriptionDetail.setVisibility(View.GONE);
        } else {
            textDescriptionDetail.setText("Description: " + description);
        }

        // Website
        if (TextUtils.isEmpty(website)) {
            textWebsiteDetail.setVisibility(View.GONE);
        } else {
            textWebsiteDetail.setText("Website: " + website);
        }

        // Opening hours
        if (TextUtils.isEmpty(openingHours)) {
            textOpeningDetail.setVisibility(View.GONE);
        } else {
            textOpeningDetail.setText("Opening hours: " + openingHours);
        }

        // Categories
        if (categories == null || categories.isEmpty()) {
            textCategoriesDetail.setVisibility(View.GONE);
        } else {
            String cats = TextUtils.join(", ", categories);
            textCategoriesDetail.setText("Categories: " + cats);
        }

        // Email
        if (TextUtils.isEmpty(email)) {
            textEmailDetail.setVisibility(View.GONE);
        } else {
            textEmailDetail.setText("Email: " + email);
        }

        // Phone
        if (TextUtils.isEmpty(phone)) {
            textPhoneDetail.setVisibility(View.GONE);
        } else {
            textPhoneDetail.setText("Phone: " + phone);
        }

        // ---- Reviews button/icon click ----
        ImageView iconReviews = view.findViewById(R.id.iconReviews);

        iconReviews.setOnClickListener(v -> {
            Bundle args2 = new Bundle();
            args2.putString("placeId", placeId);

            NavHostFragment.findNavController(AttractionDetailFragment.this)
                    .navigate(R.id.action_attractionDetailFragment_to_reviewsFragment, args2);
        });

        // ---- Time Picker ----
        textTimeDetail.setText("Time: not set");

        buttonPickTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour   = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    requireContext(),
                    (picker, hourOfDay, min) -> {
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, min);
                        textTimeDetail.setText("Time: " + selectedTime);
                    },
                    hour,
                    minute,
                    true // 24h format
            );

            dialog.show();
        });

        // ---- Date strings (today / tomorrow) ----
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Today
        Calendar calToday = Calendar.getInstance();
        String todayStr = sdf.format(calToday.getTime());

        // Tomorrow
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowStr = sdf.format(calTomorrow.getTime());

        // ---- Firestore instance ----
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // New Button add to Today
        buttonAddToday.setOnClickListener(v -> {
            Map<String, Object> data = buildAttractionMap(todayStr);
            data.put("visited", false);

            db.collection("schedule")
                    .add(data)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(requireContext(),
                                    "Added to today's schedule", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                    "Failed to add", Toast.LENGTH_SHORT).show());
        });


        // New Button add to Tomorrow
        buttonAddTomorrow.setOnClickListener(v -> {
            Map<String, Object> data = buildAttractionMap(tomorrowStr);
            data.put("visited", false);

            db.collection("schedule")
                    .add(data)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(requireContext(),
                                    "Added to tomorrow's schedule", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                    "Failed to add", Toast.LENGTH_SHORT).show());
        });

    }

    private Map<String, Object> buildAttractionMap(String scheduleDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("place_id", placeId);
        map.put("name", name);
        map.put("formatted", formatted);
        map.put("description", description);
        map.put("website", website);
        map.put("opening_hours", openingHours);
        map.put("categories", categories != null ? categories : new ArrayList<>());
        map.put("email", email);
        map.put("phone", phone);
        map.put("image", imageUrl);
        map.put("time", selectedTime);
        map.put("date", scheduleDate);   // yyyy-MM-dd (today or tomorrow)
        return map;
    }
}
