package com.example.appsreadjson;

import android.telecom.Call;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiConfig {
    @Multipart
    @POST("upload_image.php")
    Call<ServerResponse> uploadFile(
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name
    );
}
