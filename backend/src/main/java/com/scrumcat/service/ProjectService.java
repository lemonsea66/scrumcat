package com.scrumcat.service;

import com.scrumcat.dto.ProjectRequest;
import com.scrumcat.vo.ProjectVO;

import java.util.List;

public interface ProjectService {

    List<ProjectVO> listProjects();

    ProjectVO createProject(ProjectRequest request);

    ProjectVO updateProject(Long id, ProjectRequest request);

    void deleteProject(Long id);
}
