package com.abb.safe;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    public static final String TAG = SettingActivity.class.getSimpleName() + "<abb>";

    private FirebaseFirestore db;
    private TextView userName;
    private TextView userBirth;
    Switch gpsStatus;
    ImageButton screenroute;
    ImageButton screenmap;
    ImageButton screensetting;
    Button btn_checkChild;
    Button btn_accuse;
    String email;
    String name;
    String birth;
    boolean GpsShare;
    LatLng Accusenode;
    String[] member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //firebase date setting
        db = FirebaseFirestore.getInstance();

        //user
        userName = findViewById(R.id.user_name);
        userBirth = findViewById(R.id.user_birth);
        gpsStatus = findViewById(R.id.gps_status);
        userData(); //update user account information
        //child location check and accuse button
        btn_checkChild = findViewById(R.id.btn_checkChild);
        btn_accuse = findViewById(R.id.btn_accuse);
        btn_checkChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("childCheck", "child");
                startActivity(intent);
            }
        });
        btn_accuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );}
                else {
                    Location location = LocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        Log.d(TAG, "GPS settingAc: " + longitude + latitude);
                        Accusenode = new LatLng(latitude, longitude);
                        AccuseGPSData(Accusenode);
                    }
                }
            }
        });

        //Change of GPS information consent
        gpsStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    updateGpsShare("true"); //database modification
                }else{
                    updateGpsShare("false");
                }
            }
        });

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

    }
    //Get user information from database
    public void userData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            DocumentReference docRef = db.collection("members").document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "userData data: " + document.getData().getClass().getName());
                            member = document.getData().toString().substring(1, document.getData().toString().length() - 1).split(", ");
                            for (int i = 0; i < member.length; i++) {
                                if (member[i].split("=")[0].equals("name"))
                                    name = member[i].split("=")[1];
                                else if (member[i].split("=")[0].equals("birth"))
                                    birth = member[i].split("=")[1];
                                else if (member[i].split("=")[0].equals("gpsShare"))
                                    GpsShare = Boolean.valueOf(member[i].split("=")[1]);
                                else continue;
                            }
                            Log.d(TAG, "userData: " + name);
                            userShow(name, birth, GpsShare);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
    //Display user information
    public void userShow(String nname, String bbirth, boolean ggps){
        userName.setText(nname);
        userBirth.setText(bbirth);
        if (ggps == true) gpsStatus.setChecked(true);
        else gpsStatus.setChecked(false);
    }

    //Change of GPS information consent from database
    public void updateGpsShare(String gps){
        DocumentReference updateDB = db.collection("members").document(email);
        updateDB.update("gpsShare", gps)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    //save GPS data to database for accuse
    public void AccuseGPSData(LatLng node){
        //firebase date setting
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("Hnode", node);
        db.collection("Report").document(email).set(data);
        Toast.makeText(SettingActivity.this, "신고가 접수되었습니다.", Toast.LENGTH_LONG).show();
    }

}