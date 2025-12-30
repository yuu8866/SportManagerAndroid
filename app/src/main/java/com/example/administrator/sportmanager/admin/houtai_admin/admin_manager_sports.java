package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;

//管理员选择"运动器械管理"进入的界面

public class admin_manager_sports extends AppCompatActivity {
    private ImageButton back_bt, addsport, editsport, searchsport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager_sports);
        init();//界面初始化
    }

    private void init() {
        back_bt = (ImageButton) findViewById(R.id.mansport_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_manager_sports.this, com.example.administrator.sportmanager.admin.houtai_admin.admin_content.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        addsport = (ImageButton) findViewById(R.id.ad_add);
        addsport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_manager_sports.this, admin_add_sport.class);
                startActivity(intent);
            }
        });

    }
}
