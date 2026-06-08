# Docker 部署指南

## 前置要求

- Docker 20.10+
- Docker Compose v2.0+

## 快速开始

### 1. 修改环境变量

```bash
# 复制环境变量模板
cp .env .env.local

# 编辑 .env.local，修改以下关键配置：
# - MYSQL_ROOT_PASSWORD: 数据库root密码
# - MYSQL_PASSWORD: 应用数据库密码
# - REDIS_PASSWORD: Redis密码
# - TOKEN_SECRET: JWT密钥（至少64字节的随机字符串）
```

### 2. 生成JWT密钥

```bash
# Linux/Mac
openssl rand -base64 64

# Windows (PowerShell)
[Convert]::ToBase64String([byte[]]::new(64) | ForEach-Object {Get-Random -Maximum 256})
```

### 3. 修改数据库连接配置

编辑 `ruoyi-admin/src/main/resources/application-prod.yml`，确保数据库连接信息与docker-compose一致：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://mysql:3306/ruoyi?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
        username: ruoyi
        password: ${MYSQL_PASSWORD:Ruoyi@123456}
  redis:
    host: redis
    port: 6379
    password: ${REDIS_PASSWORD:redis123456}
```

### 4. 构建前端

```bash
cd ruoyi-vue3

# 安装依赖
npm install

# 构建生产版本
npm run build
```

### 5. 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f ruoyi-admin
```

### 6. 访问系统

- 前端页面: http://localhost
- 后端API: http://localhost/prod-api/
- Druid监控: http://localhost/prod-api/druid/ (默认关闭)

### 7. 初始化数据库

首次启动时，`sql/` 目录下的SQL脚本会自动执行。如果没有自动执行：

```bash
# 进入MySQL容器
docker-compose exec mysql mysql -uroot -p

# 手动导入
source /docker-entrypoint-initdb.d/tables.sql
```

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 停止并删除数据卷（慎用！会删除数据库数据）
docker-compose down -v

# 重新构建并启动
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 进入容器
docker-compose exec ruoyi-admin sh
docker-compose exec mysql bash
```

## 目录结构

```
.
├── docker-compose.yml      # Docker Compose配置
├── Dockerfile              # 后端Java应用构建配置
├── .env                    # 环境变量配置
├── nginx/
│   ├── conf.d/
│   │   └── default.conf    # Nginx配置
│   └── ssl/                # SSL证书目录
├── sql/                    # 数据库初始化脚本
├── uploadPath/             # 上传文件目录
├── logs/                   # 应用日志目录
└── ruoyi-vue3/
    └── dist/               # 前端构建产物
```

## 生产环境配置建议

### 1. 安全配置

```bash
# 修改所有默认密码
# 使用强JWT密钥（至少64字节）
# 启用HTTPS
# 限制Druid监控访问IP
```

### 2. 性能优化

```yaml
# docker-compose.yml中添加资源限制
services:
  ruoyi-admin:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### 3. 数据备份

```bash
# 备份MySQL
docker-compose exec mysql mysqldump -uroot -p ruoyi > backup_$(date +%Y%m%d).sql

# 备份Redis
docker-compose exec redis redis-cli -a redis123456 BGSAVE
docker cp ruoyi-redis:/data/dump.rdb ./backup/redis_$(date +%Y%m%d).rdb
```

### 4. 日志管理

```yaml
# docker-compose.yml中添加日志配置
services:
  ruoyi-admin:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## HTTPS配置

1. 将SSL证书放到 `nginx/ssl/` 目录

2. 编辑 `nginx/conf.d/default.conf`，取消HTTPS配置注释

3. 重启Nginx：
```bash
docker-compose restart ruoyi-nginx
```

## 故障排查

### 1. 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 实时查看日志
docker-compose logs -f

# 查看最近100行
docker-compose logs --tail=100
```

### 2. 检查服务状态

```bash
docker-compose ps
docker-compose top
```

### 3. 进入容器调试

```bash
# 进入后端容器
docker-compose exec ruoyi-admin sh

# 进入MySQL容器
docker-compose exec mysql mysql -uroot -p

# 进入Redis容器
docker-compose exec redis redis-cli -a redis123456
```

### 4. 重建服务

```bash
# 停止并删除所有容器
docker-compose down

# 重新构建并启动
docker-compose up -d --build
```

## 注意事项

1. **首次部署**：确保`sql/`目录下有完整的初始化SQL脚本
2. **数据持久化**：MySQL和Redis数据存储在Docker Volume中，`docker-compose down -v`会删除数据
3. **端口冲突**：确保3306、6379、80、8080端口未被占用
4. **文件权限**：Linux环境下可能需要设置uploadPath目录权限：`chmod 777 uploadPath`
