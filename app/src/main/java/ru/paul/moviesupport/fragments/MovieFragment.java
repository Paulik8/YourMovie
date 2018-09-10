package ru.paul.moviesupport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.Constants;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.NetworkService;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.adapters.MoviesFragmentAdapter;
import ru.paul.moviesupport.models.MoviePage;

public class MovieFragment extends Fragment {

    @BindView(R.id.movies_list)
    RecyclerView moviesList;
    Context context;
    //Database database;
    public static final String TAG = "MovieFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movies_fragment, container, false);
        ButterKnife.bind(this, v);
        //database = new Database(getActivity());
        context = getContext();
        initMoviesList();
        return v;
    }

    private void initMoviesList() {
        Database database = new Database(getActivity());
        MoviePage moviePage = database.getMoviePage();
        if (moviePage != null) {
            MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, moviePage.getResults());//
            moviesList.setLayoutManager(new LinearLayoutManager(context));
            moviesList.setAdapter(adapter);
            Log.i("movies", moviePage.getResults().get(0).getId().toString());
        }
        Log.i("movies", "Null");
        createRequest();
    }

    private void updateMoviesList(MoviePage page) {
        MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, page.getResults());
        moviesList.setLayoutManager(new LinearLayoutManager(context));
        moviesList.setAdapter(adapter);
        adapter.refreshList();
    }

    private void createRequest() {

        NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        Constants.SORT_BY_POPULARITY_DESC,
                        1);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                Log.i("page", String.valueOf(page.getResults().get(0).getId()));
                updateMoviesList(page);
                Database database = new Database(getActivity());
                database.saveMovieData(page);
                //database.deleteDB();
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {

            }
        });
    }
}
