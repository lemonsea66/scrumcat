<template>
  <article class="board-story-card" @click="$emit('open', story)">
    <div class="board-card-title-row">
      <strong>{{ story.title }}</strong>
      <a-tag :color="statusMeta[story.status]?.color">
        {{ statusMeta[story.status]?.label || story.status }}
      </a-tag>
    </div>
    <p>{{ story.description || '这个故事还没有描述。' }}</p>
    <div class="board-card-meta">
      <span>{{ story.storyPoint }} SP</span>
      <span>P{{ story.priority || '-' }}</span>
      <span v-if="story.ownerNickname">负责人 {{ story.ownerNickname }}</span>
    </div>
    <div v-if="story.members?.length" class="board-card-members">
      协作：{{ story.members.join('、') }}
    </div>
    <div class="board-task-progress">
      <span v-if="story.taskTotalCount">任务：{{ story.taskDoneCount }} / {{ story.taskTotalCount }} 已完成</span>
      <span v-else>任务：暂未拆解</span>
    </div>
  </article>
</template>

<script setup>
defineProps({
  story: {
    type: Object,
    required: true
  },
  statusMeta: {
    type: Object,
    required: true
  }
})

defineEmits(['open'])
</script>
