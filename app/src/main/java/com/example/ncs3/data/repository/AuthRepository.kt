package com.example.ncs3.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ncs3.data.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.let {
                val doc = firestore.collection("users").document(it.uid).get().await()
                doc.toObject(User::class.java) ?: User(uid = it.uid, email = email)
            } ?: User()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(user: User, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = result.user?.uid ?: ""
            val newUser = user.copy(uid = uid, createdAt = System.currentTimeMillis())
            firestore.collection("users").document(uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
    }
}
