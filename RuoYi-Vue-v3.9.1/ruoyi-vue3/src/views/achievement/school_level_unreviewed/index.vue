<template>
  <div>
    <ManageIndex
      :list-fn="listSchool_level_unreviewed"
      :get-fn="getSchool_level_unreviewed"
      :add-fn="addSchool_level_unreviewed"
      :update-fn="updateSchool_level_unreviewed"
      :del-fn="delSchool_level_unreviewed"
      :export-url="'achievement/school_level_unreviewed/export'"
      :audit-dict="School_audit_status"
      permission-prefix="achievement:school_level_unreviewed"
      review-source="school_level_unreviewed"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ManageIndex from '@/views/achievement/component/index.vue';
import { useDict } from '@/utils/dict';
import {
  listSchool_level_unreviewed,
  getSchool_level_unreviewed,
  addSchool_level_unreviewed,
  updateSchool_level_unreviewed,
  delSchool_level_unreviewed
} from '@/api/achievement/school_level_unreviewed';
const { School_audit_status } = useDict('School_audit_status');

const route = useRoute();
const flowValue = ref(route.name || 'SchoolLevelUnreviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'SchoolLevelUnreviewed';
  }
);
</script>

<style scoped>
@media (max-width: 992px) {
  .flow-filter-bar {
    flex-wrap: wrap;
  }

}
</style>
