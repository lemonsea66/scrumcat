package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.entity.Project;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.SprintStory;
import com.scrumcat.entity.Task;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.SprintStoryMapper;
import com.scrumcat.mapper.TaskMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.AnalyticsService;
import com.scrumcat.vo.AnalyticsSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ProjectMapper projectMapper;
    private final SprintMapper sprintMapper;
    private final SprintStoryMapper sprintStoryMapper;
    private final UserStoryMapper userStoryMapper;
    private final TaskMapper taskMapper;

    @Override
    public AnalyticsSummaryVO getSummary(Long projectId, Long sprintId) {
        Sprint sprint = requireCurrentUserSprint(projectId, sprintId);
        List<UserStory> stories = listSprintStories(sprint);
        AnalyticsSummaryVO summary = new AnalyticsSummaryVO();
        stories.forEach(story -> addStory(summary, story));

        List<Long> storyIds = stories.stream().map(UserStory::getId).toList();
        if (!storyIds.isEmpty()) {
            taskMapper.selectList(new LambdaQueryWrapper<Task>()
                            .eq(Task::getCreatorId, requireCurrentUserId())
                            .in(Task::getStoryId, storyIds))
                    .forEach(task -> addTask(summary, task));
        }
        return summary;
    }

    private void addStory(AnalyticsSummaryVO summary, UserStory story) {
        BigDecimal point = formatPoint(story.getStoryPoint());
        switch (story.getStatus()) {
            case "IN_PROGRESS" -> {
                summary.getStoryStatus().setInProgress(summary.getStoryStatus().getInProgress() + 1);
                summary.getStoryPoints().setInProgress(formatPoint(summary.getStoryPoints().getInProgress().add(point)));
            }
            case "DONE" -> {
                summary.getStoryStatus().setDone(summary.getStoryStatus().getDone() + 1);
                summary.getStoryPoints().setDone(formatPoint(summary.getStoryPoints().getDone().add(point)));
            }
            default -> {
                summary.getStoryStatus().setTodo(summary.getStoryStatus().getTodo() + 1);
                summary.getStoryPoints().setTodo(formatPoint(summary.getStoryPoints().getTodo().add(point)));
            }
        }
    }

    private void addTask(AnalyticsSummaryVO summary, Task task) {
        switch (task.getStatus()) {
            case "IN_PROGRESS" -> summary.getTaskStatus().setInProgress(summary.getTaskStatus().getInProgress() + 1);
            case "DONE" -> summary.getTaskStatus().setDone(summary.getTaskStatus().getDone() + 1);
            default -> summary.getTaskStatus().setTodo(summary.getTaskStatus().getTodo() + 1);
        }
    }

    private Sprint requireCurrentUserSprint(Long projectId, Long sprintId) {
        Long userId = requireCurrentUserId();
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, projectId)
                .eq(Project::getCreatorId, userId)
                .last("LIMIT 1"));
        if (project == null) {
            throw new BusinessException("项目空间不存在");
        }

        Sprint sprint = sprintMapper.selectOne(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getId, sprintId)
                .eq(Sprint::getProjectId, project.getId())
                .eq(Sprint::getCreatorId, userId)
                .last("LIMIT 1"));
        if (sprint == null) {
            throw new BusinessException("Sprint 不存在");
        }
        return sprint;
    }

    private List<UserStory> listSprintStories(Sprint sprint) {
        List<Long> storyIds = sprintStoryMapper.selectList(new LambdaQueryWrapper<SprintStory>()
                        .eq(SprintStory::getSprintId, sprint.getId()))
                .stream()
                .map(SprintStory::getStoryId)
                .toList();
        if (storyIds.isEmpty()) {
            return List.of();
        }
        return userStoryMapper.selectList(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getCreatorId, sprint.getCreatorId())
                .eq(UserStory::getProjectId, sprint.getProjectId())
                .in(UserStory::getId, storyIds));
    }

    private BigDecimal formatPoint(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(1, RoundingMode.HALF_UP);
    }

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
