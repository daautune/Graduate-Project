FROM python:3.6.0

ENV PYTHONUNBUFFERED 1

#Chạy lệnh tạo thư mục mới trong container
RUN mkdir /capstone2ereading

WORKDIR /capstone2ereading

#Copy file .txt đến thư mục container
COPY requirements.txt /capstone2ereading/requirements.txt

# Cập nhật pip của container 
RUN pip install --upgrade pip

# Cài các package lên container 
RUN pip install -r requirements.txt

#Copy source code vào container
COPY . /capstone2ereading/

# Chạy các cài đặt từ dòng lênh đinh sẵn trong file 
COPY docker-entrypoint.sh /entrypoint.sh

# Set quyền run cho file .sh
RUN chmod +x /entrypoint.sh
