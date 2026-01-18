package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvHello, tvSubtitle;
    private TextView tvBalance, tvMemberStatus, tvMemberExpire;
    private TextView tvBorrowCount, tvDueSoon, tvRecommendTitle;

    private TextView tvBorrowEmpty, tvRecommendEmpty;
    private TextView tvBorrowMore, tvRecommendMore;

    private ListView lvBorrowNow;
    private RecyclerView rvRecommend;

    private View btnMyBorrow, btnWallet, btnMember;
    private MaterialButton btnGoRent, btnOpenMember;

    private databaseHelp help;
    private String username;

    private HomeRecommendAdapter recommendAdapter;

    private final SimpleDateFormat PAY_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        tvBorrowEmpty = root.findViewById(R.id.tv_borrow_empty);
        tvRecommendEmpty = root.findViewById(R.id.tv_recommend_empty);

        tvBorrowMore = root.findViewById(R.id.tv_borrow_more);
        tvRecommendMore = root.findViewById(R.id.tv_recommend_more);

        tvRecommendTitle = root.findViewById(R.id.tv_recommend_title);

        lvBorrowNow = root.findViewById(R.id.lv_borrow_now);
        rvRecommend = root.findViewById(R.id.rv_recommend);

        btnMyBorrow = root.findViewById(R.id.btn_my_borrow);
        btnWallet = root.findViewById(R.id.btn_wallet);
        btnMember = root.findViewById(R.id.btn_member);

        btnGoRent = root.findViewById(R.id.btn_go_rent);
        btnOpenMember = root.findViewById(R.id.btn_open_member);

        //  “信用/会员系统”入口补全：余额/会员状态点击可进入对应页面
        tvBalance.setOnClickListener(v -> startActivity(new Intent(requireContext(), WalletActivity.class)));
        tvMemberStatus.setOnClickListener(v -> startActivity(new Intent(requireContext(), MemberCardActivity.class)));
        tvMemberExpire.setOnClickListener(v -> startActivity(new Intent(requireContext(), MemberCardActivity.class)));


        // 顶部欢迎文案（更像真实App：一句主标题 + 一句副标题）
        tvHello.setText("Hi，" + username + " 👋");
        tvSubtitle.setText("今日运动打卡，保持热爱～");

        // Banner 按钮：去租赁 / 开通会员
        btnGoRent.setOnClickListener(v -> switchToRentTab());
        btnOpenMember.setOnClickListener(v -> startActivity(new Intent(requireContext(), MemberCardActivity.class)));

        // 快捷入口
        btnMyBorrow.setOnClickListener(v -> startActivity(new Intent(requireContext(), person_borrow.class)));
        btnWallet.setOnClickListener(v -> startActivity(new Intent(requireContext(), WalletActivity.class)));
        btnMember.setOnClickListener(v -> startActivity(new Intent(requireContext(), MemberCardActivity.class)));

        // “查看全部/去租赁”小入口（更像真实App）
        tvBorrowMore.setOnClickListener(v -> startActivity(new Intent(requireContext(), person_borrow.class)));
        tvRecommendMore.setOnClickListener(v -> switchToRentTab());

        // 推荐列表：横向 RecyclerView
        rvRecommend.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendAdapter = new HomeRecommendAdapter(requireContext(), new ArrayList<>(), item -> {
            // 点击推荐器材 -> 进入 borrowActivity（保持你项目的传参规则：id = _id - 1）
            Intent intent = new Intent(requireContext(), borrowActivity.class);
            Bundle b = new Bundle();
            b.putInt("id", (int) item.dbId - 1);
            intent.putExtras(b);
            startActivity(intent);
        });
        rvRecommend.setAdapter(recommendAdapter);

        refreshAll();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 返回主页自动刷新，更真实
        refreshAll();
    }

    private void refreshAll() {
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

        // 当前借用数量（已支付）
        int count = help.queryActiveBorrowCount(username);
        tvBorrowCount.setText(String.valueOf(count));

        // 最快到期
        int leftMin = calcMinLeftDays();
        if (count == 0) {
            tvDueSoon.setText("暂无借用");
        } else {
            tvDueSoon.setText("剩余 " + leftMin + " 天");
        }

        // 点击卡片：进入我的借用
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

        if (cursor == null || cursor.getCount() == 0) {
            tvBorrowEmpty.setVisibility(View.VISIBLE);
            lvBorrowNow.setVisibility(View.GONE);
            if (cursor != null) cursor.close();
            return;
        }

        tvBorrowEmpty.setVisibility(View.GONE);
        lvBorrowNow.setVisibility(View.VISIBLE);

        String[] from = {"sportname", "pay_time", "days"};
        int[] to = {R.id.item_borrow_name, R.id.item_borrow_time, R.id.item_borrow_days};

        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(
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

        // 点击提醒 -> 进入我的借用
        lvBorrowNow.setOnItemClickListener((parent, view, position, id) ->
                startActivity(new Intent(requireContext(), person_borrow.class))
        );
    }

    /** 个性化推荐：按偏好类型推荐，否则热门推荐（横向卡片更像真实App） */
    private void loadRecommend() {
        String favoriteType = help.queryFavoriteSportType(username);

        if (favoriteType != null && !favoriteType.trim().isEmpty()) {
            tvRecommendTitle.setText("猜你喜欢 · " + favoriteType);
        } else {
            tvRecommendTitle.setText("热门推荐");
        }

        Cursor cursor = help.queryRecommendSportsByType(favoriteType);

        ArrayList<HomeRecommendAdapter.SportItem> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long dbId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                int sportId = cursor.getInt(cursor.getColumnIndexOrThrow("sportid"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String priceStr = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                byte[] img = cursor.getBlob(cursor.getColumnIndexOrThrow("img"));

                double price = 0;
                try { price = Double.parseDouble(priceStr); } catch (Exception ignored) {}

                list.add(new HomeRecommendAdapter.SportItem(dbId, sportId, name, type, price, img));
            }
            cursor.close();
        }

        if (list.isEmpty()) {
            tvRecommendEmpty.setVisibility(View.VISIBLE);
            rvRecommend.setVisibility(View.GONE);
        } else {
            tvRecommendEmpty.setVisibility(View.GONE);
            rvRecommend.setVisibility(View.VISIBLE);
            boolean isMember = help.isMemberActive(username);
            recommendAdapter.setMember(isMember);
            recommendAdapter.setData(list);
        }
    }

    /** ✅ 切换到底部导航“租赁”tab，避免跳旧界面 */
    private void switchToRentTab() {
        if (getActivity() == null) return;
        BottomNavigationView nav = getActivity().findViewById(R.id.bottomNav);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_rent);
        }
    }
}
