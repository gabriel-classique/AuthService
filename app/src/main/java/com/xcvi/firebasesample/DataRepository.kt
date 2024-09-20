package com.xcvi.firebasesample

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import java.time.Instant


class DataRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {

    suspend fun getData(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {

        val dataList: MutableList<String> = emptyList<String>().toMutableList()

        val user = auth.currentUser
        if (user != null){
            val dataRef = db.reference.child("Steps").child(user.uid)

            dataRef.get().addOnCompleteListener listener@{ task ->
                if (task.isSuccessful) {
                    for (snapShot in task.result.children) {
                        val data = snapShot.getValue(DataModel::class.java)
                        dataList.add(data.toString())
                    }

                } else {
                    onFailure(FirebaseException("Failed"))
                    return@listener
                }
                onSuccess(dataList.toString())
            }
        }



    }

    suspend fun saveData(content: String, onSuccess: () -> Unit) {
        auth.currentUser?.let { user ->
            val dataModel = DataModel(
                content = content,
                id = Instant.now().toEpochMilli().toString()
            )

            db.getReference("Steps").child(user.uid).child(dataModel.id).setValue(dataModel).addOnCompleteListener {
                onSuccess()
            }
        }
    }

}

class FirebaseException(
    override val message: String
) : Exception()


data class DataModel(
    val id: String = "",
    val content: String = ""
)