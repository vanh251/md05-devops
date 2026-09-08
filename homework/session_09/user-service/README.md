# Bài 1 — Khắc phục pipeline CI/CD

## Mục tiêu

Sửa pipeline CI/CD cho service `user-service` sao cho build được trên GitLab/GitHub Actions theo đúng chuẩn, không phụ thuộc vào máy local.

## Yêu cầu môi trường

- JDK 17
- Docker
- Gradle

## Chạy ứng dụng tại máy local

Trong thư mục project:

```bash
bash setup-gradle.sh
./gradlew clean build -x test
ls -lh build/libs/
java -jar build/libs/user-service-1.0.0.jar
```

Mở terminal khác để kiểm tra:

```bash
curl http://localhost:8080/api/users
```

Kết quả trả về JSON chứa hai người dùng mẫu. Dữ liệu được lưu trong code, không cần database.

Nhấn `Ctrl + C` ở terminal đang chạy Java để dừng ứng dụng trước khi chạy bài khác trên cổng `8080`.

Nếu muốn chạy test API thêm:

```bash
./gradlew test
```

## Các bước làm bài

1. Đọc file [user-service/giai-thich.md](user-service/giai-thich.md) để nắm 2 lỗi chính: sai kiểu dữ liệu của `stages` và thiếu khai báo môi trường chạy.
2. Mở file `.gitlab-ci.yml` để kiểm tra:
   - có `image` Java 17
   - `stages` là danh sách hợp lệ
   - lệnh build là `./gradlew clean build -x test`
3. Đẩy toàn bộ project lên root repository GitLab và xem job `build_job` trong pipeline.
4. Chụp ảnh kết quả `BUILD SUCCESSFUL` nếu cần minh chứng.
5. Nộp link GitHub chứa cả `.gitlab-ci.yml` và [user-service/giai-thich.md](user-service/giai-thich.md), để giảng viên có thể chạy lại source.

## Ghi chú quan trọng

Pipeline có thêm bước tạo Wrapper để ZIP có thể chạy khi chưa có file binary Wrapper. Sau khi chạy `bash setup-gradle.sh` và commit lại `gradlew`, `gradlew.bat` và thư mục `gradle/wrapper/`, có thể bỏ dòng bootstrap trong CI để cấu hình ngắn hơn.
