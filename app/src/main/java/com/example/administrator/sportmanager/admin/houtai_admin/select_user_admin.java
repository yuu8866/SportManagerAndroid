package com.example.administrator.sportmanager.admin.houtai_admin;

import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

public class select_user_admin extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_admin);
        listView=(ListView)findViewById(R.id.sel_reader_list);
        databaseHelp help=new databaseHelp(getApplicationContext());

        // v4：带会员信息查询（member_status / member_expire）
        Cursor cursor=help.queryUsersWithMemberInfo();

        String from[]={"user","password","name", "sex", "birthday", "phone", "member_status", "member_expire"};
        int to[]={R.id.read_user,R.id.read_pwd,R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_phone, R.id.read_member_status, R.id.read_member_expire};

        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.select_user_item,cursor,from,to);
        listView.setAdapter(adapter);
    }
}
