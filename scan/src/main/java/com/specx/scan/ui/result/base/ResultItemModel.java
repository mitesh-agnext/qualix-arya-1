package com.specx.scan.ui.result.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.media.app.GlideApp;
import com.specx.scan.R;
import com.specx.scan.R2;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R2.layout.item_scan_result)
public abstract class ResultItemModel extends EpoxyModelWithHolder<ResultItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String analysis;
    @EpoxyAttribute String totalAmount;
    @EpoxyAttribute boolean isAdulterant;
    @EpoxyAttribute String amountUnit;
    @EpoxyAttribute String thumbUrl;
    @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(analysis) && !TextUtils.isEmpty(totalAmount)) {
            holder.analysis.setText(analysis.toUpperCase());

            if (isAdulterant) {
                if (totalAmount.equals("1")) {
                    int redColor = ContextCompat.getColor(view.context(), R.color.light_red);
                    holder.check.setImageDrawable(new ColorDrawable(redColor));
                } else {
                    holder.check.setImageDrawable(new ColorDrawable(Color.GREEN));
                }

                holder.check.setVisibility(View.VISIBLE);
            } else {
                SpannyText spanny = new SpannyText();
                if (totalAmount.startsWith("-")) {
                    spanny.append("??", new ForegroundColorSpan(ContextCompat.getColor(view.context(), R.color.light_red)));
                } else {
                    spanny.append(totalAmount);

                    if (!TextUtils.isEmpty(amountUnit)) {
                        spanny.append(amountUnit);
                    }
                }

                holder.result.setText(spanny);
                holder.check.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(thumbUrl)) {
            GlideApp.with(view.context()).load(thumbUrl).into(holder.thumb);
            holder.thumb.setVisibility(View.VISIBLE);
        } else {
            holder.thumb.setVisibility(View.GONE);
        }

        holder.getItemView().setOnClickListener(clickListener);
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.iv_thumb) ImageView thumb;
        @BindView(R2.id.tv_analysis) TextView analysis;
        @BindView(R2.id.tv_result) TextView result;
        @BindView(R2.id.iv_check) ImageView check;

    }
}