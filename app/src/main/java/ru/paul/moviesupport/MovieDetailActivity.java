package ru.paul.moviesupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.models.MovieDetail;

public class MovieDetailActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_title)
    TextView detailTitle;
    MovieDetail movieDetail;
    Database database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        database = new Database(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initActivity();

    }

    private void initActivity() {
        Intent intent = getIntent();
        Database database = new Database(this);
        if (intent.getExtras() != null) {
            Integer idMovie = intent.getExtras().getInt("idMovie");
            movieDetail = database.getMovieDetailMovie(idMovie);
            setInterface();
            createRequestDetail(idMovie);
        }
    }

    private void setInterface() {
        if (movieDetail != null) {
            detailTitle.setText(movieDetail.getTitle());
        }
    }

    private void createRequestDetail(Integer id) {
        final NetworkService networkService = NetworkService.retrofit.create(NetworkService.class);
        Call<MovieDetail> call = networkService
                .getMovieDetail(id,
                        Constants.API_KEY,
                        Constants.LANGUAGE_ENUS);

        call.enqueue(new Callback<MovieDetail>() {

            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                movieDetail = response.body();
                database.saveMovieDetailData(movieDetail);
                setInterface();
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {

                Toast.makeText(getApplicationContext(),"Отсутствует подключение к интернету.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
