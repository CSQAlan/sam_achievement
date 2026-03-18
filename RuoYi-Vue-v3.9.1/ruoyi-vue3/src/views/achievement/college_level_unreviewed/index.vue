<template>
  <div class="flow-filter-wrapper">
    <div class="flow-filter-bar">
      <el-select v-model="flowValue" placeholder="流程筛选" style="width: 220px;" @change="handleFlowChange">
        <el-option v-for="opt in flowOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
      </el-select>
    </div>
    <ManageIndex
      :list-fn="listCollege_level_unreviewed"
      :get-fn="getCollege_level_unreviewed"
      :add-fn="addCollege_level_unreviewed"
      :update-fn="updateCollege_level_unreviewed"
      :del-fn="delCollege_level_unreviewed"
      :export-url="'achievement/college_level_unreviewed/export'"
      :audit-dict="college_audit_status"
      permission-prefix="achievement:college_level_unreviewed"
      review-source="college_level_unreviewed"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ManageIndex from '@/views/achievement/component/index.vue';
import { useDict } from '@/utils/dict';
import {
  listCollege_level_unreviewed,
  getCollege_level_unreviewed,
  addCollege_level_unreviewed,
  updateCollege_level_unreviewed,
  delCollege_level_unreviewed
} from '@/api/achievement/college_level_unreviewed';

const { college_audit_status } = useDict('college_audit_status');

const route = useRoute();
const router = useRouter();

const flowOptions = [
  { label: '院级未审核', value: 'CollegeLevelUnreviewed' },
  { label: '院级已审核', value: 'CollegeLevelReviewed' },
  { label: '校级未审核', value: 'SchoolLevelUnreviewed' },
  { label: '校级已审核', value: 'SchoolLevelReviewed' }
];

const flowValue = ref(route.name || 'CollegeLevelUnreviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'CollegeLevelUnreviewed';
  }
);

function handleFlowChange(val) {
  if (!val || val === route.name) return;
  router.push({ name: val });
}
</script>

<script>
export default {
  name: 'CollegeLevelUnreviewed'
}
</script>
