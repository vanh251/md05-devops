BÀI 4: TỐI ƯU HÓA VÀ RELOAD CẤU HÌNH KHÔNG GIÁN ĐOẠN

1. Cấu hình xử lý lỗi 413 và Tối ưu tốc độ (Gzip)

Để giải quyết lỗi 413 Request Entity Too Large khi upload ảnh và tăng tốc độ tải trang, ta bổ sung client_max_body_size và block gzip vào cấu hình Nginx (có thể đặt trong block http, server hoặc location).

Dưới đây là mẫu cấu hình được đặt ở cấp độ server:

server {
    listen 80;
    server_name api.my-ecommerce.com;

    # 1. Khắc phục lỗi 413: Mở rộng giới hạn kích thước body của request lên 10MB
    client_max_body_size 10M;

    # 2. Tối ưu tốc độ: Bật và cấu hình nén Gzip
    gzip on;
    gzip_comp_level 5; # Mức độ nén (1-9), mức 5 là cân bằng tốt giữa tốc độ CPU và dung lượng nén
    gzip_min_length 256; # Chỉ nén các file có kích thước lớn hơn 256 bytes
    gzip_proxied any;
    gzip_vary on;

    # Chỉ định đích danh các định dạng file văn bản cần được nén
    gzip_types
        text/plain
        text/css
        text/javascript
        application/javascript
        application/json
        application/xml
        text/xml;

    # Các cấu hình proxy định tuyến giữ nguyên...
    location / {
        proxy_pass http://localhost:8080;
    }
}


2. Giải đáp "Bẫy": Reload vs Restart trên Production

Kiểm tra cú pháp trước khi áp dụng

Trước khi thực hiện bất kỳ lệnh thay đổi trạng thái nào, lệnh bắt buộc phải gõ đầu tiên để kiểm tra tính hợp lệ của file cấu hình (phát hiện lỗi đánh máy, thiếu dấu ;):

Bash

sudo nginx -t

(Chỉ tiếp tục khi hệ thống báo syntax is ok và test is successful**).

Sự khác biệt chí mạng giữa Restart và Reload

Trên môi trường Production, việc sử dụng sai lệnh có thể gây thiệt hại nghiêm trọng đến trải nghiệm khách hàng.

systemctl restart nginx (KHÔNG DÙNG):
Lệnh này sẽ ngay lập tức tắt (kill) tiến trình Nginx hiện tại và khởi động lại một tiến trình hoàn toàn mới.
Hậu quả: Toàn bộ kết nối của khách hàng đang diễn ra (đang tải dở trang, đang upload file, hoặc đang xử lý thanh toán) sẽ bị ép ngắt kết nối đột ngột, gây ra thời gian chết (Downtime).

systemctl reload nginx (BẮT BUỘC DÙNG):
Lệnh này sử dụng cơ chế Zero-downtime. Khi chạy, Nginx sẽ:

Kiểm tra lại cấu hình một lần nữa.

Khởi tạo các worker processes mới áp dụng cấu hình mới để đón nhận các request tiếp theo.

Các worker processes cũ vẫn tiếp tục chạy để hoàn thành nốt việc trả response cho các khách hàng hiện tại.

Chỉ khi khách hàng hiện tại xử lý xong, worker cũ mới tự động tắt một cách duyên dáng (Graceful shutdown).
Kết quả: Cấu hình mới được áp dụng mà khách hàng không hề cảm nhận được sự gián đoạn.