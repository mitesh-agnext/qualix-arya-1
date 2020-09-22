package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AnalysisTable implements Parcelable {

    public static final String TABLE_NAME = "AnalysisList";

    public static final String USER_ID = "userId";
    public static final String ANALYSIS_ID = "analysisId";
    public static final String COMMODITY_ID = "commodityId";
    public static final String ANALYSIS_NAME = "analysisName";
    public static final String DARK_THUMB_URL = "darkThumbUrl";
    public static final String LIGHT_THUMB_URL = "lightThumbUrl";
    public static final String BETA_MATRIX = "betaMatrix";
    public static final String MEAN_MATRIX = "meanMatrix";
    public static final String ALGORITHM = "algorithm";
    public static final String ALGO_CONFIG = "algoConfig";

    public abstract String userId();
    public abstract String commodityId();
    public abstract String analysisName();
    public abstract String darkThumbUrl();
    public abstract String lightThumbUrl();
    public abstract String betaMatrix();
    public abstract String meanMatrix();
    public abstract String algorithm();
    public abstract String algoConfig();

    public static final String QUERY_CREATE_ANALYSIS_TABLE = "CREATE TABLE "
            + AnalysisTable.TABLE_NAME + "("
            + AnalysisTable.ANALYSIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AnalysisTable.USER_ID + " TEXT NOT NULL, "
            + AnalysisTable.COMMODITY_ID + " TEXT NOT NULL, "
            + AnalysisTable.ANALYSIS_NAME + " TEXT NOT NULL, "
            + AnalysisTable.DARK_THUMB_URL + " TEXT, "
            + AnalysisTable.LIGHT_THUMB_URL + " TEXT, "
            + AnalysisTable.BETA_MATRIX + " TEXT NOT NULL, "
            + AnalysisTable.MEAN_MATRIX + " TEXT, "
            + AnalysisTable.ALGORITHM + " TEXT NOT NULL DEFAULT 0, "
            + AnalysisTable.ALGO_CONFIG + " TEXT NOT NULL DEFAULT 0"
            + ", UNIQUE("
            + AnalysisTable.USER_ID + ", "
            + AnalysisTable.COMMODITY_ID + ")"
            + ")";

    public static String QUERY_SELECT_ANALYSIS = "SELECT *  FROM "
            + AnalysisTable.TABLE_NAME
            + " WHERE "
            + AnalysisTable.USER_ID
            + " = ?"
            + " AND "
            + AnalysisTable.COMMODITY_ID
            + " = ?";

    public static final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder userId(String userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder commodityId(String commodityId) {
            values.put(COMMODITY_ID, commodityId);
            return this;
        }

        public Builder analysisName(String analysisName) {
            values.put(ANALYSIS_NAME, analysisName);
            return this;
        }

        public Builder darkThumbUrl(String darkThumbUrl) {
            values.put(DARK_THUMB_URL, darkThumbUrl);
            return this;
        }

        public Builder lightThumbUrl(String lightThumbUrl) {
            values.put(LIGHT_THUMB_URL, lightThumbUrl);
            return this;
        }

        public Builder betaMatrix(String betaMatrix) {
            values.put(BETA_MATRIX, betaMatrix);
            return this;
        }

        public Builder meanMatrix(String meanMatrix) {
            values.put(MEAN_MATRIX, meanMatrix);
            return this;
        }

        public Builder algorithm(String algorithm) {
            values.put(ALGORITHM, algorithm);
            return this;
        }

        public Builder algoConfig(String algoConfig) {
            values.put(ALGO_CONFIG, algoConfig);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}