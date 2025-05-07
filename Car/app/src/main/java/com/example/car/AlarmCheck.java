package com.example.car;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class AlarmCheck extends Bottombar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_check); // 레이아웃 파일명 맞게 설정

        // Intent에서 데이터 받기
        String modelName = getIntent().getStringExtra("modelName");
        fetchMinPriceCar(modelName);


        // 로그 찍어서 잘 받아왔는지 확인
        Log.d("AlarmCheck", "받은 modelName: " + modelName);
        // Log.d("AlarmCheck", "받은 userId: " + userId);

        // 이후 이 modelName과 userId로 서버에 요청하거나 화면에 표시하면 됩니다


        // 하단 바 기능
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNav, this);
    }
    private void fetchMinPriceCar(String modelName) {
        String urlStr = "https://sdy9716.mycafe24.com/AlarmCheck.php?modelName=" + Uri.encode(modelName);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    try {
                        GridLayout gridLayout = findViewById(R.id.gridLayout); // 레이아웃 ID 확인 필요
                        gridLayout.removeAllViews();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject car = response.getJSONObject(i);

                            String carFullName = car.getString("Full_Model_Name");
                            String price = car.getString("Price");
                            String mileage = car.getString("Mileage");
                            String location = car.getString("Location");

                            // 카드 뷰 생성
                            CardView cardView = new CardView(this);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = 0;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                            params.setMargins(8, 8, 8, 8);
                            cardView.setLayoutParams(params);
                            cardView.setRadius(8);
                            cardView.setCardElevation(8);

                            LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(8, 8, 8, 8);
                            layout.setGravity(Gravity.CENTER_HORIZONTAL);

                            layout.addView(makeText(carFullName, true));
                            layout.addView(makeText("가격: " + price + "만원", false));
                            layout.addView(makeText("주행: " + mileage, false));
                            layout.addView(makeText("지역: " + location, false));

                            cardView.addView(layout);
                            gridLayout.addView(cardView);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("AlarmCheck", "에러: " + error.getMessage())
        );

        queue.add(jsonArrayRequest);
    }
    private TextView makeText(String text, boolean isTitle) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(isTitle ? 12 : 10);
        textView.setTypeface(null, isTitle ? Typeface.BOLD : Typeface.NORMAL);
        textView.setPadding(4, 4, 4, 4);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

}
