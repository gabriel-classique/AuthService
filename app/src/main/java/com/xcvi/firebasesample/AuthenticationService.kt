package com.xcvi.firebasesample

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AuthenticationService @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {

    fun isLoggedIn(
        onAlreadyLoggedIn: (User) -> Unit
    ): Boolean{
        val firebaseUser = auth.currentUser
        return if(firebaseUser != null){
            onAlreadyLoggedIn(User(firebaseUser.uid, firebaseUser.email.toString()))
            true
        } else {
            false
        }
    }

    fun logout(){
        auth.signOut()
    }

    fun register(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let {
                    val user = User(
                        uid = it.uid,
                        email = it.email.toString()
                    )
                    db.getReference("User").child(user.uid).setValue(user).addOnCompleteListener {
                        onSuccess(user)
                    }
                }
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            it.user?.let {userData ->
                val user = User(
                    uid = userData.uid,
                    email = userData.email.toString()
                )
                onSuccess(user)
            }
        }.addOnFailureListener {
            onFailure(it)
        }
    }

}