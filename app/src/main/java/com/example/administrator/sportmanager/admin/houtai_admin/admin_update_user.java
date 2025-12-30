package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;


/**
 * 修改读者信息的页面
 */

public class admin_update_user extends AppCompatActivity {
    private EditText user_ed, username, pwd_ed, birthday, phone, sex;
    private Button update_bt;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_user);
        init();//初始化界面
    }

    private void init() {
        // 将id返回的记录查询在edittext
        user_ed = (EditText) findViewById(R.id.r_name);
        pwd_ed = (EditText) findViewById(R.id.r_password);
        username = findViewById(R.id.user_name);
        birthday = findViewById(R.id.r_birthday);
        phone = findViewById(R.id.r_phone);
        sex = findViewById(R.id.r_sex);
        update_bt = (Button) findViewById(R.id.r_register);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");

        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.queryid(id);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            // 安全地获取列索引并设置文本
            int userIndex = cursor.getColumnIndex("user");
            if (userIndex != -1) {
                user_ed.setText(cursor.getString(userIndex));
            }

            int passwordIndex = cursor.getColumnIndex("password");
            if (passwordIndex != -1) {
                pwd_ed.setText(cursor.getString(passwordIndex));
            }

            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                username.setText(cursor.getString(nameIndex));
            }

            int birthdayIndex = cursor.getColumnIndex("birthday");
            if (birthdayIndex != -1) {
                birthday.setText(cursor.getString(birthdayIndex));
            }

            int phoneIndex = cursor.getColumnIndex("phone");
            if (phoneIndex != -1) {
                phone.setText(cursor.getString(phoneIndex));
            }

            int sexIndex = cursor.getColumnIndex("sex");
            if (sexIndex != -1) {
                sex.setText(cursor.getString(sexIndex));
            }
        }

        // 修改按钮的事件监听
        update_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String struser = user_ed.getText().toString().trim();
                String strpwd = pwd_ed.getText().toString().trim();
                String uname = username.getText().toString().trim();
                String birth = birthday.getText().toString().trim();
                String phonenum = phone.getText().toString().trim();
                String usersex = sex.getText().toString().trim();

                SQLiteDatabase db = help.getWritableDatabase();

                // 手机号必须 11 位数字
                if (!phonenum.matches("\\d{11}")) {
                    Toast.makeText(admin_update_user.this, "手机号必须为11位数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 密码至少 6 位
                if (!strpwd.matches("\\d.{6,}")) {
                    Toast.makeText(admin_update_user.this, "密码至少6位数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 性别只能 男/女
                if (!("男".equals(usersex) || "女".equals(usersex))) {
                    Toast.makeText(admin_update_user.this, "性别只能填写男或女", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 用户名改成已存在的，要拦截
                Cursor c = db.query("admin",
                        new String[]{"_id"},
                        "user=? AND _id<>?",
                        new String[]{struser, String.valueOf(id)},
                        null, null, null);

                if (c.moveToFirst()) {
                    c.close();
                    Toast.makeText(admin_update_user.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                c.close();

                ContentValues values = new ContentValues();
                values.put("user", struser);
                values.put("name", uname);
                values.put("password", strpwd);
                values.put("sex", usersex);
                values.put("phone", phonenum);
                values.put("birthday", birth);

                db.update("admin", values, "_id=?", new String[]{String.valueOf(id)});

                Intent intent = new Intent(admin_update_user.this, admin_editer_users.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
    }
}

