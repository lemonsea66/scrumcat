package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.ProjectRequest;
import com.scrumcat.service.ProjectService;
import com.scrumcat.vo.ProjectVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public Result<List<ProjectVO>> listProjects() {
        return Result.success(projectService.listProjects());
    }

    @PostMapping
    public Result<ProjectVO> createProject(@Valid @RequestBody ProjectRequest request) {
        return Result.success(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    public Result<ProjectVO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return Result.success(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}
