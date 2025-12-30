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
 * 管理员添加读者
 */
public class admin_add_users extends AppCompatActivity {
    private EditText user_ed, username, pwd_ed, birthday, phone, sex;
    private Button addreader, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_users);
        init();//初始化界面
    }

    private void init() {
        user_ed = (EditText) findViewById(R.id.r_name);
        pwd_ed = (EditText) findViewById(R.id.r_password);
        username = findViewById(R.id.user_name);
        birthday = findViewById(R.id.r_birthday);
        phone = findViewById(R.id.r_phone);
        sex = findViewById(R.id.r_sex);
        addreader = (Button) findViewById(R.id.r_register);
        //添加读者按钮的事件监听
        addreader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String struser = user_ed.getText().toString().trim();
                String strpwd = pwd_ed.getText().toString().trim();
                String uname = username.getText().toString().trim();
                String birth = birthday.getText().toString().trim();
                String phonenum = phone.getText().toString().trim();
                String usersex = sex.getText().toString().trim();

                // 手机号必须 11 位数字
                if (!phonenum.matches("\\d{11}")) {
                    Toast.makeText(admin_add_users.this, "手机号必须为11位数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 密码至少 6 位
                if (!strpwd.matches("\\d.{6,}")) {
                    Toast.makeText(admin_add_users.this, "密码至少6位数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 性别只能 男/女
                if (!("男".equals(usersex) || "女".equals(usersex))) {
                    Toast.makeText(admin_add_users.this, "性别只能填写男或女", Toast.LENGTH_SHORT).show();
                    return;
                }

                //验证用户名是否存在
                databaseHelp help = new databaseHelp(getApplicationContext());
                SQLiteDatabase db = help.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("user", struser);
                values.put("name", uname);
                values.put("password", strpwd);
                values.put("sex", usersex);
                values.put("phone", phonenum);
                values.put("birthday", birth);
                Cursor cursor = db.query("admin", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        int userColumnIndex = cursor.getColumnIndex("user");
                        if (userColumnIndex >= 0) {
                            String username = cursor.getString(userColumnIndex);
                            if (username.equals(user_ed.getText().toString())) {
                                Toast.makeText(admin_add_users.this, "用户名已存在", Toast.LENGTH_LONG).show();
                                ((EditText) findViewById(R.id.r_name)).setText("");
                                return;
                            }
                        } else {
                            // 可选：处理字段不存在的情况（如日志或提示）
                        }

                    } while (cursor.moveToNext());

                }
                cursor.close();
                help.insert(values);
                Toast.makeText(admin_add_users.this, "用户添加成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(admin_add_users.this, admin_manager_user.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }

        });
        back = findViewById(R.id.add_reader_back_bt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_add_users.this, admin_manager_user.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });

    }
}
