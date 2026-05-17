package com.bashkevich.counteroverlay.counter.local.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "counters",
)
data class CounterEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "amount")
    val amount: Int,
    @ColumnInfo(name = "theme_id")
    val themeId: String
)
