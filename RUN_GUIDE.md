# 项目运行使用手册

本手册用于指导如何在本地环境中完成 Todo List 应用的安装、运行、验证及常见问题排查。

## 1. 环境准备
- 操作系统：Windows 10+/macOS/Linux
- JDK：17（已配置 JAVA_HOME）
- Node.js：18+（附带 npm）
- MySQL：8.0（确保拥有 root 账户）

可选工具：Postman、TablePlus、VS Code。

## 2. 初始化数据库
1. 启动 MySQL 服务（若库不存在会在应用启动时自动创建 `todolist` 库）：
   ```bash
   net start mysql80   # Windows 服务名示例
   # 或
   brew services start mysql
   ```
2. 导入脚本：
   ```bash
   mysql -uroot -p123456 < todolist.sql
   ```
3. 验证：
   ```sql
   USE todolist;
   SELECT COUNT(*) FROM tasks;
   ```
   若返回 5 条示例数据即表示成功。

## 3. 启动后端 (Spring Boot)
```bash
cd backend
set SERVER_PORT=8081      # 默认端口为 8081，如需其他端口可调整此变量
./mvnw spring-boot:run   # Windows 可执行 mvnw.cmd
```
- 监听端口：默认 `8081`，可通过 `SERVER_PORT` 调整
- Log 关键字：`Started BackendApplication`
- 若需修改数据库信息，请编辑 `backend/src/main/resources/application.yml`

## 4. 启动前端 (Vite)
```bash
cd frontend
npm install        # 首次运行需要
npm run dev -- --host
```
- 访问地址：`http://localhost:5173`
- Dev Server 会自动代理 `/api` 请求到 `8080`

> Windows/macOS 用户可以直接运行根目录的 `run-all.bat`/`run-all.sh` 一键开启。

## 5. 功能验证清单
1. **基础 CRUD**
   - 新建任务，填写标题/描述/优先级/截止日期
   - 点击任务卡片中的“编辑”“删除”“标记完成”
2. **筛选与排序**
   - 工具栏切换「全部/进行中/已完成」
   - 排序切换为「截止时间最早」或「优先级」
3. **暗/亮模式**
   - 右上角按钮切换主题
   - 刷新浏览器，确认模式偏好已保存在 `localStorage`
4. **响应式**
   - 浏览器切换至移动尺寸（<=768px）检查布局
5. **API 检查**（可使用 Postman）
   - GET/POST/PUT/DELETE/PATCH `/api/tasks`
   - 响应体均包含 `success/message/data/timestamp`

## 6. 生产构建
- 后端：`./mvnw clean package`
- 前端：`npm run build`（产物位于 `frontend/dist`）
- 可将 `dist` 目录部署到 Nginx 或静态服务，并通过 Nginx 反向代理指向后端。

## 7. 常见问题排查
| 场景 | 解决方案 |
| --- | --- |
| 无法连接数据库 | 检查 `application.yml` 中的主机、端口、账号、密码；确认 MySQL 服务已启动 |
| 前端报 CORS 错误 | 确认证书 `app.cors.allowed-origins` 含当前前端地址，或使用 Vite 代理 |
| 端口占用 | 修改 `application.yml` 中 `server.port` 或 `vite.config.js` 中 `server.port` |
| npm/vite 无法启动 | 升级 Node.js 至 18+，或删除 `node_modules` 后重新 `npm install` |

## 8. 后续扩展建议
- 接入用户体系 & 鉴权
- 增加拖拽排序、子任务、提醒推送
- 编写 Cypress/E2E 自动化测试

如在运行中遇到任何问题，可结合日志搜索关键字定位，也可联系开发者继续支持。
