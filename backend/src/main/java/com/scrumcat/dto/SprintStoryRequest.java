package com.scrumcat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SprintStoryRequest {

    @NotNull(message = "用户故事不能为空")
    private Long storyId;
}
