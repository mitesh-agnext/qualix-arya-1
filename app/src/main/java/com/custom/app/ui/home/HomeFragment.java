package com.custom.app.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.base.app.ui.base.BaseFragment;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.count.customer.TotalCustomerRes;
import com.custom.app.data.model.count.device.TotalDeviceRes;
import com.custom.app.data.model.count.order.PurchaseOrderRes;
import com.custom.app.data.model.count.user.TotalUserRes;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment implements HomeView {

    private Unbinder unbinder;

    @Inject
    HomePresenter presenter;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.tv_total_device)
    TextView tvTotalDevice;

    @BindView(R.id.tv_highest_device)
    TextView tvHighestDevice;

    @BindView(R.id.tv_lowest_device)
    TextView tvLowestDevice;

    @BindView(R.id.rv_total_device)
    EmptyRecyclerView rvTotalDevice;

    @BindView(R.id.tv_total_unassigned_device)
    TextView tvTotalUnassignedDevice;

    @BindView(R.id.rv_unassigned_device)
    EmptyRecyclerView rvUnassignedDevice;

    @BindView(R.id.tv_total_customer)
    TextView tvTotalCustomer;

    @BindView(R.id.tv_total_partner)
    TextView tvTotalPartner;

    @BindView(R.id.tv_partner_customer)
    TextView tvPartnerCustomer;

    @BindView(R.id.tv_total_user)
    TextView tvTotalUser;

    @BindView(R.id.tv_service_user)
    TextView tvServiceUser;

    @BindView(R.id.tv_customer_user)
    TextView tvCustomerUser;

    @BindView(R.id.tv_partner_user)
    TextView tvPartnerUser;

    @BindView(R.id.tv_total_po)
    TextView tvTotalPo;

    @BindView(R.id.tv_current_month_po)
    TextView tvCurrentMonthPo;

    @BindView(R.id.tv_last_month_po)
    TextView tvLastMonthPo;

    @BindView(R.id.tv_device_po)
    TextView tvDevicePo;

    @BindView(R.id.tv_customer_po)
    TextView tvCustomerPo;

    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        swipeLayout.setOnRefreshListener(() -> presenter.fetchCount());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                ((TextView) toolbar.findViewById(R.id.title)).setText(getString(R.string.title_home));
            }
        }

        presenter.setView(this);
        presenter.fetchCount();
    }

    @Override
    public void showProgressBar() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showSnackBar(swipeLayout, msg);
    }

    @Override
    public void showTotalDevices(TotalDeviceRes detail) {
        tvTotalDevice.setText(String.format("Total devices: %s", detail.getTotal_device_count()));
        tvHighestDevice.setText(String.format("Highest devices: %s", detail.getHighest_device_count()));
        if (detail.getLowest_device_count() != null) {
            tvLowestDevice.setText(String.format("Lowest devices: %s", detail.getLowest_device_count()));
        }

        if (detail.getDevice_type_count() != null && !detail.getDevice_type_count().isEmpty()) {
            rvTotalDevice.setLayoutManager(new LinearLayoutManager(context()));
            rvTotalDevice.setHasFixedSize(true);

            HomeController controller = new HomeController(this);
            rvTotalDevice.setAdapter(controller.getAdapter());
            controller.setList(detail.getDevice_type_count());
            rvTotalDevice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showUnassignedDevices(TotalDeviceRes detail) {
        tvTotalUnassignedDevice.setText(String.format("Total unassigned devices: %s", detail.getTotal_device_count()));

        if (detail.getDevice_type_count() != null && !detail.getDevice_type_count().isEmpty()) {
            rvUnassignedDevice.setLayoutManager(new LinearLayoutManager(context()));
            rvUnassignedDevice.setHasFixedSize(true);

            HomeController controller = new HomeController(this);
            rvUnassignedDevice.setAdapter(controller.getAdapter());
            controller.setList(detail.getDevice_type_count());
            rvUnassignedDevice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showCustomers(TotalCustomerRes detail) {
        tvTotalCustomer.setText(String.format("Total customers: %s", detail.getTotal_customer()));
        tvTotalPartner.setText(String.format("Total partners: %s", detail.getTotal_partners()));
        tvPartnerCustomer.setText(String.format("Total customers under partners: %s", detail.getCustomers_under_partners()));
    }

    @Override
    public void showUsers(TotalUserRes detail) {
        tvTotalUser.setText(String.format("Total users: %s", detail.getTotal_users()));
        tvServiceUser.setText(String.format("Total service provider users: %s", detail.getService_provider_users()));
        tvCustomerUser.setText(String.format("Total customer users: %s", detail.getTotal_customer_users()));
        tvPartnerUser.setText(String.format("Total partners users: %s", detail.getTotal_partners_users()));
    }

    @Override
    public void showOrders(PurchaseOrderRes detail) {
        tvTotalPo.setText(String.format("Total purchase orders: %s", detail.getTotal_po_count()));
        tvCurrentMonthPo.setText(String.format("Current month PO: %s", detail.getCurrent_month_po_count()));
        tvLastMonthPo.setText(String.format("Last month PO: %s", detail.getLast_month_po_count()));

        if (detail.getDevice_type_po_count() != null) {
            tvDevicePo.setText(String.format("Device PO count: %s", detail.getDevice_type_po_count()));
        }
        if (detail.getCustomer_po_count() != null) {
            tvCustomerPo.setText(String.format("Customer PO count: %s", detail.getCustomer_po_count()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}