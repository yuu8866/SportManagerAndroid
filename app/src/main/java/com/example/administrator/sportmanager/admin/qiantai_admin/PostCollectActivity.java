package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏（社区帖子收藏）
 * - 展示当前账号收藏的帖子
 * - 支持点击进入帖子详情、取消收藏
 */
public class PostCollectActivity extends AppCompatActivity {

    private databaseHelp help;
    private String username;

    private RecyclerView rv;
    private TextView tvEmpty;

    private final List<PostCollectAdapter.CollectPostItem> list = new ArrayList<>();
    private PostCollectAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_collect);

        help = new databaseHelp(getApplicationContext());
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        username = sp.getString("users", "");

        rv = findViewById(R.id.rv_collect_posts);
        tvEmpty = findViewById(R.id.tv_collect_empty);

        findViewById(R.id.btn_collect_back).setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostCollectAdapter(this, list, new PostCollectAdapter.OnItemActionListener() {
            @Override
            public void onOpenDetail(long postId) {
                Intent i = new Intent(PostCollectActivity.this, PostDetailActivity.class);
                i.putExtra("post_id", postId);
                startActivity(i);
            }

            @Override
            public void onCancelCollect(long collectId) {
                help.deletePostCollectById(collectId);
                Toast.makeText(PostCollectActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });
        rv.setAdapter(adapter);

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        list.clear();

        if (username == null || username.trim().isEmpty()) {
            tvEmpty.setText("请先登录后查看收藏");
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            rv.setVisibility(android.view.View.GONE);
            adapter.notifyDataSetChanged();
            return;
        }

        List<databaseHelp.PostCollectRow> rows = help.queryPostCollectList(username);
        if (rows == null || rows.isEmpty()) {
            tvEmpty.setText("暂无收藏，去社区逛逛吧～");
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            rv.setVisibility(android.view.View.GONE);
            adapter.notifyDataSetChanged();
            return;
        }

        for (databaseHelp.PostCollectRow r : rows) {
            Post p = help.queryCommunityPostById(r.postId);
            if (p == null) continue;

            PostCollectAdapter.CollectPostItem item = new PostCollectAdapter.CollectPostItem();
            item.collectId = r.collectId;
            item.postId = r.postId;
            item.title = p.getTitle();
            item.username = p.getUsername();
            item.time = r.collectTime;
            list.add(item);
        }

        tvEmpty.setVisibility(android.view.View.GONE);
        rv.setVisibility(android.view.View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
