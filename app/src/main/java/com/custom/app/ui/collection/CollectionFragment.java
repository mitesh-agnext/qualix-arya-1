package com.custom.app.ui.collection;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.base.app.ui.base.BaseFragment;
import com.base.app.ui.custom.CustomSpinnerAdapter;
import com.core.app.ui.custom.SpannyText;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.graph.GraphItem;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionByCenterRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionCenterRegionRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionWeeklyMonthlyRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.QuantityRes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.chip.ChipGroup;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.KEY_CATEGORY_ID;
import static com.custom.app.util.Constants.KEY_CENTER_ID;
import static com.custom.app.util.Constants.KEY_DEVICE_SERIAL_NO;
import static com.custom.app.util.Constants.KEY_DEVICE_TYPE;
import static com.custom.app.util.Constants.KEY_END_DATE;
import static com.custom.app.util.Constants.KEY_REGION_ID;
import static com.custom.app.util.Constants.KEY_START_DATE;

public class CollectionFragment extends BaseFragment implements CollectionView {

    private Unbinder unbinder;
    private Random random = new Random();
    private List<CommodityItem> commodities = new ArrayList<>();
    private List<AnalyticItem> analyses = new ArrayList<>();
    private String categoryId, commodityId, analysisName, startDate, endDate, centerId, deviceType, deviceSerialNo;

    @Inject
    CollectionPresenter presenter;

    @BindView(R.id.sp_commodity)
    Spinner spCommodity;

    @OnItemSelected(R.id.sp_commodity)
    void onCommoditySelected(int selectedCommodityIndex) {
        commodityId = commodities.get(selectedCommodityIndex).getId();

        analysisName = null;
        spAnalysis.setVisibility(View.INVISIBLE);

        if (!TextUtils.isEmpty(commodityId) && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
                presenter.fetchQuantity(commodityId, startDate, endDate, centerId, deviceType, deviceSerialNo);
                presenter.fetchCollectionByCenter(commodityId, startDate, endDate, centerId, deviceType, deviceSerialNo);
                presenter.fetchCollectionOverTime(commodityId, startDate, endDate, centerId, deviceType, deviceSerialNo);
//                presenter.fetchCollectionRegion(commodityId, "25", centerId, startDate, endDate, centerId, deviceType, deviceSerialNo);
                presenter.fetchCollectionWeekly(commodityId, startDate, endDate, centerId, deviceType, deviceSerialNo);
            } else {
                presenter.fetchQuantity(commodityId, startDate, endDate);
                presenter.fetchCollectionByCenter(commodityId, startDate, endDate);
                presenter.fetchCollectionOverTime(commodityId, startDate, endDate);
//                presenter.fetchCollectionRegion(commodityId, "25", centerId, startDate, endDate);
                presenter.fetchCollectionWeekly(commodityId, startDate, endDate);
            }
        }

        presenter.fetchAnalyses(commodityId);

        onDateRangeSelected();
    }

    @BindView(R.id.sp_analysis)
    Spinner spAnalysis;

    @OnItemSelected(R.id.sp_analysis)
    void onAnalysisSelected(int selectedAnalysisIndex) {
        analysisName = analyses.get(selectedAnalysisIndex).getName();

        onDateRangeSelected();
    }

    @BindView(R.id.tv_high)
    TextView tvHigh;

    @BindView(R.id.tv_low)
    TextView tvLow;

    @BindView(R.id.tv_avg)
    TextView tvAvg;

    @BindView(R.id.tv_total)
    TextView tvTotal;

    @BindView(R.id.tv_highest)
    TextView tvHighest;

    @BindView(R.id.tv_lowest)
    TextView tvLowest;

    @BindView(R.id.tv_average)
    TextView tvAverage;

    @BindView(R.id.chart_center)
    PieChart chartCenter;

    @BindView(R.id.chart_time)
    LineChart chartTime;

    @BindView(R.id.chart_quality)
    LineChart chartQuality;

//    @BindView(R.id.chart_region)
//    LineChart chartRegion;

    @BindView(R.id.chip_group)
    ChipGroup group;

    @BindView(R.id.tv_collection)
    TextView tvCollection;

    @BindView(R.id.iv_arrow)
    ImageView ivArrow;

    @BindView(R.id.chart_change)
    LineChart chartChange;

    public static CollectionFragment newInstance(String categoryId, String startDate, String endDate, String... filter) {

        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CATEGORY_ID, categoryId);
        args.putString(KEY_START_DATE, startDate);
        args.putString(KEY_END_DATE, endDate);

        if (filter.length > 1) {
            args.putString(KEY_CENTER_ID, filter[0]);
            args.putString(KEY_DEVICE_TYPE, filter[1]);
            args.putString(KEY_DEVICE_SERIAL_NO, filter[2]);
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
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getArguments() != null) {
            categoryId = getArguments().getString(KEY_CATEGORY_ID);
            String regionId = getArguments().getString(KEY_REGION_ID);
            startDate = getArguments().getString(KEY_START_DATE);
            endDate = getArguments().getString(KEY_END_DATE);
            centerId = getArguments().getString(KEY_CENTER_ID);
            deviceType = getArguments().getString(KEY_DEVICE_TYPE);
            deviceSerialNo = getArguments().getString(KEY_DEVICE_SERIAL_NO);
            presenter.fetchCommodity(categoryId);
        }
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void onDateRangeSelected() {
        if (!TextUtils.isEmpty(commodityId) && !TextUtils.isEmpty(analysisName) &&
                !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            presenter.fetchQuality(commodityId, analysisName, startDate, endDate);
            presenter.fetchQualityOverTime(commodityId, analysisName, startDate, endDate);
        }
    }

    @Override
    public void showCommodities(List<CommodityItem> commodities) {
        this.commodities = commodities;

        List<String> names = new ArrayList<>();
        for (CommodityItem commodity : commodities) {
            names.add(commodity.getName());
        }

        spCommodity.setAdapter(new CustomSpinnerAdapter(context(), false, names, v -> spCommodity.performClick()));
        spCommodity.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAnalyses(List<AnalyticItem> analyses) {
        this.analyses = analyses;

        List<String> names = new ArrayList<>();
        for (AnalyticItem analysis : analyses) {
            names.add(analysis.getName());
        }

        spAnalysis.setAdapter(new CustomSpinnerAdapter(context(), false, names, v -> spAnalysis.performClick()));
        spAnalysis.setVisibility(View.VISIBLE);
    }

    @Override
    public void showQuality(List<QualityRes> quality) {
        tvHigh.setText(new SpannyText()
                .append(String.format("%s%%", quality.get(0).getMax_quality()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Highest Quality"));

        tvLow.setText(new SpannyText()
                .append(String.format("%s%%", quality.get(0).getMin_quality()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Lowest Quality"));

        tvAvg.setText(new SpannyText()
                .append(String.format("%s%%", quality.get(0).getAvg_quality()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Average Quality"));
    }

    @Override
    public void showQuantity(QuantityRes quantity) {
        tvTotal.setText(new SpannyText()
                .append(String.format("%s %s", quantity.getTotal_quantity(), quantity.getUnit()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Total Collection"));

        tvHighest.setText(new SpannyText()
                .append(String.format("%s %s", quantity.getMax_quantity(), quantity.getUnit()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Highest Collection"));

        tvLowest.setText(new SpannyText()
                .append(String.format("%s %s", quantity.getMin_quantity(), quantity.getUnit()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Lowest Collection"));

        tvAverage.setText(new SpannyText()
                .append(String.format("%s %s", quantity.getAverage_quantity(), quantity.getUnit()),
                        new RelativeSizeSpan(1.3f),
                        new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                .append("\n")
                .append("Average Collection"));
    }

    @Override
    public void showCollection(CollectionWeeklyMonthlyRes collection) {
        group.setOnCheckedChangeListener((group, checkedId) -> {
            LocalDateTime dateTime = LocalDate.now().atTime(LocalTime.MIDNIGHT);
            String endDate = String.valueOf(dateTime.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli());
            switch (checkedId) {
                case R.id.chip_quarterly:
                    dateTime = dateTime.minusMonths(3);
                    break;
                case R.id.chip_monthly:
                    dateTime = dateTime.minusMonths(1);
                    break;
                default:
                    dateTime = dateTime.minusWeeks(1);
                    break;
            }

            String startDate = String.valueOf(dateTime.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli());
            if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
                presenter.fetchCollectionWeekly(commodityId, startDate, endDate, centerId, deviceType,deviceSerialNo);
            } else {
                presenter.fetchCollectionWeekly(commodityId, startDate, endDate);
            }
        });

        if (collection.getDifference() != null) {
            tvCollection.setText(new SpannyText()
                    .append(String.format("%s %s", collection.getDifference(), collection.getUnit()),
                            new RelativeSizeSpan(1.3f),
                            new ForegroundColorSpan(ContextCompat.getColor(context(), R.color.black)))
                    .append("\n")
                    .append(String.format("%s%% since last time", collection.getDifference_percentage())));
        }

        List<GraphItem> chartData = new ArrayList<>();
        for (CollectionOverTimeRes item : collection.getGraph_data()) {
            if (item.getWeight() != null) {
                chartData.add(new GraphItem(item.getScan_date(), item.getWeight().toString()));
            }
        }

        if (collection.getDifference_percentage() != null) {
            if (collection.getDifference_percentage().intValue() >= 0) {
                ivArrow.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                setLineChartData(chartChange, R.color.actual_green, false, chartData);
            } else {
                ivArrow.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                setLineChartData(chartChange, R.color.red, false, chartData);
            }
        }
    }

    @Override
    public void showQualityOverTime(List<QualityOverTimeRes> qualities) {
        List<GraphItem> chartData = new ArrayList<>();
        for (QualityOverTimeRes item : qualities) {
            if (item.getQuality_avg() != null && item.getScan_date() != null) {
                chartData.add(new GraphItem(item.getScan_date(), item.getQuality_avg().toString()));
            }
        }

        setLineChartData(chartQuality, R.color.orange, true, chartData);
    }

    @Override
    public void showCollectionByCenter(List<CollectionByCenterRes> collections) {
        List<GraphItem> chartData = new ArrayList<>();
        for (CollectionByCenterRes item : collections) {
            if (item.getWeight() != null) {
                chartData.add(new GraphItem(getRandomColor(collections.indexOf(item)),
                        String.format("CC-%s", item.getInst_center_id()), item.getWeight().toString()));
            }
        }

        setPieChartData(chartCenter, chartData);
    }

    private void setPieChartData(PieChart chart, List<GraphItem> chartData) {
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (GraphItem item : chartData) {
            colors.add(item.getColor());
            entries.add(new PieEntry(Float.parseFloat(item.getCount()), item.getTitle()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(true);
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(ContextCompat.getColor(context(), R.color.black));

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextColor(ContextCompat.getColor(context(), R.color.white));

        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(58f);

        chart.setData(data);
        chart.invalidate();
    }

    @Override
    public void showCollectionOverTime(List<CollectionOverTimeRes> collections) {
        List<GraphItem> chartData = new ArrayList<>();
        for (CollectionOverTimeRes item : collections) {
            if (item.getWeight() != null && item.getScan_date() != null) {
                chartData.add(new GraphItem(item.getScan_date(), item.getWeight().toString()));
            }
        }

        setLineChartData(chartTime, R.color.orange, true, chartData);
    }

//    @Override
//    public void showCollectionRegion(List<CollectionCenterRegionRes> collections) {
//        List<GraphItem> chartData = new ArrayList<>();
//        List<GraphItem> chartData1 = new ArrayList<>();
//        for (CollectionCenterRegionRes item : collections) {
//            if (item.getCenter_collection() != null) {
//                chartData.add(new GraphItem(item.getScan_date(), item.getCenter_collection().toString()));
//            }
//            if (item.getRegion_collection() != null) {
//                chartData1.add(new GraphItem(item.getScan_date(), item.getRegion_collection().toString()));
//            }
//        }
//
//        setLineChartData(chartRegion, R.color.green, true, chartData, chartData1);
//    }

    private void setLineChartData(LineChart chart, @ColorRes int colorId, boolean showAxis,
                                  List<GraphItem> chartData, List<GraphItem>... chartData1) {
        int color = ContextCompat.getColor(context(), colorId);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries1 = new ArrayList<>();

        for (GraphItem item : chartData) {
            if (item != null) {
                entries.add(new Entry(getDateEpoch(item.getTitle()), Float.parseFloat(item.getCount())));
            }
        }

        if (chartData1.length != 0) {
            for (GraphItem item : chartData1[0]) {

                    entries1.add(new Entry(getDateEpoch(item.getTitle()), Float.parseFloat(item.getCount())));
            }
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
        if (!entries1.isEmpty()) {
            dataSet.setLabel("Center");

            LineDataSet dataSet1 = new LineDataSet(entries1, "Region");

            dataSet1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet1.setCubicIntensity(0.5f);
            dataSet1.setDrawFilled(false);
            dataSet1.setDrawCircles(false);
            dataSet1.setLineWidth(2.5f);
            dataSet1.setCircleRadius(4f);
            dataSet1.setHighLightColor(Color.CYAN);
            dataSet1.setColor(ColorUtils.blendARGB(Color.CYAN, Color.BLACK, 0.1f));
            dataSet1.setFillFormatter((lineDataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());

            data.addDataSet(dataSet1);
        }

        data.setDrawValues(false);

        chart.getDescription().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(ContextCompat.getColor(context(), R.color.black));

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(showAxis);

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(showAxis);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(context(), R.color.black));

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DateTimeFormatter.ofPattern("dd MMM").format(LocalDate.ofEpochDay((long) value));
            }
        });

        chart.setTouchEnabled(false);
        chart.setData(data);
        chart.animateX(1500);
        chart.invalidate();
    }

    private long getDateEpoch(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy")).toEpochDay();
    }

    private int getRandomColor(int seed) {
        random.setSeed(seed);
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}