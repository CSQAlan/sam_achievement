<template>
  <div class="app-container">
    <AchievementManageIndex 
      :audit-dict="college_audit_status"
      :audit-dict-school="school_audit_status"
      :show-add="false"
      :show-edit="false"
      :show-delete="false"
    />
  </div>
</template>

<script setup name="ManageIndex">
import { ref } from 'vue';
import AchievementManageIndex from '../component/index.vue';
import { useDict } from '@/utils/dict';

const { college_audit_status, school_audit_status } = useDict('college_audit_status', 'school_audit_status');

</script>

<style scoped lang="scss">
</style>
