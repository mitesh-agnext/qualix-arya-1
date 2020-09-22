package com.specx.scan.ui.result.base;

import com.airbnb.epoxy.AutoModel;
import com.base.app.ui.epoxy.BaseEpoxy;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

public class ResultController extends BaseEpoxy {

    @AutoModel ResultHeaderModel_ headerModel;
    @AutoModel ResultTableModel_ resultModel;
    @AutoModel ResultPriceModel_ priceModel;

    private ResultView view;
    private boolean showPrice;
    private boolean isNightMode;
    private CommodityItem commodity;
    private List<AnalysisItem> analyses;

    ResultController(ResultView view, boolean isNightMode) {
        this.view = view;
        this.isNightMode = isNightMode;
    }

    public void setList(boolean showPrice, CommodityItem commodity, List<AnalysisItem> analyses) {
        this.showPrice = showPrice;
        this.commodity = commodity;
        this.analyses = analyses;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        headerModel
                .view(view)
                .title("Result")
                .subtitle("Parameters")
                .addIf(analyses != null, this);

        showAnalyses(analyses);

        if (commodity != null) {
            priceModel
                    .view(view)
                    .totalAmount(commodity.getTotalAmount())
                    .amountUnit(commodity.getAmountUnit())
                    .addIf(showPrice, this);
        }
    }

    private void showAnalyses(List<AnalysisItem> analyses) {
        resultModel
                .view(view)
                .analyses(analyses)
                .addIf(analyses != null, this);

/*
        if (analyses != null) {
            for (AnalysisItem analysis : analyses) {
                new ResultItemModel_()
                        .view(view)
                        .id(analysis.getId(), analysis.getName(), analysis.getTotalAmount())
                        .thumbUrl(isNightMode ? analysis.getDarkThumbUrl() : analysis.getLightThumbUrl())
                        .analysis(analysis.getName())
                        .totalAmount(analysis.getTotalAmount())
                        .isAdulterant(analysis.isAdulterant())
                        .amountUnit(analysis.getAmountUnit())
                        .addTo(this);
            }
        }
*/
    }
}