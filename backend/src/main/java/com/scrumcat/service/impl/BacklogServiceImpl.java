package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.BacklogPriorityItemRequest;
import com.scrumcat.dto.BacklogPriorityUpdateRequest;
import com.scrumcat.entity.Project;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.BacklogService;
import com.scrumcat.vo.UserStoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BacklogServiceImpl implements BacklogService {

    private final UserStoryMapper userStoryMapper;
    private final ProjectMapper projectMapper;
    private final CollaborationMemberSupport memberSupport;

    @Override
    public List<UserStoryVO> listBacklog(Long projectId, String filter) {
        Project project = requireCurrentUserProject(projectId);
        LambdaQueryWrapper<UserStory> wrapper = new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getCreatorId, project.getCreatorId())
                .eq(UserStory::getProjectId, project.getId())
                .orderByAsc(UserStory::getPriority)
                .orderByDesc(UserStory::getUpdatedAt);

        if ("todo".equalsIgnoreCase(filter)) {
            wrapper.eq(UserStory::getStatus, "TODO");
        } else if (filter != null && !filter.isBlank() && !"all".equalsIgnoreCase(filter)) {
            throw new BusinessException("产品待办筛选条件不合法");
        }

        return userStoryMapper.selectList(wrapper)
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public void updatePriorities(BacklogPriorityUpdateRequest request) {
        Project project = requireCurrentUserProject(request.getProjectId());
        List<Long> storyIds = request.getItems()
                .stream()
                .map(BacklogPriorityItemRequest::getStoryId)
                .distinct()
                .toList();

        Map<Long, UserStory> storyMap = userStoryMapper.selectList(new LambdaQueryWrapper<UserStory>()
                        .eq(UserStory::getCreatorId, project.getCreatorId())
                        .eq(UserStory::getProjectId, project.getId())
                        .in(UserStory::getId, storyIds))
                .stream()
                .collect(Collectors.toMap(UserStory::getId, story -> story));

        if (storyMap.size() != storyIds.size()) {
            throw new BusinessException("只能调整自己的用户故事优先级");
        }

        for (BacklogPriorityItemRequest item : request.getItems()) {
            UserStory story = storyMap.get(item.getStoryId());
            story.setPriority(item.getPriority());
            userStoryMapper.updateById(story);
        }
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
