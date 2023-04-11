package com.abb.safe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;

import com.abb.safe.MyFunction.MyCluster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.abb.safe.databinding.ActivityMapsBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback //, NavigationView.OnNavigationItemSelectedListener
{
    private GoogleMap mMap;
    private FirebaseFirestore db;
    ClusterManager clusterManager;
    SearchView searchView;
    private ActivityMapsBinding binding;
    Button screenroute;
    Button screenmap;
    Button screensetting;
    Button btn_police;
    Button btn_home;
    Button btn_bell;
    Button btn_alcol;
    Button btn_heatmap;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //위치 가져와서 저장하기
        final LocationManager LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );}
        else {
            Location location = LocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.d(TAG, "GPS onCreate: " + longitude + latitude);
                setGPSData(longitude, latitude);
            }
            LocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,300f, locationListener);
        }

        //메뉴 버튼 설정
        //map 기본 메인 화면
        screenmap = findViewById(R.id.screenmap);
        screenroute = findViewById(R.id.screenroute);
        screensetting = findViewById(R.id.screensetting);
        screenmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });
        screenroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RoutesActivity.class);
                startActivity(intent);
            }
        });
        screensetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
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
        btn_alcol = (Button)findViewById(R.id.btn_alcol);
        btn_heatmap = (Button) findViewById(R.id.btn_heatmap);
        btn_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                setMarkerCluster("police");
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                setMarkerCluster("safehouse");
            }
        });
        btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                setMarkerCluster("bells");
            }
        });
        btn_alcol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                setMarkerCluster("alcol");
            }
        });
        btn_heatmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                buildheatmap();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //cluster setting
        clusterManager = new ClusterManager<>(this, mMap);
        clusterManager.setRenderer(new CustomClusterRenderer(MapsActivity.this, mMap, clusterManager));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(clusterManager);

        //LatLng me = new LatLng(37.515521,126.840513);
        LatLng SEOUL = new LatLng(37.500246, 127.024570); //37.56, 126.97 41.8847507, -88.2039607
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,13));
    }

    //marker POI 찍기
    public void setMarkerCluster(String name){
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = null;
            if (name == "police"){
                is = getResources().openRawResource(R.raw.police);
            }
            else if ( name == "bells"){
                is = getResources().openRawResource(R.raw.bells);
            }
            else if (name == "safehouse"){
                is = getResources().openRawResource(R.raw.safehouse);
            }
            else if (name == "alcol"){
                is = getResources().openRawResource(R.raw.alcol);
            }
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
                //mMap.addMarker(markerOptions);
                MyCluster cItem = new MyCluster(node, name);
                clusterManager.addItem(cItem);
                i = i + 1;
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //cluster marker color change
    public class CustomClusterRenderer extends DefaultClusterRenderer<MyCluster> {

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyCluster> clusterManager) {
            super(context, map, clusterManager);
        }

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

    //heatmap setting
    int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };
    float[] startpoints = {
            0.2f, 1f
    };

    //히트맵 표시할 자료 json파일 불러와서 추가하기.
    private ArrayList addheatmap() {
        ArrayList<WeightedLatLng> arr = new ArrayList<>();
        String lat = "";
        String lon = "";
        String weight = "";
        //json 자료 가져오기
        String json = "";
        try {
            InputStream is = getAssets().open("heatMap.json"); // json파일 이름
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            //json파일명을 가져와서 String 변수에 담음
            json = new String(buffer, "UTF-8");
            Log.d("--  json = ", json);


            JSONObject jsonObject = new JSONObject(json);

            //배열로된 자료를 가져올때
            JSONArray Array = jsonObject.getJSONArray("data");//배열의 이름
            for(int i=0; i<Array.length(); i++)
            {
                JSONObject Object = Array.getJSONObject(i);
                lat = Object.getString("latitude");
                lon = Object.getString("longitude");
                weight = Object.getString("safety grade");
                arr.add(new WeightedLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), Float.parseFloat(weight))); //madurai
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //arr.add(new WeightedLatLng(new LatLng(9.9252, 78.119),10)); //madurai

        Log.e("adding heatmap","yes");

        return arr;
    }

    //heatmap show
    private void buildheatmap(){
        Gradient gradient = new Gradient(colors,startpoints);
        HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(addheatmap())
                .radius(20)
                .gradient(gradient)
                .build();
        TileOverlayOptions tileoverlayoptions = new TileOverlayOptions().tileProvider(heatmapTileProvider);
        TileOverlay tileoverlay = mMap.addTileOverlay(tileoverlayoptions);
        tileoverlay.clearTileCache();
        Toast.makeText(this,"added heatmap",Toast.LENGTH_SHORT).show();
    }


    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            setGPSData(longitude, latitude);
        }
    };
    public void setGPSData(double lon, double lat){
        //firebase date setting
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            Map<String, Object> data = new HashMap<>();
            data.put("id",email);
            data.put("lon", lon);
            data.put("lat", lat);
            if (db.collection("GPS").document(email).get() != null){
                db.collection("GPS").document(email).set(data);}
            else{
                db.collection("GPS").document(email).update(data);}
            Log.d(TAG, "setGPSData: " + data);
        }
    }
}