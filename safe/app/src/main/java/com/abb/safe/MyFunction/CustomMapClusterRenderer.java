package com.abb.safe.MyFunction;

import android.content.Context;
import android.provider.SyncStateContract;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomMapClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
    public CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(T item,
                                               MarkerOptions markerOptions) {
        if(item.getTitle() == "police") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(0f));
        } else if (item.getTitle() == "safeHouse") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120f));
        } else if (item.getTitle() == "safeBell") {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(330f));
        }
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}