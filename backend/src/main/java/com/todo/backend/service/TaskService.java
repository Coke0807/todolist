package com.todo.backend.service;

import com.todo.backend.dto.TaskRequest;
import com.todo.backend.dto.TaskResponse;
import com.todo.backend.entity.Task;
import com.todo.backend.enums.PriorityLevel;
import com.todo.backend.exception.ResourceNotFoundException;
import com.todo.backend.repository.TaskRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * 获取全部任务列表，默认按照创建时间倒序，方便查看最新任务。
     */
    public List<TaskResponse> findAll() {
        return taskRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 根据主键查询单条任务，用于详情或编辑。
     */
    public TaskResponse findById(Long id) {
        return toResponse(getTaskOrThrow(id));
    }

    /**
     * 创建任务并持久化，同时清理输入数据。
     */
    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .completed(Boolean.TRUE.equals(request.getCompleted()))
                .build();
        normalizeTask(task);
        Task savedTask = Objects.requireNonNull(taskRepository.save(task));
        return toResponse(savedTask);
    }

    /**
     * 更新指定任务的全部字段，保持审计时间戳自动更新。
     */
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTaskOrThrow(id);
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        normalizeTask(task);
        Task savedTask = Objects.requireNonNull(taskRepository.save(task));
        return toResponse(savedTask);
    }

    /**
     * 切换任务完成状态，供快捷操作使用。
     */
    @Transactional
    public TaskResponse toggle(Long id) {
        Task task = getTaskOrThrow(id);
        task.setCompleted(!Boolean.TRUE.equals(task.getCompleted()));
        Task savedTask = Objects.requireNonNull(taskRepository.save(task));
        return toResponse(savedTask);
    }

    /**
     * 删除任务记录，若不存在则直接抛出 404。
     */
    @Transactional
    public void delete(Long id) {
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }

    /**
     * 统一封装查询逻辑，避免多处重复的空值判断。
     */
    @NonNull
    private Task getTaskOrThrow(Long id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的任务"));
    }

    /**
     * 将实体转换为响应对象，保持返回字段的可控性。
     */
    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * 补全默认值并清洗脏数据，防止无意的空指针。
     */
    private void normalizeTask(Task task) {
        task.setTitle(task.getTitle().trim());
        if (task.getPriority() == null) {
            task.setPriority(PriorityLevel.MEDIUM);
        }
        if (task.getCompleted() == null) {
            task.setCompleted(Boolean.FALSE);
        }
    }
}
