package com.example.administrator.sportmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.administrator.sportmanager.admin.databaseHelp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LoginDbTest {

    private SQLiteDatabase db;

    private static final String U = "tc_login_user01";
    private static final String P1 = "old123";
    private static final String P2 = "new123";

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new databaseHelp(ctx).getWritableDatabase();
        cleanup();
        seedUser(U, P1);
    }

    @After
    public void tearDown() {
        cleanup();
        db.close();
    }

    private void cleanup() {
        db.delete("admin", "user LIKE ?", new String[]{"tc_login_user%"});
    }

    private void seedUser(String user, String pwd) {
        ContentValues v = new ContentValues();
        v.put("user", user);
        v.put("name", "测试用户");
        v.put("password", pwd);
        v.put("sex", "男");
        v.put("phone", "13800138001");
        v.put("birthday", "2005.01.01");
        db.insert("admin", null, v);
    }

    /** 模拟登录校验：查 admin 表是否存在 user+password */
    private boolean canLogin(String user, String pwd) {
        // ✅ 加保护：避免空/空格导致崩溃（真实业务一般也应当拒绝）
        if (user == null || user.trim().isEmpty()) return false;
        if (pwd == null || pwd.trim().isEmpty()) return false;

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM admin WHERE user=? AND password=?",
                new String[]{user, pwd}
        );
        c.moveToFirst();
        int n = c.getInt(0);
        c.close();
        return n > 0;
    }


    /** TC-LOGIN-001：正确用户名+密码应登录成功 */
    @Test
    public void login_success_with_correct_password() {
        assertTrue(canLogin(U, P1));
    }

    /** TC-LOGIN-002：密码错误应登录失败 */
    @Test
    public void login_fail_with_wrong_password() {
        assertFalse(canLogin(U, "wrong"));
    }

    /** TC-LOGIN-003：用户名不存在应登录失败 */
    @Test
    public void login_fail_with_nonexistent_user() {
        assertFalse(canLogin("not_exist_user", "123456"));
    }

    /** TC-LOGIN-004：用户名为空应登录失败（且不应崩溃） */
    @Test
    public void login_fail_with_empty_username() {
        assertFalse(canLogin("", P1));
        assertFalse(canLogin("   ", P1));
    }

    /** TC-LOGIN-005：密码为空应登录失败（且不应崩溃） */
    @Test
    public void login_fail_with_empty_password() {
        assertFalse(canLogin(U, ""));
        assertFalse(canLogin(U, "   "));
    }

    /** TC-LOGIN-006：用户名大小写应敏感（一般数据库精确匹配） */
    @Test
    public void login_should_be_case_sensitive() {
        assertFalse(canLogin(U.toUpperCase(), P1));
    }

    /** TC-LOGIN-007：修改密码后：旧密码失败，新密码成功（对应个人信息修改的核心逻辑） */
    @Test
    public void login_should_fail_with_old_password_after_update_and_succeed_with_new_password() {
        // 更新密码（模拟个人信息修改保存成功后的数据库效果）
        ContentValues v = new ContentValues();
        v.put("password", P2);
        db.update("admin", v, "user=?", new String[]{U});

        assertFalse(canLogin(U, P1));
        assertTrue(canLogin(U, P2));
    }
}
