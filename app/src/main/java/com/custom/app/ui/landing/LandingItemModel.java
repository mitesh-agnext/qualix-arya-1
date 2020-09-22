package com.custom.app.ui.landing;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.core.app.ui.custom.SpannyText;
import com.custom.app.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_landing_list)
public abstract class LandingItemModel extends EpoxyModelWithHolder<LandingItemModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute String centerName;
    @EpoxyAttribute String centerType;
    @EpoxyAttribute String deviceType;
    @EpoxyAttribute String totalQuantity;
    @EpoxyAttribute String quantityUnit;
    @EpoxyAttribute String change;
    @EpoxyAttribute Map<String, Float> chartData;
    @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull Holder holder) {
        SpannyText title = new SpannyText();
        if (!TextUtils.isEmpty(centerName)) {
            title.append(centerName);
        }

        if (!TextUtils.isEmpty(centerType)) {
            title.append("\n");
            title.append(centerType);

            if (!TextUtils.isEmpty(deviceType)) {
                title.append(" | ");
                title.append(deviceType);
            }
        }

        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

        if (!TextUtils.isEmpty(totalQuantity)) {
            holder.quantity.setText(String.format("%s %s", totalQuantity, quantityUnit));
        }

        if (!TextUtils.isEmpty(change)) {
            holder.change.setText(String.format("%s%%", change));
        }

        if (chartData != null && !chartData.isEmpty()) {
            setLineChartData(holder.chart, chartData);
        }

        holder.getItemView().setOnClickListener(clickListener);
    }

    private void setLineChartData(LineChart chart, Map<String, Float> chartData) {
        ArrayList<Entry> entries = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Float> entry : chartData.entrySet()) {
            if (entry.getValue() != null) {
                entries.add(new Entry(i, entry.getValue()));
                i++;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");

        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setCubicIntensity(0.5f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setHighLightColor(Color.RED);
        dataSet.setColor(ColorUtils.blendARGB(Color.RED, Color.WHITE, 0.1f));
        dataSet.setFillFormatter((lineDataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());

        Drawable drawable = ContextCompat.getDrawable(view.context(), R.drawable.gradient_chart);
        dataSet.setFillDrawable(drawable);

        LineData data = new LineData(dataSet);
        data.setDrawValues(false);

        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.getDescription().setEnabled(false);

        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setTouchEnabled(false);

        chart.setData(data);
        chart.invalidate();
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.line_chart) LineChart chart;
        @BindView(R.id.tv_quantity) TextView quantity;
        @BindView(R.id.tv_change) TextView change;

    }
}