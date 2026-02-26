<template>
  <div class="flow-filter-wrapper">
    <div class="flow-filter-bar">
      <span class="flow-filter-title">流程导航（点击对应流程即可跳转到相应页面）</span>
      <el-select
        v-model="flowValue"
        class="flow-select"
        placeholder="请选择流程页面"
        @change="handleFlowChange"
      >
        <el-option-group
          v-for="group in flowGroups"
          :key="group.label"
          :label="group.label"
        >
          <el-option
            v-for="opt in group.options"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          >
            <div class="flow-option-row">
              <span class="flow-option-name">{{ opt.label }}</span>
              <el-tag size="small" :type="opt.tagType" effect="plain">{{ opt.tagText }}</el-tag>
            </div>
          </el-option>
        </el-option-group>
      </el-select>
    </div>

    <componentIndex
      :list-fn="listCollege_level_reviewed"
      :get-fn="getCollege_level_reviewed"
      :add-fn="addCollege_level_reviewed"
      :update-fn="updateCollege_level_reviewed"
      :del-fn="delCollege_level_reviewed"
      :export-url="'achievement/college_level_reviewed/export'"
      :audit-dict="college_audit_status"
      permission-prefix="achievement:college_level_reviewed"
      review-source="college_level_reviewed"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import componentIndex from '@/views/achievement/component/index.vue';
import { useDict } from '@/utils/dict';
import {
  listCollege_level_reviewed,
  getCollege_level_reviewed,
  addCollege_level_reviewed,
  updateCollege_level_reviewed,
  delCollege_level_reviewed
} from '@/api/achievement/college_level_reviewed';

const { college_audit_status } = useDict('college_audit_status');

const route = useRoute();
const router = useRouter();

const flowGroups = [
  {
    label: '院级流程',
    options: [
      { label: '院级未审核', value: 'CollegeLevelUnreviewed', tagText: '待处理', tagType: 'warning' },
      { label: '院级已审核', value: 'CollegeLevelReviewed', tagText: '已完成', tagType: 'success' }
    ]
  },
  {
    label: '校级流程',
    options: [
      { label: '校级未审核', value: 'SchoolLevelUnreviewed', tagText: '待处理', tagType: 'warning' },
      { label: '校级已审核', value: 'SchoolLevelReviewed', tagText: '已完成', tagType: 'success' }
    ]
  }
];

const flowValue = ref(route.name || 'CollegeLevelReviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'CollegeLevelReviewed';
  }
);

function handleFlowChange(val) {
  if (!val || val === route.name) return;
  router.push({ name: val });
}
</script>

<script>
export default {
  name: 'CollegeLevelReviewed'
}
</script>

<style scoped>
.flow-filter-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.flow-filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 10px;
  background: linear-gradient(180deg, #fbfdff 0%, #f3f8ff 100%);
}

.flow-filter-title {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.flow-select {
  width: 280px;
}

.flow-option-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.flow-option-name {
  color: var(--el-text-color-primary);
}

@media (max-width: 992px) {
  .flow-filter-bar {
    flex-wrap: wrap;
  }

  .flow-select {
    width: 100%;
  }
}
</style>
