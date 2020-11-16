package com.custom.app.data.model.bleScan.response;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class SensorDataRepository {

    private SensorDataDao mSensorDataDao;
    private List<SensorData> mAllSensorData;

    public SensorDataRepository(Context context) {
        SensorDataRoomDatabase db = SensorDataRoomDatabase.getDatabase(context);
        mSensorDataDao = db.wordDao();
    }

    public List<SensorData> getAllSensorData() {
        mAllSensorData = mSensorDataDao.getAllData();
        return mAllSensorData;
    }

    public void insert(SensorData sensorData) {
        new insertAsyncTask(mSensorDataDao).execute(sensorData);
    }


    public void updateData(SensorData sensorData){
        mSensorDataDao.update(sensorData);
    }


    private static class insertAsyncTask extends AsyncTask<SensorData, Void, Void> {

        private SensorDataDao mAsyncTaskDao;

        insertAsyncTask(SensorDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SensorData... params) {
            long inserted = mAsyncTaskDao.insert(params[0]);
            System.out.println(inserted);
            return null;
        }
    }
}