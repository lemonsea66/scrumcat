package com.scrumcat.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SprintBoardVO {

    private List<BoardStoryVO> todo = new ArrayList<>();

    private List<BoardStoryVO> inProgress = new ArrayList<>();

    private List<BoardStoryVO> done = new ArrayList<>();
}
