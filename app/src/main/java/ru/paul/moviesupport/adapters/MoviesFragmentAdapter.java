package ru.paul.moviesupport.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.paul.moviesupport.MainActivity;
import ru.paul.moviesupport.MovieDetailActivity;
import ru.paul.moviesupport.OnLoadMoreListener;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.fragments.MovieFragment;
import ru.paul.moviesupport.models.Genre;
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
                                // End has been reached
                                // Do something
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MoviesViewHolder) {
            ((MoviesViewHolder) holder).text.setText(movies.get(position).getOriginalTitle());
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @OnClick(R.id.movies_item)
                void OnMoviesClick() {
            Intent intent = new Intent(MovieFragment.CHANGE_TOOLBAR);
            Intent intentActivity = new Intent(MainActivity.OPEN_FRAGMENT);
            intentActivity.putExtra("fragment", MainActivity.MOVIE_DETAIL_FRAGMENT);
            intentActivity.putExtra("idMovie", movies.get(getAdapterPosition()).getId());
            context.sendBroadcast(intent);
            context.sendBroadcast(intentActivity);
//            Log.i("pos", String.format("click on %d item", getAdapterPosition()));
//            Log.i("posTitle", String.format("click on %s title", movies.get(getAdapterPosition()).getTitle()));
//            Intent intent = new Intent(context, MovieDetailActivity.class);
//            intent.putExtra("idMovie", movies.get(getAdapterPosition()).getId());
//            context.startActivity(intent);
        }

        MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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
