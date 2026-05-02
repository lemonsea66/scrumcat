package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.service.DashboardService;
import com.scrumcat.vo.DashboardSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public Result<DashboardSummaryVO> getSummary(@RequestParam Long projectId, @RequestParam Long sprintId) {
        return Result.success(dashboardService.getSummary(projectId, sprintId));
    }
}
