package com.custom.app.ui.business;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.app.ui.base.BaseFragment;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.ui.custom.SpannyText;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.business.CommodityRateItem;
import com.custom.app.data.model.business.PerDayAccepted;
import com.custom.app.data.model.graph.GraphItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.KEY_CATEGORY_ID;
import static com.custom.app.util.Constants.KEY_CENTER_ID;
import static com.custom.app.util.Constants.KEY_DEVICE_SERIAL_NO;
import static com.custom.app.util.Constants.KEY_DEVICE_TYPE;
import static com.custom.app.util.Constants.KEY_END_DATE;
import static com.custom.app.util.Constants.KEY_QUANTITY_DETAIL;
import static com.custom.app.util.Constants.KEY_START_DATE;
import static com.custom.app.util.Constants.KEY_TOTAL_QUANTITY;

public class BusinessFragment extends BaseFragment implements BusinessView {

    private Unbinder unbinder;

    @Inject
    BusinessPresenter presenter;

    @BindView(R.id.tv_centers)
    TextView tvCenters;

    @BindView(R.id.chart_centers)
    LineChart chartCenters;

    @BindView(R.id.rv_centers)
    EmptyRecyclerView rvCenters;

    @BindView(R.id.tv_quality)
    TextView tvQuality;

    @BindView(R.id.chart_quality)
    LineChart chartQuality;

    @BindView(R.id.rv_quality)
    EmptyRecyclerView rvQuality;

    @BindView(R.id.tv_tests)
    TextView tvTests;

    @BindView(R.id.chart_tests)
    LineChart chartTests;

    @BindView(R.id.rv_tests)
    EmptyRecyclerView rvTests;

    @BindView(R.id.tv_rate)
    TextView tvRate;

    @BindView(R.id.chart_rate)
    LineChart chartRate;

    @BindView(R.id.rv_rate)
    EmptyRecyclerView rvRate;

    String quantity = "";

    public static BusinessFragment newInstance(QuantityDetailRes detail, String categoryId, String startDate, String endDate, String... filter) {

        BusinessFragment fragment = new BusinessFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_QUANTITY_DETAIL, Parcels.wrap(detail));
        args.putString(KEY_CATEGORY_ID, categoryId);
        args.putString(KEY_START_DATE, startDate);
        args.putString(KEY_END_DATE, endDate);

        if (filter.length > 1) {
            args.putString(KEY_CENTER_ID, filter[0]);
            args.putString(KEY_DEVICE_TYPE, filter[1]);
            args.putString(KEY_DEVICE_SERIAL_NO, filter[2]);
            args.putString(KEY_TOTAL_QUANTITY, filter[3]);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((CustomApp) getActivity().getApplication()).getHomeComponent().inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_business, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getArguments() != null) {
            QuantityDetailRes detail = Parcels.unwrap(getArguments().getParcelable(KEY_QUANTITY_DETAIL));
            String categoryId = getArguments().getString(KEY_CATEGORY_ID);
            String startDate = getArguments().getString(KEY_START_DATE);
            String endDate = getArguments().getString(KEY_END_DATE);
            String centerId = getArguments().getString(KEY_CENTER_ID);
            String deviceType = getArguments().getString(KEY_DEVICE_TYPE);
            String deviceSerialNo = getArguments().getString(KEY_DEVICE_SERIAL_NO);
            if (centerId != null) {
                quantity = getArguments().getString(KEY_TOTAL_QUANTITY);
            }
            else {
                quantity = detail.getTotalQuantity().toString();
            }
            if (detail != null) {
                showCollection(detail);
            }

            if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
                presenter.fetchScanCount(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo);
                presenter.fetchVarianceAvg(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo);
                presenter.fetchAcceptedAvg(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo);
            } else {
                presenter.fetchScanCount(categoryId, startDate, endDate);
                presenter.fetchVarianceAvg(categoryId, startDate, endDate);
                presenter.fetchAcceptedAvg(categoryId, startDate, endDate);
            }
        }
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void showCollection(QuantityDetailRes detail) {

        tvCenters.setText(new SpannyText()
                .append("Collections")
                .append("\n")
                .append(quantity, new RelativeSizeSpan(1.4f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append(detail.getQuantityUnit(), new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.blue))));
        rvCenters.setLayoutManager(new LinearLayoutManager(context()));
        rvCenters.setHasFixedSize(true);

        BusinessController controller = new BusinessController(this);
        rvCenters.setAdapter(controller.getAdapter());

        if (detail.getCollection() != null) {
            List<CommodityRateItem> commodities = detail.getCollection().getCommodities();
            if (commodities != null && !commodities.isEmpty()) {
                List<GraphItem> centers = new ArrayList<>();
                for (CommodityRateItem item : commodities) {
                    centers.add(new GraphItem(item.getCommodityName(), String.format("%s %s", item.getTotal(), item.getUnit())));
                }
                controller.setList(centers);
                rvCenters.setVisibility(View.VISIBLE);
            }

            Map<String, Float> data = detail.getCollection().getCommulativeDailyData();
            if (data != null && !data.isEmpty()) {
                List<Float> chartData = new ArrayList<>(data.values());
                setLineChartData(chartCenters, ContextCompat.getColor(context(), R.color.blue), chartData);
            }
        }
    }

    @Override
    public void showScanCount(AcceptedAvgRes detail) {
        tvTests.setText(new SpannyText()
                .append("Tests")
                .append("\n")
                .append(String.valueOf(detail.getTotal_scan_count()),
                        new RelativeSizeSpan(1.4f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Samples", new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.actual_green))));
        rvTests.setLayoutManager(new LinearLayoutManager(context()));
        rvTests.setHasFixedSize(true);

        BusinessController controller = new BusinessController(this);
        rvTests.setAdapter(controller.getAdapter());

        List<CommodityRateItem> commodityRates = detail.getCommodityScan();
        if (commodityRates != null && !commodityRates.isEmpty()) {
            List<GraphItem> tests = new ArrayList<>();
            for (CommodityRateItem rate : commodityRates) {
                tests.add(new GraphItem(rate.getCommodityName(), String.format("+%s%%", rate.getScanCount())));
            }
            controller.setList(tests);
            rvTests.setVisibility(View.VISIBLE);
        }

        List<PerDayAccepted> acceptedRates = detail.getPerDayAccepted();
        if (acceptedRates != null && !acceptedRates.isEmpty()) {
            List<Float> chartData = new ArrayList<>();
            for (PerDayAccepted rate : acceptedRates) {
                chartData.add(rate.getScanCount());
            }
            setLineChartData(chartTests, ContextCompat.getColor(context(), R.color.actual_green), chartData);
        }
    }

    @Override
    public void showVarianceAvg(AcceptedAvgRes detail) {
        tvQuality.setText(new SpannyText()
                .append("Quality")
                .append("\n")
                .append(String.format("%s%%", detail.getTotalAvgVarianceRate()),
                        new RelativeSizeSpan(1.4f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Variance", new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.red))));
        rvQuality.setLayoutManager(new LinearLayoutManager(context()));
        rvQuality.setHasFixedSize(true);

        BusinessController controller = new BusinessController(this);
        rvQuality.setAdapter(controller.getAdapter());

        List<CommodityRateItem> commodityRates = detail.getCommodityVariance();
        if (commodityRates != null && !commodityRates.isEmpty()) {
            List<GraphItem> qualities = new ArrayList<>();
            for (CommodityRateItem rate : commodityRates) {
                qualities.add(new GraphItem(rate.getCommodityName(), String.format("+%s%%", rate.getVariance())));
            }
            controller.setList(qualities);
            rvQuality.setVisibility(View.VISIBLE);
        }

        List<PerDayAccepted> acceptedRates = detail.getPerDayVariance();
        if (acceptedRates != null && !acceptedRates.isEmpty()) {
            List<Float> chartData = new ArrayList<>();
            for (PerDayAccepted rate : acceptedRates) {
                chartData.add(rate.getAvgVarianceRate());
            }
            setLineChartData(chartQuality, ContextCompat.getColor(context(), R.color.yellow), chartData);
        }
    }

    @Override
    public void showAcceptedAvg(AcceptedAvgRes detail) {
        tvRate.setText(new SpannyText()
                .append("Rate")
                .append("\n")
                .append(String.format("%s%%", detail.getTotalAvgAcceptedRate()),
                        new RelativeSizeSpan(1.4f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Accepted", new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.yellow))));
        rvRate.setLayoutManager(new LinearLayoutManager(context()));
        rvRate.setHasFixedSize(true);

        BusinessController controller = new BusinessController(this);
        rvRate.setAdapter(controller.getAdapter());

        List<CommodityRateItem> commodityRates = detail.getCommodityAcceptedRate();
        if (commodityRates != null && !commodityRates.isEmpty()) {
            List<GraphItem> rates = new ArrayList<>();
            for (CommodityRateItem rate : commodityRates) {
                rates.add(new GraphItem(rate.getCommodityName(), String.format("+%s%%", rate.getAcceptance())));
            }
            controller.setList(rates);
            rvRate.setVisibility(View.VISIBLE);
        }

        List<PerDayAccepted> acceptedRates = detail.getPerDayAccepted();
        if (acceptedRates != null && !acceptedRates.isEmpty()) {
            List<Float> chartData = new ArrayList<>();
            for (PerDayAccepted rate : acceptedRates) {
                chartData.add(rate.getAvgAcceptanceRate());
            }
            setLineChartData(chartRate, ContextCompat.getColor(context(), R.color.yellow), chartData);
        }
    }

    private void setLineChartData(LineChart chart, @ColorInt int color, List<Float> chartData) {
        ArrayList<Entry> entries = new ArrayList<>();

        int i = 0;
        for (float value : chartData) {
            entries.add(new Entry(i, value));
            i++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "");

        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setCubicIntensity(0.5f);
        dataSet.setDrawFilled(false);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setHighLightColor(color);
        dataSet.setColor(ColorUtils.blendARGB(color, Color.BLACK, 0.1f));
        dataSet.setFillFormatter((lineDataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());

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
        chart.animateX(1500);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}