package com.scrumcat.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotNull(message = "用户故事不能为空")
    private Long storyId;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "预计工时不能为空")
    @Min(value = 1, message = "预计工时不能小于 1")
    private Integer estimatedHours;

    private String status;
}
