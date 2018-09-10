package ru.paul.moviesupport;

import android.app.Activity;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

import io.objectbox.Box;
import ru.paul.moviesupport.entities.MovieData;
import ru.paul.moviesupport.models.Movie;
import ru.paul.moviesupport.models.MoviePage;

public class Database {

    private Activity activity;

    public Database(Activity activity) {
        this.activity = activity;
    }

    private Box<MovieData> getMovieDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(MovieData.class);
    }

    private List<MovieData> getMovieData() {
        Box<MovieData> movieDataBox = getMovieDataBox();
        return movieDataBox.getAll();
    }

    public MoviePage getMoviePage() {//исправить, потому что когда открыл 2 страницы, покажет только первую, а должно показать сохраненные две
        if (getMovieData().size() > 0) {
            MovieData movieData = getMovieData().get(0);
            return SerializationUtils.deserialize(movieData.getPage());
        }
        else
            return null;
    }

    public void saveMovieData(MoviePage moviePage) {
        byte[] newPage = SerializationUtils.serialize(moviePage);
        MovieData newMovieData;
        List<MovieData> movieData = getMovieData();
        if (movieData.size() >= moviePage.getPage()) {
            clearMovieData();
        }
        newMovieData = new MovieData(moviePage.getPage() - 1, newPage);
        getMovieDataBox().put(newMovieData);
        //clearMovieData();//очищение перед новыми экспериментами
    }

    private void clearMovieData() {
        getMovieDataBox().removeAll();
    }

    public void deleteDB() {
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().close();
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().deleteAllFiles();
    }
}
