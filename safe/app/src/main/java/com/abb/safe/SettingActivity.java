package com.abb.safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity {

    Button screenroute;
    Button screenmap;
    Button screensetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
}