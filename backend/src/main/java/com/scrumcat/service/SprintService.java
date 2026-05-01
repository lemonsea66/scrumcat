package com.scrumcat.service;

import com.scrumcat.dto.SprintRequest;
import com.scrumcat.dto.SprintStoryRequest;
import com.scrumcat.vo.SprintVO;
import com.scrumcat.vo.UserStoryVO;

import java.util.List;

public interface SprintService {

    List<SprintVO> listSprints(Long projectId);

    SprintVO createSprint(SprintRequest request);

    SprintVO getSprint(Long id);

    SprintVO updateSprint(Long id, SprintRequest request);

    void deleteSprint(Long id);

    void addStory(Long sprintId, SprintStoryRequest request);

    List<UserStoryVO> listSprintStories(Long sprintId);

    void removeStory(Long sprintId, Long storyId);
}
