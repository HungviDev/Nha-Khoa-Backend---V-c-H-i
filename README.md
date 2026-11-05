# Medical Management System - Backend (Hệ thống Quản lý Khám Chữa Bệnh)

Dự án Backend quản lý khám chữa bệnh và nha khoa được xây dựng theo kiến trúc **Modular Monolith** sử dụng **Java 21**, **Spring Boot 3.3.4**, **Spring Data JPA**, **MySQL 8**, **Spring Security & JWT**, **Flyway**, và **Springdoc OpenAPI (Swagger UI)**.

---

## 1. Công nghệ sử dụng

- **Ngôn ngữ**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.4
- **Quản lý dependencies & build tool**: Maven & Maven Wrapper (`mvnw`, `mvnw.cmd`)
- **Cơ sở dữ liệu**: MySQL 8+
- **Quản lý Migration DB**: Flyway Migration
- **ORM / Persistence**: Spring Data JPA & Hibernate
- **Bảo mật & Xác thực**: Spring Security 6 & JWT (`io.jsonwebtoken:jjwt:0.12.6`)
- **Tài liệu API**: Springdoc OpenAPI / Swagger UI 3 (`springdoc-openapi-starter-webmvc-ui:2.6.0`)
- **Validation**: Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Future`, v.v.)
- **Tiện ích**: Project Lombok
- **Testing**: JUnit 5, Mockito & H2 Database (In-Memory cho môi trường Test độc lập)

---

## 2. Kiến trúc Modular Monolith

Codebase được tổ chức theo từng module nghiệp vụ độc lập, mỗi module chứa đầy đủ các tầng `controller`, `dto`, `entity`, `repository`, và `service`:

```text
medical-management/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── README.md
├── .gitignore
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/com/medical/management/
│   │   │   ├── MedicalManagementApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── JpaConfig.java
│   │   │   │
│   │   │   ├── common/
│   │   │   │   ├── constants/
│   │   │   │   │   └── AppConstants.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── AppException.java
│   │   │   │   │   ├── BadRequestException.java
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── UnauthorizedException.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       └── PageResponse.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── dto/
│   │   │   │   │   └── TokenPayload.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   ├── jwt/
│   │   │   │   │   ├── JwtAccessDeniedHandler.java
│   │   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   │   └── JwtTokenProvider.java
│   │   │   │   └── service/
│   │   │   │       ├── CustomUserDetailsService.java
│   │   │   │       └── UserPrincipal.java
│   │   │   │
│   │   │   ├── auth/
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   │   └── RegisterRequest.java
│   │   │   │   └── service/
│   │   │   │       ├── AuthService.java
│   │   │   │       └── AuthServiceImpl.java
│   │   │   │
│   │   │   ├── users/
│   │   │   │   ├── controller/UserController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── UserCreateRequest.java
│   │   │   │   │   ├── UserResponse.java
│   │   │   │   │   └── UserUpdateRequest.java
│   │   │   │   ├── entity/User.java
│   │   │   │   ├── repository/UserRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── UserService.java
│   │   │   │       └── UserServiceImpl.java
│   │   │   │
│   │   │   ├── roles/
│   │   │   │   ├── controller/RoleController.java
│   │   │   │   ├── dto/RoleRequest.java, RoleResponse.java
│   │   │   │   ├── entity/Role.java, RoleName.java
│   │   │   │   ├── repository/RoleRepository.java
│   │   │   │   └── service/RoleService.java, RoleServiceImpl.java
│   │   │   │
│   │   │   ├── doctors/
│   │   │   │   ├── controller/DoctorController.java
│   │   │   │   ├── dto/DoctorCreateRequest.java, DoctorResponse.java, DoctorUpdateRequest.java
│   │   │   │   ├── entity/Doctor.java
│   │   │   │   ├── repository/DoctorRepository.java
│   │   │   │   └── service/DoctorService.java, DoctorServiceImpl.java
│   │   │   │
│   │   │   ├── services/
│   │   │   │   ├── controller/MedicalServiceController.java
│   │   │   │   ├── dto/MedicalServiceRequest.java, MedicalServiceResponse.java
│   │   │   │   ├── entity/MedicalService.java
│   │   │   │   ├── repository/MedicalServiceRepository.java
│   │   │   │   └── service/MedicalServiceService.java, MedicalServiceServiceImpl.java
│   │   │   │
│   │   │   ├── appointments/
│   │   │   │   ├── controller/AppointmentController.java
│   │   │   │   ├── dto/AppointmentCreateRequest.java, AppointmentResponse.java, ...
│   │   │   │   ├── entity/Appointment.java, AppointmentStatus.java
│   │   │   │   ├── repository/AppointmentRepository.java
│   │   │   │   └── service/AppointmentService.java, AppointmentServiceImpl.java
│   │   │   │
│   │   │   ├── medicalrecords/
│   │   │   │   ├── controller/MedicalRecordController.java
│   │   │   │   ├── dto/MedicalRecordCreateRequest.java, MedicalRecordResponse.java, ...
│   │   │   │   ├── entity/MedicalRecord.java
│   │   │   │   ├── repository/MedicalRecordRepository.java
│   │   │   │   └── service/MedicalRecordService.java, MedicalRecordServiceImpl.java
│   │   │   │
│   │   │   ├── treatmentroutes/
│   │   │   │   ├── controller/TreatmentRouteController.java
│   │   │   │   ├── dto/TreatmentRouteCreateRequest.java, TreatmentRouteResponse.java, ...
│   │   │   │   ├── entity/TreatmentRoute.java
│   │   │   │   ├── repository/TreatmentRouteRepository.java
│   │   │   │   └── service/TreatmentRouteService.java, TreatmentRouteServiceImpl.java
│   │   │   │
│   │   │   ├── treatmentstages/
│   │   │   │   ├── controller/TreatmentStageController.java
│   │   │   │   ├── dto/TreatmentStageCreateRequest.java, TreatmentStageResponse.java, ...
│   │   │   │   ├── entity/TreatmentStage.java, TreatmentStageStatus.java
│   │   │   │   ├── repository/TreatmentStageRepository.java
│   │   │   │   └── service/TreatmentStageService.java, TreatmentStageServiceImpl.java
│   │   │   │
│   │   │   └── payments/
│   │   │       ├── controller/PaymentController.java
│   │   │       ├── dto/PaymentCreateRequest.java, PaymentConfirmRequest.java, PaymentResponse.java
│   │   │       ├── entity/Payment.java, PaymentMethod.java, PaymentStatus.java
│   │   │       ├── repository/PaymentRepository.java
│   │   │       └── service/PaymentService.java, PaymentServiceImpl.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           └── V1__init_schema.sql
│   │
│   └── test/
│       ├── java/com/medical/management/
│       │   ├── appointments/service/AppointmentServiceTest.java
│       │   ├── auth/service/AuthServiceTest.java
│       │   ├── payments/service/PaymentServiceTest.java
│       │   └── users/service/UserServiceTest.java
│       └── resources/
│           └── application-test.yml
```

---

## 3. Sơ đồ Quan hệ Thực thể (ERD) & Database Mapping

Dự án ánh xạ chính xác 9 bảng từ sơ đồ ERD:

| Bảng | Entity JPA | Khóa chính | Khóa ngoại | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| `roles` | `Role` | `id` | - | Vai trò (`ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_PATIENT`) |
| `users` | `User` | `id` | `role_id` -> `roles.id` | Tài khoản người dùng, mã hóa mật khẩu bằng BCrypt |
| `doctors` | `Doctor` | `id` | `user_id` -> `users.id` (1-1) | Thông tin chuyên môn bác sĩ, số năm kinh nghiệm |
| `services` | `MedicalService` | `id` | - | Dịch vụ khám/điều trị, đơn giá, ảnh minh họa |
| `appointments` | `Appointment` | `id` | `user_id`, `doctor_id`, `service_id` | Lịch hẹn khám, kiểm tra chống trùng lịch 30 phút |
| `treatment_routes` | `TreatmentRoute` | `id` | - | Phác đồ điều trị tổng thể |
| `treatment_stages` | `TreatmentStage` | `id` | `treatment_route_id` | Các giai đoạn điều trị theo thứ tự `sequence_order` |
| `medical_records` | `MedicalRecord` | `id` | `user_id`, `doctor_id`, `service_id`, `treatment_route_id` | Hồ sơ bệnh án, chẩn đoán của bác sĩ |
| `payments` | `Payment` | `id` | `user_id`, `treatment_stage_id` | Hóa đơn thanh toán (Bắt buộc khởi tạo ở trạng thái `PENDING`) |

---

## 4. Hướng dẫn cấu hình & Chạy ứng dụng

### 4.1. Chuẩn bị MySQL Database

Tạo database trên MySQL 8:

```sql
CREATE DATABASE IF NOT EXISTS medical_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4.2. Biến môi trường (`.env`)

Sao chép `.env.example` hoặc thiết lập biến môi trường khi chạy:

```bash
# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# MySQL Database
DB_URL=jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=9a4f2c8d3e71b5a6c0d8e2f4a7b9c1d3e5f7a9b2c4d6e8f0a2b4c6d8e0f2a4b6
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000
```

### 4.3. Chạy Flyway Migration & Khởi động ứng dụng

Khi ứng dụng chạy lần đầu tiên, Flyway sẽ tự động chạy file `src/main/resources/db/migration/V1__init_schema.sql` để tạo cấu trúc 9 bảng và nạp dữ liệu ban đầu cho các Role.

Chạy ứng dụng bằng Maven Wrapper:

- Trên **Windows**:
  ```cmd
  mvnw.cmd spring-boot:run
  ```
- Trên **Linux / macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```

### 4.4. Chạy Unit Test

Bộ test được cấu hình chạy trên H2 Database độc lập (không yêu cầu MySQL đang bật):

```cmd
mvnw.cmd test
```

Kết quả:
```text
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. Tài liệu API & Swagger UI

Sau khi khởi chạy ứng dụng, truy cập Swagger UI trực tiếp trên trình duyệt tại:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Tại Swagger UI, người dùng có thể bấm vào nút **Authorize** và nhập Bearer JWT token (`Bearer <access_token>`) để kiểm thử các API được phân quyền.

---

## 6. Danh sách REST Endpoints

### 6.1. Authentication (`/api/v1/auth`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Đăng ký tài khoản bệnh nhân |
| `POST` | `/api/v1/auth/login` | Public | Đăng nhập bằng Email hoặc SĐT |
| `POST` | `/api/v1/auth/refresh-token` | Public | Cấp mới JWT Access Token |
| `GET` | `/api/v1/auth/me` | Authenticated | Lấy thông tin tài khoản hiện tại |

### 6.2. Users (`/api/v1/users`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users` | ADMIN, DOCTOR | Danh sách tài khoản kèm phân trang & tìm kiếm |
| `GET` | `/api/v1/users/{id}` | ADMIN, DOCTOR, Owner | Chi tiết tài khoản |
| `POST` | `/api/v1/users` | ADMIN | Tạo tài khoản mới |
| `PUT` | `/api/v1/users/{id}` | ADMIN, Owner | Cập nhật thông tin tài khoản |
| `DELETE` | `/api/v1/users/{id}` | ADMIN | Xóa tài khoản |

### 6.3. Roles (`/api/v1/roles`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/roles` | Authenticated | Danh sách vai trò |
| `GET` | `/api/v1/roles/{id}` | Authenticated | Chi tiết vai trò |
| `POST` | `/api/v1/roles` | ADMIN | Thêm vai trò mới |
| `PUT` | `/api/v1/roles/{id}` | ADMIN | Sửa vai trò |
| `DELETE` | `/api/v1/roles/{id}` | ADMIN | Xóa vai trò |

### 6.4. Doctors (`/api/v1/doctors`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/doctors` | Public | Danh sách bác sĩ kèm phân trang & tìm kiếm |
| `GET` | `/api/v1/doctors/{id}` | Public | Chi tiết bác sĩ theo Doctor ID |
| `GET` | `/api/v1/doctors/user/{userId}` | Public | Chi tiết bác sĩ theo User ID |
| `POST` | `/api/v1/doctors` | ADMIN | Tạo hồ sơ bác sĩ |
| `PUT` | `/api/v1/doctors/{id}` | ADMIN, Doctor Owner | Cập nhật chuyên khoa, số năm kinh nghiệm |
| `DELETE` | `/api/v1/doctors/{id}` | ADMIN | Xóa hồ sơ bác sĩ |

### 6.5. Services (`/api/v1/services`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/services` | Public | Danh sách dịch vụ y tế kèm phân trang & tìm kiếm |
| `GET` | `/api/v1/services/{id}` | Public | Chi tiết dịch vụ y tế |
| `POST` | `/api/v1/services` | ADMIN | Thêm mới dịch vụ y tế |
| `PUT` | `/api/v1/services/{id}` | ADMIN | Cập nhật dịch vụ y tế |
| `DELETE` | `/api/v1/services/{id}` | ADMIN | Xóa dịch vụ y tế |

### 6.6. Appointments (`/api/v1/appointments`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/appointments` | Authenticated | Lọc lịch hẹn theo bệnh nhân, bác sĩ, trạng thái |
| `GET` | `/api/v1/appointments/{id}` | Authenticated | Chi tiết lịch hẹn |
| `POST` | `/api/v1/appointments` | Authenticated | Đặt lịch khám (Kiểm tra xung đột lịch) |
| `PUT` | `/api/v1/appointments/{id}` | Authenticated | Cập nhật lịch hẹn |
| `PATCH` | `/api/v1/appointments/{id}/status` | ADMIN, DOCTOR | Chuyển trạng thái (CONFIRMED, COMPLETED, CANCELLED) |
| `POST` | `/api/v1/appointments/{id}/cancel` | Authenticated | Hủy lịch hẹn kèm lý do |

### 6.7. Treatment Routes (`/api/v1/treatment-routes`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/treatment-routes` | Authenticated | Danh sách phác đồ điều trị |
| `GET` | `/api/v1/treatment-routes/{id}` | Authenticated | Chi tiết phác đồ điều trị |
| `POST` | `/api/v1/treatment-routes` | ADMIN, DOCTOR | Khởi tạo phác đồ điều trị |
| `PUT` | `/api/v1/treatment-routes/{id}` | ADMIN, DOCTOR | Sửa thông tin phác đồ |
| `DELETE` | `/api/v1/treatment-routes/{id}` | ADMIN | Xóa phác đồ điều trị |

### 6.8. Treatment Stages (`/api/v1/treatment-stages`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/treatment-stages/route/{routeId}` | Authenticated | Lấy danh sách giai đoạn sắp xếp theo sequence |
| `GET` | `/api/v1/treatment-stages/{id}` | Authenticated | Chi tiết giai đoạn |
| `POST` | `/api/v1/treatment-stages` | ADMIN, DOCTOR | Tạo giai đoạn điều trị mới |
| `PUT` | `/api/v1/treatment-stages/{id}` | ADMIN, DOCTOR | Cập nhật thông tin giai đoạn, chi phí, độ trễ |
| `PATCH` | `/api/v1/treatment-stages/{id}/status` | ADMIN, DOCTOR | Cập nhật tiến độ (`IN_PROGRESS`, `COMPLETED`) |
| `DELETE` | `/api/v1/treatment-stages/{id}` | ADMIN | Xóa giai đoạn điều trị |

### 6.9. Medical Records (`/api/v1/medical-records`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/medical-records` | ADMIN, DOCTOR | Danh sách hồ sơ bệnh án toàn hệ thống |
| `GET` | `/api/v1/medical-records/patient/{userId}` | ADMIN, DOCTOR, Patient Owner | Xem lịch sử bệnh án của bệnh nhân |
| `GET` | `/api/v1/medical-records/{id}` | Authenticated | Chi tiết hồ sơ bệnh án |
| `POST` | `/api/v1/medical-records` | ADMIN, DOCTOR | Tạo hồ sơ bệnh án mới kèm chẩn đoán |
| `PUT` | `/api/v1/medical-records/{id}` | ADMIN, DOCTOR | Sửa hồ sơ bệnh án |
| `DELETE` | `/api/v1/medical-records/{id}` | ADMIN | Xóa hồ sơ bệnh án |

### 6.10. Payments (`/api/v1/payments`)
| Phương thức | Endpoint | Phân quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/payments` | ADMIN | Danh sách hóa đơn thanh toán toàn viện |
| `GET` | `/api/v1/payments/user/{userId}` | ADMIN, Owner | Lịch sử thanh toán của bệnh nhân |
| `GET` | `/api/v1/payments/{id}` | Authenticated | Chi tiết hóa đơn thanh toán |
| `POST` | `/api/v1/payments` | Authenticated | Khởi tạo giao dịch (Mặc định `PENDING`) |
| `POST` | `/api/v1/payments/{id}/confirm` | ADMIN / Gateway | Xác nhận giao dịch thành công qua mã đối soát |

---

## 7. Các quy tắc nghiệp vụ quan trọng đã được xử lý

1. **Bảo mật & Mã hóa:** Mật khẩu người dùng được mã hóa bằng thuật toán `BCrypt`. Mật khẩu không bao giờ được trả về trong các DTO response.
2. **Xung đột lịch hẹn (Appointment Collision Prevention):** Hệ thống tự động kiểm tra khoảng đệm thời gian (buffer 30 phút) trước và sau giờ hẹn. Nếu bác sĩ hoặc bệnh nhân đã có một lịch khám khác chưa bị hủy trong khung giờ này, yêu cầu đặt lịch sẽ bị từ chối với thông báo lỗi rõ ràng.
3. **An toàn thanh toán (Payment Integrity):** Client không được phép tự gán trạng thái `SUCCESS` khi tạo hóa đơn. Mọi giao dịch khởi tạo ban đầu đều ở trạng thái `PENDING`. Chỉ có endpoint xác thực `/api/v1/payments/{id}/confirm` (dành cho Admin hoặc IPN Webhook từ cổng thanh toán với chữ ký/mã giao dịch hợp lệ) mới có quyền hoàn tất thanh toán.
4. **Phân trang & Chuẩn hóa dữ liệu:** Sử dụng `PageRequest` và `PageResponse<T>` chuẩn hóa; áp dụng `EntityGraph` và fetch join để triệt tiêu lỗi N+1 Query trong JPA.
