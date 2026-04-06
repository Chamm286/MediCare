package com.example.ncs3.data.models

data class MedicineCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: String
)

data class MedicineManufacturer(
    val id: String,
    val name: String,
    val country: String,
    val address: String,
    val phone: String
)

data class MedicineDetail(
    val id: String,
    val name: String,
    val genericName: String,           // Tên gốc
    val categoryId: String,             // Danh mục thuốc
    val manufacturerId: String,         // Nhà sản xuất
    val dosageForm: String,             // Dạng bào chế (viên nén, siro, tiêm...)
    val strength: String,               // Hàm lượng (500mg, 5ml...)
    val packSize: String,               // Quy cách đóng gói
    val price: Int,                     // Giá
    val prescriptionRequired: Boolean,  // Cần kê đơn không
    val indications: String,            // Công dụng / Chỉ định
    val contraindications: String,      // Chống chỉ định
    val dosage: String,                 // Liều dùng
    val administration: String,         // Cách dùng
    val sideEffects: String,            // Tác dụng phụ
    val drugInteractions: String,       // Tương tác thuốc
    val storage: String,                // Bảo quản
    val expiryWarning: String,          // Hạn sử dụng
    val imageUrl: String,               // Hình ảnh
    val relatedDiseases: List<String>   // Bệnh lý liên quan
)

// Dữ liệu mẫu cho các danh mục thuốc
val medicineCategories = listOf(
    MedicineCategory("CAT001", "Kháng sinh", "Thuốc diệt khuẩn, điều trị nhiễm trùng", "💊"),
    MedicineCategory("CAT002", "Giảm đau - Hạ sốt", "Giảm đau, hạ sốt, chống viêm", "🌡️"),
    MedicineCategory("CAT003", "Tim mạch", "Điều trị bệnh tim mạch, huyết áp", "❤️"),
    MedicineCategory("CAT004", "Tiêu hóa", "Điều trị bệnh dạ dày, đầy hơi, khó tiêu", "🍽️"),
    MedicineCategory("CAT005", "Hô hấp", "Điều trị ho, cảm cúm, viêm họng", "🫁"),
    MedicineCategory("CAT006", "Vitamin - Khoáng chất", "Bổ sung vitamin, tăng cường sức đề kháng", "🍊"),
    MedicineCategory("CAT007", "Nội tiết", "Điều trị bệnh tiểu đường, tuyến giáp", "🩸"),
    MedicineCategory("CAT008", "Da liễu", "Điều trị bệnh ngoài da, mụn, nấm", "🧴"),
    MedicineCategory("CAT009", "Thần kinh", "Điều trị đau đầu, mất ngủ, trầm cảm", "🧠"),
    MedicineCategory("CAT010", "Mắt - Tai mũi họng", "Thuốc nhỏ mắt, nhỏ mũi, xịm họng", "👁️")
)

// Dữ liệu nhà sản xuất
val manufacturers = listOf(
    MedicineManufacturer("MFR001", "Sanofi", "Pháp", "Quận 1, TP.HCM", "02838251234"),
    MedicineManufacturer("MFR002", "GSK", "Anh", "Quận 3, TP.HCM", "02839301234"),
    MedicineManufacturer("MFR003", "Pfizer", "Mỹ", "Quận 7, TP.HCM", "02854123456"),
    MedicineManufacturer("MFR004", "Novartis", "Thụy Sĩ", "Quận 2, TP.HCM", "02837441234"),
    MedicineManufacturer("MFR005", "Traphaco", "Việt Nam", "Quận Cầu Giấy, Hà Nội", "02437682345"),
    MedicineManufacturer("MFR006", "DHG Pharma", "Việt Nam", "Cần Thơ", "02923891234"),
    MedicineManufacturer("MFR007", "Imexpharm", "Việt Nam", "Bình Dương", "02743761234")
)

// Dữ liệu thuốc chi tiết
val medicinesDetail = listOf(
    MedicineDetail(
        id = "MED001",
        name = "Amoxicillin 500mg",
        genericName = "Amoxicillin (dưới dạng Amoxicillin trihydrate)",
        categoryId = "CAT001",
        manufacturerId = "MFR001",
        dosageForm = "Viên nang cứng",
        strength = "500mg",
        packSize = "Hộp 10 vỉ x 10 viên",
        price = 85000,
        prescriptionRequired = true,
        indications = "Điều trị nhiễm khuẩn đường hô hấp, tai mũi họng, tiết niệu, da và mô mềm do vi khuẩn nhạy cảm.",
        contraindications = "Quá mẫn với penicillin, người có cơ địa dị ứng.",
        dosage = "Người lớn: 500mg x 3 lần/ngày. Trẻ em: 20-40mg/kg/ngày chia 3 lần.",
        administration = "Uống trước ăn 1 giờ hoặc sau ăn 2 giờ.",
        sideEffects = "Buồn nôn, tiêu chảy, phát ban da, nổi mề đay.",
        drugInteractions = "Probenecid làm tăng nồng độ Amoxicillin trong máu, Allopurinol tăng nguy cơ phát ban.",
        storage = "Bảo quản nơi khô ráo, tránh ánh sáng, nhiệt độ dưới 30°C.",
        expiryWarning = "Không dùng quá 36 tháng kể từ ngày sản xuất.",
        imageUrl = "https://example.com/amoxicillin.jpg",
        relatedDiseases = listOf("Viêm họng", "Viêm phế quản", "Viêm amidan", "Viêm xoang")
    ),
    MedicineDetail(
        id = "MED002",
        name = "Paracetamol 500mg",
        genericName = "Paracetamol (Acetaminophen)",
        categoryId = "CAT002",
        manufacturerId = "MFR002",
        dosageForm = "Viên nén bao phim",
        strength = "500mg",
        packSize = "Hộp 20 viên",
        price = 45000,
        prescriptionRequired = false,
        indications = "Giảm đau, hạ sốt trong các trường hợp đau đầu, đau răng, đau cơ, đau khớp, sốt do cảm cúm.",
        contraindications = "Suy gan nặng, quá mẫn với paracetamol.",
        dosage = "Người lớn: 500mg - 1000mg x 3-4 lần/ngày, cách nhau 4-6 giờ. Tối đa 4000mg/ngày.",
        administration = "Uống với nước, có thể uống cùng hoặc không cùng thức ăn.",
        sideEffects = "Hiếm gặp: phản ứng da, giảm bạch cầu, hạ huyết áp.",
        drugInteractions = "Rượu làm tăng độc tính với gan, Warfarin tăng tác dụng chống đông.",
        storage = "Bảo quản nơi khô ráo, nhiệt độ phòng 15-30°C.",
        expiryWarning = "Không dùng quá 36 tháng kể từ ngày sản xuất.",
        imageUrl = "https://example.com/paracetamol.jpg",
        relatedDiseases = listOf("Đau đầu", "Sốt", "Đau cơ", "Cảm cúm", "Đau răng")
    ),
    MedicineDetail(
        id = "MED003",
        name = "Omeprazole 20mg",
        genericName = "Omeprazole",
        categoryId = "CAT004",
        manufacturerId = "MFR003",
        dosageForm = "Viên nang bao tan trong ruột",
        strength = "20mg",
        packSize = "Hộp 14 viên",
        price = 120000,
        prescriptionRequired = true,
        indications = "Điều trị trào ngược dạ dày thực quản, loét dạ dày tá tràng, hội chứng Zollinger-Ellison.",
        contraindications = "Quá mẫn với omeprazole, suy gan nặng.",
        dosage = "Trào ngược: 20mg x 1 lần/ngày trong 4 tuần. Loét: 20mg x 1-2 lần/ngày.",
        administration = "Uống trước ăn 30 phút, nuốt nguyên viên.",
        sideEffects = "Đau đầu, chóng mặt, buồn nôn, táo bón, đầy hơi.",
        drugInteractions = "Giảm hấp thu ketoconazole, itraconazole, tăng tác dụng diazepam, warfarin.",
        storage = "Bảo quản nơi khô ráo, tránh ẩm, dưới 30°C.",
        expiryWarning = "Không dùng quá 24 tháng.",
        imageUrl = "https://example.com/omeprazole.jpg",
        relatedDiseases = listOf("Trào ngược dạ dày", "Viêm loét dạ dày", "Đau dạ dày", "Ợ chua")
    ),
    MedicineDetail(
        id = "MED004",
        name = "Ambroxol 30mg",
        genericName = "Ambroxol hydrochloride",
        categoryId = "CAT005",
        manufacturerId = "MFR004",
        dosageForm = "Viên nén",
        strength = "30mg",
        packSize = "Hộp 10 vỉ x 10 viên",
        price = 65000,
        prescriptionRequired = false,
        indications = "Long đờm, tiêu nhầy trong các bệnh hô hấp cấp và mãn tính như viêm phế quản, hen phế quản.",
        contraindications = "Quá mẫn với ambroxol, loét dạ dày tá tràng.",
        dosage = "Người lớn: 30mg x 3 lần/ngày. Trẻ em: 15mg x 2-3 lần/ngày.",
        administration = "Uống sau ăn với nhiều nước.",
        sideEffects = "Buồn nôn, tiêu chảy, khô miệng, chảy nước mũi.",
        drugInteractions = "Dùng cùng kháng sinh (amoxicillin, cefuroxime) làm tăng nồng độ kháng sinh trong phổi.",
        storage = "Bảo quản nơi khô mát, tránh ánh sáng.",
        expiryWarning = "Hạn dùng 36 tháng.",
        imageUrl = "https://example.com/ambroxol.jpg",
        relatedDiseases = listOf("Ho có đờm", "Viêm phế quản", "Hen suyễn", "Cảm lạnh")
    ),
    MedicineDetail(
        id = "MED005",
        name = "Amlodipine 5mg",
        genericName = "Amlodipine besylate",
        categoryId = "CAT003",
        manufacturerId = "MFR001",
        dosageForm = "Viên nén",
        strength = "5mg",
        packSize = "Hộp 30 viên",
        price = 150000,
        prescriptionRequired = true,
        indications = "Điều trị tăng huyết áp, đau thắt ngực ổn định, bệnh động mạch vành.",
        contraindications = "Sốc tim, hẹp van động mạch chủ nặng, suy tim không ổn định.",
        dosage = "Tăng huyết áp: 5mg x 1 lần/ngày. Đau thắt ngực: 5-10mg x 1 lần/ngày.",
        administration = "Uống cùng hoặc không cùng thức ăn, uống cố định giờ trong ngày.",
        sideEffects = "Phù ngoại biên, chóng mặt, đỏ bừng mặt, đánh trống ngực, mệt mỏi.",
        drugInteractions = "Grapefruit làm tăng nồng độ amlodipine, thuốc chống nấm azole làm tăng tác dụng.",
        storage = "Bảo quản nơi khô ráo, nhiệt độ phòng 20-25°C.",
        expiryWarning = "Hạn dùng 24 tháng.",
        imageUrl = "https://example.com/amlodipine.jpg",
        relatedDiseases = listOf("Cao huyết áp", "Đau thắt ngực", "Bệnh tim mạch", "Suy tim")
    ),
    MedicineDetail(
        id = "MED006",
        name = "Berocca",
        genericName = "Vitamin B+C",
        categoryId = "CAT006",
        manufacturerId = "MFR002",
        dosageForm = "Viên sủi",
        strength = "Vitamin B1 15mg, B2 15mg, B6 10mg, B12 10mcg, C 500mg",
        packSize = "Ống 15 viên",
        price = 180000,
        prescriptionRequired = false,
        indications = "Bổ sung vitamin nhóm B và C, tăng cường năng lượng, giảm mệt mỏi, tăng đề kháng.",
        contraindications = "Quá mẫn với thành phần, suy thận nặng.",
        dosage = "1 viên/ngày pha với 200ml nước, uống sau ăn sáng.",
        administration = "Hòa tan viên sủi vào nước, uống ngay sau khi tan.",
        sideEffects = "Hiếm gặp: buồn nôn, khó tiêu, nước tiểu vàng sáng (do vitamin B2).",
        drugInteractions = "Levodopa bị giảm tác dụng, Phenytoin tăng nguy cơ độc tính.",
        storage = "Bảo quản nơi khô ráo, tránh ẩm, đậy kín sau khi dùng.",
        expiryWarning = "Hạn dùng 24 tháng, viên sủi dễ hút ẩm.",
        imageUrl = "https://example.com/berocca.jpg",
        relatedDiseases = listOf("Mệt mỏi", "Suy nhược cơ thể", "Thiếu vitamin", "Tăng đề kháng")
    ),
    MedicineDetail(
        id = "MED007",
        name = "Metformin 500mg",
        genericName = "Metformin hydrochloride",
        categoryId = "CAT007",
        manufacturerId = "MFR005",
        dosageForm = "Viên nén bao phim",
        strength = "500mg",
        packSize = "Hộp 100 viên",
        price = 95000,
        prescriptionRequired = true,
        indications = "Điều trị đái tháo đường type 2, đặc biệt ở bệnh nhân thừa cân.",
        contraindications = "Suy thận nặng, suy gan, nhiễm toan chuyển hóa, nghiện rượu.",
        dosage = "Khởi đầu 500mg x 1-2 lần/ngày, tăng dần, tối đa 2000mg/ngày.",
        administration = "Uống trong hoặc sau bữa ăn để giảm tác dụng phụ trên tiêu hóa.",
        sideEffects = "Buồn nôn, tiêu chảy, đau bụng, chán ăn, rối loạn vị giác.",
        drugInteractions = "Thuốc cản quang iod, thuốc lợi tiểu, corticosteroids làm tăng nguy cơ nhiễm toan.",
        storage = "Bảo quản nơi khô ráo, tránh ánh sáng, nhiệt độ dưới 30°C.",
        expiryWarning = "Hạn dùng 36 tháng.",
        imageUrl = "https://example.com/metformin.jpg",
        relatedDiseases = listOf("Tiểu đường type 2", "Kháng insulin", "Hội chứng buồng trứng đa nang")
    ),
    MedicineDetail(
        id = "MED008",
        name = "Melasma Cream",
        genericName = "Hydroquinone 2% + Tretinoin 0.05%",
        categoryId = "CAT008",
        manufacturerId = "MFR006",
        dosageForm = "Kem bôi ngoài da",
        strength = "Hydroquinone 2%, Tretinoin 0.05%",
        packSize = "Tuýp 15g",
        price = 220000,
        prescriptionRequired = true,
        indications = "Điều trị nám da, tàn nhang, sạm da, tăng sắc tố da.",
        contraindications = "Dị ứng thành phần, phụ nữ có thai, cho con bú.",
        dosage = "Bôi 1 lần/ngày vào buổi tối, chỉ bôi lên vùng da bị nám.",
        administration = "Rửa sạch vùng da bôi, bôi 1 lớp mỏng, tránh ánh sáng sau khi bôi.",
        sideEffects = "Đỏ da, bong tróc, kích ứng, khô da, nhạy cảm với ánh sáng.",
        drugInteractions = "Tránh dùng cùng các sản phẩm tẩy tế bào chết, retinoid khác.",
        storage = "Bảo quản nơi thoáng mát, tránh ánh sáng, nhiệt độ dưới 25°C.",
        expiryWarning = "Hạn dùng 24 tháng, không dùng quá 3-6 tháng liên tục.",
        imageUrl = "https://example.com/melasma.jpg",
        relatedDiseases = listOf("Nám da", "Tàn nhang", "Tăng sắc tố da", "Sạm da")
    ),
    MedicineDetail(
        id = "MED009",
        name = "Diazepam 5mg",
        genericName = "Diazepam",
        categoryId = "CAT009",
        manufacturerId = "MFR007",
        dosageForm = "Viên nén",
        strength = "5mg",
        packSize = "Hộp 50 viên",
        price = 75000,
        prescriptionRequired = true,
        indications = "Lo âu, căng thẳng, mất ngủ, co thắt cơ, cắt cơn động kinh.",
        contraindications = "Nhược cơ nặng, suy hô hấp, glôcôm góc đóng, nghiện rượu.",
        dosage = "Lo âu: 5-10mg x 2-3 lần/ngày. Mất ngủ: 5-10mg trước khi ngủ.",
        administration = "Uống với nước, tránh dùng kéo dài do nguy cơ nghiện.",
        sideEffects = "Buồn ngủ, chóng mặt, yếu cơ, lú lẫn, nhìn đôi, nghiện thuốc.",
        drugInteractions = "Rượu, thuốc ngủ, thuốc chống trầm cảm làm tăng tác dụng an thần.",
        storage = "Bảo quản nơi khô ráo, tránh ánh sáng, xa tầm tay trẻ em.",
        expiryWarning = "Hạn dùng 36 tháng, thuốc gây nghiện cần kiểm soát.",
        imageUrl = "https://example.com/diazepam.jpg",
        relatedDiseases = listOf("Rối loạn lo âu", "Mất ngủ", "Co thắt cơ", "Động kinh")
    ),
    MedicineDetail(
        id = "MED010",
        name = "Ofloxacin 0.3% Eye Drops",
        genericName = "Ofloxacin",
        categoryId = "CAT010",
        manufacturerId = "MFR004",
        dosageForm = "Dung dịch nhỏ mắt",
        strength = "0.3%",
        packSize = "Lọ 5ml",
        price = 35000,
        prescriptionRequired = true,
        indications = "Viêm kết mạc, viêm giác mạc, viêm bờ mi, loét giác mạc do vi khuẩn.",
        contraindications = "Quá mẫn với fluoroquinolone, trẻ em dưới 1 tuổi.",
        dosage = "Nhỏ 1-2 giọt mỗi lần, 4 lần/ngày.",
        administration = "Rửa tay trước khi nhỏ, không chạm đầu lọ vào mắt, lắc đều trước khi dùng.",
        sideEffects = "Kích ứng mắt, đỏ mắt, chảy nước mắt, nhìn mờ tạm thời.",
        drugInteractions = "Không dùng cùng các thuốc nhỏ mắt khác trong vòng 5 phút.",
        storage = "Bảo quản nơi thoáng mát, tránh ánh sáng, đậy kín sau khi dùng.",
        expiryWarning = "Hạn dùng 24 tháng, chỉ dùng 14 ngày sau khi mở nắp.",
        imageUrl = "https://example.com/ofloxacin.jpg",
        relatedDiseases = listOf("Đau mắt đỏ", "Viêm kết mạc", "Viêm giác mạc", "Loét giác mạc")
    )
)