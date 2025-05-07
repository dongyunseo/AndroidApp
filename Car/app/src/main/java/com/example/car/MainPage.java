package com.example.car;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainPage extends AppCompatActivity {
    private TextView countryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 로그인 사용자 ID 받기
        Intent loginintent = getIntent();
        String userId = loginintent.getStringExtra("userId");

        // SharedPreferences에 저장
        if (userId != null && !userId.isEmpty()) {
            SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("userId", userId);
            editor.apply();
        }

        // 동그라미에 아이디 표시
        TextView tvUserId = findViewById(R.id.tv_user_id);
        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedUserId = pref.getString("userId", "로그인ID");
        tvUserId.setText(savedUserId);

        countryTextView = findViewById(R.id.Country);
        getCountriesAndDisplay();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // BottomNavigation이 완전히 준비된 후 뱃지 표시
        bottomNavigationView.post(() -> {
            if (savedUserId != null && !savedUserId.isEmpty()) {
                fetchAlertCountAndShowBadge(savedUserId);
            }
        });

        // 바텀 네비게이션 클릭 이벤트 처리
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_my) {
                String currentUserId = pref.getString("userId", null);

                if (currentUserId != null && !currentUserId.isEmpty()) {
                    fetchAlertCountAndMove(currentUserId); // 마이페이지로 이동
                } else {
                    startActivity(new Intent(MainPage.this, LoginPage.class));
                }
                return true;
            }
            return false;
        });
    }

    private void showBadge(int count) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_my);
        badge.setVisible(true);
        badge.setNumber(count);
    }

    private void fetchAlertCountAndShowBadge(String userId) {
        String url = "https://sdy9716.mycafe24.com/alert_count.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int alertCount = jsonObject.getInt("alert_count");
                        showBadge(alertCount); // 알림 뱃지 숫자 표시
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void fetchAlertCountAndMove(String userId) {
        String url = "https://sdy9716.mycafe24.com/alert_count.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int alertCount = jsonObject.getInt("alert_count");
                        showBadge(alertCount); // 마이페이지 들어가기 전에도 뱃지 보이게

                        Intent intent = new Intent(MainPage.this, MyPage.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getCountriesAndDisplay() {
        LinearLayout containerLayout = findViewById(R.id.containerLayout);
        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/Country_Manufacturer.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray dataArray = new JSONArray(result.toString());

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject countryObject = dataArray.getJSONObject(i);
                            String country = countryObject.getString("country");

                            TextView countryTextView = new TextView(this);
                            countryTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            countryTextView.setText(country);
                            countryTextView.setTextSize(20);
                            countryTextView.setTypeface(null, Typeface.BOLD);
                            countryTextView.setPadding(0, -10, 0, 8);
                            containerLayout.addView(countryTextView);

                            FlexboxLayout logoLayout = new FlexboxLayout(this);
                            logoLayout.setLayoutParams(new FlexboxLayout.LayoutParams(
                                    FlexboxLayout.LayoutParams.MATCH_PARENT,
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                            ));
                            logoLayout.setFlexWrap(FlexWrap.WRAP);
                            logoLayout.setJustifyContent(JustifyContent.FLEX_START);
                            logoLayout.setAlignItems(AlignItems.CENTER);

                            JSONArray manufacturerArray = countryObject.getJSONArray("manufacturers");
                            for (int j = 0; j < manufacturerArray.length(); j++) {
                                JSONObject manufacturer = manufacturerArray.getJSONObject(j);
                                String logoURL = manufacturer.getString("logoURL");
                                String logoName = manufacturer.getString("name");

                                ImageView logoImage = new ImageView(this);
                                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(200, 200);
                                params.setMargins(16, 8, 16, 8);
                                logoImage.setLayoutParams(params);
                                Glide.with(this).load(logoURL).into(logoImage);

                                logoImage.setOnClickListener(v -> {
                                    SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
                                    String savedUserId = pref.getString("userId", null);

                                    Intent intent;
                                    if (savedUserId != null && !savedUserId.isEmpty()) {
                                        intent = new Intent(MainPage.this, CarImgList.class);
                                        intent.putExtra("userId", savedUserId);
                                        intent.putExtra("logoName", logoName);
                                    } else {
                                        intent = new Intent(MainPage.this, CarImgList.class);
                                        intent.putExtra("logoName", logoName);
                                    }
                                    startActivity(intent);
                                });

                                logoLayout.addView(logoImage);
                            }
                            containerLayout.addView(logoLayout);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
