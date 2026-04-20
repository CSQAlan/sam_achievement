<template>
  <div class="app-container">
    <AchievementManageIndex
        source-mode="guided"
        :list-fn="listGuidedAchievement"
        :audit-dict="college_audit_status"
        :audit-dict-school="school_audit_status"
    >
      <!-- 自定义修改按钮 -->
      <template #action-button="{ row, handleRowUpdate, openingReviewPage, openingReviewPageId, checkEditable, permEdit }">
        <el-button
            v-if="showEdit && canUseEditAction"
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

<script setup name="GuidedAchievement">
import { ref } from 'vue';
import AchievementManageIndex from '../component/index.vue';
import { useDict } from '@/utils/dict';
import { listGuidedAchievement } from '@/api/achievement/manage';

const { college_audit_status, school_audit_status } = useDict('college_audit_status', 'school_audit_status');
</script>

<style scoped lang="scss">
</style>