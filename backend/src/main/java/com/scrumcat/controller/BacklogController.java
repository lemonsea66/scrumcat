package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.BacklogPriorityUpdateRequest;
import com.scrumcat.service.BacklogService;
import com.scrumcat.vo.UserStoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backlog")
@RequiredArgsConstructor
public class BacklogController {

    private final BacklogService backlogService;

    @GetMapping
    public Result<List<UserStoryVO>> listBacklog(@RequestParam Long projectId, @RequestParam(defaultValue = "all") String filter) {
        return Result.success(backlogService.listBacklog(projectId, filter));
    }

    @PutMapping("/priorities")
    public Result<Void> updatePriorities(@Valid @RequestBody BacklogPriorityUpdateRequest request) {
        backlogService.updatePriorities(request);
        return Result.success();
    }
}
