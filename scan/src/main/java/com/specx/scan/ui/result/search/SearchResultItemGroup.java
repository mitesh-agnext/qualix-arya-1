package com.specx.scan.ui.result.search;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;
import com.base.app.ui.epoxy.LinearCarouselModel_;
import com.core.app.ui.custom.DelayedClickListener;
import com.core.app.util.AlertUtil;
import com.specx.scan.R;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.result.ResultItem;

import java.util.ArrayList;
import java.util.List;

class SearchResultItemGroup extends EpoxyModelGroup {

    private SearchResultView view;
    private ResultItem result;

    SearchResultItemGroup(SearchResultView view, ResultItem result) {
        super(R.layout.model_search_group, buildModels(view, result));
        this.view = view;
        this.result = result;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        holder.getRootView().setOnClickListener(new DelayedClickListener() {
            @Override
            public void onClicked(View v) {
                AlertUtil.showToast(view.context(), view.context().getString(R.string.long_click_copy_msg));
            }
        });
        holder.getRootView().setOnLongClickListener(v -> {
            view.onBatchIdClicked(result.getBatchId());
            return true;
        });
    }

    private static List<EpoxyModel<?>> buildModels(SearchResultView view, ResultItem result) {

        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        models.add(new SearchResultHeaderModel_()
                .view(view)
                .id(result.getId())
                .userId(result.getUserId())
                .batchId(result.getBatchId())
                .sampleId(result.getSample().getId())
                .datetime(result.getDatetime())
                .commodity(result.getCommodity().getName())
                .serialNumber(result.getSerialNumber()));

        List<AnalysisResultModel_> analysisModels = new ArrayList<>();
        for (AnalysisItem analysis : result.getAnalyses()) {
            analysisModels.add(new AnalysisResultModel_()
                    .view(view)
                    .id(analysis.getId())
                    .analysis(analysis));
        }

        models.add(new LinearCarouselModel_()
                .id("analyses")
                .models(analysisModels));

        return models;
    }
}