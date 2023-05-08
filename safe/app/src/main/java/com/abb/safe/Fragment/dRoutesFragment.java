package com.abb.safe.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abb.safe.MapsActivity;
import com.abb.safe.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;


public class dRoutesFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = dRoutesFragment.class.getSimpleName() + "<abb>";
    View RootView;
    MapView mapView;
    String title;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_droutes, container, false);
        mapView = (MapView) RootView.findViewById(R.id.mapviewshort);
        bundle = this.getArguments();
        title = bundle.getString("route");
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return RootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity());
        List<LatLng> sRoutePoly = new ArrayList<>();
        LatLng scameraI;
        if (title == "route1") {
            scameraI = new LatLng(37.5025199460646, 127.025272971135);
            sRoutePoly.add(new LatLng(37.5025199460646, 127.025272971135));
            sRoutePoly.add(new LatLng(37.500689, 127.026115));
            sRoutePoly.add(new LatLng(37.500227, 127.024559));
            sRoutePoly.add(new LatLng(37.4997960668424, 127.024750873972));
            shortrouteFunction(googleMap, sRoutePoly, scameraI);
            sRoutePoly.clear();
        } else if (title == "route2") {
            scameraI = new LatLng(37.4985112475992, 127.027465356862);
            sRoutePoly.add(new LatLng(37.4985112475992, 127.027465356862));
            sRoutePoly.add(new LatLng(37.499546, 127.027014));
            sRoutePoly.add(new LatLng(37.500098, 127.028859));
            sRoutePoly.add(new LatLng(37.500624, 127.028917));
            sRoutePoly.add(new LatLng(37.500798, 127.028868));
            sRoutePoly.add(new LatLng(37.5009494385603, 127.029188426948));
            shortrouteFunction(googleMap, sRoutePoly, scameraI);
            sRoutePoly.clear();
        } else if (title == "route3") {
            scameraI = new LatLng(37.504632241851525, 127.02475674896527);
            sRoutePoly.add(new LatLng(37.504632241851525, 127.02475674896527));
            sRoutePoly.add(new LatLng(37.50595768158011, 127.0239034125449));
            sRoutePoly.add(new LatLng(37.50651351280427, 127.0245005779597));
            sRoutePoly.add(new LatLng(37.50712197397771, 127.0260822882161));
            sRoutePoly.add(new LatLng(37.50757277993441, 127.0258457469676));
            sRoutePoly.add(new LatLng(37.50766502609567, 127.0261436867957));
            shortrouteFunction(googleMap, sRoutePoly, scameraI);
            sRoutePoly.clear();
        }

    }
    public void shortrouteFunction(GoogleMap googleMap, List<LatLng> Route, LatLng seoul){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(seoul, 16);
        googleMap.animateCamera(cameraUpdate);
        //short route
        Polyline shpolyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .addAll(Route)
                .color(Color.rgb(0, 0, 128))
                .width(20)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        shpolyline.setStartCap(new RoundCap());
        shpolyline.setEndCap(new RoundCap());
        shpolyline.setTag("ShortRoute");
    }
}