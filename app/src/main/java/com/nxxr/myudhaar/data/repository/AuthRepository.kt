package com.nxxr.myudhaar.data.repository

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository handling authentication operations with FirebaseAuth.
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun ensureUserDocumentExists(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val newUser = hashMapOf(
                    "createdAt" to FieldValue.serverTimestamp()
                )
                userRef.set(newUser)
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to check or create user document", it)
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<Unit> = try {


        auth.signInWithCredential(credential).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("AuthRepo", "Exception during sign-in", e)
        Result.failure(e)
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUserId(): String? = auth.currentUser?.uid
}
