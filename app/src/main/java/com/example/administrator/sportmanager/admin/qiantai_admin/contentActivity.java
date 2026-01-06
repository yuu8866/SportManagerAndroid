package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;

import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.MainActivity;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.example.administrator.sportmanager.admin.qiantai_admin.SearchActivity;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.bitmapToByte;
import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

//用户登录成功后的首页界面

public class contentActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private ListView listView;
    private long mExitTime;
    private TextView personName;
    private databaseHelp help;
    private static boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        help = new databaseHelp(getApplicationContext());

        //更新图片
        if (flag){
            initImg();
            flag=false;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //我们初始化DrawerLayout和侧滑菜单列表
        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }

        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                int i = position + 1;
                Intent intent = new Intent(contentActivity.this, com.example.administrator.sportmanager.admin.qiantai_admin.borrowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //侧滑菜单栏的选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shoucang:
                        //跳转至个人收藏页面
                        Intent intent = new Intent(contentActivity.this, com.example.administrator.sportmanager.admin.qiantai_admin.collectActivity.class);
                        startActivity(intent);//启动一个新的活动
                        break;
                    case R.id.jieyue:
                        //跳转到个人租赁的页面
                        Intent intent2 = new Intent(contentActivity.this, com.example.administrator.sportmanager.admin.qiantai_admin.person_borrow.class);
                        startActivity(intent2);
                        break;
                    case R.id.updateInfo:
                        //跳转到修改个人信息页面
                        Intent intent3 = new Intent(contentActivity.this, UserUpdateInfo.class);
                        startActivity(intent3);
                        break;

                    case R.id.exit:
                        Intent intent5 = new Intent(contentActivity.this, MainActivity.class);
                        startActivity(intent5);
                        ActivityCollector.finishAll();
                        break;

                    default:

                }
                drawerLayout.closeDrawers();//将滑动菜单关闭
                return true;
            }
        });

        listView = findViewById(R.id.list_view);
        Cursor cursor = help.querysports();
        /*String from[] = {"img","name", "user"};
        int to[] = {R.id.Sport_image,R.id.Sport_Name, R.id.sport_author};*/
        String from[] = {"name", "type", "user", "owner", "rank", "img","price"};
        int to[] = {R.id.user_Sport_Name, R.id.user_Sport_Type, R.id.user_sport_author, R.id.user_sport_publish, R.id.user_Sport_Rank, R.id.user_sport_info_img,R.id.user_sport_pice};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.sport_item, cursor, from, to);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.user_sport_info_img) {
                    ImageView iconImageView = (ImageView) view;
                    //iconImageView.setImageURI(Uri.parse(cursor.getString(columnIndex)));
                    iconImageView.setImageBitmap(byteToBitmap(cursor.getBlob(columnIndex)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        listView.setAdapter(adapter);

        View headView = navigationView.getHeaderView(0);
        personName = headView.findViewById(R.id.person_name);
        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        String name = perf.getString("users", "");//获得当前用户名称
        personName.setText(name);
    }

    private void initImg() {
        Bitmap bm1  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport0 ,null);
        Bitmap bm2  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport1 ,null);
        Bitmap bm3  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport2 ,null);
        Bitmap bm4  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport3 ,null);
        Bitmap bm5  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport4 ,null);
        Bitmap bm6  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport5 ,null);
        Bitmap bm7  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport6 ,null);
        Bitmap bm8  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport7 ,null);
        Bitmap bm9  = BitmapFactory.decodeResource(getResources(),R.mipmap.sport8 ,null);
        Bitmap bm10 = BitmapFactory.decodeResource(getResources(),R.mipmap.sport9 ,null);
        Bitmap bm11 = BitmapFactory.decodeResource(getResources(),R.mipmap.sport10,null);
        Bitmap bm12 = BitmapFactory.decodeResource(getResources(),R.mipmap.sport11,null);
        Bitmap bm13 = BitmapFactory.decodeResource(getResources(),R.mipmap.sport12,null);
        ContentValues values = new ContentValues();
        values.put("img",bitmapToByte(bm1 ));
        help.updateImg(values,1);
        values.put("img",bitmapToByte(bm2 ));
        help.updateImg(values,2);
        values.put("img",bitmapToByte(bm3 ));
        help.updateImg(values,3);
        values.put("img",bitmapToByte(bm4 ));
        help.updateImg(values,4);
        values.put("img",bitmapToByte(bm5 ));
        help.updateImg(values,5);
        values.put("img",bitmapToByte(bm6 ));
        help.updateImg(values,6);
        values.put("img",bitmapToByte(bm7 ));
        help.updateImg(values,7);
        values.put("img",bitmapToByte(bm8 ));
        help.updateImg(values,8);
        values.put("img",bitmapToByte(bm9 ));
        help.updateImg(values,9);
        values.put("img",bitmapToByte(bm10));
        help.updateImg(values,10);
        values.put("img",bitmapToByte(bm11));
        help.updateImg(values,11);
        values.put("img",bitmapToByte(bm12));
        help.updateImg(values,12);
        values.put("img",bitmapToByte(bm13));
        help.updateImg(values,13);
    }

    //toolbar的菜单栏的选项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                Intent s = new Intent(contentActivity.this, SearchActivity.class);
                startActivity(s);
                break;

            default:
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(contentActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

}
