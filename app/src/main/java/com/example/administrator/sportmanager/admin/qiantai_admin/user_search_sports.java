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
        init();
    }

    private void init() {
        listView = findViewById(R.id.user_search_sport_list);
        final databaseHelp help = new databaseHelp(getApplicationContext());

        back_bt = findViewById(R.id.user_search_sport_back);

        // ✅ 返回：回到新导航页（租赁Tab）
        back_bt.setOnClickListener(view -> backToUserNavRent());

        Bundle bundle = this.getIntent().getExtras();
        name = bundle.getString("name");

        Cursor cursor = help.querysportsname(name);
        String from[] = {"name", "type", "user","owner","rank","img"};
        int to[] = {R.id.admin_Sport_Name, R.id.admin_Sport_Type, R.id.admin_sport_author, R.id.admin_sport_publish, R.id.admin_Sport_Rank, R.id.admin_sport_info_img};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.admin_sport_item, cursor, from, to);
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (view.getId() == R.id.admin_sport_info_img) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(byteToBitmap(cursor1.getBlob(columnIndex)));
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        backToUserNavRent();
    }

    private void backToUserNavRent() {
        Intent i = new Intent(this, UserNavActivity.class);
        i.putExtra(UserNavActivity.EXTRA_OPEN_TAB, UserNavActivity.TAB_RENT);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}
