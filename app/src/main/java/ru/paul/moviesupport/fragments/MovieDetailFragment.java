package ru.paul.moviesupport.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.paul.moviesupport.Constants;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.MainActivity;
import ru.paul.moviesupport.NetworkService;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.models.MovieDetail;

public class MovieDetailFragment extends Fragment{

    @BindView(R.id.detail_backdrop_img)
    ImageView detailBackdropImg;
    @BindView(R.id.detail_poster_img)
    ImageView detaiPosterImg;
    @BindView(R.id.detail_heart)
    ImageView detaiHeartImg;
    @BindView(R.id.detail_common_or_stared_img)
    ImageView detailCommonOrStaredImg;
    @BindView(R.id.detail_title)
    TextView detailTitle;
    @BindView(R.id.detail_year)
    TextView detailYear;
    @BindView(R.id.detail_vote)
    TextView detailVote;
    @BindView(R.id.detail_user_score)
    TextView detailUserScore;
    @BindView(R.id.detail_budget)
    TextView detailBudget;
    @BindView(R.id.detail_budget_value)
    TextView detailBudgetValue;
    @BindView(R.id.detail_revenue)
    TextView detailRevenue;
    @BindView(R.id.detail_revenue_value)
    TextView detailRevenueValue;
    @BindView(R.id.detail_tagline)
    TextView detailTagline;
    @BindView(R.id.detail_overview)
    TextView detailOverview;
    @BindView(R.id.detail_overview_value)
    TextView detailOverviewValue;

    MovieDetail movieDetail;
    Database database;
    Context context;

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
        context = getContext();

        initDetailMovie();
        Intent intentHide = new Intent(MainActivity.HIDE_SEARCH);
        context.sendBroadcast(intentHide);
        return v;
    }

    private void initDetailMovie() {
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
            Integer image;

            Integer isSaved = database.checkStaredData(movieDetail.getIdMovie());
            if (isSaved == 1) {
                image = R.drawable.stared_img;
            } else {
                image = R.drawable.star_white;
            }
            Picasso.get()
                    .load(image)
                    .fit()
                    .into(detailCommonOrStaredImg);

            Picasso.get()
                    .load(Constants.IMAGE_URL + movieDetail.getBackdropPath())
                    .centerCrop()
                    .fit()
                    .into(detailBackdropImg);

            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(6)
                    .build();
            Picasso.get()
                    .load(Constants.IMAGE_URL + movieDetail.getPosterPath())
                    .transform(transformation)
                    .fit()
                    .into(detaiPosterImg);
            Picasso.get()
                    .load(R.drawable.heart_icon)
                    .fit()
                    .into(detaiHeartImg);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
            detailTitle.setTypeface(typeface);
            detailTitle.setText(movieDetail.getTitle());

            Double textRatedDouble = (movieDetail.getVoteAverage() * 10);
            Integer textRatedInt = textRatedDouble.intValue();
            String textRatedStr = String.valueOf(textRatedInt) + "%";
            detailVote.setText(textRatedStr);

            Typeface typefaceUserScore = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Medium.ttf");
            detailUserScore.setTypeface(typefaceUserScore);

            String budget = "Budget";
            detailBudget.setTypeface(typefaceUserScore);
            detailBudget.setText(budget);

            detailBudgetValue.setText(changeToPattern(movieDetail.getBudget()));

            detailRevenue.setTypeface(typefaceUserScore);
            String revenue = "Revenue";
            detailRevenue.setText(revenue);

            detailRevenueValue.setText(changeToPattern(movieDetail.getRevenue()));

            Typeface typefaceTagline = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Italic.ttf");
            detailTagline.setTypeface(typefaceTagline);
            detailTagline.setText(movieDetail.getTagline());

            String overview = "Overview";
            detailOverview.setTypeface(typefaceUserScore);
            detailOverview.setText(overview);

            detailOverviewValue.setText(movieDetail.getOverview());

            detailUserScore.setTypeface(typefaceUserScore);
            String userScore = "User\nScore";
            detailUserScore.setText(userScore);

            detailYear.setText(convertToString(movieDetail.getReleaseDate()));
        }
    }

    private String changeToPattern(Integer budget) {
        String str = String.valueOf(budget);
        Integer symbols = str.length();
        List<Integer> arr = new ArrayList<>();
        Integer steps = symbols / 3;
        if (symbols % 3 == 0)
            steps--;
        for (int i = 0; i < steps; i++) {
            arr.add(symbols - 3 * (i + 1));
            str = str.substring(0, arr.get(i)) + "." + str.substring(arr.get(i), str.length());
        }

        return "$ " + str;
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

                Toast.makeText(getContext(),"Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String convertToString(String date) {

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        //DateFormat inputFormat = DateFormat.getDateInstance();
        try {
            Date inputDate = inputFormat.parse(date);
            //DateFormat outputFormat = new SimpleDateFormat("dd MMMMMMMMM yyyy", Locale.US);
            DateFormat outputFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.CANADA);
            String outputDate = outputFormat.format(inputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
