# 智能会议室预订系统

基于 Spring Boot AI + DeepSeek 的智能会议室预订系统，支持自然语言预订。

## 🎯 功能特性

- ✨ **自然语言交互**：用户用日常语言描述预订需求
- 🤖 **AI智能理解**：DeepSeek模型理解并提取预订信息
- 🔧 **Function Calling**：AI自动调用预订接口完成操作
- 💾 **持久化存储**：PostgreSQL数据库存储所有预订记录
- 🎨 **简洁前端**：单页面HTML应用，交互友好
- ✅ **完整测试**：单元测试覆盖核心业务逻辑

## 🏗️ 技术栈

### 后端
- Spring Boot 3.2.0
- Spring AI 1.0.0-M4
- Spring Data JPA
- PostgreSQL 数据库
- DeepSeek API (OpenAI兼容)
- Lombok

### 前端
- 原生 HTML + CSS + JavaScript
- 响应式设计

### 测试
- JUnit 5
- AssertJ
- H2 内存数据库(测试用)

## 📦 快速开始

### 1. 环境准备

```bash
# 安装 PostgreSQL
# macOS
brew install postgresql
brew services start postgresql

# Ubuntu/Debian
sudo apt-get install postgresql
sudo service postgresql start

# 创建数据库
createdb meeting_booking
```

### 2. 配置数据库

```bash
# 执行数据库初始化脚本
psql -d meeting_booking -f schema.sql
```

### 3. 配置 DeepSeek API

```bash
# 设置环境变量
export DEEPSEEK_API_KEY=your_deepseek_api_key_here
```

或在 `application.yml` 中配置：

```yaml
spring:
   ai:
      openai:
         api-key: your_deepseek_api_key_here
```

### 4. 启动后端

```bash
# 编译项目
mvn clean install

# 运行应用
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动

### 5. 打开前端

直接用浏览器打开 `index.html` 文件即可使用

## 🧪 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=BookingServiceTest
mvn test -Dtest=RepositoryTest
```

## 📚 API 文档

### Chat API

**POST** `/api/chat`

发送自然语言消息与AI交互

**Headers:**
```
Content-Type: application/json
X-Session-Id: <session_id> (可选，用于保持对话上下文)
```

**Request Body:**
```json
{
   "message": "我想预订明天下午2点到4点的会议室A，讨论项目进展，我是张三"
}
```

**Response:**
```json
{
   "response": "好的，我已经为您预订了会议室A...",
   "bookingInfo": {
      "id": 1,
      "roomName": "会议室A",
      "title": "项目进展讨论",
      "organizer": "张三",
      "startTime": "2025-11-15 14:00:00",
      "endTime": "2025-11-15 16:00:00",
      "status": "CONFIRMED"
   }
}
```

### Room Status API

**GET** `/api/rooms/status`

获取指定日期的会议室状态

**Query Parameters:**
```
date: YYYY-MM-DD (可选，默认为当天)
```

**Response:**
```json
[
  {
    "name": "会议室A",
    "capacity": 10,
    "location": "3楼东侧",
    "facilities": "投影仪,白板,视频会议",
    "status": "partial",
    "schedules": [
      {
        "time": "10:00-12:00",
        "title": "产品评审会议",
        "organizer": "张三"
      }
    ]
  }
]
```

## 🎨 使用示例

### 示例对话 1：完整预订

**用户：** "我想预订明天下午2点到4点的会议室A，组织团队会议，预计10人参加，我是张三"

**AI：** "好的，我已经为您预订了会议室A。预订详情如下：
- 会议室：会议室A（3楼东侧，可容纳10人）
- 时间：2025年11月15日 14:00-16:00
- 组织者：张三
- 参会人数：10人
  预订已确认，预订编号：#12345"

### 示例对话 2：信息补充

**用户：** "我需要预订一个会议室"

**AI：** "好的，我来帮您预订会议室。请告诉我：
1. 您想预订哪个会议室？（会议室A/B/C/大会议厅）
2. 什么时间？
3. 会议主题是什么？
4. 您的姓名？"

**用户：** "会议室B，明天上午10点到12点，产品评审会，我是李四"

**AI：** "收到！我为您预订会议室B，明天10:00-12:00，产品评审会。[预订成功]"

## 📊 数据库设计

### meeting_room 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | SERIAL | 主键 |
| name | VARCHAR(100) | 会议室名称 |
| capacity | INTEGER | 容纳人数 |
| location | VARCHAR(200) | 位置 |
| facilities | TEXT | 设施 |
| created_at | TIMESTAMP | 创建时间 |

### room_booking 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | SERIAL | 主键 |
| room_id | INTEGER | 会议室ID (外键) |
| title | VARCHAR(200) | 会议主题 |
| organizer | VARCHAR(100) | 组织者 |
| start_time | TIMESTAMP | 开始时间 |
| end_time | TIMESTAMP | 结束时间 |
| attendees | INTEGER | 参会人数 |
| description | TEXT | 会议描述 |
| status | VARCHAR(20) | 状态 |
| created_at | TIMESTAMP | 创建时间 |

## 🔍 测试覆盖

### BookingServiceTest
- ✅ 成功预订会议室
- ✅ 时间冲突检测
- ✅ 容量限制验证
- ✅ 时间范围验证
- ✅ 历史时间拒绝
- ✅ 会议室存在性检查
- ✅ 连续预订场景

### RepositoryTest
- ✅ 会议室查询
- ✅ 容量筛选
- ✅ 冲突检测算法
- ✅ 组织者预订查询
- ✅ 时间范围查询

## 🔧 配置说明

### application.yml 配置项

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/meeting_booking
    username: postgres
    password: your_password
  
  ai:
    openai:
      base-url: https://api.deepseek.com
      api-key: ${DEEPSEEK_API_KEY}
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
          max-tokens: 2000

# 安全配置（可根据需要调整）
app:
  security:
    max-conversations-per-session: 10  # 每个会话最多10次对话
    max-requests-per-minute: 20        # 每IP每分钟最多20次请求
    max-requests-per-hour: 100         # 每IP每小时最多100次请求
    ban-threshold: 50                  # 1分钟内超过50次请求将被封禁
    ban-duration-minutes: 60           # 封禁时长60分钟
```

### 配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| max-conversations-per-session | 10 | 每个会话最多对话次数，超过需开启新会话 |
| max-requests-per-minute | 20 | 每IP每分钟最多请求数（令牌桶算法） |
| max-requests-per-hour | 100 | 每IP每小时最多请求数 |
| ban-threshold | 50 | 异常检测阈值，1分钟内超过此值将封禁IP |
| ban-duration-minutes | 60 | IP封禁时长（分钟） |

### 查看当前配置

访问 `http://localhost:8080/api/config` 可以查看当前生效的配置。

## 🚀 部署建议

1. **生产环境配置**
   - 使用环境变量管理敏感信息
   - 配置连接池参数
   - 启用SQL慢查询日志

2. **数据库优化**
   - 定期清理历史数据
   - 监控索引使用情况
   - 配置备份策略

3. **AI调用优化**
   - 实现请求重试机制
   - 添加响应缓存
   - 监控API调用量

## 📝 开发说明

### 添加新的会议室

```sql
INSERT INTO meeting_room (name, capacity, location, facilities) 
VALUES ('会议室D', 15, '5楼', '投影仪,白板,视频会议');
```

### 扩展AI功能

在 `BookingFunctions.java` 中添加新的 Function Bean：

```java
@Bean
@Description("查询可用会议室")
public Function<QueryRequest, QueryResponse> queryAvailableRooms() {
   return request -> {
      // 实现逻辑
   };
}
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 👥 联系方式

如有问题，请提交 Issue 或联系开发团队。