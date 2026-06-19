# 🏥 MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-0095D5?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Backend](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase)](https://firebase.google.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen?style=for-the-badge)](https://developer.android.com/about/versions/android-7.0)
[![Material](https://img.shields.io/badge/UI-Material%20You-757575?style=for-the-badge&logo=material-ui)](https://m3.material.io/)

> **Giải pháp y tế số toàn diện** – Kết nối bệnh nhân, bác sĩ và phòng khám trên một nền tảng duy nhất.

---

## 📑 Mục lục

1. [Tổng quan đồ án](#-tổng-quan-đồ-án)
2. [Giá trị mang lại](#-giá-trị-mang-lại)
3. [Đối tượng sử dụng & Phân quyền](#-đối-tượng-sử-dụng--phân-quyền)
4. [Quy trình nghiệp vụ](#-quy-trình-nghiệp-vụ)
5. [Cấu trúc dữ liệu](#-cấu-trúc-dữ-liệu)
6. [Công nghệ & Kiến trúc](#-công-nghệ--kiến-trúc)
7. [Cấu trúc dự án](#-cấu-trúc-dự-án)
8. [Kế hoạch phát triển](#-kế-hoạch-phát-triển)
9. [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
10. [Thông tin đồ án](#-thông-tin-đồ-án)

---

## 📌 Tổng quan đồ án

**MediCare** là một ứng dụng di động (Android) cho phép người dùng đặt lịch khám bệnh trực tuyến, thanh toán điện tử, quản lý hồ sơ sức khỏe cá nhân và tương tác với bác sĩ một cách hiệu quả.

### 🎯 Mục tiêu đồ án

- Xây dựng hệ thống quản lý lịch khám bệnh tập trung, minh bạch.
- Tối ưu hóa trải nghiệm người dùng với giao diện trực quan, thân thiện.
- Tích hợp các cổng thanh toán phổ biến tại Việt Nam (VNPay, Momo).
- Tự động hóa quy trình nhắc lịch, đánh giá và báo cáo thống kê.

### 🔥 Bài toán giải quyết

| **Vấn đề thực tế** | **Giải pháp của MediCare** |
|---------------------|-----------------------------|
| Đặt lịch khám thủ công, mất nhiều thời gian | Đặt lịch online 24/7, chọn khung giờ trống theo thời gian thực |
| Hồ sơ bệnh án rời rạc, khó tra cứu | Lưu trữ tập trung trên cloud, truy xuất mọi lúc mọi nơi |
| Không có kênh thanh toán trực tuyến | Tích hợp VNPay/Momo, hỗ trợ hoàn tiền tự động theo chính sách |
| Quên lịch hẹn, bỏ lỡ khám | Gửi thông báo qua app, email, SMS (24h và 1h trước) |
| Không có cơ chế đánh giá bác sĩ | Hệ thống đánh giá sao + nhận xét sau mỗi lần khám |

---

## 🌟 Giá trị mang lại

| **Đối tượng** | **Lợi ích nhận được** |
|---------------|------------------------|
| 🧑‍⚕️ **Bệnh nhân** | Tiết kiệm thời gian chờ đợi, dễ dàng so sánh và chọn bác sĩ phù hợp, theo dõi lịch sử khám bệnh. |
| 👨‍🔬 **Bác sĩ** | Quản lý lịch làm việc khoa học, tối ưu thời gian khám, tăng thu nhập qua hệ thống đặt lịch. |
| 🏥 **Phòng khám / Admin** | Nắm bắt doanh thu, đánh giá chất lượng dịch vụ, quản lý tập trung, giảm tải công việc hành chính. |

---

## 👥 Đối tượng sử dụng & Phân quyền

### 🔐 Ma trận quyền hạn (RBAC)

| **Chức năng** | **Bệnh nhân** | **Bác sĩ** | **Admin** |
|---------------|:-------------:|:----------:|:----------:|
| Đăng ký / Đăng nhập | ✅ | ✅ | ✅ |
| Xem danh sách bác sĩ | ✅ | ❌ | ✅ |
| Xem profile chi tiết bác sĩ | ✅ | ✅ | ✅ |
| Đặt / Hủy lịch hẹn | ✅ | ❌ | ✅ |
| Xác nhận lịch hẹn | ❌ | ✅ | ✅ |
| Thanh toán trực tuyến | ✅ | ❌ | ✅ |
| Đánh giá bác sĩ | ✅ | ❌ | ❌ |
| Quản lý lịch làm việc | ❌ | ✅ | ✅ |
| Xem thống kê doanh thu | ❌ | ✅ | ✅ |
| Quản lý người dùng | ❌ | ❌ | ✅ |
| Duyệt hồ sơ bác sĩ | ❌ | ❌ | ✅ |
| Xử lý khiếu nại | ❌ | ❌ | ✅ |

### 📋 Nghiệp vụ chi tiết từng vai trò

<details>
<summary><b>🧑‍⚕️ Bệnh nhân (7 nhóm chức năng)</b></summary>

| **STT** | **Nhóm** | **Mô tả chi tiết** |
|---------|----------|---------------------|
| 1 | Xác thực | Đăng ký email/SĐT, xác thực OTP, đăng nhập bằng Google, quên mật khẩu |
| 2 | Hồ sơ cá nhân | Cập nhật avatar, thông tin liên hệ, tiền sử bệnh, dị ứng thuốc |
| 3 | Tìm kiếm bác sĩ | Lọc theo: chuyên khoa, địa chỉ, giá khám (từ cao đến thấp), đánh giá (4–5 sao), năm kinh nghiệm |
| 4 | Đặt lịch | Chọn bác sĩ → Xem lịch rảnh (tuần) → Nhập triệu chứng → Chọn thanh toán → Xác nhận |
| 5 | Quản lý lịch hẹn | Xem lịch sử (sắp tới/đã qua), hủy lịch (kèm hoàn tiền), chỉnh sửa triệu chứng |
| 6 | Đánh giá | Form đánh giá tự động xuất hiện sau khi kết thúc lịch hẹn (sao + bình luận) |
| 7 | Thông báo | Nhận push, email, SMS: xác nhận lịch, nhắc lịch, thông báo hủy lịch |
</details>

<details>
<summary><b>👨‍⚕️ Bác sĩ (5 nhóm chức năng)</b></summary>

| **STT** | **Nhóm** | **Mô tả chi tiết** |
|---------|----------|---------------------|
| 1 | Quản lý lịch làm việc | Tạo khung giờ (thứ - ngày - ca sáng/chiều), khóa giờ bận, cập nhật giá khám |
| 2 | Xác nhận lịch hẹn | Danh sách bệnh nhân theo ngày, xác nhận (chuyển trạng thái confirmed) hoặc từ chối + lý do |
| 3 | Hồ sơ bệnh nhân | Xem lịch sử khám, ghi chú chuyên môn (private note), kê đơn (tính năng mở rộng) |
| 4 | Thống kê | Biểu đồ doanh thu theo ngày/tuần/tháng, số lượng bệnh nhân, đánh giá trung bình |
| 5 | Thông báo | Push notification khi: có lịch mới, bệnh nhân hủy lịch, bệnh nhân gửi đánh giá |
</details>

<details>
<summary><b>🔧 Quản trị viên (6 nhóm chức năng)</b></summary>

| **STT** | **Nhóm** | **Mô tả chi tiết** |
|---------|----------|---------------------|
| 1 | Quản lý bác sĩ | Phê duyệt hồ sơ, chỉnh sửa thông tin, khóa/mở tài khoản, xóa bác sĩ vi phạm |
| 2 | Quản lý bệnh nhân | Xem danh sách, khóa/mở, xem lịch sử đặt lịch và khiếu nại |
| 3 | Quản lý thanh toán | Theo dõi giao dịch (thành công/thất bại/hoàn tiền), xuất báo cáo Excel |
| 4 | Báo cáo hệ thống | Tổng hợp: số lịch hẹn, doanh thu (theo tuần/tháng/quý), rating trung bình |
| 5 | Xử lý khiếu nại | Tiếp nhận, phân công, phản hồi, lưu trữ lịch sử |
| 6 | Cấu hình hệ thống | Cập nhật chính sách hoàn tiền, phí dịch vụ, thời gian hủy lịch tối thiểu |
</details>

---

## 🔄 Quy trình nghiệp vụ

### 1. Quy trình đặt lịch
[Đăng nhập] → [Chọn chuyên khoa] → [Danh sách bác sĩ] → [Xem profile]
→ [Chọn khung giờ trống] → [Nhập triệu chứng] → [Chọn thanh toán]
→ [Xác nhận] → [Gửi thông báo] → [Lưu vào Firestore]

text

### 2. Quy trình hủy lịch & hoàn tiền

| **Thời điểm hủy** | **Tỷ lệ hoàn tiền** | **Xử lý tự động** |
|-------------------|---------------------|--------------------|
| ≥ 2 giờ trước giờ hẹn | 100% | Tự động hoàn qua VNPay/Momo |
| < 2 giờ & > 0 giờ | 50% | Admin duyệt thủ công |
| Sau giờ hẹn | 0% | Không hoàn |

### 3. Quy trình đánh giá
[Kết thúc lịch hẹn] → [Hệ thống gửi form đánh giá] → [Bệnh nhân chọn sao + nhập bình luận]
→ [Lưu vào Firestore] → [Cập nhật rating trung bình của bác sĩ]

---

## 🗄️ Cấu trúc dữ liệu (Database Schema)

### Firestore Collections

| **Collection** | **Document ID** | **Key fields** |
|----------------|----------------|----------------|
| `users` | UID (Firebase Auth) | `role` (patient/doctor/admin), `name`, `email`, `phone`, `avatar`, `createdAt` |
| `doctors` | doctorUID | `specialty`, `yearsOfExp`, `clinicAddress`, `price`, `ratingAvg`, `description`, `workSchedule` |
| `appointments` | auto | `patientId`, `doctorId`, `dateTime`, `symptoms`, `status` (pending/confirmed/completed/cancelled), `paymentStatus`, `amount` |
| `reviews` | auto | `appointmentId`, `patientId`, `doctorId`, `rating` (1-5), `comment`, `createdAt` |
| `transactions` | auto | `appointmentId`, `method` (VNPay/Momo), `amount`, `status`, `refundStatus` |

### Local Storage (Room)

| **Entity** | **Purpose** |
|------------|--------------|
| `UserEntity` | Lưu thông tin người dùng khi offline |
| `AppointmentCache` | Cache lịch hẹn sắp tới |
| `DoctorCache` | Cache danh sách bác sĩ yêu thích |

---

## ⚙️ Công nghệ & Kiến trúc

### 🧱 Kiến trúc tổng thể
┌─────────────────────────────────────────────────────────┐
│ Presentation Layer │
│ Activities / Fragments / ViewModels / DataBinding │
├─────────────────────────────────────────────────────────┤
│ Domain Layer │
│ Use Cases / Repository Interfaces / Models │
├─────────────────────────────────────────────────────────┤
│ Data Layer │
│ Repository Implementation / Local DB / Remote API │
└─────────────────────────────────────────────────────────┘

### 🔧 Stack công nghệ chi tiết

| **Category** | **Technology** | **Version** |
|--------------|----------------|--------------|
| Language | Kotlin | 1.9.0 |
| Minimum SDK | API 24 (Android 7.0) | - |
| Target SDK | API 34 (Android 14) | - |
| UI Toolkit | Material Design 3 (Material You) | 1.11.0 |
| Architecture | MVVM + Clean Architecture | - |
| DI | Dagger-Hilt | 2.48 |
| Database (Local) | Room | 2.6.1 |
| Database (Cloud) | Firebase Firestore | 24.10.0 |
| Authentication | Firebase Auth | 22.3.0 |
| Push Notification | FCM | 23.4.0 |
| Networking | Retrofit + OkHttp | 2.9.0 / 4.12.0 |
| Async | Coroutines + Flow | 1.7.3 |
| Image Loading | Glide | 4.16.0 |
| Chart | MPAndroidChart | 3.1.0 |
| Background | WorkManager | 2.9.0 |
| Maps | Google Maps API | 18.2.0 |

---

## 📅 Kế hoạch phát triển

### Giai đoạn phát triển (Phases)

| **Phase** | **Thời gian** | **Nội dung chính** | **Người thực hiện** |
|-----------|---------------|---------------------|----------------------|
| **Phase 1: Nghiên cứu & Thiết kế** | 23/02 – 31/03/2026 | Lập đề cương, Figma UI/UX, thiết kế CSDL | Trâm (60%) + Đạt (40%) |
| **Phase 2: Phát triển Frontend** | 01/04 – 25/04/2026 | Xây dựng giao diện, navigation, các màn hình chính | Trần Viết Đạt |
| **Phase 3: Phát triển Backend** | 26/04 – 10/05/2026 | Firebase Auth, Firestore, FCM, tích hợp thanh toán | Nguyễn Thị Bích Trâm |
| **Phase 4: Tích hợp & Kiểm thử** | 11/05 – 20/05/2026 | Test chức năng, fix bug, viết Unit test, UAT | Cả nhóm |
| **Phase 5: Hoàn thiện** | 21/05 – 26/05/2026 | Đóng gói APK/AAB, viết tài liệu, làm slide | Cả nhóm |
| **Phase 6: Bảo vệ** | 01/06 – 21/06/2026 | Demo trực tiếp, bảo vệ trước hội đồng | Cả nhóm |

### 🗓️ Mốc thời gian quan trọng

| **Mốc** | **Ngày** | **Nội dung** |
|---------|----------|---------------|
| Nộp đề cương | 19/03/2026 | Đã hoàn thành |
| **Nộp báo cáo cuối cùng** | **27/05/2026** | ✅ Đã nộp |
| Bảo vệ đồ án | 01–21/06/2026 | Đang chuẩn bị |

---

## 💻 Yêu cầu hệ thống

### Môi trường phát triển

| **Thành phần** | **Yêu cầu** |
|----------------|--------------|
| Hệ điều hành | Windows 10/11, macOS, hoặc Linux (Ubuntu 20.04+) |
| RAM | Tối thiểu 8GB (khuyến nghị 16GB) |
| Dung lượng trống | 10GB |
| Android Studio | Hedgehog (2023.1.1) trở lên |
| JDK | 11 hoặc 17 |
| Gradle | 8.0+ |

### Môi trường chạy (User)

| **Thiết bị** | **Yêu cầu** |
|--------------|--------------|
| Hệ điều hành | Android 7.0 (API 24) trở lên |
| RAM | 2GB+ |
| Kết nối | Internet (Wi-Fi hoặc 4G/5G) |
| Dịch vụ bắt buộc | Google Play Services (cho FCM, Maps, Auth) |

---

## 👨‍🏫 Thông tin đồ án

| **Mục** | **Thông tin** |
|---------|----------------|
| **Tên đồ án** | MEDICARE - Ứng dụng Đặt lịch khám bệnh trực tuyến |
| **Giảng viên hướng dẫn** | ThS. Nguyễn Đỗ Công Pháp |
| **Sinh viên thực hiện 1** | Nguyễn Thị Bích Trâm - 24IT277 (0934984665) |
| **Sinh viên thực hiện 2** | Trần Viết Đạt - 24IT332 (0396704484) |
| **Lớp** | 24SE1 - Công Nghệ Thông Tin (Kỹ Sư) |
| **Trường** | Đại học Công nghệ Thông tin và Truyền thông Việt - Hàn |
| **Khoa** | Khoa Khoa học Máy tính |

---

## 📝 Ghi chú & Tài liệu tham khảo

> ✅ **Xác nhận:** Đồ án đã được nghiệm thu nội bộ, đáp ứng đầy đủ các chức năng cốt lõi theo yêu cầu.

### 📚 Tài liệu tham khảo

- [Firebase Documentation](https://firebase.google.com/docs)
- [Material Design 3 Guidelines](https://m3.material.io/)
- [VNPay Developer Integration](https://vnpay.vn/developer)
- [Momo Payment API](https://developers.momo.vn/)

### 📞 Liên hệ

📧 Email: [tramnb.24it@vku.udn.vn](mailto:tramnb.24it@vku.udn.vn)  
📱 Điện thoại: 0934 984 665 (Trâm) - 0396 704 484 (Đạt)

---

<div align="center">

**© 2026 - MediCare Team**  
*Giải pháp y tế số cho người Việt*

🔥 *Đã nộp báo cáo thành công ngày 27/05/2026* 🔥

</div>
