package com.example.registerloginexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class FindActivity extends AppCompatActivity {

    private EditText et_name, et_birth;
    private Button btn_fdlogin, btn_fdpw, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        et_name = findViewById(R.id.et_name);
        et_birth = findViewById(R.id.et_birth);

        btn_fdlogin = findViewById(R.id.btn_fdlogin);
        btn_fdlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력 되어 있는 값을 get 해온다.
                String userID = et_name.getText().toString();
                String userPass = et_birth.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { //  성공 경우
                                String userID = jsonObject.getString("userID");
                                String userPass = jsonObject.getString("userPass");
                                Toast.makeText(getApplicationContext(), "성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FindActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPass", userPass);
                                startActivity(intent);
                            } else { // 실패한 경우
                                Toast.makeText(getApplicationContext(), "실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };


                btn_fdpw = findViewById(R.id.btn_fdpw);
                btn_fdpw.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View view) {
                 Intent intent = new Intent(FindActivity.this, FindPwActivity.class);
                 startActivity(intent); // 엑티비티 이동
                 }
                 });


                btn_cancel = findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FindActivity.this, LoginActivity.class);
                        startActivity(intent); // 엑티비티 이동

                    }
                })
            ;}
        })
    ;}
}