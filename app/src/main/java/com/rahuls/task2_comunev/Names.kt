package com.rahuls.task2_comunev

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "name_table")
class Names : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var tName: String = ""
    var fName: String = ""
    var lName: String = ""
}
