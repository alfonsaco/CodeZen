package edu.alfonsaco.codezen.ui.ranking;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import edu.alfonsaco.codezen.MainActivity;
import edu.alfonsaco.codezen.databinding.FragmentRankingBinding;
import edu.alfonsaco.codezen.ui.profile.profile_utils.LogrosAdapter;
import edu.alfonsaco.codezen.ui.ranking.ranking_utils.RankingAdapter;
import edu.alfonsaco.codezen.ui.ranking.ranking_utils.Usuario;
import edu.alfonsaco.codezen.utils.BDD;
import retrofit2.converter.gson.GsonConverterFactory;
import edu.alfonsaco.codezen.BuildConfig;

public class RankingFragment extends Fragment {

    private FragmentRankingBinding binding;
    private RecyclerView recyclerRanking;
    private RankingAdapter adapter;
    private ArrayList<Usuario> listaUsuarios;

    private BDD db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db=new BDD();

        // ***************** RECYCLER ******************************
        recyclerRanking=binding.recyclerRanking;
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this.getContext()));

        listaUsuarios=new ArrayList<>();
        adapter=new RankingAdapter(listaUsuarios, this.getContext());
        recyclerRanking.setAdapter(adapter);

        cargarUsuarios();
        // **********************************************************
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();

        db.getDb().collection("usuarios")
                .orderBy("cont_logros", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot query : queryDocumentSnapshots) {
                            String nombre=query.getString("username");
                            int contLogros=query.getLong("cont_logros").intValue();
                            int nivel=query.getLong("nivel").intValue();

                            Usuario usuario=new Usuario(contLogros, nombre, nivel);
                            listaUsuarios.add(usuario);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}