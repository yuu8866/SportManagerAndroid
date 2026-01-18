package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.MainActivity;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class UserNavActivity extends AppCompatActivity {

    // ✅ 让其他页面能指定回到哪个 Tab
    public static final String EXTRA_OPEN_TAB = "open_tab";
    public static final String TAB_RENT = "rent";
    public static final String TAB_HOME = "home";
    public static final String TAB_COMMUNITY = "community";

    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private databaseHelp help;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_nav);

        help = new databaseHelp(this);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        username = sp.getString("users", "");

        // 顶部栏
        toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("SportManager");

        // 抽屉（左滑菜单）
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);

        // 左上角按钮
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // 抽屉头部：用户名/会员状态
        TextView tvUser = navView.getHeaderView(0).findViewById(R.id.tv_nav_username);
        TextView tvMember = navView.getHeaderView(0).findViewById(R.id.tv_nav_member);
        tvUser.setText("用户：" + username);

        boolean isMember = help.isMemberActive(username);
        if (isMember) {
            tvMember.setText("会员用户（到期：" + help.getMemberExpire(username) + "）");
        } else {
            tvMember.setText("普通用户");
        }

        // 抽屉菜单点击
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.drawer_wallet) {
                startActivity(new Intent(UserNavActivity.this, WalletActivity.class));
            } else if (id == R.id.drawer_collect) {
                startActivity(new Intent(UserNavActivity.this, collectActivity.class));
            } else if (id == R.id.drawer_myborrow) {
                startActivity(new Intent(UserNavActivity.this, person_borrow.class));
            } else if (id == R.id.drawer_member) {
                startActivity(new Intent(UserNavActivity.this, MemberCardActivity.class));
            } else if (id == R.id.drawer_profile) {
                startActivity(new Intent(UserNavActivity.this, UserProfileAdvancedActivity.class));
            } else if (id == R.id.drawer_exit) {
                startActivity(new Intent(UserNavActivity.this, MainActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 底部导航
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_rent) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new RentFragment())
                        .commit();
                toolbar.setTitle("租赁");
                return true;
            }

            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
                toolbar.setTitle("主页");
                return true;
            }

            if (item.getItemId() == R.id.nav_community) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new CommunityFragment())
                        .commit();
                toolbar.setTitle("社区");
                return true;
            }

            return false;
        });

        // ✅ 默认显示主页 + 支持外部指定Tab
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            handleOpenTab(getIntent());
        }
    }

    // ✅ 重点：处理外部传入 tab
    private void handleOpenTab(Intent intent) {
        if (intent == null || bottomNav == null) return;
        String tab = intent.getStringExtra(EXTRA_OPEN_TAB);
        if (tab == null) return;

        if (TAB_RENT.equals(tab)) {
            bottomNav.setSelectedItemId(R.id.nav_rent);
        } else if (TAB_COMMUNITY.equals(tab)) {
            bottomNav.setSelectedItemId(R.id.nav_community);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    // ✅ CLEAR_TOP 回来时会走这里，必须重新切tab，否则不生效
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleOpenTab(intent);
    }

    @Override
    public void onBackPressed() {
        // 抽屉打开时先关闭抽屉
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
