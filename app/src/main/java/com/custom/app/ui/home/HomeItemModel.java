package com.custom.app.ui.home;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import butterknife.BindView;

@EpoxyModelClass(layout = R.layout.item_home_list)
public abstract class HomeItemModel extends EpoxyModelWithHolder<HomeItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute String count;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (!TextUtils.isEmpty(count)) {
            holder.desc.setText(count);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_desc) TextView desc;

    }
}