package com.example.administrator.sportmanager.admin.houtai_admin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.ActivityCollector;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.bitmapToByte;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员添加运动器械的界面
 */

public class admin_add_sport extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back_bt;
    Bitmap bmp = null;
    private Spinner spinner;
    private databaseHelp helper;
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<String>();
    Uri uri;
    private String pub;
    private ImageView sportimg;
    private EditText et_sportid, et_sportname, et_sporttype, et_sportuser, et_sportowner, et_sportprice, et_sportrank, et_sportcomment;
    private Button btn_sportcommit, btn_sportback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sport);
        list.add("器材云");
        list.add("第三方平台");
//        list.add("线下登记");
//        list.add("器材云");
//        list.add("器材云");
//        list.add("器材云");
        spinner = (Spinner) findViewById(R.id.spinner2);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pub = adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initdata();//界面初始化
        helper = new databaseHelp(getApplicationContext());
    }

    private void initdata() {
        sportimg = findViewById(R.id.add_sportimg);

        Resources r = admin_add_sport.this.getResources();
        bmp = BitmapFactory.decodeResource(r, R.drawable.head);

        et_sportid = findViewById(R.id.et_sportid);
        et_sportname = findViewById(R.id.et_sportname);
        et_sporttype = findViewById(R.id.et_sporttype);
        et_sportuser = findViewById(R.id.et_sportuser);
        et_sportprice = findViewById(R.id.et_sportprice);
        et_sportrank = findViewById(R.id.et_sportrank);
        et_sportcomment = findViewById(R.id.et_sportcomment);


        btn_sportcommit = findViewById(R.id.btn_sportcommit);
        btn_sportback = findViewById(R.id.btn_sportback);

        btn_sportcommit.setOnClickListener(this);
        sportimg.setOnClickListener(this);
        btn_sportback.setOnClickListener(this);

    }

    //对管理员输入的运动器械信息进行验证，全部符合要求才能通过
    boolean testid = true, testother = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sportcommit:
                if (et_sportid.getText() == null) {
                    Toast.makeText(admin_add_sport.this, "请输入器材id", Toast.LENGTH_SHORT).show();
                    testid = false;
                    break;
                }

                if (et_sportname.getText().length() == 0) {
                    Toast.makeText(admin_add_sport.this, "请输入完整器材信息", Toast.LENGTH_SHORT).show();
                    testother = false;
                    break;
                }
                if (testid == true && testother == true) {
                    helper.insersporttdata(
                            et_sportid.getText().toString(),
                            et_sportname.getText().toString(),
                            et_sporttype.getText().toString(),
                            et_sportuser.getText().toString(),
                            pub,
                            et_sportprice.getText().toString(),
                            et_sportrank.getText().toString(),
                            et_sportcomment.getText().toString(),
                            bitmapToByte(bmp)
                    );
                    Toast.makeText(admin_add_sport.this, "添加器材成功！", Toast.LENGTH_SHORT).show();
                    break;
                }


            case R.id.add_sportimg:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);  // 第二个参数是请求码
                break;

            case R.id.btn_sportback:
                Intent intentback = new Intent();
                intentback.setClass(admin_add_sport.this, admin_manager_sports.class);
                startActivity(intentback);
                ActivityCollector.finishAll();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:  // 请求码
                parseUri(data);
                break;
            default:
        }
    }

    public void parseUri(Intent data) {
        uri = data.getData();
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                Log.i("111", uri.getAuthority());
                is = admin_add_sport.this.getContentResolver().openInputStream(uri);
                bmp = BitmapFactory.decodeStream(is);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        sportimg.setImageBitmap(bmp);
    }

}
