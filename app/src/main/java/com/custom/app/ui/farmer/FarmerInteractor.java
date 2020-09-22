package com.custom.app.ui.farmer;

import com.custom.app.data.model.farmer.upload.FarmerItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface FarmerInteractor {

    Observable<List<FarmerItem>> fetchFarmersFromDb(String query);

    Single<FarmerItem> verifyFarmerFromNetwork(String query);

    Observable<FarmerItem> verifyFarmerFromDb(String query);

    Single<String> uploadFarmer(FarmerItem farmer);

    String addFarmerInDb(FarmerItem farmer);

}