package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.SprintStory;
import com.scrumcat.entity.Task;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.SprintStoryMapper;
import com.scrumcat.mapper.TaskMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.TaskBoardService;
import com.scrumcat.vo.TaskBoardRowVO;
import com.scrumcat.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskBoardServiceImpl implements TaskBoardService {

    private final SprintMapper sprintMapper;
    private final SprintStoryMapper sprintStoryMapper;
    private final UserStoryMapper userStoryMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<TaskBoardRowVO> getBoard(Long sprintId) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        List<Long> storyIds = sprintStoryMapper.selectList(new LambdaQueryWrapper<SprintStory>()
                        .eq(SprintStory::getSprintId, sprint.getId())
                        .orderByAsc(SprintStory::getCreatedAt))
                .stream()
                .map(SprintStory::getStoryId)
                .toList();
        if (storyIds.isEmpty()) {
            return List.of();
        }

        Long userId = requireCurrentUserId();
        return userStoryMapper.selectList(new LambdaQueryWrapper<UserStory>()
                        .eq(UserStory::getCreatorId, userId)
                        .eq(UserStory::getProjectId, sprint.getProjectId())
                        .in(UserStory::getId, storyIds)
                        .orderByAsc(UserStory::getPriority)
                        .orderByDesc(UserStory::getUpdatedAt))
                .stream()
                .map(this::toBoardRow)
                .toList();
    }

    private TaskBoardRowVO toBoardRow(UserStory story) {
        TaskBoardRowVO row = new TaskBoardRowVO();
        row.setStoryId(story.getId());
        row.setStoryTitle(story.getTitle());
        row.setStoryPoint(story.getStoryPoint());
        row.setPriority(story.getPriority());
        row.setOwnerNickname(story.getOwnerNickname());

        taskMapper.selectList(new LambdaQueryWrapper<Task>()
                        .eq(Task::getStoryId, story.getId())
                        .eq(Task::getCreatorId, requireCurrentUserId())
                        .orderByAsc(Task::getId))
                .stream()
                .map(this::toTaskVO)
                .forEach(task -> addTaskToRow(row, task));
        return row;
    }

    private void addTaskToRow(TaskBoardRowVO row, TaskVO task) {
        switch (task.getStatus()) {
            case "IN_PROGRESS" -> row.getInProgress().add(task);
            case "DONE" -> row.getDone().add(task);
            default -> row.getTodo().add(task);
        }
    }

    private TaskVO toTaskVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setStoryId(task.getStoryId());
        vo.setTitle(task.getTitle());
        vo.setDescription(task.getDescription());
        vo.setEstimatedHours(task.getEstimatedHours());
        vo.setStatus(task.getStatus());
        vo.setCreatedAt(task.getCreatedAt());
        vo.setUpdatedAt(task.getUpdatedAt());
        return vo;
    }

    private Sprint getCurrentUserSprint(Long id) {
        Sprint sprint = sprintMapper.selectOne(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getId, id)
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
