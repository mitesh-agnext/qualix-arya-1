package com.custom.app.ui.landing;

import com.base.app.ui.epoxy.BaseEpoxy;
import com.custom.app.data.model.quantity.CenterDetailItem;

import java.util.List;

public class LandingController extends BaseEpoxy {

    private LandingView view;
    private List<CenterDetailItem> items;

    public LandingController(LandingView view) {
        this.view = view;
    }

    void setList(List<CenterDetailItem> items) {
        this.items = items;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (CenterDetailItem item : items) {
            new LandingItemModel_()
                    .id(items.indexOf(item))
                    .view(view)
                    .centerName(item.getInstCenterName())
                    .centerType(item.getInstCenterTypeName())
                    .deviceType(item.getDeviceTypeData().size() > 0 ?
                            item.getDeviceTypeData().get(0).getDeviceTypeName() : null)
                    .deviceType(item.getDeviceTypeData().size() > 0 ?
                            item.getDeviceTypeData().get(0).getDeviceSerialNo() : null)
                    .totalQuantity(item.getTotalQuantity())
                    .quantityUnit(item.getQuantityUnit())
                    .change(item.getDifferencePercentage())
                    .chartData(item.getDeviceTypeData().size() > 0 ? item.getDeviceTypeData().get(0).getDeviceDailyData() : null)
                    .clickListener(itemView -> view.onCenterClicked(item))
                    .addTo(this);
        }
    }
}