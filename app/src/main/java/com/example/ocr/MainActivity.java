package com.example.ocr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private final String azureEndpoint = "https://ocr-cmpe277.cognitiveservices.azure.com/";
    private final String subscriptionKey = "f248726bdcca42719badd106bb84a639";
    private ImageView imageView;
    private TextView resultTextView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectImageButton = findViewById(R.id.btnSelectImage);
        Button analyzeButton = findViewById(R.id.btnAnalyze);
        resultTextView = findViewById(R.id.tvResults);
        imageView = findViewById(R.id.imageView);

        selectImageButton.setOnClickListener(view -> openImagePicker());

        analyzeButton.setOnClickListener(view -> {
            if (selectedImageUri != null) {
                analyzeImage(selectedImageUri);
            } else {
                resultTextView.setText("No image selected.");
            }
        });

        // Request permission for external storage if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PICK_IMAGE_REQUEST);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private byte[] uriToByteArray(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void analyzeImage(Uri selectedImageUri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(azureEndpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AzureAPI service = retrofit.create(AzureAPI.class);
        byte[] imageData = uriToByteArray(selectedImageUri);
        if (imageData == null) {
            resultTextView.setText("Error converting image to byte array.");
            return;
        }
        RequestBody reqFile = RequestBody.create(MediaType.parse("application/octet-stream"), imageData);

        String apiVersion = "2023-02-01-preview";
        String features = "read";
        String language = "en";
        boolean genderNeutralCaption = false;
        Call<OCRResult> call = service.analyzeImage(
                azureEndpoint,
                subscriptionKey,
                "application/octet-stream",
                reqFile,
                apiVersion,
                features,
                language,
                genderNeutralCaption);
        call.enqueue(new Callback<OCRResult>() {
            @Override
            public void onResponse(Call<OCRResult> call, Response<OCRResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OCRResult ocrResponse = response.body();
                    String resultText = "OCR Result: " + ocrResponse.getResult().getContent();
                    resultTextView.setText(resultText);
                } else {
                    Log.e("Error","Got 1st error");
                    resultTextView.setText("OCR failed with response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OCRResult> call, Throwable t) {
                Log.e("Error","Got 2st error");
                resultTextView.setText("OCR request failed: " + t.getMessage());
            }
        });
    }
}
