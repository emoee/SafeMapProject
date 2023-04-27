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
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.Marker;
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
import java.util.concurrent.BlockingDeque;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback //, NavigationView.OnNavigationItemSelectedListener
{
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
    String email;
    String ctoGCheck;
    LatLng Currentnode;


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
                Log.d(TAG, "GPS onCreate: " + longitude + latitude);
                Currentnode = new LatLng(latitude,longitude);
                setGPSData(Currentnode, true); //Save to location database
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

        // Map search function
        searchView = findViewById(R.id.idSearchView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
                            Map<String, Object> data = new HashMap<>();
                            data.put("startID", Currentnode);
                            data.put("endID", latLng);
                            String jsonString = data.toString();
                            String url = "http://10.40.9.49:5000";
                            Log.d(TAG, "onMarkerClick: " + jsonString);
                            sendPostRequest(url, jsonString);
                            return true;
                        }
                    });
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
            //Log.d("--  json = ", json);
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
        Gradient gradient = new Gradient(colors,startpoints);
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
            setGPSData(node, true);
        }
    };

    //save GPS data to database
    public void setGPSData(LatLng node, boolean T){
        //firebase date setting
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        if (T == true) {
            data.put("id", email);
            data.put("Hnode", node);
            if (user != null) {
                db.collection("GPS").document(email).set(data);
                Log.d(TAG, "setGPSData: " + data);
            }
        } else {
            db.collection("GPS").document(email).update("destination", node);
        }
    }

    public void Childcheck(){
        Log.d(TAG, "Childcheck: start" );
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
                                Log.d(TAG, "onComplete: " + member[i].split("=")[1]);
                            }
                            else if ((member[i].split("=")[0]).contains("cemail")){
                                cemail = member[i].split("=")[1];
                            }
                        }
                        if (cname != "" && cemail != ""){
                            ChildGPScheck(cname, cemail);
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

    //자녀 있는지 확인함
    public void ChildGPScheck(String cname, String cemail){
        Log.d(TAG, "ChildGPScheck: start");
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
                                ChildGPSshow(cname, cemail); //자녀 위치 확인
                            }
                            else {
                                Toast.makeText(MapsActivity.this, "위치 정보 제공에 동의하지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(MapsActivity.this, "등록된 자녀가 없습니다.", Toast.LENGTH_LONG).show();
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
    //GPS 위치 확인
    public void ChildGPSshow(String cname, String cemail){
        Log.d(TAG, "ChildGPSshow: start");
        //LatLng cGPS = new LatLng(37.500246, 127.024570); //서초초 기본 세팅
        DocumentReference docRef = db.collection("GPS").document(cemail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String data = document.getData().toString().substring(1, document.getData().toString().length()-1);
                        String[] dList = data.split("\\}, ")[0].split("Hnode=")[1].substring(1).split(", ");
                        double lat = Double.parseDouble(dList[0].split("=")[1]);
                        double lon = Double.parseDouble(dList[1].split("=")[1]);
                        Log.d(TAG, "onComplete: " + lat + lon);
                        LatLng childnode = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(childnode).title(cname + ": 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.child)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(childnode,16));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //web server service request
    public void sendPostRequest(String url, String jsonString) {
        Log.d(TAG, "sendPostRequest: start");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                jsonString
        );
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청 실패 시 실행되는 코드
                Log.d(TAG, "onFailure: fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                // 서버로부터 받은 응답에 대한 처리를 여기에 작성합니다.
                Log.d(TAG, "onResponse: " + responseString);
            }

        });
    }
}