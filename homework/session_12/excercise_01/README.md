# BÀI TẬP 1: XỬ LÝ SỰ CỐ SẬP NGINX

## 1. Xác định nguyên nhân lỗi

Dựa vào thông báo lỗi: `nginx: [emerg] unexpected "proxy_set_header" in /etc/nginx/sites-enabled/app.conf:7`

Nginx báo lỗi ở dòng 7, nhưng nguyên nhân thực sự nằm ở **dòng 6**. Câu lệnh `proxy_pass http://localhost:8080` đang bị **thiếu dấu chấm phẩy (`;`)** ở cuối. Vì thiếu dấu `;`, Nginx tiếp tục đọc dòng 7 và cố ghép nó vào lệnh của dòng 6, dẫn đến lỗi cú pháp không thể nhận diện được từ khóa `proxy_set_header`.

## 2. Cấu hình đã sửa

Bổ sung dấu chấm phẩy (`;`) vào cuối dòng `proxy_pass`:

```nginx
server {
    listen 80;
    server_name example.com;

    location / {
        proxy_pass http://localhost:8080; # Đã sửa lỗi: Thêm dấu ; ở cuối
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 3. Lệnh kiểm tra và tải lại dịch vụ

Sau khi sửa file cấu hình, thực thi lần lượt các lệnh sau trên Terminal:

**Bước 1: Kiểm tra cấu hình (Test syntax)**

Bash

```bash
sudo nginx -t
```

*Cách xác nhận thành công:* Hệ thống trả về thông báo `nginx: configuration file /etc/nginx/nginx.conf syntax is ok` và `nginx: configuration file /etc/nginx/nginx.conf test is successful`.

**Bước 2: Tải lại Nginx (Reload)**

Bash

```bash
sudo systemctl reload nginx
```

*Cách xác nhận thành công:* Lệnh chạy xong không trả về lỗi màu đỏ nào, dịch vụ Nginx cập nhật cấu hình mới mà không làm rớt kết nối của người dùng đang online.
