package com.example.car;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AlarmList extends Bottombar {
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNav, this);

        String userId = getIntent().getStringExtra("userId");
        Log.d("AlarmList", "받은 userId: " + userId);

        getAlarmListSelect(userId);
    }

    private void getAlarmListSelect(String userId) {
        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/Alarm_Select.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "userId=" + URLEncoder.encode(userId, "UTF-8");

                // 위에서 만든 postData를 서버로 전송합니다.
                // try-with-resources 문을 사용하여 OutputStream을 자동으로 닫습니다.
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                connection.connect();
                // InputStream → BufferedReader를 통해 텍스트 형식으로 한 줄씩 읽습니다.
                // 한 줄씩 읽어서 result 문자열로 응답 결과를 누적합니다.
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // 응답을 문자열로 바꾸고, 로그로 출력합니다.
                String responseStr = result.toString();
                Log.d("서버 응답", responseStr);

                //JSON 응답이 {로 시작되면, 에러 JSON으로 판단하고 "error" 키가 있으면 로그 출력 후 함수 종료합니다.
                if (responseStr.trim().startsWith("{")) {
                    JSONObject responseJson = new JSONObject(responseStr);
                    if (responseJson.has("error")) {
                        String errorMsg = responseJson.getString("error");
                        Log.e("서버 에러", errorMsg);
                        return;
                    }
                }
                //응답을 JSON 배열로 변환합니다. (
                JSONArray dataArray = new JSONArray(responseStr);

                // 네트워크 작업이 끝났으니, UI 작업은 메인 쓰레드에서 하기 위해 Handler를 사용합니다.
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                        List<AlarmItem> alarmList = new ArrayList<>();
                        // 받아온 데이터 목록을 하나씩 반복합니다. JSONObject는 각각의 alarm 데이터입니다.
                        // 기존 코드 안의 반복문 안쪽에 이 부분 추가
                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject alarm = dataArray.getJSONObject(j);
                            String carFullName = alarm.getString("Full_Model_Name");
                            String price = alarm.getString("Price");
                            String date_reg = alarm.getString("reg_date");
                            int alertCount = Integer.parseInt(alarm.getString("AlertCount"));

                            Log.d("AlarmTest Full_Model_Name", carFullName);
                            Log.d("AlarmTest price", price);
                            Log.d("AlarmTest date_reg", date_reg);
                            Log.d("AlarmList", "알림 데이터: " + alarmList.toString());

                            alarmList.add(new AlarmItem(carFullName, price, date_reg, alertCount)); // 이 줄 추가!
                        }

                        // AlarmAdapter와 RecyclerView 설정
                        AlarmAdapter adapter = new AlarmAdapter(alarmList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AlarmList.this));
                        recyclerView.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                // Log.d("postData", postData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private TextView makeText(String text, boolean isTitle) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(isTitle ? 8 : 6);
        textView.setTypeface(null, isTitle ? Typeface.BOLD : Typeface.NORMAL);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(0, 4, 0, 4);
        return textView;
    }


}