package com.custom.app.ui.commodity;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.base.app.ui.base.BaseFragment;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.user.app.data.UserManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.core.app.util.Constants.TYPE_MAX_WIDTH;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;

public class CommodityFragment extends BaseFragment implements CommodityView {

    private int deviceId;
    private Unbinder unbinder;
    private Callback callback;
    private CommodityController controller;

    @Inject
    UserManager userManager;

    @Inject
    CommodityPresenter presenter;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.emptyView)
    View emptyView;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;

    public static CommodityFragment newInstance(int deviceId) {
        CommodityFragment fragment = new CommodityFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_DEVICE_ID, deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((CustomApp) getActivity().getApplication()).getHomeComponent().inject(this);
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_commodity, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);

        GridLayoutManager layoutManager = new GridLayoutManager(context(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (controller.getAdapter().getItemViewType(position)) {
                    case TYPE_MAX_WIDTH:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setHasFixedSize(true);

        controller = new CommodityController(this);
        recyclerView.setAdapter(controller.getAdapter());

        swipeLayout.setOnRefreshListener(() -> presenter.fetchCommodities());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            deviceId = getArguments().getInt(KEY_DEVICE_ID);
        }

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                ((TextView) toolbar.findViewById(R.id.title)).setText(getString(R.string.title_scan));
            }
        }

        presenter.setView(this);
        presenter.fetchCommodities();
    }

    @Override
    public void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        swipeLayout.setRefreshing(true);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
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
    public void showList(List<CommodityItem> commodities) {
        controller.setList(userManager.isNightMode(), commodities);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(View itemView, CommodityItem commodity) {
        if (callback != null) {
            callback.showFarmerScanScreen(commodity, deviceId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    public interface Callback {

        void showFarmerScanScreen(CommodityItem commodity, int deviceId);

    }
}