package com.jogoler.jogolmapsunsri.fragment;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.bumptech.glide.Glide;
import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.Service.GPSTracker;
import com.jogoler.jogolmapsunsri.activity.LocationDetailActivity;
import com.jogoler.jogolmapsunsri.activity.MainActivity;
import com.jogoler.jogolmapsunsri.adapter.LocationListAdapter;
import com.jogoler.jogolmapsunsri.database.DataBaseHelper;
import com.jogoler.jogolmapsunsri.database.LocationQuery;
import com.jogoler.jogolmapsunsri.model.Location;
import com.jogoler.jogolmapsunsri.other.CustomFloatingSearchView;
import com.jogoler.jogolmapsunsri.other.DistanceComparator;
import com.jogoler.jogolmapsunsri.other.HaversineDistance;
import com.jogoler.jogolmapsunsri.other.NotificationHandler;

import static android.content.ContentValues.TAG;
import static com.jogoler.jogolmapsunsri.activity.MainActivity.CURRENT_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends BaseFragment{

    private int PAGE_SIZE = 6;

    private boolean isLastPage = false;
    private boolean isLoading = false;

    private RecyclerView listLocation;
    private LinearLayoutManager linearLayoutManager;
    private LocationListAdapter locationListAdapter;
    private LocationQuery locationQuery;
    private Cursor cursor;
    private List<Location> locationList;
    private List<Location> tempLocationList;

    private FloatingSearchView mSearchView;
    private CustomFloatingSearchView customFloatingSearchView;

    protected Context context;

    private DrawerLayout drawer;
    private String mLastQuery = "";

    private Double currentLatitude;
    private Double currentLongitude;
    private HaversineDistance haversineDistance;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    /**
     * Back pressed send from activity.
     *
     * @return if event is consumed, it will return true.
     */
    @Override
    public boolean onBackPressed() {
        if (mSearchView.isSearchBarFocused()) {
            mSearchView.clearSearchFocus();
            Log.d(TAG, "onBackPressed: Handled");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setDrawerLayout(DrawerLayout drawer){
            this.drawer = drawer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    ((MainActivity)getActivity()).toogleActionBar();
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        mSearchView = (FloatingSearchView) rootView.findViewById(R.id.floating_search_view);

        listLocation = (RecyclerView) rootView.findViewById(R.id.listLocation);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //((MainActivity)getActivity()).toogleActionBar();
        linearLayoutManager = new LinearLayoutManager(context);
        locationListAdapter = new LocationListAdapter(context);

        listLocation.setLayoutManager(linearLayoutManager);
        listLocation.setAdapter(locationListAdapter);
        listLocation.addOnScrollListener(recyclerViewOnScrollListener);

        //search view
        locationQuery = new LocationQuery(context);
        haversineDistance = new HaversineDistance(context);
        currentLatitude =0.0;
        currentLongitude=0.0;

        //loadData();
        initData();
        initListener();

        setupFloatingSearch();
        //setupDrawer();
    }

    private void initListener() {
        locationListAdapter.setOnItemClickListener(new LocationListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Context viewContext = view.getContext();
                TextView locationDistance = (TextView) view.findViewById(R.id.distance);
                TextView locationID = (TextView) view.findViewById(R.id.location_id);
                TextView locationName = (TextView) view.findViewById(R.id.name);
                ImageView locationThumb = (ImageView) view.findViewById(R.id.thumb);

                //store data to intent
                Intent intent = new Intent(viewContext, LocationDetailActivity.class);
                intent.putExtra("id",(locationID.getText().toString()));
                intent.putExtra("distance",(locationDistance.getText().toString()));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
                    String transitionName = getString(R.string.transition_shared_element);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        //define transition animation
                        ((Activity)context).getWindow().setExitTransition(new ChangeTransform());
                        ((Activity)context).getWindow().setEnterTransition(new ChangeTransform());
                        ((Activity)context).getWindow().setAllowEnterTransitionOverlap(true);
                        Pair<View, String> p1 = Pair.create((View) locationName, "name");
                        Pair<View, String> p2 = Pair.create((View) locationThumb, transitionName);
                        //options = ActivityOptions.makeSceneTransitionAnimation((Activity) viewContext, locationThumb,transitionName);
                        options = ActivityOptions.makeSceneTransitionAnimation((Activity) viewContext,p1,p2);
                    }
                    viewContext.startActivity(intent,options.toBundle());
                }
                else
                {
                    viewContext.startActivity(intent);
                }
            }
        });
    }

    private void initData(){
        //update location
        GPSTracker gps;
        gps = new GPSTracker(context);
        if(gps.canGetLocation()){
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }
        gps.stopUsingGPS();

        haversineDistance.setCurrentLatitude(currentLatitude);
        haversineDistance.setCurrentLongitude(currentLongitude);

        locationList = new ArrayList<>();
        locationListAdapter.setLoading(true);

      //  Location location;
        cursor = locationQuery.getLocationList();

        //asc sort by distance
        tempLocationList = haversineDistance.sortEveryLocationByDistance(cursor);
        //send location to floating search

        for(int i=0; i<PAGE_SIZE; i++){

            locationList.add(tempLocationList.get(i));
        }
        locationListAdapter.swapData(locationList);

        //no more data can be loaded.
        if(locationListAdapter.getItemCount()-1>= tempLocationList.size()){
            Toast.makeText(context, "All data has been loaded", Toast.LENGTH_SHORT).show();
            locationListAdapter.stopLoading();
            locationListAdapter.setLoading(false);
            isLastPage=true;
        }
    }

    private void loadData(){
        locationListAdapter.setLoading(true);
        isLoading = false;
        locationList = new ArrayList<>();

        int index = locationListAdapter.getItemCount() - 1;
        int end = index + PAGE_SIZE;
      //  if (end <= cursor.getCount()) {
        int i = index;
        if(i<end && i<tempLocationList.size()){
            do{
                locationList.add(tempLocationList.get(i));
                i++;
            }while(i<end && i<tempLocationList.size());
                locationListAdapter.addAll(locationList);
        }
            //no more data can be loaded.
            if(locationListAdapter.getItemCount()-1>= tempLocationList.size()){
                NotificationHandler notificationHandler = new NotificationHandler();
                Toast.makeText(context, "All data has been loaded", Toast.LENGTH_SHORT).show();
                locationListAdapter.stopLoading();
                locationListAdapter.setLoading(false);
                isLastPage=true;
            }
        Log.d(TAG, "loadData: "+tempLocationList.size());
        //}
    }

    /*private void loadData(){
        locationQuery = new LocationQuery(context);

        List<Location> locationList = new ArrayList<>();
        Location location;

        HaversineDistance haversineDistance;
        Double currentLatitude =0.0;
        Double currentLongitude=0.0;
        GPSTracker gps;
        gps = new GPSTracker(context);
        if(gps.canGetLocation()){
            //Double currentLatitude = -3.2191025;
            //Double currentLongitude = 104.6493936;
             currentLatitude = gps.getLatitude();
             currentLongitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        Cursor cursor = locationQuery.getLocationList();

        if (cursor.moveToFirst()) {
            do {
                location = new Location();
                location.setId(cursor.getInt(0));
                location.setThumb(cursor.getString(1));
                location.setName(cursor.getString(2));

                //calculate distance for near place
                haversineDistance = new HaversineDistance();
                Double refLatitude = cursor.getDouble(3);
                Double refLongitude = cursor.getDouble(4);
                Double distance = haversineDistance.calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                location.setDistance(distance);
                //end calculate distance
                locationList.add(location);
            }while (cursor.moveToNext());
        }
        //sort data by distance Ascending
        Collections.sort(locationList, new DistanceComparator(DistanceComparator.ASC));

        locationListAdapter.clear();
        locationListAdapter.notifyDataSetChanged();
        locationListAdapter.addAll(locationList);
    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((MainActivity)getActivity()).toogleActionBar();
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE
                        && locationListAdapter.getItemCount()-1 < cursor.getCount()) {
                    isLoading = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadData();
                        }
                    }, 1000);
                }
            }
        }
    };

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                }
                else {
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
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_taxi) {
                    initData();
                    initListener();
                    mSearchView.clearQuery();
                }
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
                listLocation.setTranslationY(newHeight);
            }
        });


    }
    private void setupDrawer() {
        mSearchView.attachNavigationDrawerToMenuButton(drawer);
    }

    private void loadData(Cursor cursor){
        locationListAdapter.setLoading(false);
        tempLocationList = new ArrayList<>();
        Location location;
        if (cursor!=null) {
            cursor.moveToFirst();
            do {
                location = new Location();
                location.setId(cursor.getInt(0));
                location.setThumb(cursor.getString(1));
                location.setName(cursor.getString(2));
                //calculate distance for near place
                Double refLatitude = cursor.getDouble(3);
                Double refLongitude = cursor.getDouble(4);
                Double distance = haversineDistance.calculateDistance(currentLatitude,currentLongitude,refLatitude,refLongitude);
                if(currentLatitude==0.0 && currentLongitude == 0.0) {distance=0.0;}
                location.setDistance(distance);
                tempLocationList.add(location);
            }while (cursor.moveToNext());
        }

        locationListAdapter.swapData(tempLocationList);
        Toast.makeText(context, tempLocationList.size() +" location found.", Toast.LENGTH_SHORT).show();

    }


}
