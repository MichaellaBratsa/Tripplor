package com.mbrats01.epl498_group_project.ui.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.mbrats01.epl498_group_project.MainActivity;
import com.mbrats01.epl498_group_project.R;

public class SplashScreenFragment extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreenFragment.this, MainActivity.class));
            finish();
        }, 2500);
    }
}

