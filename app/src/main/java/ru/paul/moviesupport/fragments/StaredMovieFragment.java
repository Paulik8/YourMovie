package ru.paul.moviesupport.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.MainActivity;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.adapters.StaredMovieFragmentAdapter;
import ru.paul.moviesupport.models.Movie;

public class StaredMovieFragment extends Fragment {

    Database database;
    Context context;
    List<Movie> movies;
    StaredMovieFragmentAdapter adapter;
    BroadcastReceiver receiver;
    @BindView(R.id.stared_movies_list)
    RecyclerView recyclerView;

    public static final String REMOVE_MOVIE = "REMOVE_MOVIE";
    public static final String CHANGE_TOOLBAR = "CHANGE_TOOLBAR";

    public static final String TAG = "StaredMovie";


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stared_fragment, container, false);
        ButterKnife.bind(this, v);
        context = getContext();
        database = new Database(getActivity());
        //recyclerView.setHasFixedSize(true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REMOVE_MOVIE);
        intentFilter.addAction(CHANGE_TOOLBAR);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case REMOVE_MOVIE:
                            Integer pos = intent.getExtras().getInt("position");
                            Integer movie = intent.getExtras().getInt("movie");
                            Movie movieRemove = movies.get(pos);
                            movies.remove(movieRemove);
                            database.removeFromStaredData(intent.getExtras().getInt("movie"));
                            adapter.notifyItemRemoved(pos);
                            database.updateMovieData(movie);
                            break;
                        case CHANGE_TOOLBAR:
                            ((MainActivity)getActivity()).actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                            // Show back button
                            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            break;
                    }
                }
            }
        };
        context.registerReceiver(receiver, intentFilter);

        initStaredList();

        return v;
    }

    private void initStaredList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getStaredFromDatabase();
        adapter = new StaredMovieFragmentAdapter(context, movies);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    private void getStaredFromDatabase() {
        movies = new ArrayList<>();
        movies = database.getStaredMovies();
    }
}
