package ru.paul.moviesupport;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.fragments.MovieDetailFragment;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.fragments.SearchMovieFragment;
import ru.paul.moviesupport.models.Genre;
import ru.paul.moviesupport.models.Genres;
import ru.paul.moviesupport.models.MoviePage;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.empty_view)
    TextView tvEmptyView;
    BroadcastReceiver receiver;
    public NavigationDrawer navigationDrawer;
    FragmentManager fragmentManager;
    public Boolean isGenres = false;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public List<Genre> gerneList = new ArrayList<>();

    public static final String OPEN_FRAGMENT = "OPEN_FRAGMENT";
    static final String GENRES_REQUEST = "GENRES_REQUEST";

    static final String MOVIE_FRAGMENT = "MOVIES";
    public static final String MOVIE_DETAIL_FRAGMENT = "MOVIE_DETAIL";
    static final String SEARCH_MOVIE_FRAGMENT = "SEARCH";
    static final String STARED_MOVIE_FRAGMENT = "STARED";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Show hamburger
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle (this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setupDrawerContent(navigationView);
        actionBarDrawerToggle.syncState();
        //navigationView.setNavigationItemSelectedListener(this);

        //createNavigationDrawer();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPEN_FRAGMENT);
        intentFilter.addAction(GENRES_REQUEST);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case OPEN_FRAGMENT:
                            openFragment(intent);
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                Intent intentMovies = new Intent(OPEN_FRAGMENT);
                intentMovies.putExtra("fragment", MOVIE_FRAGMENT);
                this.sendBroadcast(intentMovies);
                break;
            case R.id.nav_second_fragment:
                Intent intentSearch = new Intent(OPEN_FRAGMENT);
                intentSearch.putExtra("fragment", SEARCH_MOVIE_FRAGMENT);
                this.sendBroadcast(intentSearch);
                break;
            case R.id.nav_third_fragment:
                Intent intentStared = new Intent(OPEN_FRAGMENT);
                intentStared.putExtra("fragment", STARED_MOVIE_FRAGMENT);
                this.sendBroadcast(intentStared);
                break;
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }


//    private void createNavigationDrawer() {
//        navigationDrawer = new NavigationDrawer(this, toolbar);
//        navigationDrawer.initNavigationDrawer();
//    }

    public void openFragment(Intent intent) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);

        switch(intent.getStringExtra("fragment")) {
            case MOVIE_FRAGMENT:
                if (!(fragment instanceof MovieFragment)) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, new MovieFragment(), MovieFragment.TAG)
                            .commit();
                }
                break;
            case MOVIE_DETAIL_FRAGMENT:
                if (!(fragment instanceof MovieDetailFragment)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", intent.getExtras().getInt("idMovie"));
                    MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                    movieDetailFragment.setArguments(bundle);
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, movieDetailFragment, MovieDetailFragment.TAG)
                            .addToBackStack(null)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}
