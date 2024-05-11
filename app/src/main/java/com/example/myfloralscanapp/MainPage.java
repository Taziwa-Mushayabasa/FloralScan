package com.example.myfloralscanapp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.*;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.myfloralscanapp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainPage extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_UPLOAD = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private ActivityMainBinding binding;

    private AmazonS3Client s3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this); //Loading screen
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        // Initialize the TransferNetworkLossHandler
        TransferNetworkLossHandler.getInstance(getApplicationContext());

        initializeAWSClient();
        setupButtonListeners();

        setupBottomNavigationView();

    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // set the home as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.navigation_home) {
                // MainPage already displayed
                return true;
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                intent = new Intent(this, ImageDisplayPage.class);
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



    private void initializeAWSClient() {
        String accessKey = BuildConfig.AWS_ACCESS_KEY;
        String secretKey = BuildConfig.AWS_SECRET_KEY;

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = new AmazonS3Client(awsCredentials, new ClientConfiguration());
        s3Client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getAssets().open("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            Log.e("MainActivity", "Failed to load properties file.", ex);
        }
        return properties;
    }

    private void setupButtonListeners() {
        binding.buttonUploadFile.setOnClickListener(v -> selectImageFromGallery());
        binding.buttonUseCamera.setOnClickListener(v -> takePhotoWithCamera());
    }

    private void selectImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_UPLOAD);
    }

    private void takePhotoWithCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                handleCameraImage(data);
            } else if (requestCode == REQUEST_IMAGE_UPLOAD) {
                handleGalleryImage(data);
            }
        }
    }

    private void handleCameraImage(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        showConfirmationDialog(imageBitmap);
    }

    private void showConfirmationDialog(final Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Image");
        builder.setMessage("Do you want to use this image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File fileToUpload = convertBitmapToFile(bitmap, "gallery_image.jpg");
                if (fileToUpload != null) {
                    uploadImage(fileToUpload);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleGalleryImage(Intent data) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            showConfirmationDialog(selectedImageUri);
        }
    }

    private void showConfirmationDialog(final Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Image");
        builder.setMessage("Do you want to use this image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleGalleryImageConfirmed(imageUri);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleGalleryImageConfirmed(Uri selectedImageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            File fileToUpload = new File(getExternalFilesDir(null), "gallery_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(fileToUpload);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            uploadImage(fileToUpload);
        } catch (IOException e) {
            Log.e("MainActivity", "Failed to handle gallery image.", e);
        }
    }
    private void uploadImage(File imageFile) {

        if (!progressDialog.isShowing()) {
            progressDialog.show(); //Loading screen
        }
        String bucketName = "floralscaninput";
        String folderName = "PhotoInput/";
        String fileName = imageFile.getName();
        String objectKey = folderName + fileName;

        TransferUtility transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(s3Client)
                .build();

        PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, imageFile)
                .withCannedAcl(CannedAccessControlList.PublicRead); // Setting ACL to public read

        TransferObserver uploadObserver = transferUtility.upload(bucketName, objectKey, imageFile);
        setTransferListener(uploadObserver);
    }

    private void setTransferListener(TransferObserver uploadObserver) {
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED || state == TransferState.FAILED) {
                    progressDialog.dismiss();
                }
                if (state == TransferState.COMPLETED) {
                    startActivity(new Intent(MainPage.this, ImageDisplayPage.class));
                    finish();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    runOnUiThread(() -> progressDialog.setMessage("Uploading... " + (int) (100 * bytesCurrent / bytesTotal) + "%"));
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                runOnUiThread(() -> Toast.makeText(MainPage.this, "Error during upload: " + ex.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private File convertBitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getExternalFilesDir(null), fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (IOException e) {
            Log.e("MainActivity", "Error saving image to file.", e);
            return null;
        }
    }

}