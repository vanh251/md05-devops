# BÀI TẬP 2: TỐI ƯU TIMEOUT CHO PROXY (KHẮC PHỤC LỖI 504)

## 1. Cấu hình Nginx đã cập nhật

Để khắc phục lỗi `504 Gateway Time-out` do Backend Spring Boot tốn tới 90 giây để xuất báo cáo, chúng ta cần ghi đè thời gian chờ mặc định (60s) của Nginx bằng cách bổ sung các chỉ thị timeout vào block `location /api/export/`:

```nginx
server {
    listen 80;
    server_name api.example.com;

    location /api/export/ {
        proxy_pass http://localhost:8080;
        
        # Cấu hình tăng thời gian chờ lên 120 giây
        proxy_connect_timeout 120s;
        proxy_send_timeout 120s;
        proxy_read_timeout 120s;
    }
}
```

## 2. Giải thích ý nghĩa các thông số Timeout

* **`proxy_read_timeout`** **(Quan trọng nhất cho lỗi 504):** Xác định thời gian tối đa Nginx sẽ chờ để **nhận được dữ liệu phản hồi** từ Backend. Vì chức năng export báo cáo tốn 90 giây để xử lý, việc tăng mức này lên 120s (120 giây) sẽ giúp Nginx kiên nhẫn chờ đợi thay vì tự động ngắt kết nối và báo lỗi 504.
* **`proxy_connect_timeout`** **:** Thời gian tối đa Nginx cho phép để **thiết lập kết nối** mạng (bắt tay TCP) với máy chủ Backend. (Mặc định 60s thường là đủ, nhưng nên đồng bộ 120s trong trường hợp mạng nội bộ có độ trễ cao).
* **`proxy_send_timeout`** **:** Thời gian tối đa Nginx chờ khi **gửi dữ liệu request** (ví dụ payload hoặc file đính kèm từ người dùng) sang cho Backend.
