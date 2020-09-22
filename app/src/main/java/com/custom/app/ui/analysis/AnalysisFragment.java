package com.custom.app.ui.analysis;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.core.app.util.Util;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;
import com.user.app.data.UserManager;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.core.app.util.Constants.TYPE_MAX_WIDTH;
import static com.specx.scan.util.Constants.KEY_COMMODITY;
import static com.specx.scan.util.Constants.KEY_FARMER;
import static com.specx.scan.util.Constants.KEY_SAMPLE;

public class AnalysisFragment extends BaseFragment implements AnalysisView {

    private String batchId;
    private String farmerCode;
    private Unbinder unbinder;
    private Callback callback;
    private SampleItem sample;
    private CommodityItem commodity;
    private AnalysisController controller;

    @Inject
    UserManager userManager;

    @Inject
    AnalysisPresenter presenter;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.emptyView)
    View emptyView;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.btn_result)
    Button btnResult;

    @OnClick(R.id.btn_result)
    void showAppResults() {
        if (callback != null) {
            callback.showAllResults(batchId);
        }
    }

    public static AnalysisFragment newInstance(String farmerCode, SampleItem sample, CommodityItem commodity) {
        AnalysisFragment fragment = new AnalysisFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FARMER, farmerCode);
        args.putParcelable(KEY_SAMPLE, Parcels.wrap(sample));
        args.putParcelable(KEY_COMMODITY, Parcels.wrap(commodity));
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
        View rootView = inflater.inflate(R.layout.fragment_analysis, container, false);
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (getArguments() != null) {
                farmerCode = getArguments().getString(KEY_FARMER);
                sample = Parcels.unwrap(getArguments().getParcelable(KEY_SAMPLE));
                commodity = Parcels.unwrap(getArguments().getParcelable(KEY_COMMODITY));

                if (TextUtils.isEmpty(batchId)) {
                    batchId = String.format("%s_%s_%s_%s", userManager.getUserId(), commodity.getId(),
                            sample.getId(), Util.getDatetime());
                }

                controller = new AnalysisController(this, batchId);
                recyclerView.setAdapter(controller.getAdapter());

                if (commodity != null) {

                    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        ((TextView) toolbar.findViewById(R.id.title)).setText(commodity.getName());
                    }

                    swipeLayout.setOnRefreshListener(() -> presenter.fetchAnalyses(batchId));

                    presenter.fetchAnalyses(batchId);

                } else {
                    showEmptyView();
                }
            }
        }
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
    public void showList(List<AnalysisItem> analyses) {
        controller.setList(analyses);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if (isAnyAnalysisDone(analyses)) {
            btnResult.setVisibility(View.VISIBLE);
        } else {
            btnResult.setVisibility(View.GONE);
        }
    }

    private boolean isAnyAnalysisDone(List<AnalysisItem> analyses) {
        for (AnalysisItem analysis : analyses) {
            if (analysis.isDone()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClicked(View itemView, AnalysisItem analysis) {
        if (callback != null) {
            if ("00".equalsIgnoreCase(analysis.getId())) {
                if (analysis.isDone()) {
                    callback.showResultScreen(batchId, true);
                } else {
                    if (!TextUtils.isEmpty(userManager.getSavedDevice())) {
                        callback.showScanDialog(farmerCode, batchId, sample, commodity);
                    } else {
                        callback.showSelectDeviceDialog();
                    }
                }
            } else {
                if (analysis.isDone()) {
                    callback.showResultScreen(batchId, false);
                } else {
                    callback.showAssayScreen(farmerCode, batchId, sample, commodity);
                }
            }
        }
    }

    @Override
    public void onRemoveClicked(AnalysisItem analysis) {
        AlertUtil.showActionAlertDialog(context(), getString(R.string.remove_result_msg),
                getString(R.string.btn_cancel), getString(R.string.btn_yes), (dialog, which) -> presenter.removeAnalyses(batchId));
    }

    @Override
    public void onBatchIdClicked() {
        Util.copyToClipboard(context(), "Batch ID", batchId);
        AlertUtil.showToast(context(), getString(R.string.batch_copied_msg));
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

        void showSelectDeviceDialog();

        void showScanDialog(String farmerCode, String batchId, SampleItem sample, CommodityItem commodity);

        void showAssayScreen(String farmerCode, String batchId, SampleItem sample, CommodityItem commodity);

        void showResultScreen(String batchId, boolean isChemical);

        void showAllResults(String batchId);

    }
}