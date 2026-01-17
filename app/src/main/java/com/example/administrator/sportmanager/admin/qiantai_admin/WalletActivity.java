package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.example.administrator.sportmanager.admin.utils.PaymentDialogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {

    private TextView tvBalance, tvMemberStatus, tvMemberDays;
    private Button btnRecharge, btnMemberCard, btnMoreRecharge, btnMoreMember;
    private ListView lvRecharge, lvMember;

    private databaseHelp dbHelper;
    private String currentUser;

    private static final SimpleDateFormat TIME_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        dbHelper = new databaseHelp(this);

        tvBalance = findViewById(R.id.tv_balance);
        tvMemberStatus = findViewById(R.id.tv_member_status);
        tvMemberDays = findViewById(R.id.tv_member_days);

        btnRecharge = findViewById(R.id.btn_recharge);
        btnMemberCard = findViewById(R.id.btn_member_card);

        lvRecharge = findViewById(R.id.lv_recharge_records);
        lvMember = findViewById(R.id.lv_member_records);

        btnMoreRecharge = findViewById(R.id.btn_more_recharge);
        btnMoreMember = findViewById(R.id.btn_more_member);

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        currentUser = sp.getString("users", "");

        btnRecharge.setOnClickListener(v -> showRechargeDialog());
        btnMemberCard.setOnClickListener(v -> startActivity(new Intent(this, MemberCardActivity.class)));

        btnMoreRecharge.setOnClickListener(v -> startActivity(new Intent(this, RechargeRecordActivity.class)));
        btnMoreMember.setOnClickListener(v -> startActivity(new Intent(this, MemberPurchaseRecordActivity.class)));

        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        if (currentUser == null || currentUser.trim().isEmpty()) return;

        double balance = dbHelper.getWalletBalance(currentUser);
        tvBalance.setText(String.format(Locale.CHINA, "%.2f", balance));

        String expire = dbHelper.getMemberExpire(currentUser);
        boolean active = dbHelper.isMemberActive(currentUser);

        if (active) {
            tvMemberStatus.setText("会员用户");
            tvMemberDays.setText("到期日：" + expire + "（会员享 9 折 + 优先借用）");
        } else {
            tvMemberStatus.setText("普通用户");
            tvMemberDays.setText("未开通会员（开通可享 9 折 + 优先借用）");
        }

        // 记录展示
        List<String> recharges = dbHelper.getRechargeRecordLines(currentUser, 5);
        lvRecharge.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recharges));

        List<String> members = dbHelper.getMemberPurchaseRecordLines(currentUser, 5);
        lvMember.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, members));
    }

    private void showRechargeDialog() {
        PaymentDialogUtil.showPayDialog(
                this,
                "充值（第三方支付）",
                50.0,
                true,
                false, // 充值不允许余额支付
                (channel, amount) -> {
                    double old = dbHelper.getWalletBalance(currentUser);
                    double now = old + amount;
                    dbHelper.updateWalletBalance(currentUser, now);

                    String t = TIME_FMT.format(new Date());
                    dbHelper.insertRechargeRecord(currentUser, amount, channel, t);

                    Toast.makeText(this, "充值成功：" + amount + " 元", Toast.LENGTH_SHORT).show();
                    refreshUI();
                }
        );
    }
}
