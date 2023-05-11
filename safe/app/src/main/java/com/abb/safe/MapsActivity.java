package com.abb.safe;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import com.abb.safe.MyFunction.CustomClusterRenderer;
import com.abb.safe.MyFunction.MyCluster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.abb.safe.databinding.ActivityMapsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback //, NavigationView.OnNavigationItemSelectedListener
{
    public static final String TAG = MapsActivity.class.getSimpleName() + "<abb>";
    private GoogleMap mMap;
    private FirebaseFirestore db;
    FirebaseUser user;
    ClusterManager clusterManager;
    SearchView searchView;
    private ActivityMapsBinding binding;
    ImageButton screenroute;
    ImageButton screenmap;
    ImageButton screensetting;
    Button btn_police;
    Button btn_home;
    Button btn_bell;
    Button btn_alcol;
    Button btn_heatmap;
    Button btn_near;
    String email;
    String ctoGCheck;
    LatLng Currentnode;
    String[] NDataValue;
    Marker marker;
    int counter;

    //heatmap setting
    int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };
    float[] startpoints = {
            0.2f, 1f
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //login ID save
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        // Save user current location
        final LocationManager LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //permission check
        if (ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );}
        else {
            Location location = LocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.d(TAG, "GPS MapsAc: " + longitude + latitude);
                Currentnode = new LatLng(latitude,longitude);
                //setGPSData(Currentnode, true); //Save to location database
            }
            LocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,300f, locationListener);
        }

        // menu button
        screenmap = findViewById(R.id.screenmap);
        screenroute = findViewById(R.id.screenroute);
        screensetting = findViewById(R.id.screensetting);
        screenmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        screenroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RoutesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        screensetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Map search function
        searchView = findViewById(R.id.idSearchView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    String location = searchView.getQuery().toString();
                    mMap.clear();
                    List<Address> addressList = null;
                    if (location != null || location.equals("")) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address address = addressList.get(0); // Save GPS Values with Geocoding
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        setGPSData(latLng, false); //Save to location database (destination)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                        {
                            @Override
                            public boolean onMarkerClick(Marker arg0) {
                                Log.d(TAG, "onMarkerClick: check");
                                RouteDestination();
                                return true;
                            }
                        });
                    }
                } catch (Exception e){
                    Toast.makeText(MapsActivity.this, "상세 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
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

        //map marker button setting
        btn_police= (Button)findViewById(R.id.btn_police);
        btn_home = (Button)findViewById(R.id.btn_home);
        btn_bell = (Button)findViewById(R.id.btn_bell);
        btn_alcol = (Button)findViewById(R.id.btn_alcol);
        btn_near = (Button)findViewById(R.id.btn_near);
        btn_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Empty the map before placing a marker
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

        //nearby safe
        btn_near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                try {
                    DocumentReference docRef = db.collection("Safe").document(email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "btn_near data: " + document.getData().getClass().getName());
                                    NDataValue = document.getData().toString().substring(1, document.getData().toString().length() - 1).split(", ");
                                    double Nlat = 0;
                                    double Nlon = 0;
                                    for (int i = 0; i < NDataValue.length; i++) {
                                        if (i % 2 == 0) {
                                            Nlat = Double.parseDouble(NDataValue[i].split("=")[1].substring(1, NDataValue[i].split("=")[1].length()));
                                        } else {
                                            Nlon = Double.parseDouble(NDataValue[i].substring(0, NDataValue[i].length() - 1));
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(Nlat, Nlon)).title("근처 안전 요소"));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Nlat, Nlon), 16));
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this, "근처에 안전시설이 없습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        //heatmap setting
        btn_heatmap = (Button) findViewById(R.id.btn_heatmap);
        btn_heatmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                clusterManager.clearItems();
                buildheatmap();
            }
        });

        //child location check
        ctoGCheck = getIntent().getStringExtra("childCheck");
        if (ctoGCheck == null){
            ctoGCheck = "false";
        }
        else if (ctoGCheck.contains("child")){
            Childcheck();
            Log.d(TAG, "cCheck: " + ctoGCheck);
        }
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

        LatLng SEOUL = new LatLng(37.500246, 127.024570); //서울 서초 초등학교
        //LatLng SEOUL = new LatLng(37.477769, 126.983978); //사당역
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,13));

    }

    //marker POI
    public void setMarkerCluster(String name){
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = null;
            if (name == "police"){ is = getResources().openRawResource(R.raw.police); }
            else if ( name == "bells"){ is = getResources().openRawResource(R.raw.bells); }
            else if (name == "safehouse"){ is = getResources().openRawResource(R.raw.safehouse); }
            else if (name == "alcol"){ is = getResources().openRawResource(R.raw.alcol); }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] l = line.split(",");
                LatLng node = new LatLng(Float.parseFloat(l[0]), Float.parseFloat(l[1]));

                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(330f))  //Color
                        .title(Integer.toString(i) + " : " + l[0] + ", " + l[1])
                        .position(node);   //marker position
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

    //Search destination route
    public void RouteDestination(){
        mMap.clear();
        try {
            DocumentReference docRef = db.collection("PATH2").document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "RouteDestination data: " + document.getData().getClass().getName());
                            String Route [] = document.getData().toString().substring(1, document.getData().toString().length()-1).split(", ");
                            double Rlat[] = new double[Route.length/2+1];
                            double Rlon[] = new double[Route.length/2+1];
                            for (int i = 0; i< Route.length; i++){ //total 제외
                                try {
                                    if (i%2 == 0){
                                        Rlat[i/2] = Double.parseDouble(Route[i].split("latitude=")[1]);
                                    }
                                    else {
                                        Rlon[i/2] = Double.parseDouble(Route[i].split("longitude=")[1].substring(0, Route[i].split("longitude=")[1].length()-2));
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            TmakePolyLine(Rlat, Rlon);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }catch (Exception e){
            Log.d(TAG, "RouteDestination: document error");
            e.printStackTrace();
        }
    }

    public void TmakePolyLine(double[] Tlat, double[] Tlon){
        Log.d(TAG, "TmakePolyLine: start" );
        List<LatLng> RoutePoly = new ArrayList<>();
        for (int i = 0; i< Tlat.length-1; i++){
            RoutePoly.add(new LatLng(Tlat[i], Tlon[i]));
            Log.d(TAG, "TmakePolyLine: " + Tlat[i] + " :: " + Tlon[i]);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Tlat[0], Tlon[0]),15));
        Polyline spolyline = mMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .addAll(RoutePoly)
                .color(Color.rgb(0, 141, 98 ))
                .width(27)
                .clickable(true));
        spolyline.setStartCap(new RoundCap());
        spolyline.setEndCap(new RoundCap());
        spolyline.setTag("SafeRoute");
    }

    //json file
    private ArrayList addheatmap() {
        ArrayList<WeightedLatLng> arr = new ArrayList<>();
        String lat = "";
        String lon = "";
        String weight = "";
        String json = "";
        try {
            InputStream is = getAssets().open("heatMap.json");
            int fileSize = is.available();
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            JSONArray Array = jsonObject.getJSONArray("data");
            for(int i=0; i<Array.length(); i++)
            {
                JSONObject Object = Array.getJSONObject(i);
                lat = Object.getString("latitude");
                lon = Object.getString("longitude");
                weight = Object.getString("safety grade");
                arr.add(new WeightedLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), Float.parseFloat(weight)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("adding heatmap","yes");
        return arr;
    }

    //heatmap show
    private void buildheatmap(){
        Gradient gradient = new Gradient(colors, startpoints);
        HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(addheatmap())
                .radius(20)
                .gradient(gradient)
                .build();
        TileOverlayOptions tileoverlayoptions = new TileOverlayOptions().tileProvider(heatmapTileProvider);
        TileOverlay tileoverlay = mMap.addTileOverlay(tileoverlayoptions);
        tileoverlay.clearTileCache();
    }

    //Update GPS values in 30 seconds
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            LatLng node = new LatLng(latitude,longitude);
            setGPSData(node, true); //Save to location database
        }
    };

    //save GPS data to database
    public void setGPSData(LatLng node, boolean T){
        //firebase date setting
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        data2.put("node", new LatLng(37.498604424144, 127.02761093784272));
        data2.put("date", new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()));
        if (T == true) {
            data.put("id", email);
            data.put("Hnode", data2);
            if (user != null) {
                db.collection("GPS").document(email).set(data);
                Log.d(TAG, "setGPSData: " + data);
            }
        } else {
            db.collection("GPS").document(email).update("destination", node);
        }
    }

    public void Childcheck(){
        Log.d(TAG, "Childcheck : start" );
        db = FirebaseFirestore.getInstance();
        String[] box = null;
        DocumentReference docRef = db.collection("members").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Childcheck data: " + document.getData().getClass().getName());
                        String []member = document.getData().toString().substring(1, document.getData().toString().length()-1).split(", ");
                        List<String> strList = new ArrayList<>();
                        String cname = "";
                        String cemail = "";
                        for(int i =0; i< member.length; i++){
                            if ((member[i].split("=")[0]).contains("cname")){
                                cname  = member[i].split("=")[1];
                                Log.d(TAG, "Childcheck onComplete: " + member[i].split("=")[1]);
                            }
                            else if ((member[i].split("=")[0]).contains("cemail")){
                                cemail = member[i].split("=")[1];
                            }
                        }
                        if (cname != "" && cemail != ""){
                            ChildGPScheck(cname, cemail);
                        } else {
                            Toast.makeText(MapsActivity.this, "등록된 자녀가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "등록된 자녀가 없습니다.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //Check if it's a child
    public void ChildGPScheck(String cname, String cemail){
        Log.d(TAG, "ChildGPScheck : start");
        db = FirebaseFirestore.getInstance();
        String[] box = null;
        DocumentReference docRef = db.collection("members").document(cemail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "ChildGPScheck data: " + document.getData().getClass().getName());
                        String []member = document.getData().toString().substring(1, document.getData().toString().length()-1).split(", ");
                        List<String> strList = new ArrayList<>();
                        for(int i =0; i< member.length; i++){
                            strList.add(member[i].split("=")[1]);
                        }
                        if (strList.contains(cname) && strList.contains(cemail)){
                            if (strList.contains("true")){
                                ChildGPSshow(cname, cemail); //location check
                            }
                            else {
                                Toast.makeText(MapsActivity.this, "위치 정보 제공에 동의하지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    //GPS location check
    public void ChildGPSshow(String cname, String cemail){
        Log.d(TAG, "ChildGPSshow : start");
        counter = 1;
        DocumentReference docRef = db.collection("GPS").document(cemail);
        Timer timer = new Timer();
        TimerTask T1 = new TimerTask() {
            @Override
            public void run() {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String data = document.getData().toString().substring(1, document.getData().toString().length() - 2);
                                String[] dList = data.split("\\}, ")[0].split("Hnode=")[1].substring(1).split(", ");
                                double lat = Double.parseDouble(dList[0].split("=")[1]);
                                double lon = Double.parseDouble(dList[1].split("=")[1]);
                                Log.d(TAG, "ChildGPSshow onComplete: " + lat + lon);
                                LatLng childnode = new LatLng(lat, lon);
                                if (counter == 1){
                                    marker = mMap.addMarker(new MarkerOptions().position(childnode).title(cname + ": 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.checkchild)));
                                    counter += 1;
                                } else {
                                    marker.setPosition(childnode);
                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(childnode, 18));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        };
        timer.schedule(T1, 0, 1500); //Timer1
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                timer.cancel();
            }
        }, 18000); //Shutdown after 1800 seconds
    }
}