package com.example.car;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "3fbcda37df6c8200964f637b0ec3a28b");
    }
}