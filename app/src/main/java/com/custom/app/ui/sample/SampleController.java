package com.custom.app.ui.sample;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.airbnb.epoxy.AutoModel;
import com.base.app.ui.epoxy.BaseEpoxy;
import com.core.app.ui.custom.DelayedClickListener;
import com.core.app.util.AlertUtil;
import com.custom.app.R;
import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.custom.app.data.model.section.LocationItem;
import com.custom.app.ui.analysis.BatchHeaderModel_;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;
import com.specx.scan.data.model.variety.VarietyItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SampleController extends BaseEpoxy {

    @AutoModel BatchHeaderModel_ batchModel;
    @AutoModel ScanDetailModel_ scanDetailModel;
    @AutoModel HeaderTitleModel_ headerLocation;
    @AutoModel SpinnerItemModel_ locationModel;
    @AutoModel HeaderTitleModel_ headerCommodity;
    @AutoModel SpinnerItemModel_ commodityModel;
    @AutoModel SpinnerItemModel_ varietyModel;
    @AutoModel SampleDetailModel_ sampleModel;
    @AutoModel FarmerDetailModel_ farmerModel;
    @AutoModel ButtonModel_ proceedModel;

    private SampleView view;
    private String batchId;
    private String commodity;
    private SampleItem sample;
    private FarmerItem farmer;
    private boolean showProceed;
    private String proceedTitle;
    private ScanDetailRes scanDetail;
    private List<LocationItem> locations;
    private List<CommodityItem> commodities;
    private List<VarietyItem> varieties;

    SampleController(SampleView view, FarmerItem farmer, SampleItem sample) {
        this.view = view;
        this.farmer = farmer;
        this.sample = sample;
    }

    void setSample(SampleItem sample) {
        this.sample = sample;
        requestModelBuild();
    }

    void setLocations(List<LocationItem> locations) {
        this.locations = locations;
        requestModelBuild();
    }

    void setCommodity(String commodity) {
        this.commodity = commodity;
        requestModelBuild();
    }

    void setCommodities(List<CommodityItem> commodities) {
        this.commodities = commodities;
        requestModelBuild();
    }

    void setVarieties(List<VarietyItem> varieties) {
        this.varieties = varieties;
        requestModelBuild();
    }

    void setBatchId(String batchId) {
        this.batchId = batchId;

        requestDelayedModelBuild(1000);
    }

    void setFarmerDetail(FarmerItem farmer) {
        this.farmer = farmer;
        requestModelBuild();
    }

    void setScanDetail(ScanDetailRes scanDetail) {
        this.scanDetail = scanDetail;

        setFarmerDetail(scanDetail.getFarmer_detail());
    }

    void showProceedButton(boolean showProceed) {
        this.showProceed = showProceed;
        requestModelBuild();
    }

    void setProceedTitle(String proceedTitle) {
        this.proceedTitle = proceedTitle;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        batchModel
                .view(view)
                .title(String.format("Batch ID: #%s", batchId))
                .clickListener(new DelayedClickListener() {
                    @Override
                    public void onClicked(View v) {
                        AlertUtil.showToast(view.context(), view.context().getString(R.string.long_click_copy_msg));
                    }
                })
                .longClickListener(v -> {
                    view.onBatchIdClicked();
                    return true;
                })
                .addIf(!TextUtils.isEmpty(batchId), this);

        if (farmer != null && !"X".equals(farmer.getCode())) {
            farmerModel
                    .view(view)
                    .name(farmer.getName())
                    .village(farmer.getLocation())
                    .email(farmer.getEmail())
                    .phone(farmer.getMobile())
                    .addTo(this);
        }

        if (scanDetail != null) {
            scanDetailModel
                    .view(view)
                    .lotId(scanDetail.getLot_id())
                    .sampleId(scanDetail.getSample_id())
                    .commodity(scanDetail.getCommodity_name())
                    .location(scanDetail.getLocation())
                    .weight(scanDetail.getWeight())
                    .quantityUnit(scanDetail.getQuantity_unit())
                    .truckNumber(scanDetail.getTruck_number())
                    .areaCovered(scanDetail.getArea_covered())
                    .addTo(this);
        } else {
            if (locations != null && !locations.isEmpty()) {
                headerLocation
                        .title("Location")
                        .addTo(this);

                List<String> lnames = new ArrayList<>();
                for (LocationItem item : locations) {
                    lnames.add(item.getName());
                }

                locationModel
                        .view(view)
                        .names(lnames)
                        .listener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                if (position >= 0 && position < locations.size()) {
                                    view.onLocationSelected(locations.get(position));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        })
                        .addTo(this);
            }

            if (commodities != null && !commodities.isEmpty() && TextUtils.isEmpty(commodity)) {
                headerCommodity
                        .title("Commodity")
                        .addTo(this);

                List<String> cnames = new ArrayList<>();
                for (CommodityItem item : commodities) {
                    cnames.add(item.getName());
                }

                commodityModel
                        .view(view)
                        .names(cnames)
                        .listener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                if (position >= 0 && position < commodities.size()) {
                                    view.onCommoditySelected(commodities.get(position));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        })
                        .addTo(this);

                List<String> vnames = new ArrayList<>();
                if (varieties != null && !varieties.isEmpty()) {
                    for (VarietyItem item : varieties) {
                        vnames.add(item.getName());
                    }
                }

                varietyModel
                        .view(view)
                        .names(vnames)
                        .listener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                if (position >= 0 && position < varieties.size()) {
                                    view.onVarietySelected(varieties.get(position));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        })
                        .addIf(!vnames.isEmpty(), this);
            }

            if (view.context() != null) {
                String[] units = view.context().getResources().getStringArray(R.array.array_units);

                if (TextUtils.isEmpty(sample.getQuantityUnit()) && units.length > 0) {
                    sample.setQuantityUnit(units[0]);
                }

                sampleModel
                        .view(view)
                        .commodity(commodity)
                        .lotId(sample.getLotId())
                        .sampleId(sample.getId())
                        .weight(sample.getQuantity())
                        .quantityUnit(sample.getQuantityUnit())
                        .truckNumber(sample.getTruckNumber())
                        .adapter(new ArrayAdapter<>(view.context(), R.layout.layout_unit_dropdown, units))
                        .clickListener((adapterView, v, position, id) -> {
                            if (position >= 0 && position < units.length) {
                                sample.setQuantityUnit(units[position]);
                                view.onQuantityUnitSelected();
                            }
                        })
                        .lotWatcher(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                sample.setLotId(s.toString());
                            }
                        })
                        .truckWatcher(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                sample.setTruckNumber(s.toString());
                            }
                        })
                        .sampleWatcher(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                sample.setId(s.toString());
                                view.onSampleIdChanged();
                            }
                        })
                        .quantityWatcher(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    sample.setQuantity(Double.parseDouble(s.toString()));
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            }
                        })
                        .addTo(this);
            }
        }

        proceedModel
                .view(view)
                .title(proceedTitle)
                .clickListener(new DelayedClickListener() {
                    @Override
                    public void onClicked(View itemView) {
                        view.onProceed();
                    }
                })
                .addIf(showProceed && !TextUtils.isEmpty(proceedTitle), this);
    }
}