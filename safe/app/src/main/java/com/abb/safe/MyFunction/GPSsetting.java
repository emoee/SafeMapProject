package com.abb.safe.MyFunction;

import android.util.Log;

import com.abb.safe.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GPSsetting {
    public static final String TAG = "<abb> : GPSsetting CLASS";
    private LatLng fixednode;
    private FirebaseFirestore db;
    FirebaseUser user;
    String email;


    public GPSsetting(){
        db = FirebaseFirestore.getInstance();
        fixednode =  new LatLng(37.49795, 127.0276189);
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

    }

    public void setGPSData(LatLng node, boolean T){
        Log.d(TAG, "setGPSData: start");
        //firebase date setting
        db = FirebaseFirestore.getInstance();
        String date = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG, "setGPSData: " + date);

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        if (T == true) {
            data2.put("node", fixednode);
            data2.put("time", date);
            data.put("id", email);
            data.put("Hnode", data2);

            db.collection("GPS").document(email).set(data);
            Log.d(TAG, "setGPSData: " + data);
        } else {
            data2.put("node", node);
            data2.put("time", date);
            db.collection("GPS").document(email)
                    .update("destination", data2);
        }
    }
}
