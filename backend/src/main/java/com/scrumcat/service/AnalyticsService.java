package com.scrumcat.service;

import com.scrumcat.vo.AnalyticsSummaryVO;

public interface AnalyticsService {

    AnalyticsSummaryVO getSummary(Long projectId, Long sprintId);
}
