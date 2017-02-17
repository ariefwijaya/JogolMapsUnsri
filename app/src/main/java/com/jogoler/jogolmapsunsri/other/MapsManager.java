package com.jogoler.jogolmapsunsri.other;

import android.content.Context;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.callout.LocationCallout;
import com.jogoler.jogolmapsunsri.model.MapsMarker;
import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.qozix.tileview.widgets.ZoomPanLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by RazorX on 12/12/2016.
 */

public class MapsManager {

    private TileView mapsview;
    private View mapsViewLayout;
    public static final String UNSRI_INDRAYALAYA_MAPS = "unsri_indralaya";
    public static final String UNSRI_PALEMBANG_MAPS = "unsri_palembang";
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final double NORTH_WEST_LATITUDE = -3.223789;
    public static final double NORTH_WEST_LONGITUDE = 104.665836;
    public static final double SOUTH_EAST_LATITUDE = -3.208147;
    public static final double SOUTH_EAST_LONGITUDE = 104.643003;
    private String currentMaps;
    private String modeNetwork;
    private boolean order1;
    private boolean order2;
    private boolean order3;
    private boolean order4;

    List<MapsMarker> tempMarkerList;

    public MapsManager()
    {

    }

    public void setCurrentMaps(String currentMaps){
        this.currentMaps = currentMaps;
    }

    public String getCurrentMaps(){
        return currentMaps;
    }

    public TileView getMapsView(){
        return mapsview;
    }

    public void createMaps(Context context, View view)
    {
        //init maps state machine
            mapsview = new TileView(context);
            mapsview.setId( R.id.maps_id );
            mapsview.setSaveEnabled( true );
            mapsViewLayout = view;
            tempMarkerList = new ArrayList<>();
            order1=false;
            order2=false;
            order3=false;
            order4=false;
    }


    public void setModeNetwork(String modeNetwork){
        this.modeNetwork = modeNetwork;
    }

    public String getModeNetwork(){
        return modeNetwork;
    }

    private void configureMapsResource(){
        if(currentMaps.equalsIgnoreCase(UNSRI_INDRAYALAYA_MAPS)){
            if(modeNetwork.equalsIgnoreCase(OFFLINE)){
                mapsview.addDetailLevel(0.0125f, "tiles/unsri_maps/125/%d_%d.jpg");
                mapsview.addDetailLevel(0.2500f, "tiles/unsri_maps/250/%d_%d.jpg");
                mapsview.addDetailLevel(0.5000f, "tiles/unsri_maps/500/%d_%d.jpg");
                mapsview.addDetailLevel(1.0000f, "tiles/unsri_maps/1000/%d_%d.jpg");
            }
            else if(modeNetwork.equalsIgnoreCase(ONLINE)){
                Log.d("MAPSMANAGER", "Laya needed online resource");
            }
        }
        else if(currentMaps.equalsIgnoreCase(UNSRI_PALEMBANG_MAPS)){
            if(modeNetwork.equalsIgnoreCase(OFFLINE)){
                Log.d("MAPSMANAGER", "bukit needed offline resource");
            }
            else if(modeNetwork.equalsIgnoreCase(ONLINE)){
                Log.d("MAPSMANAGER", "bukit needed online resource");
            }
        }
        else {
            throw new Error();
        }
    }

    public void setupMaps(Context context)
    {
        // size and geolocation
        mapsview.setSize(9000, 6200);
        // we won't use a downsample here, so color it similarly to tiles
        mapsview.setBackgroundColor(0xFFF3F3F4);

        //define maps source
        configureMapsResource();

        // markers should align to the coordinate along the horizontal center and vertical bottom
        mapsview.setMarkerAnchorPoints(-0.5f, -1.0f);

        // provide the corner coordinates for relative positioning
        mapsview.defineBounds(
                NORTH_WEST_LONGITUDE,
                NORTH_WEST_LATITUDE,
                SOUTH_EAST_LONGITUDE,
                SOUTH_EAST_LATITUDE
        );

        mapsview.getTileCanvasViewGroup().setRenderBuffer(60);

        //add downsample
        ImageView downSample = new ImageView( context );
        downSample.setImageResource( R.drawable.downsample);
        mapsview.addView( downSample, 0 );

        // test higher than 1
        mapsview.setScaleLimits(0, 2);
        // start small and allow zoom
        mapsview.setScale(0.0125f);
        // with padding, we might be fast enough to create the illusion of a seamless image
        mapsview.setViewportPadding(256);

        // we're running from assets, should be fairly fast decodes, go ahead and render asap
        mapsview.setShouldRenderWhilePanning(true);

        //set to center view
        frameTo(104.648146,-3.214337);

    }

    public void setupMapsMarker(Context context, float scale, List<MapsMarker> mapsMarkers){

        if(tempMarkerList.isEmpty())
        {
            tempMarkerList = mapsMarkers;
            Log.d(TAG, "setupMapsMarker: EMPTY");
        }
        else{
            Log.d(TAG, "setupMapsMarker: NOT EMPTY");
        }

        if((scale <= 0.27048388f ) && !order1) {
            clusterMarker(context,tempMarkerList,1);
             order1=true;
        }
        else if((scale > 0.27048388f && scale <= 0.5f) && !order2) {
            clusterMarker(context,tempMarkerList,2);
            order2=true;
        }
        else if((scale > 0.5f && scale <= 1.0f) && !order3) {
            clusterMarker(context,tempMarkerList,3);
            order3=true;

        }
        else if((scale > 1.0f && scale <= 2.0f) && !order4) {
            clusterMarker(context,tempMarkerList,4);
            order4=true;
        }

    }

    public void unClusterMarker(){

            mapsview.removeAllViews();
    }

    private void clusterMarker(Context context,List<MapsMarker> mapsMarkers,int orderShow){
        // add markers for all the points
        for (int i = 0; i < tempMarkerList.size(); i++) {
            MapsMarker marker = tempMarkerList.get(i);
            // any view will do...
            ImageView viewmarker = new ImageView(context);
            viewmarker.setId(-1);
            // save the coordinate for centering and callout positioning


            if (marker.getOrder() == orderShow ) {
                viewmarker.setTag(marker);
                viewmarker.setId(i);
                // marker depend on category
                viewmarker.setImageResource(marker.getCategoryMarker());
                marker.setAvailable(true);
                Log.d("ZOOMUPDATE", "getCategoryName(): " + marker.getCategoryName());
            }


            // on tap show further information about the area indicated
            // this could be done using a OnClickListener, which is a little more "snappy", since
            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
            // confirm it's not the start of a double-tap. But this would consume the touch event and
            // interrupt dragging
            if (viewmarker.getId() != -1) {
                mapsview.getMarkerLayout().setMarkerTapListener(markerTapListener);
                // add it to the view tree
                mapsview.addMarker(viewmarker, marker.getLongitude(), marker.getLatitude(), -0.5f, -1.0f);

                ViewGroup.LayoutParams params = viewmarker.getLayoutParams();
                if (params != null) {
                    params.width = 140;
                    params.height = 140;
                } else {
                    params = new ViewGroup.LayoutParams(140, 140);
                }
            }

        }
    }

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {

        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            Log.d(TAG, "id view" + view.getId());
            // we saved the coordinate in the marker's tag
            MapsMarker marker = (MapsMarker) view.getTag();
            // lets center the screen to that coordinate
            mapsview.slideToAndCenter(marker.getLongitude(), marker.getLatitude());

            // create a simple callout
            LocationCallout callout = new LocationCallout(view.getContext());
            // add it to the view tree at the same position and offset as the marker that invoked it
            mapsview.addCallout(callout, marker.getLongitude(),marker.getLatitude(), -0.5f, -1.0f);
            // a little sugar
            callout.transitionIn();
            // stub out some text
            callout.setTitle(marker.getName());
            callout.setSubtitle(marker.getCategoryName()+ "\n" +
                                "Lat: " + marker.getLatitude() + "\n" +
                                "Lon: " + marker.getLongitude());
        }

    };


    public View getMapsRelativeLayout(){

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout myLayout = (RelativeLayout)mapsViewLayout.findViewById(R.id.maps_layout);
        myLayout.addView(mapsview, lp);

        return mapsViewLayout;
    }

    public void frameTo( final double x, final double y ) {
        mapsview.post( new Runnable() {
            @Override
            public void run() {
                mapsview.scrollToAndCenter( x, y );
            }
        });
    }

    public void pause()
    {
        mapsview.pause();
    }

    public void resume()
    {
        mapsview.resume();
    }

    public void destroy()
    {
        mapsview.destroy();
        mapsview = null;
    }

    public boolean isInUnsri(double CURRENT_LONGITUDE, double CURRENT_LATITUDE){
        if((CURRENT_LONGITUDE < NORTH_WEST_LONGITUDE && CURRENT_LONGITUDE>SOUTH_EAST_LONGITUDE)&&
                (CURRENT_LATITUDE<SOUTH_EAST_LATITUDE && CURRENT_LATITUDE>NORTH_WEST_LATITUDE)) {
            return true;
        }
        else{
            return false;
        }

    }






}


