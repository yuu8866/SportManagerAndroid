package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 帖子详情页：顶部显示用户信息，正文结束下方显示发帖时间，并支持收藏/取消收藏
 */
public class PostDetailActivity extends AppCompatActivity {

    private databaseHelp help;
    private String loginUser;
    private long postId;

    private TextView tvUser;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvTime;
    private Button btnCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        help = new databaseHelp(getApplicationContext());

        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        loginUser = perf.getString("users", "");

        postId = getIntent().getLongExtra("post_id", -1);

        tvUser = findViewById(R.id.tv_detail_user);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvTime = findViewById(R.id.tv_detail_time);
        btnCollect = findViewById(R.id.btn_collect_post);

        Button btnBack = findViewById(R.id.btn_detail_back);
        btnBack.setOnClickListener(v -> finish());

        loadPost();

        btnCollect.setOnClickListener(v -> toggleCollect());
    }

    private void loadPost() {
        Post p = help.queryCommunityPostById(postId);
        if (p == null) {
            Toast.makeText(this, "帖子不存在或已删除", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUser.setText(p.getUsername());
        tvTitle.setText(p.getTitle());
        tvContent.setText(p.getContent());
        tvTime.setText(p.getCreateTime());

        refreshCollectButton();
    }

    private void refreshCollectButton() {
        boolean collected = help.isPostCollected(loginUser, postId);
        btnCollect.setText(collected ? "已收藏" : "收藏");
    }

    private void toggleCollect() {
        if (loginUser == null || loginUser.trim().isEmpty()) {
            Toast.makeText(this, "请先登录后再收藏", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean collected = help.isPostCollected(loginUser, postId);
        if (collected) {
            help.cancelPostCollect(loginUser, postId);
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        } else {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            help.addPostCollect(loginUser, postId, now);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        }

        refreshCollectButton();
    }
}
