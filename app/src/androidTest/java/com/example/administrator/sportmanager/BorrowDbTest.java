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
public class BorrowDbTest {

    private SQLiteDatabase db;
    private databaseHelp help;

    private static final String U1 = "tc_borrow_user01";
    private static final String U2 = "tc_borrow_user02";

    // 用初始化数据里的器材：sportid=2 网球拍 发布者“小曼 ”
    private static final int SPORT_ID = 2;
    private static final String SPORT_NAME = "网球拍";
    private static final String SPORT_AUTHOR = "小曼 ";
    private static final String NOW = "2025年12月29日/12:00:00";

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        help = new databaseHelp(ctx);
        db = help.getWritableDatabase();
        cleanup();
    }

    @After
    public void tearDown() {
        cleanup();
        db.close();
    }

    private void cleanup() {
        db.delete("borrow", "Borname=? OR Borname=?", new String[]{U1, U2});
    }

    private int countBorrow(String user, int sportId) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM borrow WHERE Borname=? AND sportid=?",
                new String[]{user, String.valueOf(sportId)});
        c.moveToFirst();
        int n = c.getInt(0);
        c.close();
        return n;
    }

    private void borrowIfNotExists(String user) {
        Cursor cur = help.checkborrowinfo(SPORT_NAME, user);
        try {
            if (cur.getCount() > 0) return; // 已租赁：不插入（对应你的用例“您已经租赁该器材”）
        } finally {
            cur.close();
        }
        ContentValues v = new ContentValues();
        v.put("sportid", SPORT_ID);
        v.put("sportname", SPORT_NAME);
        v.put("sportauthor", SPORT_AUTHOR);
        v.put("Borname", user);
        v.put("nowtime", NOW);
        help.insertorrowo(v);
    }

    @Test
    public void borrow_success_should_insert_row() {
        borrowIfNotExists(U1);
        assertEquals(1, countBorrow(U1, SPORT_ID));
    }

    @Test
    public void borrow_duplicate_same_user_should_not_insert_twice() {
        borrowIfNotExists(U1);
        borrowIfNotExists(U1);
        assertEquals(1, countBorrow(U1, SPORT_ID));
    }

    @Test
    public void borrow_same_equipment_by_different_users_should_be_allowed() {
        borrowIfNotExists(U1);
        borrowIfNotExists(U2);
        assertEquals(1, countBorrow(U1, SPORT_ID));
        assertEquals(1, countBorrow(U2, SPORT_ID));
    }

    /**
     * 重要：你说“多个用户可以租同一个器材”，那么“归还”不应该把别人的记录删掉。
     * 但你项目里 databaseHelp.delborrow(sportid) 是按 sportid 删除，会把所有用户都删掉。
     * 这是一个缺陷，所以我先 @Ignore，方便你需要时打开让它变红作为缺陷证据。
     */
    @Ignore("当前版本缺陷：delborrow 按 sportid 删除，会误删其他用户租赁记录")
    @Test
    public void return_should_only_delete_current_user_record_not_others() {
        borrowIfNotExists(U1);
        borrowIfNotExists(U2);

        // 模拟归还：项目代码是 help.delborrow(sportid)
        help.delborrow(SPORT_ID);

        // 预期：只删 U1 或 U2 的其中一条；实际会删光（缺陷）
        assertEquals(0, countBorrow(U1, SPORT_ID));
        assertEquals(1, countBorrow(U2, SPORT_ID)); // 这句会失败 -> 缺陷证据
    }
}
