package ru.paul.moviesupport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.fragments.SearchMovieFragment;
import ru.paul.moviesupport.models.MoviePage;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    BroadcastReceiver receiver;
    FragmentManager fragmentManager;

    static final String OPEN_FRAGMENT = "OPEN_FRAGMENT";

    static final String MOVIE_FRAGMENT = "MOVIES";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregisterReceiver(receiver);
    }

    static final String SEARCH_MOVIE_FRAGMENT = "SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createNavigationDrawer();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPEN_FRAGMENT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case OPEN_FRAGMENT:
                            openFragment(intent.getStringExtra("fragment"));
                    }
                }
            }
        };
        registerReceiver(receiver, intentFilter);

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
}
