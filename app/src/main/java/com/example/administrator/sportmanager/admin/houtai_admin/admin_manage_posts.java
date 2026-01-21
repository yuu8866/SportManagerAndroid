package com.example.administrator.sportmanager.admin.houtai_admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 管理员端：社区帖子管理（警告 / 删除）
 */
public class admin_manage_posts extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText etSearch;
    private Button btnSearch, btnReset;
    private ListView listView;

    private databaseHelp helper;
    private Cursor currentCursor;
    private PostCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_posts);

        backBtn = findViewById(R.id.post_back);
        etSearch = findViewById(R.id.et_post_search);
        btnSearch = findViewById(R.id.btn_post_search);
        btnReset = findViewById(R.id.btn_post_reset);
        listView = findViewById(R.id.post_list);

        helper = new databaseHelp(getApplicationContext());

        backBtn.setOnClickListener(v -> finish());

        // 默认加载全部
        refreshList("");

        btnSearch.setOnClickListener(v -> refreshList(etSearch.getText().toString()));

        btnReset.setOnClickListener(v -> {
            etSearch.setText("");
            refreshList("");
        });
    }

    private void refreshList(String keyword) {
        if (currentCursor != null && !currentCursor.isClosed()) {
            currentCursor.close();
        }
        currentCursor = helper.queryCommunityPostsForAdmin(keyword);

        if (adapter == null) {
            adapter = new PostCursorAdapter(this, currentCursor);
            listView.setAdapter(adapter);
        } else {
            adapter.changeCursor(currentCursor);
        }
    }

    private String nowTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCursor != null && !currentCursor.isClosed()) {
            currentCursor.close();
        }
    }

    /**
     * CursorAdapter
     */
    private class PostCursorAdapter extends CursorAdapter {

        public PostCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_admin_post, parent, false);
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            final long postId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("create_time"));
            int warned = cursor.getInt(cursor.getColumnIndexOrThrow("warned"));
            String warnReason = cursor.getString(cursor.getColumnIndexOrThrow("warn_reason"));
            String warnTime = cursor.getString(cursor.getColumnIndexOrThrow("warn_time"));

            holder.tvTitle.setText(title);
            holder.tvContent.setText(content);
            holder.tvUser.setText("发布者：" + username);
            holder.tvTime.setText("时间：" + time);

            if (warned == 1) {
                String msg = "状态：已警告";
                if (!TextUtils.isEmpty(warnTime)) {
                    msg += "（" + warnTime + "）";
                }
                holder.tvWarnStatus.setText(msg);

                if (!TextUtils.isEmpty(warnReason)) {
                    holder.tvWarnReason.setVisibility(View.VISIBLE);
                    holder.tvWarnReason.setText("原因：" + warnReason);
                } else {
                    holder.tvWarnReason.setVisibility(View.GONE);
                }
                holder.btnWarn.setText("再次警告");
            } else {
                holder.tvWarnStatus.setText("状态：正常");
                holder.tvWarnReason.setVisibility(View.GONE);
                holder.btnWarn.setText("警告");
            }

            holder.btnWarn.setOnClickListener(v -> showWarnDialog(postId));
            holder.btnDelete.setOnClickListener(v -> confirmDelete(postId));
        }

        class ViewHolder {
            TextView tvTitle, tvContent, tvUser, tvTime, tvWarnStatus, tvWarnReason;
            Button btnWarn, btnDelete;

            ViewHolder(View root) {
                tvTitle = root.findViewById(R.id.post_title);
                tvContent = root.findViewById(R.id.post_content);
                tvUser = root.findViewById(R.id.post_user);
                tvTime = root.findViewById(R.id.post_time);
                tvWarnStatus = root.findViewById(R.id.post_warn_status);
                tvWarnReason = root.findViewById(R.id.post_warn_reason);
                btnWarn = root.findViewById(R.id.btn_post_warn);
                btnDelete = root.findViewById(R.id.btn_post_delete);
            }
        }
    }

    private void showWarnDialog(final long postId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_post_warn, null);
        final EditText etReason = dialogView.findViewById(R.id.et_warn_reason);

        new AlertDialog.Builder(this)
                .setTitle("警告帖子")
                .setView(dialogView)
                .setPositiveButton("确定警告", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reason = etReason.getText().toString().trim();
                        if (TextUtils.isEmpty(reason)) {
                            reason = "违反社区规范";
                        }
                        helper.warnCommunityPost(postId, reason, nowTime());
                        Toast.makeText(admin_manage_posts.this, "已警告该帖子", Toast.LENGTH_SHORT).show();
                        refreshList(etSearch.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void confirmDelete(final long postId) {
        new AlertDialog.Builder(this)
                .setTitle("删除帖子")
                .setMessage("确定要删除该帖子吗？删除后不可恢复")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteCommunityPost(postId);
                        Toast.makeText(admin_manage_posts.this, "已删除帖子", Toast.LENGTH_SHORT).show();
                        refreshList(etSearch.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
