package com.printer.app;

import com.base.app.ui.base.BaseView;
import com.polidea.rxandroidble2.RxBleDevice;

interface SelectPrinterView extends BaseView {

    void onItemClicked(RxBleDevice printer);

}