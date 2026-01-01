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
    private static final int DB_VERSION = 2;

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

    private static final String Creat_table = "create table admin(_id integer primary key autoincrement,user text  , name text, password text,sex text, phone text, birthday text)";

    public static final String Creat_table1 = "create table sports1 ("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    public static final String Creat_table2 = "create table " + Table_Sport + "("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    // ✅ borrow 表增加 days 字段（默认1）
    private static final String Creat_table3 =
            "create table borrow(_Bid integer primary key autoincrement," +
                    "Borname text," +
                    "sportid integer," +
                    "sportname text," +
                    "sportauthor text," +
                    "nowtime text," +
                    "days integer default 1)";

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

        // 初始化器材
        db.execSQL("insert into sports (sportid,name,type,user,owner,price,rank,comment) values " +
                "(0,'羽毛球拍','球类','孟岩','器材云',20,4.9,'借用者需保持拍面整洁，避免碰撞硬物，按时归还')," +
                "(1,'乒乓球拍','球类','小花','器材云',21,5.0,'借用者需小心使用，避免刮花拍面，不可私自更改拍面胶皮。')," +
                "(2,'网球拍','球类','小曼 ','器材云',22,4.2,'借用者需注意避免碰撞拍面，使用后将球拍放回原处。')," +
                "(3,'滚轮','轮式','烽月','器材云',23,4.3,'借用者需注意安全，避免在危险路段滑行，及时归还设备。')," +
                "(4,'橄榄球','轮式','家悦','器材云',24,4.4,'借用者需注意不要将球弄脏或损坏，按时归还。')," +
                "(5,'拉力绳','塑形','侯若飞','器材云',25,5.0,'借用者需正确使用，避免拉力过大导致断裂，保持整洁并妥善存放。')," +
                "(6,'跑步机','健身','越才','第三方平台',26,5.0,'使用完毕后，请及时清洁跑步机表面，保持卫生。')," +
                "(7,'动感单车','健身','小斯','器材云',27,4.7,'使用完毕后，请将动感单车放置在干燥通风的地方，避免生锈。')," +
                "(8,'无绳跳绳','绳类','威廉','第三方平台',28,4.8,'使用完毕后，请将跳绳卷好收纳，避免绳索缠绕。')," +
                "(9,'篮球','球类','李恩','第三方平台',29,4.9,'请在篮球充气适当时使用，避免充气不足或过足影响球的性能。')," +
                "(10,'足球','球类','莫员','器材云',210,5.0,'使用完毕后，请将足球放回指定位置，避免丢失。')," +
                "(11,'瑜伽垫','塑形','华华','第三方平台',211,4.5,'避免在瑜伽垫上使用尖锐物品，以免损坏垫面。')," +
                "(12,'排球','球类','小吾','器材云',212,4.0,'请在排球充气适当时使用，避免充气不足或过足影响球的性能。')");

        // 初始化用户
        db.execSQL("insert into admin (user,name,password,sex,phone,birthday) values " +
                "('admin','admin','admin','男','12345678901','2005.11.20')," +
                "('root','root','root','男','12345678901','2005.11.20')," +
                "('lx','lx','lx','男','12345678901','2005.11.20')," +
                "('1','1','1','女','12345678901','2005.11.20');");
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

    // ✅ 兼容 admin_add_sport.java 里旧的调用：helper.insersporttdata(...)
    public void insersporttdata(String sportid,
                                String name,
                                String type,
                                String user,
                                String owner,
                                String price,
                                String rank,
                                String comment,
                                byte[] img) {

        ContentValues values = new ContentValues();
        values.put("sportid", sportid);
        values.put("name", name);
        values.put("type", type);
        values.put("user", user);
        values.put("owner", owner);
        values.put("price", price);
        values.put("rank", rank);
        values.put("comment", comment);
        values.put("img", img);

        insertsports(values);
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

                data.add(map);
            }
        } finally {
            cursor.close();
        }
        return data;
    }

    // 在 collect 表中按用户查询
    public Cursor queryuser(String str) {
        db = getReadableDatabase();
        return db.query(Table_Name5, null, "Borname=?", new String[]{str}, null, null, null);
    }

    // ✅ 查询borrow表（按用户）——补上 days！（你租赁信息页面就是靠这个）
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
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE borrow ADD COLUMN days integer DEFAULT 1");
            } catch (Exception e) {
                Log.e("DB_UPGRADE", "days column may already exist: " + e.getMessage());
            }
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
}
