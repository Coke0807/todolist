package com.todo.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.todo.backend.enums.PriorityLevel;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 120, message = "标题长度需在120个字符以内")
    private String title;

    @Size(max = 1000, message = "描述长度需在1000个字符以内")
    private String description;

    @NotNull(message = "请为任务指定优先级")
    private PriorityLevel priority;

    @FutureOrPresent(message = "截止日期不能早于今天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private Boolean completed;
}
