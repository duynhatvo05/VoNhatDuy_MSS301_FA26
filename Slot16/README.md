# MSS301 - Practical Exam Sample - Summer 2026

**Sinh viên thực hiện**: Võ Nhật Duy  
**MSSV**: DE190516  
**Môn học**: MSS301 - Microservices with Spring Cloud  

---

## 1. Tổng quan Kiến trúc Hệ thống

Hệ thống tài trợ bài đăng (Sponsored Post Booking Platform) được xây dựng theo kiến trúc Microservices gồm 3 services:

| Service | Tên Project | Port | Công nghệ | Nhiệm vụ chính |
|---------|-------------|------|-----------|----------------|
| **User Service** | `DE190516UserService` | `8081` | Spring Boot 4.1.0, Spring Data JPA, MS SQL Server | Quản lý người dùng, creator, định giá ratePerPost và giới hạn bài đăng hàng ngày |
| **Post Service** | `DE190516PostService` | `8082` | Spring Boot 4.1.0, OpenFeign, Spring Data JPA, MS SQL Server | Quản lý chiến dịch quảng cáo, gọi `UserClient` qua OpenFeign để kiểm tra creator và tính chi phí |
| **Media Gateway** | `DE190516MediaGateway` | `8080` | Spring Boot 4.1.0, Spring Cloud Gateway (WebFlux) | Single Entry Point định tuyến `/api/users/**` -> 8081 và `/api/posts/**` -> 8082, tổng hợp Swagger UI |

---

## 2. Chuẩn bị Cơ sở Dữ liệu

1. Sử dụng Microsoft SQL Server (2016+).
2. Chạy file script: [`mss301_2026_pe_media.sql`](./mss301_2026_pe_media.sql).
   Script này sẽ:
   - Tạo Database: `MSS301_2026_PE_MEDIA`
   - Tạo bảng `users` với đầy đủ ràng buộc khóa chính, unique, check constraint (`role`, `status`, `rate_per_post`, `max_daily_posts`, `level`).
   - Tạo bảng `posts` với ràng buộc ngày `end_date > start_date`, `number_of_posts >= 1`, `total_cost >= 0`, `status`, khóa ngoại tham chiếu sang `users(user_id)`.
   - Chèn sẵn dữ liệu mẫu Seed:
     ```sql
     INSERT INTO users (username, email, role, rate_per_post, max_daily_posts, level, status) VALUES
      ('creator_a','a@example.com','CREATOR',500000,3,1,'ACTIVE'),
      ('creator_b','b@example.com','CREATOR',300000,1,1,'ACTIVE'),
      ('creator_c','c@example.com','CREATOR',800000,4,2,'SUSPENDED');
     ```

---

## 3. Cấu trúc Project theo chuẩn đề thi

Cả 3 service tuân thủ cấu trúc package chuẩn: `fu.de190516.<servicename>`

```
Slot16/
├── DE190516UserService/
│   ├── src/main/java/fu/de190516/userservice/
│   │   ├── common/         (UserConstants, GlobalExceptionHandler)
│   │   ├── config/         (CorsConfig, OpenApiConfig)
│   │   ├── controller/     (DE190516UserController)
│   │   ├── dto/            (ApiResponse, UserDTO, PageDTO)
│   │   ├── entity/         (Role, UserStatus, User)
│   │   ├── repository/     (UserRepository)
│   │   ├── service/        (UserService, UserServiceImpl)
│   │   └── De190516UserServiceApplication.java
│   └── pom.xml
├── DE190516PostService/
│   ├── src/main/java/fu/de190516/postservice/
│   │   ├── client/         (UserClient - OpenFeign declarative HTTP client)
│   │   ├── common/         (PostConstants, GlobalExceptionHandler)
│   │   ├── config/         (CorsConfig, HttpClientConfig, OpenApiConfig)
│   │   ├── controller/     (DE190516PostController)
│   │   ├── dto/            (ApiResponse, UserDTO, PostDTO, PostDetailDTO, PageDTO)
│   │   ├── entity/         (PostStatus, Post)
│   │   ├── repository/     (PostRepository)
│   │   ├── service/        (PostService, PostServiceImpl)
│   │   └── De190516PostServiceApplication.java
│   └── pom.xml
├── DE190516MediaGateway/
│   ├── src/main/java/fu/de190516/mediagateway/
│   │   ├── config/         (CorsConfig - CorsWebFilter)
│   │   └── De190516MediaGatewayApplication.java
│   └── pom.xml
├── DE190516.zip            (Gói nộp bài PEA chuẩn hóa, đã mvn clean loại bỏ target/)
└── mss301_2026_pe_media.sql
```

---

## 4. Bảng Quy chuẩn Mã Trả về (Status Code Mapping)

| Response Status | Ý nghĩa | Mã HTTP tương ứng | Mô tả |
|:---------------:|---------|:-----------------:|-------|
| **1** | Success | `200 OK` / `201 CREATED` | Thành công |
| **2** | Validation Error | `406 NOT_ACCEPTABLE` | Sai dữ liệu đầu vào hoặc vượt giới hạn |
| **3** | Duplicate Username | `226 IM_USED` | Trùng lặp username |
| **4** | Not Found | `404 NOT_FOUND` | Không tìm thấy entity (User hoặc Post) |
| **5** | User Not Active | `400 BAD_REQUEST` | Creator không ở trạng thái ACTIVE để nhận booking |
| **0** | Server Error | `500 INTERNAL_SERVER_ERROR` | Lỗi hệ thống không lường trước |

---

## 5. Hướng dẫn Chạy và Kiểm tra (Testing)

### Khởi động các Services theo thứ tự:
1. **Khởi động UserService** (Port 8081):
   ```bash
   cd DE190516UserService
   mvn spring-boot:run
   ```
2. **Khởi động PostService** (Port 8082):
   ```bash
   cd DE190516PostService
   mvn spring-boot:run
   ```
3. **Khởi động MediaGateway** (Port 8080):
   ```bash
   cd DE190516MediaGateway
   mvn spring-boot:run
   ```

### Truy cập Swagger UI:
- **MediaGateway Swagger Aggregator**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
  *Có dropdown chuyển đổi giữa `user-service` và `post-service`.*
- **UserService Swagger**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **PostService Swagger**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
