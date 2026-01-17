package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.example.administrator.sportmanager.admin.utils.PaymentDialogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 办理会员卡页面：月卡/季卡/年卡
 * - 点击支付按钮 -> 第三方支付选择弹窗
 * - 支付成功后写入 member_expire
 */
public class MemberCardActivity extends AppCompatActivity {

    private TextView tvCurrentInfo;
    private Button btnMonth;
    private Button btnQuarter;
    private Button btnYear;

    private databaseHelp dbHelper;
    private String currentUser;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private static final SimpleDateFormat TIME_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static final double PRICE_MONTH = 19.9;
    private static final double PRICE_QUARTER = 49.9;
    private static final double PRICE_YEAR = 159.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card);

        dbHelper = new databaseHelp(this);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        currentUser = sp.getString("users", "");

        tvCurrentInfo = findViewById(R.id.tv_current_member_info);
        btnMonth = findViewById(R.id.btn_pay_month);
        btnQuarter = findViewById(R.id.btn_pay_quarter);
        btnYear = findViewById(R.id.btn_pay_year);

        btnMonth.setOnClickListener(v -> payForMember("月卡", PRICE_MONTH, 30));
        btnQuarter.setOnClickListener(v -> payForMember("季卡", PRICE_QUARTER, 90));
        btnYear.setOnClickListener(v -> payForMember("年卡", PRICE_YEAR, 365));

        refreshCurrentInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCurrentInfo();
    }

    private void refreshCurrentInfo() {
        String expire = dbHelper.getMemberExpire(currentUser);
        if (isMemberActive(expire)) {
            long daysLeft = getDaysLeft(expire);
            tvCurrentInfo.setText("当前状态：会员用户（到期日：" + expire + "，剩余 " + daysLeft + " 天）");
        } else {
            if (expire != null && !expire.trim().isEmpty()) {
                tvCurrentInfo.setText("当前状态：普通用户（上次到期日：" + expire + "）");
            } else {
                tvCurrentInfo.setText("当前状态：普通用户");
            }
        }
    }

    private void payForMember(String cardName, double price, int durationDays) {
        PaymentDialogUtil.showPayDialog(
                this,
                "开通" + cardName + "（¥" + price + "）",
                price,
                false,
                true, //  允许余额支付
                (channel, amount) -> {

                    //  如果余额支付，先判断余额够不够
                    if ("余额支付".equals(channel)) {
                        double bal = dbHelper.getWalletBalance(currentUser);
                        if (bal < price) {
                            Toast.makeText(this, "余额不足，请先充值", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dbHelper.updateWalletBalance(currentUser, bal - price);
                    }

                    // 计算到期日：有效会员续期，否则从今天开始
                    String oldExpire = dbHelper.getMemberExpire(currentUser);
                    Date base;
                    if (dbHelper.isMemberActive(currentUser)) {
                        base = parseDateOrToday(oldExpire);
                    } else {
                        base = getTodayStart();
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(base);
                    cal.add(Calendar.DATE, durationDays);
                    String newExpire = DATE_FMT.format(cal.getTime());

                    dbHelper.updateMemberExpire(currentUser, newExpire);

                    //  写入购买记录
                    String now = TIME_FMT.format(new Date());
                    String startDate = DATE_FMT.format(getTodayStart());
                    dbHelper.insertMemberPurchaseRecord(currentUser, cardName, price, channel, startDate, newExpire, now);

                    Toast.makeText(this, "已开通" + cardName + "，到期：" + newExpire, Toast.LENGTH_SHORT).show();
                    refreshCurrentInfo();
                }
        );
    }


    private boolean isMemberActive(String expireDate) {
        if (expireDate == null || expireDate.trim().isEmpty()) return false;
        try {
            Date expire = DATE_FMT.parse(expireDate);
            Date todayStart = getTodayStart();
            return expire != null && !expire.before(todayStart);
        } catch (ParseException e) {
            return false;
        }
    }

    private long getDaysLeft(String expireDate) {
        try {
            Date expire = DATE_FMT.parse(expireDate);
            Date todayStart = getTodayStart();
            if (expire == null) return 0;
            long diff = expire.getTime() - todayStart.getTime();
            long days = diff / (24L * 60 * 60 * 1000);
            return Math.max(0, days + 1);
        } catch (Exception e) {
            return 0;
        }
    }

    private Date parseDateOrToday(String dateStr) {
        try {
            Date d = DATE_FMT.parse(dateStr);
            return d == null ? getTodayStart() : d;
        } catch (Exception e) {
            return getTodayStart();
        }
    }

    private Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
