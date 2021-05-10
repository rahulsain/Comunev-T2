package com.rahuls.task2_comunev

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NamesDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert
    fun insert(name: Names)

    @Query("SELECT * FROM name_table")
    fun getAll(): List<Names>

    @Query("DELETE FROM name_table")
    fun deleteAll()
}