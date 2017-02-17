package com.jogoler.jogolmapsunsri.other;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.adapter.LocationListAdapter;
import com.jogoler.jogolmapsunsri.database.LocationQuery;
import com.jogoler.jogolmapsunsri.model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by RazorX on 12/17/2016.
 */

public class CustomFloatingSearchView {
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView mSearchView;

    private RecyclerView mSearchResultsList;
    private LocationListAdapter mSearchResultsAdapter;
    private LocationQuery locationQuery;
    private List<Location> locationList;

    private Context context;
    private Double currentLatitude;
    private Double currentLongitude;
    private String mLastQuery = "";
    private int PAGE_SIZE;
    private DrawerLayout drawer;

    public CustomFloatingSearchView(Context context,FloatingSearchView view,
                                    RecyclerView mSearchResultsList,
                                    LocationListAdapter mSearchResultsAdapter,
                                    LocationQuery locationQuery,
                                    List<Location> locationList,
                                    int PAGE_SIZE,DrawerLayout drawer){
        this.context = context;
        this.mSearchView = view;
        this.mSearchResultsList = mSearchResultsList;
        this.mSearchResultsAdapter = mSearchResultsAdapter;
        this.locationQuery = locationQuery;
        this.locationList = locationList;
        this.PAGE_SIZE = PAGE_SIZE;
        this.drawer = drawer;
        setupFloatingSearch();
        setupDrawer();
    }

    private void setupDrawer() {
        mSearchView.attachNavigationDrawerToMenuButton(drawer);
    }

    public void setCurrentLocation(Double currentLatitude,Double currentLongitude){
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    private void loadData(Cursor cursor){
        int index = 0;
        int end = index + PAGE_SIZE;
        locationList.clear();
        Location location;
        HaversineDistance haversineDistance;
        if (cursor!=null) {
            cursor.moveToPosition(index);
            do {
                location = new Location();
                location.setId(cursor.getInt(0));
                location.setThumb(cursor.getString(1));
                location.setName(cursor.getString(2));
                //calculate distance for near place
                haversineDistance = new HaversineDistance(context);
                Double refLatitude = cursor.getDouble(3);
                Double refLongitude = cursor.getDouble(4);
                Double distance = haversineDistance.calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                location.setDistance(distance);
                locationList.add(location);
            }while (cursor.moveToNext() && cursor.getPosition()<end);
        }

        mSearchResultsAdapter.swapData(locationList);
        //no more data can be loaded.
        if(mSearchResultsAdapter.getItemCount()-1>= locationList.size()){
            if(mSearchResultsAdapter.isLoading()){
                Toast.makeText(context, "All data has been loaded", Toast.LENGTH_SHORT).show();
                mSearchResultsAdapter.stopLoading();
                mSearchResultsAdapter.setLoading(false);
            }
        }
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();
                    locationList = new ArrayList<>();
                    Location location;
                    //simulates a query call to a data source
                    //with a new query.
                    Cursor cursor = locationQuery.getSuggestionLocation(newQuery," LIMIT 5 ");
                    if (cursor!=null) {
                        cursor.moveToFirst();
                        do {
                            location = new Location();
                            location.setId(cursor.getInt(0));
                            location.setName(cursor.getString(1));
                            locationList.add(location);
                        }while (cursor.moveToNext());
                    }
                            mSearchView.swapSuggestions(locationList);

                            //let the users know that the background
                            //process has completed
                            mSearchView.hideProgress();

                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                Location locationSuggestion = (Location) searchSuggestion;
                Cursor cursor = locationQuery.getLocationListByKeyword(locationSuggestion.getBody());
                loadData(cursor);
               /* locationList = new ArrayList<>();
                Location location;
                HaversineDistance haversineDistance;
                if (cursor!=null) {
                    cursor.moveToFirst();
                    do {
                        location = new Location();
                        location.setId(cursor.getInt(0));
                        location.setThumb(cursor.getString(1));
                        location.setName(cursor.getString(2));
                        //calculate distance for near place
                        haversineDistance = new HaversineDistance(context);
                        Double refLatitude = cursor.getDouble(3);
                        Double refLongitude = cursor.getDouble(4);
                        Double distance = haversineDistance.calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                        if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                        location.setDistance(distance);
                        //end calculate distance
                        locationList.add(location);
                    }while (cursor.moveToNext());
                }*/

           //     mSearchResultsAdapter.clear();
               // mSearchResultsAdapter.notifyDataSetChanged();
               // mSearchResultsAdapter.swapData(locationList);

                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                Cursor cursor = locationQuery.getLocationListByKeyword(query);
                loadData(cursor);
              /*  List<Location> locationList = new ArrayList<>();
                Location location;
                HaversineDistance haversineDistance;
                if (cursor!=null) {
                    cursor.moveToFirst();
                    do {
                        location = new Location();
                        location.setId(cursor.getInt(0));
                        location.setThumb(cursor.getString(1));
                        location.setName(cursor.getString(2));
                        //calculate distance for near place
                        haversineDistance = new HaversineDistance(context);
                        Double refLatitude = cursor.getDouble(3);
                        Double refLongitude = cursor.getDouble(4);
                        Double distance = haversineDistance.calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                        if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                        location.setDistance(distance);
                        //end calculate distance
                        locationList.add(location);
                    }while (cursor.moveToNext());
                }

               // mSearchResultsAdapter.clear();
             //   mSearchResultsAdapter.notifyDataSetChanged();
                mSearchResultsAdapter.swapData(locationList);*/
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                //mSearchView.swapSuggestions(null);

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                    //just print action
                    Toast.makeText(context, item.getTitle(),
                            Toast.LENGTH_SHORT).show();
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                Location locationSuggestion = (Location) item;

                String textColor = "#787878";
                String textLight = "#000000";

                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                            R.drawable.ic_place_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);

                textView.setTextColor(Color.parseColor(textColor));
                String text = locationSuggestion.getName().toLowerCase()
                        .replaceFirst(mSearchView.getQuery().toLowerCase(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });
    }

}
