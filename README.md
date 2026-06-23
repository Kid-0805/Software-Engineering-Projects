# Software-Engineering-Projects
本项目是一个基于微服务架构的外卖全链路后台管理系统，采用前后端分离开发模式，覆盖用户端微信小程序与管理端PC后台两大交互终端。

## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0
- Redis 7.0
- Node.js 18+
- 微信开发者工具

- ### 本地运行

**1. 初始化数据库**

# 在 MySQL 中执行
source take-out.sql

**2. 修改配置文件**
# springboot/server/src/main/resources/application-dev.yml
# 修改数据库连接、Redis 连接地址

**3. 启动后端**
cd springboot/server
mvn clean install
mvn spring-boot:run

**4. 启动管理端**
cd vue3
npm install
npm run dev

**5. 启动小程序端**
使用微信开发者工具打开 app/ 目录

## 🛠 技术栈

| 层级 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot 3.2.5 + JDK 17 |
| **数据持久化** | MyBatis + MySQL 8.0 |
| **缓存中间件** | Redis 7.0（店铺状态缓存） |
| **前端管理端** | Vue 3 + TypeScript + Element Plus + ECharts |
| **前端用户端** | UniApp（一套代码编译至微信小程序） |
| **接口管理** | Apifox（IDEA插件 + 在线文档） |
| **实时通信** | WebSocket（订单实时推送） |
| **部署方案** | Docker 容器化 + Nginx 反向代理 |
| **项目管理** | Maven 多模块（common/pojo/server） |

## ✨ 核心功能

### 用户端（微信小程序）
- 🔑 微信授权登录
- 📂 分类浏览菜品/套餐
- 🛒 购物车管理（增删改查）
- 📝 下单（口味选择、地址选择、备注、餐具数量）
- 💳 模拟支付
- 📦 历史订单追踪（待接单 → 配送中 → 已完成）
- 📍 地址管理（增删改查、设置默认地址）

### 管理端（PC后台）
- 🔐 员工登录/注册/权限分级（超级管理员/普通员工）
- 📊 仪表盘（营业数据概览、ECharts图表）
- 🏷️ 分类管理（菜品/套餐分类增删改查）
- 🍽️ 菜品管理（CRUD、启售/停售、口味配置）
- 📦 套餐管理（CRUD、关联菜品）
- 📋 订单管理（接单/拒单/取消/派送/完成）
- 📈 数据统计（营业额趋势、用户增长、订单统计、销量Top10、Excel导出）
- 🔔 WebSocket 新订单实时提醒

## 🔒 安全机制

| 安全维度 | 实现方式 |
|----------|----------|
| **认证** | JWT 双Token（管理员/用户独立密钥） |
| **授权** | 权限分级（超级管理员/普通员工） |
| **密码保护** | MD5 哈希存储 |
| **上下文隔离** | ThreadLocal 存储当前用户ID |
| **异常处理** | GlobalExceptionHandler 全局捕获 |
| **自动填充** | AOP 切面自动填充公共字段（create_time/update_time等） |
| **定时任务** | 15分钟未支付自动取消、1小时派送超时自动完成 |

## 🎨 设计模式应用

| 设计模式 | 应用位置 |
|----------|----------|
| 单例模式 | WebSocketServer 会话管理 |
| 观察者模式 | WebSocket 订单状态实时推送 |
| 代理模式 | AutoFillAspect AOP 自动填充 |
| 策略模式 | JWT 双Token认证拦截器 |
| 模板方法模式 | WebMvcConfiguration 继承框架模板 |
| 工厂模式 | @Bean 配置类集中管理组件创建 |

## 📊 质量保障

- **单元测试**：JUnit 5，核心业务覆盖率 100%，通过率 100%
- **压力测试**：JMeter，20并发×5循环，平均响应时间 **2ms**，吞吐量 **104 req/s**，异常率 **0%**
- **集成测试**：前后端全链路端到端测试通过

