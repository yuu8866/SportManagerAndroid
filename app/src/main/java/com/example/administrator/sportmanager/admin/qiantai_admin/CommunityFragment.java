package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.bean.Post;
import com.example.administrator.sportmanager.admin.databaseHelp;

import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView rvPosts;
    private databaseHelp dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_community, container, false);

        rvPosts = root.findViewById(R.id.rv_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new databaseHelp(getContext());

        loadPosts();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private void loadPosts() {
        // 查询帖子
        List<Post> postList = dbHelper.queryAllCommunityPosts();

        PostAdapter adapter = new PostAdapter(postList, post -> {
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            startActivity(intent);
        });

        rvPosts.setAdapter(adapter);
    }
}
