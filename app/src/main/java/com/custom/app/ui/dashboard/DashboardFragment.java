package com.custom.app.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseFragment;
import com.custom.app.R;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.ui.business.BusinessFragment;
import com.custom.app.ui.collection.CollectionFragment;
import com.custom.app.ui.quality.QualityMapFragment;
import com.custom.app.ui.supplier.SupplierFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.BUSINESS_FRAGMENT;
import static com.custom.app.util.Constants.COLLECTION_FRAGMENT;
import static com.custom.app.util.Constants.KEY_CATEGORY_ID;
import static com.custom.app.util.Constants.KEY_CENTER_ID;
import static com.custom.app.util.Constants.KEY_DEVICE_SERIAL_NO;
import static com.custom.app.util.Constants.KEY_DEVICE_TYPE;
import static com.custom.app.util.Constants.KEY_END_DATE;
import static com.custom.app.util.Constants.KEY_QUANTITY_DETAIL;
import static com.custom.app.util.Constants.KEY_START_DATE;
import static com.custom.app.util.Constants.KEY_TOTAL_QUANTITY;
import static com.custom.app.util.Constants.QUALITY_MAP_FRAGMENT;
import static com.custom.app.util.Constants.SUPPLIER_FRAGMENT;

public class DashboardFragment extends BaseFragment implements DashboardView, OnTabSelectedListener {

    private Unbinder unbinder;

    private QuantityDetailRes detail;
    private String categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo, quantity;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    public static DashboardFragment newInstance(QuantityDetailRes detail, String categoryId, String startDate, String endDate, String... filter) {

        DashboardFragment fragment = new DashboardFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        tabLayout.addOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            detail = Parcels.unwrap(getArguments().getParcelable(KEY_QUANTITY_DETAIL));
            categoryId = getArguments().getString(KEY_CATEGORY_ID);
            startDate = getArguments().getString(KEY_START_DATE);
            endDate = getArguments().getString(KEY_END_DATE);
            centerId = getArguments().getString(KEY_CENTER_ID);
            deviceType = getArguments().getString(KEY_DEVICE_TYPE);
            deviceSerialNo = getArguments().getString(KEY_DEVICE_SERIAL_NO);
            quantity = getArguments().getString(KEY_TOTAL_QUANTITY);

            showBusinessScreen();
        }
    }

    @Override
    public void onTabSelected(Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                showBusinessScreen();
                break;
            case 1:
                showCollectionScreen();
                break;
            case 2:
                showQualityMapScreen();
                break;
            case 3:
                showSupplierScreen();
                break;
        }
    }

    @Override
    public void onTabUnselected(Tab tab) {
    }

    @Override
    public void onTabReselected(Tab tab) {
    }

    private void showBusinessScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, BusinessFragment
                    .newInstance(detail, categoryId, startDate, endDate, centerId, deviceType,deviceSerialNo, quantity), BUSINESS_FRAGMENT);
        } else {
            replaceFragment(R.id.layout_child, BusinessFragment
                    .newInstance(detail, categoryId, startDate, endDate), BUSINESS_FRAGMENT);
        }
    }

    private void showCollectionScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, CollectionFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType,deviceSerialNo), COLLECTION_FRAGMENT);
        } else {
            replaceFragment(R.id.layout_child, CollectionFragment
                    .newInstance(categoryId, startDate, endDate), COLLECTION_FRAGMENT);
        }
    }

    private void showQualityMapScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, QualityMapFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo), QUALITY_MAP_FRAGMENT);
        } else {
            replaceFragment(R.id.layout_child, QualityMapFragment
                    .newInstance(categoryId, startDate, endDate), QUALITY_MAP_FRAGMENT);
        }
    }

    private void showSupplierScreen() {
        if (!TextUtils.isEmpty(centerId) && !TextUtils.isEmpty(deviceType)) {
            replaceFragment(R.id.layout_child, SupplierFragment
                    .newInstance(categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo), SUPPLIER_FRAGMENT);
        } else {
            replaceFragment(R.id.layout_child, SupplierFragment
                    .newInstance(categoryId, startDate, endDate), SUPPLIER_FRAGMENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}