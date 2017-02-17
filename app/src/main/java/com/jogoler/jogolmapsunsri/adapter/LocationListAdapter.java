package com.jogoler.jogolmapsunsri.adapter;

/**
 * Created by RazorX on 12/13/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogoler.jogolmapsunsri.R;
import com.jogoler.jogolmapsunsri.model.Location;
import com.jogoler.jogolmapsunsri.other.HaversineDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean loading = true;

    private List<Location> locationList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    LoadingViewHolder loadingViewHolder;
    private int lastPosition = -1;

    public LocationListAdapter(Context context) {
        this.context = context;
        locationList = new ArrayList<>();
    }

    private void add(Location item) {
        locationList.add(item);
        notifyItemInserted(locationList.size() - 1);
    }

    public void addAll(List<Location> locationList) {
        for (Location location : locationList) {
            add(location);
        }
    }

    public void remove(Location item) {
        int position = locationList.indexOf(item);
        if (position > -1) {
            locationList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    @Override
    public int getItemViewType (int position) {
        if(isPositionFooter (position)) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    private boolean isPositionFooter (int position) {
        return position == locationList.size();
    }

    private Location getItem(int position){
        return locationList.get(position);
    }

   /* @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_location, parent, false);

        final LocationViewHolder locationViewHolder = new LocationViewHolder(view);

        locationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = locationViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (recyclerItemClickListener != null) {
                        recyclerItemClickListener.onItemClick(adapterPos, locationViewHolder.itemView);
                    }
                }
            }
        });

        return locationViewHolder;
    }
    */


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_location, parent, false);
            return new LocationViewHolder(view, onItemClickListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LocationViewHolder) {
            LocationViewHolder locationViewHolder = (LocationViewHolder) holder;
            final Location location = locationList.get(position);
            //set data to layout view
            Glide.with(context)
                    .load(location.getThumb())
                    .placeholder(R.drawable.ic_place_black_24dp)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into( locationViewHolder.locationThumb);

            locationViewHolder.locationName.setText(location.getName());
            locationViewHolder.locationId.setText(Integer.toString(location.getId()));
            //get & Format the near place
            HaversineDistance haversineDistance = new HaversineDistance(context);
            Double distance = location.getDistance();
            locationViewHolder.locationDistance.setText(haversineDistance.formatDistance(distance));

            setAnimation(locationViewHolder.itemView,position);
           /* Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            locationViewHolder.itemView.startAnimation(animation);*/
        } else if (holder instanceof LoadingViewHolder) {
            this.loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
            loadingViewHolder.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder)
    {   if (holder instanceof LocationViewHolder) {
            LocationViewHolder locationViewHolder = (LocationViewHolder) holder;
            locationViewHolder.clearAnimation();
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setLoading(boolean loading){
        this.loading = loading;
    }

    public void stopLoading(){
        loadingViewHolder.progressBar.setVisibility(View.GONE);
    }

    public void startLoading(){
        loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
    }

    public boolean isLoading(){
        if(loadingViewHolder != null)
        {
            return true;
        }else {return false;}
    }

    public void swapData(List<Location> mNewDataSet) {
        locationList = mNewDataSet;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return locationList == null ? 0 : locationList.size() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    static class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView locationThumb;
        TextView locationName;
        TextView locationDistance;
        TextView locationId;

        OnItemClickListener onItemClickListener;

        public LocationViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            //select View in layout
            locationId = (TextView) itemView.findViewById(R.id.location_id);
            locationThumb = (ImageView) itemView.findViewById(R.id.thumb);
            locationDistance = (TextView) itemView.findViewById(R.id.distance);
            locationName = (TextView) itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }
        public void clearAnimation()
        {
            itemView.clearAnimation();
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}
