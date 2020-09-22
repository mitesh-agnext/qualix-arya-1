package com.custom.app.ui.analysis;

import android.text.TextUtils;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.DelayedClickListener;
import com.custom.app.R;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;
import static com.core.app.util.Constants.TYPE_MAX_WIDTH;

@EpoxyModelClass(layout = R.layout.header_batch)
public abstract class BatchHeaderModel extends EpoxyModelWithHolder<BatchHeaderModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute(DoNotHash) DelayedClickListener clickListener;
    @EpoxyAttribute(DoNotHash) OnLongClickListener longClickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        holder.title.setOnClickListener(clickListener);
        holder.title.setOnLongClickListener(longClickListener);
    }

    @Override
    protected int getViewType() {
        return TYPE_MAX_WIDTH;
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_title) TextView title;

    }
}