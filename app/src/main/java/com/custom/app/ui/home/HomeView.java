package com.custom.app.ui.home;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.count.customer.TotalCustomerRes;
import com.custom.app.data.model.count.device.TotalDeviceRes;
import com.custom.app.data.model.count.order.PurchaseOrderRes;
import com.custom.app.data.model.count.user.TotalUserRes;

interface HomeView extends BaseView {

    void showTotalDevices(TotalDeviceRes detail);

    void showUnassignedDevices(TotalDeviceRes detail);

    void showCustomers(TotalCustomerRes detail);

    void showUsers(TotalUserRes detail);

    void showOrders(PurchaseOrderRes detail);

}