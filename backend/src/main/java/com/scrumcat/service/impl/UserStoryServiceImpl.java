package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.dto.UserStoryRequest;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.entity.Project;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.UserStoryService;
import com.scrumcat.vo.UserStoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStoryServiceImpl implements UserStoryService {

    private static final List<String> ALLOWED_STATUS = Arrays.asList("TODO", "IN_PROGRESS", "DONE");

    private final UserStoryMapper userStoryMapper;
    private final ProjectMapper projectMapper;
    private final CollaborationMemberSupport memberSupport;

    @Override
    public List<UserStoryVO> listCurrentUserStories(Long projectId) {
        Long userId = requireCurrentUserId();
        LambdaQueryWrapper<UserStory> wrapper = new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getCreatorId, userId)
                .orderByAsc(UserStory::getPriority)
                .orderByDesc(UserStory::getUpdatedAt);
        if (projectId != null) {
            requireCurrentUserProject(projectId);
            wrapper.eq(UserStory::getProjectId, projectId);
        }
        return userStoryMapper.selectList(wrapper)
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public UserStoryVO createStory(UserStoryRequest request) {
        Long userId = requireCurrentUserId();
        requireCurrentUserProject(request.getProjectId());
        String status = normalizeStatus(request.getStatus(), "TODO");

        UserStory story = new UserStory();
        story.setProjectId(request.getProjectId());
        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setStoryPoint(request.getStoryPoint());
        story.setPriority(request.getPriority());
        story.setStatus(status);
        story.setOwnerNickname(request.getOwnerNickname());
        story.setCreatorId(userId);
        userStoryMapper.insert(story);
        memberSupport.replaceMembers(CollaborationMemberSupport.STORY, story.getId(), request.getMembers(), userId);
        return toVO(story);
    }

    @Override
    public UserStoryVO updateStory(Long id, UserStoryRequest request) {
        UserStory story = getCurrentUserStory(id);
        requireCurrentUserProject(request.getProjectId());
        story.setTitle(request.getTitle());
        story.setProjectId(request.getProjectId());
        story.setDescription(request.getDescription());
        story.setStoryPoint(request.getStoryPoint());
        story.setPriority(request.getPriority());
        story.setStatus(normalizeStatus(request.getStatus(), story.getStatus()));
        story.setOwnerNickname(request.getOwnerNickname());
        userStoryMapper.updateById(story);
        memberSupport.replaceMembers(CollaborationMemberSupport.STORY, story.getId(), request.getMembers(), story.getCreatorId());
        return toVO(userStoryMapper.selectById(id));
    }

    @Override
    public void deleteStory(Long id) {
        UserStory story = getCurrentUserStory(id);
        memberSupport.deleteMembers(CollaborationMemberSupport.STORY, story.getId());
        userStoryMapper.deleteById(story.getId());
    }

    @Override
    public UserStoryVO updateStatus(Long id, StoryStatusUpdateRequest request) {
        UserStory story = getCurrentUserStory(id);
        story.setStatus(normalizeStatus(request.getStatus(), story.getStatus()));
        userStoryMapper.updateById(story);
        return toVO(userStoryMapper.selectById(id));
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

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
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

    private String normalizeStatus(String status, String defaultStatus) {
        String value = (status == null || status.isBlank()) ? defaultStatus : status;
        if (!ALLOWED_STATUS.contains(value)) {
            throw new BusinessException("用户故事状态不合法");
        }
        return value;
    }

    private UserStoryVO toVO(UserStory story) {
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
