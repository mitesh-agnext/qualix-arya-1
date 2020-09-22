package com.custom.app.ui.sample;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.epoxy.BaseHolder;
import com.custom.app.R;

import butterknife.BindView;

import static com.core.app.util.Constants.TYPE_MAX_WIDTH;

@EpoxyModelClass(layout = R.layout.header_title_model)
public abstract class HeaderTitleModel extends EpoxyModelWithHolder<HeaderTitleModel.Holder> {

    @EpoxyAttribute String title;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }
    }

    @Override
    protected int getViewType() {
        return TYPE_MAX_WIDTH;
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_title) TextView title;

    }
}