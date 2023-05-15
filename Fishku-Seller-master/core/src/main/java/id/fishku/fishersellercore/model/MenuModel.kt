package id.fishku.fishersellercore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MenuModel(
    val id_fish: String,
    val name: String,
    val price: String,
    val photo_url: String,
    val weight: Int
): Parcelable
