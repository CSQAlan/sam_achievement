<template>
  <div>
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


const flowValue = ref(route.name || 'CollegeLevelUnreviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'CollegeLevelUnreviewed';
  }
);


</script>

<script>
export default {
  name: 'CollegeLevelUnreviewed'
}
</script>

<style scoped>



@media (max-width: 992px) {
  .flow-filter-bar {
    flex-wrap: wrap;
  }


}
</style>
