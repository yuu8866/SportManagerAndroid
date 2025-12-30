package com.example.administrator.sportmanager.admin.houtai_admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

/**
 * 管理员查询运动器械信息
 */

public class admin_select_sports extends AppCompatActivity {
    private ImageButton back_bt;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_select_sports);
        init();//界面初始化

    }

    private void init() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        back_bt = (ImageButton) findViewById(R.id.select_sport_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_select_sports.this, com.example.administrator.sportmanager.admin.houtai_admin.admin_select_message.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        listView = (ListView) findViewById(R.id.select_sport_list);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.querysports();
        String from[] = {"name", "type", "user","owner","rank","img", "price"};
        int to[] = {R.id.admin_Sport_Name, R.id.admin_Sport_Type, R.id.admin_sport_author, R.id.admin_sport_publish, R.id.admin_Sport_Rank, R.id.admin_sport_info_img, R.id.admin_sport_pice};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.admin_sport_item, cursor, from, to);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.admin_sport_info_img) {
                    ImageView imageView = (ImageView) view;
                    //imageView.setImageURI(Uri.parse(cursor.getString(columnIndex)));
                    imageView.setImageBitmap(byteToBitmap(cursor.getBlob(columnIndex)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final long temp = l;
                builder.setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除
                        help.delsports((int) temp);
                        //sort _id
                        help.sortsportsid();
                        //删除后重新显示
                        Cursor cursor = help.querysports();
                        String from[] = {"name", "type", "user","owner","rank","img"};
                        int to[] = {R.id.admin_Sport_Name, R.id.admin_Sport_Type, R.id.admin_sport_author, R.id.admin_sport_publish, R.id.admin_Sport_Rank, R.id.admin_sport_info_img};
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(admin_select_sports.this, R.layout.admin_sport_item, cursor, from, to);
                        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                                if (view.getId() == R.id.admin_sport_info_img) {
                                    ImageView imageView = (ImageView) view;
                                    //imageView.setImageURI(Uri.parse(cursor.getString(columnIndex)));
                                    imageView.setImageBitmap(byteToBitmap(cursor.getBlob(columnIndex)));
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        });
                        listView.setAdapter(adapter);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });


        //listview的单击事件,修改器械信息
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //传值到修改界面
                int i = position;
                Intent intent = new Intent(admin_select_sports.this, admin_update_sport.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", i);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });


    }

}
