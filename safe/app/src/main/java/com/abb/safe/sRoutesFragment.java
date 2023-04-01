package com.abb.safe;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;


public class sRoutesFragment extends Fragment implements OnMapReadyCallback {
    View RootView;
    MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_sroutes, container, false);
        mapView = (MapView) RootView.findViewById(R.id.mapview);
        if (getArguments().getString("name") == "routesafe1")
        {

        }
        else {
            Log.d("오류", "onMapReady: ");
        }
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
        saferoute1Fun(googleMap);

    }

    public void saferoute1Fun(GoogleMap googleMap){
        LatLng RouteSafe1 = new LatLng(37.500246, 127.024570);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(RouteSafe1, 17);
        googleMap.animateCamera(cameraUpdate);

        //경로 그리기~~~
        Polyline polyline = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(37.5026847864391, 127.025091342321),
                        new LatLng(37.4994950706731, 127.026576631368),
                        new LatLng(37.499034147171, 127.025151583119),
                        new LatLng(37.4999156732587, 127.024740076147))
                .color(Color.rgb(0,128,0))
                .width(25)
                .clickable(true));
        //polyline.setJointType(JointType.ROUND);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setTag("First SafeRoute");

        //경로 클릭 이벤트
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {
                Log.d("poly", "onPolylineClick: "+polyline);
                //AR 유니티로 넘어가게??? 뭐하지?\
                //클릭이벤트

            }
        });
    }
}