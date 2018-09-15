package ru.paul.moviesupport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.adapters.StaredMovieFragmentAdapter;
import ru.paul.moviesupport.models.Movie;

public class StaredMovieFragment extends Fragment {

    Database database;
    Context context;
    List<Movie> movies;
    StaredMovieFragmentAdapter adapter;
    @BindView(R.id.stared_movies_list)
    RecyclerView recyclerView;

    public static final String TAG = "StaredMovie";

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
        initStaredList();
        return v;
    }

    private void initStaredList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getStaredFromDatabase();
        adapter = new StaredMovieFragmentAdapter(context, movies);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getStaredFromDatabase() {
        movies = database.getStaredMovies();
    }
}
