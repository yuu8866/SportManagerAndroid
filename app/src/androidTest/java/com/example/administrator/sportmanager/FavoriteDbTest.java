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
public class FavoriteDbTest {

    private SQLiteDatabase db;
    private databaseHelp help;

    private static final String U1 = "tc_fav_user01";
    private static final String U2 = "tc_fav_user02";

    private static final int SPORT_ID = 2;
    private static final String SPORT_NAME = "网球拍";
    private static final String SPORT_AUTHOR = "小曼 ";
    private static final String NOW = "2025年12月29日/12:10:00";

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
        db.delete("collect", "Borname=? OR Borname=?", new String[]{U1, U2});
    }

    private int countCollect(String user, int sportId) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM collect WHERE Borname=? AND sportid=?",
                new String[]{user, String.valueOf(sportId)});
        c.moveToFirst();
        int n = c.getInt(0);
        c.close();
        return n;
    }

    private void collectIfNotExists(String user) {
        Cursor cur = help.checkcollectinfo(SPORT_NAME, user);
        try {
            if (cur.getCount() > 0) return; // 已收藏：不插入（对应“您已经收藏了这个器材”）
        } finally {
            cur.close();
        }

        ContentValues v = new ContentValues();
        v.put("sportid", SPORT_ID);
        v.put("sportname", SPORT_NAME);
        v.put("sportauthor", SPORT_AUTHOR);
        v.put("Borname", user);
        v.put("nowtime", NOW);
        v.put("type", "球类");
        v.put("rank", "4.2");
        v.put("price", "22");
        v.put("img", (byte[]) null); // 允许为空
        help.insertocollect(v);
    }

    @Test
    public void favorite_success_should_insert_row() {
        collectIfNotExists(U1);
        assertEquals(1, countCollect(U1, SPORT_ID));
    }

    @Test
    public void favorite_duplicate_same_user_should_not_insert_twice() {
        collectIfNotExists(U1);
        collectIfNotExists(U1);
        assertEquals(1, countCollect(U1, SPORT_ID));
    }

    @Test
    public void favorite_should_be_isolated_by_user_no_cross_account() {
        collectIfNotExists(U1);
        collectIfNotExists(U2);
        assertEquals(1, countCollect(U1, SPORT_ID));
        assertEquals(1, countCollect(U2, SPORT_ID));
    }
}
