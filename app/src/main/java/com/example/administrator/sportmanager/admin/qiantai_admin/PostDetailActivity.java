package com.example.administrator.sportmanager.admin.qiantai_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvUser, tvTitle, tvContent, tvTime;
    private databaseHelp dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvUser = findViewById(R.id.tv_detail_user);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvTime = findViewById(R.id.tv_detail_time);

        dbHelper = new databaseHelp(this);

        long postId = getIntent().getLongExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Post post = dbHelper.queryCommunityPostById(postId);
        if (post == null) {
            Toast.makeText(this, "帖子读取失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUser.setText(post.getUsername());
        tvTitle.setText(post.getTitle());
        tvContent.setText(post.getContent());
        tvTime.setText(post.getCreateTime());
    }
}
