package com.custom.app.ui.home;

import androidx.annotation.NonNull;

import com.custom.app.data.model.count.customer.TotalCustomerRes;
import com.custom.app.data.model.count.device.TotalDeviceRes;
import com.custom.app.data.model.count.order.PurchaseOrderRes;
import com.custom.app.data.model.count.user.TotalUserRes;

class HomeParser {

    @NonNull
    static TotalDeviceRes device(TotalDeviceRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static TotalCustomerRes customer(TotalCustomerRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static TotalUserRes user(TotalUserRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static PurchaseOrderRes order(PurchaseOrderRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }
}