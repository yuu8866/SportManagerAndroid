package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.sportmanager.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class HomeRecommendAdapter extends RecyclerView.Adapter<HomeRecommendAdapter.VH> {

    public static class SportItem {
        public long dbId;      // sports 表 _id
        public int sportId;    // sports 表 sportid
        public String name;
        public String type;
        public double price;
        public byte[] img;

        public SportItem(long dbId, int sportId, String name, String type, double price, byte[] img) {
            this.dbId = dbId;
            this.sportId = sportId;
            this.name = name;
            this.type = type;
            this.price = price;
            this.img = img;
        }
    }

    public interface OnItemClick {
        void onClick(SportItem item);
    }

    private final Context ctx;
    private final LayoutInflater inflater;
    private final OnItemClick onItemClick;
    private final ArrayList<SportItem> data = new ArrayList<>();
    private boolean isMember = false;

    public HomeRecommendAdapter(Context ctx, ArrayList<SportItem> init, OnItemClick click) {
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.onItemClick = click;
        if (init != null) data.addAll(init);
    }

    public void setMember(boolean member) {
        this.isMember = member;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<SportItem> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_home_recommend_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SportItem it = data.get(position);

        h.tvName.setText(it.name);
        h.tvType.setText(it.type);

        h.tvPrice.setText("原价：¥" + String.format(Locale.CHINA, "%.2f", it.price) + "/天");

        if (isMember) {
            double mp = it.price * 0.9;
            h.tvMemberPrice.setText("会员价：¥" + String.format(Locale.CHINA, "%.2f", mp) + "/天");
            h.tvTag.setText("会员9折");
        } else {
            h.tvMemberPrice.setText("开通会员享 9 折");
            h.tvTag.setText("热门");
        }

        Bitmap bm = null;
        try { bm = byteToBitmap(it.img); } catch (Exception ignored) {}
        if (bm != null) {
            h.img.setImageBitmap(bm);
        } else {
            h.img.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(it);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView img;
        TextView tvName, tvType, tvPrice, tvMemberPrice, tvTag;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_sport);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvMemberPrice = itemView.findViewById(R.id.tv_member_price);
            tvTag = itemView.findViewById(R.id.tv_tag);
        }
    }
}
