package com.specx.scan.ui.result.search;

import com.base.app.ui.epoxy.BaseEpoxy;
import com.specx.scan.data.model.result.ResultItem;

import java.util.List;

public class SearchResultController extends BaseEpoxy {

    private SearchResultView view;
    private List<ResultItem> results;

    SearchResultController(SearchResultView view) {
        this.view = view;
    }

    void setList(List<ResultItem> results) {
        this.results = results;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (ResultItem result : results) {
            new SearchResultItemGroup(view, result)
                    .addTo(this);
        }
    }
}