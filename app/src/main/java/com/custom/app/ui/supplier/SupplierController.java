package com.custom.app.ui.supplier;

import com.airbnb.epoxy.AutoModel;
import com.base.app.ui.epoxy.BaseEpoxy;
import com.custom.app.data.model.supplier.SupplierItem;

import java.util.List;

public class SupplierController extends BaseEpoxy {

    @AutoModel SupplierHeaderModel_ headerSupplier;

    private SupplierView view;
    private List<SupplierItem> suppliers;

    public SupplierController(SupplierView view) {
        this.view = view;
    }

    void setList(List<SupplierItem> suppliers) {
        this.suppliers = suppliers;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        if (suppliers != null && !suppliers.isEmpty()) {
            headerSupplier
                    .addTo(this);

            for (SupplierItem item : suppliers) {
                new SupplierItemModel_()
                        .id(suppliers.indexOf(item) + 1)
                        .view(view)
                        .name(item.getFarmerName())
                        .phone(item.getPhoneNumber())
                        .area(item.getArea())
                        .center(item.getInstCenterId())
                        .average(item.getAvgCollection())
                        .clickListener(itemView -> view.onItemClicked(item))
                        .addTo(this);
            }
        }
    }
}