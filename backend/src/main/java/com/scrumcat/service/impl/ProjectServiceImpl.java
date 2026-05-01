package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.dto.ProjectRequest;
import com.scrumcat.entity.Project;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.ProjectMapper;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.ProjectService;
import com.scrumcat.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final UserStoryMapper userStoryMapper;
    private final SprintMapper sprintMapper;
    private final CollaborationMemberSupport memberSupport;

    @Override
    public List<ProjectVO> listProjects() {
        Long userId = requireCurrentUserId();
        return projectMapper.selectList(new LambdaQueryWrapper<Project>()
                        .eq(Project::getCreatorId, userId)
                        .orderByDesc(Project::getUpdatedAt))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public ProjectVO createProject(ProjectRequest request) {
        Long userId = requireCurrentUserId();
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerNickname(request.getOwnerNickname());
        project.setCreatorId(userId);
        projectMapper.insert(project);
        memberSupport.replaceMembers(CollaborationMemberSupport.PROJECT, project.getId(), request.getMembers(), userId);
        return toVO(projectMapper.selectById(project.getId()));
    }

    @Override
    public ProjectVO updateProject(Long id, ProjectRequest request) {
        Project project = getCurrentUserProject(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerNickname(request.getOwnerNickname());
        projectMapper.updateById(project);
        memberSupport.replaceMembers(CollaborationMemberSupport.PROJECT, project.getId(), request.getMembers(), project.getCreatorId());
        return toVO(projectMapper.selectById(project.getId()));
    }

    @Override
    public void deleteProject(Long id) {
        Project project = getCurrentUserProject(id);
        Long storyCount = userStoryMapper.selectCount(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getProjectId, project.getId())
                .eq(UserStory::getCreatorId, project.getCreatorId()));
        Long sprintCount = sprintMapper.selectCount(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getProjectId, project.getId())
                .eq(Sprint::getCreatorId, project.getCreatorId()));
        if (storyCount > 0 || sprintCount > 0) {
            throw new BusinessException("项目下已有用户故事或 Sprint，请先清理关联数据再删除");
        }
        memberSupport.deleteMembers(CollaborationMemberSupport.PROJECT, project.getId());
        projectMapper.deleteById(project.getId());
    }

    private Project getCurrentUserProject(Long id) {
        Long userId = requireCurrentUserId();
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, id)
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

    private ProjectVO toVO(Project project) {
        ProjectVO vo = new ProjectVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setOwnerNickname(project.getOwnerNickname());
        vo.setMembers(memberSupport.listMembers(CollaborationMemberSupport.PROJECT, project.getId()));
        vo.setCreatedAt(project.getCreatedAt());
        vo.setUpdatedAt(project.getUpdatedAt());
        return vo;
    }
}
