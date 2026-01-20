package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.widget.ArrayAdapter;

public class UserProfileAdvancedActivity extends AppCompatActivity {
    private androidx.activity.result.ActivityResultLauncher<Intent> editProfileLauncher;

    private databaseHelp help;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_advanced);
        editProfileLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //  修改成功后刷新本页数据
                        recreate();
                    }
                }
        );

        help = new databaseHelp(this);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        username = sp.getString("users", "");

        MaterialToolbar topBar = findViewById(R.id.profileTopBar);
        topBar.setNavigationOnClickListener(v -> finish());

        TextView tvUser = findViewById(R.id.tv_profile_username);
        TextView tvMember = findViewById(R.id.tv_profile_member);
        TextView tvLeft = findViewById(R.id.tv_profile_member_left);
        TextView tvVipBadge = findViewById(R.id.tv_vip_badge);

        TextView tvBalance = findViewById(R.id.tv_profile_balance);
        TextView tvBorrowNow = findViewById(R.id.tv_profile_borrow_count);
        TextView tvCollect = findViewById(R.id.tv_profile_collect_count);
        TextView tvSpend = findViewById(R.id.tv_profile_total_spend);

        tvUser.setText("用户：" + username);

        // 统计
        tvBalance.setText(String.format(Locale.CHINA, "%.2f", help.getWalletBalance(username)));
        tvBorrowNow.setText(String.valueOf(help.queryActiveBorrowCount(username)));
        tvCollect.setText(String.valueOf(help.queryCollectCount(username)));
        tvSpend.setText(String.format(Locale.CHINA, "%.2f", help.queryTotalSpend(username)));

        // 会员状态
        boolean isMember = help.isMemberActive(username);
        String expire = help.getMemberExpire(username);

        if (isMember) {
            tvVipBadge.setVisibility(TextView.VISIBLE);
            tvMember.setText("会员用户（到期：" + expire + "）");
            tvLeft.setText("会员剩余：" + calcLeftDays(expire) + " 天  |  权益：9折+优先借用");
        } else {
            tvVipBadge.setVisibility(TextView.GONE);
            tvMember.setText("普通用户");
            tvLeft.setText("未开通会员（开通享 9 折 + 优先借用）");
        }

        // 快捷按钮
        MaterialButton btnWallet = findViewById(R.id.btn_profile_wallet);
        MaterialButton btnBorrow = findViewById(R.id.btn_profile_myborrow);
        MaterialButton btnMember = findViewById(R.id.btn_profile_member);

        btnWallet.setOnClickListener(v -> startActivity(new Intent(this, WalletActivity.class)));
        btnBorrow.setOnClickListener(v -> startActivity(new Intent(this, person_borrow.class)));
        btnMember.setOnClickListener(v -> startActivity(new Intent(this, MemberCardActivity.class)));

        // 充值记录
        ListView lvRecharge = findViewById(R.id.lv_recharge_record);
        ArrayList<String> rechargeLines = new ArrayList<>(help.getRechargeRecordLines(username, 5));
        if (rechargeLines.isEmpty()) rechargeLines.add("暂无充值记录");
        lvRecharge.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rechargeLines));

        // 会员购买记录
        ListView lvMember = findViewById(R.id.lv_member_record);
        ArrayList<String> memberLines = new ArrayList<>(help.getMemberPurchaseRecordLines(username, 5));
        if (memberLines.isEmpty()) memberLines.add("暂无会员购买记录");
        lvMember.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberLines));

        // 编辑按钮：跳转到修改个人信息页面
        View btnEditProfile = findViewById(R.id.btn_edit_profile);


        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileAdvancedActivity.this, UserUpdateInfo.class);
            editProfileLauncher.launch(intent);
        });

    }

    private int calcLeftDays(String expireDate) {
        try {
            if (expireDate == null || expireDate.trim().isEmpty()) return 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date end = sdf.parse(expireDate);
            long diff = end.getTime() - System.currentTimeMillis();
            int days = (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
            return Math.max(days, 0);
        } catch (Exception e) {
            return 0;
        }
    }
}
