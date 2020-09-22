package com.custom.app.ui.analysis;

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

@EpoxyModelClass(layout = R.layout.item_analysis_list)
public abstract class AnalysisItemModel extends EpoxyModelWithHolder<AnalysisItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute int thumbnail;
    @EpoxyAttribute boolean isDone;
    @EpoxyAttribute(DoNotHash) DelayedClickListener clickListener;
    @EpoxyAttribute(DoNotHash) DelayedClickListener removeListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (isDone) {
            holder.done.setVisibility(View.VISIBLE);
            holder.remove.setVisibility(View.VISIBLE);
        } else {
            holder.done.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
        }

        GlideApp.with(view.context())
                .load(ContextCompat.getDrawable(view.context(), thumbnail))
                .into(holder.thumb);

        holder.item.setOnClickListener(clickListener);
        holder.remove.setOnClickListener(removeListener);
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.cv_item) CardView item;
        @BindView(R.id.iv_done) ImageView done;
        @BindView(R.id.iv_remove) ImageView remove;
        @BindView(R.id.iv_thumb) ImageView thumb;
        @BindView(R.id.tv_title) TextView title;

    }
}