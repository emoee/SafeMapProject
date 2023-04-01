package com.abb.safe;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.abb.safe.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.google.android.material.navigation.NavigationView;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback //, NavigationView.OnNavigationItemSelectedListener
{

    private GoogleMap mMap;
    SearchView searchView;
    private ActivityMapsBinding binding;
    Button screenroute;
    Button screenmap;
    Button screensetting;
    Button btn_police;
    Button btn_home;
    Button btn_bell;
    //Button btn_open;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*드로우 메뉴가 열림
        drawerLayout = findViewById(R.id.drawer_layout);
        btn_open = findViewById(R.id.btn_navbar);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        });
        //네비게이션
        navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);
        }
        */
        //메뉴 버튼 설정
        //map 기본 메인 화면
        screenmap = findViewById(R.id.screenmap);
        screenroute = findViewById(R.id.screenroute);
        screensetting = findViewById(R.id.screensetting);
        screenmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("오류 생김1");
                }

            }
        });
        screenroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(),RoutesActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("오류 생김2");
                }

            }
        });
        screensetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("오류 생김3");
                }

            }
        });



        searchView = findViewById(R.id.idSearchView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //search 검색 기능 구현
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();
                mMap.clear();
                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                //검색창에 경로 검색하면 그리기
                /*
                if (location.equals("route 1")){
                    //경로 그리기~~~
                    Polyline polyline = mMap.addPolyline((new PolylineOptions())
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
                }
                 */

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);


        //버튼 클릭 안전요소 POI 기능
        btn_police= (Button)findViewById(R.id.btn_police);
        btn_home = (Button)findViewById(R.id.btn_home);
        btn_bell = (Button)findViewById(R.id.btn_bell);
        btn_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //경찰서 POI 찍기
                mMap.clear();
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
                        mMap.addMarker(markerOptions);
                        i = i + 1;
                    }
                    reader.close();
                    is.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아동안전 지킴이집 POI 찍기
                mMap.clear();
                StringBuffer strBuffer = new StringBuffer();
                try {
                    InputStream is = getResources().openRawResource(R.raw.safehouse);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    int i = 1;
                    while ((line = reader.readLine()) != null) {
                        String[] l = line.split(",");
                        LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(120f))  //마커색상지정
                                .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                                .position(node);   //마커위치
                        //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                        mMap.addMarker(markerOptions);
                        i = i + 1;
                    }
                    reader.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //안전 비상벨 POI 찍기
                mMap.clear();
                StringBuffer strBuffer = new StringBuffer();
                try {
                    InputStream is = getResources().openRawResource(R.raw.bells);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    int i = 1;
                    while ((line = reader.readLine()) != null) {
                        String[] l = line.split(",");
                        LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(330f))  //마커색상지정
                                .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                                .position(node);   //마커위치
                        //System.out.println(Integer.toString(i) + " : " + l[0] + ", " + l[1]);
                        mMap.addMarker(markerOptions);
                        i = i + 1;
                    }
                    reader.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng me = new LatLng(37.515521,126.840513);
        LatLng SEOUL = new LatLng(37.500246, 127.024570); //37.56, 126.97 41.8847507, -88.2039607
        //mMap.addMarker(new MarkerOptions().position(SEOUL).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,15));

    }
    //네비게이션 사용 시
    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();
        System.out.println("클릭1");

        if (id == R.id.nav_route) {
            Toast.makeText(MapsActivity.this, "카메라", Toast.LENGTH_SHORT).show();
            System.out.println("클릭2");
        } else if (id == R.id.nav_setting) {
            Toast.makeText(MapsActivity.this, "갤러리", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);



        return false;
    }
*/
}