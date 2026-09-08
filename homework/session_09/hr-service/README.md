# Bài 3 — Test PostgreSQL rồi build JAR

## Mục tiêu

Khởi động PostgreSQL bằng Docker Compose, chạy test tích hợp thật trên database, sau đó build JAR để chuẩn bị cho môi trường CI/CD.

## 1. Chuẩn bị môi trường

```bash
bash setup-gradle.sh
docker compose up -d --wait
```

Compose sẽ khởi động PostgreSQL 14 và chờ healthcheck báo `healthy`.

Nếu phiên bản Docker Compose cũ chưa hỗ trợ `--wait`, làm theo cách sau:

```bash
docker compose up -d
docker compose exec postgres pg_isready -U hr_test_user -d hr_test_db
```

Chỉ chạy test khi thấy output tương ứng với trạng thái `accepting connections`.

## 2. Chạy test tích hợp

```bash
./gradlew clean test
```

### Cấu hình mặc định

- Local: `localhost:5433/hr_test_db`
- User: `hr_test_user`
- Password: `ci_test_password`

Nếu terminal đang giữ biến môi trường `SPRING_DATASOURCE_*`, hãy reset lại đúng các giá trị trên.

### Các test thực tế

Trong `EmployeeRepositoryTest`, có 4 test quan trọng:

1. Xác nhận server đang chạy trên PostgreSQL
2. Lưu nhân viên, xóa persistence context, đọc lại từ DB và kiểm tra dữ liệu
3. Kiểm tra truy vấn chỉ trả nhân viên thuộc phòng IT
4. Kiểm tra ràng buộc email duy nhất

`@AutoConfigureTestDatabase(replace = NONE)` đảm bảo dùng PostgreSQL thật, không dùng H2 hoặc mock database.

Mỗi test sẽ rollback dữ liệu sau khi chạy. Profile test dùng `create-drop`, nên chỉ thao tác trên database thử nghiệm, không ảnh hưởng tới database thực tế.

Mở báo cáo: `build/reports/tests/test/index.html`.

> Nếu thấy `NO-SOURCE`, không có nghĩa là database không được test; hãy kiểm tra log và kết quả test thực tế.

## 3. Build JAR

```bash
./gradlew build -x test
ls -lh build/libs/hr-service-1.0.0.jar
```

## 4. Chạy API thử nghiệm

Giữ PostgreSQL đang chạy, sau khi test kết thúc:

```bash
./gradlew bootRun
```

Mở terminal mới để gửi request:

```bash
curl -i -X POST http://localhost:8080/api/employees -H 'Content-Type: application/json' -d '{"fullName":"Nguyen Van An","email":"an@example.com","department":"IT"}'
curl http://localhost:8080/api/employees
curl 'http://localhost:8080/api/employees?department=IT'
```

### Kết quả mong đợi

- `POST` trả HTTP `201`
- `GET` trả danh sách nhân viên đã lưu
- Email trùng trả HTTP `409`

Dừng `bootRun` bằng `Ctrl + C` trước khi chạy lại test, vì test sẽ tạo/xóa bảng và schema.

## 5. Pipeline GitLab

Đẩy nội dung thư mục này lên root repository GitLab. Pipeline cần có:

- Stage `test`: chạy PostgreSQL service alias `postgres`, cấu hình đầy đủ `POSTGRES_*` và `SPRING_DATASOURCE_*`, rồi chạy `./gradlew test`
- Stage `build`: chỉ chạy sau khi test thành công, dùng `./gradlew build -x test`
- Artifact build: `build/libs/*.jar`, `expire_in: 1 day`
- Artifact test: báo cáo HTML và JUnit XML để dễ debug

### Lưu ý quan trọng

- Local dùng `localhost:5433`
- CI dùng `postgres:5432`
- Database service chỉ tồn tại trong job test
- Job build không cần DB vì đã bỏ qua task test
- Mỗi job chạy trên môi trường riêng, nên build sẽ biên dịch lại từ source

Sau khi pipeline thành công, mở job `build_job` và tải artifact JAR. GitLab có thể giữ artifact của pipeline gần nhất lâu hơn `expire_in` theo cấu hình mặc định.

## 6. Kết thúc lab

```bash
docker compose down
```

Compose này không dùng volume bền vững, nên dữ liệu lab sẽ bị bỏ khi container bị xóa.

## Nộp bài

- Link GitHub chứa file `.gitlab-ci.yml`
- Bên cạnh đó nên có source, kết quả test và ảnh pipeline test/build thành công

## Lỗi thường gặp

| Lỗi | Kiểm tra |
|---|---|
| Connection refused local | Compose đã healthy chưa, port `5433` có đang bận không |
| Connection refused CI | Alias `postgres`, URL và log service PostgreSQL |
| Password authentication failed | `POSTGRES_*` và `SPRING_DATASOURCE_*` phải khớp |
| Job pending | Repo có Runner phù hợp đang online không |
| `gradlew: not found` | Chạy `bash setup-gradle.sh`; CI giữ dòng bootstrap |
| `Permission denied gradlew` | Chạy `chmod +x gradlew` |
| Artifact không có JAR | Kiểm tra log `bootJar`, đường dẫn `build/libs` và job build |
