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
    public databaseHelp(Context context) {
        super(context, DB_NAME, null, 1);
    }
    private static final String Table_Name1 = "admin";//用户表
    private static final String Table_Name2 = "sports";//运动器械表
    private static final String Table_Name3 = "borrow";//借书表
    private static final String Table_Name5 = "collect";//收藏表

    public static final String id = "_id";
    public static final String Table_Sport = "sports";
    public static final String Sport_id = "sportid";//书号（运动器械的ISBN）
    public static final String Sport_Name = "name";
    public static final String Sport_Type = "type";
    public static final String Sport_user = "user";
    public static final String Sport_owner = "owner";//出版社
    public static final String Sport_Price = "price";
    public static final String Sport_Rank = "rank";//等级或者评分
    public static final String Sport_Comment = "comment";//简介
    public static final String Sport_Img = "img";

    private static final String Creat_table = "create table admin(_id integer primary key autoincrement,user text , name text, password text,sex text, phone text, birthday text)";

    public static final String Creat_table1 = "create table sports1 ("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    public static final String Creat_table2 = "create table " + Table_Sport + "("
            + id + " integer primary key autoincrement," + Sport_id + "," + Sport_Name + "," + Sport_Type + " text,"
            + Sport_user + "," + Sport_owner + "," + Sport_Price + "," + Sport_Rank + "," + Sport_Comment + "," + Sport_Img + " BLOB DEFAULT NULL)";

    private static final String Creat_table3 = "create table borrow(_Bid integer primary key autoincrement,Borname text,sportid integer,sportname text,sportauthor text,nowtime text)";
    //借书表，Bid是借阅记录编号，Borname是借阅者名称，sportname 是书名，sportid是书籍编号，sportauthor是作者名称，nowtime是当前系统时间
    private static final String Creat_table5 = "create table collect(_id integer primary key autoincrement,Borname text,sportid integer,sportname text,sportauthor text,nowtime text,type text,rank text,price text,img blob)";



    private static final String Creat_table7 = "create table admin1(_id integer primary key autoincrement,user text , name text, password text,sex text, phone text, birthday text)";
    SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db = db;
        db.execSQL(Creat_table);
        db.execSQL(Creat_table2);
        db.execSQL(Creat_table3);

        db.execSQL(Creat_table5);

        //初始化书单
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
        //初始化用户
        db.execSQL("insert into admin (user,name,password,sex,phone,birthday) values " +
                "('admin','admin','admin','男','12345678901','2022.11.20')," +
                "('root','root','root','男','12345678901','2022.11.20')," +
                "('lx','lx','lx','男','12345678901','2022.11.20')," +
                "('1','1','1','女','12345678901','2022.11.20');");
    }



    //插入图片
    public void updateImg(ContentValues values, int id) {
        db = getWritableDatabase();
        db.update("sports", values, "_id=?", new String[]{String.valueOf(id)});
        //db.execSQL("update sports set img = "+img+" where _id ="+id);
    }

    //根据sportid和sportname查找评论
    public Cursor remarkInfo(String sportid, String sportname) {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM remark WHERE sportname = '" + sportname + "' AND sportid = '" + sportid + "' order by nowtime desc;", null);
        return cursor;
    }

    //插入评论
    public boolean insertRemark(String Rmkname, String sportid, String sportname, String time, String remark) {
        db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("Rmkname", Rmkname);
        values.put("sportid", sportid);
        values.put("sportname", sportname);
        values.put("nowtime", time);
        values.put("rem", remark);
        Log.i("vale", "insertRemark: " + values + "112312");
        long line = db.insert("remark", null, values);
        db.close();
        if (line != -1) {
            return true;
        } else {
            return false;
        }
    }

    //推荐
    public Cursor recommend() {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM sports order by rank DESC", null);
        return cursor;
    }

    //sportsid重新排序
    public void sortsportsid() {
        db = getReadableDatabase();
        db.execSQL(Creat_table1);
        db.execSQL("insert into sports1 (sportid,name,type,user,owner,price,rank,comment,img) select sportid,name,type,user,owner,price,rank,comment,img from sports");
        db.execSQL("drop table sports");
        db.execSQL("alter table sports1 rename to sports");
        db.close();
    }

    //userid重新排序
    public void sortuserid() {
        db = getReadableDatabase();
        db.execSQL(Creat_table7);
        db.execSQL("insert into admin1 (user,name,password,sex,phone,birthday) select user,name,password,sex,phone,birthday from admin");
        db.execSQL("drop table admin");
        db.execSQL("alter table admin1 rename to admin");
        db.close();
    }

    //根据_id和用户名查询boroow表中的数据
    public Cursor checkborrowinfo(String sportname, String name) {
        db = getReadableDatabase();
        //Cursor cursor = db.query(Table_Name3, null, "sportname = \'" +sportname+"\'Borname = \'"+name+"\'" , null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT sportname,Borname FROM borrow WHERE sportname = \'" + sportname + "\' AND Borname = \'" + name + "\';", null);
        //Cursor cursor = db.execSQL("SELECT sportname,Borname FROM borrow WHERE sportname = \'"+sportname+"\' AND Borname = \'"+name+"\';");
        return cursor;

    }

    //根据_id和用户名查询collect表中的数据
    public Cursor checkcollectinfo(String sportname, String name) {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sportname,Borname FROM collect WHERE sportname = \'" + sportname + "\' AND Borname = \'" + name + "\';", null);
        return cursor;

    }


    //往admin表中插入信息
    public void insert(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name1, null, values);
        db.close();
    }

    //把新添加的运动器械数据插入到sport表中
    public boolean insersporttdata(String sportid, String name, String type, String user,
                                  String owner, String price, String rank, String comment, byte[] img) {
        db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Sport_id, sportid);
        values.put(Sport_Name, name);
        values.put(Sport_Type, type);
        values.put(Sport_user, user);
        values.put(Sport_owner, owner);
        values.put(Sport_Price, price);
        values.put(Sport_Rank, rank);
        values.put(Sport_Comment, comment);
        values.put(Sport_Img, img);
        long line = db.insert("sports", null, values);
        db.close();
        if (line != -1) {
            return true;
        } else {
            return false;
        }
    }

    //查询所有读者信息
    public Cursor query() {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name1, null, null, null, null, null, null);
        return cursor;

    }

    //删除读者的信息
    public void del(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name1, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    //通过id查询读者信息
    public Cursor queryid(int id) {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name1, null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        return cursor;
    }

    //通过name查询读者信息
    public Cursor queryname(String name) {
        Log.i("inut:", "queryname: " + name);
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name1, null, "user=?", new String[]{name}, null, null, null);
        return cursor;
    }

    //往sports表中插入数据
    public void insertsports(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name2, null, values);
        db.close();

    }

    //往sports中查询所有数据
    public Cursor querysports() {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name2, null, null, null, null, null, null);
        return cursor;
    }

    //往sports表中通过_id查找
    public Cursor querysportsid(int id) {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name2, null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        return cursor;
    }

    //往sports表中通过sportid查找
    public Cursor querysportssportid(int id) {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name2, null, "sportid=?", new String[]{String.valueOf(id)}, null, null, null);
        //mtCursor cursor = db.rawQuery("select * from sports where sportid = "+id,null);
        return cursor;
    }

    //往sports表中通过name查找
    public Cursor querysportsname(String name) {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name2, null, "name like ?", new String[]{"%" + name + "%"}, null, null, null, null);
        return cursor;
    }

    //删除运动器械信息
    public void delsports(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name2, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    //往borrow表中添加数据
    public void insertorrowo(ContentValues values) {
        db = getReadableDatabase();
        db.insert(Table_Name3, null, values);
        db.close();

    }

    //往collect表中添加数据
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


    //查询borrow表中的所有数据
    @SuppressLint("Range")
    public List<Map<String, Object>> queryborrow() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from borrow order by Borname asc", null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("_Bid", cursor.getString(cursor.getColumnIndex("_Bid")));
            map.put("Borname", cursor.getString(cursor.getColumnIndex("Borname")));
            map.put("sportid", cursor.getString(cursor.getColumnIndex("sportid")));
            map.put("sportname", cursor.getString(cursor.getColumnIndex("sportname")));
            map.put("sportauthor", cursor.getString(cursor.getColumnIndex("sportauthor")));
            map.put("nowtime", cursor.getString(cursor.getColumnIndex("nowtime")));
            data.add(map);
        }
        return data;
    }
    //在collector表中根据用户查询

    public Cursor queryuser(String str) {
        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name5, null, "Borname=?", new String[]{str}, null, null, null);
        return cursor;
    }

    //查询borrow表中的所有数据
    @SuppressLint("Range")
    public List<Map<String, Object>> queryborrow(String str) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        db = getReadableDatabase();
        Cursor cursor = db.query(Table_Name3, null, "Borname=?", new String[]{str}, null, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("_Bid", cursor.getInt(cursor.getColumnIndex("_Bid")));
            map.put("Borname", cursor.getString(cursor.getColumnIndex("Borname")));
            map.put("sportid", cursor.getInt(cursor.getColumnIndex("sportid")));
            map.put("sportname", cursor.getString(cursor.getColumnIndex("sportname")));
            map.put("sportauthor", cursor.getString(cursor.getColumnIndex("sportauthor")));
            map.put("bortime", cursor.getString(cursor.getColumnIndex("nowtime")));
            data.add(map);
        }
        return data;
    }

    //通过删除borrow表的信息
    public void delborrow(int id) {
        db = getReadableDatabase();
        db.delete(Table_Name3, "sportid=?", new String[]{String.valueOf(id)});
        db.close();
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(Creat_table);
                break;
            case 2:
                db.execSQL(Creat_table2);
                break;
            case 3:
                db.execSQL(Creat_table3);
                break;

            case 4:
                db.execSQL(Creat_table5);
                break;
            default:

        }

    }

    //打开外键
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }



}
