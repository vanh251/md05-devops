# Bài 2 — Docker hóa Payment Service

## Mục tiêu

Docker hóa service `payment-service` để chạy như một container độc lập. Đây là API mô phỏng, không kết nối cổng thanh toán, không thu tiền và không lưu giao dịch thực tế.

## 1. Tạo Wrapper và JAR

```bash
bash setup-gradle.sh
./gradlew clean build
ls -lh build/libs/payment-service-1.0.0.jar
```

`build.gradle` đã cố định tên `bootJar` đúng với đề bài và tắt `plain JAR` để tránh chọn nhầm file build sai.

## 2. Build image Docker

```bash
docker build -t payment-service:1.0.0 .
```

`Dockerfile` cần đầy đủ các thành phần cơ bản như `FROM`, `WORKDIR`, `COPY`, `EXPOSE`, `ENTRYPOINT`.

Lưu ý: lệnh trên cần chạy sau khi file JAR đã được tạo. `.dockerignore` không nên loại bỏ thư mục `build/libs`.

## 3. Chạy và kiểm tra service

```bash
docker run -d --name payment-service-lab -p 8080:8080 payment-service:1.0.0
docker logs payment-service-lab
curl http://localhost:8080/api/payments/health
curl -i -X POST http://localhost:8080/api/payments -H 'Content-Type: application/json' -d '{"orderId":"ORDER-1","amount":100000}'
```

### Kết quả mong đợi

- `GET /api/payments/health` trả về trạng thái `UP`
- `POST /api/payments` trả về HTTP `201` cùng các trường như `paymentId`, `orderId`, `amount`, `status: DEMO_CREATED`
- Nếu `amount` âm, thiếu `orderId` hoặc thiếu `amount`, API trả HTTP `400`

Nếu cổng `8080` đang bận, hãy dùng lệnh sau:

```bash
docker run -d --name payment-service-lab -p 8081:8080 payment-service:1.0.0
```

Sau đó truy cập `http://localhost:8081`.

> `EXPOSE` chỉ khai báo cổng trong container, còn `-p` mới là lệnh ánh xạ cổng từ máy host ra container.

## 4. Dừng container khi hoàn tất

```bash
docker stop payment-service-lab
docker rm payment-service-lab
```

Nếu build lại image, nên xóa container cũ trước rồi chạy container mới để đảm bảo dùng đúng image vừa build.

## Nộp bài

- File `Dockerfile` (không phải `.txt`)
- Link GitHub repository

Có thể bổ sung ảnh minh chứng như:

- `docker build`
- `docker ps`
- kết quả gọi API

Không cần commit thư mục `build/libs` hoặc file JAR lên Git.
