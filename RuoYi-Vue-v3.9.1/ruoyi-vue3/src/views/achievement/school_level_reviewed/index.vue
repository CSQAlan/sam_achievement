<template>
  <ManageIndex
      :list-fn="listSchool_level_reviewed"
      :get-fn="getSchool_level_reviewed"
      :add-fn="addSchool_level_reviewed"
      :update-fn="updateSchool_level_reviewed"
      :del-fn="delSchool_level_reviewed"
      :export-url="'achievement/School_level_reviewed/export'"
      :audit-dict="School_audit_status"
      permission-prefix="achievement:School_level_reviewed"
      review-source="School_level_reviewed"
  />
</template>

<script setup>
import ManageIndex from '@/views/achievement/manage/index.vue';
import { useDict } from '@/utils/dict';
import {
  listSchool_level_reviewed,
  getSchool_level_reviewed,
  addSchool_level_reviewed,
  updateSchool_level_reviewed,
  delSchool_level_reviewed
} from '@/api/achievement/School_level_reviewed';

const { School_audit_status } = useDict('School_audit_status');
</script>
