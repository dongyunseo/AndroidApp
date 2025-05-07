package com.example.car;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CarImgList extends Bottombar {
    // private FlexboxLayout carLayout;
    private LinearLayout containerLayout; // 전역으로 선언
    private String logoName; // 전역 변수로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_imglist);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        setupBottomNavigation(bottomNav, this);

        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedUserId = pref.getString("userId", null);
        if (savedUserId != null && !savedUserId.isEmpty()) {
            fetchAlertCountAndShowBadge(this, bottomNav, savedUserId); // 바로 호출
        }

        // 레이아웃 연결
        containerLayout = findViewById(R.id.containerLayout);

        // 제조사 이름 받아오기
        Intent intent = getIntent();
        logoName = intent.getStringExtra("logoName");


        if (logoName != null) {
            fetchCarListByManufacturer();
        }

        Log.d("받은 logoName", logoName);
    }


    private void fetchCarListByManufacturer() {
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/Car_List.php?manufacturer_name=" + URLEncoder.encode(logoName, "UTF-8"));
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
                Log.d("dataArray : ", String.valueOf(dataArray));

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        // 제조사 이름 TextView 생성
                        TextView logoNameTextView = new TextView(this);
                        logoNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        logoNameTextView.setText(logoName);
                        logoNameTextView.setTextSize(25);
                        logoNameTextView.setTypeface(null, Typeface.BOLD);
                        logoNameTextView.setPadding(0, 16, 0, 8);
                        containerLayout.addView(logoNameTextView);


                        // 제조사 Horizontal LinearLayout 생성
                        FlexboxLayout logoLayout = new FlexboxLayout(this);
                        logoLayout.setLayoutParams(new FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.MATCH_PARENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT
                        ));
                        logoLayout.setFlexWrap(FlexWrap.WRAP);
                        logoLayout.setJustifyContent(JustifyContent.FLEX_START);
                        logoLayout.setAlignItems(AlignItems.CENTER);

                        // 차량 이미지 추가
                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject manufacturer = dataArray.getJSONObject(j);
                            String carName = manufacturer.getString("carName");
                            String imgUrl = manufacturer.getString("imgUrl");
                            String fullUrl = "https://sdy9716.mycafe24.com" + imgUrl;
                            // 수직 레이아웃 생성
                            LinearLayout itemLayout = new LinearLayout(this);
                            itemLayout.setOrientation(LinearLayout.VERTICAL);
                            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(4, 4, 4, 4); // 마진도 살짝 줄임
                            itemLayout.setLayoutParams(layoutParams);
                            itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                            // FrameLayout으로 겹치기 위한 레이아웃 생성
                            FrameLayout frameLayout = new FrameLayout(this);
                            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                                    310, 310
                            );
                            frameParams.setMargins(8, 8, 8, 8);
                            frameLayout.setLayoutParams(frameParams);

                            // 이미지뷰 추가 (바닥에 깔림)
                            ImageView logoImage = new ImageView(this);
                            FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            );
                            logoImage.setLayoutParams(imageParams);
                            logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            Glide.with(this)
                                    .load(fullUrl)
                                    .error(R.drawable.ic_error)
                                    .into(logoImage);

                            // 텍스트뷰 추가 (위에 겹쳐짐)
                            TextView carNameText = new TextView(this);
                            carNameText.setText(carName);
                            carNameText.setTextColor(Color.BLACK); // 필요 시 색상 조정
                            carNameText.setTextSize(12);
                            carNameText.setGravity(Gravity.CENTER);
                            carNameText.setIncludeFontPadding(false);
                            carNameText.setBackgroundColor(Color.parseColor("#80FFFFFF")); // 반투명 배경 (선택사항)

                            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL // 이미지 하단 중앙
                            );
                            textParams.setMargins(0, 0, 0, 8); // 아래쪽 여백
                            carNameText.setLayoutParams(textParams);


                            // FrameLayout에 이미지와 텍스트 추가
                            frameLayout.addView(logoImage);
                            frameLayout.addView(carNameText);

                            // 최종적으로 FlexboxLayout에 추가
                            logoLayout.addView(frameLayout);

                            // ✅ 다음 페이지 이동
                            logoImage.setOnClickListener(v -> {
                                SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
                                String savedUserId = pref.getString("userId", null);

                                // userId가 null이 아니면 로그인된 상태로 간주
                                if (savedUserId != null && !savedUserId.isEmpty()) {
                                    // 로그인되었다면 ID 와 차량 모델명을 CarImgList에 전송
                                    Log.d("intent MainPage", savedUserId);
                                    Intent intent = new Intent(CarImgList.this, CarSelectList.class);
                                    intent.putExtra("userId", savedUserId); // userId를 Intent에 담아 전달
                                    intent.putExtra("carName", carName);
                                    Log.d("CarSelectList Login On", carName);
                                    Log.d("CarSelectList Login On", savedUserId);
                                    startActivity(intent); // CarImgList로 이동
                                } else {
                                    // 로그인되지 않은 상태일 경우 필요한 처리를 추가할 수 있습니다.
                                    // 예를 들어 로그인 페이지로 이동
                                    Intent intent = new Intent(CarImgList.this, CarSelectList.class);
                                    intent.putExtra("carName", carName);
                                    Log.d("CarSelectList Login OFF : ", carName);
                                    startActivity(intent);
                                }
                            });
                        }
                        containerLayout.addView(logoLayout);

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
