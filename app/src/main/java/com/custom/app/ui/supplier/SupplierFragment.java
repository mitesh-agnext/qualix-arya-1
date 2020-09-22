package com.custom.app.ui.supplier;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.app.ui.base.BaseFragment;
import com.base.app.ui.custom.CustomSpinnerAdapter;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.supplier.SupplierItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.KEY_CATEGORY_ID;
import static com.custom.app.util.Constants.KEY_CENTER_ID;
import static com.custom.app.util.Constants.KEY_DEVICE_TYPE;
import static com.custom.app.util.Constants.KEY_END_DATE;
import static com.custom.app.util.Constants.KEY_REGION_ID;
import static com.custom.app.util.Constants.KEY_START_DATE;

public class SupplierFragment extends BaseFragment implements SupplierView {

    private Unbinder unbinder;
    private SupplierController controller;

    private List<CommodityItem> commodities = new ArrayList<>();
    private String categoryId, commodityId, startDate, endDate;

    @Inject
    SupplierPresenter presenter;

    @BindView(R.id.sp_commodity)
    Spinner spCommodity;

    @OnItemSelected(R.id.sp_commodity)
    void onCommoditySelected(int selectedCommodityIndex) {
        commodityId = commodities.get(selectedCommodityIndex).getId();

        if (!TextUtils.isEmpty(commodityId) && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            presenter.fetchSuppliers(commodityId, "25", startDate, endDate);
        }
    }

    @BindView(R.id.emptyView)
    View emptyView;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;

    public static SupplierFragment newInstance(String categoryId, String startDate,
                                               String endDate, String... filter) {
        SupplierFragment fragment = new SupplierFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CATEGORY_ID, categoryId);
        args.putString(KEY_START_DATE, startDate);
        args.putString(KEY_END_DATE, endDate);

        if (filter.length > 1) {
            args.putString(KEY_CENTER_ID, filter[0]);
            args.putString(KEY_DEVICE_TYPE, filter[1]);
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
        View rootView = inflater.inflate(R.layout.fragment_supplier, container, false);
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
            String centerId = getArguments().getString(KEY_CENTER_ID);
            String deviceType = getArguments().getString(KEY_DEVICE_TYPE);

            presenter.fetchCommodity(categoryId);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context()));
        recyclerView.setHasFixedSize(true);

        controller = new SupplierController(this);
        recyclerView.setAdapter(controller.getAdapter());
    }

    @Override
    public void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showSnackBar(recyclerView, msg);
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
    public void showList(List<SupplierItem> suppliers) {
        controller.setList(suppliers);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(SupplierItem supplier) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}