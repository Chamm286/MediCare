# 🏥 MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://material.io/)

## 📌 Giới thiệu chung

**MediCare** là nền tảng đặt lịch khám bệnh trực tuyến trên Android, được xây dựng nhằm giải quyết các vấn đề:

- ⏳ Chờ đợi lâu khi đặt lịch khám truyền thống.
- 📋 Quản lý lịch hẹn và hồ sơ bệnh án rời rạc.
- 🔔 Thiếu thông báo nhắc lịch thông minh.
- 💳 Không có kênh thanh toán trực tuyến minh bạch.

Ứng dụng hướng đến việc số hóa quy trình khám chữa bệnh, tạo sự kết nối liền mạch giữa **bệnh nhân**, **bác sĩ** và **quản trị viên**.

---

## 👥 Đối tượng sử dụng & Nghiệp vụ chi tiết

### 🧑‍⚕️ Bệnh nhân (Patient)

| **Nhóm chức năng** | **Mô tả nghiệp vụ** |
|-------------------|----------------------|
| 🔐 Xác thực & Hồ sơ | Đăng ký email/Google, xác thực OTP, cập nhật thông tin cá nhân, hồ sơ sức khỏe cơ bản. |
| 🔍 Tìm kiếm bác sĩ | Lọc theo chuyên khoa, địa điểm, giá khám, đánh giá, năm kinh nghiệm. Xem profile chi tiết bác sĩ. |
| 📅 Đặt lịch & Thanh toán | Chọn khung giờ trống → Nhập triệu chứng → Chọn phương thức thanh toán (VNPay/Momo) → Xác nhận. |
| 📆 Quản lý lịch hẹn | Xem danh sách lịch đã đặt, hủy lịch (kèm chính sách hoàn tiền), chỉnh sửa thông tin. |
| ⭐ Đánh giá | Sau khi khám xong, hệ thống mở form đánh giá sao (1–5) và nhận xét. Đánh giá hiển thị công khai. |
| 📜 Lịch sử khám | Tra cứu toàn bộ lần khám, xem hóa đơn điện tử, tải về. |
| 🔔 Thông báo | Nhận thông báo qua app, email, SMS trước 24h và 1h khi có lịch hẹn. |

### 👨‍⚕️ Bác sĩ (Doctor)

| **Nhóm chức năng** | **Mô tả nghiệp vụ** |
|-------------------|----------------------|
| 🗓️ Quản lý lịch làm việc | Tạo khung giờ cố định hoặc linh động, khóa/mở lịch theo ngày, tuần. |
| ✅ Xác nhận lịch hẹn | Xem danh sách bệnh nhân đã đặt, xác nhận hoặc từ chối (có lý do). |
| 📁 Hồ sơ bệnh nhân | Tra cứu lịch sử khám, ghi chú chuyên môn cho từng lần hẹn. |
| 📊 Thống kê | Xem doanh thu, số lượng bệnh nhân theo ngày/tuần/tháng qua biểu đồ. |
| 🔔 Thông báo | Nhận push notification khi có lịch hẹn mới hoặc bệnh nhân hủy. |

### 🔧 Quản trị viên (Admin)

| **Nhóm chức năng** | **Mô tả nghiệp vụ** |
|-------------------|----------------------|
| 👨‍⚕️ Quản lý bác sĩ | Phê duyệt hồ sơ, cập nhật thông tin, khóa/mở tài khoản. |
| 👤 Quản lý người dùng | Khóa/mở tài khoản bệnh nhân vi phạm, xem lịch sử hoạt động. |
| 📈 Báo cáo hệ thống | Tổng hợp lịch hẹn, doanh thu, đánh giá trung bình, xuất báo cáo Excel/PDF. |
| 💬 Khiếu nại | Tiếp nhận, phân công xử lý, gửi phản hồi đến người dùng. |
| 💰 Thanh toán | Theo dõi giao dịch, xử lý hoàn tiền tự động hoặc thủ công. |

---

## ⚙️ Công nghệ sử dụng

| **Thành phần** | **Công nghệ** | **Mục đích** |
|---------------|---------------|----------------|
| Ngôn ngữ | Kotlin | Phát triển Android native |
| IDE | Android Studio | Môi trường phát triển |
| Local DB | Room + SharedPreferences | Lưu offline |
| Cloud DB | Firestore | Đồng bộ dữ liệu realtime |
| Xác thực | Firebase Auth + OAuth2 | Email/Google login |
| Thông báo | FCM | Push notification |
| Thanh toán | VNPay SDK, Momo API | Tích hợp cổng thanh toán |
| Biểu đồ | MPAndroidChart | Thống kê trực quan |
| Giao diện | Material Components | Thiết kế chuẩn Material You |
| Tác vụ nền | WorkManager | Gửi thông báo định kỳ |
| Bản đồ | Google Maps API | Vị trí phòng khám, chỉ đường |

---

## 🧱 Kiến trúc ứng dụng

- **Pattern:** MVVM (Model - View - ViewModel)
- **DI:** Dagger-Hilt
- **Network:** Retrofit + OkHttp
- **Async:** Coroutines + Flow
- **Image:** Glide

---

## 📅 Kế hoạch thực hiện (Cập nhật đến 27/05/2026)

| **Thời gian** | **Nội dung** | **Người thực hiện** |
|---------------|--------------|----------------------|
| 23/02 – 01/03/2026 | Nghiên cứu sơ bộ, lập đề cương | Nguyễn Thị Bích Trâm |
| 19/03/2026 | Nộp đề cương chi tiết | Nguyễn Thị Bích Trâm |
| 24/03 – 31/03/2026 | Thiết kế UI/UX (Figma) + Thiết kế CSDL | Trần Viết Đạt |
| 01/04 – 25/04/2026 | Phát triển Frontend (Android) | Trần Viết Đạt |
| 26/04 – 10/05/2026 | Phát triển Backend (Firebase, API) | Nguyễn Thị Bích Trâm |
| 11/05 – 20/05/2026 | Kiểm thử, sửa lỗi, viết tài liệu | Cả nhóm |
| 21/05 – 26/05/2026 | Hoàn thiện báo cáo, đóng gói sản phẩm | Cả nhóm |
| **27/05/2026** | **Nộp báo cáo cuối cùng** | Nguyễn Thị Bích Trâm |
| 01/06 – 21/06/2026 | Chuẩn bị slide, demo, bảo vệ trước hội đồng | Cả nhóm |

---

## 👨‍🏫 Thông tin đồ án

| **Mục** | **Thông tin** |
|---------|----------------|
| **Tên đồ án** | MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến |
| **Giảng viên hướng dẫn** | ThS. Nguyễn Đỗ Công Pháp |
| **Sinh viên 1** | Nguyễn Thị Bích Trâm - 24IT277 (0934984665) |
| **Sinh viên 2** | Trần Viết Đạt - 24IT332 (0396704484) |
| **Lớp** | 24SE1 - Công Nghệ Thông Tin (Kỹ Sư) |
| **Trường** | Đại học CNTT & TT Việt – Hàn (VKU) |
| **Khoa** | Khoa học Máy tính |

---

## 📝 Ghi chú

> Đây là đồ án kết thúc học phần, đã được nghiệm thu và đánh giá nội bộ.  
> Mọi đóng góp và phản hồi xin vui lòng liên hệ qua email: [tramnb.24it@vku.udn.vn](mailto:tramnb.24it@vku.udn.vn)

---

**© 2026 - MediCare Team**  
*Giải pháp y tế số cho người Việt*
