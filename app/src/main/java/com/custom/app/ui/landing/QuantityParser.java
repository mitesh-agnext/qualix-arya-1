package com.custom.app.ui.landing;

import androidx.annotation.NonNull;

import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.category.CategoryDetailItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.data.model.supplier.SupplierItem;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionByCenterRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionCenterRegionRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionWeeklyMonthlyRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.QuantityRes;

import java.util.List;

class QuantityParser {

    @NonNull
    static QuantityDetailRes parse(QuantityDetailRes body) throws NullPointerException {
        if (body != null && !body.getInstCenterDetails().isEmpty()) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<CategoryDetailItem> category(List<CategoryDetailItem> categories) throws NullPointerException {
        if (categories != null && !categories.isEmpty()) {
            return categories;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static AcceptedAvgRes business(AcceptedAvgRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static QuantityRes quantity(QuantityRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<CollectionByCenterRes> center(List<CollectionByCenterRes> collections) throws NullPointerException {
        if (collections != null && !collections.isEmpty()) {
            return collections;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<CollectionOverTimeRes> time(List<CollectionOverTimeRes> collections) throws NullPointerException {
        if (collections != null && !collections.isEmpty()) {
            return collections;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<CollectionCenterRegionRes> region(List<CollectionCenterRegionRes> collections) throws NullPointerException {
        if (collections != null && !collections.isEmpty()) {
            return collections;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static CollectionWeeklyMonthlyRes weekly(CollectionWeeklyMonthlyRes body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<SupplierItem> supplier(List<SupplierItem> suppliers) throws NullPointerException {
        if (suppliers != null && !suppliers.isEmpty()) {
            return suppliers;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }
}