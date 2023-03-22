package com.abb.safe_location;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.abb.safe_location.databinding.ActivityMapsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        /* 안전 길찾기 거리 한정지어서 POI 찍기
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.node);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");

                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(300f))  //마커색상지정
                        .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                        .position(node);   //마커위치

                //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                googleMap.addMarker(markerOptions);
                i = i + 1;

            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
        // 안전 비상벨 핑 찍기
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.bells);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");

                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                if (37.506 > Float.parseFloat(l[0]) && Float.parseFloat(l[0]) > 37.3 &&
                       127.028 > Float.parseFloat(l[1]) && Float.parseFloat(l[1]) > 126.8) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(100f))  //마커색상지정
                            .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                            .position(node);   //마커위치

                    System.out.println(l[0] + ", " + l[1]);
                    googleMap.addMarker(markerOptions);
                }
                i = i + 1;

            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 여성지킴이집
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.safehouse);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");

                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                if (37.506 > Float.parseFloat(l[0]) && Float.parseFloat(l[0]) > 37.3 &&
                        127.028 > Float.parseFloat(l[1]) && Float.parseFloat(l[1]) > 126.8) {


                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(200f))  //마커색상지정
                            .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                            .position(node);   //마커위치

                    //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                    googleMap.addMarker(markerOptions);
                }
                i = i + 1;

            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        /* 파출소/치안센터
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.police);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");

                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));

                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(0f))  //마커색상지정
                            .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                            .position(node);   //마커위치

                    //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                    googleMap.addMarker(markerOptions);

                i = i + 1;

            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        /*
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = getResources().openRawResource(R.raw.cctv);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");

                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                if (37.503 > Float.parseFloat(l[0]) && Float.parseFloat(l[0]) > 37.495 &&
                        127.028 > Float.parseFloat(l[1]) && Float.parseFloat(l[1]) > 126.9) {

                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(240f))  //마커색상지정
                            .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                            .position(node);   //마커위치

                    //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                    googleMap.addMarker(markerOptions);
                }
                i = i + 1;

            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
       }

         */
        LatLng SEOUL = new LatLng(37.500246, 127.024570); //37.56, 126.97 41.8847507, -88.2039607
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 16));

    }
}