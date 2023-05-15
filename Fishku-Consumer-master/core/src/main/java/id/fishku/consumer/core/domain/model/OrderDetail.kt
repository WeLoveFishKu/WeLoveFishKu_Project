package id.fishku.consumer.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class OrderDetail(
    val fishName: String,
    val weight: Int,
    val photoUrl: String? = null,
    val fishPrice: Int,
    val status: String,
)