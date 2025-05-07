package com.example.car;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPage extends Bottombar {

    private TextView tvName, tvBirthday, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // 하단 네비게이션 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_my);
        setupBottomNavigation(bottomNavigationView, this);

        tvName = findViewById(R.id.tv_name);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvAddress = findViewById(R.id.tv_address);

        // 로그인한 사용자 ID 받기
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        // 사용자 정보 가져오기
        if (userId != null && !userId.isEmpty()) {
            getUserInfo(userId);
        }

        // SharedPreferences에서 userId 가져오기
        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedUserId = pref.getString("userId", "비로그인 상태");
        if (savedUserId != null && !savedUserId.isEmpty()) {
            fetchAlertCountAndShowBadge(this, bottomNavigationView, savedUserId); // 바로 호출
        }
        TextView textView = findViewById(R.id.tv_mypage_id);
        textView.setText(savedUserId);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                // 메인 페이지로 이동
                Intent mainIntent = new Intent(MyPage.this, MainPage.class);
                mainIntent.putExtra("userId", savedUserId);
                startActivity(mainIntent);
                return true;
            }

            if (itemId == R.id.navigation_my) {
                // 현재 화면이므로 아무 동작 없음
                return true;
            }

            return false;
        });

        // 로그아웃 버튼 처리
        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(view -> {
            Log.d("MyPage", "로그아웃 버튼 클릭됨");

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            Intent loginIntent = new Intent(MyPage.this, LoginPage.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });

        // 알림버튼
        Button btnAlarm = findViewById(R.id.btn_alarm);
        btnAlarm.setOnClickListener(view -> {
            Log.d("서버응답 MyPage getAlarmInfo", "userId 확인: " + userId);

            Intent intent_alarm = new Intent(MyPage.this, AlarmList.class);
            intent_alarm.putExtra("userId", userId); // userId를 Intent에 담아 전달
            startActivity(intent_alarm);

        });
    }
    // 사용자 정보를 서버에서 받아오는 메서드
    private void getUserInfo(String userId) {
        String url = "https://sdy9716.mycafe24.com/Get_User_Info.php";
        Log.d("서버응답 MyPage getUserInfo", "userId 확인: " + userId);

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("서버응답", "응답 내용: " + jsonResponse);  // ✅ 이 부분 추가
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String name = jsonObject.optString("name");
                        String birthDate = jsonObject.optString("birth_date");
                        String address = jsonObject.optString("address");

                        Log.d("서버응답", "이름: " + name + ", 생년월일: " + birthDate + ", 주소:" + address);  // ✅ 추가

                        runOnUiThread(() -> {
                            tvName.setText(name);
                            tvBirthday.setText(birthDate);
                            tvAddress.setText(address);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("서버응답", "실패 코드: " + response.code());  // ❌ 응답이 200이 아닐 때
                }
            }


            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("서버응답 MyPage", "서버 연결 실패", e);
            }

        });
    }
}
