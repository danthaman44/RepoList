package com.example.repolist.api;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.repolist.models.Item;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemDao userDao();
}
