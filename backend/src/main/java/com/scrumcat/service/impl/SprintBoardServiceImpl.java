package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.SprintStory;
import com.scrumcat.entity.StoryStatusLog;
import com.scrumcat.entity.Task;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.SprintStoryMapper;
import com.scrumcat.mapper.StoryStatusLogMapper;
import com.scrumcat.mapper.TaskMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.SprintBoardService;
import com.scrumcat.vo.BoardStoryVO;
import com.scrumcat.vo.SprintBoardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintBoardServiceImpl implements SprintBoardService {

    private static final List<String> ALLOWED_STATUS = Arrays.asList("TODO", "IN_PROGRESS", "DONE");

    private final SprintMapper sprintMapper;
    private final SprintStoryMapper sprintStoryMapper;
    private final UserStoryMapper userStoryMapper;
    private final TaskMapper taskMapper;
    private final StoryStatusLogMapper storyStatusLogMapper;
    private final CollaborationMemberSupport memberSupport;

    @Override
    public SprintBoardVO getBoard(Long sprintId) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        List<Long> storyIds = listSprintStoryIds(sprint.getId());
        if (storyIds.isEmpty()) {
            return new SprintBoardVO();
        }

        Long userId = requireCurrentUserId();
        List<UserStory> stories = userStoryMapper.selectList(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getCreatorId, userId)
                .eq(UserStory::getProjectId, sprint.getProjectId())
                .in(UserStory::getId, storyIds)
                .orderByAsc(UserStory::getPriority)
                .orderByDesc(UserStory::getUpdatedAt));

        SprintBoardVO board = new SprintBoardVO();
        stories.forEach(story -> addStoryToBoard(board, story));
        return board;
    }

    @Override
    @Transactional
    public SprintBoardVO updateStoryStatus(Long sprintId, Long storyId, StoryStatusUpdateRequest request) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        UserStory story = getCurrentUserStory(storyId);
        if (!sprint.getProjectId().equals(story.getProjectId())) {
            throw new BusinessException("只能更新当前 Sprint 所属项目下的用户故事");
        }
        requireStoryInSprint(sprint.getId(), story.getId());

        String oldStatus = story.getStatus();
        String newStatus = normalizeStatus(request.getStatus());
        if (!newStatus.equals(oldStatus)) {
            story.setStatus(newStatus);
            userStoryMapper.updateById(story);
            StoryStatusLog log = new StoryStatusLog();
            log.setSprintId(sprint.getId());
            log.setStoryId(story.getId());
            log.setOldStatus(oldStatus);
            log.setNewStatus(newStatus);
            log.setChangedBy(requireCurrentUserId());
            storyStatusLogMapper.insert(log);
        }
        return getBoard(sprint.getId());
    }

    private void addStoryToBoard(SprintBoardVO board, UserStory story) {
        BoardStoryVO vo = toBoardStoryVO(story);
        switch (story.getStatus()) {
            case "IN_PROGRESS" -> board.getInProgress().add(vo);
            case "DONE" -> board.getDone().add(vo);
            default -> board.getTodo().add(vo);
        }
    }

    private BoardStoryVO toBoardStoryVO(UserStory story) {
        BoardStoryVO vo = new BoardStoryVO();
        vo.setId(story.getId());
        vo.setProjectId(story.getProjectId());
        vo.setTitle(story.getTitle());
        vo.setDescription(story.getDescription());
        vo.setStoryPoint(story.getStoryPoint());
        vo.setPriority(story.getPriority());
        vo.setStatus(story.getStatus());
        vo.setOwnerNickname(story.getOwnerNickname());
        vo.setMembers(memberSupport.listMembers(CollaborationMemberSupport.STORY, story.getId()));
        vo.setTaskTotalCount(taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStoryId, story.getId())).intValue());
        vo.setTaskDoneCount(taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStoryId, story.getId())
                .eq(Task::getStatus, "DONE")).intValue());
        vo.setCreatedAt(story.getCreatedAt());
        vo.setUpdatedAt(story.getUpdatedAt());
        return vo;
    }

    private List<Long> listSprintStoryIds(Long sprintId) {
        return sprintStoryMapper.selectList(new LambdaQueryWrapper<SprintStory>()
                        .eq(SprintStory::getSprintId, sprintId)
                        .orderByAsc(SprintStory::getCreatedAt))
                .stream()
                .map(SprintStory::getStoryId)
                .toList();
    }

    private Sprint getCurrentUserSprint(Long id) {
        Long userId = requireCurrentUserId();
        Sprint sprint = sprintMapper.selectOne(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getId, id)
                .eq(Sprint::getCreatorId, userId)
                .last("LIMIT 1"));
        if (sprint == null) {
            throw new BusinessException("Sprint 不存在");
        }
        return sprint;
    }

    private UserStory getCurrentUserStory(Long id) {
        Long userId = requireCurrentUserId();
        UserStory story = userStoryMapper.selectOne(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getId, id)
                .eq(UserStory::getCreatorId, userId)
                .last("LIMIT 1"));
        if (story == null) {
            throw new BusinessException("用户故事不存在");
        }
        return story;
    }

    private void requireStoryInSprint(Long sprintId, Long storyId) {
        Long count = sprintStoryMapper.selectCount(new LambdaQueryWrapper<SprintStory>()
                .eq(SprintStory::getSprintId, sprintId)
                .eq(SprintStory::getStoryId, storyId));
        if (count == 0) {
            throw new BusinessException("这个用户故事不在当前 Sprint 中");
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank() || !ALLOWED_STATUS.contains(status)) {
            throw new BusinessException("用户故事状态不合法");
        }
        return status;
    }

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
