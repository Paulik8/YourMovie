package ru.paul.moviesupport;

import android.app.Activity;

import io.objectbox.Box;
import ru.paul.moviesupport.entities.MovieData;

public class Database {

    private Activity activity;

    public Database(Activity activity) {
        this.activity = activity;
    }

    private Box<MovieData> getMovieDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(MovieData.class);
    }
}
