@echo off
setlocal ENABLEDELAYEDEXPANSION
set ROOT=%~dp0
set BACKEND=%ROOT%backend
set FRONTEND=%ROOT%frontend

echo 正在启动 Spring Boot 后端服务...
start "TodoList Backend" cmd /k "cd /d %BACKEND% && mvnw.cmd spring-boot:run"

echo 正在启动 Vue 前端项目...
start "TodoList Frontend" cmd /k "cd /d %FRONTEND% && npm run dev -- --host"

echo 已在新窗口内启动前后端，按任意键退出当前脚本。
pause >nul
endlocal
