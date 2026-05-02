package com.scrumcat.service;

import com.scrumcat.dto.TaskRequest;
import com.scrumcat.dto.TaskStatusUpdateRequest;
import com.scrumcat.vo.TaskVO;

import java.util.List;

public interface TaskService {

    List<TaskVO> listStoryTasks(Long storyId);

    TaskVO createTask(TaskRequest request);

    TaskVO updateTask(Long id, TaskRequest request);

    void deleteTask(Long id);

    TaskVO updateStatus(Long id, TaskStatusUpdateRequest request);
}
