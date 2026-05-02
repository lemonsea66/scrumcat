package com.scrumcat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("story_status_log")
public class StoryStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sprintId;

    private Long storyId;

    private String oldStatus;

    private String newStatus;

    private Long changedBy;

    private LocalDateTime changedAt;
}
