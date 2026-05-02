package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.dto.TaskRequest;
import com.scrumcat.dto.TaskStatusUpdateRequest;
import com.scrumcat.service.TaskService;
import com.scrumcat.vo.TaskVO;
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
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/api/stories/{storyId}/tasks")
    public Result<List<TaskVO>> listStoryTasks(@PathVariable Long storyId) {
        return Result.success(taskService.listStoryTasks(storyId));
    }

    @PostMapping("/api/tasks")
    public Result<TaskVO> createTask(@Valid @RequestBody TaskRequest request) {
        return Result.success(taskService.createTask(request));
    }

    @PutMapping("/api/tasks/{id}")
    public Result<TaskVO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return Result.success(taskService.updateTask(id, request));
    }

    @DeleteMapping("/api/tasks/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return Result.success();
    }

    @PutMapping("/api/tasks/{id}/status")
    public Result<TaskVO> updateStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateRequest request) {
        return Result.success(taskService.updateStatus(id, request));
    }
}
