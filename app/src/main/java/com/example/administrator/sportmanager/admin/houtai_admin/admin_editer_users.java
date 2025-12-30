package com.example.administrator.sportmanager.admin.houtai_admin;

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
 * 编辑读者页面
 */

public class admin_editer_users extends AppCompatActivity {
    private ListView listView;
    private ImageButton back_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_editer_users);
        init();//初始化界面

    }

    private void init() {
        back_bt = (ImageButton) findViewById(R.id.deletereader_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_editer_users.this, admin_manager_user.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        listView = (ListView) findViewById(R.id.delete_reader_list);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        Cursor cursor = help.query();
        String from[] = {"user", "password", "name", "sex", "birthday", "phone"};
        int to[] = {R.id.read_user, R.id.read_pwd, R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_phone};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.select_user_item, cursor, from, to);
        listView.setAdapter(adapter);
        //listview的单击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //传值到修改界面
                int i = position + 1;
                Intent intent = new Intent(admin_editer_users.this, admin_update_user.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", i);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
