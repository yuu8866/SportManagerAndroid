package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.example.administrator.sportmanager.admin.utils.BitmapTool;


import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 帖子详情页：收藏 + 点赞 + 评论 + 进入贴主主页
 */
public class PostDetailActivity extends AppCompatActivity {

    private databaseHelp help;
    private String loginUser;
    private long postId;

    private ImageView ivImg;

    private TextView tvUser, tvTitle, tvContent, tvTime;
    private Button btnCollect, btnLike;
    private TextView tvLikeCount;

    private EditText etComment;
    private Button btnSendComment;
    private ListView lvComments;

    private SimpleCursorAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        help = new databaseHelp(getApplicationContext());

        SharedPreferences perf = getSharedPreferences("data", MODE_PRIVATE);
        loginUser = perf.getString("users", "");

        postId = getIntent().getLongExtra("post_id", -1);

        tvUser = findViewById(R.id.tv_detail_user);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvTime = findViewById(R.id.tv_detail_time);

        btnCollect = findViewById(R.id.btn_collect_post);
        btnLike = findViewById(R.id.btn_like_post);
        tvLikeCount = findViewById(R.id.tv_like_count);

        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);
        lvComments = findViewById(R.id.lv_comments);
        ivImg = findViewById(R.id.iv_detail_img);

        Button btnBack = findViewById(R.id.btn_detail_back);
        btnBack.setOnClickListener(v -> finish());

        loadPost();
        refreshLikeUI();
        refreshCollectButton();
        refreshComments();

        // ✅ 收藏
        btnCollect.setOnClickListener(v -> toggleCollect());

        // ✅ 点赞
        btnLike.setOnClickListener(v -> toggleLike());

        // ✅ 评论
        btnSendComment.setOnClickListener(v -> sendComment());

        // ✅ 点击贴主 → 贴主主页
        tvUser.setOnClickListener(v -> {
            Intent i = new Intent(PostDetailActivity.this, PublicUserHomeActivity.class);
            i.putExtra("target_user", tvUser.getText().toString().trim());
            startActivity(i);
        });
    }

    private void loadPost() {
        Post p = help.queryCommunityPostById(postId);
        if (p == null) {
            Toast.makeText(this, "帖子不存在或已删除", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvUser.setText(p.getUsername());
        tvTitle.setText(p.getTitle());
        tvContent.setText(p.getContent());
        tvTime.setText(p.getCreateTime());

        byte[] img = p.getImg();
        if(img != null && img.length > 0){
            Bitmap bmp = BitmapTool.byteToBitmap(img);
            ivImg.setImageBitmap(bmp);
            ivImg.setVisibility(View.VISIBLE);
        }else{
            ivImg.setVisibility(View.GONE);
        }


    }

    private void refreshCollectButton() {
        boolean collected = help.isPostCollected(loginUser, postId);
        btnCollect.setText(collected ? "已收藏" : "收藏");
    }

    private void toggleCollect() {
        if (TextUtils.isEmpty(loginUser)) {
            Toast.makeText(this, "请先登录后再收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean collected = help.isPostCollected(loginUser, postId);
        if (collected) {
            help.cancelPostCollect(loginUser, postId);
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        } else {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            help.addPostCollect(loginUser, postId, now);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        }
        refreshCollectButton();
    }

    private void refreshLikeUI() {
        int count = help.queryPostLikeCount(postId);
        tvLikeCount.setText(String.valueOf(count));
        boolean liked = help.isPostLiked(loginUser, postId);
        btnLike.setText(liked ? "已点赞" : "点赞");
    }

    private void toggleLike() {
        if (TextUtils.isEmpty(loginUser)) {
            Toast.makeText(this, "请先登录后再点赞", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean liked = help.isPostLiked(loginUser, postId);
        if (liked) {
            help.cancelPostLike(loginUser, postId);
        } else {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            help.addPostLike(loginUser, postId, now);
        }
        refreshLikeUI();
    }

    private void refreshComments() {
        Cursor c = help.queryPostCommentCursor(postId);

        String[] from = {"user", "content", "create_time"};
        int[] to = {R.id.tv_comment_user, R.id.tv_comment_content, R.id.tv_comment_time};

        commentAdapter = new SimpleCursorAdapter(
                this, R.layout.item_comment, c, from, to, 0
        );
        lvComments.setAdapter(commentAdapter);
    }

    private void sendComment() {
        if (TextUtils.isEmpty(loginUser)) {
            Toast.makeText(this, "请先登录后再评论", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        help.addPostComment(loginUser, postId, text, now);

        etComment.setText("");
        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
        refreshComments();
    }

    // ✅ 统一跳转入口：供外部调用
    public static void start(Context context, long postId) {
        Intent i = new Intent(context, PostDetailActivity.class);
        i.putExtra("post_id", postId);
        context.startActivity(i);
    }
}
