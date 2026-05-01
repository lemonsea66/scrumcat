package com.scrumcat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sprint_story")
public class SprintStory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sprintId;

    private Long storyId;

    private LocalDateTime createdAt;
}
