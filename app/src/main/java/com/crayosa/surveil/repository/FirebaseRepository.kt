package com.crayosa.surveil.repository

import android.util.Log
import com.crayosa.surveil.datamodels.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest


class FirebaseRepository(private val firestore: FirebaseFirestore){

    fun getEnrolledRooms(uID: String) : Flow<MutableList<ClassRoom?>>{
        return callbackFlow {
            val result : MutableList<ClassRoom?> = mutableListOf()
            firestore.collection(USERS_COLLECTION).document(uID).get()
                .addOnSuccessListener {
                    val data = it.data?.get(ENROLLED_CLASSROOMS)
                    if (data != null) {
                        val list = data as MutableList<HashMap<String,String>>?
                        if (list != null) {
                            for(l in list){
                                ClassRoom(
                                    l[FIELD_ID],
                                    l[FIELD_NAME]!!,
                                    l[FIELD_SECTION_NAME]!!,
                                    l[FIELD_TEACHERS_NAME]!!,
                                    l[FIELD_COLOR]!!,
                                    l[FIELD_GENDER]!!
                                ).let {classroom ->
                                    result.add(classroom)
                                }
                            }
                        }
                    }
                    else{
                        return@addOnSuccessListener
                    }
                    trySend(result).isSuccess
                }

            awaitClose {  }
        }
    }

    fun addClassRoom(classroom : ClassRoom, user: Users){
        firestore.collection(CLASSROOM_COLLECTION).add(
            hashMapOf(
                NAME_STRING to classroom.name,
                SECTION_NAME to classroom.section_name,
                TEACHER_NAME to classroom.teacher_name,
                ENROLLED_MEMBERS to listOf(hashMapOf(
                    ROLE_FIELD to ROLE_ADMIN,
                    NAME_STRING to classroom.teacher_name,
                    USER_ID to user.id!!
                )),
                FIELD_COLOR to classroom.color,
                FIELD_GENDER to classroom.gender
            )
        ).addOnSuccessListener {
            firestore.collection(USERS_COLLECTION).document(user.id)
                .update(ENROLLED_CLASSROOMS, FieldValue
                    .arrayUnion(ClassRoom(it.id, classroom.section_name, classroom.name,
                        classroom.teacher_name, classroom.color, classroom.gender)))
        }

    }

    private fun getClassroom(cID: String) : Flow<ClassRoom?>{
        return callbackFlow {
            var classroom: ClassRoom?
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .get().addOnSuccessListener {
                    Log.d(TAG,cID)
                    if(it.data != null) {
                        val l = it.data as HashMap<String, String>
                        classroom = ClassRoom(
                            l[FIELD_ID],
                            l[FIELD_NAME]!!,
                            l[FIELD_SECTION_NAME]!!,
                            l[FIELD_TEACHERS_NAME]!!,
                            l[FIELD_COLOR]!!,
                            l[FIELD_GENDER]!!
                        )
                        trySend(classroom).isSuccess
                    }
                }
            awaitClose {  }
        }
    }

    suspend fun getClassRoomMembers(cID: String) : Flow<List<Members>> {
        return callbackFlow {
            val list = mutableListOf<Members>()
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .get().addOnSuccessListener {
                    if(it.data != null){
                        val data = it.data as  HashMap<String,*>
                        val members = data[ENROLLED_MEMBERS] as List<HashMap<String,*>>
                        for(m in members ){
                            list.add(Members(
                               m[NAME_STRING]!!.toString(),
                               m[ROLE_FIELD]!! as Long,
                                m[USER_ID]!!.toString()
                            ))
                        }
                        trySend(list)
                    }
                }
            awaitClose {  }
        }
    }

    suspend fun joinClassRoom(cID : String, user: Users){
        getClassroom(cID).collectLatest {classroom ->
            if(classroom != null){
                firestore.collection(USERS_COLLECTION).document(user.id!!)
                    .update(ENROLLED_CLASSROOMS,
                        FieldValue.arrayUnion(ClassRoom(cID, classroom.section_name, classroom.name,
                            classroom.teacher_name, classroom.color, classroom.gender)))
                firestore.collection(CLASSROOM_COLLECTION).document(cID)
                    .update(ENROLLED_MEMBERS, FieldValue.arrayUnion(
                        hashMapOf(
                            ROLE_FIELD to ROLE_STUDENT,
                            NAME_STRING to user.name,
                            USER_ID to user.id
                    )))
            }
        }
    }

    fun addLectures(lecture: Lecture, cID : String){
        firestore.collection(CLASSROOM_COLLECTION).document(cID)
            .collection(LECTURES_FIELD)
            .add(
                lecture
            )
    }

    fun isAdmin(cID: String, uID: String) : Flow<Boolean> {
        return callbackFlow {
            var result = false
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .get().addOnSuccessListener {
                    if (it.data != null) {
                        val data = it.data as HashMap<String, *>
                        val members = data[ENROLLED_MEMBERS] as List<HashMap<String, *>>
                        for (m in members) {
                            if (m[USER_ID]!!.toString() == uID) {
                                result = m[ROLE_FIELD] as Long == ROLE_ADMIN
                            }
                        }
                        trySend(result)
                    }
                }
            awaitClose{}
        }
    }

    suspend fun getLectures(cID : String) : Flow<MutableList<Lecture>>{
        return callbackFlow {
            val list = mutableListOf<Lecture>()
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .collection(LECTURES_FIELD)
                .get().addOnSuccessListener {
                    val documents = it.documents
                    for(data in documents){
                        if (data != null){
                            list.add(
                                Lecture(data.id,data[LECTURE_URL].toString(), data[NAME_STRING].toString())
                            )
                        }
                    }
                    trySend(list)
                }
            awaitClose {  }
        }
    }

    fun addProgress(cID: String,lID: String, progress : Progress){
        firestore.collection(CLASSROOM_COLLECTION).document(cID)
            .collection(LECTURES_FIELD).document(lID)
            .collection(PROGRESS_FIELD)
            .document(progress.id)
            .set(progress)
    }

    fun getProgress(cID: String, lID : String, uID: String, uname : String) : Flow<Progress>{
        return callbackFlow {
            var flag = true
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .collection(LECTURES_FIELD).document(lID)
                .collection(PROGRESS_FIELD).get().addOnSuccessListener {
                    val documents = it.documents
                    for(data in documents){
                        if (data != null){
                            if(data.id == uID) {
                                trySend(
                                    Progress(
                                        data.id,
                                        data[NAME_STRING].toString(),
                                        (data[COMPLETION_FIELD] as Double).toFloat()
                                    )
                                )
                                flag = false
                            }
                        }
                    }
                    if(flag){
                        trySend(Progress(
                            uID, uname, 0.0f
                        ))
                    }
                }
            awaitClose{}
        }
    }


    fun getProgressList(cID: String, lID : String) : Flow<List<Progress>>{
        return callbackFlow {
            val memberList = mutableListOf<Members>()
            firestore.collection(CLASSROOM_COLLECTION).document(cID)
                .get().addOnSuccessListener {
                    if(it.data != null){
                        val data = it.data as  HashMap<String,*>
                        val members = data[ENROLLED_MEMBERS] as List<HashMap<String,*>>
                        for(m in members ){
                            if(m[ROLE_FIELD]!! == ROLE_ADMIN) continue
                            memberList.add(Members(
                                m[NAME_STRING]!!.toString(),
                                m[ROLE_FIELD]!! as Long,
                                m[USER_ID]!!.toString()
                            ))
                        }
                    }

                    val list = mutableListOf<Progress>()
                    firestore.collection(CLASSROOM_COLLECTION).document(cID)
                        .collection(LECTURES_FIELD).document(lID)
                        .collection(PROGRESS_FIELD).get().addOnSuccessListener {
                            val documents = it.documents
                            for(data in documents){
                                if (data != null){
                                    list.add(
                                        Progress(data.id,data[NAME_STRING].toString(), (data[COMPLETION_FIELD] as Double).toFloat())
                                    )
                                }
                            }

                            Log.d(TAG,memberList.toString())
                            Log.d(TAG, list.toString())
                            val duplicateList = memberList.toList()
                            for(m in  duplicateList){
                                for(l in list){
                                    if(l.id == m.id)
                                        memberList.remove(m)
                                }
                            }
                            for (m in memberList){
                                list.add(
                                    Progress(
                                        m.id,m.name, 0.0f
                                    )
                                )
                            }
                            trySend(list)
                        }

                }




            awaitClose{}
        }
    }



    companion object{
        const val ENROLLED_CLASSROOMS = "enrolled_classrooms"
        const val ROLE_ADMIN = 0L
        const val ROLE_STUDENT = 1L
        const val ROLE_FIELD = "role"
        const val USERS_COLLECTION = "users"
        const val CLASSROOM_COLLECTION = "classroom"
        const val NAME_STRING = "name"
        const val COMPLETION_FIELD = "completion"
        const val SECTION_NAME = "section_name"
        const val TEACHER_NAME = "teacher_name"
        const val ENROLLED_MEMBERS = "members"

        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_SECTION_NAME = "section_name"
        const val FIELD_TEACHERS_NAME = "teacher_name"
        const val FIELD_COLOR = "color"
        const val FIELD_GENDER = "gender"
        const val USER_ID =  "uid"
        const val LECTURES_FIELD = "Lectures"
        const val LECTURE_URL = "url"

        const val PROGRESS_FIELD = "progress"

        const val TAG = "FirebaseRepo"
    }

}