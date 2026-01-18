package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

/**
 * 租赁列表页（BottomNav: 租赁）
 * ✅ 右上角新增搜索器材功能（SearchView）
 */
public class RentFragment extends Fragment {

    private ListView listView;
    private databaseHelp help;

    private SimpleCursorAdapter adapter;
    private Cursor currentCursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 让 Fragment 可以向 Toolbar 注入菜单（右上角搜索）
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        help = new databaseHelp(requireContext());
        listView = root.findViewById(R.id.list_rent);

        // 默认加载全部器材
        currentCursor = help.querysports();

        String[] from = {"name", "type", "user", "owner", "rank", "img", "price"};
        int[] to = {R.id.user_Sport_Name, R.id.user_Sport_Type, R.id.user_sport_author,
                R.id.user_sport_publish, R.id.user_Sport_Rank, R.id.user_sport_info_img, R.id.user_sport_pice};

        adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.sport_item,
                currentCursor,
                from,
                to,
                0
        );

        adapter.setViewBinder((view, c, columnIndex) -> {
            if (view.getId() == R.id.user_sport_info_img) {
                ImageView icon = (ImageView) view;
                icon.setImageBitmap(byteToBitmap(c.getBlob(columnIndex)));
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这里的 id 是 Cursor 的 _id（真实主键），比 position 更可靠
                Intent intent = new Intent(requireContext(), borrowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", (int) id - 1); // borrowActivity 里做了 +1
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return root;
    }

    /**
     * ✅ Toolbar 右上角菜单（搜索器材）
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_rent_top, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索器材/类型/发布人...");

        // 输入变化实时搜索
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return true;
            }
        });

        // 关闭搜索（X 按钮）后恢复全部列表
        searchView.setOnCloseListener(() -> {
            doSearch("");
            return false;
        });
    }

    private void doSearch(String keyword) {
        if (help == null || adapter == null) return;

        Cursor newCursor;
        if (keyword == null || keyword.trim().isEmpty()) {
            newCursor = help.querysports();
        } else {
            newCursor = help.searchSports(keyword.trim());
        }

        // 释放旧 cursor，避免资源泄露
        Cursor old = adapter.getCursor();
        adapter.changeCursor(newCursor);
        currentCursor = newCursor;
        if (old != null && !old.isClosed()) old.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 释放 cursor
        if (currentCursor != null && !currentCursor.isClosed()) {
            currentCursor.close();
        }
    }
}
