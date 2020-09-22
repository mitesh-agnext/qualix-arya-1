package com.custom.app.ui.analysis;

import android.view.View;

import com.base.app.ui.base.BaseView;
import com.specx.scan.data.model.analysis.AnalysisItem;

import java.util.List;

interface AnalysisView extends BaseView {

    void showList(List<AnalysisItem> analyses);

    void onItemClicked(View itemView, AnalysisItem analysis);

    void onRemoveClicked(AnalysisItem analysis);

    void onBatchIdClicked();

}