package com.example.administrator.sportmanager.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;

import com.example.administrator.sportmanager.admin.qiantai_admin.contentActivity;

public class MainActivity extends AppCompatActivity {
    private EditText user_ed, pwd_ed;
    private Button login_bt, register_bt;
    private Button im_bt;
    private CheckBox rember, auto_login;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();//界面初始化

    }
    private void init() {
        user_ed = (EditText) findViewById(R.id.name);
        pwd_ed = (EditText) findViewById(R.id.password);
        //复选框的监听事件
        rember = (CheckBox) findViewById(R.id.rmber_pwd);//记住密码
        //auto_login = (CheckBox) findViewById(R.id.auto_login);//自动登录
        sp = getSharedPreferences("data", MODE_PRIVATE);
        String Rusername = sp.getString("users", "");
        String Rpassword = sp.getString("passwords", "");
        boolean choseRemember = sp.getBoolean("remember", false);
        boolean choseAutoLogin = sp.getBoolean("autologin", false);
        //      Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if (choseRemember) {
            user_ed.setText(Rusername);
            pwd_ed.setText(Rpassword);
            rember.setChecked(true);
        }
        //注册按钮的事件监听
        register_bt = (Button) findViewById(R.id.register);
        register_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.administrator.sportmanager.admin.registerActivity.class);
                startActivity(intent);
            }
        });
        //切换按钮的事件监听--管理员
        im_bt = (Button) findViewById(R.id.admin);
        im_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.administrator.sportmanager.admin.AdminActivity.class);
                startActivity(intent);
            }
        });
        //登录按钮的事件监听
        login_bt = (Button) findViewById(R.id.login);

        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String struser = user_ed.getText().toString();
                String strpwd = pwd_ed.getText().toString();
                databaseHelp help = new databaseHelp(getApplicationContext());
                SQLiteDatabase db = help.getWritableDatabase();
                boolean login_succ = false;
                Cursor cursor = db.query("admin", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("user"));
                        @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
                        if (username.equals(struser) && password.equals(strpwd)) {
                            login_succ = true;
                            Intent intent = new Intent(MainActivity.this, contentActivity.class);
                            startActivity(intent);
                            /*
                            将用户名存储到sharedpreferences中
                            获取用户名和密码，方便在记住密码时使用
                             */
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("users", username);
                            editor.putString("passwords", password);
                            //是否记住密码
                            if (rember.isChecked()) {
                                editor.putBoolean("remember", true);
                            } else {
                                editor.putBoolean("remember", false);
                            }

                            editor.apply();
                        }

                    } while (cursor.moveToNext());

                }
                if (!login_succ) {
                    Toast.makeText(MainActivity.this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                }
                cursor.close();

            }

        });

    }
}
