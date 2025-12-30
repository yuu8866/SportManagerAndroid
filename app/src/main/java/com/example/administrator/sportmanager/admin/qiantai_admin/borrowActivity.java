package com.example.administrator.sportmanager.admin.qiantai_admin;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

//用户从首页选择书目后进入的借书页面

public class borrowActivity extends AppCompatActivity {
    private ImageView borrow_sportimg;
    private TextView borrow_sportwriter,borrow_sportname,borrow_sporttype,borrow_sportpublicer,borrow_sportprice,borrow_sportrank,borrow_sportcomment,borrow_sportid;
    private Button borrow_bt,collect_bt,remark_bt,back;
    private String str,sport_id,sport_name;
    private ListView listView;
    int id;
    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        /*
        获取当前系统时间
        用作借书日期time
         */
        SimpleDateFormat formatter = new SimpleDateFormat    ("yyyy年MM月dd日/HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str = formatter.format(curDate);
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("time",str);
        editor.apply();



        borrow_sportid=findViewById(R.id.borrow_sportid);
        borrow_sportname=findViewById(R.id.borrow_sportname);
        borrow_sportwriter=findViewById(R.id.borrow_sportuser);
        borrow_sportprice=findViewById(R.id.borrow_sportprice);
        borrow_sportimg=findViewById(R.id.borrow_sportimg);
        borrow_sporttype=findViewById(R.id.borrow_sporttype);
        borrow_sportpublicer=findViewById(R.id.borrow_sportowner);
        borrow_sportrank=findViewById(R.id.borrow_sportrank);
        borrow_sportcomment=findViewById(R.id.borrow_sportcomment);

        //获取id
        Bundle bundle=this.getIntent().getExtras();
        id=bundle.getInt("id") + 1;
        final databaseHelp help=new databaseHelp(getApplicationContext());
        final Cursor cursor=help.querysportsid(id);
        Log.i("cursor", "onCreate: " +cursor.getCount());
        //信息显示
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            borrow_sportid.setText(cursor.getString(cursor.getColumnIndex("sportid")));
            sport_id = cursor.getString(cursor.getColumnIndex("sportid"));
            borrow_sportname.setText(cursor.getString(cursor.getColumnIndex("name")));
            sport_name = cursor.getString(cursor.getColumnIndex("name"));
            borrow_sportwriter.setText(cursor.getString(cursor.getColumnIndex("user")));
            borrow_sportprice.setText(cursor.getString(cursor.getColumnIndex("price")));
            borrow_sportimg.setImageBitmap(byteToBitmap(cursor.getBlob(cursor.getColumnIndex("img"))));
            borrow_sporttype.setText(cursor.getString(cursor.getColumnIndex("type")));
            borrow_sportpublicer.setText(cursor.getString(cursor.getColumnIndex("owner")));
            borrow_sportrank.setText(cursor.getString(cursor.getColumnIndex("rank")));
            borrow_sportcomment.setText(cursor.getString(cursor.getColumnIndex("comment")));
        }



        borrow_bt=(Button)findViewById(R.id.borroe_bt);
        borrow_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //借器材日期
                SharedPreferences perf=getSharedPreferences("data",MODE_PRIVATE);
                // String datetime=perf.getString("time","");//获得当前系统时间
                String username=perf.getString("users","");//获得当前用户名称
                String strbname=borrow_sportname.getText().toString();//器材名
                //查询是否已经借过
                Cursor cur = help.checkborrowinfo(strbname,username);
                if (cur.getCount()>0)
                {
                    Toast.makeText(borrowActivity.this,"您已经租赁该器材!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //将器械信息插入借阅表中
                String strbid=borrow_sportid.getText().toString();
                String strbauthor=borrow_sportwriter.getText().toString();
                int intbid=Integer.parseInt(strbid);
                ContentValues values=new ContentValues();
                values.put("sportid",intbid);
                values.put("sportname",strbname);
                values.put("sportauthor",strbauthor);
                values.put("Borname",username);
                values.put("nowtime",str);
                help.insertorrowo(values);
                //获取当前用户
                Toast.makeText(borrowActivity.this,"租赁成功",Toast.LENGTH_SHORT).show();
            }
        });
        collect_bt=(Button)findViewById(R.id.collect_bt);
        collect_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将器械信息插入收藏表中
                //收藏日期
                SharedPreferences perf=getSharedPreferences("data",MODE_PRIVATE);
                // String datetime=perf.getString("time","");//获得当前系统时间
                String username=perf.getString("users","");//获得当前用户名称
                String strbname=borrow_sportname.getText().toString();

                //查询是否收藏过
                Cursor cur = help.checkcollectinfo(strbname,username);
                if (cur.getCount()>0)
                {
                    Toast.makeText(borrowActivity.this,"您已经收藏了这个器材!",Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues values=new ContentValues();
                values.put("sportid",Integer.parseInt(borrow_sportid.getText().toString()));
                values.put("sportname",strbname);
                values.put("sportauthor",borrow_sportwriter.getText().toString());
                values.put("Borname",username);
                values.put("nowtime",str);
                values.put("type",borrow_sporttype.getText().toString());
                values.put("rank",borrow_sportrank.getText().toString());
                values.put("price",borrow_sportprice.getText().toString());
                values.put("img",cursor.getBlob(cursor.getColumnIndex("img")));
                help.insertocollect(values);
                //获取当前用户
                Toast.makeText(borrowActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
            }
        });

        ImageView back=findViewById(R.id.borrow_back_bt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(borrowActivity.this, com.example.administrator.sportmanager.admin.qiantai_admin.contentActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
    }

}
