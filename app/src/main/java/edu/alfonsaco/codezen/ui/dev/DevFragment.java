package edu.alfonsaco.codezen.ui.dev;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
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


    static String ID_Cliente=BuildConfig.GITHUB_CLIENTE;
    static String ID_Cliente_Secret=BuildConfig.GITHUB_CLIENTE_SECRET;
    static String URL_REDIRECT="codezen://callback";

    // Componentes
    private LinearLayout btnInicioSesionGithub;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDevBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnInicioSesionGithub=binding.btnInicioSesionGithub;
        btnInicioSesionGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarConOAuth();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Conectar con GitHub mediante OAuth
    private void conectarConOAuth() {
        String URL="https://github.com/login/oauth/authorize"
                +"?client_id="+ID_Cliente
                +"&scope=read:user%20repo"
                +"&redirect_uri="+URL_REDIRECT;

        Intent intentConectar=new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(intentConectar);
    }

    // OBTENER EL TOKEN DEL USUARIO EN GITHUB
    public static void obtenerToken(String codigo, Context context) {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://github.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        GitHubOAuthService oAuth=retrofit.create(GitHubOAuthService.class);
        oAuth.getAccessToken(ID_Cliente, ID_Cliente_Secret, codigo, URL_REDIRECT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()) {
                            String tokenAcceso=response.body().get("access_token").getAsString();
                            Toast.makeText(context.getApplicationContext(), "TOKEN: "+tokenAcceso, Toast.LENGTH_SHORT).show();

                            context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("token", tokenAcceso)
                                    .apply();

                            obtenerUsuario(tokenAcceso, context);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("ERROR", "Error al conectar");
                    }
                });
    }

    private static void obtenerUsuario(String token, Context context) {
        Retrofit retrofitApi = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api=retrofitApi.create(GitHubApi.class);
        api.getUser("Bearer"+token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    JsonObject user = response.body();
                    String username = user.get("login").getAsString();
                    Toast.makeText(context.getApplicationContext(), "USERNAME: "+username, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("GitHub", "Fallo al obtener usuario: " + t.getMessage());
            }
        });
    }
}