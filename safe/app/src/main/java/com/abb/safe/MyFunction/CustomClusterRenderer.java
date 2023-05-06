package com.abb.safe.MyFunction;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomClusterRenderer extends DefaultClusterRenderer<MyCluster> {
    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyCluster> clusterManager) {
        super(context, map, clusterManager);
    }
    //cluster marker color change
    @Override
    protected void onBeforeClusterItemRendered(MyCluster item, MarkerOptions markerOptions) {
        Log.d("cluster", "onBeforeClusterItemRendered: " + item.getTitle());
        if(item.getTitle() == "police") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(0f));
        } else if (item.getTitle() == "bells") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120f));
        } else if (item.getTitle() == "safehouse") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(330f));
        } else if (item.getTitle() == "alcol") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(60f));
        }
        markerOptions.snippet(item.getSnippet());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}

