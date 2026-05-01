package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.dto.UserStoryRequest;
import com.scrumcat.service.UserStoryService;
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
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;

    @GetMapping
    public Result<List<UserStoryVO>> listStories(@RequestParam(required = false) Long projectId) {
        return Result.success(userStoryService.listCurrentUserStories(projectId));
    }

    @PostMapping
    public Result<UserStoryVO> createStory(@Valid @RequestBody UserStoryRequest request) {
        return Result.success(userStoryService.createStory(request));
    }

    @PutMapping("/{id}")
    public Result<UserStoryVO> updateStory(@PathVariable Long id, @Valid @RequestBody UserStoryRequest request) {
        return Result.success(userStoryService.updateStory(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteStory(@PathVariable Long id) {
        userStoryService.deleteStory(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<UserStoryVO> updateStatus(@PathVariable Long id, @Valid @RequestBody StoryStatusUpdateRequest request) {
        return Result.success(userStoryService.updateStatus(id, request));
    }
}
