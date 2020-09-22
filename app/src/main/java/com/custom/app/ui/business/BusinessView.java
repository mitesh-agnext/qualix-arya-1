package com.custom.app.ui.business;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.quantity.QuantityDetailRes;

interface BusinessView extends BaseView {

    void showCollection(QuantityDetailRes detail);

    void showVarianceAvg(AcceptedAvgRes detail);

    void showScanCount(AcceptedAvgRes detail);

    void showAcceptedAvg(AcceptedAvgRes detail);

}