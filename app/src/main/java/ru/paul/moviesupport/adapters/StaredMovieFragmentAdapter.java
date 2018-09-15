package ru.paul.moviesupport.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.models.Movie;

public class StaredMovieFragmentAdapter extends RecyclerView.Adapter{

    Context context;
    List<Movie> movies;

    public StaredMovieFragmentAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public StaredMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false);
        return new StaredMovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class StaredMovieViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.movies_img)
        ImageView moviesImg;
        @BindView(R.id.movies_title)
        TextView moviesTitle;
        @BindView(R.id.movies_year)
        TextView moviesYear;
        @BindView(R.id.movies_img_rated)
        ImageView moviesImgRated;
        @BindView(R.id.movies_text_rated)
        TextView moviesTextRated;


        StaredMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }
}
