package com.custom.app.ui.user.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.base.app.ui.base.BaseActivity;
import com.core.app.ui.custom.SpannyText;
import com.custom.app.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.media.app.GlideApp;
import com.user.app.data.UserData;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDetailActivity extends BaseActivity implements UserDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.civ_profile)
    ImageView civProfile;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_desc)
    TextView tvDesc;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        Window window = getWindow();

        window.setEnterTransition(fade);
        window.setExitTransition(fade);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        if (!TextUtils.isEmpty(userManager.getUserData())) {
            String jsonData = userManager.getUserData();
            UserData user = new Gson().fromJson(jsonData, UserData.class);
            if (user != null) {
                GlideApp.with(this)
                        .load(user.getProfile())
                        .error(R.drawable.ic_profile)
                        .into(civProfile);

                tvTitle.setText(user.getName());
                tvDesc.setText(user.getEmail());

                appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                    float percentage = 1f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                    civProfile.setAlpha(percentage);

                    if (percentage == 0f) {
                        if (!TextUtils.isEmpty(user.getName())) {
                            toolbar.setTitle(user.getName());
                        }
                    } else {
                        toolbar.setTitle("");
                    }
                });

                if (!TextUtils.isEmpty(user.getMobile())) {
                    SpannyText spanny = new SpannyText("Contact")
                            .append(": ")
                            .append(user.getMobile());
                    tvPhone.setText(spanny);
                }
            }
        }
    }
}