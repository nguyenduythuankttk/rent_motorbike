#!/bin/bash
# ============================================================
# Script biên dịch và chạy dự án Motorbike Rental
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$BASE_DIR/src"
OUT_DIR="$BASE_DIR/out"
LIB_DIR="$BASE_DIR/lib"

# Tìm JDBC driver
JDBC_JAR=$(find "$LIB_DIR" -name "mysql-connector*.jar" 2>/dev/null | head -1)

if [ -z "$JDBC_JAR" ]; then
    echo "=========================================="
    echo "  LỖI: Không tìm thấy MySQL JDBC driver!"
    echo "=========================================="
    echo ""
    echo "Vui lòng tải file .jar tại:"
    echo "  https://dev.mysql.com/downloads/connector/j/"
    echo "Chọn 'Platform Independent' → tải file .jar"
    echo "Đặt file vào thư mục: $LIB_DIR"
    exit 1
fi

echo ">> Sử dụng JDBC: $JDBC_JAR"
echo ">> Biên dịch..."

mkdir -p "$OUT_DIR"

# Thu thập tất cả file .java
JAVA_FILES=$(find "$SRC_DIR" -name "*.java")

javac -encoding UTF-8 -cp "$JDBC_JAR" -d "$OUT_DIR" $JAVA_FILES

if [ $? -ne 0 ]; then
    echo ""
    echo "Biên dịch thất bại!"
    exit 1
fi

echo ">> Biên dịch thành công!"
echo ">> Khởi động ứng dụng..."
echo ""

java -cp "$OUT_DIR:$JDBC_JAR" -Dfile.encoding=UTF-8 Main
