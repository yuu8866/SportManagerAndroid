package com.example.administrator.sportmanager.admin.qiantai_admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.sportmanager.R;
import com.example.administrator.sportmanager.admin.databaseHelp;

import static com.example.administrator.sportmanager.admin.utils.BitmapTool.byteToBitmap;

public class RentFragment extends Fragment {

    private ListView listView;
    private databaseHelp help;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        help = new databaseHelp(requireContext());
        listView = root.findViewById(R.id.list_rent);

        Cursor cursor = help.querysports();

        String from[] = {"name", "type", "user", "owner", "rank", "img","price"};
        int to[] = {R.id.user_Sport_Name, R.id.user_Sport_Type, R.id.user_sport_author,
                R.id.user_sport_publish, R.id.user_Sport_Rank, R.id.user_sport_info_img, R.id.user_sport_pice};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.sport_item,
                cursor,
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
                Intent intent = new Intent(requireContext(), borrowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", position); // borrowActivity内部会+1
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return root;
    }
}
