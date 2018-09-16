package ru.paul.moviesupport;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import ru.paul.moviesupport.entities.GenreData;
import ru.paul.moviesupport.entities.MovieData;
import ru.paul.moviesupport.entities.MovieData_;
import ru.paul.moviesupport.entities.MovieDetailData;
import ru.paul.moviesupport.entities.MovieDetailData_;
import ru.paul.moviesupport.entities.SearchData;
import ru.paul.moviesupport.entities.SearchData_;
import ru.paul.moviesupport.entities.StaredData;
import ru.paul.moviesupport.entities.StaredData_;
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

    private Box<StaredData> getStaredDataBox() {
        return ((MovieSupportApplication)activity.getApplication()).getBoxStore().boxFor(StaredData.class);
    }

    private Box<GenreData> getGenreDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(GenreData.class);
    }

    private Box<SearchData> getSearchDataBox() {
        return ((MovieSupportApplication) activity.getApplication()).getBoxStore().boxFor(SearchData.class);
    }

    private List<MovieData> getMovieData() {
        Box<MovieData> movieDataBox = getMovieDataBox();
        return movieDataBox.getAll();
    }

    private List<SearchData> getSearchData() {
        Box<SearchData> box = getSearchDataBox();
        return  box.getAll();
    }

    private StaredData getStaredOneData(Integer id) {
        Box<StaredData> staredDataBox = getStaredDataBox();
        return staredDataBox.query().equal(StaredData_.movieId, id).build().findFirst();
    }

    public Integer checkStaredData(Integer id) {
        Movie movieFromDatabase;
        Box<StaredData> movieDataBox = getStaredDataBox();
        Query<StaredData> query =  movieDataBox.query().equal(StaredData_.movieId, id).build();
        StaredData movieDataFromDatabase = query.findFirst();
        if (movieDataFromDatabase != null) {
            movieFromDatabase = SerializationUtils.deserialize(movieDataFromDatabase.getMovie());
            if (movieFromDatabase.isSaved()) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    public void saveSearchData(MoviePage moviePage) {
        List<Movie> movies = moviePage.getResults();
        List<SearchData> moviesToDatabase = new ArrayList<>();
        List<SearchData> movieData = getSearchData();
        if (movieData.size() > 0 && moviePage.getPage() == 1) {
            clearSearchData();
        }
        for (int i = 0; i < moviePage.getResults().size(); i++) {
            byte[] newMovie = SerializationUtils.serialize(movies.get(i));
            moviesToDatabase.add(new SearchData(i + 1 + getSearchData().size(), movies.get(i).getId(), newMovie));
        }

        getSearchDataBox().put(moviesToDatabase);
        //clearSearchData();
    }

    public List<Movie> getMoviesSearch() {
        if (getSearchData().size() > 0) {
            List<SearchData> movieData = getSearchData();
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

    public void updateSearchData(Integer id) {
        Movie movieFromDatabase;
        Box<SearchData> searchDataBox = getSearchDataBox();
        //byte [] movieByte = SerializationUtils.serialize(movie);
        Query<SearchData> query =  searchDataBox.query().equal(SearchData_.searchIdMovie, id).build();
        SearchData movieDataFromDatabase = query.findFirst();
        if (movieDataFromDatabase == null) {
            return;
        }
        movieFromDatabase = SerializationUtils.deserialize(movieDataFromDatabase.getMovie());
        if (!movieFromDatabase.isSaved()) {
            movieFromDatabase.setSaved(true);
        } else {
            movieFromDatabase.setSaved(false);
        }
        byte[] movieByteToDatabase = SerializationUtils.serialize(movieFromDatabase);
        movieDataFromDatabase.setMovie(movieByteToDatabase);
        searchDataBox.put(movieDataFromDatabase);
        Log.i("tag","tag");
    }

    public void updateMovieData(Integer id) {
        Movie movieFromDatabase;
        Box<MovieData> movieDataBox = getMovieDataBox();
        //byte [] movieByte = SerializationUtils.serialize(movie);
        Query<MovieData> query =  movieDataBox.query().equal(MovieData_.idOfMovie, id).build();
        MovieData movieDataFromDatabase = query.findFirst();
        if (movieDataFromDatabase == null) {
            return;
        }
        movieFromDatabase = SerializationUtils.deserialize(movieDataFromDatabase.getMovie());
        if (!movieFromDatabase.isSaved()) {
            movieFromDatabase.setSaved(true);
        } else {
            movieFromDatabase.setSaved(false);
        }
        byte[] movieByteToDatabase = SerializationUtils.serialize(movieFromDatabase);
        movieDataFromDatabase.setMovie(movieByteToDatabase);
        movieDataBox.put(movieDataFromDatabase);
        Log.i("tag","tag");
    }

    private List<StaredData> getStaredData() {
        return getStaredDataBox().getAll();
    }

    public List<Movie> getStaredMovies() {
        List<StaredData> staredData = getStaredData();
        List<Movie> movies = new ArrayList<>();
        if (staredData != null) {
            for (int i = 0; i < staredData.size(); i++) {
                Movie movie = SerializationUtils.deserialize(staredData.get(i).getMovie());
                movies.add(movie);
            }
        }
        return movies;
    }

    public void saveStaredData(byte[] movieByte, Integer id) {
        Box<StaredData> box = getStaredDataBox();
        Movie movie = SerializationUtils.deserialize(movieByte);
        if (!movie.isSaved()) {
            movie.setSaved(true);
        } else {
            movie.setSaved(false);
        }
        byte[] movieByteToDatabase = SerializationUtils.serialize(movie);
        box.put(new StaredData(0, id, movieByteToDatabase));
        //clearStaredData();
    }

    public void removeFromStaredData(Integer id) {
        StaredData staredData = getStaredOneData(id);
        if (staredData != null) {
            getStaredDataBox().remove(staredData);
        }
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
            moviesToDatabase.add(new MovieData(i + 1 + getMovieData().size(), movies.get(i).getId(), newMovie));
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
        //clearMovieDetailData();
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

    public void clearMovieData() {
        getMovieDataBox().removeAll();
    }
    public void clearSearchData() {
        getSearchDataBox().removeAll();
    }
    public void clearStaredData() {
        getStaredDataBox().removeAll();
    }
    public void clearMovieDetailData() {
        getMovieDataBox().removeAll();
    }

    public void deleteDB() {
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().close();
        ((MovieSupportApplication) activity.getApplication()).getBoxStore().deleteAllFiles();
    }
}
