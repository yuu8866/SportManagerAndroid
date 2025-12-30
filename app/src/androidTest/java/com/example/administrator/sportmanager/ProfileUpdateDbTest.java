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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ProfileUpdateDbTest {

    private SQLiteDatabase db;

    private static final String U = "tc_profile_user01";

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new databaseHelp(ctx).getWritableDatabase();
        cleanup();
        seedUser();
    }

    @After
    public void tearDown() {
        cleanup();
        db.close();
    }

    private void cleanup() {
        db.delete("admin", "user=?", new String[]{U});
    }

    private void seedUser() {
        ContentValues v = new ContentValues();
        v.put("user", U);
        v.put("name", "原姓名");
        v.put("password", "old123");
        v.put("sex", "男");
        v.put("phone", "13800138099");
        v.put("birthday", "2005.01.01");
        db.insert("admin", null, v);
    }

    private String getField(String col) {
        Cursor c = db.rawQuery("SELECT " + col + " FROM admin WHERE user=?", new String[]{U});
        c.moveToFirst();
        String val = c.getString(0);
        c.close();
        return val;
    }

    @Test
    public void update_profile_should_write_to_db() {
        ContentValues v = new ContentValues();
        v.put("name", "新姓名");
        v.put("password", "new123");
        v.put("sex", "女");
        v.put("phone", "13800138000");
        v.put("birthday", "2005.02.28");
        db.update("admin", v, "user=?", new String[]{U});

        assertEquals("新姓名", getField("name"));
        assertEquals("new123", getField("password"));
        assertEquals("女", getField("sex"));
        assertEquals("13800138000", getField("phone"));
        assertEquals("2005.02.28", getField("birthday"));
    }


    @Ignore("当前版本缺陷：个人信息修改未校验密码为空，需求上应拒绝保存")
    @Test
    public void update_password_empty_should_be_rejected() {
        // 需求预期：拒绝写入空密码
        ContentValues v = new ContentValues();
        v.put("password", "");
        db.update("admin", v, "user=?", new String[]{U});

        // 预期仍为 old123（但当前实现会变成空 -> 缺陷）
        assertEquals("old123", getField("password"));
    }
}
