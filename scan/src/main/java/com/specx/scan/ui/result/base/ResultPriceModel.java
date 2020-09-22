package com.specx.scan.ui.result.base;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.specx.scan.R2;

import butterknife.BindView;

@EpoxyModelClass(layout = R2.layout.model_result_price)
public abstract class ResultPriceModel extends EpoxyModelWithHolder<ResultPriceModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String totalAmount;
    @EpoxyAttribute String amountUnit;

    @Override
    public void bind(@NonNull Holder holder) {
        if ("-".equals(totalAmount)) {
            holder.amount.setText(totalAmount);
        } else {
            holder.amount.setText(String.format("₹ %s", totalAmount));
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tv_amount)
        TextView amount;

    }
}