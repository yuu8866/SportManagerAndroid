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
public class RegisterDbTest {

    private SQLiteDatabase db;

    private static final String U1 = "tc_reg_user01";
    private static final String U2 = "tc_reg_user02";

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new databaseHelp(ctx).getWritableDatabase();
        cleanup();
    }

    @After
    public void tearDown() {
        cleanup();
        db.close();
    }

    /** 清理测试数据：删除我们用例插入的用户 */
    private void cleanup() {
        db.delete("admin", "user LIKE ?", new String[]{"tc_reg_user%"});
        db.delete("admin", "user LIKE ?", new String[]{"tc_login_user%"}); // 保险起见
    }

    private long insertUser(String user, String name, String password, String sex, String phone, String birthday) {
        ContentValues v = new ContentValues();
        v.put("user", user);
        v.put("name", name);
        v.put("password", password);
        v.put("sex", sex);
        v.put("phone", phone);
        v.put("birthday", birthday);
        return db.insert("admin", null, v);
    }

    private int countAdminByUser(String user) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM admin WHERE user=?", new String[]{user});
        c.moveToFirst();
        int n = c.getInt(0);
        c.close();
        return n;
    }

    private Cursor queryAdminByUser(String user) {
        // 明确列名，避免 SELECT * 列顺序不稳定
        return db.rawQuery(
                "SELECT user,name,password,sex,phone,birthday FROM admin WHERE user=?",
                new String[]{user}
        );
    }



    /** TC-REG-001：注册成功应插入一条用户记录 */
    @Test
    public void register_success_should_insert_user_row() {
        long id = insertUser(U1, "张三", "123456", "男", "13800138000", "2005.01.01");
        assertTrue("insert 返回 -1 表示插入失败", id != -1);
        assertEquals(1, countAdminByUser(U1));
    }

    /** TC-REG-002：注册插入后，字段应完整可查且与输入一致 */
    @Test
    public void register_inserted_row_should_match_input_fields() {
        insertUser(U1, "张三", "123456", "男", "13800138000", "2005.01.01");

        Cursor c = queryAdminByUser(U1);
        assertTrue("应能查询到该用户记录", c.moveToFirst());

        assertEquals(U1, c.getString(0));                // user
        assertEquals("张三", c.getString(1));            // name
        assertEquals("123456", c.getString(2));          // password
        assertEquals("男", c.getString(3));              // sex
        assertEquals("13800138000", c.getString(4));     // phone
        assertEquals("2005.01.01", c.getString(5));      // birthday
        c.close();
    }

    /** TC-REG-003：多个不同用户都能注册成功（基本并存能力） */
    @Test
    public void register_should_allow_multiple_distinct_users() {
        insertUser(U1, "张三", "123456", "男", "13800138000", "2005.01.01");
        insertUser(U2, "李四", "123456", "女", "13800138001", "2005.01.02");

        assertEquals(1, countAdminByUser(U1));
        assertEquals(1, countAdminByUser(U2));
    }

    /** TC-REG-004：生日字符串存储格式应保持 yyyy.MM.dd */
    @Test
    public void register_should_keep_birthday_format_string() {
        insertUser(U1, "张三", "123456", "男", "13800138000", "2005.01.01");

        Cursor c = queryAdminByUser(U1);
        assertTrue(c.moveToFirst());
        String birthday = c.getString(5);
        c.close();

        assertTrue("生日格式应为 yyyy.MM.dd",
                birthday != null && birthday.matches("^\\d{4}\\.\\d{2}\\.\\d{2}$"));
    }

    // ========== ⚠️ 缺陷回归类用例（你现在系统存在缺陷的话，会失败；先 Ignore 不让构建红） ==========
    // 你后面修复了“输入校验/唯一性”后，把 @Ignore 去掉，这些就能当回归测试用。

    /** TC-REG-006：性别为空应拒绝 */
  //  @Ignore("当前系统缺陷：性别未做必填/枚举校验，修复后取消 Ignore")
    @Test
    public void register_should_reject_empty_gender() {
        long id = insertUser(U1, "张三", "123456", "", "13800138000", "2005.01.01");
        assertEquals("应拒绝插入（返回 -1）", -1, id);
        assertEquals(0, countAdminByUser(U1));
    }

    /** TC-REG-012：手机号重复应拒绝 */
    //@Ignore("当前系统缺陷：手机号唯一性未约束，修复后取消 Ignore")
    @Test
    public void register_should_reject_duplicate_phone() {
        insertUser(U1, "张三", "123456", "男", "13800138000", "2005.01.01");
        long id2 = insertUser(U2, "李四", "123456", "女", "13800138000", "2005.01.02");
        assertEquals(-1, id2);
    }

    /** TC-REG-013：生日为空应拒绝 */
   // @Ignore("当前系统缺陷：生日为空可能仍放行，修复后取消 Ignore")
    @Test
    public void register_should_reject_empty_birthday() {
        long id = insertUser(U1, "张三", "123456", "男", "13800138000", "");
        assertEquals(-1, id);
    }

    /** TC-REG-017：生日无效日期（2005.02.30）应拒绝 */
 //   @Ignore("当前系统缺陷：生日有效性未严格校验，修复后取消 Ignore")
    @Test
    public void register_should_reject_invalid_birthday_date() {
        long id = insertUser(U1, "张三", "123456", "男", "13800138000", "2005.02.30");
        assertEquals(-1, id);
    }
}
