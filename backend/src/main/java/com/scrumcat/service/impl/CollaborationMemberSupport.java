package com.scrumcat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scrumcat.entity.CollaborationMember;
import com.scrumcat.mapper.CollaborationMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CollaborationMemberSupport {

    public static final String PROJECT = "PROJECT";
    public static final String STORY = "STORY";
    public static final String SPRINT = "SPRINT";

    private final CollaborationMemberMapper collaborationMemberMapper;

    public List<String> listMembers(String targetType, Long targetId) {
        return collaborationMemberMapper.selectList(new LambdaQueryWrapper<CollaborationMember>()
                        .eq(CollaborationMember::getTargetType, targetType)
                        .eq(CollaborationMember::getTargetId, targetId)
                        .orderByAsc(CollaborationMember::getId))
                .stream()
                .map(CollaborationMember::getNickname)
                .toList();
    }

    public void replaceMembers(String targetType, Long targetId, List<String> members, Long creatorId) {
        deleteMembers(targetType, targetId);
        if (members == null) {
            return;
        }

        members.stream()
                .filter(member -> member != null && !member.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(member -> {
                    CollaborationMember collaborationMember = new CollaborationMember();
                    collaborationMember.setTargetType(targetType);
                    collaborationMember.setTargetId(targetId);
                    collaborationMember.setNickname(member);
                    collaborationMember.setRole("MEMBER");
                    collaborationMember.setCreatorId(creatorId);
                    collaborationMemberMapper.insert(collaborationMember);
                });
    }

    public void deleteMembers(String targetType, Long targetId) {
        collaborationMemberMapper.delete(new LambdaQueryWrapper<CollaborationMember>()
                .eq(CollaborationMember::getTargetType, targetType)
                .eq(CollaborationMember::getTargetId, targetId));
    }
}
