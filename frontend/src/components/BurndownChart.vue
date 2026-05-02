<template>
  <div ref="chartRef" class="chart-box"></div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({ dates: [], planned: [], actual: [] })
  },
  compact: {
    type: Boolean,
    default: false
  }
})

const chartRef = ref(null)
let chart = null

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
})

watch(() => props.data, renderChart, { deep: true })

async function renderChart() {
  await nextTick()
  if (!chartRef.value) {
    return
  }
  chart = chart || echarts.init(chartRef.value)
  chart.setOption({
    color: ['#D7B69E', '#8FB8D8'],
    tooltip: { trigger: 'axis' },
    legend: props.compact ? { show: false } : { bottom: 0, icon: 'circle' },
    grid: {
      top: props.compact ? 18 : 28,
      right: 18,
      bottom: props.compact ? 24 : 48,
      left: 38
    },
    xAxis: {
      type: 'category',
      data: props.data?.dates || [],
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: props.compact ? '' : '剩余 SP',
      min: 0
    },
    series: [
      {
        name: '计划线',
        type: 'line',
        smooth: true,
        symbol: props.compact ? 'none' : 'circle',
        data: props.data?.planned || []
      },
      {
        name: '实际线',
        type: 'line',
        smooth: true,
        symbol: props.compact ? 'none' : 'circle',
        data: props.data?.actual || []
      }
    ]
  })
}

function resizeChart() {
  chart?.resize()
}
</script>
