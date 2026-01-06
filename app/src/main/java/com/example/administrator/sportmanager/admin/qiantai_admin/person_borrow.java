package com.example.administrator.sportmanager.admin.qiantai_admin;
/*
个人借书表
 */

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
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.List;
import java.util.Map;

public class person_borrow extends AppCompatActivity {
    private ListView listView;
    private String sportname, sportauthor, sport_bor_time,username;
    private int sportid,borrowid;
    private List<Map<String, Object>> data;
    private Map map;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_borrow);
        listView = (ListView) findViewById(R.id.show_borrow);
        final databaseHelp help = new databaseHelp(getApplicationContext());
        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);

        username = perf.getString("users", "");//获得当前用户名称
        //根据用户查询自己的借阅信息
        data = help.queryborrow(username);
        SimpleAdapter adapter = new SimpleAdapter(
                person_borrow.this, data, R.layout.borrow_item,
                new String[]{"Borname", "sportid", "sportname", "sportauthor", "days", "bortime"},
                new int[]{R.id.Borname, R.id.Bsportid, R.id.Bsportname, R.id.Bsportauthor, R.id.Bdays, R.id.Bnowtimae
                }
        );
        listView.setAdapter(adapter);
        //通过id查询运动器械表里的所有信息，用bundle进行数据交互

        //点击item跳转到还书页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                map = solveData(position);

                //  不要强转 (int)，统一转 String 再 parse，避免 ClassCastException
                int borrowid = Integer.parseInt(String.valueOf(map.get("_Bid")));
                int sportid  = Integer.parseInt(String.valueOf(map.get("sportid")));

                String sportname = String.valueOf(map.get("sportname"));
                String sportauthor = String.valueOf(map.get("sportauthor"));
                String sport_bor_time = String.valueOf(map.get("bortime"));

                // days 可能为空，给默认值
                String daysDisplay = map.get("days") == null ? "1天" : String.valueOf(map.get("days"));
                String daysStr = map.get("days") == null ? "1" : String.valueOf(map.get("days"));
                daysStr = daysStr.replaceAll("\\D+", ""); // "2天" -> "2"
                int daysInt = 1;
                try { daysInt = Integer.parseInt(daysStr); } catch (Exception ignore) {}


                Intent intent = new Intent(person_borrow.this, com.example.administrator.sportmanager.admin.qiantai_admin.PayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("sportid", sportid);
                bundle.putInt("borrowid", borrowid);
                bundle.putString("sportname", sportname);
                bundle.putString("sportauthor", sportauthor);
                bundle.putString("sporttime", sport_bor_time);
                bundle.putInt("days", daysInt);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        back = findViewById(R.id.btn_person_borrow_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(person_borrow.this, com.example.administrator.sportmanager.admin.qiantai_admin.contentActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }
        });




    }
    public Map solveData(int i){
        map = data.get(i);
        return map;
    }
}
