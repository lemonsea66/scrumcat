package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.service.AnalyticsService;
import com.scrumcat.vo.AnalyticsSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public Result<AnalyticsSummaryVO> getSummary(@RequestParam Long projectId, @RequestParam Long sprintId) {
        return Result.success(analyticsService.getSummary(projectId, sprintId));
    }
}
