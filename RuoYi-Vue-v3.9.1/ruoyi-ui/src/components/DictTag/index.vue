<template>
  <div>
    <template v-for="(item, index) in options">
      <template v-if="isValueMatch(item)">
        <span
          v-if="(item.elTagType == 'default' || item.elTagType == '') && (item.elTagClass == '' || item.elTagClass == null)"
          :key="item.value"
          :index="index"
          :class="item.elTagClass"
        >{{ item.label + " " }}</span>
        <el-tag
          v-else
          :disable-transitions="true"
          :key="item.value + ''"
          :index="index"
          :type="item.elTagType"
          :class="item.elTagClass"
        >{{ item.label + " " }}</el-tag>
      </template>
    </template>
    <template v-if="unmatch && showValue">
      {{ unmatchArray | handleArray }}
    </template>
  </div>
</template>

<script setup>
// 记录未匹配的项
const unmatchArray = ref([])

const props = defineProps({
  // 数据
  options: {
    type: Array,
    default: null,
  },
  // 当前的值
  value: [Number, String, Array],
  // 当未找到匹配的数据时，显示value
  showValue: {
    type: Boolean,
    default: true,
  },
  separator: {
    type: String,
    default: ",",
  }
})

const values = computed(() => {
  if (props.value === null || typeof props.value === 'undefined' || props.value === '') return []
  if (typeof props.value === 'number' || typeof props.value === 'boolean') return [props.value]
  return Array.isArray(props.value) ? props.value.map(item => '' + item) : String(props.value).split(props.separator)
})

const unmatch = computed(() => {
  unmatchArray.value = []
  // 没有 value 不显示
  if (props.value === null || typeof props.value === 'undefined' || props.value === '') return false
  // 没有字典项时直接显示原始值
  if (!Array.isArray(props.options) || props.options.length === 0) {
    unmatchArray.value = values.value
    return true
  }
  let unmatch = false // 添加一个标志来判断是否有未匹配项
  values.value.forEach(item => {
    if (!props.options.some(v => isOptionMatch(v, item))) {
      unmatchArray.value.push(item)
      unmatch = true // 如果有未匹配项，将标志设置为true
    }
  })
  return unmatch // 返回标志的值
})

function handleArray(array) {
  if (array.length === 0) return ""
  return array.reduce((pre, cur) => {
    return pre + " " + cur
  })
}

function isOptionMatch(option, value) {
  return option.value == value || (option.code != null && option.code == value)
}

function isValueMatch(item) {
  return values.value.some(val => val == item.value || (item.code != null && item.code == val))
}
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
