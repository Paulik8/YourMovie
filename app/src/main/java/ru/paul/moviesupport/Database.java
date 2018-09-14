package ru.paul.moviesupport;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import ru.paul.moviesupport.entities.GenreData;
import ru.paul.moviesupport.entities.MovieData;
import ru.paul.moviesupport.entities.MovieDetailData;
import ru.paul.moviesupport.entities.MovieDetailData_;
import ru.paul.moviesupport.models.Genre;
import ru.paul.moviesupport.models.Movie;
import ru.paul.moviesupport.models.MovieDetail;
import ru.paul.moviesupport.models.MoviePage;

public class Database {

    private Activity activity;

    public Database(Activity activity) {
        this.activity = activity;
    }

    private Box<MovieData> getMovieDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(MovieData.class);
    }

    private Box<MovieDetailData> getMovieDetailDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(MovieDetailData.class);
    }

    private Box<GenreData> getGenreDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(GenreData.class);
    }

    private List<MovieData> getMovieData() {
        Box<MovieData> movieDataBox = getMovieDataBox();
        return movieDataBox.getAll();
    }

    private MovieDetailData getMovieDetailData(Integer id) {
        Box<MovieDetailData> movieDetailDataBox = getMovieDetailDataBox();
        return movieDetailDataBox.query().equal(MovieDetailData_.idMovie, id).build().findFirst();
    }

    public MovieDetail getMovieDetailMovie(Integer id) {
        Box<MovieDetailData> movieDetailDataBox = getMovieDetailDataBox();
        MovieDetailData movieDetailData = movieDetailDataBox.query().equal(MovieDetailData_.idMovie, id).build().findFirst();
        if (movieDetailData != null) {
            return SerializationUtils.deserialize(movieDetailData.getMovieDetail());
        } else
            return null;
    }

    public List<Movie> getFirstPageMovies() {//исправить, потому что когда открыл 2 страницы, покажет только первую, а должно показать сохраненные две
        if (getMovieData().size() > 0) {
            List<MovieData> movieData = getMovieData();
//            Movie movie0 = SerializationUtils.deserialize(getMovieData().get(0).getMovie());
//            Log.i("movieData[0]", movie0.getOriginalTitle());
//            Movie movie1 = SerializationUtils.deserialize(getMovieData().get(1).getMovie());
//            Log.i("movieData[1]", movie1.getOriginalTitle());
//            Movie movie19 = SerializationUtils.deserialize(getMovieData().get(19).getMovie());
//            Log.i("movieData[19]", movie19.getOriginalTitle());
//            if (getMovieData().size() > 20) {
//                movie19 = SerializationUtils.deserialize(getMovieData().get(19).getMovie());
//                Log.i("movieData[19]", movie19.getOriginalTitle());
//                Movie movie20 = SerializationUtils.deserialize(getMovieData().get(20).getMovie());
//                Log.i("movieData[20]", movie20.getOriginalTitle());
//                Movie movie21 = SerializationUtils.deserialize(getMovieData().get(21).getMovie());
//                Log.i("movieData[21]", movie21.getOriginalTitle());
//            }
            List<Movie> moviesFromDatabase = new ArrayList<>();
            for (int i = 0; i < movieData.size(); i++) {
                Movie movie = SerializationUtils.deserialize(movieData.get(i).getMovie());
                moviesFromDatabase.add(movie);
            }
            return moviesFromDatabase;
        }
        else
            return null;
    }

    public void saveMovieData(MoviePage moviePage) {
        List<Movie> movies = moviePage.getResults();
        List<MovieData> moviesToDatabase = new ArrayList<>();
        List<MovieData> movieData = getMovieData();
        if (movieData.size() > 0 && moviePage.getPage() == 1) {
            clearMovieData();
        }
        for (int i = 0; i < moviePage.getResults().size(); i++) {
            byte[] newMovie = SerializationUtils.serialize(movies.get(i));
            moviesToDatabase.add(new MovieData(i + 1 + getMovieData().size(), newMovie));
        }

        getMovieDataBox().put(moviesToDatabase);
        //clearMovieData();//очищение перед новыми экспериментами
    }

    public void saveMovieDetailData(MovieDetail movieDetail) {
        Box<MovieDetailData> box = getMovieDetailDataBox();
        byte[] newMovie = SerializationUtils.serialize((Serializable) movieDetail);
        MovieDetailData movieDetailData = getMovieDetailData(movieDetail.getIdMovie());
        if (movieDetailData != null) {
            movieDetailData.setIdMovie(movieDetail.getIdMovie());
            movieDetailData.setMovieDetail(newMovie);
            box.put(movieDetailData);
        } else {
            box.put(new MovieDetailData(0, movieDetail.getIdMovie(), newMovie));
        }
    }

    public List<GenreData> getGenreData() {
        Box<GenreData> genreDataBox = getGenreDataBox();
        return genreDataBox.getAll();
    }

    public List<Genre> getGenreList() {
        if (getGenreData() != null) {
            List<GenreData> genreData = getGenreData();
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < genreData.size(); i++) {
                genres.add(new Genre(genreData.get(i).getGenreId(), genreData.get(i).getGenreName()));
            }
            return genres;
        } else
            return null;
    }

    public void saveGenreData(List<Genre> genres) {
        List<GenreData> genreData = getGenreData();
        if (genreData != null) {
            getGenreDataBox().removeAll();
            genreData.clear();
        } else {
            genreData = new ArrayList<>();
        }
        for (int i = 0; i < genres.size(); i++) {
            genreData.add(new GenreData(i + 1, genres.get(i).getId(), genres.get(i).getName()));
        }
        getGenreDataBox().put(genreData);
    }

    private void clearMovieData() {
        getMovieDataBox().removeAll();
    }

    public void deleteDB() {
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().close();
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().deleteAllFiles();
    }
}
