package com.printer.app;

import com.base.app.ui.epoxy.BaseEpoxy;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

import java.util.List;

public class SelectPrinterController extends BaseEpoxy {

    private SelectPrinterView view;
    private List<ScanResult> printers;

    SelectPrinterController(SelectPrinterView view) {
        this.view = view;
    }

    void setList(List<ScanResult> printers) {
        this.printers = printers;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (ScanResult result : printers) {
            RxBleDevice printer = result.getBleDevice();
            new SelectPrinterItemModel_()
                    .view(view)
                    .id(printer.getMacAddress())
                    .name(printer.getName())
                    .mac(printer.getMacAddress())
                    .rssi(result.getRssi())
                    .clickListener(itemView -> view.onItemClicked(printer))
                    .addTo(this);
        }
    }
}