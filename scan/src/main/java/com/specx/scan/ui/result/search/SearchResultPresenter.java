package com.specx.scan.ui.result.search;

import com.base.app.ui.base.BasePresenter;

public abstract class SearchResultPresenter extends BasePresenter<SearchResultView> {

    abstract void searchResult(String query);

}