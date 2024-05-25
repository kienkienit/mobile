package com.app.shopfee.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.shopfee.model.Drink;

@Database(entities = {Drink.class}, version = 1)
public abstract class DrinkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "drink.db";

    private static DrinkDatabase instance;

    public static synchronized DrinkDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DrinkDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract DrinkDAO drinkDAO();
}
