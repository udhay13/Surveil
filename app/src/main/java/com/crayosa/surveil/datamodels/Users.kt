package com.crayosa.surveil.datamodels

import com.google.firebase.firestore.DocumentId

data class Users(
    val id : String?,
    val name : String,
    val classRooms : List<String> //id roomName subjectName staffName
)
