#!/bin/bash
sudo useradd -m rikkeilms
echo "da tao user: rikkeilmd"

sudo mkdir -p /opt/rikkei/course-service 
echo "da tao thu muc thanh cong"

sudo chown -R rikkeilms:rikkeilms /opt/rekkei/course-service
echo "da chuyen quyen so huu cho rikkeilms"

sudo chmod 755 /opt/rekkei/course-service 
echo "thiet lap phan quyen 755 thanh cong"

