package ru.paul.moviesupport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.adapters.MoviesFragmentAdapter;

public class MovieFragment extends Fragment {

//    @BindView(R.id.movies)
//    RecyclerView moviesList;
    public static final String TAG = "MovieFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movies_fragment, container, false);
        //ButterKnife.bind(this, v);
        Log.i("movie", "movie");
        return v;
    }

    private void initMoviesList() {
        MoviesFragmentAdapter adapter = new MoviesFragmentAdapter();
    }
}
