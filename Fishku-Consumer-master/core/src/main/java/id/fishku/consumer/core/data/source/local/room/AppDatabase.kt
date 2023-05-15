package id.fishku.consumer.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import id.fishku.consumer.core.data.source.local.entity.FishEntity

@Database(entities = [FishEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fishDao(): FishDao
}