#!/bin/bash

echo "=== BẮT ĐẦU CÀI ĐẶT MÔI TRƯỜNG QUICKBITE ==="

# 1. Cập nhật hệ thống
echo "1. Đang cập nhật hệ thống..."
sudo apt-get update && sudo apt-get upgrade -y

# 2. Cài đặt các gói phần mềm bắt buộc
echo "2. Đang cài đặt openjdk-17-jdk, git, curl..."
sudo apt-get install -y openjdk-17-jdk git curl

# 3. Kiểm tra và tạo group 'quickbite'
echo "3. Đang kiểm tra nhóm (group) 'quickbite'..."
if ! getent group quickbite > /dev/null 2>&1; then
    sudo groupadd quickbite
    echo "   -> Đã tạo nhóm 'quickbite' thành công."
else
    echo "   -> Nhóm 'quickbite' đã tồn tại, bỏ qua bước tạo mới."
fi

# 4. Kiểm tra và tạo user 'quickbite'
echo "4. Đang kiểm tra người dùng (user) 'quickbite'..."
if ! getent passwd quickbite > /dev/null 2>&1; then
    # -r: system user (không tự tạo thư mục home)
    # -g: gán vào group quickbite
    # -s /bin/false: không cho phép login trực tiếp
    sudo useradd -r -g quickbite -s /bin/false quickbite
    echo "   -> Đã tạo user 'quickbite' thành công."
else
    echo "   -> User 'quickbite' đã tồn tại, bỏ qua bước tạo mới."
fi

echo "=== HOÀN TẤT CÀI ĐẶT ==="

