package edu.alfonsaco.codezen.ui.dev.api;

import com.google.gson.JsonObject;

import retrofit2.Call;

public interface GitHubApi {
    @retrofit2.http.GET("user")
    Call<JsonObject> getUser(@retrofit2.http.Header("Authorization") String authHeader);
}
