package com.rahuls.task2_comunev

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Names::class], version = 1)
abstract class NamesRoomDatabase : RoomDatabase() {
    abstract fun namesDao(): NamesDao
}