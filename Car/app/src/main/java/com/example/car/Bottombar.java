package com.example.car;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Bottombar extends AppCompatActivity {

    protected void setupBottomNavigation(BottomNavigationView bottomNavigationView, Context context) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            SharedPreferences pref = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
            String savedUserId = pref.getString("userId", null);

            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_my) {
                if (savedUserId != null && !savedUserId.isEmpty()) {
                    intent = new Intent(context, MyPage.class);
                } else {
                    intent = new Intent(context, LoginPage.class);
                }
                context.startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_home) {
                intent = new Intent(context, MainPage.class);
                if (savedUserId != null && !savedUserId.isEmpty()) {
                    intent.putExtra("userId", savedUserId);
                }
                context.startActivity(intent);
                return true;
            }

            return true;
        });
    }
    public void fetchAlertCountAndShowBadge(Context context, BottomNavigationView navView, String userId) {
        String url = "https://sdy9716.mycafe24.com/alert_count.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int alertCount = jsonObject.getInt("alert_count");
                        showBadge(navView, alertCount);
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

    public void showBadge(BottomNavigationView navView, int count) {
        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_my);
        badge.setVisible(true);
        badge.setNumber(count);
    }

}
