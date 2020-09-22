package com.custom.app.ui.sample;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.custom.CustomSpinnerAdapter;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import java.util.List;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_spinner_model)
public abstract class SpinnerItemModel extends EpoxyModelWithHolder<SpinnerItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute List<String> names;
    @EpoxyAttribute(DoNotHash) AdapterView.OnItemSelectedListener listener;

    @Override
    public void bind(@NonNull Holder holder) {
        holder.spinner.setAdapter(new CustomSpinnerAdapter(view.context(), false, names,
                v -> holder.spinner.performClick()));

        holder.spinner.setOnItemSelectedListener(listener);
        holder.spinner.setVisibility(View.VISIBLE);
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.sp_commodity) Spinner spinner;

    }
}