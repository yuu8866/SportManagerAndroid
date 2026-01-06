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
    private TextView pay_sportid, pay_sportname, pay_sportuser, pay_sportprice, pay_sporttype, pay_sportowner, pay_sportrank, pay_sportcomment, borrow_time, pay_days; // 添加 pay_days 变量声明
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
        pay_days = findViewById(R.id.pay_days); // 初始化 pay_days 变量

        Bundle bundle = this.getIntent().getExtras();
        pay_sportid.setText(bundle.getInt("sportid")+"");
        pay_sportname.setText(bundle.getString("sportname"));
        pay_sportuser.setText(bundle.getString("sportauthor"));
        int days = bundle.getInt("days", 1);
        pay_days.setText(days + "天");
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
        Log.e("DAYS_DEBUG", "bundle days = " + days);

        // ✅ 从 borrow 读取：总价(分)、支付状态、支付时间
        int totalFen = 0;
        int payStatus = 0;
        String payTime = "";

        Cursor bcInfo = help.queryBorrowById(borrowid);
        if (bcInfo != null) {
            try {
                if (bcInfo.moveToFirst()) {
                    int idxTotal = bcInfo.getColumnIndex("total_price");
                    if (idxTotal != -1) totalFen = bcInfo.getInt(idxTotal);

                    int idxStatus = bcInfo.getColumnIndex("pay_status");
                    if (idxStatus != -1) payStatus = bcInfo.getInt(idxStatus);

                    int idxPayTime = bcInfo.getColumnIndex("pay_time");
                    if (idxPayTime != -1) {
                        String t = bcInfo.getString(idxPayTime);
                        payTime = (t == null) ? "" : t;
                    }
                }
            } finally {
                bcInfo.close();
            }
        }

// ✅ 显示“应付总价”（分 -> 元，保留2位）
        pay_sportprice.setText(String.format("%.2f 元", totalFen / 100.0));

// ✅ 显示租赁时间 + 支付时间（不改布局，只改内容）
        String rentTime = bundle.getString("sporttime", "");
        borrow_time.setText("租赁：" + rentTime + "\n支付：" + (payStatus == 1 ? payTime : "未支付"));


        //订单按钮的事件监听
        paysport_bt = findViewById(R.id.pay_bt);

// ✅ 先查订单状态（pay_status）


// ✅ 根据状态改按钮文字
        if (payStatus == 0) {
            paysport_bt.setText("确认支付");
        } else {
            paysport_bt.setText("归还器材");
        }

        paysport_bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                // 再查一次，防止状态变化（保险）
                int curStatus = 0;
                Cursor c2 = help.queryBorrowById(borrowid);
                if (c2 != null) {
                    try {
                        if (c2.moveToFirst()) {
                            int stIdx = c2.getColumnIndex("pay_status");
                            if (stIdx != -1) curStatus = c2.getInt(stIdx);
                        }
                    } finally {
                        c2.close();
                    }
                }

                if (curStatus == 0) {
                    // ✅ 还没支付 → 执行支付
                    @SuppressLint("SimpleDateFormat")
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String now = sdf.format(new java.util.Date());

                    help.setBorrowPaid(borrowid, now);
                    borrow_time.setText("租赁：" + rentTime + "\n支付：" + now);
                    paysport_bt.setText("归还器材");

                    Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                    // 支付后按钮变成“归还器材”
                    paysport_bt.setText("归还器材");
                } else {
                    // ✅ 已支付 → 执行归还（保留你原来的归还逻辑）
                    // 你原来是 help.delborrow(sportid); 这里建议更稳一点：按 borrowid 删除
                    help.delBorrowById(borrowid); // 需要你在 databaseHelp 里新增这个方法（下面给）
                    Toast.makeText(PayActivity.this, "归还成功", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PayActivity.this, person_borrow.class);
                    startActivity(intent);
                    ActivityCollector.finishAll();
                }
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
        paysport_bt.setOnLongClickListener(v -> {
            // 只允许取消“未支付”订单
            int curStatus = 0;
            Cursor c = help.queryBorrowById(borrowid);
            if (c != null) {
                try {
                    if (c.moveToFirst()) {
                        int stIdx = c.getColumnIndex("pay_status");
                        if (stIdx != -1) curStatus = c.getInt(stIdx);
                    }
                } finally {
                    c.close();
                }
            }

            if (curStatus == 0) {
                help.delBorrowById(borrowid);
                Toast.makeText(PayActivity.this, "已取消订单（未支付）", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PayActivity.this, person_borrow.class));
                ActivityCollector.finishAll();
                return true;
            } else {
                Toast.makeText(PayActivity.this, "已支付订单请归还器材", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
