package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.service.TaskBoardService;
import com.scrumcat.vo.TaskBoardRowVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task-board")
@RequiredArgsConstructor
public class TaskBoardController {

    private final TaskBoardService taskBoardService;

    @GetMapping("/{sprintId}")
    public Result<List<TaskBoardRowVO>> getBoard(@PathVariable Long sprintId) {
        return Result.success(taskBoardService.getBoard(sprintId));
    }
}
