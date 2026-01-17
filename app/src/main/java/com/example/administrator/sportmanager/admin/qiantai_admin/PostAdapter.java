package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onClick(Post post);
    }

    private final List<Post> postList;
    private final OnPostClickListener listener;

    public PostAdapter(List<Post> postList, OnPostClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvUser.setText("发帖人：" + post.getUsername());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvUser;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvUser = itemView.findViewById(R.id.tv_post_user);
        }
    }
}
