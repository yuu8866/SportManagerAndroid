package com.example.administrator.sportmanager.admin.houtai_admin;

import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

/**
 * 查找读者的界面
 *
 */
public class select_user_admin extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_admin);
        listView=(ListView)findViewById(R.id.sel_reader_list);
        databaseHelp help=new databaseHelp(getApplicationContext());
        Cursor cursor=help.query();
        String from[]={"user","password","name", "sex", "birthday", "phone"};
        int to[]={R.id.read_user,R.id.read_pwd,R.id.read_name, R.id.read_sex, R.id.read_birth, R.id.read_phone};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.select_user_item,cursor,from,to);
        listView.setAdapter(adapter);
    }
}
