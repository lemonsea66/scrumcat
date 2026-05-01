package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.SprintRequest;
import com.scrumcat.dto.SprintStoryRequest;
import com.scrumcat.service.SprintService;
import com.scrumcat.vo.SprintVO;
import com.scrumcat.vo.UserStoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @GetMapping
    public Result<List<SprintVO>> listSprints(@RequestParam Long projectId) {
        return Result.success(sprintService.listSprints(projectId));
    }

    @PostMapping
    public Result<SprintVO> createSprint(@Valid @RequestBody SprintRequest request) {
        return Result.success(sprintService.createSprint(request));
    }

    @GetMapping("/{id}")
    public Result<SprintVO> getSprint(@PathVariable Long id) {
        return Result.success(sprintService.getSprint(id));
    }

    @PutMapping("/{id}")
    public Result<SprintVO> updateSprint(@PathVariable Long id, @Valid @RequestBody SprintRequest request) {
        return Result.success(sprintService.updateSprint(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return Result.success();
    }

    @PostMapping("/{sprintId}/stories")
    public Result<Void> addStory(@PathVariable Long sprintId, @Valid @RequestBody SprintStoryRequest request) {
        sprintService.addStory(sprintId, request);
        return Result.success();
    }

    @GetMapping("/{sprintId}/stories")
    public Result<List<UserStoryVO>> listSprintStories(@PathVariable Long sprintId) {
        return Result.success(sprintService.listSprintStories(sprintId));
    }

    @DeleteMapping("/{sprintId}/stories/{storyId}")
    public Result<Void> removeStory(@PathVariable Long sprintId, @PathVariable Long storyId) {
        sprintService.removeStory(sprintId, storyId);
        return Result.success();
    }
}
