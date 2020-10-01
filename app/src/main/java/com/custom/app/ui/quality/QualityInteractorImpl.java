package com.custom.app.ui.quality;

import com.custom.app.data.model.quality.QualityMapItem;
import com.custom.app.network.RestService;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes;
import com.user.app.data.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

public class QualityInteractorImpl implements QualityInteractor {

    private RestService restService;
    private UserManager userManager;

    public QualityInteractorImpl(RestService restService, UserManager userManager) {
        this.restService = restService;
        this.userManager = userManager;
    }

    @Override
    public Single<List<QualityRes>> quality(String commodityId, String analysis, String from,
                                      String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("client_id", userManager.getCustomerId());
        query.put("commodity_id", commodityId);
        query.put("analysis_name", analysis);
        query.put("date_from", from);
        query.put("date_to", to);

        return restService.quality(query)
                .map(QualityParser::parse);
    }

    @Override
    public Single<List<QualityOverTimeRes>> qualityOverTime(String commodityId, String analysis,
                                                            String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("client_id", userManager.getCustomerId());
        query.put("commodity_id", commodityId);
        query.put("analysis_name", analysis);
        query.put("date_from", from);
        query.put("date_to", to);

        return restService.qualityOverTime(query)
                .map(QualityParser::time);
    }

    @Override
    public Single<List<QualityMapItem>> qualityMap(String commodityId, String analysis,
                                                   String from, String to) {
        Map<String, String> query = new HashMap<>();
        query.put("client_id", userManager.getCustomerId());
        query.put("commodity_id", commodityId);
        query.put("analysis_name", analysis);
        query.put("date_from", from);
        query.put("date_to", to);

        return restService.qualityMap(query)
                .map(QualityParser::map);
    }
}