package com.custom.app.ui.home;

import com.base.app.ui.epoxy.BaseEpoxy;

import java.util.List;

public class HomeController extends BaseEpoxy {

    private HomeView view;
    private List<DeviceItem> items;

    public HomeController(HomeView view) {
        this.view = view;
    }

    void setList(List<DeviceItem> items) {
        this.items = items;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (DeviceItem item : items) {
            new HomeItemModel_()
                    .id(items.indexOf(item))
                    .view(view)
                    .title(item.getDevice_name())
                    .count(item.getDevice_count())
                    .addTo(this);
        }
    }
}