package ru.paul.moviesupport;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class NavigationDrawer implements Drawer.OnDrawerItemClickListener{

    private Activity activity;
    Drawer drawer;
    Toolbar toolbar;

    NavigationDrawer(Activity activity, Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
    }

    public void initNavigationDrawer() {

        drawer = new Drawer()
                .withActivity(activity)
                .withToolbar(toolbar)
                //.withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_movies_list).withIcon(FontAwesome.Icon.faw_film).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_search_movie).withIcon(FontAwesome.Icon.faw_search).withIdentifier(2));
        drawer.build();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
        switch (drawerItem.getIdentifier()) {
            case 1:
                Intent intentMovie = new Intent(MainActivity.OPEN_FRAGMENT);
                intentMovie.putExtra("fragment", MainActivity.MOVIE_FRAGMENT);
                activity.sendBroadcast(intentMovie);
                break;
            case 2:
                Intent intentSearch = new Intent(MainActivity.OPEN_FRAGMENT);
                intentSearch.putExtra("fragment", MainActivity.SEARCH_MOVIE_FRAGMENT);
                activity.sendBroadcast(intentSearch);
                break;
        }
    }
}
