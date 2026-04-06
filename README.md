# 🏥 MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://material.io/)

## 📋 Thông tin đồ án

| **Mục** | **Thông tin** |
|---------|--------------|
| **Tên đồ án** | MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến |
| **Giảng viên hướng dẫn** | ThS. Nguyễn Đỗ Công Pháp |
| **Sinh viên thực hiện 1** | Nguyễn Thị Bích Trâm - 24IT277 (0934984665) |
| **Sinh viên thực hiện 2** | Trần Viết Đạt - 24IT332 (0396704484) |
| **Lớp** | 24SE1 - Công Nghệ Thông Tin (Kỹ Sư) |
| **Trường** | Đại học Công nghệ Thông tin và Truyền thông Việt - Hàn |
| **Khoa** | Khoa Khoa học Máy tính |

## 📱 Giới thiệu

**MediCare** là ứng dụng di động (nền tảng Android) hỗ trợ người dùng đặt lịch khám bệnh trực tuyến một cách nhanh chóng, thuận tiện và chính xác. Ứng dụng kết nối bệnh nhân với đội ngũ bác sĩ chuyên khoa, giúp tối ưu hóa quy trình đặt lịch, thanh toán và theo dõi lịch sử khám bệnh.

### 🌟 Điểm đặc biệt
- Hệ thống thông báo nhắc lịch thông minh
- Tích hợp thanh toán trực tuyến (VNPay, Momo)
- Cơ chế đánh giá bác sĩ minh bạch
- Giải pháp y tế số toàn diện cho người dùng Việt Nam

## 👥 Đối tượng người dùng

| **Vai trò** | **Mô tả** |
|-------------|-----------|
| 🧑‍⚕️ **Bệnh nhân (Patient)** | Người có nhu cầu đặt lịch khám bệnh, tra cứu bác sĩ và quản lý hồ sơ sức khỏe cá nhân |
| 👨‍🔬 **Bác sĩ (Doctor)** | Người hành nghề y tế muốn quản lý lịch làm việc, lịch hẹn và hồ sơ bệnh nhân trực tuyến |
| 🔧 **Quản trị viên (Admin)** | Người quản lý toàn bộ hệ thống, tài khoản và dữ liệu của nền tảng |

## ✨ Tính năng chi tiết

### 🧑‍⚕️ Bệnh nhân (Patient)
- ✅ **Đăng ký / Đăng nhập** - Email và Google (Firebase Authentication / OAuth2), xác thực email
- 🔍 **Tìm kiếm bác sĩ** - Theo chuyên khoa, địa điểm, tên bác sĩ; lọc theo đánh giá, kinh nghiệm, mức phí
- 📋 **Xem profile bác sĩ** - Thông tin chi tiết, học vấn, kinh nghiệm, đánh giá từ bệnh nhân
- 📅 **Đặt lịch hẹn** - Chọn bác sĩ → Xem lịch rảnh → Chọn ngày giờ → Điền triệu chứng → Xác nhận
- 📆 **Quản lý lịch hẹn** - Xem danh sách, hủy hoặc chỉnh sửa trong thời hạn cho phép
- 💳 **Thanh toán trực tuyến** - VNPay, Momo, thẻ ngân hàng; xem lịch sử giao dịch và hóa đơn điện tử
- ⭐ **Đánh giá bác sĩ** - Đánh giá sao (1-5) và viết nhận xét sau khi kết thúc lịch hẹn
- 📜 **Xem lịch sử khám bệnh** - Tra cứu toàn bộ lịch sử các lần khám theo thời gian
- 🔔 **Nhận thông báo nhắc lịch** - Push notification, email, SMS trước 24h và 1h

### 👨‍🔬 Bác sĩ (Doctor)
- 🗓️ **Quản lý lịch làm việc** - Tạo và cập nhật khung giờ, khóa/mở theo nhu cầu
- ✅ **Xem và xác nhận lịch hẹn** - Xem danh sách theo ngày/tuần, xác nhận hoặc từ chối
- 📁 **Xem hồ sơ bệnh nhân** - Tra cứu thông tin và lịch sử khám của bệnh nhân
- 📊 **Thống kê doanh thu** - Báo cáo theo ngày, tuần, tháng dưới dạng biểu đồ
- 🔔 **Nhận thông báo** - Push notification khi có lịch hẹn mới hoặc bệnh nhân hủy lịch

### 🔧 Quản trị viên (Admin)
- 👨‍⚕️ **Quản lý bác sĩ** - Thêm, xóa, cập nhật, duyệt hồ sơ
- 👤 **Quản lý người dùng** - Xem, khóa, mở khóa tài khoản bệnh nhân
- 📈 **Báo cáo thống kê** - Số lượng lịch hẹn, doanh thu, đánh giá hệ thống
- 💬 **Xử lý khiếu nại** - Tiếp nhận và phản hồi phản ánh
- 💰 **Quản lý thanh toán** - Theo dõi giao dịch, xử lý hoàn tiền

## 📋 Nghiệp vụ chi tiết

### Quy trình đặt lịch
User đăng nhập → Chọn chuyên khoa → Xem danh sách bác sĩ → Chọn bác sĩ
→ Xem lịch rảnh → Chọn ngày/giờ → Điền triệu chứng → Thanh toán (nếu có)
→ Nhận email/SMS xác nhận → Nhận thông báo nhắc lịch (24h, 1h trước)

### Quy trình hủy lịch
| **Thời điểm hủy** | **Chính sách hoàn tiền** |
|-------------------|--------------------------|
| Trước 2h so với giờ hẹn | Hoàn tiền 100% |
| Trong vòng 2h trước giờ hẹn | Hoàn tiền 50% |
| Sau giờ hẹn | Không hoàn tiền |

### Quy trình đánh giá
- Hệ thống tự động mở form đánh giá sau khi lịch hẹn kết thúc
- Bệnh nhân đánh giá sao (1-5) và viết nhận xét
- Đánh giá được hiển thị công khai trên profile bác sĩ

## 🛠 Công nghệ sử dụng

| **Thành phần** | **Công nghệ / Thư viện** | **Mục đích** |
|----------------|--------------------------|---------------|
| **Ngôn ngữ lập trình** | Kotlin / Java | Phát triển ứng dụng Android |
| **Môi trường phát triển** | Android Studio | IDE chính |
| **CSDL cục bộ** | Room Database + SharedPreferences | Lưu trữ dữ liệu offline |
| **CSDL đám mây** | Firebase Firestore / Realtime Database | Đồng bộ dữ liệu thời gian thực |
| **Xác thực** | Firebase Authentication / OAuth2 | Đăng nhập Email / Google |
| **Thông báo** | Firebase Cloud Messaging (FCM) | Push notification nhắc lịch |
| **Thanh toán** | VNPay SDK / Momo API | Thanh toán trực tuyến |
| **Biểu đồ** | MPAndroidChart / AnyChart | Vẽ biểu đồ thống kê |
| **Giao diện** | Material Components | Thiết kế theo chuẩn Material Design |
| **Tác vụ nền** | WorkManager | Lên lịch gửi thông báo, kiểm tra lịch hẹn |
| **Bản đồ** | Google Maps API | Hiển thị vị trí phòng khám, chỉ đường |

## 🏗️ Kiến trúc ứng dụng
- **Architecture Pattern:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Dagger-Hilt
- **Networking:** Retrofit
- **Image Loading:** Glide

## 📂 Cấu trúc dự án
ncs3/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/medicare/
│ │ │ │ ├── activities/ # Các Activity chính
│ │ │ │ ├── adapters/ # RecyclerView Adapters
│ │ │ │ ├── fragments/ # Các Fragment
│ │ │ │ ├── models/ # Data models (Entities)
│ │ │ │ ├── repository/ # Repository pattern
│ │ │ │ ├── viewmodel/ # ViewModels
│ │ │ │ ├── utils/ # Utility classes
│ │ │ │ └── services/ # Firebase, FCM services
│ │ │ └── res/
│ │ │ ├── layout/ # XML layouts
│ │ │ ├── drawable/ # Icons, images
│ │ │ ├── values/ # Colors, strings, themes
│ │ │ └── navigation/ # Navigation graphs
│ ├── build.gradle
├── gradle/
├── build.gradle
└── README.md

## 📊 Kế hoạch thực hiện

| **Thời gian** | **Nội dung** | **Người thực hiện** |
|---------------|--------------|----------------------|
| 23/02 - 01/03/2026 | Nghiên cứu sơ bộ & Lập đề cương | Nguyễn Thị Bích Trâm |
| 19/03/2026 | Nộp đề cương chi tiết | Nguyễn Thị Bích Trâm |
| 24/03 - 31/03/2026 | Thiết kế UI/UX & Prototype (Figma) | Trần Viết Đạt |
| 24/03 - 31/03/2026 | Thiết kế CSDL & Kiến trúc | Trần Viết Đạt |
| 01/04 - 30/04/2026 | Phát triển Frontend & Backend | Cả nhóm |
| 01/05 - 25/05/2026 | Kiểm thử & Viết tài liệu | Nguyễn Thị Bích Trâm |
| 26/05 - 27/05/2026 | Hoàn thiện & Tổng kết | Cả nhóm |
| 27/05/2026 | Nộp kết quả cuối cùng | Nguyễn Thị Bích Trâm |
| 01/06 - 21/06/2026 | Chuẩn bị & Bảo vệ | Cả nhóm |

