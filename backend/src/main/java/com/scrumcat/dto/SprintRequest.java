package com.scrumcat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SprintRequest {

    @NotNull(message = "项目空间不能为空")
    private Long projectId;

    @NotBlank(message = "Sprint 名称不能为空")
    private String name;

    private String goal;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private String status;

    private String ownerNickname;

    private List<String> members;
}
