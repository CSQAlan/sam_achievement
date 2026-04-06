<template>
  <div class="app-container">
    <AchievementManageIndex
        source-mode="participated"
        :list-fn="listParticipatedAchievement"
        :audit-dict="college_audit_status"
        :audit-dict-school="school_audit_status"
        :show-add="false"
        :show-edit="false"
        :show-delete="false"
    />
  </div>
</template>

<script setup name="ParticipatedAchievement">
import { ref } from 'vue';
import AchievementManageIndex from '../component/index.vue';
import { useDict } from '@/utils/dict';
import { listParticipatedAchievement } from '@/api/achievement/manage';

const { college_audit_status, school_audit_status } = useDict('college_audit_status', 'school_audit_status');
</script>

<style scoped lang="scss">
</style>