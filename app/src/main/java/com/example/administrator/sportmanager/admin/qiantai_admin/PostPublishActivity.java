package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 发布帖子页面
 */
public class PostPublishActivity extends AppCompatActivity {

    private databaseHelp help;
    private String loginUser;

    private EditText etTitle;
    private EditText etContent;
    private Button btnPublish;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_publish);

        help = new databaseHelp(getApplicationContext());

        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        loginUser = perf.getString("users", "");

        etTitle = findViewById(R.id.et_post_title);
        etContent = findViewById(R.id.et_post_content);
        btnPublish = findViewById(R.id.btn_publish_post);
        btnBack = findViewById(R.id.btn_publish_back);

        btnBack.setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(v -> doPublish());
    }

    private void doPublish() {
        if (loginUser == null || loginUser.trim().isEmpty()) {
            Toast.makeText(this, "请先登录后再发帖", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入帖子标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入帖子内容", Toast.LENGTH_SHORT).show();
            return;
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        help.insertCommunityPost(title, content, loginUser, now);

        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
