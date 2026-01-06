package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class SearchActivity extends AppCompatActivity {

    private EditText input;

    // ✅ search_btn 现在是 Button（你要显示“搜索”两个字）
    private Button btnSearch;

    // ✅ 返回按钮如果你 XML 里还是 ImageButton，就继续用 ImageButton
    private ImageButton btnBack;

    private ListView listView;

    private databaseHelp help;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        help = new databaseHelp(getApplicationContext());

        input = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.search_btn);
        btnBack = findViewById(R.id.search_back);
        listView = findViewById(R.id.search_list);

        // ✅ 点击搜索按钮
        btnSearch.setOnClickListener(v -> doSearch());

        // ✅ 返回
        btnBack.setOnClickListener(v -> finish());

        // 初始：显示全部（也可以改成空）
        Cursor cursor = help.querysports();

        String[] from = {"name", "type", "user", "owner", "rank", "img", "price"};
        int[] to = {R.id.user_Sport_Name, R.id.user_Sport_Type, R.id.user_sport_author,
                R.id.user_sport_publish, R.id.user_Sport_Rank, R.id.user_sport_info_img, R.id.user_sport_pice};

        adapter = new SimpleCursorAdapter(this, R.layout.sport_item, cursor, from, to, 0);
        adapter.setViewBinder((view, c, columnIndex) -> {
            if (view.getId() == R.id.user_sport_info_img) {
                ImageView iv = (ImageView) view;
                iv.setImageBitmap(byteToBitmap(c.getBlob(columnIndex)));
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);

        // 点击结果 -> 进入详情页（复用 borrowActivity，不改它）
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // id 是 Cursor 的 _id（数据库自增），borrowActivity 里是 bundle.getInt("id")+1 查 _id
            Intent intent = new Intent(SearchActivity.this, borrowActivity.class);
            Bundle b = new Bundle();
            b.putInt("id", (int) id - 1); // 让 borrowActivity 里 +1 后刚好等于 _id
            intent.putExtras(b);
            startActivity(intent);
        });

        // 键盘搜索（回车/搜索键）
        input.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
    }

    private void doSearch() {
        String kw = input.getText().toString().trim();

        Cursor newCursor;
        if (kw.isEmpty()) {
            newCursor = help.querysports();
        } else {
            newCursor = help.searchSports(kw);
        }

        if (newCursor != null && newCursor.getCount() == 0) {
            Toast.makeText(this, "没有找到相关器材", Toast.LENGTH_SHORT).show();
        }

        // SimpleCursorAdapter.changeCursor 会自动关闭旧 cursor
        adapter.changeCursor(newCursor);
    }
}
