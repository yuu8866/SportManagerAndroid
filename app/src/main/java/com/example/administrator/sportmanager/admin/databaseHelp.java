package com.example.administrator.sportmanager.admin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class databaseHelp extends SQLiteOpenHelper {
    private static final String DB_NAME = "CMP.db";
    private static final int DB_VERSION = 9;

    public databaseHelp(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String Table_Name1 = "admin";//用户表
    private static final String Table_Name2 = "sports";//运动器械表
    private static final String Table_Name3 = "borrow";//租赁表
    private static final String Table_Name5 = "collect";//收藏表

    public static final String id = "_id";
    public static final String Table_Sport = "sports";
    public static final String Sport_id = "sportid";
    public static final String Sport_Name = "name";
    public static final String Sport_Type = "type";
    public static final String Sport_user = "user";
    public static final String Sport_owner = "owner";
    public static final String Sport_Price = "price";
    public static final String Sport_Rank = "rank";
    public static final String Sport_Comment = "comment";
    public static final String Sport_Img = "img";

    // v4 新增字段：wallet_balance(余额) / member_expire(会员到期日：yyyy-MM-dd)
    private static final String Creat_table =
            "create table admin(_id integer primary key autoincrement," +
                    "user text," +
                    "name text," +
                    "password text," +
                    "sex text," +
                    "phone text," +
                    "birthday text," +
                    "wallet_balance real DEFAULT 0," +
                    "member_expire text)";

    // v5：充值记录表
    private static final String CREATE_RECHARGE_RECORD =
            "create table wallet_recharge_record(" +
                    "_id integer primary key autoincrement," +
                    "user text," +
                    "amount real," +
                    "channel text," +
                    "create_time text)";

    // v5：会员购买记录表
    private static final String CREATE_MEMBER_PURCHASE_RECORD =
            "create table member_purchase_record(" +
                    "_id integer primary key autoincrement," +
                    "user text," +
                    "card_type text," +
                    "amount real," +
                    "channel text," +
                    "start_date text," +
                    "expire_date text," +
                    "create_time text)";

    // v6：钱包流水记录表（充值/租借扣款/会员开通/退款等）
    private static final String CREATE_WALLET_FLOW_RECORD =
            "create table wallet_flow_record(" +
                    "_id integer primary key autoincrement," +
                    "user text," +
                    "biz_type text," +
                    "amount real," +
                    "channel text," +
                    "balance_after real," +
                    "create_time text)";

    // ======================= v7：社区帖子 ====================
    private static final String CREATE_COMMUNITY_POST =
            "create table community_post(" +
                    "_id integer primary key autoincrement," +
                    "title text," +
                    "content text," +
                    "username text," +
                    "create_time text," +
                    "img blob," +
                    "warned integer DEFAULT 0," +
                    "warn_reason text," +
                    "warn_time text" +
                    ")";


    // ======================= v7：帖子收藏（我的收藏里展示） =======================
    private static final String CREATE_COMMUNITY_POST_COLLECT =
            "create table IF NOT EXISTS community_post_collect(" +
                    "_id integer primary key autoincrement," +
                    "post_id integer," +
                    "user text," +
                    "collect_time text" +
                    ")";



    public static final String Creat_table1 = "create table sports1 ("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    public static final String Creat_table2 = "create table " + Table_Sport + "("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    //
    private static final String Creat_table3 =
            "create table borrow(_Bid integer primary key autoincrement," +
                    "Borname text," +
                    "sportid integer," +
                    "sportname text," +
                    "sportauthor text," +
                    "nowtime text," +
                    "days integer default 1," +
                    "total_price integer default 0," +
                    "pay_status integer default 0," +
                    "pay_time text)";

    // collect 表没有 days 字段（收藏不存天数）
    private static final String Creat_table5 =
            "create table collect(_id integer primary key autoincrement,Borname text,sportid integer,sportname text,sportauthor text,nowtime text,type text,rank text,price text,img blob)";

    private static final String Creat_table7 = "create table admin1(_id integer primary key autoincrement,user text , name text, password text,sex text, phone text, birthday text)";

    SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(Creat_table);
        db.execSQL(Creat_table2);
        db.execSQL(Creat_table3);
        db.execSQL(Creat_table5);
        db.execSQL(CREATE_RECHARGE_RECORD);
        db.execSQL(CREATE_MEMBER_PURCHASE_RECORD);
        db.execSQL(CREATE_WALLET_FLOW_RECORD);
        db.execSQL(CREATE_COMMUNITY_POST);
        db.execSQL(CREATE_COMMUNITY_POST_COLLECT);



        // 初始化器材
        db.execSQL("insert into sports (sportid,name,type,user,owner,price,rank,comment) values " +
                "(0,'羽毛球拍','球类','孟岩','器材云',200,8.9,'借用者需保持拍面整洁，避免碰撞硬物，按时归还')," +
                "(1,'乒乓球拍','球类','小花','器材云',150,5.0,'借用者需小心使用，避免刮花拍面，不可私自更改拍面胶皮。')," +
                "(2,'网球拍','球类','小曼 ','器材云',220,4.2,'借用者需注意避免碰撞拍面，使用后将球拍放回原处。')," +
                "(3,'滚轮','轮式','烽月','器材云',50,4.3,'借用者需注意安全，避免在危险路段滑行，及时归还设备。')," +
                "(4,'橄榄球','轮式','家悦','器材云',55,4.4,'借用者需注意不要将球弄脏或损坏，按时归还。')," +
                "(5,'拉力绳','塑形','侯若飞','器材云',30,5.0,'借用者需正确使用，避免拉力过大导致断裂，保持整洁并妥善存放。')," +
                "(6,'跑步机','健身','越才','第三方平台',1500,10.0,'使用完毕后，请及时清洁跑步机表面，保持卫生。')," +
                "(7,'动感单车','健身','小斯','器材云',2000,11.0,'使用完毕后，请将动感单车放置在干燥通风的地方，避免生锈。')," +
                "(8,'无绳跳绳','绳类','威廉','第三方平台',45,4.8,'使用完毕后，请将跳绳卷好收纳，避免绳索缠绕。')," +
                "(9,'篮球','球类','李恩','第三方平台',300,6.9,'请在篮球充气适当时使用，避免充气不足或过足影响球的性能。')," +
                "(10,'足球','球类','莫员','器材云',210,5.0,'使用完毕后，请将足球放回指定位置，避免丢失。')," +
                "(11,'瑜伽垫','塑形','华华','第三方平台',210,4.5,'避免在瑜伽垫上使用尖锐物品，以免损坏垫面。')," +
                "(12,'排球','球类','小吾','器材云',60,4.0,'请在排球充气适当时使用，避免充气不足或过足影响球的性能。')");

        // 初始化用户
        db.execSQL("insert into admin (user,name,password,sex,phone,birthday) values " +
                "('admin','admin','admin','男','12345678901','2005.11.20')," +
                "('root','root','root123456','男','12345678901','2005.11.20')," +
                "('lx','lx','lx123456','男','12345678901','2005.11.20')," +
                "('1','1','123456','女','12345678901','2005.11.20');");

        // 初始化社区示例帖子（v7）
        db.execSQL("insert into community_post (title,content,username,create_time) values " +
                "('健身新手一周怎么练？','刚开始健身总是坚持不下来，我的建议是：\\n1）先每周3练（胸背腿）\\n2）每天30分钟快走\\n3）别一开始就练太猛\\n坚持一个月就会明显变好～','lx','2026-01-12 18:20')," +
                "('减脂期三餐食谱分享','我最近减脂吃得比较干净：\\n早餐：鸡蛋+燕麦+牛奶\\n午餐：鸡胸+糙米+青菜\\n晚餐：番茄鸡蛋汤+水果\\n低油低糖，饱腹感还不错！','user11','2026-01-14 12:05')," +
                "('器材租什么最划算？','如果预算有限，我最推荐先租：\\n1）哑铃（最通用）\\n2）瑜伽垫（练核心必备）\\n3）拉力绳（性价比很高）\\n先练出习惯再考虑升级装备！','1','2026-01-15 09:30');");

    }

    // 插入图片
    public void updateImg(ContentValues values, int id) {
        db = getWritableDatabase();
        db.update("sports", values, "_id=?", new String[]{String.valueOf(id)});
    }

    // 推荐
    public Cursor recommend() {
        db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM sports order by rank DESC", null);
    }

    // sportsid重新排序
    public void sortsportsid() {
        db = getReadableDatabase();
        db.execSQL(Creat_table1);
        db.execSQL("insert into sports1 (sportid,name,type,user,owner,price,rank,comment,img) select sportid,name,type,user,owner,price,rank,comment,img from sports");
        db.execSQL("drop table sports");
        db.execSQL("alter table sports1 rename to sports");
        db.close();
    }

    // userid重新排序
    public void sortuserid() {
        db = getReadableDatabase();
        db.execSQL(Creat_table7);
        db.execSQL("insert into admin1 (user,name,password,sex,phone,birthday) select user,name,password,sex,phone,birthday from admin");
        db.execSQL("drop table admin");
        db.execSQL("alter table admin1 rename to admin");
        db.close();
    }

    // 查询是否已租赁
    public Cursor checkborrowinfo(String sportname, String name) {
        db = getReadableDatabase();
        return db.rawQuery("SELECT sportname,Borname FROM borrow WHERE sportname = ? AND Borname = ?;",
                new String[]{sportname, name});
    }

    // 查询是否已收藏
    public Cursor checkcollectinfo(String sportname, String name) {
        db = getReadableDatabase();
        return db.rawQuery("SELECT sportname,Borname FROM collect WHERE sportname = ? AND Borname = ?;",
                new String[]{sportname, name});
    }

    // 手机号查重
    public Cursor queryPhone(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from admin where phone=?", new String[]{phone});
    }

    // 往admin表中插入信息
    public long insert(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name1, null, values);
        db.close();
        return 0;
    }

    // 查询所有用户
    public Cursor query() {
        db = getReadableDatabase();
        return db.query(Table_Name1, null, null, null, null, null, null);
    }

    // 删除用户
    public void del(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name1, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 通过id查询用户
    public Cursor queryid(int id) {
        db = getReadableDatabase();
        return db.query(Table_Name1, null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // 通过用户名查询用户
    public Cursor queryname(String name) {
        db = getReadableDatabase();
        return db.query(Table_Name1, null, "user=?", new String[]{name}, null, null, null);
    }

    // 往sports表中插入数据
    public void insertsports(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name2, null, values);
        db.close();
    }

    // 兼容 admin_add_sport.java 里旧的调用：helper.insersporttdata(...)
    public void insersporttdata(String sportid, String name, String type, String user,
                                String owner, String price, String rank, String comment, byte[] img)
    {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sportid", sportid);
        values.put("name", name);
        values.put("type", type);
        values.put("user", user);
        values.put("owner", owner);
        values.put("price", price);
        values.put("rank", rank);
        values.put("comment", comment);
        db.insert(Table_Name2, null, values);
        db.close();
    }

    // ✅ 查询当前用户已支付的借用单（主页用）
    public Cursor queryActiveBorrow(String username) {
        db = getReadableDatabase();
        return db.rawQuery(
                "SELECT _Bid as _id, sportname, pay_time, days " +
                        "FROM borrow WHERE Borname=? AND pay_status=1 " +
                        "ORDER BY _Bid DESC LIMIT 5",
                new String[]{username}
        );
    }

    // ✅ 推荐器材：rank高 -> 推荐（主页用）
    public Cursor queryRecommendSports() {
        db = getReadableDatabase();
        return db.rawQuery(
                "SELECT _id, sportid, name, type, user, owner, price, rank, comment, img " +
                        "FROM sports ORDER BY rank DESC LIMIT 6",
                null
        );
    }



    // 查询所有器材
    public Cursor querysports() {
        db = getReadableDatabase();
        return db.query(Table_Name2, null, null, null, null, null, null);
    }

    // 通过 _id 查询器材
    public Cursor querysportsid(int id) {
        db = getReadableDatabase();
        return db.query(Table_Name2, null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // 通过 sportid 查询器材
    public Cursor querysportssportid(int id) {
        db = getReadableDatabase();
        return db.query(Table_Name2, null, "sportid=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // 通过 name 模糊查器材
    public Cursor querysportsname(String name) {
        db = getReadableDatabase();
        return db.query(Table_Name2, null, "name like ?", new String[]{"%" + name + "%"}, null, null, null, null);
    }

    // 多字段模糊搜索：name/type/user/owner/price/rank(租金)
    public Cursor searchSports(String keyword) {
        db = getReadableDatabase();

        String like = "%" + keyword + "%";
        String sql = "SELECT * FROM " + Table_Name2
                + " WHERE name LIKE ?"
                + " OR type LIKE ?"
                + " OR user LIKE ?"
                + " OR owner LIKE ?"
                + " OR CAST(price AS TEXT) LIKE ?"
                + " OR CAST(rank AS TEXT) LIKE ?";

        return db.rawQuery(sql, new String[]{like, like, like, like, like, like});
    }


    // 删除器材
    public void delsports(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name2, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 往borrow表中添加数据
    public void insertorrowo(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name3, null, values);
        db.close();
    }

    //  插入 borrow 并返回订单ID（_Bid）
    public long insertBorrowReturnId(ContentValues values) {
        db = getWritableDatabase();
        long id = db.insert("borrow", null, values);
        db.close();
        return id;
    }

    //  按订单主键 _Bid 查询订单
    public Cursor queryBorrowById(int borrowId) {
        db = getReadableDatabase();
        return db.query("borrow", null, "_Bid=?", new String[]{String.valueOf(borrowId)}, null, null, null);
    }

    //  将订单标记为已支付
    public void setBorrowPaid(int borrowId, String payTime) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pay_status", 1);
        cv.put("pay_time", payTime);
        db.update("borrow", cv, "_Bid=?", new String[]{String.valueOf(borrowId)});
        db.close();
    }

    // ======================= 主页：借用概览 =======================

    /** 当前用户：查询“已支付”的借用订单（主页用） */
    public Cursor queryActiveBorrowForHome(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _Bid AS _id, sportname, pay_time, days " +
                "FROM borrow WHERE Borname=? AND pay_status=1 " +
                "ORDER BY _Bid DESC LIMIT 5";
        return db.rawQuery(sql, new String[]{username});
    }

    /** 当前用户：查询“当前借用数量”（已支付） */
    public int queryActiveBorrowCount(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM borrow WHERE Borname=? AND pay_status=1",
                    new String[]{username});
            if (c.moveToFirst()) return c.getInt(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

// ======================= 主页：偏好推荐（按历史借用类型） =======================

    /** 查询用户历史最常借的器材类型（通过 borrow 表 + sports 表联表统计） */
    public String queryFavoriteSportType(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String sql =
                    "SELECT s.type, COUNT(*) AS cnt " +
                            "FROM borrow b " +
                            "JOIN sports s ON b.sportid = s.sportid " +
                            "WHERE b.Borname=? AND b.pay_status=1 " +
                            "GROUP BY s.type " +
                            "ORDER BY cnt DESC LIMIT 1";
            c = db.rawQuery(sql, new String[]{username});
            if (c.moveToFirst()) {
                return c.getString(0);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** 推荐器材：按类型优先推荐（否则热门 rank 推荐） */
    public Cursor queryRecommendSportsByType(String type) {
        SQLiteDatabase db = getReadableDatabase();
        if (type == null || type.trim().isEmpty()) {
            return db.rawQuery(
                    "SELECT _id, sportid, name, type, user, owner, price, rank, comment, img " +
                            "FROM sports ORDER BY rank DESC LIMIT 6",
                    null
            );
        }
        return db.rawQuery(
                "SELECT _id, sportid, name, type, user, owner, price, rank, comment, img " +
                        "FROM sports WHERE type=? ORDER BY rank DESC LIMIT 6",
                new String[]{type}
        );
    }

    public void delBorrowById(int borrowId) {
        db = getWritableDatabase();
        db.delete("borrow", "_Bid=?", new String[]{String.valueOf(borrowId)});
        db.close();
    }

    // 往collect表中添加数据
    public void insertocollect(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name5, null, values);
        db.close();
    }

    // 删除收藏（按 collect 表的 _id）
    public void delcollect(int id) {
        db = getWritableDatabase();
        db.delete("collect", "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 查询borrow表（全部）
    @SuppressLint("Range")
    public List<Map<String, Object>> queryborrow() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from borrow order by Borname asc", null);
        try {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("_Bid", cursor.getString(cursor.getColumnIndex("_Bid")));
                map.put("Borname", cursor.getString(cursor.getColumnIndex("Borname")));
                map.put("sportid", cursor.getString(cursor.getColumnIndex("sportid")));
                map.put("sportname", cursor.getString(cursor.getColumnIndex("sportname")));
                map.put("sportauthor", cursor.getString(cursor.getColumnIndex("sportauthor")));
                map.put("nowtime", cursor.getString(cursor.getColumnIndex("nowtime")));

                int daysIndex = cursor.getColumnIndex("days");
                if (daysIndex != -1) {
                    map.put("days", cursor.getInt(daysIndex) + "天");
                } else {
                    map.put("days", "1天");
                }

                int totalIndex = cursor.getColumnIndex("total_price");
                if (totalIndex != -1) {
                    map.put("total_price", cursor.getInt(totalIndex) + "元");
                } else {
                    map.put("total_price", "0元");
                }

                int statusIndex = cursor.getColumnIndex("pay_status");
                int status = (statusIndex != -1) ? cursor.getInt(statusIndex) : 0;
                map.put("pay_status", status == 1 ? "已支付" : "未支付");

                int payTimeIndex = cursor.getColumnIndex("pay_time");
                if (payTimeIndex != -1) {
                    String pt = cursor.getString(payTimeIndex);
                    map.put("pay_time", pt == null ? "" : pt);
                } else {
                    map.put("pay_time", "");
                }

                data.add(map);
            }
        } finally {
            cursor.close();
        }
        return data;
    }

    // 取消支付：按订单主键 _Bid 删除一条 borrow 记录
    public void delBorrowByBid(int borrowId) {
        db = getWritableDatabase();
        db.delete(Table_Name3, "_Bid=?", new String[]{String.valueOf(borrowId)});
        db.close();
    }

    // 确认支付：把 pay_status 改为 1，并写入 pay_time
    public int markBorrowPaid(int borrowId, String payTime) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pay_status", 1);
        cv.put("pay_time", payTime);
        int rows = db.update(Table_Name3, cv, "_Bid=?", new String[]{String.valueOf(borrowId)});
        db.close();
        return rows;
    }


    // 在 collect 表中按用户查询
    public Cursor queryuser(String str) {
        db = getReadableDatabase();
        return db.query(Table_Name5, null, "Borname=?", new String[]{str}, null, null, null);
    }

    // 查询borrow表（按用户）——补上 days！（你租赁信息页面就是靠这个）
    @SuppressLint("Range")
    public List<Map<String, Object>> queryborrow(String str) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name3, null, "Borname=?", new String[]{str}, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("_Bid", cursor.getInt(cursor.getColumnIndex("_Bid")));
                map.put("Borname", cursor.getString(cursor.getColumnIndex("Borname")));
                map.put("sportid", cursor.getInt(cursor.getColumnIndex("sportid")));
                map.put("sportname", cursor.getString(cursor.getColumnIndex("sportname")));
                map.put("sportauthor", cursor.getString(cursor.getColumnIndex("sportauthor")));
                map.put("bortime", cursor.getString(cursor.getColumnIndex("nowtime")));

                int daysIndex = cursor.getColumnIndex("days");
                if (daysIndex != -1) {
                    map.put("days", cursor.getInt(daysIndex) + "天");
                } else {
                    map.put("days", "1天");
                }

                data.add(map);
            }
        } finally {
            cursor.close();
        }
        return data;
    }

    // 删除borrow表的信息（按 sportid）
    public void delborrow(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name3, "sportid=?", new String[]{String.valueOf(id)});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // v2：增加 days
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE borrow ADD COLUMN days integer DEFAULT 1");
            } catch (Exception e) {
                Log.e("DB_UPGRADE", "days column may already exist: " + e.getMessage());
            }
        }
        // v3：增加支付相关字段
        if (oldVersion < 3) {
            try { db.execSQL("ALTER TABLE borrow ADD COLUMN total_price integer DEFAULT 0"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE borrow ADD COLUMN pay_status integer DEFAULT 0"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE borrow ADD COLUMN pay_time text"); } catch (Exception ignored) {}
        }
        // v4：增加钱包余额与会员到期日
        if (oldVersion < 4) {
            try { db.execSQL("ALTER TABLE admin ADD COLUMN wallet_balance real DEFAULT 0"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE admin ADD COLUMN member_expire text"); } catch (Exception ignored) {}
        }

        // v5：充值记录/会员购买记录表
        if (oldVersion < 5) {
            try { db.execSQL(CREATE_RECHARGE_RECORD); } catch (Exception ignored) {}
            try { db.execSQL(CREATE_MEMBER_PURCHASE_RECORD); } catch (Exception ignored) {}
        }

        // v6：钱包流水记录表
        if (oldVersion < 6) {
            try { db.execSQL(CREATE_WALLET_FLOW_RECORD); } catch (Exception ignored) {}
        }

        // v7：社区帖子 + 帖子收藏
        if (oldVersion < 7) {
            try { db.execSQL(CREATE_COMMUNITY_POST); } catch (Exception ignored) {}
            try { db.execSQL(CREATE_COMMUNITY_POST_COLLECT); } catch (Exception ignored) {}
        }

        // v8：社区帖子图片 + 点赞评论
        if (oldVersion < 8) {
            try { db.execSQL("ALTER TABLE community_post ADD COLUMN img blob"); } catch (Exception ignored) {}
            try { db.execSQL("create table IF NOT EXISTS community_post_like(_id integer primary key autoincrement, post_id integer, user text, create_time text)"); } catch (Exception ignored) {}
            try { db.execSQL("create table IF NOT EXISTS community_post_comment(_id integer primary key autoincrement, post_id integer, user text, content text, create_time text)"); } catch (Exception ignored) {}
        }

        // v9：帖子警告（管理员端）
        if (oldVersion < 9) {
            try { db.execSQL("ALTER TABLE community_post ADD COLUMN warned integer DEFAULT 0"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE community_post ADD COLUMN warn_reason text"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE community_post ADD COLUMN warn_time text"); } catch (Exception ignored) {}
        }




    }

    // 打开外键
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    // 用户名重复
    public boolean queryAdminUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"user"};
        String selection = "user = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("admin", columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 手机号重复
    public boolean queryAdminPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"phone"};
        String selection = "phone = ?";
        String[] selectionArgs = {phone};

        Cursor cursor = db.query("admin", columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ======================= v4：钱包/会员相关 =======================

    /** 获取用户余额（默认 0） */
    public double getWalletBalance(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT wallet_balance FROM admin WHERE user=? LIMIT 1", new String[]{username});
            if (c != null && c.moveToFirst()) {
                return c.getDouble(0);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    /** 更新用户余额 */
    public void updateWalletBalance(String username, double newBalance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("wallet_balance", newBalance);
        db.update("admin", values, "user=?", new String[]{username});
    }

    /** 获取会员到期日 yyyy-MM-dd，可能为 null/"" */
    public String getMemberExpire(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT member_expire FROM admin WHERE user=? LIMIT 1", new String[]{username});
            if (c != null && c.moveToFirst()) {
                return c.getString(0);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** 更新会员到期日 yyyy-MM-dd */
    public void updateMemberExpire(String username, String expireDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("member_expire", expireDate);
        db.update("admin", values, "user=?", new String[]{username});
    }

    /**
     * 管理员端：查询用户列表（附加 member_status 字段：会员用户/普通用户）
     * - member_expire 必须是 yyyy-MM-dd 才能与 SQLite date('now') 比较
     */
    public Cursor queryUsersWithMemberInfo() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _id,user,password,name,sex,birthday,phone,member_expire, " +
                "CASE WHEN member_expire IS NOT NULL AND member_expire != '' AND member_expire >= date('now') " +
                "THEN '会员用户' ELSE '普通用户' END AS member_status " +
                "FROM admin";
        return db.rawQuery(sql, null);
    }

    // ======================= v5：记录&会员权限 =======================

    /** 是否会员有效 */
    public boolean isMemberActive(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT member_expire FROM admin WHERE user=? LIMIT 1",
                    new String[]{username}
            );
            if (c != null && c.moveToFirst()) {
                String expire = c.getString(0);
                if (expire == null || expire.trim().isEmpty()) return false;

                // member_expire 必须 yyyy-MM-dd
                Cursor t = db.rawQuery("SELECT CASE WHEN ? >= date('now') THEN 1 ELSE 0 END",
                        new String[]{expire});
                try {
                    return t.moveToFirst() && t.getInt(0) == 1;
                } finally {
                    t.close();
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return false;
    }

    /** 会员折扣：9折 */
    public double getMemberDiscountRate(String username) {
        return isMemberActive(username) ? 0.9 : 1.0;
    }

    /** 器材是否已被借出（pay_status=1 视为已借出） */
    public boolean isSportBorrowedByOthers(int sportId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM borrow WHERE sportid=? AND pay_status=1",
                    new String[]{String.valueOf(sportId)});
            return c.moveToFirst() && c.getInt(0) > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    /** 插入充值记录 */
    public void insertRechargeRecord(String username, double amount, String channel, String createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user", username);
        cv.put("amount", amount);
        cv.put("channel", channel);
        cv.put("create_time", createTime);
        db.insert("wallet_recharge_record", null, cv);
    }

    /** 获取充值记录（最新在前） */
    public List<String> getRechargeRecordLines(String username, int limit) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT amount,channel,create_time FROM wallet_recharge_record " +
                            "WHERE user=? ORDER BY _id DESC LIMIT " + limit,
                    new String[]{username}
            );
            while (c.moveToNext()) {
                double amt = c.getDouble(0);
                String ch = c.getString(1);
                String t = c.getString(2);
                list.add("充值 ¥" + String.format("%.2f", amt) + "  |  " + ch + "\n" + t);
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /** 插入会员购买记录 */
    public void insertMemberPurchaseRecord(String username, String cardType, double amount,
                                           String channel, String startDate, String expireDate, String createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user", username);
        cv.put("card_type", cardType);
        cv.put("amount", amount);
        cv.put("channel", channel);
        cv.put("start_date", startDate);
        cv.put("expire_date", expireDate);
        cv.put("create_time", createTime);
        db.insert("member_purchase_record", null, cv);
    }

    /** 获取会员购买记录（最新在前） */
    public List<String> getMemberPurchaseRecordLines(String username, int limit) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT card_type,amount,channel,expire_date,create_time FROM member_purchase_record " +
                            "WHERE user=? ORDER BY _id DESC LIMIT " + limit,
                    new String[]{username}
            );
            while (c.moveToNext()) {
                String type = c.getString(0);
                double amt = c.getDouble(1);
                String ch = c.getString(2);
                String expire = c.getString(3);
                String t = c.getString(4);
                list.add(type + "  ¥" + String.format("%.2f", amt) + "  |  " + ch +
                        "\n到期：" + expire + "\n" + t);
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }
    // ======================= v6：钱包流水 =======================

    /** 插入钱包流水 */
    public void insertWalletFlow(String username, String bizType, double amount,
                                 String channel, double balanceAfter, String createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user", username);
        cv.put("biz_type", bizType);
        cv.put("amount", amount);
        cv.put("channel", channel);
        cv.put("balance_after", balanceAfter);
        cv.put("create_time", createTime);
        db.insert("wallet_flow_record", null, cv);
    }

    /** 获取钱包流水（最新在前） */
    public List<String> getWalletFlowLines(String username, int limit) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT biz_type,amount,channel,balance_after,create_time " +
                            "FROM wallet_flow_record WHERE user=? ORDER BY _id DESC LIMIT " + limit,
                    new String[]{username}
            );
            while (c.moveToNext()) {
                String type = c.getString(0);
                double amt = c.getDouble(1);
                String ch = c.getString(2);
                double after = c.getDouble(3);
                String t = c.getString(4);

                String sign = (amt >= 0) ? "+" : "";
                list.add(type + "  " + sign + String.format("%.2f", amt) +
                        "  |  " + ch +
                        "\n余额：" + String.format("%.2f", after) +
                        "\n" + t);
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // ======================= 个人主页：统计数据 =======================

    /** 历史借用次数（已支付） */
    public int queryBorrowHistoryCount(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM borrow WHERE Borname=? AND pay_status=1",
                    new String[]{username});
            if (c.moveToFirst()) return c.getInt(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    /** 收藏数量 */
    public int queryCollectCount(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM collect WHERE Borname=?",
                    new String[]{username});
            if (c.moveToFirst()) return c.getInt(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    /** 总消费金额（已支付订单 total_price 求和） */
    public double queryTotalSpend(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT IFNULL(SUM(total_price),0) FROM borrow WHERE Borname=? AND pay_status=1",
                    new String[]{username});
            if (c.moveToFirst()) return c.getDouble(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

// ======================= v7：社区帖子（查询 + 收藏） =======================

    /** 获取社区帖子列表（按最新倒序） */
    public java.util.List<com.example.administrator.sportmanager.admin.bean.Post> queryAllCommunityPosts() {
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        java.util.List<com.example.administrator.sportmanager.admin.bean.Post> list = new java.util.ArrayList<>();
        android.database.Cursor c = null;
        try {
            c = db.rawQuery("SELECT _id,title,content,username,create_time FROM community_post ORDER BY _id DESC", null);
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String title = c.getString(1);
                String content = c.getString(2);
                String username = c.getString(3);
                String time = c.getString(4);
                list.add(new com.example.administrator.sportmanager.admin.bean.Post(id, title, content, username, time));
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /** 如果帖子表为空，则插入内置示例帖子 */
    public void ensureSampleCommunityPosts() {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        android.database.Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM community_post", null);
            if (c.moveToFirst()) {
                int count = c.getInt(0);
                if (count <= 0) {
                    db.execSQL("INSERT INTO community_post(title,content,username,create_time) VALUES(?,?,?,?)",
                            new Object[]{"器材租什么最划算？", "如果预算有限，我最推荐先租：\n1) 哑铃（最通用）\n2) 瑜伽垫（练核心必备）\n3) 拉力绳（性价比很高）\n先练出习惯再考虑升级装备！", "1", "2026-01-15 09:30"});
                    db.execSQL("INSERT INTO community_post(title,content,username,create_time) VALUES(?,?,?,?)",
                            new Object[]{"减脂期三餐食谱分享", "我最近减脂吃得比较干净：\n早餐：鸡蛋+燕麦+牛奶\n午餐：鸡胸+糙米+青菜\n晚餐：番茄鸡蛋汤+水果\n低油低糖，饱腹感还不错！", "user11", "2026-01-14 12:05"});
                    db.execSQL("INSERT INTO community_post(title,content,username,create_time) VALUES(?,?,?,?)",
                            new Object[]{"健身新手一周怎么练？", "新手建议一周 3 练：\nD1：全身力量（深蹲/推/拉）\nD3：有氧+核心\nD5：全身力量\n其余时间多走路+拉伸，动作标准比重量重要！", "lx", "2026-01-13 20:10"});
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    /** 根据 id 查询帖子详情 */
    public com.example.administrator.sportmanager.admin.bean.Post queryCommunityPostById(long postId) {
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        android.database.Cursor c = null;
        try {
            c = db.rawQuery("SELECT _id,title,content,username,create_time FROM community_post WHERE _id=? LIMIT 1",
                    new String[]{String.valueOf(postId)});
            if (c != null && c.moveToFirst()) {
                long id = c.getLong(0);
                String title = c.getString(1);
                String content = c.getString(2);
                String username = c.getString(3);
                String time = c.getString(4);
                return new com.example.administrator.sportmanager.admin.bean.Post(id, title, content, username, time);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** 是否已收藏该帖子 */
    public boolean isPostCollected(String user, long postId) {
        if (user == null) user = "";
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        android.database.Cursor c = null;
        try {
            c = db.rawQuery("SELECT _id FROM community_post_collect WHERE user=? AND post_id=? LIMIT 1",
                    new String[]{user, String.valueOf(postId)});
            return c != null && c.moveToFirst();
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return false;
    }

    /** 收藏帖子 */
    public void addPostCollect(String user, long postId, String collectTime) {
        if (user == null) user = "";
        if (collectTime == null) collectTime = "";
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("INSERT INTO community_post_collect(post_id,user,collect_time) VALUES(?,?,?)",
                    new Object[]{postId, user, collectTime});
        } catch (Exception ignored) {
        }
    }

    /** 取消收藏（按 user + postId） */
    public void cancelPostCollect(String user, long postId) {
        if (user == null) user = "";
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM community_post_collect WHERE user=? AND post_id=?",
                    new Object[]{user, postId});
        } catch (Exception ignored) {
        }
    }

    /** 取消收藏（按收藏记录id） */
    public void delPostCollectByCollectId(long collectId) {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM community_post_collect WHERE _id=?",
                    new Object[]{collectId});
        } catch (Exception ignored) {
        }
    }

    // ======================= 社区：我的收藏（封装成 List 方便 RecyclerView 使用） =======================

    /** 收藏列表行（只存收藏表字段，帖子标题等可以再查 post 表） */
    public static class PostCollectRow {
        public long collectId;
        public long postId;
        public String collectTime;
    }

    /** 查询当前用户的收藏记录（按时间倒序） */
    @android.annotation.SuppressLint("Range")
    public java.util.List<PostCollectRow> queryPostCollectList(String user) {
        java.util.List<PostCollectRow> out = new java.util.ArrayList<>();
        if (user == null) user = "";
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        android.database.Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT _id, post_id, collect_time FROM community_post_collect WHERE user=? ORDER BY _id DESC",
                    new String[]{user}
            );
            while (c.moveToNext()) {
                PostCollectRow r = new PostCollectRow();
                r.collectId = c.getLong(c.getColumnIndex("_id"));
                r.postId = c.getLong(c.getColumnIndex("post_id"));
                r.collectTime = c.getString(c.getColumnIndex("collect_time"));
                out.add(r);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return out;
    }



    /** 删除收藏记录（按收藏表主键 _id） */
    public void deletePostCollectById(long collectId) {
        delPostCollectByCollectId(collectId);
    }


    /**
     * 查询“我的收藏 - 社区收藏”
     * 返回字段：_id(post_collect), post_id, title, username, create_time
     */
    public android.database.Cursor queryPostCollectCursor(String user) {
        if (user == null) user = "";
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        try {
            return db.rawQuery(
                    "SELECT c._id as _id, p._id as post_id, p.title as title, p.username as username, p.create_time as create_time " +
                            "FROM community_post_collect c " +
                            "LEFT JOIN community_post p ON c.post_id = p._id " +
                            "WHERE c.user=? " +
                            "ORDER BY c._id DESC",
                    new String[]{user}
            );
        } catch (Exception e) {
            return null;
        }
    }
    /** 发布帖子：插入一条 community_post */
    public void insertCommunityPost(String title, String content, String username, String createTime) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("INSERT INTO community_post(title,content,username,create_time) VALUES(?,?,?,?)",
                    new Object[]{title, content, username, createTime});
        } catch (Exception e) {
            Log.e("DB_POST", "insertCommunityPost error：" + e.getMessage());
        }
    }


    // ======================= v8：点赞 =======================

    public boolean isPostLiked(String user, long postId) {
        if (user == null) user = "";
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery(
                    "SELECT _id FROM community_post_like WHERE user=? AND post_id=? LIMIT 1",
                    new String[]{user, String.valueOf(postId)}
            );
            return c != null && c.moveToFirst();
        } catch (Exception e) {
            return false;
        } finally {
            if (c != null) c.close();
        }
    }

    public void addPostLike(String user, long postId, String time) {
        if (user == null) user = "";
        try {
            getWritableDatabase().execSQL(
                    "INSERT INTO community_post_like(post_id,user,create_time) VALUES(?,?,?)",
                    new Object[]{postId, user, time}
            );
        } catch (Exception ignored) {}
    }

    public void cancelPostLike(String user, long postId) {
        if (user == null) user = "";
        try {
            getWritableDatabase().execSQL(
                    "DELETE FROM community_post_like WHERE user=? AND post_id=?",
                    new Object[]{user, postId}
            );
        } catch (Exception ignored) {}
    }

    public int queryPostLikeCount(long postId) {
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery(
                    "SELECT COUNT(*) FROM community_post_like WHERE post_id=?",
                    new String[]{String.valueOf(postId)}
            );
            if (c.moveToFirst()) return c.getInt(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

// ======================= v8：评论 =======================

    public void addPostComment(String user, long postId, String content, String time) {
        if (user == null) user = "";
        try {
            getWritableDatabase().execSQL(
                    "INSERT INTO community_post_comment(post_id,user,content,create_time) VALUES(?,?,?,?)",
                    new Object[]{postId, user, content, time}
            );
        } catch (Exception ignored) {}
    }

    public Cursor queryPostCommentCursor(long postId) {
        try {
            return getReadableDatabase().rawQuery(
                    "SELECT _id as _id, user, content, create_time FROM community_post_comment WHERE post_id=? ORDER BY _id DESC",
                    new String[]{String.valueOf(postId)}
            );
        } catch (Exception e) {
            return null;
        }
    }


    public java.util.List<com.example.administrator.sportmanager.admin.bean.Post> queryCommunityPostsByUser(String user) {
        if (user == null) user = "";
        java.util.List<com.example.administrator.sportmanager.admin.bean.Post> list = new java.util.ArrayList<>();
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery(
                    "SELECT _id,title,content,username,create_time FROM community_post WHERE username=? ORDER BY _id DESC",
                    new String[]{user}
            );
            while (c.moveToNext()) {
                list.add(new com.example.administrator.sportmanager.admin.bean.Post(
                        c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)
                ));
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * 管理员端：按关键字搜索用户（user / name / phone 模糊搜索）
     * 同时返回 member_status 字段（会员用户/普通用户）
     */
    public Cursor searchUsersWithMemberInfo(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        String like = "%" + keyword + "%";

        String sql = "SELECT _id,user,password,name,sex,birthday,phone,member_expire, " +
                "CASE WHEN member_expire IS NOT NULL AND member_expire != '' AND member_expire >= date('now') " +
                "THEN '会员用户' ELSE '普通用户' END AS member_status " +
                "FROM admin " +
                "WHERE user LIKE ? OR name LIKE ? OR phone LIKE ?";

        return db.rawQuery(sql, new String[]{like, like, like});
    }

    // ======================= v9：管理员端 - 社区帖子管理 =======================

    /**
     * 管理员端：查询帖子（支持关键字：标题/内容/发布者）
     * 返回字段：_id,title,content,username,create_time,warned,warn_reason,warn_time
     */
    public Cursor queryCommunityPostsForAdmin(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        String baseSql = "SELECT _id,title,content,username,create_time, " +
                "IFNULL(warned,0) AS warned, IFNULL(warn_reason,'') AS warn_reason, IFNULL(warn_time,'') AS warn_time " +
                "FROM community_post";

        if (keyword == null) keyword = "";
        keyword = keyword.trim();
        if (keyword.isEmpty()) {
            return db.rawQuery(baseSql + " ORDER BY _id DESC", null);
        }

        String like = "%" + keyword + "%";
        return db.rawQuery(
                baseSql + " WHERE title LIKE ? OR content LIKE ? OR username LIKE ? ORDER BY _id DESC",
                new String[]{like, like, like}
        );
    }

    /** 警告帖子：设置 warned=1，记录原因与时间 */
    public void warnCommunityPost(long postId, String reason, String warnTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("warned", 1);
        cv.put("warn_reason", reason == null ? "" : reason);
        cv.put("warn_time", warnTime == null ? "" : warnTime);
        db.update("community_post", cv, "_id=?", new String[]{String.valueOf(postId)});
    }

    /**
     * 删除帖子（同时清理：收藏/点赞/评论）
     * 说明：目前业务上删除=物理删除
     */
    public void deleteCommunityPost(long postId) {
        SQLiteDatabase db = getWritableDatabase();
        try { db.execSQL("DELETE FROM community_post_collect WHERE post_id=?", new Object[]{postId}); } catch (Exception ignored) {}
        try { db.execSQL("DELETE FROM community_post_like WHERE post_id=?", new Object[]{postId}); } catch (Exception ignored) {}
        try { db.execSQL("DELETE FROM community_post_comment WHERE post_id=?", new Object[]{postId}); } catch (Exception ignored) {}
        try { db.execSQL("DELETE FROM community_post WHERE _id=?", new Object[]{postId}); } catch (Exception ignored) {}
    }




}

