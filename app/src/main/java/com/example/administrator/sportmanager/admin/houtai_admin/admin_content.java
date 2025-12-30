package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi; // 已修正
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.MainActivity;


public class admin_content extends AppCompatActivity {
    private long mExitTime;
    private ImageButton selct_bt, manReader_bt, mansport_bt, back_bt;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_content);
        init();//界面初始化

    }

    private void init() {
        //查询信息
        selct_bt = (ImageButton) findViewById(R.id.ad_select);
        selct_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_content.this, com.example.administrator.sportmanager.admin.houtai_admin.admin_select_message.class);
                startActivity(intent);
            }
        });
        //管理用户
        manReader_bt = (ImageButton) findViewById(R.id.ad_manager_reader);
        manReader_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_content.this, admin_manager_user.class);
                startActivity(intent);
            }
        });
        //管理运动器械
        mansport_bt = (ImageButton) findViewById(R.id.ad_manager_sport);
        mansport_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_content.this, admin_manager_sports.class);
                startActivity(intent);
            }
        });
        back_bt = findViewById(R.id.content_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_content.this, MainActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });


    }

}
