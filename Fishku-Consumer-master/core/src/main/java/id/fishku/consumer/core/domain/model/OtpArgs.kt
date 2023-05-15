package id.fishku.consumer.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpArgs(
    val user: User?,
    val state: Boolean = false
): Parcelable
