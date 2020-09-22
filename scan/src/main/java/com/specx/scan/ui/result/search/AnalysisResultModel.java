package com.specx.scan.ui.result.search;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.specx.scan.R2;
import com.specx.scan.data.model.analysis.AnalysisItem;

import butterknife.BindView;

@EpoxyModelClass(layout = R2.layout.layout_analysis_result)
public abstract class AnalysisResultModel extends EpoxyModelWithHolder<AnalysisResultModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute AnalysisItem analysis;

    @Override
    public void bind(@NonNull Holder holder) {

        if (!TextUtils.isEmpty(analysis.getName())) {
            holder.analysis.setText(analysis.getName());
        }

        if (!TextUtils.isEmpty(analysis.getTotalAmount())) {
            SpannyText spanny = new SpannyText(analysis.getTotalAmount())
                    .append(analysis.getAmountUnit());
            holder.result.setText(spanny);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tv_analysis) TextView analysis;
        @BindView(R2.id.tv_result) TextView result;

    }
}