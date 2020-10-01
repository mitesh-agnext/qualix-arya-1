package com.custom.app.ui.quality;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.base.app.ui.base.BaseFragment;
import com.base.app.ui.custom.CustomSpinnerAdapter;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.quality.QualityMapItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.custom.app.util.Constants.KEY_CATEGORY_ID;
import static com.custom.app.util.Constants.KEY_CENTER_ID;
import static com.custom.app.util.Constants.KEY_DEVICE_SERIAL_NO;
import static com.custom.app.util.Constants.KEY_DEVICE_TYPE;
import static com.custom.app.util.Constants.KEY_END_DATE;
import static com.custom.app.util.Constants.KEY_START_DATE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

public class QualityMapFragment extends BaseFragment implements QualityMapView, OnMapReadyCallback {

    private GoogleMap map;
    private Unbinder unbinder;
    private List<CommodityItem> commodities = new ArrayList<>();
    private List<AnalyticItem> analyses = new ArrayList<>();
    private String categoryId, commodityId, analysisName, startDate, endDate;

    @Inject
    QualityPresenter presenter;

    @BindView(R.id.sp_commodity)
    Spinner spCommodity;

    @OnItemSelected(R.id.sp_commodity)
    void onCommoditySelected(int selectedCommodityIndex) {
        commodityId = commodities.get(selectedCommodityIndex).getId();

        analysisName = null;
        spAnalysis.setVisibility(View.INVISIBLE);

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

    @BindView(R.id.fab_map)
    FloatingActionButton fabMap;

    @OnClick(R.id.fab_map)
    public void changeMapType() {
        fabMap.setRotation(0);
        ViewCompat.animate(fabMap).withLayer().rotation(360).setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        map.setMapType(map.getMapType() == MAP_TYPE_NORMAL ? MAP_TYPE_SATELLITE : MAP_TYPE_NORMAL);
    }

    public static QualityMapFragment newInstance(String categoryId, String startDate,
                                                 String endDate, String... filter) {
        QualityMapFragment fragment = new QualityMapFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_quality_map, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getArguments() != null) {
            categoryId = getArguments().getString(KEY_CATEGORY_ID);
            startDate = getArguments().getString(KEY_START_DATE);
            endDate = getArguments().getString(KEY_END_DATE);
            String centerId = getArguments().getString(KEY_CENTER_ID);
            String deviceType = getArguments().getString(KEY_DEVICE_TYPE);
            String deviceSerialNo = getArguments().getString(KEY_DEVICE_SERIAL_NO);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void showProgressBar() {
        map.clear();
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(30, 30, 30, 30);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        presenter.fetchCommodity(categoryId);

        fabMap.show();
    }

    @Override
    public void onDateRangeSelected() {
        if (!TextUtils.isEmpty(commodityId) && !TextUtils.isEmpty(analysisName) &&
                !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            presenter.fetchQualityMap(commodityId, analysisName, startDate, endDate);
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
    public void showQualityMap(List<QualityMapItem> qualities) {

        for (QualityMapItem item : qualities) {
            List<LatLng> points = item.getCoordinates();
            if (points != null && !points.isEmpty()) {
                String quality = String.format("Avg Quality: %s%%", item.getAvg_quality());
                drawPolygon(item.getCenter_name(), quality, points,
                        ContextCompat.getColor(context(), R.color.actual_green));

                if (qualities.indexOf(item) == 0) {
                    LatLngBounds.Builder pathBounds = new LatLngBounds.Builder();
                    for (LatLng point : points) {
                        pathBounds.include(point);
                    }

                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(pathBounds.build(), 100), 2000, null);
                }
            }
        }
    }

    private void drawPolygon(String title, String desc, List<LatLng> points, int color) {
        if (points.size() > 2) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .fillColor(Color.argb(10, Color.red(color), Color.green(color), Color.blue(color)))
                    .strokeColor(Color.argb(10, 0, 0, 0))
                    .addAll(points)
                    .strokeWidth(5);
            map.addPolygon(polygonOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : points) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(convertAreaToBitmap(SphericalUtil.computeArea(points))))
                    .title(title)
                    .snippet(desc)
                    .position(bounds.getCenter())
                    .anchor(0.5f, 0.5f)
                    .draggable(false)
                    .zIndex(100)
                    .flat(false));
        }
    }

    private Bitmap convertAreaToBitmap(double area) {
        View customView = ((LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.layout_plot_area, null);
        TextView tvArea = customView.findViewById(R.id.tv_area);
        tvArea.setText(String.format(Locale.getDefault(), "%.2f ac", area * 0.000247105));
        customView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(customView.getMeasuredWidth(),
                customView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        customView.layout(0, 0, customView.getMeasuredWidth(), customView.getMeasuredHeight());
        customView.draw(canvas);
        return bitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}