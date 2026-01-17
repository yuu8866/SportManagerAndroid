package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.List;

public class MemberPurchaseRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ListView lv = findViewById(R.id.lv_records);

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String user = sp.getString("users", "");

        databaseHelp db = new databaseHelp(this);
        List<String> lines = db.getMemberPurchaseRecordLines(user, 50);

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines));
    }
}
