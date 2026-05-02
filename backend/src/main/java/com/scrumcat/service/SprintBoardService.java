package com.scrumcat.service;

import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.vo.SprintBoardVO;

public interface SprintBoardService {

    SprintBoardVO getBoard(Long sprintId);

    SprintBoardVO updateStoryStatus(Long sprintId, Long storyId, StoryStatusUpdateRequest request);
}
