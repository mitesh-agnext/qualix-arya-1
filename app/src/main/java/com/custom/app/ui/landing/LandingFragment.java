package com.custom.app.ui.landing;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.app.ui.base.BaseFragment;
import com.base.app.ui.custom.CustomSpinnerAdapter;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.data.model.category.CategoryDetailItem;
import com.custom.app.data.model.quantity.CenterDetailItem;
import com.custom.app.data.model.quantity.DeviceTypeItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.ui.dashboard.DashboardFragment;
import com.custom.app.util.Utils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneOffset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.DASHBOARD_FRAGMENT;

public class LandingFragment extends BaseFragment implements LandingView {

    private Unbinder unbinder;
    private LandingController controller;
    private QuantityDetailRes detail;
    private String categoryId, startDate, endDate;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;
    private List<CategoryDetailItem> categories = new ArrayList<>();

    @Inject
    QuantityPresenter presenter;

    @BindView(R.id.sp_category)
    Spinner spCategory;

    @OnItemSelected(R.id.sp_category)
    void onCategorySelected(int selectedIndex) {
        categoryId = categories.get(selectedIndex).getCommodityCategoryId();

        fetchQuantityDetail();
    }

    @Override
    @OnClick(R.id.btn_date)
    public void showDatePicker() {
        datePicker.show(getChildFragmentManager(), datePicker.toString());
    }

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;

    public static LandingFragment newInstance() {
        return new LandingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_landing, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                ((TextView) toolbar.findViewById(R.id.title)).setText("Qualix-Arya");
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context(), RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        controller = new LandingController(this);
        recyclerView.setAdapter(controller.getAdapter());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
        long max = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        constraints.setValidator(DateValidatorPointBackward.before(max));
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        builder.setCalendarConstraints(constraints.build());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDateTime from = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.first)).atTime(LocalTime.MIDNIGHT);
            LocalDateTime to = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(selection.second)).atTime(LocalTime.MAX);

            startDate = String.valueOf(from.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli());
            endDate = String.valueOf(to.toInstant(ZoneOffset.ofHoursMinutes(5, 30)).toEpochMilli());

            fetchQuantityDetail();
        });

        presenter.setView(this);
        presenter.fetchCategories();
    }

    void fetchQuantityDetail() {
        if (!TextUtils.isEmpty(categoryId)) {
            if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                presenter.fetchQuantityDetail(categoryId, startDate, endDate);
            } else {
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                endDate = ""+c.getTime();
                startDate = ""+Utils.Companion.getDaysAgo(7).getTime();
                presenter.fetchQuantityDetail(categoryId, startDate, endDate);
//                showMessage("Please select date range");
            }
        } else {
            showMessage("Please select commodity category");
        }
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void showCategories(List<CategoryDetailItem> categories) {
        this.categories = categories;

        List<String> names = new ArrayList<>();
        for (CategoryDetailItem category : categories) {
            names.add(category.getCommodityCategoryName());
        }

        spCategory.setAdapter(new CustomSpinnerAdapter(context(), false, names, v -> spCategory.performClick()));
        spCategory.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDetails(QuantityDetailRes detail) {
        this.detail = detail;
        controller.setList(detail.getInstCenterDetails());
        recyclerView.setVisibility(View.VISIBLE);
        replaceFragment(R.id.layout_content, DashboardFragment
                .newInstance(detail, categoryId, startDate, endDate), DASHBOARD_FRAGMENT);
    }

    @Override
    public void onCenterClicked(CenterDetailItem center) {
        List<DeviceTypeItem> devices = center.getDeviceTypeData();
        if (devices != null && !devices.isEmpty()) {
            String centerId = center.getInstCenterId();
            String deviceType = devices.get(0).getDeviceTypeName();
            String deviceSerialNo = devices.get(0).getDeviceSerialNo();

            replaceFragment(R.id.layout_content, DashboardFragment
                    .newInstance(detail, categoryId, startDate, endDate, centerId, deviceType, deviceSerialNo, center.getTotalQuantity()), DASHBOARD_FRAGMENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }
}