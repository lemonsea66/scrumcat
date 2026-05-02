package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardStoryVO {

    private Long id;

    private Long projectId;

    private String title;

    private String description;

    private BigDecimal storyPoint;

    private Integer priority;

    private String status;

    private String ownerNickname;

    private List<String> members;

    private Integer taskTotalCount;

    private Integer taskDoneCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
