package com.custom.app.ui.quality;

import com.custom.app.data.model.quality.QualityMapItem;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes;

import java.util.List;

import io.reactivex.Single;

public interface QualityInteractor {

    Single<List<QualityRes>> quality(String commodityId, String analysis, String from,
                               String to, String... filter);

    Single<List<QualityOverTimeRes>> qualityOverTime(String commodityId, String analysis,
                                                     String from, String to, String... filter);

    Single<List<QualityMapItem>> qualityMap(String commodityId, String analysis, String from, String to);

}