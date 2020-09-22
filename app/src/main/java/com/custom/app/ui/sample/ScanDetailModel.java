package com.custom.app.ui.sample;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import butterknife.BindView;

@EpoxyModelClass(layout = R.layout.layout_scan_detail)
public abstract class ScanDetailModel extends EpoxyModelWithHolder<ScanDetailModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String lotId;
    @EpoxyAttribute String sampleId;
    @EpoxyAttribute String location;
    @EpoxyAttribute String commodity;
    @EpoxyAttribute String areaCovered;
    @EpoxyAttribute Double weight;
    @EpoxyAttribute String truckNumber;
    @EpoxyAttribute String quantityUnit;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(location)) {
            holder.location.setText(location);
            holder.location.setVisibility(View.VISIBLE);
            holder.titleLocation.setVisibility(View.VISIBLE);
        } else {
            holder.location.setVisibility(View.GONE);
            holder.titleLocation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(commodity)) {
            holder.commodity.setText(commodity);
            holder.commodity.setVisibility(View.VISIBLE);
            holder.titleCommodity.setVisibility(View.VISIBLE);
        } else {
            holder.commodity.setVisibility(View.GONE);
            holder.titleCommodity.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(lotId)) {
            holder.lot.setText(lotId);
        }
        if (!TextUtils.isEmpty(sampleId)) {
            holder.sample.setText(sampleId);
        }
        if (!TextUtils.isEmpty(areaCovered)) {
            holder.etArea.setText(String.format("%s ha", areaCovered));
            holder.tilArea.setVisibility(View.VISIBLE);
        } else {
            holder.tilArea.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(truckNumber)) {
            holder.truck.setText(truckNumber);
        }
        if (weight != null) {
            holder.quantity.setText(String.format(Locale.getDefault(), "%.2f %s", weight, quantityUnit));
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.title_location) TextView titleLocation;
        @BindView(R.id.tv_location) TextView location;
        @BindView(R.id.title_commodity) TextView titleCommodity;
        @BindView(R.id.tv_commodity) TextView commodity;
        @BindView(R.id.et_lot_id) EditText lot;
        @BindView(R.id.et_truck_no) EditText truck;
        @BindView(R.id.til_area) TextInputLayout tilArea;
        @BindView(R.id.et_area) EditText etArea;
        @BindView(R.id.et_sample_id) EditText sample;
        @BindView(R.id.et_quantity) EditText quantity;

    }
}