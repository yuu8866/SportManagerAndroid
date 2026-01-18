package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.List;
import java.util.Map;

public class person_borrow extends AppCompatActivity {

    private ListView listView;
    private String username;
    private List<Map<String, Object>> data;
    private Map map;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_borrow);

        listView = findViewById(R.id.show_borrow);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);

        username = perf.getString("users", "");
        data = help.queryborrow(username);

        SimpleAdapter adapter = new SimpleAdapter(
                person_borrow.this, data, R.layout.borrow_item,
                new String[]{"Borname", "sportid", "sportname", "sportauthor", "days", "bortime"},
                new int[]{R.id.Borname, R.id.Bsportid, R.id.Bsportname, R.id.Bsportauthor, R.id.Bdays, R.id.Bnowtimae}
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {

            map = data.get(position);

            int borrowid = Integer.parseInt(String.valueOf(map.get("_Bid")));
            int sportid = Integer.parseInt(String.valueOf(map.get("sportid")));

            String sportname = String.valueOf(map.get("sportname"));
            String sportauthor = String.valueOf(map.get("sportauthor"));
            String sport_bor_time = String.valueOf(map.get("bortime"));

            String daysStr = map.get("days") == null ? "1" : String.valueOf(map.get("days"));
            daysStr = daysStr.replaceAll("\\D+", "");
            int daysInt = 1;
            try { daysInt = Integer.parseInt(daysStr); } catch (Exception ignore) {}

            Intent intent = new Intent(person_borrow.this, PayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("sportid", sportid);
            bundle.putInt("borrowid", borrowid);
            bundle.putString("sportname", sportname);
            bundle.putString("sportauthor", sportauthor);
            bundle.putString("sporttime", sport_bor_time);
            bundle.putInt("days", daysInt);
            intent.putExtras(bundle);

            startActivity(intent);
        });

        back = findViewById(R.id.btn_person_borrow_back);

        // ✅ 返回：永远回到新导航页（主页Tab）
        back.setOnClickListener(v -> backToUserNavHome());
    }

    @Override
    public void onBackPressed() {
        backToUserNavHome();
    }

    private void backToUserNavHome() {
        Intent i = new Intent(this, UserNavActivity.class);
        i.putExtra(UserNavActivity.EXTRA_OPEN_TAB, UserNavActivity.TAB_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}
