package com.example.ncs3.service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSeeder(private val firestore: FirebaseFirestore) {

    suspend fun seedAllData() {
        withContext(Dispatchers.IO) {
            try {
                // 👈 BỎ CHECK TỒN TẠI, LUÔN SEED
                println("🔥 Bắt đầu tạo dữ liệu mẫu đồng bộ cho MediCare...")
                val timestamp = System.currentTimeMillis()

                // ============================================================
                // 1. TẠO USERS (Patient + Doctor)
                // ============================================================
                val users = listOf(
                    mapOf(
                        "uid" to "RTwDkAcYwHZSkR0l6Sjlwlb6eGrh2",
                        "fullName" to "Nguyễn Thị Bích Trâm",
                        "email" to "tramntb.24it@vku.udn.vn",
                        "phone" to "0977445566",
                        "role" to "patient",
                        "avatar" to "https://lh3.googleusercontent.com/a/ACg8ocI16jCVek_neQJuqEwZQnzuD-k9snw76BqBjbvki7JS3m3judqh=s96-c",
                        "isActive" to true,
                        "createdAt" to timestamp
                    ),
                    mapOf(
                        "uid" to "user_002",
                        "fullName" to "Nguyễn Văn Nam",
                        "email" to "nguyenvannam@gmail.com",
                        "phone" to "0988777666",
                        "role" to "patient",
                        "avatar" to "https://randomuser.me/api/portraits/men/32.jpg",
                        "isActive" to true,
                        "createdAt" to timestamp
                    ),
                    mapOf(
                        "uid" to "user_003",
                        "fullName" to "Lê Thị Hoa",
                        "email" to "lethihoa@gmail.com",
                        "phone" to "0912345678",
                        "role" to "patient",
                        "avatar" to "https://randomuser.me/api/portraits/women/44.jpg",
                        "isActive" to true,
                        "createdAt" to timestamp
                    )
                )
                // Thêm 10 bác sĩ users
                val doctorUsers = (1..10).map { i ->
                    val names = listOf(
                        "BS Trần Thị Bình", "BS Lê Văn Cường", "BS Nguyễn Thùy Linh",
                        "BS Nguyễn Vĩnh Ngọc", "BS Trần Văn Hùng", "BS Nguyễn Minh Tuấn",
                        "BS Phạm Thị Minh Hạnh", "BS Nguyễn Hoàng Minh", "BS Lê Thị Hải",
                        "BS Nguyễn Gia Bình"
                    )
                    val emails = listOf(
                        "tranb@ncsmedicare.com", "cuonglv@ncsmedicare.com", "linh@ncsmedicare.com",
                        "ngocnv@ncsmedicare.com", "hungtv@ncsmedicare.com", "tuanmn@ncsmedicare.com",
                        "hanhptm@ncsmedicare.com", "minhnh@ncsmedicare.com", "hailt@ncsmedicare.com",
                        "binhng@ncsmedicare.com"
                    )
                    val avatars = listOf(
                        "https://randomuser.me/api/portraits/women/68.jpg",
                        "https://randomuser.me/api/portraits/men/75.jpg",
                        "https://randomuser.me/api/portraits/women/65.jpg",
                        "https://randomuser.me/api/portraits/men/22.jpg",
                        "https://randomuser.me/api/portraits/men/86.jpg",
                        "https://randomuser.me/api/portraits/men/42.jpg",
                        "https://randomuser.me/api/portraits/women/17.jpg",
                        "https://randomuser.me/api/portraits/men/52.jpg",
                        "https://randomuser.me/api/portraits/women/33.jpg",
                        "https://randomuser.me/api/portraits/men/12.jpg"
                    )
                    mapOf(
                        "uid" to "doctor_",
                        "fullName" to names[i-1],
                        "email" to emails[i-1],
                        "phone" to "090",
                        "role" to "doctor",
                        "doctorId" to "D",
                        "avatar" to avatars[i-1],
                        "createdAt" to timestamp
                    )
                }
                val allUsers = users + doctorUsers
                allUsers.forEach { user ->
                    firestore.collection("users").document(user["uid"] as String).set(user).await()
                }
                println("✅ Đã tạo  người dùng")

                // ============================================================
                // 2. TẠO BÁC SĨ (doctors) - 10 BÁC SĨ VỚI HÌNH ẢNH THẬT
                // ============================================================
// 2. TẠO BÁC SĨ (doctors) - 10 BÁC SĨ VỚI HÌNH ẢNH THẬT
// ============================================================
                val doctors = listOf(
                    // D001 - BS Trần Thị Bình
                    mapOf(
                        "id" to "D001",
                        "name" to "BS Trần Thị Bình",
                        "specialty" to "Khoa nội",
                        "specialtyId" to "SP002",
                        "hospital" to "BV Chợ Rẫy",
                        "hospitalId" to "H001",
                        "avatar" to "https://randomuser.me/api/portraits/women/68.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2016/11/29/13/14/attractive-1869761_1280.jpg", // ảnh nữ bác sĩ
                        "rating" to 4.8, "reviews" to 156, "experience" to 12, "price" to 350000,
                        "degree" to "BS.CKII - Chuyên khoa II Nội",
                        "description" to "Bác sĩ nội khoa với 12 năm kinh nghiệm tại BV Chợ Rẫy",
                        "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"),
                        "timeSlots" to listOf("08:00", "09:30", "10:00", "14:00", "15:30"),
                        "phone" to "0903123456", "isVerified" to true, "isOnline" to true
                    ),
                    // D002 - BS Lê Văn Cường
                    mapOf(
                        "id" to "D002",
                        "name" to "BS Lê Văn Cường",
                        "specialty" to "Nội tổng quát",
                        "specialtyId" to "SP001",
                        "hospital" to "PK Hoàn Mỹ",
                        "hospitalId" to "H005",
                        "avatar" to "https://randomuser.me/api/portraits/men/75.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2017/02/23/13/05/doctor-2092178_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.7, "reviews" to 98, "experience" to 10, "price" to 400000,
                        "degree" to "BS.CKI - Nội tổng quát",
                        "description" to "Bác sĩ nội khoa tổng quát với 10 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"),
                        "timeSlots" to listOf("07:30", "09:00", "13:30", "16:00"),
                        "phone" to "0904567890", "isVerified" to true, "isOnline" to true
                    ),
                    // D003 - BS Nguyễn Thùy Linh
                    mapOf(
                        "id" to "D003",
                        "name" to "BS Nguyễn Thùy Linh",
                        "specialty" to "Da liễu thẩm mỹ",
                        "specialtyId" to "SP006",
                        "hospital" to "BV Da liễu TP.HCM",
                        "hospitalId" to "H006",
                        "avatar" to "https://randomuser.me/api/portraits/women/65.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2020/05/31/13/38/doctor-5242517_1280.jpg", // ảnh nữ bác sĩ
                        "rating" to 4.9, "reviews" to 203, "experience" to 8, "price" to 450000,
                        "degree" to "BS.CKII - Da liễu",
                        "description" to "Bác sĩ da liễu thẩm mỹ với 8 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 2", "Thứ 3", "Thứ 5"),
                        "timeSlots" to listOf("08:30", "10:00", "14:30", "16:30"),
                        "phone" to "0905678901", "isVerified" to true, "isOnline" to true
                    ),
                    // D004 - BS Nguyễn Vĩnh Ngọc
                    mapOf(
                        "id" to "D004",
                        "name" to "BS Nguyễn Vĩnh Ngọc",
                        "specialty" to "Cơ xương khớp",
                        "specialtyId" to "SP007",
                        "hospital" to "BV Chợ Rẫy",
                        "hospitalId" to "H001",
                        "avatar" to "https://randomuser.me/api/portraits/men/22.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2019/11/03/20/11/doctor-4599640_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.8, "reviews" to 178, "experience" to 15, "price" to 380000,
                        "degree" to "PGS.TS - Cơ xương khớp",
                        "description" to "Chuyên gia cơ xương khớp với 15 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"),
                        "timeSlots" to listOf("08:00", "10:00", "13:00", "15:00"),
                        "phone" to "0906789012", "isVerified" to true, "isOnline" to true
                    ),
                    // D005 - BS Trần Văn Hùng
                    mapOf(
                        "id" to "D005",
                        "name" to "BS Trần Văn Hùng",
                        "specialty" to "Sản phụ khoa",
                        "specialtyId" to "SP004",
                        "hospital" to "BV Từ Dũ",
                        "hospitalId" to "H004",
                        "avatar" to "https://randomuser.me/api/portraits/men/86.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2020/05/31/13/38/doctor-5242521_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.6, "reviews" to 145, "experience" to 9, "price" to 420000,
                        "degree" to "BS.CKII - Sản phụ khoa",
                        "description" to "Bác sĩ sản phụ khoa với 9 năm kinh nghiệm tại BV Từ Dũ",
                        "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"),
                        "timeSlots" to listOf("08:00", "09:30", "14:00", "16:00"),
                        "phone" to "0907890123", "isVerified" to true, "isOnline" to true
                    ),
                    // D006 - BS Nguyễn Minh Tuấn
                    mapOf(
                        "id" to "D006",
                        "name" to "BS Nguyễn Minh Tuấn",
                        "specialty" to "Thận nhân tạo",
                        "specialtyId" to "SP008",
                        "hospital" to "BV Bạch Mai",
                        "hospitalId" to "H002",
                        "avatar" to "https://randomuser.me/api/portraits/men/42.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2020/11/06/11/30/doctor-5716366_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.5, "reviews" to 87, "experience" to 7, "price" to 360000,
                        "degree" to "BS.CKI - Thận nhân tạo",
                        "description" to "Bác sĩ thận nhân tạo với 7 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"),
                        "timeSlots" to listOf("08:00", "09:00", "14:00", "16:00"),
                        "phone" to "0908901234", "isVerified" to true, "isOnline" to true
                    ),
                    // D007 - BS Phạm Thị Minh Hạnh
                    mapOf(
                        "id" to "D007",
                        "name" to "BS Phạm Thị Minh Hạnh",
                        "specialty" to "Dinh dưỡng lâm sàng",
                        "specialtyId" to "SP009",
                        "hospital" to "PK Hoàn Mỹ",
                        "hospitalId" to "H005",
                        "avatar" to "https://randomuser.me/api/portraits/women/17.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2016/11/29/13/14/attractive-1869761_1280.jpg", // ảnh nữ bác sĩ
                        "rating" to 4.7, "reviews" to 112, "experience" to 6, "price" to 320000,
                        "degree" to "BS.CKI - Dinh dưỡng",
                        "description" to "Bác sĩ dinh dưỡng lâm sàng với 6 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"),
                        "timeSlots" to listOf("08:30", "10:30", "13:30", "15:30"),
                        "phone" to "0909012345", "isVerified" to true, "isOnline" to true
                    ),
                    // D008 - BS Nguyễn Hoàng Minh
                    mapOf(
                        "id" to "D008",
                        "name" to "BS Nguyễn Hoàng Minh",
                        "specialty" to "Mắt",
                        "specialtyId" to "SP010",
                        "hospital" to "BV Mắt TP.HCM",
                        "hospitalId" to "H007",
                        "avatar" to "https://randomuser.me/api/portraits/men/52.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2016/11/23/14/00/doctor-1852910_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.4, "reviews" to 76, "experience" to 5, "price" to 300000,
                        "degree" to "BS.CKI - Mắt",
                        "description" to "Bác sĩ mắt với 5 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"),
                        "timeSlots" to listOf("08:00", "10:00", "14:00", "16:00"),
                        "phone" to "0910123456", "isVerified" to true, "isOnline" to true
                    ),
                    // D009 - BS Lê Thị Hải
                    mapOf(
                        "id" to "D009",
                        "name" to "BS Lê Thị Hải",
                        "specialty" to "Tai mũi họng",
                        "specialtyId" to "SP011",
                        "hospital" to "BV Tai Mũi Họng TP.HCM",
                        "hospitalId" to "H008",
                        "avatar" to "https://randomuser.me/api/portraits/women/33.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2018/02/28/00/20/doctor-3187052_1280.jpg", // ảnh nữ bác sĩ
                        "rating" to 4.6, "reviews" to 94, "experience" to 8, "price" to 340000,
                        "degree" to "BS.CKII - Tai mũi họng",
                        "description" to "Bác sĩ tai mũi họng với 8 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 3", "Thứ 5", "Thứ 7"),
                        "timeSlots" to listOf("08:00", "09:00", "14:00", "15:00"),
                        "phone" to "0911234567", "isVerified" to true, "isOnline" to true
                    ),
                    // D010 - BS Nguyễn Gia Bình
                    mapOf(
                        "id" to "D010",
                        "name" to "BS Nguyễn Gia Bình",
                        "specialty" to "Hồi sức cấp cứu",
                        "specialtyId" to "SP012",
                        "hospital" to "BV Chợ Rẫy",
                        "hospitalId" to "H001",
                        "avatar" to "https://randomuser.me/api/portraits/men/12.jpg",
                        "imageUrl" to "https://cdn.pixabay.com/photo/2018/02/22/02/24/medicine-3170671_1280.jpg", // ảnh nam bác sĩ
                        "rating" to 4.9, "reviews" to 234, "experience" to 18, "price" to 500000,
                        "degree" to "GS.TS - Hồi sức cấp cứu",
                        "description" to "Chuyên gia hàng đầu về hồi sức cấp cứu với 18 năm kinh nghiệm",
                        "schedule" to listOf("Thứ 2", "Thứ 4", "Thứ 6"),
                        "timeSlots" to listOf("08:00", "10:00", "14:00", "16:00"),
                        "phone" to "0912345678", "isVerified" to true, "isOnline" to true
                    )
                )

                // ============================================================
                // 3. TẠO CHUYÊN KHOA (specialties)
                // ============================================================
                val specialties = listOf(
                    mapOf("id" to "SP001", "name" to "Nội tổng quát", "icon" to "🏥", "color" to "#0D47A1", "description" to "Khám bệnh tổng quát, nội khoa"),
                    mapOf("id" to "SP002", "name" to "Khoa nội", "icon" to "🫀", "color" to "#E53935", "description" to "Khám và điều trị bệnh nội khoa"),
                    mapOf("id" to "SP003", "name" to "Nhi khoa", "icon" to "👶", "color" to "#43A047", "description" to "Khám và điều trị bệnh cho trẻ em"),
                    mapOf("id" to "SP004", "name" to "Sản phụ khoa", "icon" to "🤰", "color" to "#EC407A", "description" to "Khám thai, phụ khoa"),
                    mapOf("id" to "SP005", "name" to "Răng hàm mặt", "icon" to "🦷", "color" to "#8D6E63", "description" to "Khám răng miệng, niềng răng"),
                    mapOf("id" to "SP006", "name" to "Da liễu", "icon" to "🧴", "color" to "#7B1FA2", "description" to "Khám và điều trị bệnh về da"),
                    mapOf("id" to "SP007", "name" to "Cơ xương khớp", "icon" to "🦴", "color" to "#FF6D00", "description" to "Khám và điều trị bệnh xương khớp"),
                    mapOf("id" to "SP008", "name" to "Thận nhân tạo", "icon" to "🫘", "color" to "#00897B", "description" to "Điều trị bệnh thận và thận nhân tạo"),
                    mapOf("id" to "SP009", "name" to "Dinh dưỡng lâm sàng", "icon" to "🥗", "color" to "#4CAF50", "description" to "Tư vấn dinh dưỡng và chế độ ăn"),
                    mapOf("id" to "SP010", "name" to "Mắt", "icon" to "👁️", "color" to "#1E88E5", "description" to "Khám và điều trị bệnh về mắt"),
                    mapOf("id" to "SP011", "name" to "Tai mũi họng", "icon" to "👂", "color" to "#00897B", "description" to "Khám và điều trị bệnh tai mũi họng"),
                    mapOf("id" to "SP012", "name" to "Hồi sức cấp cứu", "icon" to "🚑", "color" to "#D32F2F", "description" to "Cấp cứu và hồi sức tích cực")
                )
                specialties.forEach { spec ->
                    firestore.collection("specialties").document(spec["id"] as String).set(spec).await()
                }
                println("✅ Đã tạo  chuyên khoa")

                // ============================================================
                // 4. TẠO BỆNH VIỆN (hospitals)
                // ============================================================
                val hospitals = listOf(
                    mapOf("id" to "H001", "name" to "BV Chợ Rẫy", "address" to "Quận 5, TP.HCM", "phone" to "02838554137", "rating" to 4.8),
                    mapOf("id" to "H002", "name" to "BV Bạch Mai", "address" to "Quận Đống Đa, Hà Nội", "phone" to "02438693731", "rating" to 4.7),
                    mapOf("id" to "H003", "name" to "BV Nhi Đồng 1", "address" to "Quận 10, TP.HCM", "phone" to "02838551122", "rating" to 4.6),
                    mapOf("id" to "H004", "name" to "BV Từ Dũ", "address" to "Quận 1, TP.HCM", "phone" to "02838391313", "rating" to 4.9),
                    mapOf("id" to "H005", "name" to "PK Hoàn Mỹ", "address" to "Quận 10, TP.HCM", "phone" to "02838656111", "rating" to 4.5),
                    mapOf("id" to "H006", "name" to "BV Da liễu TP.HCM", "address" to "Quận 3, TP.HCM", "phone" to "02839322222", "rating" to 4.4),
                    mapOf("id" to "H007", "name" to "BV Mắt TP.HCM", "address" to "Quận 1, TP.HCM", "phone" to "02838234567", "rating" to 4.3),
                    mapOf("id" to "H008", "name" to "BV Tai Mũi Họng TP.HCM", "address" to "Quận 5, TP.HCM", "phone" to "02838551234", "rating" to 4.4)
                )
                hospitals.forEach { hospital ->
                    firestore.collection("hospitals").document(hospital["id"] as String).set(hospital).await()
                }
                println("✅ Đã tạo  bệnh viện")

                // ============================================================
                // 5. TẠO LỊCH HẸN (appointments)
                // ============================================================
                val appointments = listOf(
                    mapOf(
                        "id" to "APP001",
                        "patientId" to "RTwDkAcYwHZSkR0l6Sjlwlb6eGrh2",
                        "patientName" to "Nguyễn Thị Bích Trâm",
                        "patientEmail" to "tramntb.24it@vku.udn.vn",
                        "patientPhone" to "0977445566",
                        "doctorId" to "D001",
                        "doctorName" to "BS Trần Thị Bình",
                        "doctorEmail" to "tranb@ncsmedicare.com",
                        "specialty" to "Khoa nội",
                        "hospital" to "BV Chợ Rẫy",
                        "date" to "20/06/2026",
                        "timeSlot" to "10:00",
                        "symptoms" to "Đau bụng, khó tiêu",
                        "status" to "pending",
                        "price" to 350000,
                        "paymentStatus" to "pending",
                        "paymentMethod" to "momo",
                        "appointmentType" to "Khám bệnh",
                        "createdAt" to timestamp
                    ),
                    mapOf(
                        "id" to "APP002",
                        "patientId" to "user_002",
                        "patientName" to "Nguyễn Văn Nam",
                        "patientEmail" to "nguyenvannam@gmail.com",
                        "patientPhone" to "0988777666",
                        "doctorId" to "D002",
                        "doctorName" to "BS Lê Văn Cường",
                        "doctorEmail" to "cuonglv@ncsmedicare.com",
                        "specialty" to "Nội tổng quát",
                        "hospital" to "PK Hoàn Mỹ",
                        "date" to "21/06/2026",
                        "timeSlot" to "09:00",
                        "symptoms" to "Sốt, ho, đau họng",
                        "status" to "confirmed",
                        "price" to 400000,
                        "paymentStatus" to "paid",
                        "paymentMethod" to "vnpay",
                        "appointmentType" to "Khám bệnh",
                        "createdAt" to timestamp + 60000,
                        "confirmedAt" to timestamp + 3600000
                    ),
                    mapOf(
                        "id" to "APP003",
                        "patientId" to "user_003",
                        "patientName" to "Lê Thị Hoa",
                        "patientEmail" to "lethihoa@gmail.com",
                        "patientPhone" to "0912345678",
                        "doctorId" to "D003",
                        "doctorName" to "BS Nguyễn Thùy Linh",
                        "doctorEmail" to "linh@ncsmedicare.com",
                        "specialty" to "Da liễu thẩm mỹ",
                        "hospital" to "BV Da liễu TP.HCM",
                        "date" to "22/06/2026",
                        "timeSlot" to "14:00",
                        "symptoms" to "Nổi mẩn đỏ trên mặt",
                        "status" to "confirmed",
                        "price" to 450000,
                        "paymentStatus" to "paid",
                        "paymentMethod" to "hospital",
                        "appointmentType" to "Khám bệnh",
                        "createdAt" to timestamp + 120000,
                        "confirmedAt" to timestamp + 480000
                    )
                )
                appointments.forEach { appointment ->
                    firestore.collection("appointments").document(appointment["id"] as String).set(appointment).await()
                }
                println("✅ Đã tạo  lịch hẹn")

                println("🎉 HOÀN THÀNH! Dữ liệu mẫu đã đồng bộ!")
                println("📊 Tổng số:")
                println("   -  users (3 patients, 10 doctors)")
                println("   -  bác sĩ")
                println("   -  chuyên khoa")
                println("   -  bệnh viện")
                println("   -  lịch hẹn")

            } catch (e: Exception) {
                println("❌ Lỗi khi tạo dữ liệu: ")
                e.printStackTrace()
            }
        }
    }
}
