package edu.alfonsaco.codezen.ui.dev;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import edu.alfonsaco.codezen.MainActivity;
import retrofit2.converter.gson.GsonConverterFactory;
import edu.alfonsaco.codezen.BuildConfig;

import edu.alfonsaco.codezen.databinding.FragmentDevBinding;
import edu.alfonsaco.codezen.ui.dev.api.GitHubApi;
import edu.alfonsaco.codezen.ui.dev.api.GitHubOAuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DevFragment extends Fragment {

    private FragmentDevBinding binding;
    private LinearLayout btnInicioSesionGithub;

    static String ID_Cliente = BuildConfig.GITHUB_CLIENTE;
    static String ID_Cliente_Secret = BuildConfig.GITHUB_CLIENTE_SECRET;
    static String URL_REDIRECT = "codezen://callback";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDevBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnInicioSesionGithub = binding.btnInicioSesionGithub;
        btnInicioSesionGithub.setOnClickListener(v -> conectarConOAuth());

        checkLoginStatus();
    }

    public void checkLoginStatus() {
        SharedPreferences preferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);

        btnInicioSesionGithub.setVisibility(token == null ? View.VISIBLE : View.GONE);

        if (token != null) {
            Toast.makeText(getContext(), "USUARIO VERIFICADO", Toast.LENGTH_SHORT).show();
        }
    }

    private void conectarConOAuth() {
        String URL = "https://github.com/login/oauth/authorize"
                + "?client_id=" + ID_Cliente
                + "&scope=read:user%20repo"
                + "&redirect_uri=" + URL_REDIRECT;

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
    }

    public static void obtenerToken(String codigo, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://github.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        GitHubOAuthService oAuth = retrofit.create(GitHubOAuthService.class);
        oAuth.getAccessToken(ID_Cliente, ID_Cliente_Secret, codigo, URL_REDIRECT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String tokenAcceso = response.body().get("access_token").getAsString();
                            storeTokenAndFetchUser(tokenAcceso, context);
                        } else {
                            Toast.makeText(context, "Error al obtener token", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                        Log.e("GITHUB_OAUTH", "Error: ", t);
                    }
                });
    }

    private static void storeTokenAndFetchUser(String token, Context context) {
        // Store token
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                .edit()
                .putString("token", token)
                .apply();

        // Fetch user info
        obtenerUsuario(token, context);
    }

    private static void obtenerUsuario(String token, Context context) {
        Retrofit retrofitApi = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofitApi.create(GitHubApi.class);
        api.getUser("Bearer " + token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject user = response.body();
                    String username = user.get("login").getAsString();
                    Toast.makeText(context, "Bienvenido " + username, Toast.LENGTH_SHORT).show();

                    // Notify fragment to update UI
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).updateDevFragmentUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("GitHub", "Error al obtener usuario", t);
            }
        });
    }
}