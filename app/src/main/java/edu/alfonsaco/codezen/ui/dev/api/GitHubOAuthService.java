package edu.alfonsaco.codezen.ui.dev.api;

import com.google.gson.JsonObject;

import retrofit2.Call;

public interface GitHubOAuthService {
    @retrofit2.http.FormUrlEncoded
    @retrofit2.http.POST("login/oauth/access_token")
    @retrofit2.http.Headers("Accept: application/json")
    Call<JsonObject> getAccessToken(
            @retrofit2.http.Field("client_id") String clientId,
            @retrofit2.http.Field("client_secret") String clientSecret,
            @retrofit2.http.Field("code") String code,
            @retrofit2.http.Field("redirect_uri") String redirectUri
    );
}
