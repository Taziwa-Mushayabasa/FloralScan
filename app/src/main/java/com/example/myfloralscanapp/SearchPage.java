package com.example.myfloralscanapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchPage extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private LinearLayout resultsContainer;
    private TextView noResultsFoundText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        resultsContainer = findViewById(R.id.resultsContainer);
        noResultsFoundText = findViewById(R.id.noResultsFoundText);

        setupSearchButton();
        setupBottomNavigationView();

        //testHardcodedSearch("rose");
    }

 /*   private void testHardcodedSearch(String query) {
        searchInput.setText(query);
        String validationMessage = validateQuery(query);
        if ("Valid".equals(validationMessage)) {
            performSearch(query.trim());
        } else {
            Toast.makeText(this, validationMessage, Toast.LENGTH_SHORT).show();
        }
    } */

    public String validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Please enter a search query.";
        }
        return "Valid";
    }

    // Modify the existing method to use the new method
    private void setupSearchButton() {
        searchButton.setOnClickListener(view -> {
            String query = searchInput.getText().toString();
            String validationMessage = validateQuery(query);
            if ("Valid".equals(validationMessage)) {
                performSearch(query.trim());
                resultsContainer.removeAllViews();
                noResultsFoundText.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, validationMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search); // set the search as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.navigation_home) {
                intent = new Intent(this, MainPage.class);
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                intent = new Intent(this, ImageDisplayPage.class);
            } else if (item.getItemId() == R.id.navigation_notifications) {
                intent = new Intent(this, AboutUsPage.class);
            } else if (item.getItemId() == R.id.navigation_search) {
                // SearchPageActivity is already displayed, no need to do anything
                return true;
            }

            if (intent != null) {
                startActivity(intent);
            }
            return true; // always return true because we handle all cases
        });
    }

    public void performSearch(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error encoding the query.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BuildConfig.ACCESS_KEY + encodedQuery;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SearchPage.this, "Network error, please try again.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SearchPage.this, "Error fetching results.", Toast.LENGTH_LONG).show());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                runOnUiThread(() -> parseResults(responseBody));
            }
        });
    }

    private void parseResults(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            if (dataArray.length() == 0) {
                noResultsFoundText.setVisibility(View.VISIBLE);
            } else {
                noResultsFoundText.setVisibility(View.GONE);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);

                    String commonName = item.optString("common_name", "No common name");
                    String scientificName = item.optJSONArray("scientific_name").optString(0, "No scientific name");
                    String description = item.optString("description", "Description not available.");
                    int plantId = item.optInt("id", -1);  // Use -1 as default to indicate invalid ID

                    JSONObject defaultImage = item.optJSONObject("default_image");
                    String imageUrl = defaultImage != null ? defaultImage.optString("regular_url", "") : "";

                    runOnUiThread(() -> updateResultsView(commonName, scientificName, imageUrl, description, plantId));
                }
            }
        } catch (JSONException e) {
            runOnUiThread(() -> Toast.makeText(SearchPage.this, "Error parsing data.", Toast.LENGTH_LONG).show());
        }
    }
    private void updateResultsView(String commonName, String scientificName, String imageUrl, String description, int plantId) {
        View resultView = getLayoutInflater().inflate(R.layout.item_plant, resultsContainer, false);

        TextView nameTextView = resultView.findViewById(R.id.textview_plant_name);
        TextView sciNameTextView = resultView.findViewById(R.id.textview_plant_sci_name);
        ImageView imageView = resultView.findViewById(R.id.imageview_plant);

        nameTextView.setText(commonName);
        sciNameTextView.setText(scientificName);
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_flower); // Default image if URL is empty
        }

        resultView.setOnClickListener(v -> {
            if (plantId != -1) {
                fetchPlantDetails(plantId);
            } else {
                Toast.makeText(SearchPage.this, "Invalid plant ID", Toast.LENGTH_SHORT).show();
            }
        });

        resultsContainer.addView(resultView);
    }

    private void fetchPlantDetails(int plantId) {
        String url = "https://perenual.com/api/species/details/" + plantId + "?key=sk-xcWu65d4a15e7a4354192";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SearchPage.this, "Error fetching plant details.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SearchPage.this, "Failed to fetch plant details.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                runOnUiThread(() -> displayPlantDetails(responseBody));
            }
        });
    }

    private void displayPlantDetails(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String description = jsonObject.optString("description", "Description not available.");

            runOnUiThread(() -> {
                // Ensure you're actually updating a UI element that is visible and correctly configured
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchPage.this);
                builder.setTitle("Plant Details");
                builder.setMessage(description);
                builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        } catch (JSONException e) {
            Log.e("JSONError", "Failed to parse JSON", e);
            runOnUiThread(() -> Toast.makeText(SearchPage.this, "Error parsing details data.", Toast.LENGTH_LONG).show());
        }
    }
}