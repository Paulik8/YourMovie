package ru.paul.moviesupport;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.paul.moviesupport.models.Genres;
import ru.paul.moviesupport.models.MovieDetail;
import ru.paul.moviesupport.models.MoviePage;

public interface NetworkService {

    @GET("discover/movie/")
    Call<MoviePage> getPage(@Query("api_key") String apiKey,
                            @Query("language") String language,
                            @Query("sort_by") String sortOrder,
                            @Query("page") Integer page);

    @GET("genres/get-movie-list/")
    Call<Genres> getGenres(@Query("api_key") String apiKey,
                           @Query("language") String language);

    @GET("movie/{id}")
    Call<MovieDetail> getMovieDetail (@Path("id") int id,
                                      @Query("api_key") String apiKey,
                                      @Query("language") String language);

    @GET("search/movie")
    Call <MoviePage> getSearchPage (@Query("api_key") String apiKey,
                                    @Query("language") String language,
                                    @Query("query") String query,
                                    @Query("page") Integer page);
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
