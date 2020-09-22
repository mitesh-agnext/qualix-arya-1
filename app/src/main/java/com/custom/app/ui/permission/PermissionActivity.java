package com.custom.app.ui.permission;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.app.ui.base.BaseActivity;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.custom.app.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PermissionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            ((TextView) toolbar.findViewById(R.id.title)).setText(getString(R.string.title_permission));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        PermissionController controller = new PermissionController();
        recyclerView.setAdapter(controller.getAdapter());

        if (!TextUtils.isEmpty(userManager.getPermissions())) {
            List<String> permissions = new Gson().fromJson(userManager.getPermissions(),
                    new TypeToken<List<String>>() {}.getType());

            controller.setList(permissions);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}