# ShopEasy Inventory and POS System

面向中小商家的进销存 + 门店收银管理系统（Web 版）。

当前为项目骨架：商品管理、收银台选品与购物车已可用，销售结算、采购、库存流水、会员、报表等模块待实现。

## 技术栈

| 层次 | 选型 |
| --- | --- |
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router + Axios |
| 后端 | Spring Boot 3.3 + Java 21 + MyBatis-Plus |
| 数据库 | MySQL 8 + Flyway |
| 接口文档 | springdoc-openapi（Swagger UI） |

## 目录结构

```text
ShopEasy/
├── web/                        # 前端（Vue 3 + Vite）
│   └── src/
│       ├── api/                # Axios 封装与接口定义
│       ├── layout/             # 主框架布局
│       ├── router/             # 路由
│       ├── stores/             # Pinia 状态（购物车）
│       ├── styles/             # 全局样式
│       └── views/              # 页面
├── server/                     # 后端（Spring Boot）
│   └── src/main/
│       ├── java/com/shopeasy/  # common / config / module
│       └── resources/
│           ├── application.yml
│           └── db/migration/   # Flyway 表结构与种子数据
├── deploy/docker-compose.yml   # 本地 MySQL（端口 3307）
└── README.md
```

## 环境要求

- JDK 21（Spring Boot 3 最低要求 17）
- Maven 3.8+
- Node.js 20.19+ / 22.12+
- MySQL 8（本地安装或 Docker）

## 本地启动

### 1. 准备数据库

方式一：使用已有 MySQL，创建数据库（表结构由 Flyway 自动初始化）：

```sql
CREATE DATABASE shopeasy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

方式二：使用 Docker（映射到本机 3307，避免与已有 MySQL 冲突）：

```bash
docker compose -f deploy/docker-compose.yml up -d
```

### 2. 启动后端

```powershell
cd server
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"   # 按实际 JDK 路径调整
mvn spring-boot:run
```

数据库连接通过环境变量覆盖，默认值如下：

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_HOST` | `localhost` | 数据库地址 |
| `DB_PORT` | `3306` | 使用 Docker 时改为 `3307` |
| `DB_NAME` | `shopeasy` | 数据库名 |
| `DB_USER` | `root` | 用户名 |
| `DB_PASSWORD` | 空 | 密码 |

启动后：

- 健康检查：<http://localhost:8080/api/health>
- 接口文档：<http://localhost:8080/swagger-ui.html>

首次启动时 Flyway 会执行 `db/migration` 下的脚本，建表并写入演示分类与商品。

### 3. 启动前端

```bash
cd web
npm install
npm run dev
```

访问 <http://localhost:5173>。开发环境已配置 `/api` 代理到 `http://localhost:8080`。

## 已实现

- 商品管理：分页查询、关键字/分类筛选、新增、编辑、删除、库存预警标识
- 收银台：分类筛选、商品搜索、加入购物车、数量增减、金额合计
- 后端骨架：统一响应体、全局异常处理、参数校验、分页插件、CORS、Flyway 迁移
- 数据库结构：分类、商品、供应商、会员、员工、采购单、销售单、库存流水、收银班次

## 待实现

- 销售结算：生成销售单、扣减库存、写库存流水、支付方式与会员积分
- 采购入库：采购单、到货入库、供应商管理
- 库存流水查询与盘点调整
- 会员管理与积分
- 经营报表（日结、商品排行、毛利）
- 员工登录与角色权限（Spring Security + JWT）

## 设计约定

- 库存以 `stock_movement` 流水为准，`product.stock` 仅作快照；单据作废走红冲，不物理删除。
- 表结构统一由 Flyway 管理，禁止手工改库。
- 所有表预留多门店扩展空间，但首版按单店实现。
- 暂不接入外接硬件：后续扫码枪按键盘输入处理，小票使用浏览器打印样式。
