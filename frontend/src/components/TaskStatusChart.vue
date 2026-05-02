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

const values = computed(() => [
  props.data?.todo || 0,
  props.data?.inProgress || 0,
  props.data?.done || 0
])

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
})

watch(values, renderChart, { deep: true })

async function renderChart() {
  await nextTick()
  if (!chartRef.value) {
    return
  }
  chart = chart || echarts.init(chartRef.value)
  chart.setOption({
    color: ['#D7B69E', '#8FB8D8', '#92C6A0'],
    tooltip: { trigger: 'axis' },
    grid: { top: 28, right: 16, bottom: 32, left: 36 },
    xAxis: {
      type: 'category',
      data: ['未开始', '进行中', '已完成'],
      axisTick: { show: false }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        type: 'bar',
        barWidth: 32,
        data: values.value,
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      }
    ]
  })
}

function resizeChart() {
  chart?.resize()
}
</script>
