package com.scrumcat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BacklogPriorityUpdateRequest {

    @NotNull(message = "项目空间不能为空")
    private Long projectId;

    @Valid
    @NotEmpty(message = "优先级列表不能为空")
    private List<BacklogPriorityItemRequest> items;
}
