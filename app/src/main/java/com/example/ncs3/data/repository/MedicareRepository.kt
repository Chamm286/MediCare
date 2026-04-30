package com.example.ncs3.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ncs3.data.models.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import android.net.Uri
import java.util.*

class MedicareRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

    suspend fun getUser(uid: String): User? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDoctors(): List<Doctor> {
        return try {
            val snapshot = firestore.collection("doctors").get().await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Doctor(
                    id = doc.id,
                    name = data["name"] as? String ?: "",
                    specialty = data["specialty"] as? String ?: "",
                    hospital = data["hospital"] as? String ?: "",
                    avatar = data["avatar"] as? String ?: "👨‍⚕️",
                    rating = (data["rating"] as? Number)?.toDouble() ?: 0.0,
                    reviews = (data["reviews"] as? Number)?.toInt() ?: 0,
                    experience = (data["experience"] as? Number)?.toInt() ?: 0,
                    price = (data["price"] as? Number)?.toInt() ?: 0,
                    degree = data["degree"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    schedule = (data["schedule"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    timeSlots = (data["timeSlots"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    phone = data["phone"] as? String ?: "",
                    isVerified = data["isVerified"] as? Boolean ?: false
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return try {
            val doc = firestore.collection("doctors").document(doctorId).get().await()
            val data = doc.data ?: return null
            Doctor(
                id = doc.id,
                name = data["name"] as? String ?: "",
                specialty = data["specialty"] as? String ?: "",
                hospital = data["hospital"] as? String ?: "",
                avatar = data["avatar"] as? String ?: "👨‍⚕️",
                rating = (data["rating"] as? Number)?.toDouble() ?: 0.0,
                reviews = (data["reviews"] as? Number)?.toInt() ?: 0,
                experience = (data["experience"] as? Number)?.toInt() ?: 0,
                price = (data["price"] as? Number)?.toInt() ?: 0,
                degree = data["degree"] as? String ?: "",
                description = data["description"] as? String ?: "",
                schedule = (data["schedule"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                timeSlots = (data["timeSlots"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                phone = data["phone"] as? String ?: "",
                isVerified = data["isVerified"] as? Boolean ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun uploadDoctorImage(doctorId: String, imageUri: Uri): Result<String> {
        return try {
            val storage = FirebaseStorage.getInstance()  // SỬA: thêm val storage =
            val storageRef = storage.reference
            val imageRef = storageRef.child("doctors/$doctorId.jpg")
            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            firestore.collection("doctors").document(doctorId)
                .update("imageUrl", downloadUrl.toString())
                .await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    suspend fun getHospitals(): List<Hospital> {
        return try {
            val snapshot = firestore.collection("hospitals").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Hospital::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

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

    suspend fun getCart(userId: String): List<CartItem> {
        return try {
            val snapshot = firestore.collection("cart_items")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(CartItem::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToCart(cartItem: CartItem): Result<String> {
        return try {
            val docRef = firestore.collection("cart_items").add(cartItem).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(cartItemId: String): Result<Boolean> {
        return try {
            firestore.collection("cart_items").document(cartItemId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAppointmentsByPatient(patientId: String): List<Appointment> {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("patientId", patientId)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appointment::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAppointmentsByDoctor(doctorId: String): List<Appointment> {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appointment::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createAppointment(appointment: Appointment): Result<String> {
        return try {
            val docRef = firestore.collection("appointments").add(appointment).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelAppointment(appointmentId: String): Result<Boolean> {
        return try {
            firestore.collection("appointments").document(appointmentId).update("status", "cancelled").await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Boolean> {
        return try {
            firestore.collection("appointments").document(appointmentId)
                .update("status", status)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodayAppointments(doctorId: String): List<Appointment> {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(Date())
        return getAppointmentsByDoctor(doctorId).filter { it.date == today }
    }

    suspend fun seedPromotionsIfEmpty() {
        try {
            val snapshot = firestore.collection("promotions").get().await()
            if (snapshot.isEmpty) {
                val promotions = listOf(
                    mapOf(
                        "icon" to "⚡",
                        "title" to "GIẢM 30%",
                        "description" to "Đặt lịch đầu tiên",
                        "color" to "#FF6B6B",
                        "discount" to "30%",
                        "code" to "MEDI30",
                        "startDate" to System.currentTimeMillis(),
                        "endDate" to System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000
                    ),
                    mapOf(
                        "icon" to "🚚",
                        "title" to "FREE SHIP",
                        "description" to "Đơn từ 200k",
                        "color" to "#4ECDC4",
                        "discount" to "Free",
                        "code" to "SHIPFREE",
                        "startDate" to System.currentTimeMillis(),
                        "endDate" to System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000
                    ),
                    mapOf(
                        "icon" to "🎁",
                        "title" to "TẶNG VOUCHER",
                        "description" to "Khám định kỳ",
                        "color" to "#FFB347",
                        "discount" to "50k",
                        "code" to "VOUCHER50",
                        "startDate" to System.currentTimeMillis(),
                        "endDate" to System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000
                    )
                )
                promotions.forEach { promo ->
                    firestore.collection("promotions").add(promo).await()
                }
                Log.d("MedicareRepo", "Đã tạo ${promotions.size} promotions trong Firestore")
            } else {
                Log.d("MedicareRepo", "Promotions đã tồn tại, không cần seed")
            }
        } catch (e: Exception) {
            Log.e("MedicareRepo", "Lỗi khi seed promotions: ${e.message}")
        }
    }

    suspend fun getActivePromotions(): List<Promotion> {
        return try {
            val snapshot = firestore.collection("promotions")
                .whereGreaterThanOrEqualTo("endDate", System.currentTimeMillis())
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                Promotion(
                    id = doc.id,
                    icon = doc.getString("icon") ?: "🎁",
                    title = doc.getString("title") ?: "ƯU ĐÃI",
                    description = doc.getString("description") ?: "",
                    color = doc.getString("color") ?: "#FF6B6B",
                    discount = doc.getString("discount") ?: "30%",
                    code = doc.getString("code") ?: "",
                    startDate = doc.getLong("startDate") ?: 0,
                    endDate = doc.getLong("endDate") ?: 0
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun verifyDoctor(doctorId: String, isVerified: Boolean): Result<Boolean> {
        return try {
            firestore.collection("doctors").document(doctorId)
                .update("isVerified", isVerified)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveRating(doctorId: String, rating: Number, review: String, patientId: String): Result<Boolean> {
        val ratingFloat = rating.toFloat()
        return try {
            val ratingData = hashMapOf(
                "doctorId" to doctorId,
                "rating" to ratingFloat,
                "review" to review,
                "patientId" to patientId,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("ratings").add(ratingData).await()

            val snapshot = firestore.collection("ratings")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()

            val ratings = snapshot.documents.mapNotNull { doc ->
                doc.getDouble("rating")?.toFloat()
            }

            val averageRating = if (ratings.isNotEmpty()) {
                ratings.average().toFloat()
            } else {
                ratingFloat
            }

            firestore.collection("doctors").document(doctorId)
                .update("rating", averageRating.toDouble())
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDoctorRatings(doctorId: String): List<Rating> {
        return try {
            val snapshot = firestore.collection("ratings")
                .whereEqualTo("doctorId", doctorId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                Rating(
                    id = doc.id,
                    doctorId = doc.getString("doctorId") ?: "",
                    rating = doc.getDouble("rating")?.toFloat() ?: 0f,
                    review = doc.getString("review") ?: "",
                    patientId = doc.getString("patientId") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAppReviews(): List<AppReview> {
        return try {
            val snapshot = firestore.collection("app_reviews")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(AppReview::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveAppReview(userId: String, userName: String, rating: Int, comment: String): Result<String> {
        return try {
            val reviewData = hashMapOf(
                "userId" to userId,
                "userName" to userName,
                "rating" to rating,
                "comment" to comment,
                "createdAt" to System.currentTimeMillis()
            )
            val docRef = firestore.collection("app_reviews").add(reviewData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedSpecialtiesIfEmpty() {
        try {
            val snapshot = firestore.collection("specialties").get().await()
            if (snapshot.isEmpty) {
                val specialties = listOf(
                    mapOf("name" to "Nội tổng quát", "icon" to "🏥", "color" to "#0D47A1", "description" to "Khám và điều trị các bệnh nội khoa"),
                    mapOf("name" to "Tim mạch", "icon" to "❤️", "color" to "#E53935", "description" to "Khám và điều trị bệnh tim mạch"),
                    mapOf("name" to "Nhi khoa", "icon" to "👶", "color" to "#43A047", "description" to "Khám và điều trị bệnh cho trẻ em"),
                    mapOf("name" to "Răng hàm mặt", "icon" to "🦷", "color" to "#00BCD4", "description" to "Khám răng miệng, niềng răng, trồng răng"),
                    mapOf("name" to "Sản phụ khoa", "icon" to "🤰", "color" to "#E91E63", "description" to "Chăm sóc sức khỏe phụ nữ và thai kỳ"),
                    mapOf("name" to "Da liễu", "icon" to "🧴", "color" to "#FF9800", "description" to "Khám và điều trị các bệnh về da"),
                    mapOf("name" to "Mắt", "icon" to "👁️", "color" to "#3F51B5", "description" to "Khám mắt, cận thị, đục thủy tinh thể"),
                    mapOf("name" to "Tai mũi họng", "icon" to "👂", "color" to "#9C27B0", "description" to "Khám và điều trị bệnh tai mũi họng")
                )
                specialties.forEach { specialty ->
                    firestore.collection("specialties").add(specialty).await()
                }
                Log.d("MedicareRepo", "✅ Đã tạo ${specialties.size} chuyên khoa")
            }
        } catch (e: Exception) {
            Log.e("MedicareRepo", "Lỗi seed specialties: ${e.message}")
        }
    }

    suspend fun seedHospitalsIfEmpty() {
        try {
            val snapshot = firestore.collection("hospitals").get().await()
            if (snapshot.isEmpty) {
                val hospitals = listOf(
                    mapOf("name" to "Bệnh viện Bạch Mai", "address" to "78 Đường Giải Phóng, Đống Đa, Hà Nội", "phone" to "024 3869 3731", "rating" to 4.5),
                    mapOf("name" to "Bệnh viện Việt Đức", "address" to "40 Tràng Thi, Hoàn Kiếm, Hà Nội", "phone" to "024 3825 3535", "rating" to 4.6),
                    mapOf("name" to "Bệnh viện Nhi Trung ương", "address" to "18/879 La Thành, Đống Đa, Hà Nội", "phone" to "024 6258 1818", "rating" to 4.4),
                    mapOf("name" to "Bệnh viện Phụ sản TW", "address" to "43 Tràng Thi, Hoàn Kiếm, Hà Nội", "phone" to "024 3825 3436", "rating" to 4.5),
                    mapOf("name" to "Bệnh viện Răng Hàm Mặt TW", "address" to "40A Tràng Thi, Hoàn Kiếm, Hà Nội", "phone" to "024 3825 2016", "rating" to 4.3)
                )
                hospitals.forEach { hospital ->
                    firestore.collection("hospitals").add(hospital).await()
                }
                Log.d("MedicareRepo", "✅ Đã tạo ${hospitals.size} bệnh viện")
            }
        } catch (e: Exception) {
            Log.e("MedicareRepo", "Lỗi seed hospitals: ${e.message}")
        }
    }

    suspend fun seedMedicinesIfEmpty() {
        try {
            val snapshot = firestore.collection("medicines").get().await()
            if (snapshot.isEmpty) {
                val medicines = listOf(
                    mapOf("name" to "Paracetamol 500mg", "price" to 50000, "unit" to "Hộp 10 viên", "category" to "Thuốc kê đơn", "description" to "Giảm đau, hạ sốt", "prescriptionRequired" to false, "stock" to 100, "imageUrl" to ""),
                    mapOf("name" to "Amoxicillin 500mg", "price" to 80000, "unit" to "Hộp 20 viên", "category" to "Thuốc kê đơn", "description" to "Kháng sinh", "prescriptionRequired" to true, "stock" to 50, "imageUrl" to ""),
                    mapOf("name" to "Vitamin C 1000mg", "price" to 120000, "unit" to "Lọ 30 viên", "category" to "Vitamin", "description" to "Bổ sung vitamin C", "prescriptionRequired" to false, "stock" to 200, "imageUrl" to ""),
                    mapOf("name" to "Omega-3 1000mg", "price" to 250000, "unit" to "Lọ 60 viên", "category" to "Thực phẩm chức năng", "description" to "Tốt cho tim mạch", "prescriptionRequired" to false, "stock" to 80, "imageUrl" to ""),
                    mapOf("name" to "Omeprazol 20mg", "price" to 90000, "unit" to "Hộp 14 viên", "category" to "Thuốc kê đơn", "description" to "Điều trị trào ngược dạ dày", "prescriptionRequired" to true, "stock" to 60, "imageUrl" to "")
                )
                medicines.forEach { medicine ->
                    firestore.collection("medicines").add(medicine).await()
                }
                Log.d("MedicareRepo", "✅ Đã tạo ${medicines.size} thuốc")
            }
        } catch (e: Exception) {
            Log.e("MedicareRepo", "Lỗi seed medicines: ${e.message}")
        }
    }

    suspend fun seedDoctorsIfEmpty() {
        try {
            val snapshot = firestore.collection("doctors").get().await()
            if (snapshot.isEmpty) {
                val doctors = listOf(
                    mapOf(
                        "name" to "Trần Thị Bình", "specialty" to "Nội tổng quát", "hospital" to "Bệnh viện Bạch Mai",
                        "avatar" to "👩‍⚕️", "rating" to 4.8, "reviews" to 127, "experience" to 12, "price" to 300000,
                        "degree" to "Tiến sĩ Y khoa - Đại học Y Hà Nội",
                        "description" to "12 năm kinh nghiệm khám và điều trị các bệnh nội khoa.",
                        "schedule" to listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"),
                        "timeSlots" to listOf("08:00-12:00", "08:00-12:00", "14:00-17:00", "08:00-12:00", "14:00-17:00", "08:00-12:00"),
                        "phone" to "0912345678", "isVerified" to true
                    ),
                    mapOf(
                        "name" to "Nguyễn Văn An", "specialty" to "Tim mạch", "hospital" to "Bệnh viện Việt Đức",
                        "avatar" to "👨‍⚕️", "rating" to 4.9, "reviews" to 234, "experience" to 15, "price" to 400000,
                        "degree" to "Phó Giáo sư - Tiến sĩ Y khoa",
                        "description" to "Chuyên gia đầu ngành về tim mạch với 15 năm kinh nghiệm.",
                        "schedule" to listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6"),
                        "timeSlots" to listOf("09:00-11:00", "14:00-16:00", "09:00-11:00", "14:00-16:00", "09:00-11:00"),
                        "phone" to "0987654321", "isVerified" to true
                    ),
                    mapOf(
                        "name" to "Lê Thị Hương", "specialty" to "Nhi khoa", "hospital" to "Bệnh viện Nhi Trung ương",
                        "avatar" to "👩‍⚕️", "rating" to 4.7, "reviews" to 89, "experience" to 8, "price" to 250000,
                        "degree" to "Thạc sĩ Y khoa - Chuyên ngành Nhi",
                        "description" to "8 năm kinh nghiệm khám và điều trị bệnh nhi.",
                        "schedule" to listOf("Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"),
                        "timeSlots" to listOf("08:00-11:00", "08:00-11:00", "14:00-16:00", "14:00-16:00", "08:00-11:00"),
                        "phone" to "0934567890", "isVerified" to true
                    )
                )
                doctors.forEach { doctor ->
                    firestore.collection("doctors").add(doctor).await()
                }
                Log.d("MedicareRepo", "✅ Đã tạo ${doctors.size} bác sĩ")
            }
        } catch (e: Exception) {
            Log.e("MedicareRepo", "Lỗi seed doctors: ${e.message}")
        }
    }
}