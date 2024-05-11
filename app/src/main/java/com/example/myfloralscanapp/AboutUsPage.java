package com.example.myfloralscanapp;



import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.ViewFlipper;

public class AboutUsPage extends AppCompatActivity {
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        viewFlipper = findViewById(R.id.viewFlipper);

        int[] images = {
                R.drawable.img1,
                R.drawable.img2,
                R.drawable.img3
        };

        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(2000); // Flip every 2 seconds
        viewFlipper.setAutoStart(true); // Start flipping automatically
        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        setupBottomNavigationView();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications); // set the notifications as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.navigation_home) {
                intent = new Intent(this, MainPage.class);
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                intent = new Intent(this, ImageDisplayPage.class);
            } else if (item.getItemId() == R.id.navigation_notifications) {
                // AboutUs is already displayed, no need to do anything
                return true;
            } else if (item.getItemId() == R.id.navigation_search) {
                intent = new Intent(this, SearchPage.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
            return true; // always return true because we handle all cases
        });
    }}
