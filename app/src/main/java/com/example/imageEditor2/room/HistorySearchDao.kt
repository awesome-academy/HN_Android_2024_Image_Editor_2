package com.example.imageEditor2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imageEditor2.model.QueryModel

@Dao
interface HistorySearchDao {
    @Query("SELECT * FROM queryModel")
    suspend fun getAll(): List<QueryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queryModel: QueryModel)

    @Delete
    suspend fun delete(queryModel: QueryModel)
}
