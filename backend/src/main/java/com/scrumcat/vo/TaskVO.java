package com.scrumcat.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVO {

    private Long id;

    private Long storyId;

    private String title;

    private String description;

    private Integer estimatedHours;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
