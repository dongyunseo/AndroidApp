package com.example.car;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    private Button btnKakaoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnKakaoLogin = findViewById(R.id.btn_kakao_login);
        btnKakaoLogin.setOnClickListener(v -> {kakaoLogin();});

        // 로그인 시 필요 id pw
        EditText etId = findViewById(R.id.et_id);
        EditText etPw = findViewById(R.id.et_pw);
        Button btnLogin = findViewById(R.id.btn_login);

        // 로그 출력
        Log.d("LoginInput", "ID: " + etId);
        Log.d("LoginInput", "PW: " + etPw);


        btnLogin.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String pw = etPw.getText().toString().trim();
            handleLogin(id, pw); // 로그인 메소드 호출
        });

        // 회원가입 버튼 클릭 리스너 추가
        Button btnSignup = findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(v -> {
            // ActivitySingup으로 이동
            Intent intent = new Intent(LoginPage.this, ActivitySingup.class);
            startActivity(intent);
        });

        getAppKeyHash();
    }


    /**
     * 카카오 로그인 시 필요한 해시키를 얻는 메소드 이다.
     */
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    private void kakaoLogin() {
        // 로그인 콜백
        Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
            if (throwable != null) {
                // 로그인 실패
                Toast.makeText(LoginPage.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                Log.e("login_test", "login_test false  = "+String.valueOf(throwable));
            } else if (oAuthToken != null) {
                // 로그인 성공
                Toast.makeText(LoginPage.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                Log.e("login_test", "login_test true = "+String.valueOf(oAuthToken));
                // 여기서 사용자 정보 조회 가능

                Log.e("login_test", "login_test else = "+String.valueOf(oAuthToken));
            }
            return null;
        };

        // 카카오톡 앱이 있으면 앱으로 로그인, 없으면 카카오 계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, callback);
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(this, callback);
        }
    }

    private void handleLogin(String id, String pw) {
        if (id.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버에 로그인 요청 (예시용 URL)
        String url = "https://sdy9716.mycafe24.com/Login.php"; // 서버 주소 수정

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();

                        // MainActivity로 이동 (아이디 전달)
                        Intent intent = new Intent(LoginPage.this, MainPage.class);
                        intent.putExtra("userId", id);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "로그인 실패: 아이디 또는 비밀번호가 틀림", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
                    Log.e("login_error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("pw", pw);
                return params;
            }
        };

        queue.add(request);
    }

}