package com.custom.app.ui.scan.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.base.app.ui.base.BaseFragment;
import com.base.app.ui.custom.StepProgressView;
import com.custom.app.R;
import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.custom.app.ui.farmer.scan.FarmerScanFragment;
import com.custom.app.ui.sample.SampleFragment;
import com.custom.app.ui.sampleBLE.SampleBleFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.custom.app.util.Constants.FARMER_SCAN_FRAGMENT;
import static com.custom.app.util.Constants.KEY_SCAN_ID;
import static com.custom.app.util.Constants.SAMPLE_FRAGMENT;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_NAME;

public class SelectScanFragment extends BaseFragment implements SelectScanView {

    private Unbinder unbinder;

    private int deviceId;
    private String scanId;
    private String deviceName;

    @BindView(R.id.stepsView)
    StepProgressView stepsView;

    public static SelectScanFragment newInstance(String scanId, int deviceId, String deviceName) {
        SelectScanFragment fragment = new SelectScanFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SCAN_ID, scanId);
        args.putInt(KEY_DEVICE_ID, deviceId);
        args.putString(KEY_DEVICE_NAME, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_scan, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        stepsView.setStepTitles(Arrays.asList("Scan", "Data", "Results"));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
//             ((TextView) toolbar.findViewById(R.id.title)).setText(getString(R.string.title_select_scan));
            }
        }

        if (getArguments() != null) {
            scanId = getArguments().getString(KEY_SCAN_ID);
            deviceId = getArguments().getInt(KEY_DEVICE_ID);
            deviceName = getArguments().getString(KEY_DEVICE_NAME);

            if (savedInstanceState == null) {
                if (deviceId == 1 || deviceId == 2 || deviceId == 4) {
                    replaceFragment(R.id.layout_content,
                            FarmerScanFragment.newInstance(scanId, deviceId, deviceName), FARMER_SCAN_FRAGMENT);
                }
                //TODO Move the fragment to BLE Input screen and skip the Qr screen
                else if(deviceId == 7)
                {
                    replaceFragment(R.id.layout_content, SampleBleFragment.newInstance(scanId, deviceId, deviceName, new FarmerItem("X")), FARMER_SCAN_FRAGMENT);
                }
                else {
                    replaceFragment(R.id.layout_content,
                            SampleFragment.newInstance(scanId, deviceId, deviceName,
                                    new FarmerItem("X")), SAMPLE_FRAGMENT);
                }
            }
        }
    }

    public void setStep(int step) {
        if (stepsView != null) {
            if (step == 0) {
                stepsView.resetView();
            } else {
                stepsView.markCurrentAsDone();
                stepsView.selectStep(step);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}