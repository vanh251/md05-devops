BÀI 3: BẢO MẬT BẰNG HTTPS VỚI LET'S ENCRYPT

1. Chuỗi lệnh cài đặt Certbot trên Ubuntu

Để cài đặt công cụ Certbot và thư viện plugin hỗ trợ tự động cấu hình cho Nginx, chạy các lệnh sau trên Terminal:

# Cập nhật danh sách các gói phần mềm
sudo apt update

# Cài đặt Certbot và plugin dành cho Nginx
sudo apt install certbot python3-certbot-nginx -y

2. Lệnh Certbot tự động cấp phát và cấu hình SSL

Để Certbot tự động lấy chứng chỉ SSL cho domain api.my-ecommerce.com và tự động cập nhật cấu hình Nginx hiện tại, bao gồm cả việc chèn mã chuyển hướng HTTP sang HTTPS, sử dụng lệnh:

sudo certbot --nginx -d api.my-ecommerce.com

Sau khi chạy lệnh, Certbot sẽ thực hiện các công việc chính:

Xác thực quyền kiểm soát domain.

Cấp phát chứng chỉ SSL/TLS từ Let's Encrypt.

Tự động cập nhật cấu hình Nginx.

Có thể cấu hình chuyển hướng từ HTTP sang HTTPS.

Lưu chứng chỉ và khóa bí mật vào thư mục /etc/letsencrypt/.

3. Cấu hình Nginx HTTPS thủ công (Port 443)

Nếu chọn cách viết file cấu hình Nginx thủ công thay vì để Certbot tự động cấu hình, cần thực hiện hai phần:

Chuyển hướng request từ Port 80 (HTTP) sang Port 443 (HTTPS).

Cấu hình HTTPS và khai báo đường dẫn tới chứng chỉ SSL cùng khóa bí mật.

Ví dụ:

# Block 1: Chuyển hướng các request ở Port 80 (HTTP) sang HTTPS
server {
    listen 80;
    server_name api.my-ecommerce.com;

    # Trả về mã 301 chuyển hướng vĩnh viễn
    return 301 https://$host$request_uri;
}

# Block 2: Xử lý các request an toàn ở Port 443 (HTTPS)
server {
    listen 443 ssl;
    server_name api.my-ecommerce.com;

    # Chứng chỉ SSL do Let's Encrypt cấp
    ssl_certificate /etc/letsencrypt/live/api.my-ecommerce.com/fullchain.pem;

    # Khóa bí mật của chứng chỉ
    ssl_certificate_key /etc/letsencrypt/live/api.my-ecommerce.com/privkey.pem;

    # Cấu hình proxy đẩy request về Backend API Gateway
    location / {
        proxy_pass http://localhost:8080;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

Giải thích các thành phần chính

Thành phần

Ý nghĩa

listen 80

Nginx lắng nghe request HTTP trên Port 80

listen 443 ssl

Nginx lắng nghe request HTTPS trên Port 443

server_name

Tên domain được phục vụ

ssl_certificate

Đường dẫn tới chứng chỉ SSL

ssl_certificate_key

Đường dẫn tới khóa bí mật

proxy_pass

Chuyển tiếp request tới Backend API Gateway

X-Forwarded-Proto

Giúp backend biết request ban đầu sử dụng HTTP hay HTTPS

Lưu ý: Không đặt URL Markdown vào đường dẫn file Linux. Đường dẫn đúng là:

/etc/letsencrypt/live/api.my-ecommerce.com/fullchain.pem

/etc/letsencrypt/live/api.my-ecommerce.com/privkey.pem

4. Thiết lập tự động gia hạn (Auto Renew)

4.1. Thời hạn của chứng chỉ Let's Encrypt

Chứng chỉ Let's Encrypt có thời hạn 90 ngày. Vì vậy, hệ thống cần được cấu hình để tự động kiểm tra và gia hạn chứng chỉ trước khi hết hạn.

4.2. Cách 1: Sử dụng Systemd Timer

Khi cài đặt Certbot thông qua apt trên Ubuntu, hệ thống thường đã có cơ chế tự động chạy Certbot để kiểm tra việc gia hạn.

Có thể kiểm tra cấu hình và mô phỏng quá trình gia hạn bằng lệnh:

sudo certbot renew --dry-run

Lệnh này thực hiện dry run, tức là mô phỏng quá trình gia hạn mà không thực sự thay đổi chứng chỉ đang sử dụng.

Nếu quá trình kiểm tra thành công, Certbot có khả năng thực hiện gia hạn tự động khi chứng chỉ gần hết hạn.

Có thể kiểm tra timer bằng:

systemctl list-timers | grep certbot

4.3. Cách 2: Cấu hình thủ công bằng Cronjob

Nếu hệ thống không có hoặc không sử dụng Systemd Timer, có thể cấu hình Cronjob để tự động chạy Certbot.

Mở crontab của root:

sudo crontab -e

Thêm dòng sau:

0 3 * * * /usr/bin/certbot renew --quiet && systemctl reload nginx

Cronjob trên có ý nghĩa:

0 3 * * *: chạy vào 03:00 sáng mỗi ngày.

/usr/bin/certbot renew --quiet: kiểm tra và gia hạn chứng chỉ nếu cần.

&&: chỉ thực hiện lệnh tiếp theo nếu Certbot chạy thành công.

systemctl reload nginx: yêu cầu Nginx tải lại cấu hình và chứng chỉ mới mà không cần dừng service.

Lưu ý: Không nhất thiết phải dùng cả Systemd Timer và Cronjob cùng lúc. Nếu Certbot đã được cấu hình tự động bằng Systemd Timer, nên kiểm tra và sử dụng cơ chế đó thay vì tạo thêm Cronjob.

5. Quy trình tổng quát

Có thể tóm tắt quy trình triển khai HTTPS với Let's Encrypt và Nginx như sau:

Client
   |
   | HTTPS :443
   v
+----------------------+
|       Nginx          |
|   SSL/TLS Termination |
+----------------------+
   |
   | HTTP :8080
   v
+----------------------+
|   Backend API Gateway|
+----------------------+
   |
   v
Microservices

Quy trình thực hiện:

1. Cài đặt Certbot
       |
       v
2. Cấp chứng chỉ SSL cho domain
       |
       v
3. Cấu hình Nginx Port 80
       |
       | HTTP
       v
   Redirect 301
       |
       v
4. Nginx Port 443
       |
       | HTTPS
       v
5. SSL/TLS Termination
       |
       v
6. Proxy request tới API Gateway :8080
       |
       v
7. Tự động gia hạn chứng chỉ

6. Các lệnh kiểm tra quan trọng

Kiểm tra cấu hình Nginx

sudo nginx -t

Nếu cấu hình hợp lệ, kết quả thường có dạng:

syntax is ok
test is successful

Reload Nginx

sudo systemctl reload nginx

Kiểm tra trạng thái Nginx

sudo systemctl status nginx

Kiểm tra chứng chỉ

sudo certbot certificates

Kiểm tra khả năng tự động gia hạn

sudo certbot renew --dry-run

7. Kết luận

Việc kết hợp Nginx + HTTPS + Let's Encrypt + Certbot giúp bảo vệ kết nối giữa client và hệ thống backend.

Mô hình triển khai:

Port 80: nhận HTTP và chuyển hướng sang HTTPS.

Port 443: tiếp nhận kết nối HTTPS và xử lý SSL/TLS.

Let's Encrypt: cung cấp chứng chỉ SSL/TLS miễn phí.

Certbot: tự động cấp phát và gia hạn chứng chỉ.

Nginx: thực hiện SSL/TLS termination và reverse proxy tới Backend API Gateway.

Auto Renew: đảm bảo chứng chỉ được gia hạn trước khi hết hạn.