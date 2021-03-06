package ru.paul.moviesupport.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;

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
import ru.paul.moviesupport.OnLoadMoreListener;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.fragments.SearchMovieFragment;
import ru.paul.moviesupport.models.Movie;

public class MoviesFragmentAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context context;
    private List<Movie> movies;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;

    public MoviesFragmentAdapter(Context context, List<Movie> movies, RecyclerView recyclerView) {
        this.context = context;
        this.movies = movies;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return movies.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.movies_item, parent, false);

            vh = new MoviesViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MoviesViewHolder) {

            ((MoviesViewHolder) holder).materialRippleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.BACK_BUTTON_TOOLBAR);
                    Intent intentActivity = new Intent(MainActivity.OPEN_FRAGMENT);
                    intentActivity.putExtra("fragment", MainActivity.MOVIE_DETAIL_FRAGMENT);
                    intentActivity.putExtra("idMovie", movies.get(position).getId());
                    context.sendBroadcast(intentActivity);
                    context.sendBroadcast(intent);
                }
            });

            setImageStaredOrCommon(position, holder);

            ((MoviesViewHolder) holder).moviesImgSavedOrCommon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = movies.get(position);
                    Integer idMovie = movie.getId();
                    Fragment fragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.container);
                    Intent intent;
                    if (!movie.isSaved()) {
                        byte[] movieByte = SerializationUtils.serialize(movie);
                        if (fragment instanceof MovieFragment)
                            intent = new Intent(MovieFragment.STARED_SAVE);
                        else
                            intent = new Intent(SearchMovieFragment.STARED_SAVE);
                        intent.putExtra("movieByte", movieByte);
                        intent.putExtra("movie", idMovie);
                    } else {
                        if (fragment instanceof MovieFragment)
                            intent = new Intent(MovieFragment.STARED_REMOVE);
                        else
                            intent = new Intent(SearchMovieFragment.STARED_REMOVE);
                        intent.putExtra("movie", idMovie);
                    }
                    context.sendBroadcast(intent);

                    if (!movie.isSaved()) {
                        movie.setSaved(true);
                    } else {
                        movie.setSaved(false);
                    }
                    notifyItemChanged(position);

                }
            });

            Double textRatedDouble = (movies.get(position).getVoteAverage() * 10);
            Integer textRatedInt = textRatedDouble.intValue();
            String textRatedStr = String.valueOf(textRatedInt) + "%";
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Medium.ttf");
            ((MoviesViewHolder) holder).moviesTitle.setTypeface(typeface);

            String year = convertToString(movies.get(position).getReleaseDate());

            Picasso.get()
                    .load(Constants.IMAGE_URL + movies.get(position).getPosterPath())
                    .fit()
                    .into(((MoviesViewHolder) holder).moviesImg);
            ((MoviesViewHolder) holder).moviesTitle.setText(movies.get(position).getTitle());
            ((MoviesViewHolder) holder).moviesYear.setText(year);
            Picasso.get()
                    .load(R.drawable.vote)
                    .fit()
                    .into(((MoviesViewHolder) holder).moviesImgRated);
            ((MoviesViewHolder) holder).moviesTextRated.setText(textRatedStr);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        MaterialRippleLayout materialRippleLayout;
        ImageView moviesImg;
        TextView moviesTitle;
        TextView moviesYear;
        ImageView moviesImgRated;
        TextView moviesTextRated;
        ImageView moviesImgSavedOrCommon;


        MoviesViewHolder(View itemView) {
            super(itemView);
            materialRippleLayout = itemView.findViewById(R.id.movies_item);
            moviesImg = itemView.findViewById(R.id.movies_img);
            moviesTitle = itemView.findViewById(R.id.movies_title);
            moviesYear = itemView.findViewById(R.id.movies_year);
            moviesImgRated = itemView.findViewById(R.id.movies_img_rated);
            moviesTextRated = itemView.findViewById(R.id.movies_text_rated);
            moviesImgSavedOrCommon = itemView.findViewById(R.id.movies_img_saved_or_common);
        }
    }

    private String convertToString(String date) {

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        try {
            Date inputDate = inputFormat.parse(date);
            DateFormat outputFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.CANADA);
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
                .into(((MoviesViewHolder)holder).moviesImgSavedOrCommon);
    }


    class ProgressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar1)
        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void unregisterOnLoadMoreListener() {
        this.onLoadMoreListener = null;
    }

    public void setLoaded() {
        loading = false;
    }
}
