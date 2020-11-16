package com.custom.app.data.model.bleScan.response;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SensorData.class}, version = 1, exportSchema = false)
public abstract class SensorDataRoomDatabase extends RoomDatabase {

   public abstract SensorDataDao wordDao();
   private static SensorDataRoomDatabase INSTANCE;

   static SensorDataRoomDatabase getDatabase(final Context context) {
       if (INSTANCE == null) {
           synchronized (SensorDataRoomDatabase.class) {
               if (INSTANCE == null) {
                   INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                           SensorDataRoomDatabase.class, "qualix_database")
                             // Wipes and rebuilds instead of migrating 
                             // if no Migration object.
                            // Migration is not part of this practical.
                           .fallbackToDestructiveMigration()
                           .build();                
               }
           }
       }
       return INSTANCE;
   }
}