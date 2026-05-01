package com.scrumcat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BacklogPriorityItemRequest {

    @NotNull(message = "用户故事不能为空")
    private Long storyId;

    @NotNull(message = "优先级不能为空")
    private Integer priority;
}
