package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.example.administrator.sportmanager.admin.utils.PaymentDialogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PayActivity extends AppCompatActivity {

    private TextView pay_sportid, pay_sportname, pay_sportuser, pay_sportprice,
            pay_sporttype, pay_sportowner, pay_sportrank, pay_sportcomment,
            borrow_time, pay_days;

    private Button paysport_bt, pay_back_bt;
    private databaseHelp help;
    private int sportid, borrowid;

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
        pay_days = findViewById(R.id.pay_days);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "参数缺失", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pay_sportid.setText(bundle.getInt("sportid") + "");
        pay_sportname.setText(bundle.getString("sportname"));
        pay_sportuser.setText(bundle.getString("sportauthor"));

        int days = bundle.getInt("days", 1);
        pay_days.setText(days + "天");
        borrowid = bundle.getInt("borrowid");

        sportid = bundle.getInt("sportid");
        Cursor cursor = help.querysportssportid(sportid);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            pay_sportprice.setText(cursor.getString(cursor.getColumnIndex("price")));
            pay_sporttype.setText(cursor.getString(cursor.getColumnIndex("type")));
            pay_sportowner.setText(cursor.getString(cursor.getColumnIndex("owner")));
            pay_sportrank.setText(cursor.getString(cursor.getColumnIndex("rank")));
            pay_sportcomment.setText(cursor.getString(cursor.getColumnIndex("comment")));
        }
        cursor.close();

        Log.e("DAYS_DEBUG", "bundle days = " + days);

        // 从 borrow 读取总价/支付状态/支付时间
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

        pay_sportprice.setText(String.format("%.2f 元", totalFen / 100.0));

        String rentTime = bundle.getString("sporttime", "");
        borrow_time.setText("租赁：" + rentTime + "\n支付：" + (payStatus == 1 ? payTime : "未支付"));

        paysport_bt = findViewById(R.id.pay_bt);
        pay_back_bt = findViewById(R.id.pay_back_bt);

        if (payStatus == 0) {
            paysport_bt.setText("确认支付");
        } else {
            paysport_bt.setText("立刻归还");
        }


        final int finalTotalFen = totalFen;
        final double finalPayYuan = finalTotalFen / 100.0;
        final String finalRentTime = rentTime;

        paysport_bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

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
                    // 未支付 → 弹支付弹窗
                    PaymentDialogUtil.showPayDialog(
                            PayActivity.this,
                            "选择支付方式（应付 ¥" + String.format("%.2f", finalPayYuan) + "）",
                            finalPayYuan,
                            false,
                            true,
                            (channel, amount) -> {

                                if ("余额支付".equals(channel)) {
                                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                                    String username = sp.getString("users", "");
                                    double bal = help.getWalletBalance(username);
                                    if (bal < finalPayYuan) {
                                        Toast.makeText(PayActivity.this, "余额不足，请先充值", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    double after = bal - finalPayYuan;
                                    help.updateWalletBalance(username, after);

                                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    help.insertWalletFlow(username, "租借扣款", -finalPayYuan, "余额支付", after, now);
                                }

                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String now = sdf.format(new Date());

                                help.setBorrowPaid(borrowid, now);
                                borrow_time.setText("租赁：" + finalRentTime + "\n支付：" + now);

                                paysport_bt.setText("归还器材");
                                Toast.makeText(PayActivity.this, channel + " 支付成功", Toast.LENGTH_SHORT).show();
                            }
                    );
                } else {
                    // 已支付 → 归还器材（删除订单）
                    help.delBorrowById(borrowid);
                    Toast.makeText(PayActivity.this, "归还成功", Toast.LENGTH_SHORT).show();

                    // ✅ 归还成功后直接 finish 回上一个页面（不会跳老界面）
                    finish();
                }
            }
        });

        // ✅ 返回按钮：只做 finish（回到上一个页面），不会跳到老 contentActivity
        pay_back_bt.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
