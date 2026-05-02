package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BurndownVO {

    private Long sprintId;

    private String sprintName;

    private List<String> dates = new ArrayList<>();

    private List<BigDecimal> planned = new ArrayList<>();

    private List<BigDecimal> actual = new ArrayList<>();

    private BigDecimal totalPoints = BigDecimal.ZERO;

    private BigDecimal remainingPoints = BigDecimal.ZERO;
}
