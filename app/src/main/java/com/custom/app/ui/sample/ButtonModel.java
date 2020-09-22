package com.custom.app.ui.sample;

import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.DelayedClickListener;
import com.custom.app.R;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;
import static com.core.app.util.Constants.TYPE_MAX_WIDTH;

@EpoxyModelClass(layout = R.layout.button_model)
public abstract class ButtonModel extends EpoxyModelWithHolder<ButtonModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String title;
    @EpoxyAttribute(DoNotHash) DelayedClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.button.setText(title);
            holder.button.setOnClickListener(clickListener);
        }
    }

    @Override
    protected int getViewType() {
        return TYPE_MAX_WIDTH;
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.button) Button button;

    }
}