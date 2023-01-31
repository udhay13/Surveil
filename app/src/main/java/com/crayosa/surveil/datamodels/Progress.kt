package com.crayosa.surveil.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Progress(
    val id : String,
    val name : String,
    val completion : Float
): Parcelable
