package com.example.ncs3.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.ncs3.data.models.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicareRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Lấy danh sách bác sĩ
    suspend fun getDoctors(): List<Doctor> {
        return try {
            val snapshot = firestore.collection("doctors").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Doctor::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Lấy danh sách chuyên khoa
    suspend fun getSpecialties(): List<Specialty> {
        return try {
            val snapshot = firestore.collection("specialties").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Specialty::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Lấy danh sách thuốc
    suspend fun getMedicines(): List<Medicine> {
        return try {
            val snapshot = firestore.collection("medicines").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Medicine::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Lấy lịch hẹn của bệnh nhân
    suspend fun getAppointmentsByPatient(patientId: String): List<Appointment> {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("patientId", patientId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appointment::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Lấy thông tin user
    suspend fun getUser(uid: String): User? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)?.copy(uid = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    // Đặt lịch hẹn mới
    suspend fun createAppointment(appointment: Appointment): Result<String> {
        return try {
            val docRef = firestore.collection("appointments").add(appointment).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
