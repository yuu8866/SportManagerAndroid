package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

/**
 * 修改个人信息页面
 *
 * ⚠️ 你之前报错的根因：把 person_borrow 的代码误粘贴到了 UserUpdateInfo.java
 * 导致“public class person_borrow 应该声明在 person_borrow.java”
 */
public class UserUpdateInfo extends AppCompatActivity {

    private EditText username, pwd_ed, birthday, phone, sex;
    private TextView user_ed;
    private Button update_bt, back;
    private String uname2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_info);
        init();
    }

    @SuppressLint("Range")
    private void init() {
        user_ed = findViewById(R.id.u_name);
        pwd_ed = findViewById(R.id.u_password);
        username = findViewById(R.id.useu_name);
        birthday = findViewById(R.id.u_birthday);
        phone = findViewById(R.id.u_phone);
        sex = findViewById(R.id.u_sex);
        update_bt = findViewById(R.id.u_confirm);
        back = findViewById(R.id.update_user_back);

        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        uname2 = perf.getString("users", "");

        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.queryname(uname2);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    // 用户名不允许修改
                    user_ed.setText(cursor.getString(cursor.getColumnIndex("user")));
                    pwd_ed.setText(cursor.getString(cursor.getColumnIndex("password")));
                    username.setText(cursor.getString(cursor.getColumnIndex("name")));
                    birthday.setText(cursor.getString(cursor.getColumnIndex("birthday")));
                    phone.setText(cursor.getString(cursor.getColumnIndex("phone")));
                    sex.setText(cursor.getString(cursor.getColumnIndex("sex")));
                }
            } finally {
                cursor.close();
            }
        }

        // 修改按钮
        update_bt.setOnClickListener(view -> {
            String strpwd = pwd_ed.getText().toString();
            String uname = username.getText().toString();
            String birth = birthday.getText().toString();
            String phonenum = phone.getText().toString();
            String usersex = sex.getText().toString();

            SQLiteDatabase db = help.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", uname);
            values.put("password", strpwd);
            values.put("sex", usersex);
            values.put("phone", phonenum);
            values.put("birthday", birth);

            db.update("admin", values, "user=?", new String[]{String.valueOf(uname2)});
            Toast.makeText(UserUpdateInfo.this, "信息修改成功", Toast.LENGTH_LONG).show();
        });

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
}
