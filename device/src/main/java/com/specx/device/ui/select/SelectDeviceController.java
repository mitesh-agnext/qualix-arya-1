package com.specx.device.ui.select;

import com.base.app.ui.epoxy.BaseEpoxy;

import java.util.List;

public class SelectDeviceController extends BaseEpoxy {

    private SelectDeviceView view;
    private List<SelectDeviceItem> devices;

    SelectDeviceController(SelectDeviceView view) {
        this.view = view;
    }

    void setList(List<SelectDeviceItem> devices) {
        this.devices = devices;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (SelectDeviceItem device : devices) {
            new SelectDeviceItemModel_()
                    .view(view)
                    .id(device.getMac())
                    .name(device.getName())
                    .mac(device.getMac())
                    .rssi(device.getRssi())
                    .clickListener(itemView -> view.onItemClicked(device))
                    .addTo(this);
        }
    }
}