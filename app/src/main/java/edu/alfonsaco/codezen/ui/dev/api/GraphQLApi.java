package edu.alfonsaco.codezen.ui.dev.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.google.gson.JsonObject;

public interface GraphQLApi {
    @Headers("Content-Type: JSON/application")
    @POST("graphql")
    Call<String> llamadaAPI(
            @Body String body,
            @Header("Authorization") String token
    );
}

