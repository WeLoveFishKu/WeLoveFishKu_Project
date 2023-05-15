package id.fishku.consumer.core.data.source.local.room

import androidx.room.*
import id.fishku.consumer.core.data.source.local.entity.FishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FishDao {
    @Query("SELECT * FROM fish")
    fun getAllFish(): Flow<List<FishEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFish(fishes: List<FishEntity>)
}