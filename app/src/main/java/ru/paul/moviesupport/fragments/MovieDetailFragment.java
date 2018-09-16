package ru.paul.moviesupport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.Constants;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.NetworkService;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.models.MovieDetail;

public class MovieDetailFragment extends Fragment{

    @BindView(R.id.title)
    TextView detailTitle;
    MovieDetail movieDetail;
    Database database;

    public static final String TAG = "MovieDetailFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        ButterKnife.bind(this, v);
        database = new Database(getActivity());

        initActivity();
        //HIDE_SEARCH
        return v;
    }

    private void initActivity() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int id = bundle.getInt("id");
            movieDetail = database.getMovieDetailMovie(id);
            setInterface();
            createRequestDetail(id);
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

                Toast.makeText(getContext(),"Отсутствует подключение к интернету.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
