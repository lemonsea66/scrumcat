package com.scrumcat.service;

import com.scrumcat.vo.DashboardSummaryVO;

public interface DashboardService {

    DashboardSummaryVO getSummary(Long projectId, Long sprintId);
}
