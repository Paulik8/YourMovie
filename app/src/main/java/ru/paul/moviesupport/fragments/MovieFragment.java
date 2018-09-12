package ru.paul.moviesupport.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.Constants;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.MainActivity;
import ru.paul.moviesupport.NetworkService;
import ru.paul.moviesupport.OnLoadMoreListener;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.adapters.MoviesFragmentAdapter;
import ru.paul.moviesupport.models.Movie;
import ru.paul.moviesupport.models.MoviePage;

public class MovieFragment extends Fragment {

    @BindView(R.id.movies_list)
    RecyclerView moviesList;
    Context context;
    Handler handler;
    List<Movie> movie;
    List<Movie> firstPageMovies;
    MoviesFragmentAdapter adapter;
    Integer pageNumber = 1;
    List<Movie> requestDownMovies;
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
        handler = new Handler();
        //database = new Database(getActivity());
        context = getContext();
        moviesList.setHasFixedSize(true);
        initMoviesList();

        return v;
    }

    private void initMoviesList() {
        Database database = new Database(getActivity());
        firstPageMovies = database.getFirstPageMovies();
        if (firstPageMovies != null) {
            moviesList.setLayoutManager(new LinearLayoutManager(context));
            MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, firstPageMovies, moviesList);//
            moviesList.setAdapter(adapter);
            Log.i("movies", firstPageMovies.get(0).getOriginalTitle());
        }
        Log.i("pageNumber", pageNumber.toString());
        createRequest(pageNumber);
    }

    private void updateMoviesList(MoviePage page) {
        moviesList.setLayoutManager(new LinearLayoutManager(context));
        MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, page.getResults(), moviesList);
        moviesList.setAdapter(adapter);

        adapter.refreshList();
    }

    public void setListener(List<Movie> responseMovies) {
        //if (firstPageMovies == null) {
            moviesList.setLayoutManager(new LinearLayoutManager(context));
        //}
        movie = new ArrayList<>(responseMovies);
        adapter = new MoviesFragmentAdapter(context, movie, moviesList);
        moviesList.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                createRequestDown(++pageNumber);
                movie.add(null);
                adapter.notifyItemInserted(movie.size() - 1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   remove progress item
                        movie.remove(movie.size() - 1);
                        adapter.notifyItemRemoved(movie.size());
                        //add items one by one
                        int start = 0;
                        Log.i("remove", "remove");
                        //pageNumber++;

                        int end = start + requestDownMovies.size();

                        movie.addAll(requestDownMovies);
                        adapter.notifyDataSetChanged();
//                        for (int i = start + 1; i < end; i++) {
//                            movie.add(requestDownMovies.get(i));
//                            adapter.notifyItemInserted(movie.size());
//                        }
                        adapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);

            }
        });
    }

    private void createRequestDown(Integer pageNumber) {
        Log.i("req", "req");
        NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        Constants.SORT_BY_POPULARITY_DESC,
                        pageNumber);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                requestDownMovies = response.body().getResults();
                Log.i("request", requestDownMovies.get(0).getOriginalTitle());
                //updateMoviesList(page);
                Database database = new Database(getActivity());
                database.saveMovieData(response.body());
                //setListener(page);
                //database.deleteDB();
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                //setListener(moviePage);
            }
        });
    }

    private void createRequest(Integer pageNumber) {

        NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        Constants.SORT_BY_POPULARITY_DESC,
                        pageNumber);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                Log.i("page", page.getResults().get(0).getOriginalTitle());
                //updateMoviesList(page);
                Database database = new Database(getActivity());
                database.saveMovieData(page);
                setListener(page.getResults());
                //database.deleteDB();
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                setListener(firstPageMovies);
            }
        });
    }
}
