package com.custom.app.ui.commodity;

import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.custom.app.R;

import butterknife.BindView;

import static com.core.app.util.Constants.TYPE_MAX_WIDTH;

@EpoxyModelClass(layout = R.layout.header_commodity)
public abstract class CommodityHeaderModel extends EpoxyModelWithHolder<CommodityHeaderModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute String subtitle;
    @EpoxyAttribute int step;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (!TextUtils.isEmpty(subtitle)) {
            holder.subtitle.setText(subtitle);
        }

        SpannyText spanny = new SpannyText(String.valueOf(step), new RelativeSizeSpan(1.5f))
                .append("/2");

        holder.step.setText(spanny);
    }

    @Override
    protected int getViewType() {
        return TYPE_MAX_WIDTH;
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_subtitle) TextView subtitle;
        @BindView(R.id.tv_step) TextView step;

    }
}