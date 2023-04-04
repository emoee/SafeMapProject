package com.example.a2loginactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity2 extends AppCompatActivity {

    EditText et_id, et_pass;

    Button btn_login;


    char result2;
    private static String IP_ADDRESS = "http://10.0.2.2:8080/";
    private static String TAG = "login2.php";

    // @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String m_id = et_id.getText().toString();
                String m_pw = et_pass.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://10.0.2.2:8080/login2.php", m_id, m_pw);
                Log.d("qq", m_id + m_pw);
            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String> {
                ProgressDialog progressDialog;

          @Override
          protected void onPreExecute() {
              super.onPreExecute();

              progressDialog = ProgressDialog.show(LoginActivity2.this,
                        "Please Wait", null, true, true);
          }

          @Override
          protected void onPostExecute(String result) {
              super.onPostExecute(result);
              Log.d("qq", "\n\n" + result);
              progressDialog.dismiss();
              result2 = result.toString().charAt(0);
              Log.d("qq", "\n\n" + result2);
              if (result2 == '1') {
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                    startActivity(intent);

              } else {
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 다릅니다. ", Toast.LENGTH_LONG).show();
              }
              Log.d(TAG, "POST response  - " + result);
          }

          @Override
          protected String doInBackground(String... params) {
                String m_id = (String) params[1];
                String m_pw = (String) params[2];

                String serverURL = (String) params[0];
                serverURL = serverURL + "?" + "ID=" + m_id + "&PW=" + m_pw;
                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "GET response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    return sb.toString();

                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    return new String("Error: " + e.getMessage());
                }
          }
    }
}
