package com.example.golf_ppc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


class Converters  {
    @TypeConverter
    fun fromStatus(value: ReservationStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): ReservationStatus = ReservationStatus.valueOf(value)
}

@Database(
    entities = [ReservacionData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservacionDAO(): ReservacionDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "golf_club_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}