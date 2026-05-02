<template>
  <div ref="chartRef" class="chart-box"></div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({ todo: 0, inProgress: 0, done: 0 })
  }
})

const chartRef = ref(null)
let chart = null

const seriesData = computed(() => [
  { name: '未开始', value: props.data?.todo || 0 },
  { name: '进行中', value: props.data?.inProgress || 0 },
  { name: '已完成', value: props.data?.done || 0 }
])

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
})

watch(seriesData, renderChart, { deep: true })

async function renderChart() {
  await nextTick()
  if (!chartRef.value) {
    return
  }
  chart = chart || echarts.init(chartRef.value)
  chart.setOption({
    color: ['#D7B69E', '#8FB8D8', '#92C6A0'],
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, icon: 'circle' },
    series: [
      {
        type: 'pie',
        radius: ['48%', '68%'],
        center: ['50%', '42%'],
        data: seriesData.value,
        label: { formatter: '{b}: {c}' }
      }
    ]
  })
}

function resizeChart() {
  chart?.resize()
}
</script>
