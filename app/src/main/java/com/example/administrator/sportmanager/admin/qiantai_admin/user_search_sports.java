package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class user_search_sports extends AppCompatActivity {
    private ImageButton back_bt;
    private ListView listView;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_sports);
        init();//界面初始化

    }

    private void init() {
        listView = findViewById(R.id.user_search_sport_list);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        back_bt = findViewById(R.id.user_search_sport_back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_search_sports.this, com.example.administrator.sportmanager.admin.qiantai_admin.contentActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });
        Bundle bundle=this.getIntent().getExtras();
        name=bundle.getString("name");
        Cursor cursor = help.querysportsname(name);
        String from[] = {"name", "type", "user","owner","rank","img"};
        int to[] = {R.id.admin_Sport_Name, R.id.admin_Sport_Type, R.id.admin_sport_author, R.id.admin_sport_publish, R.id.admin_Sport_Rank, R.id.admin_sport_info_img};
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
    }


}
