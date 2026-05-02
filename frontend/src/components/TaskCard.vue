<template>
  <article class="task-card">
    <strong>{{ task.title }}</strong>
    <p v-if="task.description">{{ task.description }}</p>
    <div class="task-card-meta">
      <span>{{ task.estimatedHours }}h</span>
      <a-tag :color="statusMeta[task.status]?.color">
        {{ statusMeta[task.status]?.label || task.status }}
      </a-tag>
    </div>
    <div class="task-card-actions" @click.stop @mousedown.stop>
      <template v-if="task.status === 'TODO'">
        <a-button size="small" class="soft-outline-button" @click="emitStatus('IN_PROGRESS')">开始</a-button>
        <a-button size="small" type="primary" @click="emitStatus('DONE')">完成</a-button>
      </template>
      <template v-else-if="task.status === 'IN_PROGRESS'">
        <a-button size="small" class="soft-outline-button" @click="emitStatus('TODO')">退回</a-button>
        <a-button size="small" type="primary" @click="emitStatus('DONE')">完成</a-button>
      </template>
      <template v-else>
        <a-button size="small" class="soft-outline-button" @click="emitStatus('IN_PROGRESS')">重新进行</a-button>
      </template>
    </div>
  </article>
</template>

<script setup>
const props = defineProps({
  task: {
    type: Object,
    required: true
  },
  statusMeta: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['change-status'])

function emitStatus(status) {
  if (props.task.status === status) {
    return
  }
  emit('change-status', props.task, status)
}
</script>
