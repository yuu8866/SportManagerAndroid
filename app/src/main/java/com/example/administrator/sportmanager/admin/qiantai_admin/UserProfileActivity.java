package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {

    private databaseHelp help;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        help = new databaseHelp(this);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        username = sp.getString("users", "");

        MaterialToolbar topBar = findViewById(R.id.profileTopBar);
        topBar.setNavigationOnClickListener(v -> finish());

        TextView tvUser = findViewById(R.id.tv_profile_username);
        TextView tvMember = findViewById(R.id.tv_profile_member);
        TextView tvLeft = findViewById(R.id.tv_profile_member_left);

        TextView tvBalance = findViewById(R.id.tv_profile_balance);
        TextView tvBorrowCount = findViewById(R.id.tv_profile_borrow_count);

        tvUser.setText("用户：" + username);

        // 余额 + 借用数量
        tvBalance.setText(String.format(Locale.CHINA, "%.2f", help.getWalletBalance(username)));
        tvBorrowCount.setText(String.valueOf(help.queryActiveBorrowCount(username)));

        // 会员状态
        boolean isMember = help.isMemberActive(username);
        if (isMember) {
            String expire = help.getMemberExpire(username);
            tvMember.setText("会员用户（到期：" + expire + "）");
            tvLeft.setText("会员剩余：" + calcLeftDays(expire) + " 天");
        } else {
            tvMember.setText("普通用户");
            tvLeft.setText("未开通会员");
        }

        // 按钮跳转
        MaterialButton btnWallet = findViewById(R.id.btn_profile_wallet);
        MaterialButton btnBorrow = findViewById(R.id.btn_profile_myborrow);
        MaterialButton btnMember = findViewById(R.id.btn_profile_member);

        btnWallet.setOnClickListener(v -> startActivity(new Intent(this, WalletActivity.class)));
        btnBorrow.setOnClickListener(v -> startActivity(new Intent(this, person_borrow.class)));
        btnMember.setOnClickListener(v -> startActivity(new Intent(this, MemberCardActivity.class)));
    }

    private int calcLeftDays(String expireDate) {
        try {
            // 你的会员到期日一般是 yyyy-MM-dd 格式
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
