# 多阶段构建：第一阶段编译Java应用
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# 复制pom.xml并下载依赖（利用Docker缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并打包
COPY src ./src
RUN mvn package -DskipTests -B

# 第二阶段：运行环境
FROM openjdk:8-jre-slim

WORKDIR /home/ruoyi

# 安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制jar包
COPY --from=builder /app/ruoyi-admin/target/ruoyi-admin.jar .

# 创建上传目录和日志目录
RUN mkdir -p /home/ruoyi/uploadPath /home/ruoyi/logs

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", \
    "-Xms512m", \
    "-Xmx1024m", \
    "-Dspring.profiles.active=prod", \
    "ruoyi-admin.jar"]
