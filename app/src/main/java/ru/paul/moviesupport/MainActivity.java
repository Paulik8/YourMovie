package ru.paul.moviesupport;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.fragments.SearchMovieFragment;
import ru.paul.moviesupport.models.Genre;
import ru.paul.moviesupport.models.Genres;
import ru.paul.moviesupport.models.MoviePage;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty_view)
    TextView tvEmptyView;
    BroadcastReceiver receiver;
    FragmentManager fragmentManager;
    public Boolean isGenres = false;
    public List<Genre> gerneList = new ArrayList<>();

    static final String OPEN_FRAGMENT = "OPEN_FRAGMENT";
    static final String GENRES_REQUEST = "GENRES_REQUEST";

    static final String MOVIE_FRAGMENT = "MOVIES";


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        unregisterReceiver(receiver);
//    }

    static final String SEARCH_MOVIE_FRAGMENT = "SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        setSupportActionBar(toolbar);

        createNavigationDrawer();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPEN_FRAGMENT);
        intentFilter.addAction(GENRES_REQUEST);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case OPEN_FRAGMENT:
                            openFragment(intent.getStringExtra("fragment"));
                        break;
                        case GENRES_REQUEST:
                            createGenresRequest();
                        break;
                    }
                }
            }
        };
        registerReceiver(receiver, intentFilter);

        //createGenresRequest();

        Intent startIntent = new Intent(OPEN_FRAGMENT);
        startIntent.putExtra("fragment", MOVIE_FRAGMENT);
        sendBroadcast(startIntent);
    }

    private void createNavigationDrawer() {
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar);
        navigationDrawer.initNavigationDrawer();
    }

    public void openFragment(String name) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        switch(name) {
            case MOVIE_FRAGMENT:
                if (!(fragment instanceof MovieFragment)) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, new MovieFragment(), MovieFragment.TAG)
                            .commit();
                }
                break;
            case SEARCH_MOVIE_FRAGMENT:
                if (!(fragment instanceof SearchMovieFragment)) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, new SearchMovieFragment(), SearchMovieFragment.TAG)
                            .commit();
                }
                break;
        }
    }

    private void createGenresRequest() {
        Log.i("req", "req");
        final NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<Genres> call = networkService
                .getGenres(Constants.API_KEY,
                        Constants.LANGUAGE_ENUS);

        call.enqueue(new Callback<Genres>() {

            @Override
            public void onResponse(@NonNull Call<Genres> call, @NonNull Response<Genres> response) {
                if (response.body() != null) {
                    Database database = new Database((Activity) getApplicationContext());
                    if (database.getGenreData() != null) {
                        gerneList = new ArrayList<>(response.body().getGenres());
                    }
                    isGenres = true;

                    database.saveGenreData(gerneList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Genres> call, @NonNull Throwable t) {
                Database database = new Database((Activity) getApplicationContext());
                if (database.getGenreData() != null) {
                    gerneList = new ArrayList<>(database.getGenreList());
                }
                Toast.makeText(getApplicationContext(),"Отсутствует подключение к интернету.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
