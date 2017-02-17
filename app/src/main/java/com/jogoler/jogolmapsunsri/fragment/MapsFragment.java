package com.jogoler.jogolmapsunsri.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.Service.GPSTracker;
import com.jogoler.jogolmapsunsri.database.LocationQuery;
import com.jogoler.jogolmapsunsri.model.MapsMarker;
import com.jogoler.jogolmapsunsri.other.MapsManager;
import com.qozix.tileview.TileView;
import com.qozix.tileview.detail.DetailLevel;
import com.qozix.tileview.widgets.ZoomPanLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapsManager mapsManager;

    private Context context;

    private FloatingActionButton fab;

    private GPSTracker gps;

    ImageView myLocationMarker;

    private boolean myLocationMarkerShown;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFab(FloatingActionButton fab){
        this.fab = fab;
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps.getLocation();
                Double currentLatitude = 0.1;
                Double currentLongitude = 0.1;
                String msg = "";
                if (gps.canGetLocation()) {
                    currentLatitude = gps.getLatitude();
                    currentLongitude = gps.getLongitude();

                    if (currentLatitude == 0.1 && currentLongitude == 0.1) {
                        msg = "Couldn't get your location !";

                    } else {
                       // msg = "Lat, Lon => " + currentLatitude + ", " + currentLongitude;

                        if(mapsManager.isInUnsri(currentLongitude,currentLatitude)){
                            if(!myLocationMarkerShown) {
                                myLocationMarker.setImageResource(R.drawable.marker_my_location);
                                mapsManager.getMapsView().addMarker(myLocationMarker, currentLongitude, currentLatitude, -0.5f, -1.0f);
                                ViewGroup.LayoutParams params = myLocationMarker.getLayoutParams();
                                if (params != null) {
                                    params.width = 140;
                                    params.height = 140;
                                } else {
                                    params = new ViewGroup.LayoutParams(140, 140);
                                }
                                myLocationMarkerShown=true;
                                mapsManager.frameTo(currentLongitude,currentLatitude);
                            }
                            else{
                                mapsManager.getMapsView().moveMarker(myLocationMarker,currentLongitude,currentLatitude);
                                mapsManager.frameTo(currentLongitude,currentLatitude);
                            }
                        }
                        else{
                            msg+="\n You are not in Unsri Indralaya.";
                        }

                    }

                    Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    gps.showSettingsAlert();
                }


            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_maps, container, false);
       // savedMaps = FragmentManager.getSupportFragmentManager().getFragment(savedInstanceState, "savedMaps");
        super.onCreateView(inflater,container,savedInstanceState);
        //get inflater layout
        myLocationMarkerShown = false;
        myLocationMarker = new ImageView(context);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

            //init maps
            mapsManager = new MapsManager();
            mapsManager.createMaps(context,view);
            mapsManager.setCurrentMaps(MapsManager.UNSRI_INDRAYALAYA_MAPS);
            mapsManager.setModeNetwork(MapsManager.OFFLINE);
            //do configuration on maps
            mapsManager.setupMaps(context);
            mapsManager.setupMapsMarker(context,0.27048388f,getMapsMarkerData());

            mapsManager.getMapsView().addZoomPanListener(new ZoomPanLayout.ZoomPanListener() {
                @Override
                public void onPanBegin(int x, int y, Origination origin) {
                    Log.d(TAG, "onPanBegin: ");
                }

                @Override
                public void onPanUpdate(int x, int y, Origination origin) {
                    Log.d(TAG, "onPanUpdate: ");
                }

                @Override
                public void onPanEnd(int x, int y, Origination origin) {
                    Log.d(TAG, "onPanEnd: ");
                }

                @Override
                public void onZoomBegin(float scale, Origination origin) {
                    Log.d(TAG, "onZoomBegin: ");
                }

                @Override
                public void onZoomUpdate(float scale, Origination origin) {
                        //mapsManager.unClusterMarker();
                        mapsManager.setupMapsMarker(context,scale,getMapsMarkerData());
                        Log.d("ZOOMUPDATE", "onZoomUpdate: "+scale);
                }

                @Override
                public void onZoomEnd(float scale, Origination origin)
                {
                    Log.d(TAG, "onZoomEnd: ");
                }
            });



        //get maps to view
        view = mapsManager.getMapsRelativeLayout();
        return view;
    }

    private List<MapsMarker> getMapsMarkerData(){
        List<MapsMarker> mapsMarkersList =  new ArrayList<>();
        MapsMarker marker;
        LocationQuery locationQuery = new LocationQuery(context);
        Cursor cursor = locationQuery.getAllLocationMarkerByRegion("idl"); //layo
        if (cursor!=null) {
            cursor.moveToFirst();
            do {
                marker = new MapsMarker();
                marker.setLatitude(cursor.getDouble(0));
                marker.setLongitude(cursor.getDouble(1));
                marker.setCategoryId(cursor.getInt(2));
                marker.setCategoryName(cursor.getString(3));
                marker.setCategoryMarker(cursor.getInt(4));
                marker.setOrder(cursor.getInt(5));
                marker.setName(cursor.getString(6));
                marker.setAvailable(false);
                mapsMarkersList.add(marker);
            }while (cursor.moveToNext());
        }

        return mapsMarkersList;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        gps = new GPSTracker(this.context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
