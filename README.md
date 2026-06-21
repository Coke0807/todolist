# Todo List 全栈应用

一个基于 **Spring Boot 3 + MySQL 8 + Vue 3 + Element Plus** 的现代化待办应用，支持暗/亮主题、任务筛选排序、完善的 REST API 以及一键运行脚本。

## ✨ 功能亮点
- 任务 CRUD、优先级与截止日期管理
- 一键切换亮/暗主题，自动记忆用户偏好
- 多维筛选（全部/进行中/已完成）与排序（创建时间、截止时间、优先级）
- Element Plus 毛玻璃 UI、响应式布局、移动端适配
- 完整 RESTful API、统一响应体、全局异常处理、CORS 配置
- `todolist.sql` 提供数据库建表及测试数据

## 🧱 技术栈
| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3 (Composition API)、Vite、Element Plus、Axios、Day.js |
| 后端 | Spring Boot 3.5、Spring Data JPA、Jakarta Validation |
| 数据库 | MySQL 8.0 |

## 📂 目录结构
```
.
├─ backend/          # Spring Boot 服务
├─ frontend/         # Vue 3 + Vite 前端
├─ todolist.sql      # 数据库脚本
├─ run-all.bat/.sh   # 一键启动脚本
└─ README.md
```

## 🚀 快速开始
1. **导入数据库**（库不存在会在后端启动时自动创建，若需示例数据请执行脚本）
   ```sql
   mysql -uroot -p123456 < todolist.sql
   ```
2. **启动后端**（默认端口 8081，如需其他端口可先 `set SERVER_PORT=xxxx`）
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
3. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev -- --host
   ```
4. 浏览器访问 http://localhost:5173 即可体验。

> 也可直接运行根目录下的 `run-all.bat` 或 `run-all.sh` 一键开启前后端。

## 🔌 API 列表
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/api/tasks` | 获取任务列表 |
| GET | `/api/tasks/{id}` | 获取单个任务 |
| POST | `/api/tasks` | 创建任务 |
| PUT | `/api/tasks/{id}` | 更新任务 |
| DELETE | `/api/tasks/{id}` | 删除任务 |
| PATCH | `/api/tasks/{id}/toggle` | 切换完成状态 |

所有响应统一为：
```json
{
  "success": true,
  "message": "获取任务成功",
  "data": { ... },
  "timestamp": "2025-12-26T11:11:11Z"
}
```

## 🧪 建议测试流程
1. 启动本地 MySQL，执行 `todolist.sql`
2. `./mvnw spring-boot:run` 启动后端，使用 Postman 或 curl 触发全部 API
3. `npm run dev -- --host` 启动前端，验证：
   - 新建/编辑/删除/切换完成状态
   - 筛选（全部/进行中/已完成）
   - 排序（创建、截止、优先级）
   - 亮/暗模式切换与平滑过渡、刷新后模式保持
   - 移动端（375px）展示是否正常
4. `npm run build`、`./mvnw clean package` 以确保可编译

## ⚙️ 配置
- 默认数据库：`jdbc:mysql://localhost:3306/todolist`
- 账号：`root` / `123456`
- 可通过 `backend/src/main/resources/application.yml` 调整

## 📄 运行脚本
- `run-all.bat`：在 Windows 上打开两个独立终端分别运行后端与前端
- `run-all.sh`：在 macOS / Linux 上并行启动两个进程，`Ctrl + C` 自动清理

## 📚 附件
- `RUN_GUIDE.md`：更详细的部署与验证手册

欢迎根据团队需要继续扩展，例如接入通知、拖拽排序或多用户体系。
