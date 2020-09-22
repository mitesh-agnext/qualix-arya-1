package com.specx.scan.ui.result.search;

import com.specx.scan.ui.result.base.ResultInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchResultPresenterImpl extends SearchResultPresenter {

    private SearchResultView view;
    private ResultInteractor interactor;

    public SearchResultPresenterImpl(ResultInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(SearchResultView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void searchResult(String query) {
        disposable = interactor.searchResultInDb(query)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(results -> {
                    if (isViewAttached()) {
                        view.showList(results);
                    }
                }, error -> {
                    showEmptyView();
                    showMessage(error.getMessage());
                });
    }
}