<template>
  <div>
    <ManageIndex
      :list-fn="listSchool_level_reviewed"
      :get-fn="getSchool_level_reviewed"
      :add-fn="addSchool_level_reviewed"
      :update-fn="updateSchool_level_reviewed"
      :del-fn="delSchool_level_reviewed"
      :export-url="'achievement/school_level_reviewed/export'"
      :audit-dict="School_audit_status"
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
const { School_audit_status } = useDict('School_audit_status');

const route = useRoute();
const router = useRouter();



const flowValue = ref(route.name || 'SchoolLevelReviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'SchoolLevelReviewed';
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
