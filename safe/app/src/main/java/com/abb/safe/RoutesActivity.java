package com.abb.safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.abb.safe.Fragment.dRoutesFragment;
import com.abb.safe.Fragment.sRoutesFragment;
import com.unity3d.player.UnityPlayerActivity;

public class RoutesActivity extends AppCompatActivity {

    ImageButton screenroute;
    ImageButton screenmap;
    ImageButton screensetting;
    Button route1;
    Button route2;
    Button route3;
    Button UnityAR_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

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

        //지도 그리기
        route1 = findViewById(R.id.route1);
        route2 = findViewById(R.id.route2);
        route3 = findViewById(R.id.route3);
        route1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("route","route1");//번들에 넘길 값 저장
                    sRoutesFragment sroutesFragment = new sRoutesFragment();
                    sroutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.framesafe, sroutesFragment).commit();

                    dRoutesFragment droutesFragment = new dRoutesFragment();
                    droutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameshort, droutesFragment).commit();

                } catch (Exception e) {
                    System.out.println("프레임 오류 생김");
                }

            }
        });
        route2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("route","route2");//번들에 넘길 값 저장
                    sRoutesFragment sroutesFragment = new sRoutesFragment();
                    sroutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.framesafe, sroutesFragment).commit();

                    dRoutesFragment droutesFragment = new dRoutesFragment();
                    droutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameshort, droutesFragment).commit();

                } catch (Exception e) {
                    System.out.println("프레임 오류 생김");
                }

            }
        });
        route3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("route","route3");//번들에 넘길 값 저장
                    sRoutesFragment sroutesFragment = new sRoutesFragment();
                    sroutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.framesafe, sroutesFragment).commit();

                    dRoutesFragment droutesFragment = new dRoutesFragment();
                    droutesFragment.setArguments(bundle); //보내기
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameshort, droutesFragment).commit();

                } catch (Exception e) {
                    System.out.println("프레임 오류 생김");
                }

            }
        });
        UnityAR_btn = findViewById(R.id.UnityAR_btn);
        UnityAR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(RoutesActivity.this, UnityPlayerActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("프레임 오류 생김");
                }

            }
        });
    }
}