package com.abb.safe.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abb.safe.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.Arrays;
import java.util.List;


public class dRoutesFragment extends Fragment implements OnMapReadyCallback {
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
        if (title == "route1") {
            shortroute1Fun(googleMap);
        } else if (title == "route2") {
            shortroute2Fun(googleMap);
        } else if (title == "route3") {
            shortroute3Fun(googleMap);
        }

    }

    public void shortroute1Fun(GoogleMap googleMap) {
        LatLng RouteShort1 = new LatLng(37.5025199460646, 127.025272971135);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(RouteShort1, 16);
        googleMap.animateCamera(cameraUpdate);

        //short route
        Polyline shpolyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(37.5025199460646, 127.025272971135),
                        new LatLng(37.500689, 127.026115),
                        new LatLng(37.500227, 127.024559),
                        new LatLng(37.4997960668424, 127.024750873972))
                .color(Color.rgb(0, 0, 128))
                .width(27)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        List<PatternItem> pattern = Arrays.asList(
                new Dot(), new Gap(10));
        shpolyline.setPattern(pattern);
        shpolyline.setStartCap(new RoundCap());
        shpolyline.setEndCap(new RoundCap());
        shpolyline.setTag("First ShortRoute");
    }
    public void shortroute2Fun(GoogleMap googleMap) {
        LatLng RouteShort2 = new LatLng(37.4985112475992, 127.027465356862);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(RouteShort2, 16);
        googleMap.animateCamera(cameraUpdate);

        //short route
        Polyline shpolyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(37.4985112475992, 127.027465356862),
                        new LatLng(37.499546, 127.027014),
                        new LatLng(37.500098, 127.028859),
                        new LatLng(37.500624, 127.028917),
                        new LatLng(37.500798, 127.028868),
                        new LatLng(37.5009494385603, 127.029188426948))
                .color(Color.rgb(0, 0, 128))
                .width(27)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        List<PatternItem> pattern = Arrays.asList(
                new Dot(), new Gap(10));
        shpolyline.setPattern(pattern);
        shpolyline.setStartCap(new RoundCap());
        shpolyline.setEndCap(new RoundCap());
        shpolyline.setTag("Second ShortRoute");
    }
    public void shortroute3Fun(GoogleMap googleMap) {
        LatLng RouteShort3 = new LatLng(37.504632241851525, 127.02475674896527);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(RouteShort3, 16);
        googleMap.animateCamera(cameraUpdate);

        //short route
        Polyline shpolyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(37.504632241851525, 127.02475674896527),
                        new LatLng(37.5055153202136, 127.027768300926),
                        new LatLng(37.5069028803928, 127.027150073017),
                        new LatLng(37.5070519815924, 127.027113823959),
                        new LatLng(37.50790990509755, 127.02707238363251))
                .color(Color.rgb(0, 0, 128))
                .width(27)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        List<PatternItem> pattern = Arrays.asList(
                new Dot(), new Gap(10));
        shpolyline.setPattern(pattern);
        shpolyline.setStartCap(new RoundCap());
        shpolyline.setEndCap(new RoundCap());
        shpolyline.setTag("Third ShortRoute");
    }
}