package com.todo.backend.controller;

import com.todo.backend.dto.ApiResponse;
import com.todo.backend.dto.TaskRequest;
import com.todo.backend.dto.TaskResponse;
import com.todo.backend.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取任务列表，前端可基于此做筛选与排序。
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks() {
        return ResponseEntity.ok(ApiResponse.ok("获取任务成功", taskService.findAll()));
    }

    /**
     * 查询单个任务详情，便于编辑回显。
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("获取任务成功", taskService.findById(id)));
    }

    /**
     * 创建任务，校验请求体后返回持久化结果。
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("创建任务成功", response));
    }

    /**
     * 更新任务的所有字段。
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("更新任务成功", taskService.update(id, request)));
    }

    /**
     * 快捷切换任务完成状态。
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TaskResponse>> toggleTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("切换任务状态成功", taskService.toggle(id)));
    }

    /**
     * 删除指定任务。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("删除任务成功", null));
    }
}
