package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class PayActivity extends AppCompatActivity {
    private TextView pay_sportid, pay_sportname, pay_sportuser, pay_sportprice, pay_sporttype, pay_sportowner, pay_sportrank, pay_sportcomment, borrow_time;
    private ImageView pay_sportimg;
    private Button paysport_bt,pay_back_bt;
    private databaseHelp help;
    private int sportid,borrowid;
    private String time_str;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        help = new databaseHelp(getApplicationContext());

        pay_sportid = findViewById(R.id.pay_sportid);
        pay_sportname = findViewById(R.id.pay_sportname);
        pay_sportuser = findViewById(R.id.pay_sportuser);
        pay_sportprice = findViewById(R.id.pay_sportprice);

        pay_sporttype = findViewById(R.id.pay_sporttype);
        pay_sportowner = findViewById(R.id.pay_sportowner);
        pay_sportrank = findViewById(R.id.pay_sportrank);
        pay_sportcomment = findViewById(R.id.pay_sportcomment);
        borrow_time = findViewById(R.id.pay_time);

        Bundle bundle = this.getIntent().getExtras();
        pay_sportid.setText(bundle.getInt("sportid")+"");
        pay_sportname.setText(bundle.getString("sportname"));
        pay_sportuser.setText(bundle.getString("sportauthor"));
        borrow_time.setText(bundle.getString("sporttime"));
        borrowid=bundle.getInt("borrowid");

        sportid = bundle.getInt("sportid");
        Cursor cursor = help.querysportssportid(sportid);
        Log.i("count123", String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            pay_sportprice.setText(cursor.getString(cursor.getColumnIndex("price")));
//            pay_sportimg.setImageBitmap(byteToBitmap(cursor.getBlob(cursor.getColumnIndex("img"))));
            pay_sporttype.setText(cursor.getString(cursor.getColumnIndex("type")));
            pay_sportowner.setText(cursor.getString(cursor.getColumnIndex("owner")));
            pay_sportrank.setText(cursor.getString(cursor.getColumnIndex("rank")));
            pay_sportcomment.setText(cursor.getString(cursor.getColumnIndex("comment")));
        }

        //还书按钮的事件监听
        paysport_bt = findViewById(R.id.pay_bt);
        paysport_bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                Toast.makeText(PayActivity.this, "还器材成功", Toast.LENGTH_SHORT).show();
                /*
                删除相应的借书信息
                */
                help.delborrow(sportid);

                Intent intent = new Intent(PayActivity.this, person_borrow.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        pay_back_bt = findViewById(R.id.pay_back_bt);
        pay_back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, person_borrow.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
    }
}
