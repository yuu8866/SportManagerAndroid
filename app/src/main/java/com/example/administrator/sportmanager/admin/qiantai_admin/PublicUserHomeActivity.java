package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.List;

public class PublicUserHomeActivity extends AppCompatActivity {

    private databaseHelp help;
    private String targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_user_home);

        help = new databaseHelp(this);
        targetUser = getIntent().getStringExtra("target_user");

        TextView tv = findViewById(R.id.tv_public_user);
        tv.setText("贴主主页：" + targetUser);

        RecyclerView rv = findViewById(R.id.rv_user_posts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Post> posts = help.queryCommunityPostsByUser(targetUser);
        rv.setAdapter(new PostAdapter(this, posts, post -> {
            PostDetailActivity.start(PublicUserHomeActivity.this, post.getId());
        }));
    }
}
