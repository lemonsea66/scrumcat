package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardSummaryVO {

    private String projectName;

    private String sprintName;

    private Integer completionRate = 0;

    private BigDecimal remainingPoints = BigDecimal.ZERO;

    private Integer inProgressTasks = 0;

    private Integer doneTasks = 0;

    private Integer openIssues = 0;

    private String catStatusLevel;

    private String catStatus;
}
