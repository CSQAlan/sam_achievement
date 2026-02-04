<template>
  <ManageIndex
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
</template>

<script setup>
import ManageIndex from '@/views/achievement/manage/index.vue';
import { useDict } from '@/utils/dict';
import {
  listCollege_level_reviewed,
  getCollege_level_reviewed,
  addCollege_level_reviewed,
  updateCollege_level_reviewed,
  delCollege_level_reviewed
} from '@/api/achievement/college_level_reviewed';

const { college_audit_status } = useDict('college_audit_status');
</script>
