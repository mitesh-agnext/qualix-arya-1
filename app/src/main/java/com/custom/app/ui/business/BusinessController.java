package com.custom.app.ui.business;

import com.base.app.ui.epoxy.BaseEpoxy;
import com.custom.app.data.model.graph.GraphItem;

import java.util.List;

public class BusinessController extends BaseEpoxy {

    private BusinessView view;
    private List<GraphItem> items;

    public BusinessController(BusinessView view) {
        this.view = view;
    }

    void setList(List<GraphItem> items) {
        this.items = items;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (GraphItem item : items) {
            new BusinessItemModel_()
                    .id(items.indexOf(item))
                    .view(view)
                    .color(item.getColor())
                    .title(item.getTitle())
                    .count(item.getCount())
                    .addTo(this);
        }
    }
}