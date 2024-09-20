package com.xcvi.firebasesample

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import java.time.Instant

class DataRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {

    suspend fun getDataById(id: String, onSuccess: (DataModel)-> Unit) {
        val dataRef = db.getReference("User")
        dataRef.child(id).get().addOnSuccessListener {
            if(it.exists()){
                val uid = it.child("uid").value as String
                val content = it.child("content").value as String
                onSuccess(
                    DataModel(id = id, uid = uid, content = content)
                )
            }
        }
    }

    suspend fun saveData(content: String, onSuccess: ()-> Unit) {
        auth.currentUser?.let { user ->
            val dataModel = DataModel(
                uid = user.uid,
                content = content,
                id = Instant.now().toEpochMilli().toString()
            )

            db.getReference("Data").child(dataModel.id).setValue(dataModel).addOnCompleteListener {
                onSuccess()
            }
        }
    }

}


data class DataModel(
    val id: String,
    val uid: String,
    val content: String
)