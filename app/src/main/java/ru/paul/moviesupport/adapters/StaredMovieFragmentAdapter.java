package ru.paul.moviesupport.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.Constants;
import ru.paul.moviesupport.MainActivity;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.fragments.StaredMovieFragment;
import ru.paul.moviesupport.models.Movie;

public class StaredMovieFragmentAdapter extends RecyclerView.Adapter{

    private Context context;
    private List<Movie> movies;

    public StaredMovieFragmentAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false);
        return new StaredMovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((StaredMovieViewHolder) holder).materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieFragment.CHANGE_TOOLBAR);
                Intent intentActivity = new Intent(MainActivity.OPEN_FRAGMENT);
                intentActivity.putExtra("fragment", MainActivity.MOVIE_DETAIL_FRAGMENT);
                intentActivity.putExtra("idMovie", movies.get(position).getId());
                context.sendBroadcast(intent);
                context.sendBroadcast(intentActivity);
            }
        });

        setImageStaredOrCommon(position, holder);

        Double textRatedDouble = (movies.get(position).getVoteAverage() * 10);
        Integer textRatedInt = textRatedDouble.intValue();
        String textRatedStr = String.valueOf(textRatedInt) + "%";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Medium.ttf");
        ((StaredMovieViewHolder) holder).moviesTitle.setTypeface(typeface);

        String year = convertToString(movies.get(position).getReleaseDate());

        Picasso.get()
                .load(Constants.IMAGE_URL + movies.get(position).getPosterPath())
                .fit()
                .into(((StaredMovieViewHolder) holder).moviesImg);
        ((StaredMovieViewHolder) holder).moviesTitle.setText(movies.get(position).getTitle());
        ((StaredMovieViewHolder) holder).moviesYear.setText(year);
        Picasso.get()
                .load(R.drawable.vote)
                .fit()
                .into(((StaredMovieViewHolder) holder).moviesImgRated);
        ((StaredMovieViewHolder) holder).moviesTextRated.setText(textRatedStr);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class StaredMovieViewHolder extends RecyclerView.ViewHolder{

        //@BindView(R.id.movies_item)
        MaterialRippleLayout materialRippleLayout;
        //@BindView(R.id.movies_img)
        ImageView moviesImg;
        //@BindView(R.id.movies_title)
        TextView moviesTitle;
        //@BindView(R.id.movies_year)
        TextView moviesYear;
        //@BindView(R.id.movies_img_rated)
        ImageView moviesImgRated;
        //@BindView(R.id.movies_text_rated)
        TextView moviesTextRated;
        //@BindView(R.id.movies_img_saved_or_common)
        ImageView moviesImgSavedOrCommon;


        StaredMovieViewHolder(View itemView) {
            super(itemView);
            materialRippleLayout = itemView.findViewById(R.id.movies_item);
            moviesImg = itemView.findViewById(R.id.movies_img);
            moviesTitle = itemView.findViewById(R.id.movies_title);
            moviesYear = itemView.findViewById(R.id.movies_year);
            moviesImgRated = itemView.findViewById(R.id.movies_img_rated);
            moviesTextRated = itemView.findViewById(R.id.movies_text_rated);
            moviesImgSavedOrCommon = itemView.findViewById(R.id.movies_img_saved_or_common);
            //ButterKnife.bind(itemView);
        }
    }

    private String convertToString(String date) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date inputDate = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd MMMMMMMMM yyyy", Locale.US);
            return outputFormat.format(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setImageStaredOrCommon(Integer pos, RecyclerView.ViewHolder holder) {
        Integer image;
        Movie movie = movies.get(pos);
        if (!movie.isSaved()) {
            image = R.drawable.common_img;
        } else {
            image = R.drawable.stared_img;
        }
        Picasso.get()
                .load(image)
                .fit()
                .into(((StaredMovieViewHolder)holder).moviesImgSavedOrCommon);
    }
}
