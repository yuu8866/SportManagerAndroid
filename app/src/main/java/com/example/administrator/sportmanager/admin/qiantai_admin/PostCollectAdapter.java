package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;

import java.util.List;

/**
 * 我的收藏-社区收藏列表适配器
 */
public class PostCollectAdapter extends RecyclerView.Adapter<PostCollectAdapter.VH> {

    public interface OnItemActionListener {
        void onOpenDetail(long postId);
        void onCancelCollect(long collectId);
    }

    public static class CollectPostItem {
        public long collectId;   // community_post_collect._id
        public long postId;      // community_post._id
        public String title;
        public String username;
        public String time;
    }

    private final Context context;
    private final List<CollectPostItem> data;
    private final OnItemActionListener listener;

    public PostCollectAdapter(Context context, List<CollectPostItem> data, OnItemActionListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_collect_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CollectPostItem item = data.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvUser.setText("发帖人：" + item.username);
        holder.tvTime.setText(item.time);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenDetail(item.postId);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancelCollect(item.collectId);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUser, tvTime;
        Button btnCancel;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_collect_post_title);
            tvUser = itemView.findViewById(R.id.tv_collect_post_user);
            tvTime = itemView.findViewById(R.id.tv_collect_post_time);
            btnCancel = itemView.findViewById(R.id.btn_cancel_collect_post);
        }
    }
}
