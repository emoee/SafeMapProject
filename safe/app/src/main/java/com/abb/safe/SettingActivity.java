package com.abb.safe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.abb.safe.Fragment.GuardianFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView userName;
    private TextView userBirth;
    Switch gpsStatus;
    Button screenroute;
    Button screenmap;
    Button screensetting;
    Button btn_setguardian;
    String email;
    String member[];
    String name;
    String birth;
    boolean GpsShare;

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

        //Registration of guardian information
        btn_setguardian = findViewById(R.id.btn_setguardian);
        btn_setguardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    GuardianFragment guardianFragment = new GuardianFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameGRegister,guardianFragment).commit();

                } catch (Exception e) {
                    System.out.println("frame error");
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
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData().getClass().getName());
                                    member = document.getData().toString().substring(1, document.getData().toString().length()-1).split(", ");
                                    userShow();
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
    public void userShow(){
        for (int i = 0; i < member.length; i++){
            if (member[i].split("=")[0].equals("name")) name = member[i].split("=")[1];
            else if (member[i].split("=")[0].equals("birth")) birth = member[i].split("=")[1];
            else if (member[i].split("=")[0].equals("gpsShare")) GpsShare = Boolean.valueOf(member[i].split("=")[1]);
            else continue;
        }
        userName.setText(name);
        userBirth.setText(birth);
        if (GpsShare == true) gpsStatus.setChecked(true);
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
}