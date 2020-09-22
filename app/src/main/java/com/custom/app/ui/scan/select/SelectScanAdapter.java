package com.custom.app.ui.scan.select;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.custom.app.ui.commodity.CommodityFragment;
import com.custom.app.ui.createData.flcScan.FlcFragmnet;
import com.custom.app.util.Permissions;

import java.util.List;

public class SelectScanAdapter extends FragmentPagerAdapter {

    private List<String> scans;

    SelectScanAdapter(FragmentManager fragmentManager, List<String> scans) {
        super(fragmentManager);
        this.scans = scans;
    }

    @Override
    public int getCount() {
        return scans.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String scan = scans.get(position);
        switch (scan) {
            case Permissions.SCAN_NANO:
                return CommodityFragment.newInstance(0);
            case Permissions.SCAN_VISIO:
                return CommodityFragment.newInstance(1);
            case Permissions.SCAN_FLC:
                return FlcFragmnet.Companion.newInstance();
        }
        return FlcFragmnet.Companion.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String scan = scans.get(position);
        switch (scan) {
            case Permissions.SCAN_NANO:
                return "Nano";
            case Permissions.SCAN_VISIO:
                return "Visio";
            case Permissions.SCAN_FLC:
                return "FLC";
        }
        return null;
    }
}