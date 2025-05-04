package edu.alfonsaco.codezen.ui.dev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.alfonsaco.codezen.BuildConfig;

import edu.alfonsaco.codezen.databinding.FragmentDevBinding;
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

        // CONECTAR A LA API DE GRAPHQL
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

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
}