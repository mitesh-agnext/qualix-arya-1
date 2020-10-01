package com.custom.app.ui.quality;

import androidx.annotation.NonNull;

import com.custom.app.data.model.quality.QualityMapItem;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes;

import java.util.List;

class QualityParser {

    @NonNull
    static List<QualityRes> parse(List<QualityRes> body) throws NullPointerException {
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<QualityOverTimeRes> time(List<QualityOverTimeRes> qualities) throws NullPointerException {
        if (qualities != null && !qualities.isEmpty()) {
            return qualities;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<QualityMapItem> map(List<QualityMapItem> qualities) throws NullPointerException {
        if (qualities != null && !qualities.isEmpty()) {
            return qualities;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }
}