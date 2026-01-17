package com.example.administrator.sportmanager.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.qiantai_admin.UserNavActivity;

public class MainActivity extends AppCompatActivity {

    private EditText user_ed, pwd_ed;
    private Button login_bt, register_bt, im_bt;
    private CheckBox rember;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        user_ed = findViewById(R.id.name);
        pwd_ed = findViewById(R.id.password);

        rember = findViewById(R.id.rmber_pwd);
        sp = getSharedPreferences("data", MODE_PRIVATE);

        // 读取记住密码
        String Rusername = sp.getString("users", "");
        String Rpassword = sp.getString("passwords", "");
        boolean choseRemember = sp.getBoolean("remember", false);

        if (choseRemember) {
            user_ed.setText(Rusername);
            pwd_ed.setText(Rpassword);
            rember.setChecked(true);
        }

        // 注册
        register_bt = findViewById(R.id.register);
        register_bt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, registerActivity.class);
            startActivity(intent);
        });

        // 管理员入口
        im_bt = findViewById(R.id.admin);
        im_bt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        });

        // 登录
        login_bt = findViewById(R.id.login);
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String struser = user_ed.getText().toString().trim();
                String strpwd = pwd_ed.getText().toString().trim();

                if (struser.isEmpty() || strpwd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseHelp help = new databaseHelp(getApplicationContext());
                SQLiteDatabase db = help.getReadableDatabase();

                boolean login_succ = false;
                Cursor cursor = null;

                try {
                    // ✅ 只查这一条，效率更高（不用遍历整表）
                    cursor = db.rawQuery("SELECT user,password FROM admin WHERE user=? LIMIT 1",
                            new String[]{struser});

                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("user"));
                        @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));

                        if (username.equals(struser) && password.equals(strpwd)) {
                            login_succ = true;

                            // ✅ 先存 SharedPreferences（重要）
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("users", username);
                            editor.putString("passwords", password);
                            editor.putBoolean("remember", rember.isChecked());
                            editor.apply();

                            // ✅ Part C：清空旧页面栈，永远不回旧 contentActivity
                            Intent intent = new Intent(MainActivity.this, UserNavActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "登录异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (cursor != null) cursor.close();
                    // db 不一定要 close（SQLiteOpenHelper 管理），但也可以安全关闭
                    // db.close();
                }

                if (!login_succ) {
                    Toast.makeText(MainActivity.this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
