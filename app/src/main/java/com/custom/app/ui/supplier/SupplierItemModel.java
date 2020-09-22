package com.custom.app.ui.supplier;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_supplier_list)
public abstract class SupplierItemModel extends EpoxyModelWithHolder<SupplierItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String name;
    @EpoxyAttribute String phone;
    @EpoxyAttribute String area;
    @EpoxyAttribute String center;
    @EpoxyAttribute String average;
    @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        holder.index.setText(String.valueOf(id()));

        if (!TextUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        if (!TextUtils.isEmpty(phone)) {
            holder.phone.setText(phone);
        }

        if (!TextUtils.isEmpty(area)) {
            holder.area.setText(area);
        }

        if (!TextUtils.isEmpty(center)) {
            holder.center.setText(center);
        }

        if (!TextUtils.isEmpty(average)) {
            holder.average.setText(average);
        }

        holder.getItemView().setOnClickListener(clickListener);
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_index) TextView index;
        @BindView(R.id.tv_name) TextView name;
        @BindView(R.id.tv_phone) TextView phone;
        @BindView(R.id.tv_area) TextView area;
        @BindView(R.id.tv_center) TextView center;
        @BindView(R.id.tv_average) TextView average;

    }
}