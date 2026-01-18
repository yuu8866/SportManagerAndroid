package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;

import java.util.List;

/**
 * 社区帖子列表适配器（正方形卡片，两列展示）
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    private final Context context;
    private final List<Post> data;
    private final OnPostClickListener listener;

    public PostAdapter(Context context, List<Post> data, OnPostClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Post p = data.get(position);
        holder.tvTitle.setText(p.getTitle());
        holder.tvUser.setText("发帖人：" + p.getUsername());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUser;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvUser = itemView.findViewById(R.id.tv_post_user);
        }
    }
}
