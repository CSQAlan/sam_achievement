<template>
  <div>
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
import { useRoute} from 'vue-router';
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



const flowValue = ref(route.name || 'CollegeLevelReviewed');

watch(
  () => route.name,
  (val) => {
    flowValue.value = val || 'CollegeLevelReviewed';
  }
);


</script>

<script>
export default {
  name: 'CollegeLevelReviewed'
}
</script>

<style scoped>


@media (max-width: 992px) {
  .flow-filter-bar {
    flex-wrap: wrap;
  }

}
</style>
