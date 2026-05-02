<template>
  <div v-if="imageUrl" class="cat-image-frame" :class="variant">
    <img :src="imageUrl" :alt="alt" @error="hidden = true" />
  </div>
  <div v-else class="cat-image-fallback" :class="variant">
    {{ fallback }}
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: '猫猫插图'
  },
  fallback: {
    type: String,
    default: '猫猫提示'
  },
  variant: {
    type: String,
    default: ''
  }
})

const images = import.meta.glob('../assets/imgs/*.png', {
  eager: true,
  query: '?url',
  import: 'default'
})
const hidden = ref(false)

watch(() => props.name, () => {
  hidden.value = false
})

const imageUrl = computed(() => {
  if (hidden.value) {
    return ''
  }
  return images[`../assets/imgs/${props.name}`] || ''
})
</script>
