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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

// 用户从首页/租赁页选择器材后进入的详情页面
public class borrowActivity extends AppCompatActivity {

    private ImageView borrow_sportimg;
    private TextView borrow_sportwriter, borrow_sportname, borrow_sporttype,
            borrow_sportpublicer, borrow_sportprice, borrow_sportrank,
            borrow_sportcomment, borrow_sportid;

    private Button borrow_bt, collect_bt;
    private ImageView back_img; // 返回按钮（你XML里一般是ImageView）
    private String str, sport_id, sport_name;
    private ListView listView;

    private int intbid;  // 当前器材ID

    // 保存用户最近一次选择的租赁天数（默认1）
    private int days = 1;

    int id;

    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        // 获取当前系统时间（租赁时间 nowtime）
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日/HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        str = formatter.format(curDate);

        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("time", str);
        editor.apply();

        // ========== 绑定控件 ==========
        borrow_sportid = findViewById(R.id.borrow_sportid);
        borrow_sportname = findViewById(R.id.borrow_sportname);
        borrow_sportwriter = findViewById(R.id.borrow_sportuser);
        borrow_sportprice = findViewById(R.id.borrow_sportprice);
        borrow_sportimg = findViewById(R.id.borrow_sportimg);
        borrow_sporttype = findViewById(R.id.borrow_sporttype);
        borrow_sportpublicer = findViewById(R.id.borrow_sportowner);
        borrow_sportrank = findViewById(R.id.borrow_sportrank);
        borrow_sportcomment = findViewById(R.id.borrow_sportcomment);

        borrow_bt = findViewById(R.id.borroe_bt);
        collect_bt = findViewById(R.id.collect_bt);


        // 返回按钮（你布局里一般叫 borrow_back_bt，是 ImageView）
        try {
            back_img = findViewById(R.id.borrow_back_bt);
        } catch (Exception ignore) {}

        // ========== 获取器材ID ==========
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "参数缺失，无法打开详情", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        id = bundle.getInt("id") + 1;

        final databaseHelp help = new databaseHelp(getApplicationContext());
        final Cursor cursor = help.querysportsid(id);

        Log.i("cursor", "onCreate: " + cursor.getCount());

        // ========== 信息显示 ==========
        if (cursor != null && cursor.getCount() > 0) {
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

            // 当前器材ID intbid
            try {
                intbid = Integer.parseInt(cursor.getString(cursor.getColumnIndex("sportid")));
            } catch (Exception e) {
                intbid = id;
            }
        } else {
            Toast.makeText(this, "未查询到器材信息", Toast.LENGTH_SHORT).show();
        }

        // ========== 租赁按钮：弹出选择租赁天数 ==========
        borrow_bt.setOnClickListener(v -> showDaysDialog(help));

        // ========== 收藏按钮 ==========
        collect_bt.setOnClickListener(v -> doCollect(help, cursor));

        // ========== 返回按钮：永远回到新导航页（租赁Tab） ==========
        if (back_img != null) {
            back_img.setOnClickListener(v -> backToUserNavRent());
        }

        // 关闭 cursor（避免泄漏）
        // ⚠️ 注意：我们在收藏时还会用到 cursor 的 img，所以这里不 close，
        // 你如果担心泄漏，可以在收藏时重新查一次图片。
        // if (cursor != null) cursor.close();
    }

    @Override
    public void onBackPressed() {
        backToUserNavRent();
    }

    // ✅ 关键：禁止返回到旧 contentActivity（老界面）
    private void backToUserNavRent() {
        Intent i = new Intent(this, UserNavActivity.class);
        i.putExtra(UserNavActivity.EXTRA_OPEN_TAB, UserNavActivity.TAB_RENT);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    // ✅ 弹窗选择租赁天数（普通最多 7 天，会员最多 30 天）
    private void showDaysDialog(databaseHelp help) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String username = sp.getString("users", "");

        boolean isMember = help.isMemberActive(username);

        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(1);
        picker.setMaxValue(isMember ? 30 : 7); // ✅ 核心就在这
        picker.setValue(1);
        picker.setWrapSelectorWheel(false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isMember ? "请选择租赁天数（会员最多30天）" : "请选择租赁天数（普通最多7天）")
                .setView(picker)
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .setPositiveButton("确定", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            days = picker.getValue();
            dialog.dismiss();
            doBorrow(help, days);
        }));

        dialog.show();
    }


    // ✅ 创建租赁记录（写入 borrow 表）
    private void doBorrow(databaseHelp help, int days) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String username = sp.getString("users", "");

        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String sportName = borrow_sportname.getText().toString();

        // ========= 会员优先借用：热门大件器材（示例：跑步机/动感单车）仅会员可直接借用 =========
        boolean memberActive = help.isMemberActive(username);
        boolean vipOnly = (intbid == 6 || intbid == 7);
        if (vipOnly && !memberActive) {
            Toast.makeText(this, "该器材为热门大件，会员可优先借用，请先开通会员", Toast.LENGTH_SHORT).show();
            try {
                Intent i = new Intent(this, MemberCardActivity.class);
                startActivity(i);
            } catch (Exception ignore) {}
            return;
        }

        // 1）防止重复租赁
        Cursor cur = help.checkborrowinfo(sportName, username);
        if (cur != null) {
            try {
                if (cur.getCount() > 0) {
                    Toast.makeText(this, "你已经租赁过该器材啦", Toast.LENGTH_SHORT).show();
                    return;
                }
            } finally {
                cur.close();
            }
        }

        // 2）计算应付金额：单价 × 天数（会员 9 折）
        double unitPrice = 0;
        try {
            String p = borrow_sportprice.getText().toString().replaceAll("[^0-9.]", "");
            unitPrice = Double.parseDouble(p);
        } catch (Exception ignored) {}

        double totalYuan = unitPrice * days;
        if (memberActive) totalYuan = totalYuan * 0.9;
        int totalFen = (int) Math.round(totalYuan * 100);

        // 3）插入 borrow（pay_status 默认 0）
        ContentValues values = new ContentValues();
        values.put("sportid", intbid);
        values.put("sportname", sportName);
        values.put("sportauthor", borrow_sportwriter.getText().toString());
        values.put("Borname", username);
        values.put("nowtime", str);

        values.put("days", days);
        values.put("total_price", totalFen);
        values.put("pay_status", 0);

        long res = help.getWritableDatabase().insert("borrow", null, values);

        if (res != -1) {
            Toast.makeText(this, "租赁成功（" + days + "天）", Toast.LENGTH_SHORT).show();
            long borrowId = help.insertBorrowReturnId(values);

            Intent pay = new Intent(borrowActivity.this, PayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("sportid", intbid);
            bundle.putInt("borrowid", (int) borrowId);
            bundle.putString("sportname", sportName);
            bundle.putString("sportauthor", borrow_sportwriter.getText().toString());
            bundle.putString("sporttime", str);
            bundle.putInt("days", days);
            pay.putExtras(bundle);

            startActivity(pay);
            finish();

        } else {
            Toast.makeText(this, "租赁失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }




    // ✅ 收藏功能（写入 collect 表）
    @SuppressLint("Range")
    private void doCollect(databaseHelp help, Cursor cursor) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String username = sp.getString("users", "");

        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String sportName = borrow_sportname.getText().toString();

        // 防重复收藏
        Cursor cur = help.checkcollectinfo(sportName, username);
        if (cur != null) {
            try {
                if (cur.getCount() > 0) {
                    Toast.makeText(this, "你已经收藏过该器材啦", Toast.LENGTH_SHORT).show();
                    return;
                }
            } finally {
                cur.close();
            }
        }

        ContentValues values = new ContentValues();
        values.put("sportid", intbid);
        values.put("sportname", sportName);
        values.put("sportauthor", borrow_sportwriter.getText().toString());
        values.put("Borname", username);
        values.put("nowtime", str);
        values.put("type", borrow_sporttype.getText().toString());
        values.put("rank", borrow_sportrank.getText().toString());
        values.put("price", borrow_sportprice.getText().toString());

        // 图片 blob 存入（如果 cursor 为空就不存）
        try {
            if (cursor != null && cursor.getCount() > 0) {
                byte[] imgBlob = cursor.getBlob(cursor.getColumnIndex("img"));
                values.put("img", imgBlob);
            }
        } catch (Exception ignore) {}

        help.insertocollect(values);

        Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(borrowActivity.this, collectActivity.class));
        finish();
    }
}
