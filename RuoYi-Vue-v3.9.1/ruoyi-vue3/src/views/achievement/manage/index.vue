<template>
  <div class="app-container">
    <AchievementManageIndex
        source-mode="responsible"
        :audit-dict="college_audit_status"
        :audit-dict-school="school_audit_status"
    >
      <template #action-button="{ row, handleRowUpdate, openingReviewPage, openingReviewPageId, checkEditable, permEdit }">
        <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleRowUpdate(row)"
            v-hasPermi="permEdit"
            :disabled="openingReviewPage || !checkEditable(row)"
            :loading="openingReviewPage && String(openingReviewPageId) === String(row?.achievementId)"
        >修改</el-button>
      </template>
    </AchievementManageIndex>
  </div>
</template>

<script setup name="ResponsibleAchievement">
import { ref } from 'vue';
import AchievementManageIndex from '../component/index.vue';
import { useDict } from '@/utils/dict';

const { college_audit_status, school_audit_status } = useDict('college_audit_status', 'school_audit_status');
</script>

<style scoped lang="scss">
</style>