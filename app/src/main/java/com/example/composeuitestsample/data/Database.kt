package com.example.composeuitestsample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "bird")
data class Bird(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String,
)

@Database(
    entities = [Bird::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "bird_database"
            ).build()
        }
    }
}
