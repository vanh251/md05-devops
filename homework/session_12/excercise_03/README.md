# BÀI TẬP 3: CẤU HÌNH HEADER SECURITY

## 1. Cấu hình Nginx bổ sung Security Headers

Để đáp ứng yêu cầu từ đội Security, chúng ta sử dụng chỉ thị `add_header` của Nginx. Việc bổ sung tham số `always` ở cuối mỗi dòng là một "best practice" (thực hành tốt nhất) để đảm bảo Nginx trả về các header này ngay cả khi hệ thống báo lỗi (như 404, 500).

Dưới đây là file cấu hình hoàn chỉnh:

```nginx
server {
    listen 443 ssl;
    server_name secure.example.com;

    ssl_certificate /etc/nginx/ssl/server.crt;
    ssl_certificate_key /etc/nginx/ssl/server.key;

    # Cấu hình Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    location / {
        proxy_pass http://localhost:8080;
    }
}
```

### Giải thích ý nghĩa các Header:

* **`X-Frame-Options: SAMEORIGIN`**: Chống Clickjacking. Ngăn không cho website khác nhúng trang web của cậu vào thẻ `<iframe>`, trừ khi đó là các trang thuộc cùng một tên miền (same origin).
* **`X-Content-Type-Options: nosniff`**: Ngăn chặn trình duyệt tự động đoán định dạng file (MIME-sniffing), ép trình duyệt phải tuân thủ đúng định dạng `Content-Type` mà máy chủ trả về, giúp phòng ngừa các cuộc tấn công XSS qua file tải lên.
* **`Strict-Transport-Security`** **(HSTS)**: Ép buộc trình duyệt luôn luôn phải kết nối với website bằng giao thức HTTPS an toàn trong vòng 1 năm (`max-age=31536000` giây), bao gồm cả các domain con (`includeSubDomains`).

## 2. Câu lệnh kiểm chứng

Sau khi lưu file và chạy `sudo systemctl reload nginx`, ta sử dụng lệnh `curl` với cờ `-I` (chỉ lấy phần Header của HTTP Response) để kiểm chứng:

Bash

```bash
curl -I [https://secure.example.com](https://secure.example.com)
```

**Kết quả mong đợi trả về trên Terminal sẽ có dạng:**

Plaintext

```text
HTTP/1.1 200 OK
Server: nginx
Date: Tue, 15 Sep 2026 01:40:00 GMT
Content-Type: text/html; charset=utf-8
Connection: keep-alive
X-Frame-Options: SAMEORIGIN
X-Content-Type-Options: nosniff
Strict-Transport-Security: max-age=31536000; includeSubDomains
```
