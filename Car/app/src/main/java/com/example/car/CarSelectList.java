package com.example.car;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
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

public class CarSelectList extends Bottombar {

    private GridLayout gridLayout;
    private String Model_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_selectlist);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNav, this);

        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedUserId = pref.getString("userId", null);
        if (savedUserId != null && !savedUserId.isEmpty()) {
            fetchAlertCountAndShowBadge(this, bottomNav, savedUserId); // 바로 호출
        }
        gridLayout = findViewById(R.id.gridLayout);

        Intent intent = getIntent();
        Model_Name = intent.getStringExtra("carName");

        if (Model_Name != null) {
            fetchCarListByManufacturer(Model_Name);
        }

        // Log.d("받은 Model_Name", Model_Name);

        ImageButton btnAlarm = findViewById(R.id.btn_alarm);
        btnAlarm.setOnClickListener(view -> {
            String userId = pref.getString("userId", null);

            if (userId == null) {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {
                showAlarmCheckboxDialog(this, userId, Model_Name);
            }
        });


    }
    // 알림버튼 클릭 후 나오는 라디오 박스 정보 select
    private void showAlarmCheckboxDialog(Context context, String userId, String modelName) {
        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/get_full_model_names.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "Model_Name=" + URLEncoder.encode(modelName, "UTF-8");
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes("UTF-8"));
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray fullNames = new JSONArray(result.toString());

                String[] items = new String[fullNames.length()];            // UI에 보여줄 문자열 (모델명 + 가격)
                String[] fullModelNames = new String[fullNames.length()];   // 저장용 Full_Model_Name
                String[] prices = new String[fullNames.length()];           // 저장용 가격

                for (int i = 0; i < fullNames.length(); i++) {
                    JSONObject obj = fullNames.getJSONObject(i);

                    String fullModelNameOnly = obj.getString("Full_Model_Name");
                    String priceOnly = obj.getString("Price");

                    fullModelNames[i] = fullModelNameOnly;
                    prices[i] = priceOnly;
                    items[i] = fullModelNameOnly + " (" + priceOnly + "만원)";
                }



                new Handler(Looper.getMainLooper()).post(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    TextView title = new TextView(context);
                    title.setText("알림 받을 차량을 선택하세요 * 모델별 최저가격");
                    title.setPadding(40, 40, 40, 20);
                    title.setTextSize(10);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    builder.setCustomTitle(title);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_multichoice_custom, null);
                    ListView listView = dialogView.findViewById(R.id.listView);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_checkbox, items);
                    listView.setAdapter(adapter);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                    builder.setView(dialogView);
                    builder.setPositiveButton("확인", (dialog, which) -> {
                        for (int i = 0; i < items.length; i++) {
                            if (listView.isItemChecked(i)) {
                                String selectedFullModel = fullModelNames[i];
                                String selectedPrice = prices[i];
                                saveAlarmToServer(userId, selectedFullModel, selectedPrice); // 이렇게 전송
                            }
                        }
                        Toast.makeText(context, "알림 설정 완료", Toast.LENGTH_SHORT).show();
                    });


                    builder.setNegativeButton("뒤로가기", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // 🔽 버튼 글자 크기 조정
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(10);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(10);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    // 알림 Insert
    private void saveAlarmToServer(String userId, String selectedFullModel, String selectedPrice) {
        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/save_alarm.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "userId=" + URLEncoder.encode(userId, "UTF-8") +
                        "&Full_Model_Name=" + URLEncoder.encode(selectedFullModel, "UTF-8") +
                        "&Price=" + URLEncoder.encode(selectedPrice, "UTF-8") +
                        "&Use_YN=Y";
                Log.d("saveAlarmToServer", userId);
                Log.d("saveAlarmToServer", selectedFullModel);
                Log.d("saveAlarmToServer", selectedPrice);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes("UTF-8"));
                }

                conn.getInputStream().close(); // 응답은 처리하지 않음

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    private void fetchCarListByManufacturer(String Model_Name) {
        new Thread(() -> {
            try {
                URL url = new URL("https://sdy9716.mycafe24.com/Car_SelectList.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "Model_Name=" + URLEncoder.encode(Model_Name, "UTF-8");
                // Log.d("postData", postData);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                connection.connect();

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                String responseStr = result.toString();
                // Log.d("서버 응답", responseStr);

                // 에러 체크
                if (responseStr.trim().startsWith("{")) {
                    JSONObject responseJson = new JSONObject(responseStr);
                    if (responseJson.has("error")) {
                        String errorMsg = responseJson.getString("error");
                        // Log.e("서버 에러", errorMsg);
                        return;
                    }
                }

                JSONArray dataArray = new JSONArray(responseStr);

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject car = dataArray.getJSONObject(j);
                            String carFullName = car.getString("Full_Model_Name");
                            String ncarurl = car.getString("URL");
                            String imgUrl = car.optString("Car_Img_URL", "images/default.png"); // 기본 이미지 경로
                            String price = car.getString("Price");
                            String mileage = car.getString("Mileage");
                            String location = car.getString("Location");

                            // Glide 로 이미지를 바로 imgUrl을 사용
                            // Log.d("imgUrl : " , imgUrl);

                            CardView cardView = new CardView(CarSelectList.this);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = 0;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                            params.setMargins(8, 8, 8, 8);
                            cardView.setLayoutParams(params);
                            cardView.setRadius(8);
                            cardView.setCardElevation(8);

                            LinearLayout layout = new LinearLayout(CarSelectList.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(8, 8, 8, 8);
                            layout.setGravity(Gravity.CENTER_HORIZONTAL);

                            ImageView imageView = new ImageView(CarSelectList.this);
                            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(500, 335);
                            imageView.setLayoutParams(imgParams);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(CarSelectList.this).load(imgUrl).error(R.drawable.ic_error).into(imageView); // imgUrl을 사용

                            // 👉 이미지 클릭 시 웹페이지 이동
                            imageView.setOnClickListener(v -> {
                                if (!ncarurl.isEmpty()) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ncarurl));
                                    startActivity(intent);
                                }
                            });

                            layout.addView(imageView);
                            // Log.d("https://sdy9716.mycafe24.com/", imgUrl);
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
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private TextView makeText(String text, boolean isTitle) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(isTitle ? 10 : 8);
        textView.setTypeface(null, isTitle ? Typeface.BOLD : Typeface.NORMAL);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(0, 4, 0, 4);
        return textView;
    }


}
