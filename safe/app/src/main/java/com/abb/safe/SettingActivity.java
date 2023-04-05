package com.abb.safe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView userName;
    private TextView userBirth;
    Switch gpsStatus;
    Button screenroute;
    Button screenmap;
    Button screensetting;
    String ID;
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

        //gps 동의 여부 수정 가능하도록 스위치 버튼 추가
        gpsStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치 버튼이 체크되었는지 검사하여 db 수정
                if (isChecked){
                    updateGpsShare("true");
                }else{
                    updateGpsShare("false");
                }
            }
        });


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

    }
    public void userData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            db.collection("members")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            // 데이터를 가져오는 작업이 잘 동작했을 떄
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("id").equals(email)){
                                        ID = document.getId();
                                        member = document.getData().toString().substring(1, document.getData().toString().length()-1).split(", ");
                                        Log.d(TAG, "onComplete: " + member);
                                        userShow();
                                    }
                                }
                            }
                            // 데이터를 가져오는 작업이 에러났을 때
                            else {
                                Log.w(TAG, "Error => ", task.getException());
                            }
                        }
                    });

        }
    }

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

    public void updateGpsShare(String gps){
        DocumentReference updateDB = db.collection("members").document(ID);
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