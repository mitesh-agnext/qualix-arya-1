package com.custom.app.ui.supplier;

import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import static com.core.app.util.Constants.TYPE_MAX_WIDTH;

@EpoxyModelClass(layout = R.layout.header_supplier_list)
public abstract class SupplierHeaderModel extends EpoxyModelWithHolder<SupplierHeaderModel.Holder> {

    @Override
    protected int getViewType() {
        return TYPE_MAX_WIDTH;
    }

    static class Holder extends BaseHolder {
    }
}