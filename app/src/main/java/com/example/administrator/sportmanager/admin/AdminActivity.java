package com.example.administrator.sportmanager.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.houtai_admin.admin_content;

public class AdminActivity extends AppCompatActivity {
    private EditText user_ed, pwd_ed;
    private Button back_bt, login_bt;
    private CheckBox rember;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();//界面初始化
    }

    private void init() {

        user_ed = (EditText) findViewById(R.id.a_name);
        pwd_ed = (EditText) findViewById(R.id.a_password);

        rember = findViewById(R.id.admin_rmber_pwd);//记住密码
        sp = getSharedPreferences("admin", MODE_PRIVATE);
        String Rusername = sp.getString("users", "");
        String Rpassword = sp.getString("passwords", "");
        boolean choseRemember = sp.getBoolean("adminremember", false);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if (choseRemember) {
            user_ed.setText(Rusername);
            pwd_ed.setText(Rpassword);
            rember.setChecked(true);
        }

        //返回按钮的事件监听
        back_bt = (Button) findViewById(R.id.a_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new
                        Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
                com.example.administrator.sportmanager.admin.ActivityCollector.finishAll();
            }
        });
        //登录按钮的事件监听
        login_bt = (Button) findViewById(R.id.a_register);
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String struser = user_ed.getText().toString();
                String strpwd = pwd_ed.getText().toString();
                String admin = "admin";
                String pwd = "admin";
                //如果管理员输入的用户名与密码正确就登录，用户名：admin；密码：admin
                if (admin.equals(struser) && pwd.equals(strpwd)) {
                    Intent intent = new Intent(AdminActivity.this, admin_content.class);
                    startActivity(intent);
                    /*
                            将用户名存储到sharedpreferences中
                            获取用户名和密码，方便在记住密码时使用
                             */
                    SharedPreferences.Editor editor = getSharedPreferences("admin", MODE_PRIVATE).edit();
                    editor.putString("users", struser);
                    editor.putString("passwords", strpwd);
                    //是否记住密码
                    if (rember.isChecked()) {
                        editor.putBoolean("adminremember", true);
                    } else {
                        editor.putBoolean("adminremember", false);
                    }
                    editor.apply();
                } else {

                    Toast.makeText(AdminActivity.this, "用户名或密码不正确", Toast.LENGTH_LONG).show();
                    ((EditText) findViewById(R.id.a_name)).setText("");
                    ((EditText) findViewById(R.id.a_password)).setText("");
                    ((EditText) findViewById(R.id.a_name)).requestFocus();
                }

            }
        });
    }
}
