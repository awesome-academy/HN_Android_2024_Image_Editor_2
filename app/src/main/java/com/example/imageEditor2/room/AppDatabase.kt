package com.example.imageEditor2.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.imageEditor2.model.QueryModel

@Database(entities = [QueryModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historySearchDao(): HistorySearchDao
}
