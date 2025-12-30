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
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

/**
 * 管理员删除读者
 */
public class admin_delete_users extends AppCompatActivity {
    private ListView listView;
    private ImageButton back_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_users);
        init();//初始化界面

    }

    private void init() {
        back_bt = (ImageButton) findViewById(R.id.editreader_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_delete_users.this, admin_manager_user.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        listView = (ListView) findViewById(R.id.edit_reader_list);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.query();
        String from[] = {"user", "password", "name", "sex", "birthday", "phone"};
        int to[] = {R.id.read_user, R.id.read_pwd, R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_phone};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.select_user_item, cursor, from, to);
        listView.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //listview的单击事件监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                builder.setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        help.del((int) position + 1);
                        help.sortuserid();
                        //删除后重新显示
                        Cursor cursor = help.query();
                        String from[] = {"user", "password", "name", "sex", "birthday", "phone"};
                        int to[] = {R.id.read_user, R.id.read_pwd, R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_phone};
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.select_user_item, cursor, from, to);
                        listView.setAdapter(adapter);

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        help.close();
    }
}
