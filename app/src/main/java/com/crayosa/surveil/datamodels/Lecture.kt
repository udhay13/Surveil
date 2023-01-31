package com.crayosa.surveil.datamodels

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    @DocumentId val id : String?,
    val url : String,
    val name : String
) : Parcelable
