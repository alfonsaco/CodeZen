package edu.alfonsaco.codezen.ui.dev.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;

public interface GitHubApi {
    @retrofit2.http.GET("user")
    Call<JsonObject> getUser(@retrofit2.http.Header("Authorization") String authHeader);

    // Endpoints para obtener el número de commtis y pull requests
    @retrofit2.http.GET("users/{username}/repos")
    Call<JsonArray> listRepos(
            @retrofit2.http.Path("username") String username,
            @retrofit2.http.Header("Authorization") String authHeader
    );

    @retrofit2.http.GET("repos/{owner}/{repo}/contributors")
    Call<JsonArray> listContributors(
            @retrofit2.http.Path("owner") String owner,
            @retrofit2.http.Path("repo") String repo,
            @retrofit2.http.Header("Authorization") String authHeader
    );
}
