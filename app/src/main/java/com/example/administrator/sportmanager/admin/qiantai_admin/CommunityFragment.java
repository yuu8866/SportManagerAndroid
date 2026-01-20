package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * 社区页面：两列正方形帖子卡片 + 悬浮发布按钮
 */
public class CommunityFragment extends Fragment {

    private databaseHelp help;
    private RecyclerView rv;
    private PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community, container, false);

        rv = root.findViewById(R.id.rv_posts);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FloatingActionButton fab = root.findViewById(R.id.fab_publish);

        help = new databaseHelp(requireContext());

        // 保证示例帖子存在
        help.ensureSampleCommunityPosts();

        // 初始化列表
        loadPosts();

        // 点击帖子进入详情
        adapter = new PostAdapter(requireContext(), loadPosts(), post -> {
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 点击悬浮按钮进入发布页
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PostPublishActivity.class);
            startActivity(intent);
        });


        return root;
    }

    /** 每次回来都刷新（发布帖子后会自动更新） */
    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private List<Post> loadPosts() {
        return help.queryAllCommunityPosts();
    }

    private void refreshList() {
        if (adapter != null) {
            List<Post> newData = loadPosts();
            adapter = new PostAdapter(requireContext(), newData, post -> {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("post_id", post.getId());
                startActivity(intent);
            });
            rv.setAdapter(adapter);
        }
    }
}
