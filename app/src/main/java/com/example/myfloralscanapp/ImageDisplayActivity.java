package com.example.myfloralscanapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.InputStream;
import java.net.URL;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ImageView uploadedImage = findViewById(R.id.uploadedImage);
        TextView dummyTextView = findViewById(R.id.dummyTextView);

        String imageUrl = "https://floralscaninput.s3.eu-west-1.amazonaws.com/PhotoInput/gallery_image.jpg"; // Replace with your actual presigned URL
        new DownloadImage(imageUrl, uploadedImage).execute();

        dummyTextView.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit...");

        setupBottomNavigationView();
    }

    private static class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;

        public DownloadImage(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(Void... params) {
            try {
                InputStream in = new URL(url).openStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent intentMain = new Intent(ImageDisplayActivity.this, MainActivity.class);
                startActivity(intentMain);
                return true;
            }
            return false;
        });
    }
}