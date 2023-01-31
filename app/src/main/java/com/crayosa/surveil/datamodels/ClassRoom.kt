package com.crayosa.surveil.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClassRoom(
    val id : String?,
    val name : String,
    val section_name :  String,
    val teacher_name : String,
    val color : String,
    val gender : String
): Parcelable