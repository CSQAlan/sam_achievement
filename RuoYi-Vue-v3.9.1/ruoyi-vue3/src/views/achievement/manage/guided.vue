<template>
  <div class="app-container">
    <AchievementManageIndex 
      source-mode="guided"
      :list-fn="listGuidedAchievement"
      :audit-dict="college_audit_status"
      :audit-dict-school="school_audit_status"
    />
  </div>
</template>

<script setup name="GuidedAchievement">
import { ref } from 'vue';
import AchievementManageIndex from '../component/index.vue';
import { useDict } from '@/utils/dict';
import { listGuidedAchievement } from '@/api/achievement/manage';

const { college_audit_status, school_audit_status } = useDict('college_audit_status', 'school_audit_status');

</script>

<style scoped lang="scss">
</style>
