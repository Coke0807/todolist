#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

echo "启动 Spring Boot 后端..."
(
  cd "$BACKEND_DIR"
  ./mvnw spring-boot:run
) &
BACK_PID=$!

echo "启动 Vue 前端..."
(
  cd "$FRONTEND_DIR"
  npm run dev -- --host
) &
FRONT_PID=$!

cleanup() {
  echo "\n正在关闭服务..."
  kill "$BACK_PID" "$FRONT_PID" 2>/dev/null || true
}

trap cleanup INT TERM

wait "$BACK_PID"
wait "$FRONT_PID"
