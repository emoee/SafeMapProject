package com.example.registerloginexample;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // 서버 URL 설정
    final static private String URL = "http://localhost:8080/register.php";
    private Map<String, String> map; // 해쉬맵 선언

    public RegisterRequest(String userID, String userPassword, String userName, int userBirth, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        // php파일에서 데이터를 인식하기 위해 만드는 설정
        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPassword", userPassword);
        map.put("userName", userName);
        map.put("userBirth", userBirth + "");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        return map;
    }
}




