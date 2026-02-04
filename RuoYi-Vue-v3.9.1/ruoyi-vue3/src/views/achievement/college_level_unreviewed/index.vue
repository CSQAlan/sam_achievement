<template>
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
</template>

<script setup>
import ManageIndex from '@/views/achievement/manage/index.vue';
import { useDict } from '@/utils/dict';
import {
  listCollege_level_unreviewed,
  getCollege_level_unreviewed,
  addCollege_level_unreviewed,
  updateCollege_level_unreviewed,
  delCollege_level_unreviewed
} from '@/api/achievement/college_level_unreviewed';

const { college_audit_status } = useDict('college_audit_status');
</script>
