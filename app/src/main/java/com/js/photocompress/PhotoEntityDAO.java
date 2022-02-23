package com.js.photocompress;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoEntityDAO {

    @Query("SELECT * FROM PhotoEntity")
    List<PhotoEntity> getAllData();

    @Insert
    void insertPhotoEntity(PhotoEntity... entity);

}
