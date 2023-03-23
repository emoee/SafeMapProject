package com.example.registerloginexample;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    // 서버 URL 설정
    final static private String URL = "http://localhost:8080/login_php.php";
    private Map<String, String> map; // 해쉬맵 선언

    public LoginRequest(String m_id, String m_pw,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        // php파일에서 데이터를 인식하기 위해 만드는 설정
        map = new HashMap<>();
        map.put("m_id", m_id);
        map.put("m_pw", m_pw);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
        //getParams를 재정의하고 서버에 전송할 데이터를 Map객체에 담아 반환하는 형태
    }
}




