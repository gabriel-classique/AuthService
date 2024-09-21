package com.xcvi.firebasesample

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.Instant


class DataRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {

    companion object {
        const val DATA_PATH = "Data"
    }

    private fun getDataReference(uid: String): DatabaseReference {
        return db.reference.child(DATA_PATH).child(uid)
    }

    fun observeData(): Flow<Result<List<DataModel>>> {
        return callbackFlow {
            auth.currentUser?.let { user ->
                val postListener = object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val items = dataSnapshot.children.map { item ->
                            item.getValue(DataModel::class.java)
                        }
                        this@callbackFlow.trySendBlocking(Result.success(items.filterNotNull()))
                    }
                }
                getDataReference(user.uid).addValueEventListener(postListener)

                awaitClose {
                    getDataReference(user.uid).removeEventListener(postListener)
                }
            }
        }
    }

    fun getData(onSuccess: (List<DataModel>) -> Unit, onFailure: (Exception) -> Unit) {
        auth.currentUser?.let { user ->
            val dataRef = db.reference.child("Steps").child(user.uid)

            dataRef.get().addOnCompleteListener listener@{ task ->
                if (task.isSuccessful) {

                    val data = task.result.children.map { snapShot ->
                        snapShot.getValue(DataModel::class.java) ?: return@listener
                    }
                    onSuccess(data)


                } else {
                    onFailure(FirebaseException("Failed"))
                    return@listener
                }
            }
        }
    }


    fun saveData(content: String, onSuccess: () -> Unit) {
        auth.currentUser?.let { user ->
            val dataModel = DataModel(
                content = content,
                id = Instant.now().toEpochMilli().toString()
            )

            getDataReference(user.uid).child(dataModel.id).setValue(dataModel)
                .addOnCompleteListener {
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