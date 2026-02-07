<template>
  <div class="flow-filter-wrapper">
    <div class="flow-filter-bar">
      <el-select v-model="flowValue" placeholder="流程筛选" style="width: 220px;" @change="handleFlowChange">
        <el-option v-for="opt in flowOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
      </el-select>
    </div>
    <ManageIndex
      :list-fn="listSchool_level_reviewed"
      :get-fn="getSchool_level_reviewed"
      :add-fn="addSchool_level_reviewed"
      :update-fn="updateSchool_level_reviewed"
      :del-fn="delSchool_level_reviewed"
      :export-url="'achievement/school_level_reviewed/export'"
      :audit-dict="school_audit_status"
      permission-prefix="achievement:school_level_reviewed"
      review-source="school_level_reviewed"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ManageIndex from '@/views/achievement/component/index.vue';
import { useDict } from '@/utils/dict';
import {
  listSchool_level_reviewed,
  getSchool_level_reviewed,
  addSchool_level_reviewed,
  updateSchool_level_reviewed,
  delSchool_level_reviewed
} from '@/api/achievement/school_level_reviewed';

const { school_audit_status } = useDict('school_audit_status');

const route = useRoute();
const router = useRouter();

const flowOptions = [
  { label: '院级未审核', value: 'CollegeLevelUnreviewed' },
  { label: '院级已审核', value: 'CollegeLevelReviewed' },
  { label: '校级未审核', value: 'SchoolLevelUnreviewed' },
  { label: '校级已审核', value: 'SchoolLevelReviewed' }
];

const flowValue = ref(route.name || 'SchoolLevelReviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'SchoolLevelReviewed';
  }
);

function handleFlowChange(val) {
  if (!val || val === route.name) return;
  router.push({ name: val });
}
</script>
