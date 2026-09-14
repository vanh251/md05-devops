# GIẢI ĐÁP BẪY: HEADER `X-Forwarded-For` TRONG NGINX

## 1. Bản chất của Reverse Proxy
Nginx hoạt động như một "người trung gian" (Reverse Proxy). Khi một người dùng (Client) gửi yêu cầu (request) đến hệ thống, luồng kết nối thực tế diễn ra như sau:
**Client (IP Thật)** ➔ **Nginx** ➔ **Backend API**

Do Nginx là người trực tiếp đứng ra nhận request từ Client, sau đó nó tự tạo ra một request hoàn toàn mới để gọi xuống Backend, nên Backend sẽ lầm tưởng Nginx chính là người đang sử dụng dịch vụ.

## 2. Trả lời câu hỏi bẫy
**Nếu không truyền Header `X-Forwarded-For`, Backend sẽ thấy IP của ai?**
- Backend sẽ **chỉ nhìn thấy IP của máy chủ Nginx** (thường là `127.0.0.1` nếu cài trên cùng một máy, hoặc IP nội bộ của Nginx trong mạng Docker).
- Backend hoàn toàn **không biết** IP thực sự của người dùng là gì.

## 3. Hậu quả nghiệp vụ nếu thiếu Header này
Nếu Backend chỉ nhìn thấy IP `127.0.0.1` của Nginx, hệ thống sẽ gặp hàng loạt vấn đề nghiêm trọng trong thực tế:
*   **Sập cơ chế Rate Limiting (Chống Spam):** Backend đếm số lượng request và lầm tưởng tất cả người dùng trên mạng đều là một người (Nginx). Khi số lượng request vượt ngưỡng, Backend sẽ chặn IP của Nginx, dẫn đến việc toàn bộ hệ thống bị gián đoạn (Downtime).
*   **Mất dấu vết bảo mật (Audit Log):** Không thể lưu lại lịch sử đăng nhập, không thể cảnh báo "Có người đăng nhập từ thiết bị lạ/quốc gia lạ".
*   **Chặn IP (Blacklist) vô hiệu:** Không thể cấm các IP của hacker vì Backend không hề nhận diện được chúng.

## 4. Giải pháp kỹ thuật
Để khắc phục, chúng ta bắt buộc phải cấu hình Nginx "nhét" IP thật của Client vào một bì thư (Header) trước khi gửi cho Backend:

```nginx
# Lấy IP thật của người dùng và gán vào Header X-Real-IP
proxy_set_header X-Real-IP $remote_addr;

# Ghi nối IP thật vào danh sách proxy đã đi qua (X-Forwarded-For)
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;