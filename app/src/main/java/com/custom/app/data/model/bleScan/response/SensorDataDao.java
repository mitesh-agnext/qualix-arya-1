package com.custom.app.data.model.bleScan.response;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SensorDataDao {

    @Insert
    long insert(SensorData sensorData);

    @Query("DELETE FROM sensor_data")
    void deleteAll();

    @Query("SELECT * from sensor_data where issynchronized=0 ORDER BY sample_id ASC")
    List<SensorData> getAllData();

    @Update
    void update(SensorData sensorData);

}