package com.abb.safe.MyFunction;

import static android.content.ContentValues.TAG;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DBAccess {
    private String mydata;
    private String[] mylistdata;
    private DocumentReference docRef;
    private FirebaseFirestore mydb;

    public DBAccess(){
        mydb = FirebaseFirestore.getInstance();
    }

    public synchronized String DB(String collection, String document){
        Log.d(TAG, "DB: Start" );
        if (collection.contains("members")){
            docRef = mydb.collection("members").document(document);
        } else if (collection.contains("GPS")) {
            docRef = mydb.collection("GPS").document(document);
        } else if (collection.contains("PATH2")) {
            docRef = mydb.collection("PATH2").document(document);
        } else if (collection.contains("Safe")) {
            docRef = mydb.collection("Safe").document(document);
        }
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mydata = document.getData().toString().substring(1, document.getData().toString().length() - 1);
                            Log.d(TAG, "onComplete: " + mydata);
                        } else {
                            Log.d(TAG, "No such document");
                            return;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            return mydata;
    }
    public synchronized String[] DBList(String collection, String document){
        Log.d(TAG, "DB List: Start" );
        if (collection.equals("members")){
            docRef = mydb.collection("members").document(document);
        } else if (collection == "GPS") {
            docRef = mydb.collection("GPS").document(document);
        } else if (collection == "PATH2") {
            docRef = mydb.collection("PATH2").document(document);
        } else if (collection == "Safe") {
            docRef = mydb.collection("Safe").document(document);
        }
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mylistdata = document.getData().toString().substring(1, document.getData().toString().length() - 1).split(", ");
                            for (int i = 0; i < mylistdata.length; i++) {
                                Log.d(TAG, "onComplete: " + mylistdata[i]);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                            return;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        try{
            Thread.sleep(500);
            Log.d(TAG, "DBList: " + mylistdata);
        }catch (Exception e){
            e.printStackTrace();
        }
            return mylistdata;
        }
}
