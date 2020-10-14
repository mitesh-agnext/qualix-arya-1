package com.specx.scan.ui.result.base;

import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.specx.scan.R;
import com.specx.scan.R2;

import butterknife.BindView;

@EpoxyModelClass(layout = R2.layout.header_scan_result)
public abstract class ResultHeaderModel extends EpoxyModelWithHolder<ResultHeaderModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute String subtitle;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            SpannyText spanny = new SpannyText(title,
                    new ForegroundColorSpan(ContextCompat.getColor(view.context(), R.color.medium_grey)))
                    .append(" ");
            holder.title.setText(spanny);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tv_title) TextView title;

    }
}