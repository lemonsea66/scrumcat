package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.service.SprintBoardService;
import com.scrumcat.vo.SprintBoardVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sprint-board")
@RequiredArgsConstructor
public class SprintBoardController {

    private final SprintBoardService sprintBoardService;

    @GetMapping("/{sprintId}")
    public Result<SprintBoardVO> getBoard(@PathVariable Long sprintId) {
        return Result.success(sprintBoardService.getBoard(sprintId));
    }

    @PutMapping("/{sprintId}/stories/{storyId}/status")
    public Result<SprintBoardVO> updateStoryStatus(
            @PathVariable Long sprintId,
            @PathVariable Long storyId,
            @Valid @RequestBody StoryStatusUpdateRequest request) {
        return Result.success(sprintBoardService.updateStoryStatus(sprintId, storyId, request));
    }
}
