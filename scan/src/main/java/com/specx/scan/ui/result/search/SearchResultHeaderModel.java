package com.specx.scan.ui.result.search;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.specx.scan.R2;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import timber.log.Timber;

@EpoxyModelClass(layout = R2.layout.header_search_result)
public abstract class SearchResultHeaderModel extends EpoxyModelWithHolder<SearchResultHeaderModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String userId;
    @EpoxyAttribute String batchId;
    @EpoxyAttribute String sampleId;
    @EpoxyAttribute String datetime;
    @EpoxyAttribute String commodity;
    @EpoxyAttribute String serialNumber;

    @Override
    public void bind(@NonNull Holder holder) {
        if (!TextUtils.isEmpty(userId)) {
            SpannyText spanny = new SpannyText("User ID:")
                    .append(" ")
                    .append(userId);
            holder.user.setText(spanny);
        }

        if (!TextUtils.isEmpty(batchId)) {
            SpannyText spanny = new SpannyText("Batch ID:")
                    .append("\n")
                    .append(batchId);
            holder.batch.setText(spanny);
        }

        if (!TextUtils.isEmpty(sampleId)) {
            SpannyText spanny = new SpannyText("Sample ID:")
                    .append("\n")
                    .append(sampleId);
            holder.sample.setText(spanny);
        }

        if (!TextUtils.isEmpty(datetime)) {
            try {
                LocalDateTime date = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyMMddHHmmss"));
                holder.datetime.setText(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm").format(date));
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        if (!TextUtils.isEmpty(commodity)) {
            SpannyText spanny = new SpannyText("Commodity:")
                    .append(" ")
                    .append(commodity);
            holder.commodity.setText(spanny);
        }

        if (!TextUtils.isEmpty(serialNumber)) {
            SpannyText spanny = new SpannyText("Device ID:")
                    .append(" ")
                    .append(serialNumber);
            holder.device.setText(spanny);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tv_user) TextView user;
        @BindView(R2.id.tv_batch) TextView batch;
        @BindView(R2.id.tv_sample) TextView sample;
        @BindView(R2.id.tv_device) TextView device;
        @BindView(R2.id.tv_datetime) TextView datetime;
        @BindView(R2.id.tv_commodity) TextView commodity;

    }
}