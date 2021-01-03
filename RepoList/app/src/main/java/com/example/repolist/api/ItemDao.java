package com.example.repolist.api;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.repolist.models.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE language = :lang")
    List<Item> getByLanguage(String lang);

    @Insert
    void insertAll(List<Item> items);

    @Insert
    void insert(Item item);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM item")
    public void deleteAll();
}