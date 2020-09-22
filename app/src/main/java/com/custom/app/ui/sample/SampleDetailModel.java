package com.custom.app.ui.sample;

import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import butterknife.BindView;

@EpoxyModelClass(layout = R.layout.layout_sample_detail)
public abstract class SampleDetailModel extends EpoxyModelWithHolder<SampleDetailModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String lotId;
    @EpoxyAttribute String commodity;
    @EpoxyAttribute String sampleId;
    @EpoxyAttribute Double weight;
    @EpoxyAttribute String truckNumber;
    @EpoxyAttribute String quantityUnit;
    @EpoxyAttribute TextWatcher lotWatcher;
    @EpoxyAttribute TextWatcher truckWatcher;
    @EpoxyAttribute TextWatcher sampleWatcher;
    @EpoxyAttribute TextWatcher quantityWatcher;
    @EpoxyAttribute ArrayAdapter<CharSequence> adapter;
    @EpoxyAttribute AdapterView.OnItemClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(commodity)) {
            holder.commodity.setText(commodity);
            holder.commodity.setVisibility(View.VISIBLE);
            holder.titleCommodity.setVisibility(View.VISIBLE);
        } else {
            holder.commodity.setVisibility(View.GONE);
            holder.titleCommodity.setVisibility(View.GONE);
        }

        holder.lot.setText(lotId);
        if (setText(holder.sample, sampleId)) {
            holder.sample.setSelection(holder.sample.length());
        }
        holder.unit.setAdapter(adapter);
        if (!TextUtils.isEmpty(quantityUnit)) {
            holder.unit.setText(quantityUnit, false);
        }
        if (!TextUtils.isEmpty(truckNumber)) {
            holder.truck.setText(truckNumber);
        }
        holder.quantity.setText(String.valueOf(weight));
        holder.unit.setOnItemClickListener(clickListener);
        holder.lot.addTextChangedListener(lotWatcher);
        holder.truck.addTextChangedListener(truckWatcher);
        holder.sample.addTextChangedListener(sampleWatcher);
        holder.quantity.addTextChangedListener(quantityWatcher);
    }

    public boolean setText(TextView textView, @Nullable CharSequence text) {
        if (!isTextDifferent(text, textView.getText())) {
            return false;
        }
        textView.setText(text);
        return true;
    }

    private boolean isTextDifferent(@Nullable CharSequence str1, @Nullable CharSequence str2) {
        if (str1 == str2) return false;
        if ((str1 == null) || (str2 == null)) return true;
        final int length = str1.length();
        if (length != str2.length()) return true;
        if (str1 instanceof Spanned) return !str1.equals(str2);
        for (int i = 0; i < length; i++) if (str1.charAt(i) != str2.charAt(i)) return true;
        return false;
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.title_commodity) TextView titleCommodity;
        @BindView(R.id.tv_commodity) TextView commodity;
        @BindView(R.id.et_lot_id) EditText lot;
        @BindView(R.id.et_truck_no) EditText truck;
        @BindView(R.id.et_sample_id) EditText sample;
        @BindView(R.id.et_quantity) EditText quantity;
        @BindView(R.id.et_unit) AutoCompleteTextView unit;

    }
}