package com.example.administrator.sportmanager.admin.houtai_admin;

import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.appbar.MaterialToolbar;

public class select_user_admin extends AppCompatActivity {

    private ListView listView;
    private EditText etKeyword;
    private Button btnSearch, btnReset;
    private SimpleCursorAdapter adapter;
    private databaseHelp help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_admin);

        help = new databaseHelp(getApplicationContext());

        MaterialToolbar toolbar = findViewById(R.id.adminUserSelectTopBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        listView = findViewById(R.id.sel_reader_list);
        etKeyword = findViewById(R.id.et_keyword);
        btnSearch = findViewById(R.id.btn_search);
        btnReset = findViewById(R.id.btn_reset);

        // ✅ 默认显示全部用户
        loadAllUsers();

        // ✅ 搜索
        btnSearch.setOnClickListener(v -> {
            String keyword = etKeyword.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadAllUsers();
            } else {
                loadUsersByKeyword(keyword);
            }
        });

        // ✅ 重置
        btnReset.setOnClickListener(v -> {
            etKeyword.setText("");
            loadAllUsers();
        });
    }

    private void loadAllUsers() {
        Cursor cursor = help.queryUsersWithMemberInfo();
        bindList(cursor);
    }

    private void loadUsersByKeyword(String keyword) {
        Cursor cursor = help.searchUsersWithMemberInfo(keyword);
        bindList(cursor);
    }

    private void bindList(Cursor cursor) {
        String[] from = {"user", "password", "name", "sex", "birthday", "member_status", "phone"};
        int[] to = {R.id.read_user, R.id.read_pwd, R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_member_status, R.id.read_phone};

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this, R.layout.select_user_item, cursor, from, to, 0);
            listView.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }
}
