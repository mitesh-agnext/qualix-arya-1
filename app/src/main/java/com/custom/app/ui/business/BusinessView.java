package com.custom.app.ui.business;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.ui.dashboard.CenterData;

interface BusinessView extends BaseView {

    void showCollection(QuantityDetailRes detail, CenterData centerData);

    void showVarianceAvg(AcceptedAvgRes detail);

    void showScanCount(AcceptedAvgRes detail);

    void showAcceptedAvg(AcceptedAvgRes detail);

}