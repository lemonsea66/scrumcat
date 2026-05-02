package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.TaskRequest;
import com.scrumcat.dto.TaskStatusUpdateRequest;
import com.scrumcat.entity.Task;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.TaskMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.TaskService;
import com.scrumcat.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final List<String> ALLOWED_STATUS = Arrays.asList("TODO", "IN_PROGRESS", "DONE");

    private final TaskMapper taskMapper;
    private final UserStoryMapper userStoryMapper;

    @Override
    public List<TaskVO> listStoryTasks(Long storyId) {
        requireCurrentUserStory(storyId);
        return taskMapper.selectList(new LambdaQueryWrapper<Task>()
                        .eq(Task::getStoryId, storyId)
                        .eq(Task::getCreatorId, requireCurrentUserId())
                        .orderByAsc(Task::getStatus)
                        .orderByAsc(Task::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public TaskVO createTask(TaskRequest request) {
        requireCurrentUserStory(request.getStoryId());
        Long userId = requireCurrentUserId();

        Task task = new Task();
        task.setStoryId(request.getStoryId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setStatus(normalizeStatus(request.getStatus(), "TODO"));
        task.setCreatorId(userId);
        taskMapper.insert(task);
        return toVO(task);
    }

    @Override
    public TaskVO updateTask(Long id, TaskRequest request) {
        Task task = getCurrentUserTask(id);
        requireCurrentUserStory(request.getStoryId());
        task.setStoryId(request.getStoryId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setStatus(normalizeStatus(request.getStatus(), task.getStatus()));
        taskMapper.updateById(task);
        return toVO(taskMapper.selectById(id));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = getCurrentUserTask(id);
        taskMapper.deleteById(task.getId());
    }

    @Override
    public TaskVO updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = getCurrentUserTask(id);
        task.setStatus(normalizeStatus(request.getStatus(), task.getStatus()));
        taskMapper.updateById(task);
        return toVO(taskMapper.selectById(id));
    }

    private Task getCurrentUserTask(Long id) {
        Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getId, id)
                .eq(Task::getCreatorId, requireCurrentUserId())
                .last("LIMIT 1"));
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        requireCurrentUserStory(task.getStoryId());
        return task;
    }

    private UserStory requireCurrentUserStory(Long storyId) {
        UserStory story = userStoryMapper.selectOne(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getId, storyId)
                .eq(UserStory::getCreatorId, requireCurrentUserId())
                .last("LIMIT 1"));
        if (story == null) {
            throw new BusinessException("用户故事不存在");
        }
        return story;
    }

    private String normalizeStatus(String status, String defaultStatus) {
        String value = (status == null || status.isBlank()) ? defaultStatus : status;
        if (!ALLOWED_STATUS.contains(value)) {
            throw new BusinessException("任务状态不合法");
        }
        return value;
    }

    private TaskVO toVO(Task task) {
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

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
