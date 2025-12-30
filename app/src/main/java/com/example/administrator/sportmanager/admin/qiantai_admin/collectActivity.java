package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

//侧边栏进入的收藏页面

public class collectActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private ListView listView;
    private databaseHelp help;
    private String username;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        listView = findViewById(R.id.show_collect);
        help = new databaseHelp(getApplicationContext());
        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        username = perf.getString("users", "");//获得当前用户名称
        //根据用户查询自己的收藏信息
        Cursor cursor=help.queryuser(username);
        String from[] = {"sportname", "sportauthor", "nowtime","type","rank","price","img"};
        int to[] = {R.id.collect_Sport_name, R.id.collect_sport_author, R.id.collect_time,R.id.collect_Sport_Type,R.id.collect_Sport_Rank,R.id.collect_sport_pice,R.id.collect_sport_info_img};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.collect_item, cursor, from, to);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.collect_sport_info_img) {
                    ImageView iconImageView = (ImageView) view;
                    //iconImageView.setImageURI(Uri.parse(cursor.getString(columnIndex)));
                    iconImageView.setImageBitmap(byteToBitmap(cursor.getBlob(columnIndex)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // id 就是 collect 表的 _id
            new AlertDialog.Builder(collectActivity.this)
                    .setTitle("取消收藏")
                    .setMessage("确定取消收藏该器材吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        help.delcollect((int) id);

                        // 刷新列表
                        Cursor newCursor = help.queryuser(username);
                        adapter.changeCursor(newCursor);

                        android.widget.Toast.makeText(
                                collectActivity.this, "已取消收藏", android.widget.Toast.LENGTH_SHORT
                        ).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });

        back = findViewById(R.id.btn_person_collect_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(collectActivity.this, com.example.administrator.sportmanager.admin.qiantai_admin.contentActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });


    }
}
