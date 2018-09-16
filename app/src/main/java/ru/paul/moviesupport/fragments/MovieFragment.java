package ru.paul.moviesupport.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

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
import ru.paul.moviesupport.models.Genre;
import ru.paul.moviesupport.models.Movie;
import ru.paul.moviesupport.models.MoviePage;

public class MovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.movies_list)
    RecyclerView moviesList;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean isSetListener = false;
    Context context;
    Handler handler;
    List<Movie> movie;
    List<Movie> firstPageMovies;
    MoviesFragmentAdapter adapter;
    Integer pageNumber;
    BroadcastReceiver broadcastReceiver;
    List<Movie> requestDownMovies;

    static final String HANDLER_MESSAGE = "HANDLER_MESSAGE";
    static final String CREATE_REQUEST = "CREATE_REQUEST";
    public static final String CHANGE_TOOLBAR = "CHANGE_TOOLBAR";
    public static final String STARED_REMOVE = "STARED_REMOVE";
    public static final String STARED_SAVE = "STARED_SAVE";
    public static final String HIDE_SEARCH = "HIDE_SEARCH";

    Intent intent;
    Database database;
    public static final String TAG = "MovieFragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(broadcastReceiver);
        if (adapter != null) {
            adapter.unregisterOnLoadMoreListener();
        }
        isSetListener = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movies_fragment, container, false);
        ButterKnife.bind(this, v);
        swipeRefreshLayout.setOnRefreshListener(this);
        handler = new Handler();
        //((MainActivity) getActivity()).menuActivity.findItem(R.id.action_search).setVisible(false);
        pageNumber = 1;
        intent = new Intent(HANDLER_MESSAGE);
        database = new Database(getActivity());
        context = getContext();
        moviesList.setHasFixedSize(true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HANDLER_MESSAGE);
        intentFilter.addAction(CREATE_REQUEST);
        intentFilter.addAction(CHANGE_TOOLBAR);
        intentFilter.addAction(STARED_REMOVE);
        intentFilter.addAction(STARED_SAVE);
        intentFilter.addAction(HIDE_SEARCH);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case HANDLER_MESSAGE:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //   remove progress item
                                    movie.remove(movie.size() - 1);
                                    adapter.notifyItemRemoved(movie.size());
                                    //add items one by one
                                    Log.i("remove", "remove");

                                    if (requestDownMovies != null) {
                                        movie.addAll(requestDownMovies);
                                        adapter.notifyDataSetChanged();
                                    }
    //                        for (int i = start + 1; i < end; i++) {
    //                            movie.add(requestDownMovies.get(i));
    //                            adapter.notifyItemInserted(movie.size());
    //                        }
                                    adapter.setLoaded();
                                    //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case CREATE_REQUEST:
                            createRequest(1, true);
                            break;
                        case CHANGE_TOOLBAR:
                            Log.i("change", "change");
                            ((MainActivity)getActivity()).actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                            // Show back button
                            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            break;
                        case STARED_REMOVE:
                            Integer integerRemove = intent.getExtras().getInt("movie");
                            database.updateMovieData(integerRemove);
                            database.removeFromStaredData(integerRemove);
                            break;
                        case STARED_SAVE:
                            Integer integerSave = intent.getExtras().getInt("movie");
                            database.updateMovieData(integerSave);
                            database.saveStaredData(intent.getExtras().getByteArray("movieByte"), integerSave);
                            break;
                        case HIDE_SEARCH:
                            ((MainActivity) getActivity()).menuActivity.findItem(R.id.action_search).setVisible(false);
                            break;
                    }
                }
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);

        initMoviesList();

        return v;
    }

    private void initMoviesList() {
        //Database database = new Database(getActivity());
        firstPageMovies = database.getFirstPageMovies();
        if (firstPageMovies != null) {
            moviesList.setLayoutManager(new LinearLayoutManager(context));
            MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, firstPageMovies, moviesList);//
            moviesList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Log.i("movies", firstPageMovies.get(0).getOriginalTitle());
        }
        Log.i("pageNumberInit", pageNumber.toString());
        createRequest(pageNumber, false);
    }

    public void setListener(List<Movie> responseMovies) {
        if (firstPageMovies == null) {
            moviesList.setLayoutManager(new LinearLayoutManager(context));
        }
        Log.i("set", "set");
        //}
        if (responseMovies == null) {
            movie = new ArrayList<>();
        } else {
            movie = new ArrayList<>(responseMovies);
        }
        adapter = new MoviesFragmentAdapter(context, movie, moviesList);
        moviesList.setAdapter(adapter);
        //adapter.notifyDataSetChanged();//

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                createRequestDown(++pageNumber);
                Log.i("pageNumber", pageNumber.toString());
                movie.add(null);
                adapter.notifyItemInserted(movie.size() - 1);
            }
        });
    }

//    public void refreshGenresAdapter(List<Genre> genres) {
//        adapter.setGenres(genres);
//    }

    private void createRequestDown(final Integer page) {
        Log.i("req", "req");
        final NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        Constants.SORT_BY_POPULARITY_DESC,
                        page);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                if (page != null) {
                    List<Movie> movies = page.getResults();
                    //TODO
                    for (int i = 0; i < movies.size(); i++) {
                        Integer result = database.checkStaredData(movies.get(i).getId());
                        if (result == 1) {
                            movies.get(i).setSaved(true);
                        } else {
                            movies.get(i).setSaved(false);
                        }
                    }
                    database.saveMovieData(page);
                    requestDownMovies = movies;
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                //setListener(moviePage);
                Log.i("requestFail", "fail");
                requestDownMovies = null;
                --pageNumber;
                context.sendBroadcast(intent);
                Toast.makeText(context,"Отсутствует подключение к интернету.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRequest(Integer page, final Boolean isRefresh) {

        NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        Constants.SORT_BY_POPULARITY_DESC,
                        page);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                if (page != null) {
                    List<Movie> movies = page.getResults();
                    Log.i("page", page.getResults().get(0).getOriginalTitle());
                    //TODO
                    for (int i = 0; i < movies.size(); i++) {
                        Integer result = database.checkStaredData(movies.get(i).getId());
                        if (result == 1) {
                            movies.get(i).setSaved(true);
                        } else {
                            movies.get(i).setSaved(false);
                        }
                    }
                        database.saveMovieData(page);
                    Log.i("pageNumberNow", pageNumber.toString());
                        if (!isRefresh) {
                            setListener(movies);
                            isSetListener = true;
                        } else {
                            movie.clear();
                            movie.addAll(movies);
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                        }
                }
                //database.deleteDB();
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                //if (firstPageMovies != null) {
                if (!isSetListener) {
                    setListener(firstPageMovies);
                    isSetListener = true;
                }
                //}
                Toast.makeText(context,"Отсутствует подключение к интернету.", Toast.LENGTH_SHORT).show();
                if (isRefresh) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
    }

    @Override
    public void onRefresh() {
        pageNumber = 1;
        createRequest(pageNumber, true);
    }

}
