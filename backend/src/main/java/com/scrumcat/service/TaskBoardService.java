package com.scrumcat.service;

import com.scrumcat.vo.TaskBoardRowVO;

import java.util.List;

public interface TaskBoardService {

    List<TaskBoardRowVO> getBoard(Long sprintId);
}
