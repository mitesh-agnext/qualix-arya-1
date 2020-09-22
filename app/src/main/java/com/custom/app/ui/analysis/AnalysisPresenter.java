package com.custom.app.ui.analysis;

import com.base.app.ui.base.BasePresenter;

public abstract class AnalysisPresenter extends BasePresenter<AnalysisView> {

    abstract void fetchAnalyses(String batchId);

    abstract void removeAnalyses(String batchId);

}