package com.abb.safe.Fragment;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import okhttp3.Route;


public class sRoutesFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = sRoutesFragment.class.getSimpleName() + "<abb>";
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
        RootView = inflater.inflate(R.layout.fragment_sroutes, container, false);
        mapView = (MapView) RootView.findViewById(R.id.mapviewsafe);
        bundle = this.getArguments();
        title = bundle.getString("route");
        Log.d(TAG, "onCreateView: " +title);
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
        List<LatLng> RoutePoly = new ArrayList<>();
        LatLng cameraI;
        if (title == "route1") {
            cameraI = new LatLng(37.5025199460646, 127.025272971135);
            RoutePoly.add(new LatLng(37.5025199460646, 127.025272971135));
            RoutePoly.add(new LatLng(37.5023664092144, 127.024705030111));
            RoutePoly.add(new LatLng(37.5024032448174, 127.024643615183));
            RoutePoly.add(new LatLng(37.5024312818848, 127.024552815756));
            RoutePoly.add(new LatLng(37.502419958268, 127.024170282665));
            RoutePoly.add(new LatLng(37.5027239972006, 127.02352064695));
            RoutePoly.add(new LatLng(37.50217055465836, 127.02172658307967));
            RoutePoly.add(new LatLng(37.499160820451905, 127.02315208953428));
            saferouteFunction(googleMap, RoutePoly, cameraI); //seocho
            RoutePoly.clear();
        } else if (title == "route2") {
            cameraI = new LatLng(37.4985112475992, 127.027465356862);
            RoutePoly.add(new LatLng(37.4985112475992, 127.027465356862));
            RoutePoly.add(new LatLng(37.4982836140792, 127.028486574436));
            RoutePoly.add(new LatLng(37.4991347773734, 127.031007194783));
            RoutePoly.add(new LatLng(37.5000748571257, 127.030556240277));
            RoutePoly.add(new LatLng(37.4998294893138, 127.029726271021));
            RoutePoly.add(new LatLng(37.5009494385603, 127.029188426948));
            saferouteFunction(googleMap, RoutePoly, cameraI); //gangnam libary
            RoutePoly.clear();
        } else if (title == "route3") {
            cameraI = new LatLng(37.504632241851525, 127.02475674896527);
            RoutePoly.add(new LatLng(37.504632241851525, 127.02475674896527));
            RoutePoly.add(new LatLng(37.505531304131964, 127.02777722941545));
            RoutePoly.add(new LatLng(37.50688479485652, 127.02715479661151));
            RoutePoly.add(new LatLng(37.50705908221907, 127.02711342432036));
            RoutePoly.add(new LatLng(37.508582084656, 127.02705229318053));
            saferouteFunction(googleMap, RoutePoly, cameraI); //nonhyun
            RoutePoly.clear();
        }
    }

    public void saferouteFunction(GoogleMap googleMap, List<LatLng> Route, LatLng seoul){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(seoul, 16);
        googleMap.animateCamera(cameraUpdate);
        Polyline sapolyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .addAll(Route)
                .color(Color.rgb(0,128,0))
                .width(20)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        sapolyline.setStartCap(new RoundCap());
        sapolyline.setEndCap(new RoundCap());
        sapolyline.setTag("SafeRoute");
    }
}