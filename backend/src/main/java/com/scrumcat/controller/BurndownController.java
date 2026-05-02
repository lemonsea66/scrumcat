package com.scrumcat.controller;

import com.scrumcat.common.Result;
import com.scrumcat.service.BurndownService;
import com.scrumcat.vo.BurndownVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/burndown")
@RequiredArgsConstructor
public class BurndownController {

    private final BurndownService burndownService;

    @GetMapping("/{sprintId}")
    public Result<BurndownVO> getBurndown(@PathVariable Long sprintId) {
        return Result.success(burndownService.getBurndown(sprintId));
    }
}
