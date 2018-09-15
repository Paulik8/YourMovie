package ru.paul.moviesupport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import ru.paul.moviesupport.Database;
import ru.paul.moviesupport.R;

public class StaredMovieFragment extends Fragment {

    Database database;
    @BindView(R.id.stared_movies_list)
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stared_fragment, container, false);
        database = new Database(getActivity());
        initStaredList();
        return v;
    }

    private void initStaredList() {

    }
}
