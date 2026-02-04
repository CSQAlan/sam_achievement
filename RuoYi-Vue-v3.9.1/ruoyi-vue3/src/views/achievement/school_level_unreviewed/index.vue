<template>
  <ManageIndex
      :list-fn="listSchool_level_unreviewed"
      :get-fn="getSchool_level_unreviewed"
      :add-fn="addSchool_level_unreviewed"
      :update-fn="updateSchool_level_unreviewed"
      :del-fn="delSchool_level_unreviewed"
      :export-url="'achievement/School_level_unreviewed/export'"
      :audit-dict="School_audit_status"
      permission-prefix="achievement:School_level_unreviewed"
      review-source="School_level_unreviewed"
  />
</template>

<script setup>
import ManageIndex from '@/views/achievement/manage/index.vue';
import { useDict } from '@/utils/dict';
import {
  listSchool_level_unreviewed,
  getSchool_level_unreviewed,
  addSchool_level_unreviewed,
  updateSchool_level_unreviewed,
  delSchool_level_unreviewed
} from '@/api/achievement/School_level_unreviewed';

const { School_audit_status } = useDict('School_audit_status');
</script>
