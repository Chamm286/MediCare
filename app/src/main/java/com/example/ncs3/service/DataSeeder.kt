package com.example.ncs3.service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSeeder(private val firestore: FirebaseFirestore) {

    suspend fun seedAllData() {
        withContext(Dispatchers.IO) {
            try {
                // Kiểm tra nếu đã có dữ liệu thì không tạo lại
                val snapshot = firestore.collection("doctors").limit(1).get().await()
                if (snapshot.documents.isNotEmpty()) {
                    println("✅ Dữ liệu đã tồn tại, bỏ qua seed")
                    return@withContext
                }

                println("🔥 Đang tạo dữ liệu mẫu cho MediCare...")

                // 1. TẠO CHUYÊN KHOA (specialties)
                val specialties = listOf(
                    mapOf("id" to "SP001", "name" to "Tim mạch", "icon" to "❤️", "color" to "#E53935", "description" to "Khám và điều trị các bệnh về tim mạch, huyết áp"),
                    mapOf("id" to "SP002", "name" to "Nội tổng quát", "icon" to "🏥", "color" to "#0D47A1", "description" to "Khám bệnh tổng quát, nội khoa"),
                    mapOf("id" to "SP003", "name" to "Nhi khoa", "icon" to "👶", "color" to "#43A047", "description" to "Khám và điều trị bệnh cho trẻ em"),
                    mapOf("id" to "SP004", "name" to "Sản phụ khoa", "icon" to "🤰", "color" to "#EC407A", "description" to "Khám thai, phụ khoa"),
                    mapOf("id" to "SP005", "name" to "Răng hàm mặt", "icon" to "🦷", "color" to "#8D6E63", "description" to "Khám răng miệng, niềng răng"),
                    mapOf("id" to "SP006", "name" to "Da liễu", "icon" to "💆", "color" to "#7B1FA2", "description" to "Khám và điều trị bệnh về da"),
                    mapOf("id" to "SP007", "name" to "Mắt", "icon" to "👁️", "color" to "#1E88E5", "description" to "Khám mắt, cận thị"),
                    mapOf("id" to "SP008", "name" to "Tai - Mũi - Họng", "icon" to "👂", "color" to "#00897B", "description" to "Khám tai mũi họng"),
                    mapOf("id" to "SP009", "name" to "Thần kinh", "icon" to "🧠", "color" to "#5E35B1", "description" to "Khám bệnh thần kinh"),
                    mapOf("id" to "SP010", "name" to "Cơ xương khớp", "icon" to "🦴", "color" to "#FF6D00", "description" to "Khám xương khớp")
                )
                specialties.forEach { spec ->
                    firestore.collection("specialties").document(spec["id"] as String).set(spec).await()
                }
                println("✅ Đã tạo ${specialties.size} chuyên khoa")

                // 2. TẠO BỆNH VIỆN (hospitals)
                val hospitals = listOf(
                    mapOf("id" to "H001", "name" to "BV Chợ Rẫy", "address" to "Quận 5, TP.HCM", "phone" to "02838554137", "rating" to 4.8),
                    mapOf("id" to "H002", "name" to "BV Bạch Mai", "address" to "Quận Đống Đa, Hà Nội", "phone" to "02438693731", "rating" to 4.7),
                    mapOf("id" to "H003", "name" to "BV Nhi Đồng 1", "address" to "Quận 10, TP.HCM", "phone" to "02838551122", "rating" to 4.6),
                    mapOf("id" to "H004", "name" to "BV Từ Dũ", "address" to "Quận 1, TP.HCM", "phone" to "02838391313", "rating" to 4.9),
                    mapOf("id" to "H005", "name" to "PK Hoàn Mỹ", "address" to "Quận 10, TP.HCM", "phone" to "02838656111", "rating" to 4.5),
                    mapOf("id" to "H006", "name" to "BV Đa khoa Tâm Anh", "address" to "Quận 7, TP.HCM", "phone" to "02837446666", "rating" to 4.9),
                    mapOf("id" to "H007", "name" to "BV Vinmec Times City", "address" to "Hai Bà Trưng, Hà Nội", "phone" to "02439743566", "rating" to 5.0)
                )
                hospitals.forEach { hospital ->
                    firestore.collection("hospitals").document(hospital["id"] as String).set(hospital).await()
                }
                println("✅ Đã tạo ${hospitals.size} bệnh viện")

                // 3. TẠO BÁC SĨ (doctors)
                val doctors = listOf(
                    mapOf("id" to "D001", "name" to "Trần Thị Binh", "specialtyId" to "SP001", "hospitalId" to "H001", "avatar" to "👩‍⚕️", "rating" to 4.9, "experience" to 15, "price" to 500000, "degree" to "PGS.TS", "description" to "Chuyên gia tim mạch", "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"), "timeSlots" to listOf("08:00", "09:30", "14:00", "15:30"), "isOnline" to true, "phone" to "0903123456"),
                    mapOf("id" to "D002", "name" to "Lê Văn Cường", "specialtyId" to "SP002", "hospitalId" to "H005", "avatar" to "👨‍⚕️", "rating" to 4.8, "experience" to 10, "price" to 400000, "degree" to "BS.CKII", "description" to "Bác sĩ nội khoa", "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"), "timeSlots" to listOf("07:30", "09:00", "13:30", "16:00"), "isOnline" to true, "phone" to "0904567890"),
                    mapOf("id" to "D003", "name" to "Nguyễn Thị Ngọc Dung", "specialtyId" to "SP003", "hospitalId" to "H003", "avatar" to "👩‍⚕️", "rating" to 4.9, "experience" to 12, "price" to 450000, "degree" to "PGS.TS", "description" to "Chuyên gia nhi khoa", "schedule" to listOf("Thứ 2", "Thứ 3", "Thứ 5"), "timeSlots" to listOf("08:30", "10:00", "14:30", "16:30"), "isOnline" to true, "phone" to "0912345678"),
                    mapOf("id" to "D004", "name" to "Phạm Văn Tâm", "specialtyId" to "SP004", "hospitalId" to "H004", "avatar" to "👨‍⚕️", "rating" to 4.7, "experience" to 8, "price" to 350000, "degree" to "BS.CKI", "description" to "Bác sĩ sản phụ khoa", "schedule" to listOf("Thứ 4", "Thứ 6", "Chủ nhật"), "timeSlots" to listOf("09:00", "11:00", "15:00", "17:00"), "isOnline" to true, "phone" to "0934567890"),
                    mapOf("id" to "D005", "name" to "Hoàng Minh Tâm", "specialtyId" to "SP005", "hospitalId" to "H001", "avatar" to "👨‍⚕️", "rating" to 4.8, "experience" to 9, "price" to 380000, "degree" to "BS.CKII", "description" to "Chuyên gia chỉnh nha", "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 7"), "timeSlots" to listOf("08:00", "10:30", "13:00", "15:30"), "isOnline" to false, "phone" to "0945678901"),
                    mapOf("id" to "D006", "name" to "Trần Thị Mai", "specialtyId" to "SP006", "hospitalId" to "H002", "avatar" to "👩‍⚕️", "rating" to 4.9, "experience" to 11, "price" to 420000, "degree" to "TS.BS", "description" to "Chuyên da liễu", "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"), "timeSlots" to listOf("09:00", "11:00", "14:00", "16:00"), "isOnline" to true, "phone" to "0956789012"),
                    mapOf("id" to "D007", "name" to "Nguyễn Hoàng Anh", "specialtyId" to "SP007", "hospitalId" to "H006", "avatar" to "👨‍⚕️", "rating" to 4.8, "experience" to 14, "price" to 480000, "degree" to "PGS.TS", "description" to "Chuyên gia nhãn khoa", "schedule" to listOf("Thứ 2", "Thứ 5", "Thứ 6"), "timeSlots" to listOf("08:00", "09:30", "13:30", "15:00"), "isOnline" to false, "phone" to "0967890123"),
                    mapOf("id" to "D008", "name" to "Lê Thị Hương", "specialtyId" to "SP008", "hospitalId" to "H007", "avatar" to "👩‍⚕️", "rating" to 4.7, "experience" to 10, "price" to 370000, "degree" to "BS.CKII", "description" to "Bác sĩ tai mũi họng", "schedule" to listOf("Thứ 3", "Thứ 4", "Thứ 7"), "timeSlots" to listOf("08:30", "10:30", "14:30", "16:30"), "isOnline" to true, "phone" to "0978901234"),
                    mapOf("id" to "D009", "name" to "Vũ Đức Minh", "specialtyId" to "SP009", "hospitalId" to "H002", "avatar" to "👨‍⚕️", "rating" to 4.9, "experience" to 16, "price" to 550000, "degree" to "GS.TS", "description" to "Chuyên gia thần kinh", "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 5"), "timeSlots" to listOf("09:00", "11:00", "14:00", "16:00"), "isOnline" to false, "phone" to "0989012345"),
                    mapOf("id" to "D010", "name" to "Phan Thị Thu", "specialtyId" to "SP010", "hospitalId" to "H003", "avatar" to "👩‍⚕️", "rating" to 4.8, "experience" to 12, "price" to 430000, "degree" to "TS.BS", "description" to "Chuyên cơ xương khớp", "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 6"), "timeSlots" to listOf("07:30", "09:30", "13:00", "15:30"), "isOnline" to true, "phone" to "0990123456")
                )
                doctors.forEach { doctor ->
                    firestore.collection("doctors").document(doctor["id"] as String).set(doctor).await()
                }
                println("✅ Đã tạo ${doctors.size} bác sĩ")

                // 4. TẠO THUỐC (medicines)
                val medicines = listOf(
                    mapOf("id" to "M001", "name" to "Paracetamol 500mg", "category" to "Giảm đau", "price" to 5000, "dosage" to "1 viên/lần", "frequency" to "2 lần/ngày", "instruction" to "Uống sau ăn"),
                    mapOf("id" to "M002", "name" to "Amoxicillin 500mg", "category" to "Kháng sinh", "price" to 8000, "dosage" to "1 viên/lần", "frequency" to "3 lần/ngày", "instruction" to "Uống trước ăn"),
                    mapOf("id" to "M003", "name" to "Oresol", "category" to "Bù nước", "price" to 3000, "dosage" to "1 gói/lần", "frequency" to "Khi cần", "instruction" to "Pha với 200ml nước"),
                    mapOf("id" to "M004", "name" to "Efferalgan 500mg", "category" to "Giảm đau", "price" to 7000, "dosage" to "1 viên/lần", "frequency" to "2 lần/ngày", "instruction" to "Uống sau ăn"),
                    mapOf("id" to "M005", "name" to "Vitamin C 500mg", "category" to "Vitamin", "price" to 4000, "dosage" to "1 viên/ngày", "frequency" to "1 lần/ngày", "instruction" to "Uống sau ăn"),
                    mapOf("id" to "M006", "name" to "Aspirin 100mg", "category" to "Giảm đau", "price" to 6000, "dosage" to "1 viên/lần", "frequency" to "2 lần/ngày", "instruction" to "Uống sau ăn"),
                    mapOf("id" to "M007", "name" to "Loratadine 10mg", "category" to "Kháng histamin", "price" to 9000, "dosage" to "1 viên/ngày", "frequency" to "1 lần/ngày", "instruction" to "Uống trước ăn"),
                    mapOf("id" to "M008", "name" to "Omeprazole 20mg", "category" to "Dạ dày", "price" to 12000, "dosage" to "1 viên/ngày", "frequency" to "1 lần/ngày", "instruction" to "Uống trước ăn 30 phút")
                )
                medicines.forEach { medicine ->
                    firestore.collection("medicines").document(medicine["id"] as String).set(medicine).await()
                }
                println("✅ Đã tạo ${medicines.size} loại thuốc")

                println("🎉 HOÀN THÀNH! Đã tạo toàn bộ dữ liệu mẫu!")
            } catch (e: Exception) {
                println("❌ Lỗi khi tạo dữ liệu: ${e.message}")
            }
        }
    }
}