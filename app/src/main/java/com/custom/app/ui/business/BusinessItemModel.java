package com.custom.app.ui.business;

import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;
import com.media.app.GlideApp;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

@EpoxyModelClass(layout = R.layout.item_business_list)
public abstract class BusinessItemModel extends EpoxyModelWithHolder<BusinessItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute int color;
    @EpoxyAttribute String title;
    @EpoxyAttribute String count;

    @Override
    public void bind(@NonNull Holder holder) {
        if (color != 0) {
            GlideApp.with(view.context())
                    .load(new ColorDrawable(color))
                    .into(holder.thumb);
        }

        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (!TextUtils.isEmpty(count)) {
            holder.desc.setText(count);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.iv_thumb) CircleImageView thumb;
        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_desc) TextView desc;

    }
}