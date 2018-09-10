package ru.paul.moviesupport.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.paul.moviesupport.R;
import ru.paul.moviesupport.models.Movie;

public class MoviesFragmentAdapter extends RecyclerView.Adapter<MoviesFragmentAdapter.MoviesViewHolder> {

    private Context context;
    private List<Movie> movies;

    public MoviesFragmentAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false);
        return new MoviesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.text.setText(String.valueOf(movies.get(position).getId()));
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

        MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
