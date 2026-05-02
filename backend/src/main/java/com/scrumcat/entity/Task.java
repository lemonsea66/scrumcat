package com.scrumcat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storyId;

    private String title;

    private String description;

    private Integer estimatedHours;

    private String status;

    private Long creatorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
