package com.example.ocr;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AzureAPI {
    @POST
    Call<OCRResult> analyzeImage(
            @Url String url,
            @Header("Ocp-Apim-Subscription-Key") String subscriptionKey,
            @Header("Content-Type") String contentType,
            @Body RequestBody image,
            @Query("api-version") String apiVersion,
            @Query("features") String features,
            @Query("language") String language,
            @Query("gender-neutral-caption") boolean genderNeutralCaption);
}