package com.scrumcat.service;

import com.scrumcat.dto.StoryStatusUpdateRequest;
import com.scrumcat.dto.UserStoryRequest;
import com.scrumcat.vo.UserStoryVO;

import java.util.List;

public interface UserStoryService {

    List<UserStoryVO> listCurrentUserStories();

    UserStoryVO createStory(UserStoryRequest request);

    UserStoryVO updateStory(Long id, UserStoryRequest request);

    void deleteStory(Long id);

    UserStoryVO updateStatus(Long id, StoryStatusUpdateRequest request);
}
