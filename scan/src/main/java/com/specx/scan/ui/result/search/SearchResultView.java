package com.specx.scan.ui.result.search;

import com.base.app.ui.base.BaseView;
import com.specx.scan.data.model.result.ResultItem;

import java.util.List;

public interface SearchResultView extends BaseView {

    void showList(List<ResultItem> results);

    void onBatchIdClicked(String batchId);

}