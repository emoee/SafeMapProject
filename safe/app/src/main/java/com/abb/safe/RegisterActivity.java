package com.abb.safe;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.abb.safe.Fragment.GRegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName() + "<abb>";

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextBirth;
    private Button btn_gshow;
    private Button buttonJoin;
    private Button buttonJoin_login;
    public static Context context;
    CheckBox gpsLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        gpsLoc = findViewById(R.id.checkbox_gps);

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editTextName = (EditText) findViewById(R.id.editText_name);
        editTextBirth = (EditText) findViewById(R.id.editText_birth);

        btn_gshow = (Button) findViewById(R.id.btn_gshow);
        buttonJoin = (Button) findViewById(R.id.btn_join);
        buttonJoin_login = (Button) findViewById(R.id.btn_join_login);
        btn_gshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Sign up for a guardian account
                    GRegisterFragment gRegisterFragment = new GRegisterFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameGRegister,gRegisterFragment).commit();

                } catch (Exception e) {
                    System.out.println("프레임 오류 생김");
                }

            }
        });
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    //email & password not blank
                    if (gpsLoc.isChecked()) { //Consent to provide gps information
                        createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextName.getText().toString(), editTextBirth.getText().toString(), true);
                    } else {
                        createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextName.getText().toString(), editTextBirth.getText().toString(), false);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonJoin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LoginActivity connection
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void createUser(String email, String password, String name, String birth, boolean gpsCheck) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Save database when membership registration is successful
                            Map<String, Object> data = new HashMap<>();
                            data.put("id", email);
                            data.put("name", name);
                            data.put("birth", birth);
                            data.put("gpsShare", gpsCheck);

                            db.collection("members").document(email)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                            Log.d("sign up", "onComplete: "+task.getResult());
                            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            // LoginActivity connection
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Duplicate account, out of email format, password less than 6 digits
                            Log.d(TAG, "onComplete: false");
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 계정 혹은 비밀번호를 변경해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}