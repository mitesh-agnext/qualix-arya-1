package com.custom.app.ui.analysis;

import android.view.View;

import com.airbnb.epoxy.AutoModel;
import com.base.app.ui.epoxy.BaseEpoxy;
import com.core.app.ui.custom.DelayedClickListener;
import com.custom.app.ui.sample.HeaderTitleModel_;
import com.specx.scan.data.model.analysis.AnalysisItem;

import java.util.List;

public class AnalysisController extends BaseEpoxy {

    @AutoModel BatchHeaderModel_ batchModel;
    @AutoModel HeaderTitleModel_ headerModel;

    private String batchId;
    private AnalysisView view;
    private List<AnalysisItem> analyses;

    AnalysisController(AnalysisView view, String batchId) {
        this.view = view;
        this.batchId = batchId;
    }

    void setList(List<AnalysisItem> analyses) {
        this.analyses = analyses;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        batchModel
                .view(view)
                .title(String.format("Batch ID: #%s", batchId))
                .longClickListener(v -> {
                    view.onBatchIdClicked();
                    return false;
                })
                .addTo(this);

        headerModel
                .title("Type of Scan")
                .addTo(this);

        if (analyses != null) {
            for (AnalysisItem analysis : analyses) {
                new AnalysisItemModel_()
                        .view(view)
                        .id(analysis.getId())
                        .title(analysis.getName())
                        .isDone(analysis.isDone())
                        .thumbnail(analysis.getThumbnail())
                        .clickListener(new DelayedClickListener() {
                            @Override
                            public void onClicked(View itemView) {
                                view.onItemClicked(itemView, analysis);
                            }
                        })
                        .removeListener(new DelayedClickListener() {
                            @Override
                            public void onClicked(View itemView) {
                                view.onRemoveClicked(analysis);
                            }
                        })
                        .addTo(this);
            }
        }
    }
}