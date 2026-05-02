package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.entity.Project;
import com.scrumcat.entity.Sprint;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.service.AnalyticsService;
import com.scrumcat.service.BurndownService;
import com.scrumcat.service.DashboardService;
import com.scrumcat.vo.AnalyticsSummaryVO;
import com.scrumcat.vo.BurndownVO;
import com.scrumcat.vo.DashboardSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectMapper projectMapper;
    private final SprintMapper sprintMapper;
    private final AnalyticsService analyticsService;
    private final BurndownService burndownService;

    @Override
    public DashboardSummaryVO getSummary(Long projectId, Long sprintId) {
        Project project = requireCurrentUserProject(projectId);
        Sprint sprint = requireProjectSprint(project.getId(), sprintId);
        AnalyticsSummaryVO analytics = analyticsService.getSummary(project.getId(), sprint.getId());
        BurndownVO burndown = burndownService.getBurndown(sprint.getId());

        BigDecimal totalPoints = burndown.getTotalPoints();
        BigDecimal donePoints = analytics.getStoryPoints().getDone();
        int completionRate = calculateCompletionRate(donePoints, totalPoints);

        DashboardSummaryVO summary = new DashboardSummaryVO();
        summary.setProjectName(project.getName());
        summary.setSprintName(sprint.getName());
        summary.setCompletionRate(completionRate);
        summary.setRemainingPoints(burndown.getRemainingPoints());
        summary.setInProgressTasks(analytics.getTaskStatus().getInProgress());
        summary.setDoneTasks(analytics.getTaskStatus().getDone());
        summary.setOpenIssues(0);
        applyCatStatus(summary, completionRate);
        return summary;
    }

    private int calculateCompletionRate(BigDecimal donePoints, BigDecimal totalPoints) {
        if (totalPoints == null || totalPoints.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return donePoints
                .multiply(BigDecimal.valueOf(100))
                .divide(totalPoints, 0, RoundingMode.HALF_UP)
                .min(BigDecimal.valueOf(100))
                .intValue();
    }

    private void applyCatStatus(DashboardSummaryVO summary, int completionRate) {
        if (completionRate >= 100) {
            summary.setCatStatusLevel("已完成");
            summary.setCatStatus("本轮 Sprint 已完成，猫猫获得了一份小鱼干奖励。");
        } else if (completionRate >= 80) {
            summary.setCatStatusLevel("接近完成");
            summary.setCatStatus("猫猫已经看到小鱼干了，最后几个任务继续收尾。");
        } else if (completionRate >= 40) {
            summary.setCatStatusLevel("稳步推进");
            summary.setCatStatus("猫猫正在陪你一起推进 Sprint，当前节奏比较稳定。");
        } else {
            summary.setCatStatusLevel("需要关注");
            summary.setCatStatus("猫猫轻轻拍了拍看板，提醒你关注阻塞项。");
        }
    }

    private Project requireCurrentUserProject(Long projectId) {
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, projectId)
                .eq(Project::getCreatorId, requireCurrentUserId())
                .last("LIMIT 1"));
        if (project == null) {
            throw new BusinessException("项目空间不存在");
        }
        return project;
    }

    private Sprint requireProjectSprint(Long projectId, Long sprintId) {
        Sprint sprint = sprintMapper.selectOne(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getId, sprintId)
                .eq(Sprint::getProjectId, projectId)
                .eq(Sprint::getCreatorId, requireCurrentUserId())
                .last("LIMIT 1"));
        if (sprint == null) {
            throw new BusinessException("Sprint 不存在");
        }
        return sprint;
    }

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
