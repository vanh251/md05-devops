#!/bin/bash
echo "=== bắt đầu thiết lập thư mục và phân quyền ==="
echo "1. Đang tạo thư mục /opt/quickbite/user-service"
sudo mkdir -p /opt/quickbite/user-service

echo "2. chuyển quyền sở hữu cho user/group quickbite"
sudo chown -R quickbite:quickbite /opt/quickbite

echo "3. thiết lập quyền 750 cho thư mục /opt/quickbite"
sudo chmod 750 /opt/quickbite

echo "=== hoàn tất ==="

