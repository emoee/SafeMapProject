package com.abb.safe.Fragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abb.safe.MapsActivity;
import com.abb.safe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GRegisterFragment extends Fragment {
    public static final String TAG = GRegisterFragment.class.getSimpleName() + "<abb>";
    View RootView;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private Context context;
    private EditText editTextGEmail;
    private EditText editTextGPassword;
    private EditText editTextGName;
    private EditText editTextGBirth;
    private EditText editText_cemail;
    private EditText editText_cname;
    private Button buttonGJoin;
    private Button buttonExit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_gregister, container, false);

        //firebase date setting
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextGEmail = (EditText) RootView.findViewById(R.id.editText_gemail);
        editTextGPassword = (EditText) RootView.findViewById(R.id.editText_gpassWord);
        editTextGName = (EditText) RootView.findViewById(R.id.editText_gname);
        editTextGBirth = (EditText) RootView.findViewById(R.id.editText_gbirth);
        editText_cemail = (EditText) RootView.findViewById(R.id.editText_cemail);
        editText_cname = (EditText) RootView.findViewById(R.id.editText_cname);
        context = container.getContext();

        buttonGJoin = (Button) RootView.findViewById(R.id.btn_gjoin);
        buttonExit = (Button) RootView.findViewById(R.id.buttonExit);
        //Sign up for a guardian account
        buttonGJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextGEmail.getText().toString().equals("") && !editTextGPassword.getText().toString().equals("")) {
                   String email = editTextGEmail.getText().toString();
                   String password = editTextGPassword.getText().toString();
                   firebaseAuth.createUserWithEmailAndPassword(email, password)
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Save database when membership registration is successful
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", email);
                                        data.put("name", editTextGName.getText().toString());
                                        data.put("birth", editTextGBirth.getText().toString());
                                        data.put("cname",editText_cname.getText().toString());
                                        data.put("cemail", editText_cemail.getText().toString());
                                        data.put("gpsShare", "false");

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
                                        GExit();
                                    } else {
                                        // Duplicate account, out of email format, password less than 6 digits
                                        Log.d(TAG, "onComplete: false");
                                        Toast.makeText(context, "이미 존재하는 계정 혹은 비밀번호를 변경해주세요.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(context, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }

        }
        });
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GExit();
            }
        });

        return RootView;
    }
    //Turn off frame layout
    public void GExit(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(GRegisterFragment.this).commit();
        fragmentManager.popBackStack();
    }
}