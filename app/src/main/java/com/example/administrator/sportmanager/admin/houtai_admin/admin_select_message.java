package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;

/**
 * 查询信息页面
 */
public class admin_select_message extends AppCompatActivity {
private ImageButton back_bt,sport_info,borrow_bt,pay_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_select_message);
        inti();//界面初始化；
    }

    private void inti() {
        back_bt=(ImageButton)findViewById(R.id.sel_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(admin_select_message.this,admin_content.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        sport_info=(ImageButton)findViewById(R.id.ad_select_sports);
        sport_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(admin_select_message.this, admin_select_sports.class);
                startActivity(intent);
            }
        });
        //查询借书信息
        borrow_bt=(ImageButton)findViewById(R.id.ad_select_brrow);
        borrow_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(admin_select_message.this,admin_borrow_info.class);
                startActivity(intent);
            }
        });

    }
}
