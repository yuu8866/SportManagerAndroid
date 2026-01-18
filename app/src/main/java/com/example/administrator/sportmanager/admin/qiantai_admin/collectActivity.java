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

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

/**
 * 收藏列表
 *
 * ⚠️ 你之前报错的根因：把 borrowActivity 的代码误粘贴到了 collectActivity.java
 * 导致“public class borrowActivity 应该声明在 borrowActivity.java”这类错误。
 *
 * 这里是修复后的正确版本。
 */
public class collectActivity extends AppCompatActivity {

    private ListView listView;
    private databaseHelp help;
    private String username;
    private Button back;

    // 用成员变量，方便刷新列表
    private SimpleCursorAdapter collectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        listView = findViewById(R.id.show_collect);
        back = findViewById(R.id.btn_person_collect_back);

        help = new databaseHelp(getApplicationContext());
        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        username = perf.getString("users", "");

        // 查询当前用户收藏
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

                // 显示图片（BLOB → Bitmap）
                ImageView img = view.findViewById(R.id.collect_sport_info_img);
                int imgIndex = cursor.getColumnIndex("img");
                if (imgIndex != -1) {
                    byte[] blob = cursor.getBlob(imgIndex);
                    if (blob != null) {
                        img.setImageBitmap(byteToBitmap(blob));
                    }
                }

                // 获取 collect 表主键 _id（用于删除）
                int idIndex = cursor.getColumnIndex("_id");
                final int collectId = (idIndex != -1) ? cursor.getInt(idIndex) : -1;

                // 右侧“取消收藏”按钮（你 XML 里如果还没加，会是 null，这里做保护）
                Button btnCancel = view.findViewById(R.id.btn_cancel_collect);
                if (btnCancel == null) return;

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

                                // 重新查询并刷新
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

        // ✅ 返回：永远回到“新导航页”，避免回到旧 contentActivity（无底部导航）
        back.setOnClickListener(v -> backToUserNavHome());
    }

    @Override
    public void onBackPressed() {
        backToUserNavHome();
    }

    private void backToUserNavHome() {
        Intent i = new Intent(this, UserNavActivity.class);
        i.putExtra(UserNavActivity.EXTRA_OPEN_TAB, UserNavActivity.TAB_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放 cursor（可选但推荐）
        if (collectAdapter != null) {
            Cursor c = collectAdapter.getCursor();
            if (c != null && !c.isClosed()) c.close();
        }
    }
}
