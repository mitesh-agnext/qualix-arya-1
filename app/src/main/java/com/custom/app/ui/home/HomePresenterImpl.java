package com.custom.app.ui.home;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomePresenterImpl extends HomePresenter {

    private HomeView view;
    private HomeInteractor interactor;

    public HomePresenterImpl(HomeInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(HomeView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchCount() {
        showProgressBar();

        disposable = interactor.getTotalDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(device -> {
                    if (isViewAttached()) {
                        view.showTotalDevices(device);
                    }

                    return interactor.getUnassignedDevices();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(device -> {
                    if (isViewAttached()) {
                        view.showUnassignedDevices(device);
                    }

                    return interactor.getTotalCustomers();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(customer -> {
                    if (isViewAttached()) {
                        view.showCustomers(customer);
                    }

                    return interactor.getTotalUsers();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(user -> {
                    if (isViewAttached()) {
                        view.showUsers(user);
                    }

                    return interactor.getPurchaseOrders();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(order -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showOrders(order);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}