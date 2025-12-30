package com.example.administrator.sportmanager.admin.houtai_admin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.bitmapToByte;
import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

/**
 * 管理员添加运动器械的界面
 */

public class admin_update_sport extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back_bt;
    //    private EditText name_ed, author_Ed, page_ed, price_ed, publish_ed, intime_rd;
    private Button add_sport_bt;
    private String str;
    Bitmap bmp= null;
    private int id;
    private databaseHelp helper;
    Uri uri;
    private ImageView sportimg;
    private EditText et_sportid,et_sportname,et_sporttype,et_sportuser,et_sportowner,et_sportprice,et_sportrank,et_sportcomment;
    private Button btn_sportcommit,btn_sportback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upadte_sport);

        initdata();//界面初始化
        helper = new databaseHelp(getApplicationContext());
    }

    @SuppressLint("Range")
    private void initdata() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id")+1 ;
        Log.i("cursor", "initdata: " + id);
        sportimg=findViewById(R.id.admin_sportimg);
        et_sportid=findViewById(R.id.et_sportid);
        et_sportname=findViewById(R.id.et_sportname);
        et_sporttype=findViewById(R.id.et_sporttype);
        et_sportuser=findViewById(R.id.et_sportuser);
        et_sportowner=findViewById(R.id.et_sportowner);
        et_sportprice=findViewById(R.id.et_sportprice);
        et_sportrank=findViewById(R.id.et_sportrank);
        et_sportcomment=findViewById(R.id.et_sportcomment);


        btn_sportcommit=findViewById(R.id.btn_sportcommit);
        btn_sportback=findViewById(R.id.btn_sportback);

        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.querysportsid(id);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sportimg.setImageBitmap(byteToBitmap(cursor.getBlob(cursor.getColumnIndex("img"))));
            bmp = byteToBitmap(cursor.getBlob(cursor.getColumnIndex("img")));
            et_sportid.setText(cursor.getString(cursor.getColumnIndex("sportid")));
            et_sportname.setText(cursor.getString(cursor.getColumnIndex("name")));
            et_sportuser.setText(cursor.getString(cursor.getColumnIndex("user")));
            et_sporttype.setText(cursor.getString(cursor.getColumnIndex("type")));
            et_sportowner.setText(cursor.getString(cursor.getColumnIndex("owner")));
            et_sportrank.setText(cursor.getString(cursor.getColumnIndex("rank")));
            et_sportcomment.setText(cursor.getString(cursor.getColumnIndex("comment")));
            et_sportprice.setText(cursor.getString(cursor.getColumnIndex("price")));
        }

        btn_sportcommit.setOnClickListener(this);
        sportimg.setOnClickListener(this);
        btn_sportback.setOnClickListener(this);

    }
    //对管理员输入的运动器械信息进行验证，全部符合要求才能通过
    boolean testid=true,testother=true;
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_sportcommit:

                if (et_sportid.getText() == null) {
                    Toast.makeText(admin_update_sport.this,"请输入运动器械id",Toast.LENGTH_SHORT).show();
                    testid=false;
                    break;
                }

                if(et_sportname.getText().length()==0){
                    Toast.makeText(admin_update_sport.this,"请输入完整运动器械信息",Toast.LENGTH_SHORT).show();
                    testother=false;
                    break;
                }
                if(testid==true&&testother==true){
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name", et_sportname.getText().toString());
                    values.put("user", et_sportuser.getText().toString());
                    values.put("sportid", et_sportid.getText().toString());
                    values.put("price", et_sportprice.getText().toString());
                    values.put("owner", et_sportowner.getText().toString());
                    values.put("comment", et_sportcomment.getText().toString());
                    values.put("rank", et_sportrank.getText().toString());
                    values.put("type", et_sporttype.getText().toString());
                    values.put("img", bitmapToByte(bmp));
                    db.update("sports", values, "_id=?", new String[]{String.valueOf(id)});
                    Log.i("123", String.valueOf(et_sportid));
                    Toast.makeText(admin_update_sport.this,"修改器械成功！",Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.admin_sportimg:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);  // 第二个参数是请求码
                break;

            case R.id.btn_sportback:
                Intent intentback=new Intent();
                intentback.setClass(admin_update_sport.this, admin_select_sports.class);
                startActivity(intentback);
                ActivityCollector.finishAll();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:  // 请求码
                parseUri(data);
                break;
            default:
        }
    }

    public void parseUri(Intent data) {
        try {
            uri=data.getData();
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        InputStream is=null;
        if(uri.getAuthority()!=null){
            try {
                is= admin_update_sport.this.getContentResolver().openInputStream(uri);
                bmp= BitmapFactory.decodeStream(is);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sportimg.setImageBitmap(bmp);
        }
        if (uri.getAuthority()==null)
            return;
    }

    //    private void init() {
//        //返回按钮的事件监听
//        back_bt = (ImageButton) findViewById(R.id.addsport_back);
//        back_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(admin_add_sport.this, admin_manager_sport.class);
//                startActivity(intent);
//            }
//        });
////        name_ed = (EditText) findViewById(R.id.add_bokname);
////        author_Ed = (EditText) findViewById(R.id.add_bokauthor);
////        page_ed = (EditText) findViewById(R.id.add_bokpage);
////        price_ed = (EditText) findViewById(R.id.add_bokprice);
////        publish_ed = (EditText) findViewById(R.id.add_bokpublish);
//
//        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日    ");
//        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
//        str    =    formatter.format(curDate);
//        //添加按钮的事件监听
//        add_sport_bt = (Button) findViewById(R.id.add_sport);
//        add_sport_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                String strname = name_ed.getText().toString();
////                String strauthor = author_Ed.getText().toString();
////                String strpage = page_ed.getText().toString();
////                String strprice = price_ed.getText().toString();
////                String strpublish = publish_ed.getText().toString();
//
//                //将字符串型转换成double类型
////                Double dprice = Double.parseDouble(strprice);
////                if (strname.equals("")) {
////                    Toast.makeText(admin_add_sport.this, "名称不能为空，请重新输入",
////                            Toast.LENGTH_LONG).show();
////
////                } else if (strauthor.equals("")) {
////                    Toast.makeText(admin_add_sport.this, "作者不能为空，请重新输入",
////                            Toast.LENGTH_LONG).show();
////
////                } else if ("".equals(dprice)) {
////                    Toast.makeText(admin_add_sport.this, "价格不能为空，请重新输入",
////                            Toast.LENGTH_LONG).show();
////
////                } else if (strpage.equals("")) {
////                    Toast.makeText(admin_add_sport.this, "页数不能为空，请重新输入",
////                            Toast.LENGTH_LONG).show();
////
////                } else if (strpublish.equals("")) {
////                    Toast.makeText(admin_add_sport.this, "出版社不能为空，请重新输入",
////                            Toast.LENGTH_LONG).show();
////
////                } else {
////                    ContentValues values = new ContentValues();
////                    values.put("sportname", strname);
////                    values.put("author", strauthor);
////                    values.put("page", strpage);
////                    values.put("price", strprice);
////                    values.put("publish", strpublish);
////                    values.put("intime", str);
////                    databaseHelp helper = new databaseHelp(
////                            getApplicationContext());
//                    helper.insertsports(values);
////                    Toast.makeText(admin_add_sport.this, "运动器械添加成功",
////                            Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(admin_add_sport.this,
////                            admin_manager_sport.class);
////                    startActivity(intent);
////                    ActivityCollector.finishAll();
////                }
//            }
//        });
//    }
}
