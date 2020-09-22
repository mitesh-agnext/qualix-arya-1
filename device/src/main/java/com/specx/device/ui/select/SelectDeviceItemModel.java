package com.specx.device.ui.select;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.specx.device.R2;

import java.util.Locale;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R2.layout.item_select_device)
public abstract class SelectDeviceItemModel extends EpoxyModelWithHolder<SelectDeviceItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String name;
    @EpoxyAttribute String mac;
    @EpoxyAttribute int rssi;
    @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        if (!TextUtils.isEmpty(mac)) {
            holder.mac.setText(mac);
        }

        holder.rssi.setText(String.format(Locale.getDefault(), "%d dB", rssi));

        holder.getItemView().setOnClickListener(clickListener);
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tv_name) TextView name;
        @BindView(R2.id.tv_mac) TextView mac;
        @BindView(R2.id.tv_rssi) TextView rssi;

    }
}