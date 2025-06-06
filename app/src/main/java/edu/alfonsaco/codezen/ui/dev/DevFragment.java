package edu.alfonsaco.codezen.ui.dev;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.utils.ArchievementsUnlocks;
import edu.alfonsaco.codezen.utils.BDD;
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
    private ArchievementsUnlocks logros;
    private BDD db;

    // GitHub
    static String ID_Cliente = BuildConfig.GITHUB_CLIENTE;
    static String ID_Cliente_Secret = BuildConfig.GITHUB_CLIENTE_SECRET;
    static String URL_REDIRECT = "codezen://callback";

    // Componentes
    private TextView txtNombreGitHub;
    private ImageView avatarGitHub;
    private LinearLayout btnInicioSesionGithub;
    private Button btnCerrarSesionGitHub;
    private TextView txtSeguidos;
    private TextView txtSeguidores;
    private TextView txtOcultar2;
    private TextView txtOcultar1;
    private TextView txtTotalCommits;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDevBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db=new BDD();
        logros=new ArchievementsUnlocks(db);

        btnInicioSesionGithub = binding.btnInicioSesionGithub;
        btnInicioSesionGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarConOAuth();
            }
        });

        // Elementos para ocultar
        txtNombreGitHub=binding.txtNombreGitHub;
        avatarGitHub=binding.avatarGitHub;
        btnCerrarSesionGitHub=binding.btnCerrarSesionGitHub;
        txtSeguidores=binding.txtSeguidores;
        txtSeguidos=binding.txtSiguiendo;
        txtOcultar1=binding.txtOcultar1;
        txtOcultar2=binding.txtOcultar2;
        txtTotalCommits=binding.txtTotalCommits;

        btnCerrarSesionGitHub.setVisibility(View.GONE);
        txtNombreGitHub.setVisibility(View.GONE);
        avatarGitHub.setVisibility(View.GONE);
        txtSeguidores.setVisibility(View.GONE);
        txtSeguidos.setVisibility(View.GONE);
        txtOcultar1.setVisibility(View.GONE);
        txtOcultar2.setVisibility(View.GONE);
        txtTotalCommits.setVisibility(View.GONE);

        verificarSesionIniciada();


        // Cerrar sesión en GitHub
        btnCerrarSesionGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                ocultarComponentes();
            }
        });
    }



    private void ocultarComponentes() {
        btnInicioSesionGithub.setVisibility(View.VISIBLE);

        txtNombreGitHub.setVisibility(View.GONE);
        avatarGitHub.setVisibility(View.GONE);
        btnCerrarSesionGitHub.setVisibility(View.GONE);
        txtSeguidores.setVisibility(View.GONE);
        txtSeguidos.setVisibility(View.GONE);
        txtOcultar1.setVisibility(View.GONE);
        txtOcultar2.setVisibility(View.GONE);
        txtTotalCommits.setVisibility(View.GONE);
    }

    // Método para verificar si se ha iniciado sesión o no
    public void verificarSesionIniciada() {
        if (!isAdded() || getActivity() == null) {
            Log.w("DevFragment", "Fragmento no adjunto a la actividad");
            return;
        }

        SharedPreferences preferences = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        String username = preferences.getString("username", null);
        String avatar=preferences.getString("avatar", null);
        int seguidos=preferences.getInt("seguidos", 0);
        int seguidores=preferences.getInt("seguidores", 0);

        obtenerTotalCommits(token, username, requireContext());

        Log.e("URL", "Avatar: "+avatar);
        requireActivity().runOnUiThread(() -> {
            try {
                // Actualizar UI
                // Ocultar
                btnInicioSesionGithub.setVisibility(token == null ? View.VISIBLE : View.GONE);
                // Mostrar
                txtNombreGitHub.setVisibility(token != null ? View.VISIBLE : View.GONE);
                btnCerrarSesionGitHub.setVisibility(token != null ? View.VISIBLE : View.GONE);
                txtSeguidores.setVisibility(token != null ? View.VISIBLE : View.GONE);
                txtSeguidos.setVisibility(token != null ? View.VISIBLE : View.GONE);
                txtOcultar2.setVisibility(token != null ? View.VISIBLE : View.GONE);
                txtOcultar1.setVisibility(token != null ? View.VISIBLE : View.GONE);
                txtTotalCommits.setVisibility(token != null ? View.VISIBLE : View.GONE);

                if (username != null) {
                    txtNombreGitHub.setText(username);
                } else if (token != null) {
                    txtNombreGitHub.setText("Cargando...");
                    // Forzar nueva obtención de datos si hay token pero no username
                    obtenerUsuario(token, requireContext());
                }

                if(avatar != null) {
                    avatarGitHub.setVisibility(token != null ? View.VISIBLE : View.GONE);
                    Glide.with(requireContext()).load(avatar).circleCrop().into(avatarGitHub);
                }

                txtSeguidores.setText(String.valueOf(seguidores));
                txtSeguidos.setText(String.valueOf(seguidos));

            } catch (Exception e) {
                Log.e("DevFragment", "Error en runOnUiThread", e);
            }
        });
    }

    // ************************************* OBTENER DATOS *****************************************
    private void obtenerTotalCommits(String token, String username, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        GitHubApi api = retrofit.create(GitHubApi.class);

        api.listRepos(username, "Bearer " + token).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("GitHubAPI", "Error al obtener repos: " + response.code());
                    return;
                }

                JsonArray repos = response.body();
                int[] totalCommits = {0};
                int[] pendingCalls = {repos.size()};

                for (JsonElement repoElement : repos) {
                    JsonObject repo = repoElement.getAsJsonObject();
                    String repoName = repo.get("name").getAsString();
                    String owner = repo.get("owner").getAsJsonObject().get("login").getAsString();

                    api.listContributors(owner, repoName, "Bearer " + token)
                            .enqueue(new Callback<JsonArray>() {
                                @Override
                                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                    pendingCalls[0]--;
                                    if (response.isSuccessful() && response.body() != null) {
                                        for (JsonElement contributor : response.body()) {
                                            JsonObject contributorObj = contributor.getAsJsonObject();
                                            if (contributorObj.get("login").getAsString().equals(username)) {
                                                totalCommits[0] += contributorObj.get("contributions").getAsInt();
                                                break;
                                            }
                                        }

                                    }
                                    if (pendingCalls[0] == 0) {
                                        Log.d("TOTAL_COMMITS", "Total commits: " + totalCommits[0]);
                                    }

                                    // Evitamos que de error
                                    if (isAdded()) {
                                        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
                                        prefs.edit().putInt("cont_commits", totalCommits[0]).apply();

                                        requireActivity().runOnUiThread(() -> {
                                            txtTotalCommits.setText(String.valueOf(totalCommits[0]));
                                        });
                                    }

                                    txtTotalCommits.setText(String.valueOf(totalCommits[0]));
                                    logros.logrosGitHub(context, totalCommits[0]);
                                }

                                @Override
                                public void onFailure(Call<JsonArray> call, Throwable t) {
                                    pendingCalls[0]--;
                                    Log.e("GitHubAPI", "Error al obtener contribs", t);
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("GitHubAPI", "Error al obtener repos", t);
            }
        });
    }


    // ******************************* CONECTAR CON API DE GITHUB **********************************
    private void conectarConOAuth() {
        String URL = "https://github.com/login/oauth/authorize"
                + "?client_id=" + ID_Cliente
                + "&scope=read:user"
                + "&redirect_uri=" + URL_REDIRECT;

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
    }
    
    public static void obtenerToken(String codigo, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://github.com/")
                .addConverterFactory(GsonConverterFactory.create())
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
                            Log.d("DevFragment", "Token obtenido: " + tokenAcceso);
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("GITHUB_OAUTH", "Error al leer el cuerpo del error: ", e);
                            }
                            Log.e("GITHUB_OAUTH", "Error al obtener token: " + response.code() + " - " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void storeTokenAndFetchUser(String token, Context context) {
        if (context == null) {
            Log.e("DevFragment", "Contexto nulo en storeToken");
            return;
        }

        SharedPreferences.Editor editor = context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();

        Log.d("DevFragment", "Token almacenado, obteniendo usuario...");
        obtenerUsuario(token, context);
    }

    private static void obtenerUsuario(String token, Context context) {
        if (!(context instanceof MainActivity)) {
            Log.e("DevFragment", "Contexto no es MainActivity");
            return;
        }

        MainActivity activity = (MainActivity) context;
        if (activity.isFinishing() || activity.isDestroyed()) {
            Log.w("DevFragment", "Actividad no disponible");
            return;
        }

        Retrofit retrofitApi = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofitApi.create(GitHubApi.class);
        api.getUser("Bearer " + token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("GitHubAPI", "Error: " + response.code());
                    return;
                }

                try {
                    JsonObject user = response.body();
                    String username = user.get("login").getAsString();
                    String avatar=user.get("avatar_url").getAsString();
                    int seguidores=user.get("followers").getAsInt();
                    int seguidos=user.get("following").getAsInt();

                    SharedPreferences.Editor editor = context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit();
                    editor.putString("username", username);
                    editor.putString("avatar", avatar);
                    editor.putInt("seguidores", seguidores);
                    editor.putInt("seguidos", seguidos);
                    editor.apply();

                    activity.runOnUiThread(() -> {
                        if (!activity.isFinishing()) {
                            activity.updateDevFragmentUI();
                        }
                    });

                } catch (Exception e) {
                    Log.e("GitHubAPI", "Error parsing response", e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("GitHubAPI", "Network error", t);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
    // *********************************************************************************************
}