package ru.paul.moviesupport;

import android.app.Application;

import io.objectbox.BoxStore;
import ru.paul.moviesupport.entities.MyObjectBox;

public class MovieSupportApplication extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(MovieSupportApplication.this).build();
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
