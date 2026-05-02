package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskBoardRowVO {

    private Long storyId;

    private String storyTitle;

    private BigDecimal storyPoint;

    private Integer priority;

    private String ownerNickname;

    private List<TaskVO> todo = new ArrayList<>();

    private List<TaskVO> inProgress = new ArrayList<>();

    private List<TaskVO> done = new ArrayList<>();
}
