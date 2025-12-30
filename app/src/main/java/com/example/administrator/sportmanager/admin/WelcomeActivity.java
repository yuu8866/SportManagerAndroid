package com.example.administrator.sportmanager.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.sportmanager.R;

public class WelcomeActivity extends Activity {
    private static int SPLASH_DISPLAY_LENGTH = 3000; // 3秒
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 移除WelcomeActivity，避免在MainActivity之上
                finish();

                // 启动MainActivity
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}