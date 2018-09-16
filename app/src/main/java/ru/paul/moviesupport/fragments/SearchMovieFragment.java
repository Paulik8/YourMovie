package ru.paul.moviesupport.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

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

public class SearchMovieFragment extends Fragment {

    @BindView(R.id.search_movies_list)
    RecyclerView recyclerView;
    Context context;
    Database database;
    Handler handler;
    Integer pageNumber;
    Intent intent;
    String query;
    BroadcastReceiver receiver;
    List<Movie> movie;
    List<Movie> moviesFromDatabase;
    List<Movie> requestDownMovies;
    MoviesFragmentAdapter adapter;

    static final String HANDLER_MESSAGE = "HANDLER_MESSAGE";
    static final String CHANGE_TOOLBAR = "CHANGE_TOOLBAR";
    static final String SHOW_SEARCH = "SHOW_SEARCH";
    public static final String SEARCH = "SEARCH";
    public static final String STARED_REMOVE = "STARED_REMOVE";
    public static final String STARED_SAVE = "STARED_SAVE";

    public static String TAG = "SearchMovieFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("search", "search");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_movies_fragment, container, false);
        ButterKnife.bind(this, v);
        database = new Database(getActivity());
        context = getContext();
        handler = new Handler();
        //((MainActivity) getActivity()).menuActivity.findItem(R.id.action_search).setVisible(false);
        pageNumber = 1;
        intent = new Intent(HANDLER_MESSAGE);
        database = new Database(getActivity());
        context = getContext();
        recyclerView.setHasFixedSize(true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HANDLER_MESSAGE);
        intentFilter.addAction(CHANGE_TOOLBAR);
        intentFilter.addAction(SHOW_SEARCH);
        intentFilter.addAction(SEARCH);
        intentFilter.addAction(STARED_REMOVE);
        intentFilter.addAction(STARED_SAVE);

        receiver = new BroadcastReceiver() {
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
                                    adapter.setLoaded();
                                }
                            });
                            break;
                        case STARED_REMOVE:
                            Integer integerRemove = intent.getExtras().getInt("movie");
                            database.updateMovieData(integerRemove);
                            database.updateSearchData(integerRemove);
                            database.removeFromStaredData(integerRemove);
                            break;
                        case STARED_SAVE:
                            Integer integerSave = intent.getExtras().getInt("movie");
                            database.updateMovieData(integerSave);
                            database.updateSearchData(integerSave);
                            database.saveStaredData(intent.getExtras().getByteArray("movieByte"), integerSave);
                            break;
                        case CHANGE_TOOLBAR:
                            ((MainActivity)getActivity()).actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                            // Show back button
                            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            break;
                        case SHOW_SEARCH:
                            ((MainActivity) getActivity()).menuActivity.findItem(R.id.action_search).setVisible(true);
                            break;
                        case SEARCH:
                            query = null;
                            query = intent.getExtras().getString("query");

                            pageNumber = 1;
                            createRequest(pageNumber, query);
                            break;
                    }
                }
            }
        };
        context.registerReceiver(receiver, intentFilter);
        Intent intentShow = new Intent(SHOW_SEARCH);
        context.sendBroadcast(intentShow);
        initSearchMovies();

        return v;
    }

    private void initSearchMovies() {
        moviesFromDatabase = database.getMoviesSearch();
        if (moviesFromDatabase != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            MoviesFragmentAdapter adapter = new MoviesFragmentAdapter(context, moviesFromDatabase, recyclerView);
            recyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
        setListener(moviesFromDatabase);
    }

    public void setListener(List<Movie> responseMovies) {
        if (moviesFromDatabase == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        Log.i("set", "set");
        //}
        if (responseMovies == null) {
            movie = new ArrayList<>();
        } else {
            movie = new ArrayList<>(responseMovies);
        }
        adapter = new MoviesFragmentAdapter(context, movie, recyclerView);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();//

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                createRequestDown(++pageNumber, query);
                Log.i("pageNumber", pageNumber.toString());
                movie.add(null);
                adapter.notifyItemInserted(movie.size() - 1);
            }
        });
    }

    private void createRequest(Integer page, String query) {

        if (query == null) {
            return;
        }

        NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getSearchPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        query,
                        page);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                if (page != null && page.getResults().size() > 0) {
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
                        database.saveSearchData(page);
                        Log.i("pageNumberNow", pageNumber.toString());

                        movie.clear();
                        movie.addAll(movies);
                        adapter.notifyDataSetChanged();
                } else {
                    searchNothing();
                    Toast.makeText(getActivity().getApplicationContext(), "No matches were found.", Toast.LENGTH_SHORT).show();
                }
                //database.deleteDB();
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                searchNothing();
                Toast.makeText(getActivity().getApplicationContext(), "Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchNothing() {
        database.clearSearchData();
        movie.clear();
        adapter.notifyDataSetChanged();
    }

    private void createRequestDown(final Integer page, String query) {
        Log.i("req", "req");
        if (query == null) {
            requestDownMovies = null;
            context.sendBroadcast(intent);
            return;
        }
        final NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MoviePage> call = networkService
                .getSearchPage(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS,
                        query,
                        page);

        call.enqueue(new Callback<MoviePage>() {

            @Override
            public void onResponse(@NonNull Call<MoviePage> call, @NonNull Response<MoviePage> response) {
                MoviePage page = response.body();
                if (page != null && page.getResults().size() > 0)  {
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
                        database.saveSearchData(page);
                        requestDownMovies = movies;
                } else {
                    requestDownMovies = null;
                }
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(@NonNull Call<MoviePage> call, @NonNull Throwable t) {
                //setListener(moviePage);
                Log.i("requestFail", "fail");
                requestDownMovies = null;
                --pageNumber;
                context.sendBroadcast(intent);
                Toast.makeText(getActivity().getApplicationContext(),"Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(receiver);
        if (adapter != null) {
            adapter.unregisterOnLoadMoreListener();
        }
    }
}
