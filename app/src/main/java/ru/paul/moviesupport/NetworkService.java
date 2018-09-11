package ru.paul.moviesupport;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.paul.moviesupport.models.MoviePage;

public interface NetworkService {

    @GET("discover/movie/")
    Call<MoviePage> getPage(@Query("api_key") String apiKey,
                            @Query("language") String language,
                            @Query("sort_by") String sortOrder,
                            @Query("page") Integer page);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
