package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class collectActivity extends AppCompatActivity {

    private ListView listView;
    private RecyclerView rvPostCollect;
    private databaseHelp help;
    private String username;
    private Button back;

    // ✅ 器材收藏 ListView 的适配器
    private SimpleCursorAdapter collectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        listView = findViewById(R.id.show_collect);
        rvPostCollect = findViewById(R.id.rv_collect_posts);
        back = findViewById(R.id.btn_person_collect_back);

        help = new databaseHelp(getApplicationContext());

        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        username = perf.getString("users", "");

        // 1）社区收藏（帖子收藏）
        initPostCollectList();

        // 2）器材收藏（原逻辑保留）
        Cursor cursor = help.queryuser(username);

        String[] from = {"sportname", "sportauthor", "nowtime", "type", "rank", "price"};
        int[] to = {
                R.id.collect_Sport_name,
                R.id.collect_sport_author,
                R.id.collect_time,
                R.id.collect_Sport_Type,
                R.id.collect_Sport_Rank,
                R.id.collect_sport_pice
        };

        collectAdapter = new SimpleCursorAdapter(this, R.layout.collect_item, cursor, from, to, 0) {
            @Override
            public void bindView(android.view.View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                // 1) 显示图片（BLOB → Bitmap）
                ImageView img = view.findViewById(R.id.collect_sport_info_img);
                int imgIndex = cursor.getColumnIndex("img");
                if (imgIndex != -1) {
                    byte[] blob = cursor.getBlob(imgIndex);
                    if (blob != null) {
                        img.setImageBitmap(byteToBitmap(blob));
                    }
                }

                // 2) 获取 collect 表的主键 _id（用于删除）
                int idIndex = cursor.getColumnIndex("_id");
                final int collectId = (idIndex != -1) ? cursor.getInt(idIndex) : -1;

                // 3) 右侧“取消收藏”按钮
                Button btnCancel = view.findViewById(R.id.btn_cancel_collect);
                if (btnCancel == null) {
                    return;
                }

                btnCancel.setOnClickListener(v -> {
                    if (collectId == -1) {
                        Toast.makeText(collectActivity.this, "取消收藏失败：找不到记录ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new AlertDialog.Builder(collectActivity.this)
                            .setTitle("取消收藏")
                            .setMessage("确定取消收藏该器材吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                help.delcollect(collectId);

                                // 重新查询并刷新列表
                                Cursor newCursor = help.queryuser(username);
                                collectAdapter.changeCursor(newCursor);

                                Toast.makeText(collectActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
            }
        };

        listView.setAdapter(collectAdapter);

        back.setOnClickListener(v -> finish());
    }

    /**
     * 我的收藏 - 社区收藏：读取 community_post_collect，并支持取消收藏 / 点击进入详情
     */
    private void initPostCollectList() {
        rvPostCollect.setLayoutManager(new LinearLayoutManager(this));

        Cursor c = help.queryPostCollectCursor(username);
        java.util.ArrayList<PostCollectAdapter.CollectPostItem> list = new java.util.ArrayList<>();
        try {
            if (c != null) {
                while (c.moveToNext()) {
                    PostCollectAdapter.CollectPostItem item = new PostCollectAdapter.CollectPostItem();
                    int idxCollectId = c.getColumnIndex("_id");
                    int idxPostId = c.getColumnIndex("post_id");
                    int idxTitle = c.getColumnIndex("title");
                    int idxUser = c.getColumnIndex("username");
                    int idxTime = c.getColumnIndex("create_time");

                    item.collectId = idxCollectId != -1 ? c.getLong(idxCollectId) : -1;
                    item.postId = idxPostId != -1 ? c.getLong(idxPostId) : -1;
                    item.title = idxTitle != -1 ? c.getString(idxTitle) : "";
                    item.username = idxUser != -1 ? c.getString(idxUser) : "";
                    item.time = idxTime != -1 ? c.getString(idxTime) : "";
                    list.add(item);
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }

        PostCollectAdapter adapter = new PostCollectAdapter(this, list, new PostCollectAdapter.OnItemActionListener() {
            @Override
            public void onOpenDetail(long postId) {
                Intent intent = new Intent(collectActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }

            @Override
            public void onCancelCollect(long collectId) {
                new AlertDialog.Builder(collectActivity.this)
                        .setTitle("取消收藏")
                        .setMessage("确定取消收藏该帖子吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            help.delPostCollectByCollectId(collectId);
                            Toast.makeText(collectActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            // 刷新
                            initPostCollectList();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        rvPostCollect.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (collectAdapter != null) {
            Cursor c = collectAdapter.getCursor();
            if (c != null && !c.isClosed()) c.close();
        }
    }
}
