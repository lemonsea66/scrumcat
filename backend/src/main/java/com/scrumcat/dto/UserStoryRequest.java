package com.scrumcat.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserStoryRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "故事点不能为空")
    @DecimalMin(value = "0.5", message = "故事点不能小于 0.5")
    private BigDecimal storyPoint;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    private String status;
}
