package com.mbrats01.epl498_group_project.ui.plan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mbrats01.epl498_group_project.R;
import com.squareup.picasso.Picasso;

public class PlannedAttractionFragment extends Fragment {

    private ImageView imageDetail;
    private TextView textNameDetail, textScheduled, textFormattedDetail,
            textDescriptionDetail, textWebsiteDetail, textOpeningDetail,
            textEmailDetail, textPhoneDetail;

    private String name, formatted, description, website, openingHours,
            email, phone, imageUrl, selectedTime, selectedDate;

    public PlannedAttractionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planned_attraction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        imageDetail = view.findViewById(R.id.imageDetail);
        textNameDetail = view.findViewById(R.id.textNameDetail);
        textScheduled = view.findViewById(R.id.textScheduled);
        textFormattedDetail = view.findViewById(R.id.textFormattedDetail);
        textDescriptionDetail = view.findViewById(R.id.textDescriptionDetail);
        textWebsiteDetail = view.findViewById(R.id.textWebsiteDetail);
        textOpeningDetail = view.findViewById(R.id.textOpeningDetail);
        textEmailDetail = view.findViewById(R.id.textEmailDetail);
        textPhoneDetail = view.findViewById(R.id.textPhoneDetail);

        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name", "");
            formatted = args.getString("formatted", "");
            description = args.getString("description", "");
            website = args.getString("website", "");
            openingHours = args.getString("openingHours", "");
            email = args.getString("email", "");
            phone = args.getString("phone", "");
            imageUrl = args.getString("imageUrl", "");
            selectedTime = args.getString("selectedTime", "");
            selectedDate = args.getString("selectedDate", "");
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(imageDetail);
        }

        textNameDetail.setText(name);

        if (!TextUtils.isEmpty(selectedTime)) {
            textScheduled.setText("Scheduled for: " + selectedDate + " at " + selectedTime);
        } else {
            textScheduled.setText("Scheduled for: " + selectedDate);
        }

        textFormattedDetail.setText(formatted);
        textDescriptionDetail.setText(description);
        textWebsiteDetail.setText(website);
        textOpeningDetail.setText(openingHours);
        textEmailDetail.setText(email);
        textPhoneDetail.setText(phone);
    }
}
