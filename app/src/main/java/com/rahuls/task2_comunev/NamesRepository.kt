package com.rahuls.task2_comunev

import android.content.Context
import androidx.room.Room

class NamesRepository private constructor(mCtx: Context) {
    //our app database object
    private val appDatabase: NamesRoomDatabase =
        Room.databaseBuilder(mCtx, NamesRoomDatabase::class.java, "alldata").build()

    fun getAppDatabase(): NamesRoomDatabase {
        return appDatabase
    }

    companion object {
        private var mInstance: NamesRepository? = null
        @Synchronized
        fun getInstance(mCtx: Context): NamesRepository? {
            if (mInstance == null) {
                mInstance = NamesRepository(mCtx)
            }
            return mInstance
        }
    }

}