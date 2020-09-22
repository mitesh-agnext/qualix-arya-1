package com.custom.app.ui.sample;

import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import butterknife.BindView;

@EpoxyModelClass(layout = R.layout.layout_farmer_detail)
public abstract class FarmerDetailModel extends EpoxyModelWithHolder<FarmerDetailModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String name;
    @EpoxyAttribute String village;
    @EpoxyAttribute String email;
    @EpoxyAttribute String phone;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(name)) {
            holder.name.setText(name);
        }
        if (!TextUtils.isEmpty(village)) {
            holder.village.setText(village);
        }
        if (!TextUtils.isEmpty(email)) {
            holder.email.setText(email);
        }
        if (!TextUtils.isEmpty(phone)) {
            holder.phone.setText(phone);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.et_name) EditText name;
        @BindView(R.id.et_village) EditText village;
        @BindView(R.id.et_email) EditText email;
        @BindView(R.id.et_phone) EditText phone;

    }
}