package com.example.a2loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_id, tv_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1. 값을 가져온다
        //2. 클릭을 감지한다
        //3. 1번의 값을 액티비티로 넘긴다


        tv_id = findViewById(R.id.tv_id);        //activity_main에서 해당 id를 가져옴
        tv_pass = findViewById(R.id.tv_pass);


        Intent intent = getIntent();
        String m_id = intent.getStringExtra("m_id");
        String m_pw = intent.getStringExtra("m_pw");

        tv_id.setText(m_id);
        tv_pass.setText(m_pw);

    }
}