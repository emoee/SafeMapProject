package com.abb.safe.MyFunction;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyCluster implements ClusterItem {
    private LatLng position;
    private String mytitle;
    public  MyCluster(LatLng latlng, String title){
        position = latlng;
        mytitle = title;
    }
    public LatLng getPosition(){
        return position;
    }
    public String getTitle(){
        return mytitle;
    }
    public String getSnippet(){
        return null;
    }
}
