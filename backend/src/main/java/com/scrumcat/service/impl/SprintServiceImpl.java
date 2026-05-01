package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.SprintRequest;
import com.scrumcat.dto.SprintStoryRequest;
import com.scrumcat.entity.Project;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.SprintStory;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.SprintStoryMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.service.SprintService;
import com.scrumcat.vo.SprintVO;
import com.scrumcat.vo.UserStoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {

    private static final List<String> ALLOWED_STATUS = Arrays.asList("PLANNED", "ACTIVE", "CLOSED");

    private final SprintMapper sprintMapper;
    private final SprintStoryMapper sprintStoryMapper;
    private final UserStoryMapper userStoryMapper;
    private final ProjectMapper projectMapper;
    private final CollaborationMemberSupport memberSupport;

    @Override
    public List<SprintVO> listSprints(Long projectId) {
        Project project = requireCurrentUserProject(projectId);
        return sprintMapper.selectList(new LambdaQueryWrapper<Sprint>()
                        .eq(Sprint::getCreatorId, project.getCreatorId())
                        .eq(Sprint::getProjectId, project.getId())
                        .orderByDesc(Sprint::getUpdatedAt))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public SprintVO createSprint(SprintRequest request) {
        Long userId = requireCurrentUserId();
        requireCurrentUserProject(request.getProjectId());
        validateDates(request);

        Sprint sprint = new Sprint();
        sprint.setProjectId(request.getProjectId());
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setStatus(normalizeStatus(request.getStatus(), "PLANNED"));
        sprint.setOwnerNickname(request.getOwnerNickname());
        sprint.setCreatorId(userId);
        sprintMapper.insert(sprint);
        memberSupport.replaceMembers(CollaborationMemberSupport.SPRINT, sprint.getId(), request.getMembers(), userId);
        return toVO(sprint);
    }

    @Override
    public SprintVO getSprint(Long id) {
        return toVO(getCurrentUserSprint(id));
    }

    @Override
    public SprintVO updateSprint(Long id, SprintRequest request) {
        Sprint sprint = getCurrentUserSprint(id);
        requireCurrentUserProject(request.getProjectId());
        validateDates(request);

        sprint.setProjectId(request.getProjectId());
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setStatus(normalizeStatus(request.getStatus(), sprint.getStatus()));
        sprint.setOwnerNickname(request.getOwnerNickname());
        sprintMapper.updateById(sprint);
        memberSupport.replaceMembers(CollaborationMemberSupport.SPRINT, sprint.getId(), request.getMembers(), sprint.getCreatorId());
        return toVO(sprintMapper.selectById(id));
    }

    @Override
    public void deleteSprint(Long id) {
        Sprint sprint = getCurrentUserSprint(id);
        sprintStoryMapper.delete(new LambdaQueryWrapper<SprintStory>()
                .eq(SprintStory::getSprintId, sprint.getId()));
        memberSupport.deleteMembers(CollaborationMemberSupport.SPRINT, sprint.getId());
        sprintMapper.deleteById(sprint.getId());
    }

    @Override
    public void addStory(Long sprintId, SprintStoryRequest request) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        UserStory story = getCurrentUserStory(request.getStoryId());
        if (!sprint.getProjectId().equals(story.getProjectId())) {
            throw new BusinessException("只能加入当前项目下的用户故事");
        }

        Long count = sprintStoryMapper.selectCount(new LambdaQueryWrapper<SprintStory>()
                .eq(SprintStory::getSprintId, sprint.getId())
                .eq(SprintStory::getStoryId, request.getStoryId()));
        if (count > 0) {
            throw new BusinessException("这个用户故事已经在当前 Sprint 中");
        }

        SprintStory sprintStory = new SprintStory();
        sprintStory.setSprintId(sprint.getId());
        sprintStory.setStoryId(request.getStoryId());
        sprintStoryMapper.insert(sprintStory);
    }

    @Override
    public List<UserStoryVO> listSprintStories(Long sprintId) {
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
                .map(this::toStoryVO)
                .toList();
    }

    @Override
    public void removeStory(Long sprintId, Long storyId) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        UserStory story = getCurrentUserStory(storyId);
        if (!sprint.getProjectId().equals(story.getProjectId())) {
            throw new BusinessException("只能移出当前项目下的用户故事");
        }

        int deleted = sprintStoryMapper.delete(new LambdaQueryWrapper<SprintStory>()
                .eq(SprintStory::getSprintId, sprint.getId())
                .eq(SprintStory::getStoryId, storyId));
        if (deleted == 0) {
            throw new BusinessException("这个用户故事不在当前 Sprint 中");
        }
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

    private Project requireCurrentUserProject(Long projectId) {
        Long userId = requireCurrentUserId();
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, projectId)
                .eq(Project::getCreatorId, userId)
                .last("LIMIT 1"));
        if (project == null) {
            throw new BusinessException("项目空间不存在");
        }
        return project;
    }

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }

    private void validateDates(SprintRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
    }

    private String normalizeStatus(String status, String defaultStatus) {
        String value = (status == null || status.isBlank()) ? defaultStatus : status;
        if (!ALLOWED_STATUS.contains(value)) {
            throw new BusinessException("Sprint 状态不合法");
        }
        return value;
    }

    private SprintVO toVO(Sprint sprint) {
        SprintVO vo = new SprintVO();
        vo.setId(sprint.getId());
        vo.setProjectId(sprint.getProjectId());
        vo.setName(sprint.getName());
        vo.setGoal(sprint.getGoal());
        vo.setStartDate(sprint.getStartDate());
        vo.setEndDate(sprint.getEndDate());
        vo.setStatus(sprint.getStatus());
        vo.setOwnerNickname(sprint.getOwnerNickname());
        vo.setMembers(memberSupport.listMembers(CollaborationMemberSupport.SPRINT, sprint.getId()));
        vo.setStoryCount(sprintStoryMapper.selectCount(new LambdaQueryWrapper<SprintStory>()
                .eq(SprintStory::getSprintId, sprint.getId())).intValue());
        vo.setCreatedAt(sprint.getCreatedAt());
        vo.setUpdatedAt(sprint.getUpdatedAt());
        return vo;
    }

    private UserStoryVO toStoryVO(UserStory story) {
        UserStoryVO vo = new UserStoryVO();
        vo.setId(story.getId());
        vo.setProjectId(story.getProjectId());
        vo.setTitle(story.getTitle());
        vo.setDescription(story.getDescription());
        vo.setStoryPoint(story.getStoryPoint());
        vo.setPriority(story.getPriority());
        vo.setStatus(story.getStatus());
        vo.setOwnerNickname(story.getOwnerNickname());
        vo.setMembers(memberSupport.listMembers(CollaborationMemberSupport.STORY, story.getId()));
        vo.setCreatedAt(story.getCreatedAt());
        vo.setUpdatedAt(story.getUpdatedAt());
        return vo;
    }
}
