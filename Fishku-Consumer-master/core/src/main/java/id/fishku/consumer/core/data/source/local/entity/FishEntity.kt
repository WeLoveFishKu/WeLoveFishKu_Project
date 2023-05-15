package id.fishku.consumer.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fish")
data class FishEntity(
    @PrimaryKey
    @ColumnInfo(name = "idFish")
    var idFish: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "price")
    var price: Int,

    @ColumnInfo(name = "location")
    var location: String,

    @ColumnInfo(name = "photo_url")
    var photoUrl: String,
)