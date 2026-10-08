# FUCinema Booking System – Microservices Architecture

Backend hệ thống quản lý và đặt vé xem phim **FUCinema** theo kiến trúc **Microservices** với **API Gateway** làm điểm tiếp nhận tập trung (Single Entry Point).

---

## 1. Kiến Trúc Hệ Thống & Cổng Dịch Vụ (Port Table)

| Dịch vụ | Cổng | Cơ sở dữ liệu | Công nghệ chính | Chức năng chính |
|---|---|---|---|---|
| **api-gateway** | `9000` | — | Spring Cloud Gateway Server WebMVC, Spring Security, Nimbus JOSE (HS256) | Cửa ngõ duy nhất, bảo mật JWT, giải mã claims, chống giả mạo header (`X-User-*`), RBAC điều hướng |
| **customer-service** | `8081` | SQL Server 2022 (`cinema_customer`) | Spring Data JPA, Flyway, BCrypt, JWT Service | Đăng nhập Admin & Customer, Đăng ký, Quản lý Profile (`/me`), CRUD Customer (Admin), Soft delete |
| **movie-service** | `8082` | MongoDB 7.0 (`cinema_movie`) | Spring Data MongoDB, DataSeeder, MongoTemplate Criteria | Quản lý Thể loại (Genre), Phòng chiếu (CinemaRoom), Phim (Movie), Suất chiếu (Showtime - Decimal128) |
| **booking-service** | `8083` | MySQL 8.3 (`cinema_booking`) | Spring Data JPA, OpenFeign, Flyway | Sơ đồ ghế (Seat Map), Đặt vé & snapshot chi tiết, Lịch sử đặt vé, Hủy vé trước 2h, Báo cáo doanh thu |

---

## 2. Thông Tin Cơ Sở Dữ Liệu (Docker Containers)

| Container Name | Database Engine | Host Port | Database Name | Username | Password |
|---|---|---|---|---|---|
| `cinema-sqlserver` | Microsoft SQL Server 2022 | `1433` | `cinema_customer` | `sa` | `Fucinema@2026` |
| `cinema-mongo` | MongoDB 7.0 | `27017` | `cinema_movie` | `root` | `password` (auth: `admin`) |
| `cinema-mysql` | MySQL 8.3 | `3306` | `cinema_booking` | `root` | `mysql` |

---

## 3. Hướng Dẫn Cài Đặt & Khởi Động (Run Guide)

### Bước 1: Khởi động hệ thống cơ sở dữ liệu (Docker)
Tại thư mục `fu-cinema/`:
```bash
cd fu-cinema
docker compose up -d
```
> Kiểm tra các container đã chạy ổn định: `docker ps` (SQL Server đạt trạng thái `healthy`).

### Bước 2: Khởi động 3 Microservices Backend (Theo thứ tự)
Bạn có thể khởi động qua IntelliJ IDEA bằng cách bấm Run ▶️ trên các file Application, hoặc chạy qua terminal (yêu cầu JDK 21+):

1. **Customer Service (Cổng 8081):**
   ```bash
   cd fu-cinema/customer-service
   ./mvnw spring-boot:run
   ```
2. **Movie Service (Cổng 8082):**
   ```bash
   cd fu-cinema/movie-service
   ./mvnw spring-boot:run
   ```
3. **Booking Service (Cổng 8083):**
   ```bash
   cd fu-cinema/booking-service
   ./mvnw spring-boot:run
   ```

### Bước 3: Khởi động API Gateway (Cổng 9000)
```bash
cd fu-cinema/api-gateway
./mvnw spring-boot:run
```

---

## 4. Danh Sách Tài Khoản Kiểm Thử (Test Accounts)

| Vai trò | Email | Mật khẩu | Trạng thái | Quyền hạn & Chức năng |
|---|---|---|---|---|
| **ADMIN** | `admin@fucinema.com` | `@@abc123@@` | `ACTIVE` | Toàn quyền quản trị hệ thống: Quản lý khách hàng (CRUD/soft delete), Quản lý Thể loại, Phòng chiếu, Phim, Suất chiếu, Xem danh sách đặt vé, Xem báo cáo doanh thu (`/api/bookings/report`). Bị cấm gọi các API cá nhân của khách hàng (`/me`, `/my-bookings`). |
| **CUSTOMER** (An) | `an@gmail.com` | `123456` | `ACTIVE` | Khách hàng hợp lệ (ID = 1): Xem & sửa profile cá nhân (`/me`), đổi mật khẩu (`/me/password`), xem sơ đồ ghế, đặt vé xem phim, xem lịch sử đặt vé (`/api/bookings/my-bookings`), hủy vé hợp lệ trước giờ chiếu 2 tiếng. |
| **CUSTOMER** (Bình) | `binh@gmail.com` | `123456` | `ACTIVE` | Khách hàng hợp lệ (ID = 2): Dùng để kiểm thử phân quyền kiểm tra chủ sở hữu vé (BR11: Khách hàng chỉ xem được vé của chính mình, không xem được vé của khách hàng khác). |
| **CUSTOMER** (Chi - Bị khóa) | `chi@gmail.com` | `123456` | `INACTIVE` | Khách hàng bị khóa: Đăng nhập bị chặn với mã lỗi **HTTP 403 Forbidden** kèm thông báo `"Tài khoản đã bị vô hiệu hóa"`. |

---

## 5. Hướng Dẫn Kiểm Thử Với Postman (Collection Runner)

Toàn bộ kịch bản kiểm thử E2E (End-to-End) bao gồm 85 requests (217 assertions) bao phủ từ tính năng **F1 đến F10** được lưu trong thư mục `postman/`.

### Các file kịch bản:
- **Environment:** `postman/FUCinema-Local.postman_environment.json`
- **Collection:** `postman/FUCinemaBookingSystem.postman_collection.json`

### Các bước thực hiện:
1. Mở ứng dụng **Postman**, bấm nút **Import** (hoặc `Cmd + O` trên Mac).
2. Kéo thả 2 file `.json` trên vào Postman.
3. Ở góc trên bên phải giao diện Postman, chọn Environment là **`FUCinema-Local`**.
4. Click chuột phải vào Collection **`FUCinemaBookingSystem`** ➔ Chọn **Run collection**.
5. Đảm bảo 8 folder được chọn theo đúng thứ tự (`01-Auth` đến `08-Report`).
6. Bấm nút màu xanh **Run FUCinemaBookingSystem**.

### Kết quả mong đợi:
- **100% Tests Passed** (0 Failed, 217/217 assertions thành công).
- Toàn bộ luồng nghiệp vụ từ đăng nhập, phân quyền gateway, kiểm tra trùng lịch chiếu, đặt vé, chống trùng ghế, snapshot giá vé, hủy vé và báo cáo doanh thu hoạt động chuẩn xác theo yêu cầu.

---

## 6. Tổng Hợp Quy Tắc Nghiệp Vụ Đã Hiện Thực (Business Rules)

- **BR01**: Tên thể loại phim là duy nhất trong hệ thống.
- **BR02**: Tên phòng chiếu là duy nhất trong hệ thống.
- **BR03**: Không cho phép xóa thể loại đang được tham chiếu bởi phim (HTTP 409 Conflict).
- **BR04**: Không xếp suất chiếu cho phim đã kết thúc (`ENDED`) hoặc phòng đang bảo trì (`MAINTENANCE`). Giờ chiếu phải ở tương lai.
- **BR05**: Không cho phép trùng khung giờ chiếu trong cùng một phòng chiếu (HTTP 409 Conflict).
- **BR06**: Hủy suất chiếu theo cơ chế Soft-delete (`CANCELLED`), giữ lại toàn bộ lịch sử dữ liệu.
- **BR07**: Không được đặt trùng mã ghế trong cùng một yêu cầu đặt vé.
- **BR08**: Mã ghế phải nằm trong giới hạn sơ đồ của phòng chiếu (`row <= seatRows`, `col <= seatsPerRow`).
- **BR09**: Không được đặt ghế đã có người đặt trước trong cùng suất chiếu.
- **BR10**: Không cho phép đặt vé vào các suất chiếu đã bị hủy (`CANCELLED`).
- **BR11**: Khách hàng chỉ xem được thông tin vé của chính mình; Admin có quyền xem tất cả.
- **BR12**: Khách hàng chỉ được phép hủy vé trước giờ chiếu ít nhất 2 tiếng.
- **BR13**: Admin có quyền tìm kiếm khách hàng theo tên, email và trạng thái.
- **BR14**: Snapshot thông tin giá vé, tên phim, phòng chiếu, giờ chiếu vào chi tiết vé tại thời điểm đặt (đảm bảo tính toàn vẹn dữ liệu khi giá hoặc phim thay đổi về sau).
- **BR15**: Tạo/cập nhật phim phải kiểm tra thể loại tồn tại; tạo suất chiếu phải kiểm tra phim và phòng chiếu tồn tại.
- **BR16**: Hỗ trợ lưu trữ và hiển thị tiếng Việt có dấu đầy đủ trên tất cả các hệ quản trị CSDL (`NVARCHAR` trên SQL Server, UTF-8 trên MySQL và MongoDB).
