package com.jogoler.jogolmapsunsri.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.database.LocationQuery;
import com.jogoler.jogolmapsunsri.model.LocationDetail;
import com.jogoler.jogolmapsunsri.other.ExpandableTextView;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class LocationDetailActivity extends AppCompatActivity {

    private LocationQuery locationQuery;
    private LocationDetail location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        setupActionBar();

        // Retrieve data from item click
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("id"));
        String distance = i.getStringExtra("distance");
        loadLocationData(id);
        location.setDistance(distance);
        renderDetailThumbnaiView();
        renderDetailViewInfo();
        renderDetailViewDescription();
        renderDetailViewMap();
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setMaxLines(3);
        collapsingToolbar.setTitle(location.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_share)
        {
            startShareActivity("Place",getLocationDetailText());
            Toast.makeText(getApplicationContext(), "Share Location!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        supportFinishAfterTransition();
       // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // analytics
       // GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void setupActionBar()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail_activity);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setLogo(R.drawable.ic_place_black_24dp);
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(null);
    }

    private void loadLocationData(int id){
        locationQuery = new LocationQuery(this);
        location = new LocationDetail();
        Cursor cursor = locationQuery.getLocationDetailById(id);
        if (cursor.moveToFirst()) {
            do {
                location.setId(id);
                location.setCategory_id(cursor.getInt(0));
                location.setRegion_id(cursor.getString(1));
                location.setName(cursor.getString(2));
                location.setIntro(cursor.getString(3));
                location.setDescription(cursor.getString(4));
                location.setThumb(cursor.getString(5));
                location.setLink(cursor.getString(6));
                location.setLatitude(cursor.getDouble(7));
                location.setLongitude(cursor.getDouble(8));
                location.setAddress(cursor.getString(9));
                location.setPhone(cursor.getString(10));
                location.setEmail(cursor.getString(11));
                location.setFavorite(cursor.getInt(12));
            } while (cursor.moveToNext());
        }
    }

    private void renderDetailThumbnaiView(){
        ImageView locationThumbnailImageView = (ImageView) findViewById(R.id.location_thumbnail);
        Glide.with(this)
                .load(location.getThumb())
                .placeholder(R.drawable.ic_place_black_24dp)
                .crossFade()
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(locationThumbnailImageView);
    }

    private void renderDetailViewInfo(){
        TextView introTextView = (TextView) findViewById(R.id.location_detail_info_intro);
        TextView addressTextView = (TextView) findViewById(R.id.location_detail_info_address);
        TextView distanceTextView = (TextView) findViewById(R.id.location_detail_info_distance);
        TextView linkTextView = (TextView) findViewById(R.id.location_detail_info_link);
        TextView phoneTextView = (TextView) findViewById(R.id.location_detail_info_phone);
        TextView emailTextView = (TextView) findViewById(R.id.location_detail_info_email);

        if(location.getIntro()!=null) introTextView.setText(location.getIntro());
        else introTextView.setVisibility(View.GONE);

        if(location.getAddress()!=null) addressTextView.setText(location.getAddress());
        else addressTextView.setVisibility(View.GONE);

        if(location.getDistance()!=null) distanceTextView.setText(location.getDistance());
        else distanceTextView.setVisibility(View.GONE);

        if(location.getLink()!=null) linkTextView.setText(location.getLink());
        else linkTextView.setVisibility(View.GONE);

        if(location.getPhone()!=null) phoneTextView.setText(location.getPhone());
        else phoneTextView.setVisibility(View.GONE);

        if(location.getEmail()!=null) emailTextView.setText(location.getEmail());
        else emailTextView.setVisibility(View.GONE);

    }

    private void renderDetailViewDescription(){
        ExpandableTextView descriptionTextView = (ExpandableTextView) findViewById(R.id.location_detail_description_text);
        CardView card_description = (CardView)findViewById(R.id.card_description_text);
        if(location.getDescription()!=null){
            descriptionTextView.setText(location.getDescription());
            descriptionTextView.setTrimLength(400);
        }
        else{
            card_description.setVisibility(View.GONE);
        }
    }

    private void renderDetailViewMap(){
        ImageView mapImageView = (ImageView) findViewById(R.id.location_detail_map_image);
        mapImageView.setImageResource(R.drawable.downsample);

        //mapImageView.setImageBitmap();
    }

    private void startShareActivity(String subject, String text)
    {
        try
        {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }

    private String getLocationDetailText()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getName());
        builder.append("\n\n");
        if(location.getAddress()!=null && !location.getAddress().trim().equals(""))
        {
            builder.append(location.getAddress());
            builder.append("\n\n");
        }
        if(location.getIntro()!=null && !location.getIntro().trim().equals(""))
        {
            builder.append(location.getIntro());
            builder.append("\n\n");
        }
        if(location.getDescription()!=null && !location.getDescription().trim().equals(""))
        {
            builder.append(location.getDescription());
            builder.append("\n\n");
        }
        if(location.getLink()!=null && !location.getLink().trim().equals(""))
        {
            builder.append(location.getLink());
        }
        return builder.toString();
    }
}
