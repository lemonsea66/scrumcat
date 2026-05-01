package com.scrumcat.service;

import com.scrumcat.dto.BacklogPriorityUpdateRequest;
import com.scrumcat.vo.UserStoryVO;

import java.util.List;

public interface BacklogService {

    List<UserStoryVO> listBacklog(Long projectId, String filter);

    void updatePriorities(BacklogPriorityUpdateRequest request);
}
