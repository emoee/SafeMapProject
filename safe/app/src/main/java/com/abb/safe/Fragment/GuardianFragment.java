package com.abb.safe.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
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

import com.abb.safe.R;
import com.abb.safe.RegisterActivity;
import com.abb.safe.SettingActivity;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuardianFragment extends Fragment {
    private FirebaseFirestore db;
    private Context context;
    View RootView;
    Button btn_exit;
    Button btn_check;
    EditText editTextPersonName;
    EditText editTextPhone;
    String email;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //firebase date setting
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_guardian, container, false);

        btn_exit = (Button) RootView.findViewById(R.id.btn_exit);
        btn_check = (Button) RootView.findViewById(R.id.btn_check);
        context = container.getContext();
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnExit();
            }
        });
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                editTextPersonName = (EditText) RootView.findViewById(R.id.editTextPersonName);
                editTextPhone = (EditText) RootView.findViewById(R.id.editTextPhone);
                try {
                    if (user != null) {
                        email = user.getEmail();
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", email);
                        data.put("name", editTextPersonName.getText().toString());
                        data.put("phone", editTextPhone.getText().toString());

                        if (db.collection("Guardian").document(email).get() != null){
                            db.collection("Guardian").document(email).set(data);}
                        else{
                            db.collection("Guardian").document(email).update(data);}
                    }
                    Toast.makeText(context, "입력되었습니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e){ e.printStackTrace();}
            }
        });


        return RootView;
    }

    public void BtnExit(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(GuardianFragment.this).commit();
        fragmentManager.popBackStack();
    }

}
