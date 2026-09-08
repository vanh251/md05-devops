# Phân tích và sửa lỗi bài 1

## 1. Lỗi về `stages`

`stages` phải là một danh sách YAML, ví dụ:

```yaml
stages:
  - build_app
```

Nếu thiếu dấu gạch ngang `-`, YAML sẽ bị hiểu là một chuỗi thay vì danh sách. Dù YAML có thể parse, GitLab CI vẫn không chấp nhận cấu trúc đó theo đúng schema.

## 2. Thiếu image Java

Pipeline cần khai báo môi trường chạy có Java phù hợp, ví dụ:

```yaml
image: gradle:7.6.4-jdk17
```

Với Docker executor, điều này rất cần thiết. Nếu không có image, tùy từng runner và executor mà lỗi có thể khác nhau, nhưng rõ ràng là môi trường chạy chưa được định nghĩa đúng.

## 3. Quyền thực thi của `gradlew`

Trên Linux, file `gradlew` cần quyền thực thi:

```bash
chmod +x gradlew
```

Nếu không cấp quyền, lệnh `./gradlew` sẽ lỗi do permission denied.

## 4. Tạo Wrapper trước khi dùng Gradle

File ZIP không có sẵn binary Wrapper nên cần chạy:

```bash
bash setup-gradle.sh
```

Sau khi chạy lệnh này, cần commit các file:

- `gradlew`
- `gradlew.bat`
- thư mục `gradle/wrapper/`

Sau đó có thể bỏ dòng bootstrap trong CI nếu muốn cấu hình ngắn gọn hơn.

## 5. Ý nghĩa của các lệnh build

```bash
./gradlew clean build -x test
```

- `clean`: xóa build cũ
- `build`: biên dịch và đóng gói ứng dụng
- `-x test`: bỏ qua task test theo yêu cầu đề bài

## 6. Yêu cầu về runner

Runner cần:

- Docker executor hoặc executor hỗ trợ image/service
- quyền truy cập mạng để tải dependency
- quyền thực thi job

Shell executor không hoạt động đúng với cách cấu hình này vì không dùng image theo chuẩn Docker.
