package com.example.administrator.sportmanager.admin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class registerActivity extends Activity {

    private EditText etUser;      // 用户名(user)
    private EditText etPwd;       // 密码
    private EditText etName;      // 姓名(name)
    private EditText etSex;       // 性别
    private EditText etBirthday;  // 生日
    private EditText etPhone;     // 手机号

    private Button btnRegister;
    private Button btnBack;

    private databaseHelp dbHelper;

    // 规则：>=6 且包含字母+数字；特殊字符可有可无
    private static final Pattern PWD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");
    // 手机号：11位且1开头
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    // 生日格式：yyyy.MM.dd（严格）
    private static final SimpleDateFormat BIRTHDAY_FMT =
            new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 严格解析日期（比如 2022.13.40 这种直接判错）
        BIRTHDAY_FMT.setLenient(false);

        initView();
        initDb();
        initListener();
    }

    private void initView() {

        etUser = findViewById(R.id.r_name);         // “请输入用户名”
        etPwd = findViewById(R.id.r_password);      // “请输入密码”
        etName = findViewById(R.id.user_name);      // “请输入姓名”
        etSex = findViewById(R.id.r_sex);           // “请输入性别(男/女)”
        etBirthday = findViewById(R.id.r_birthday); // “请输入生日”
        etPhone = findViewById(R.id.r_phone);       // “请输入手机号”

        btnRegister = findViewById(R.id.r_register);
        btnBack = findViewById(R.id.r_back);
    }

    private void initDb() {

        dbHelper = new databaseHelp(this);
    }


    private void initListener() {
        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String username = safeText(etUser);
        String password = safeText(etPwd);
        String realName = safeText(etName);
        String sex = safeText(etSex);
        String birthday = safeText(etBirthday);
        String phone = safeText(etPhone);

        // 1) 用户名不能为空
        if (TextUtils.isEmpty(username)) {
            toast("用户名不能为空");
            return;
        }

        // 2) 姓名：只要求不为空（允许重复）
        if (TextUtils.isEmpty(realName)) {
            toast("姓名不能为空");
            return;
        }

        // 3) 密码：>=6 且必须包含字母+数字（特殊字符可有可无）
        if (!PWD_PATTERN.matcher(password).matches()) {
            toast("密码需至少6位，且必须同时包含字母和数字");
            return;
        }

        // 4) 性别：只能 男/女
        if (!("男".equals(sex) || "女".equals(sex))) {
            toast("性别只能输入 男 或 女");
            return;
        }

        // 5) 手机号：11位且1开头
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            toast("手机号必须为11位且1开头");
            return;
        }

        // 6) 生日：必须 yyyy.MM.dd 且早于今天
        Date birthDate = parseBirthdayOrNull(birthday);
        if (birthDate == null) {
            toast("生日格式必须为 yyyy.MM.dd（例如 2005.01.01）");
            return;
        }
        if (!isBeforeToday(birthDate)) {
            toast("生日必须早于今天");
            return;
        }

        // 7) 用户名唯一：查 admin.user（不是 name！）
        if (existsInAdmin("user", username)) {
            toast("该用户名已经注册，请重新输入");
            return;
        }

        // 手机号唯一：查 admin.phone
        if (existsInAdmin("phone", phone)) {
            toast("该手机号已经注册，请重新输入");
            return;
        }

        // 写入：user=用户名，name=姓名
        ContentValues values = new ContentValues();
        values.put("user", username);
        values.put("name", realName);
        values.put("password", password);
        values.put("sex", sex);
        values.put("phone", phone);
        values.put("birthday", birthday);

        try {

            dbHelper.insert(values);
            toast("用户注册成功");
            startActivity(new Intent(registerActivity.this, MainActivity.class));
            finish();
        } catch (Exception e) {
            toast("注册失败：" + e.getMessage());
        }
    }

    private String safeText(EditText et) {
        return et == null ? "" : et.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** 直接用 helper.getReadableDatabase() 查 admin 表 */
    private boolean existsInAdmin(String column, String value) {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.query(
                    "admin",
                    new String[]{"_id"},
                    column + "=?",
                    new String[]{value},
                    null, null,
                    null,
                    "1"
            );
            return c != null && c.moveToFirst();
        } catch (Exception e) {
            // 查表失败时，为了不误伤注册流程，默认当作“不存在”
            return false;
        } finally {
            if (c != null) c.close();
        }
    }

    private Date parseBirthdayOrNull(String birthday) {
        if (TextUtils.isEmpty(birthday)) return null;
        try {
            return BIRTHDAY_FMT.parse(birthday);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean isBeforeToday(Date date) {
        // 把“今天”归零到 00:00:00，保证“生日=今天”不通过
        Date now = new Date();
        try {
            String todayStr = BIRTHDAY_FMT.format(now);
            Date todayStart = BIRTHDAY_FMT.parse(todayStr);
            return date.before(todayStart);
        } catch (Exception e) {
            return date.before(now);
        }
    }
}
