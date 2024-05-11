package com.example.myfloralscanapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class ImageDisplayPage extends AppCompatActivity {

    private ImageView uploadedImage;
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard); // set the dashboard as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.navigation_home) {
                intent = new Intent(this, MainPage.class);
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                // ImageDisplay is already open
                return true;
            } else if (item.getItemId() == R.id.navigation_notifications) {
                intent = new Intent(this, AboutUsPage.class);
            } else if (item.getItemId() == R.id.navigation_search) {
                intent = new Intent(this, SearchPage.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
            return true; // always return true because we handle all cases
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        uploadedImage = findViewById(R.id.uploadedImage);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        plantDescriptionTextView = findViewById(R.id.plantDescriptionTextView);

        setupBottomNavigationView();
        getPlantDetailsFromLambda();
    }

    private void getPlantDetailsFromLambda() {
        OkHttpClient client = new OkHttpClient();
        String lambdaEndpoint = "https://utf5cdwynh.execute-api.eu-west-1.amazonaws.com/PlantDefaultStage/flowers"; // API Gateway endpoint

        Request request = new Request.Builder()
                .url(lambdaEndpoint)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("LambdaAPI", "Network request failed: " + e.getMessage());
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("LambdaResponse", "Successful response data: " + responseData);
                    runOnUiThread(() -> updateUI(responseData));
                } else {
                    Log.e("LambdaResponse", "Unsuccessful response with status code: " + response.code() + " and message: " + response.message());
                    String errorBody = response.body().string();
                    Log.e("LambdaResponse", "Unsuccessful response body: " + errorBody);
                    // Handle non-successful response
                }
            }
        });
    }

    private void updateUI(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            //Gets plant details by parsing into string
            final String plantName = jsonObject.optString("common_name", "Name not available");
            final String description = jsonObject.optString("description", "Description not available");

            // updates the page with the plant details
            plantNameTextView.setText(plantName);
            plantDescriptionTextView.setText(description);


            String imageUrl = jsonObject.optString("image_url", "");
            if (!imageUrl.isEmpty()) {
                // Loads image
                Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder_flower).into(uploadedImage);
            } else {

                uploadedImage.setImageResource(R.drawable.placeholder_flower); // Use an actual placeholder resource
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("UpdateUI", "JSONException: " + e.getMessage());
            // Handle exception
        }
    }

}