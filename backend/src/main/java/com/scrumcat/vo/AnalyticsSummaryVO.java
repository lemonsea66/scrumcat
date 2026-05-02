package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalyticsSummaryVO {

    private StatusCount storyStatus = new StatusCount();

    private StatusPoints storyPoints = new StatusPoints();

    private StatusCount taskStatus = new StatusCount();

    @Data
    public static class StatusCount {
        private Integer todo = 0;
        private Integer inProgress = 0;
        private Integer done = 0;
    }

    @Data
    public static class StatusPoints {
        private BigDecimal todo = BigDecimal.ZERO;
        private BigDecimal inProgress = BigDecimal.ZERO;
        private BigDecimal done = BigDecimal.ZERO;
    }
}
