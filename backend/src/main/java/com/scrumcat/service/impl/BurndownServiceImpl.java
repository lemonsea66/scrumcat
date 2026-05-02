package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.common.UserContext;
import com.scrumcat.entity.Sprint;
import com.scrumcat.entity.SprintStory;
import com.scrumcat.entity.StoryStatusLog;
import com.scrumcat.entity.UserStory;
import com.scrumcat.exception.BusinessException;
import com.scrumcat.mapper.SprintMapper;
import com.scrumcat.mapper.SprintStoryMapper;
import com.scrumcat.mapper.StoryStatusLogMapper;
import com.scrumcat.mapper.UserStoryMapper;
import com.scrumcat.service.BurndownService;
import com.scrumcat.vo.BurndownVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BurndownServiceImpl implements BurndownService {

    private final SprintMapper sprintMapper;
    private final SprintStoryMapper sprintStoryMapper;
    private final UserStoryMapper userStoryMapper;
    private final StoryStatusLogMapper storyStatusLogMapper;

    @Override
    public BurndownVO getBurndown(Long sprintId) {
        Sprint sprint = getCurrentUserSprint(sprintId);
        List<UserStory> stories = listSprintStories(sprint);
        Map<Long, BigDecimal> storyPoints = new HashMap<>();
        stories.forEach(story -> storyPoints.put(story.getId(), safePoint(story.getStoryPoint())));

        BigDecimal totalPoints = storyPoints.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BurndownVO vo = new BurndownVO();
        vo.setSprintId(sprint.getId());
        vo.setSprintName(sprint.getName());
        vo.setTotalPoints(formatPoint(totalPoints));

        List<LocalDate> dates = listSprintDates(sprint);
        dates.forEach(date -> {
            vo.getDates().add(date.toString());
            vo.getPlanned().add(calculatePlanned(totalPoints, dates.size(), vo.getPlanned().size()));
        });

        Set<Long> doneStoryIds = initialDoneStoriesWithoutLogs(stories, sprint);
        List<StoryStatusLog> logs = listSprintLogs(sprint, storyPoints.keySet());
        int logIndex = 0;
        for (LocalDate date : dates) {
            while (logIndex < logs.size()
                    && !logs.get(logIndex).getChangedAt().toLocalDate().isAfter(date)) {
                StoryStatusLog log = logs.get(logIndex);
                if ("DONE".equals(log.getNewStatus())) {
                    doneStoryIds.add(log.getStoryId());
                } else if ("DONE".equals(log.getOldStatus())) {
                    doneStoryIds.remove(log.getStoryId());
                }
                logIndex++;
            }
            vo.getActual().add(formatPoint(totalPoints.subtract(sumDonePoints(doneStoryIds, storyPoints))));
        }

        BigDecimal remaining = vo.getActual().isEmpty()
                ? totalPoints
                : vo.getActual().get(vo.getActual().size() - 1);
        vo.setRemainingPoints(formatPoint(remaining));
        return vo;
    }

    private Sprint getCurrentUserSprint(Long id) {
        Sprint sprint = sprintMapper.selectOne(new LambdaQueryWrapper<Sprint>()
                .eq(Sprint::getId, id)
                .eq(Sprint::getCreatorId, requireCurrentUserId())
                .last("LIMIT 1"));
        if (sprint == null) {
            throw new BusinessException("Sprint 不存在");
        }
        return sprint;
    }

    private List<UserStory> listSprintStories(Sprint sprint) {
        List<Long> storyIds = sprintStoryMapper.selectList(new LambdaQueryWrapper<SprintStory>()
                        .eq(SprintStory::getSprintId, sprint.getId()))
                .stream()
                .map(SprintStory::getStoryId)
                .toList();
        if (storyIds.isEmpty()) {
            return List.of();
        }
        return userStoryMapper.selectList(new LambdaQueryWrapper<UserStory>()
                .eq(UserStory::getCreatorId, sprint.getCreatorId())
                .eq(UserStory::getProjectId, sprint.getProjectId())
                .in(UserStory::getId, storyIds));
    }

    private List<StoryStatusLog> listSprintLogs(Sprint sprint, Set<Long> storyIds) {
        if (storyIds.isEmpty()) {
            return List.of();
        }
        return storyStatusLogMapper.selectList(new LambdaQueryWrapper<StoryStatusLog>()
                .eq(StoryStatusLog::getSprintId, sprint.getId())
                .in(StoryStatusLog::getStoryId, storyIds)
                .le(StoryStatusLog::getChangedAt, sprint.getEndDate().atTime(LocalTime.MAX))
                .orderByAsc(StoryStatusLog::getChangedAt)
                .orderByAsc(StoryStatusLog::getId));
    }

    private Set<Long> initialDoneStoriesWithoutLogs(List<UserStory> stories, Sprint sprint) {
        Set<Long> doneStoryIds = new HashSet<>();
        for (UserStory story : stories) {
            if (!"DONE".equals(story.getStatus())) {
                continue;
            }
            Long logCount = storyStatusLogMapper.selectCount(new LambdaQueryWrapper<StoryStatusLog>()
                    .eq(StoryStatusLog::getSprintId, sprint.getId())
                    .eq(StoryStatusLog::getStoryId, story.getId())
                    .le(StoryStatusLog::getChangedAt, sprint.getEndDate().atTime(LocalTime.MAX)));
            if (logCount == 0) {
                doneStoryIds.add(story.getId());
            }
        }
        return doneStoryIds;
    }

    private List<LocalDate> listSprintDates(Sprint sprint) {
        long days = ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate());
        if (days < 0) {
            return List.of(sprint.getStartDate());
        }
        return sprint.getStartDate().datesUntil(sprint.getEndDate().plusDays(1)).toList();
    }

    private BigDecimal calculatePlanned(BigDecimal totalPoints, int dateCount, int index) {
        if (dateCount <= 1) {
            return formatPoint(totalPoints);
        }
        BigDecimal remainingSlots = BigDecimal.valueOf(dateCount - 1L - index);
        BigDecimal divisor = BigDecimal.valueOf(dateCount - 1L);
        return formatPoint(totalPoints.multiply(remainingSlots).divide(divisor, 2, RoundingMode.HALF_UP));
    }

    private BigDecimal sumDonePoints(Set<Long> doneStoryIds, Map<Long, BigDecimal> storyPoints) {
        return doneStoryIds.stream()
                .map(storyId -> storyPoints.getOrDefault(storyId, BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal safePoint(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal formatPoint(BigDecimal value) {
        return safePoint(value).max(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP);
    }

    private Long requireCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
