package com.example.car;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivitySingup extends AppCompatActivity {


    private EditText etUserId, etPassword, etName, etPhone, etBirthDate;
    private RadioGroup rgGender;
    private Spinner spinnerArea0, spinnerArea1;
    private Button btnCheckId, btnSignup;

    private String[] area0 = {"시/도 선택", "서울특별시", "인천광역시", "대전광역시", "광주광역시", "대구광역시", "울산광역시", "부산광역시", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주도"};

    private String[][] area1 = {
            {}, // 0: "시/도 선택"
            {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"}, // 1: 서울
            {"계양구", "남구", "남동구", "동구", "부평구", "서구", "연수구", "중구", "강화군", "옹진군"}, // 2: 인천
            {"대덕구", "동구", "서구", "유성구", "중구"}, // 3: 대전
            {"광산구", "남구", "동구", "북구", "서구"}, // 4: 광주
            {"남구", "달서구", "동구", "북구", "서구", "수성구", "중구", "달성군"}, // 5: 대구
            {"남구", "동구", "북구", "중구", "울주군"}, // 6: 울산
            {"강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구", "기장군"}, // 7: 부산
            {"고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시", "화성시", "가평군", "양평군", "여주군", "연천군"}, // 8: 경기도
            {"강릉시", "동해시", "삼척시", "속초시", "원주시", "춘천시", "태백시", "고성군", "양구군", "양양군", "영월군", "인제군", "정선군", "철원군", "평창군", "홍천군", "화천군", "횡성군"}, // 9: 강원도
            {"제천시", "청주시", "충주시", "괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "증평군", "진천군", "청원군"}, // 10: 충북
            {"계룡시", "공주시", "논산시", "보령시", "서산시", "아산시", "천안시", "금산군", "당진군", "부여군", "서천군", "연기군", "예산군", "청양군", "태안군", "홍성군"}, // 11: 충남
            {"군산시", "김제시", "남원시", "익산시", "전주시", "정읍시", "고창군", "무주군", "부안군", "순창군", "완주군", "임실군", "장수군", "진안군"}, // 12: 전북
            {"광양시", "나주시", "목포시", "순천시", "여수시", "강진군", "고흥군", "곡성군", "구례군", "담양군", "무안군", "보성군", "신안군", "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군", "해남군", "화순군"}, // 13: 전남
            {"경산시", "경주시", "구미시", "김천시", "문경시", "상주시", "안동시", "영주시", "영천시", "포항시", "고령군", "군위군", "봉화군", "성주군", "영덕군", "영양군", "예천군", "울릉군", "울진군", "의성군", "청도군", "청송군", "칠곡군"}, // 14: 경북
            {"거제시", "김해시", "마산시", "밀양시", "사천시", "양산시", "진주시", "진해시", "창원시", "통영시", "거창군", "고성군", "남해군", "산청군", "의령군", "창녕군", "하동군", "함안군", "함양군", "합천군"}, // 15: 경남
            {"서귀포시", "제주시", "남제주군", "북제주군"} // 16: 제주
    };

    private boolean isIdChecked = false; // ID 중복 체크 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 초기화
        spinnerArea0 = findViewById(R.id.spinner_city);
        spinnerArea1 = findViewById(R.id.spinner_district);
        etUserId = findViewById(R.id.et_user_id);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etBirthDate = findViewById(R.id.et_birth_date);
        btnCheckId = findViewById(R.id.btn_check_id);
        btnSignup = findViewById(R.id.btn_signup);
        rgGender = findViewById(R.id.rg_gender); // 성별 RadioGroup 추가

        btnSignup.setEnabled(false); // 기본은 비활성화

        // 중복 체크 버튼 눌렀을 때 동작
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = etUserId.getText().toString().trim();
                if (userId.isEmpty()) {
                    Toast.makeText(ActivitySingup.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkIdDuplication(userId); // ID 중복 체크
            }
        });

        // 회원가입 버튼 눌렀을 때 동작
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isIdChecked) {
                    Toast.makeText(ActivitySingup.this, "아이디 중복 확인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userId = etUserId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String birthDate = etBirthDate.getText().toString().trim();
                String city = spinnerArea0.getSelectedItem().toString();
                String district = spinnerArea1.getSelectedItem().toString();

                // 성별 값 가져오기 (M 또는 W로 변환)
                String gender = ((RadioButton) findViewById(rgGender.getCheckedRadioButtonId())).getText().toString();
                if (gender.equals("남성")) {
                    gender = "M";
                } else if (gender.equals("여성")) {
                    gender = "W";
                }

                // 회원가입 처리
                sendSignupData(userId, password, name, birthDate, city, district, phone, gender); // 성별 포함
            }
        });

        // 시/도 Spinner 설정
        ArrayAdapter<String> adapterArea0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, area0);
        adapterArea0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea0.setAdapter(adapterArea0);

        // 시/도 선택 시 구/군 업데이트
        spinnerArea0.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateArea1(position); // 구/군 업데이트
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    // ID 중복 체크 함수
    private void checkIdDuplication(String userId) {
        String serverUrl = "https://sdy9716.mycafe24.com/Customer_id_Ch.php"; // 서버 URL Customer_id_Ch.php

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("서버 응답", response);  // 서버에서 받은 응답 출력

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isDuplicate = jsonResponse.getBoolean("isDuplicate");

                            if (isDuplicate) {
                                Toast.makeText(ActivitySingup.this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivitySingup.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                isIdChecked = true; // 중복 체크 성공 시 회원가입 버튼 활성화
                                btnSignup.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("서버 응답2", response);
                            Toast.makeText(ActivitySingup.this, "아이디 중복 확인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivitySingup.this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", userId);
                return params;
            }
        };

        Volley.newRequestQueue(ActivitySingup.this).add(stringRequest);
    }

    // 구/군 업데이트
    private void updateArea1(int position) {
        ArrayAdapter<String> adapterArea1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, area1[position]);
        adapterArea1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea1.setAdapter(adapterArea1);
    }

    // 회원가입 데이터 전송
    private void sendSignupData(String userId, String password, String name, String birthDate, String city, String district, String phone, String gender) {
        String serverUrl = "https://sdy9716.mycafe24.com/Singup.php"; // 서버 URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            Toast.makeText(ActivitySingup.this, message, Toast.LENGTH_SHORT).show();

                            // 회원가입 성공 시 처리 (예: 로그인 화면으로 이동)
                            if (jsonResponse.getBoolean("success")) {
                                finish(); // 회원가입 후 화면 종료
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ActivitySingup.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivitySingup.this, "회원가입 서버 연결 오류", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("password", password);
                params.put("name", name);
                params.put("birth_date", birthDate);
                params.put("city", city);
                params.put("district", district);
                params.put("phone", phone);
                params.put("gender", gender); // 성별 추가
                return params;
            }
        };

        Volley.newRequestQueue(ActivitySingup.this).add(stringRequest);
    }
}