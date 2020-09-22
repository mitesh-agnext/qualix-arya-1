package com.custom.app.ui.commodity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.DelayedClickListener;
import com.custom.app.R;
import com.media.app.GlideApp;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_commodity_list)
public abstract class CommodityItemModel extends EpoxyModelWithHolder<CommodityItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute String desc;
    @EpoxyAttribute String thumbUrl;
    @EpoxyAttribute(DoNotHash) DelayedClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (!TextUtils.isEmpty(desc)) {
            holder.desc.setText(desc);
            holder.desc.setVisibility(View.VISIBLE);
        } else {
            holder.desc.setVisibility(View.GONE);
        }

        GlideApp.with(view.context()).load(thumbUrl)
                .error(ContextCompat.getDrawable(view.context(), R.drawable.ic_no_image))
                .into(holder.thumb);

        holder.item.setOnClickListener(clickListener);
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.cv_item) CardView item;
        @BindView(R.id.iv_thumb) ImageView thumb;
        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_desc) TextView desc;

    }
}