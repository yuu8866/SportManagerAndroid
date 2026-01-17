package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class HomeFragment extends Fragment {

    private TextView tvHello, tvSubtitle;
    private TextView tvBalance, tvMemberStatus, tvMemberExpire;
    private TextView tvBorrowCount, tvDueSoon, tvRecommendTitle;

    private ListView lvBorrowNow, lvRecommend;

    private View btnMyBorrow, btnWallet, btnMember;

    private databaseHelp help;
    private String username;

    private final SimpleDateFormat PAY_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        help = new databaseHelp(requireContext());
        SharedPreferences sp = requireContext().getSharedPreferences("data", 0);
        username = sp.getString("users", "");

        tvHello = root.findViewById(R.id.tv_hello);
        tvSubtitle = root.findViewById(R.id.tv_subtitle);

        tvBalance = root.findViewById(R.id.tv_balance);
        tvMemberStatus = root.findViewById(R.id.tv_member_status);
        tvMemberExpire = root.findViewById(R.id.tv_member_expire);
        tvBorrowCount = root.findViewById(R.id.tv_borrow_count);
        tvDueSoon = root.findViewById(R.id.tv_due_soon);

        tvRecommendTitle = root.findViewById(R.id.tv_recommend_title);

        lvBorrowNow = root.findViewById(R.id.lv_borrow_now);
        lvRecommend = root.findViewById(R.id.lv_recommend);

        btnMyBorrow = root.findViewById(R.id.btn_my_borrow);
        btnWallet = root.findViewById(R.id.btn_wallet);
        btnMember = root.findViewById(R.id.btn_member);

        // 顶部欢迎文案
        tvHello.setText("Hi，" + username + " 👋");
        tvSubtitle.setText("今日运动打卡，保持热爱～");

        // 快捷入口
        btnMyBorrow.setOnClickListener(v -> startActivity(new Intent(requireContext(), person_borrow.class)));
        btnWallet.setOnClickListener(v -> startActivity(new Intent(requireContext(), WalletActivity.class)));
        btnMember.setOnClickListener(v -> startActivity(new Intent(requireContext(), MemberCardActivity.class)));

        refreshTopCards();
        loadBorrowNow();
        loadRecommend();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 返回主页时刷新一下数据更真实
        refreshTopCards();
        loadBorrowNow();
        loadRecommend();
    }

    private void refreshTopCards() {
        // 余额
        double balance = help.getWalletBalance(username);
        tvBalance.setText(String.format(Locale.CHINA, "%.2f", balance));

        // 会员状态
        boolean isMember = help.isMemberActive(username);
        String expire = help.getMemberExpire(username);

        if (isMember) {
            tvMemberStatus.setText("会员用户");
            tvMemberExpire.setText("到期：" + expire + "（9折+优先借用）");
        } else {
            tvMemberStatus.setText("普通用户");
            tvMemberExpire.setText("开通会员享 9 折 + 优先借用");
        }

        // 当前借用数量
        int count = help.queryActiveBorrowCount(username);
        tvBorrowCount.setText(String.valueOf(count));

        // 最快到期
        int leftMin = calcMinLeftDays();
        if (count == 0) {
            tvDueSoon.setText("暂无借用");
        } else {
            tvDueSoon.setText("剩余 " + leftMin + " 天");
        }

        // 点击卡片：查看借用详情
        tvBorrowCount.setOnClickListener(v -> startActivity(new Intent(requireContext(), person_borrow.class)));
        tvDueSoon.setOnClickListener(v -> startActivity(new Intent(requireContext(), person_borrow.class)));
    }

    /** 计算所有已支付订单中“最小剩余天数” */
    private int calcMinLeftDays() {
        Cursor c = help.queryActiveBorrowForHome(username);
        int min = 999999;
        if (c != null) {
            while (c.moveToNext()) {
                String payTime = c.getString(c.getColumnIndexOrThrow("pay_time"));
                int days = c.getInt(c.getColumnIndexOrThrow("days"));
                int left = calcLeftDays(payTime, days);
                if (left < min) min = left;
            }
            c.close();
        }
        if (min == 999999) min = 0;
        if (min < 0) min = 0;
        return min;
    }

    private int calcLeftDays(String payTime, int days) {
        if (payTime == null) return days;
        try {
            Date start = PAY_FMT.parse(payTime);
            long endMs = start.getTime() + (long) days * 24 * 60 * 60 * 1000;
            long now = System.currentTimeMillis();
            long diff = endMs - now;
            return (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
        } catch (ParseException e) {
            return days;
        }
    }

    /** 当前借用提醒（最多5条） */
    private void loadBorrowNow() {
        Cursor cursor = help.queryActiveBorrowForHome(username);

        String[] from = {"sportname", "pay_time", "days"};
        int[] to = {R.id.item_borrow_name, R.id.item_borrow_time, R.id.item_borrow_days};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.item_borrow_now,
                cursor,
                from,
                to,
                0
        );

        adapter.setViewBinder((view, c, columnIndex) -> {
            if (view.getId() == R.id.item_borrow_days) {
                TextView tv = (TextView) view;
                String payTime = c.getString(c.getColumnIndexOrThrow("pay_time"));
                int days = c.getInt(c.getColumnIndexOrThrow("days"));
                int left = calcLeftDays(payTime, days);
                if (left < 0) left = 0;
                tv.setText("剩余 " + left + " 天");
                return true;
            }

            if (view.getId() == R.id.item_borrow_time) {
                TextView tv = (TextView) view;
                String payTime = c.getString(c.getColumnIndexOrThrow("pay_time"));
                tv.setText("开始时间：" + (payTime == null ? "未知" : payTime));
                return true;
            }

            return false;
        });

        lvBorrowNow.setAdapter(adapter);

        // 点击借用提醒 -> 进入“我的借用”
        lvBorrowNow.setOnItemClickListener((parent, view, position, id) -> {
            startActivity(new Intent(requireContext(), person_borrow.class));
        });
    }

    /** 个性化推荐：先按偏好类型推荐，否则热门推荐 */
    private void loadRecommend() {
        String favoriteType = help.queryFavoriteSportType(username);

        if (favoriteType != null && !favoriteType.trim().isEmpty()) {
            tvRecommendTitle.setText("猜你喜欢 · " + favoriteType);
        } else {
            tvRecommendTitle.setText("热门推荐");
        }

        Cursor cursor = help.queryRecommendSportsByType(favoriteType);

        boolean isMember = help.isMemberActive(username);

        String[] from = {"name", "type", "price", "img"};
        int[] to = {R.id.tv_sport_name, R.id.tv_sport_type, R.id.tv_price, R.id.img_sport};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.item_recommend_sport,
                cursor,
                from,
                to,
                0
        );

        adapter.setViewBinder((view, c, columnIndex) -> {

            if (view.getId() == R.id.img_sport) {
                ImageView img = (ImageView) view;
                img.setImageBitmap(byteToBitmap(c.getBlob(columnIndex)));
                return true;
            }

            // 原价展示
            if (view.getId() == R.id.tv_price) {
                TextView tv = (TextView) view;
                double price = 0;
                try {
                    price = Double.parseDouble(c.getString(columnIndex));
                } catch (Exception ignored) {}
                tv.setText("原价：¥" + String.format(Locale.CHINA, "%.2f", price) + "/天");
                return true;
            }

            // 会员价展示
            if (view.getId() == R.id.tv_member_price) {
                TextView tv = (TextView) view;
                double price = 0;
                try {
                    price = Double.parseDouble(c.getString(c.getColumnIndexOrThrow("price")));
                } catch (Exception ignored) {}

                if (isMember) {
                    double memberPrice = price * 0.9;
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("会员价：¥" + String.format(Locale.CHINA, "%.2f", memberPrice) + "/天");
                } else {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("开通会员享 9 折");
                }
                return true;
            }

            return false;
        });

        lvRecommend.setAdapter(adapter);

        // 点击推荐器材 -> 进入 borrowActivity
        lvRecommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // SimpleCursorAdapter 的 id = 当前行的 _id
                // borrowActivity 里会 bundle.getInt("id") + 1，所以这里传（_id - 1）
                Intent intent = new Intent(requireContext(), borrowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", (int) id - 1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
