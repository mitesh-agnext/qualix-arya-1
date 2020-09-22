package com.custom.app.ui.farmer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;

import com.custom.app.data.model.farmer.upload.FarmerExclusionStrategy;
import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.custom.app.network.RestService;
import com.data.app.db.table.FarmerTable;
import com.data.app.db.table.ResultTable;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;

public class FarmerInteractorImpl implements FarmerInteractor {

    private RestService restService;
    private UserManager userManager;
    private BriteDatabase database;

    FarmerInteractorImpl(RestService restService, UserManager userManager, BriteDatabase database) {
        this.restService = restService;
        this.userManager = userManager;
        this.database = database;
    }

    @Override
    public Observable<List<FarmerItem>> fetchFarmersFromDb(String query) {
        return database.createQuery(FarmerTable.TABLE_NAME, query, userManager.getUserId())
                .map(FarmerParser::parse);
    }

    @Override
    public Single<FarmerItem> verifyFarmerFromNetwork(String query) {
        return restService.verifyFarmer(query)
                .map(FarmerParser::status);
    }

    @Override
    public Observable<FarmerItem> verifyFarmerFromDb(String query) {
        return fetchFarmersFromDb(FarmerTable.QUERY_ALL_FARMERS)
                .map(farmers -> {
                    for (FarmerItem farmer : farmers) {
                        if (query.equalsIgnoreCase(farmer.getCode())
                                || query.equalsIgnoreCase(farmer.getMobile())) {
                            return farmer;
                        }
                    }

                    throw new AssertionError("No records found!");
                });
    }

    @Override
    public Single<String> uploadFarmer(FarmerItem farmer) {
        return restService.uploadFarmer(farmer)
                .map(response -> {
                    String farmerCode = FarmerParser.status(response).getCode();
                    farmer.setUploaded(true);
                    addFarmerInDb(farmer);
                    return farmerCode;
                });
    }

    @Override
    public String addFarmerInDb(FarmerItem newFarmer) {
        String farmerCode = newFarmer.getCode();

        BriteDatabase.Transaction transaction = database.newTransaction();
        try {

            String farmerDetail = new FarmerExclusionStrategy().getGson().toJson(newFarmer);

            ContentValues values = new FarmerTable.Builder()
                    .userId(userManager.getUserId())
                    .farmerCode(newFarmer.getCode())
                    .farmerPhone(newFarmer.getMobile())
                    .farmerDetail(farmerDetail)
                    .isUploaded(newFarmer.isUploaded())
                    .build();

            long id = database.insert(FarmerTable.TABLE_NAME, CONFLICT_IGNORE, values);

            if (id == -1) {
                farmerCode = null;

                ContentValues uploaded = new FarmerTable.Builder()
                        .isUploaded(newFarmer.isUploaded())
                        .build();

                database.update(FarmerTable.TABLE_NAME, CONFLICT_NONE, uploaded,
                        ResultTable.FARMER_CODE + " = ?", newFarmer.getCode());
            }

            transaction.markSuccessful();

        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
            farmerCode = null;
        } finally {
            transaction.end();
        }

        return farmerCode;
    }
}