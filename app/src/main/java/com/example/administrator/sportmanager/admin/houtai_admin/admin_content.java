package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.MainActivity;

public class admin_content extends AppCompatActivity {

    private ImageButton selct_bt, manReader_bt, mansport_bt, managePost_bt, back_bt;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_content);
        init();
    }

    private void init() {
        selct_bt = findViewById(R.id.ad_select);
        manReader_bt = findViewById(R.id.ad_manager_reader);
        mansport_bt = findViewById(R.id.ad_manager_sport);
        managePost_bt = findViewById(R.id.ad_manager_post);
        back_bt = findViewById(R.id.content_back);

        selct_bt.setOnClickListener(v ->
                startActivity(new Intent(admin_content.this, admin_select_message.class)));

        manReader_bt.setOnClickListener(v ->
                startActivity(new Intent(admin_content.this, admin_manager_user.class)));

        mansport_bt.setOnClickListener(v ->
                startActivity(new Intent(admin_content.this, admin_manager_sports.class)));

        managePost_bt.setOnClickListener(v ->
                startActivity(new Intent(admin_content.this, admin_manage_posts.class)));

        back_bt.setOnClickListener(v -> {
            Intent intent = new Intent(admin_content.this, MainActivity.class);
            startActivity(intent);
            ActivityCollector.finishAll();
        });
    }
}
