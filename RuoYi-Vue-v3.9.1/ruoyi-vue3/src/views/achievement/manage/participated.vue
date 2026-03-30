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
    >
      <!-- participated 模式可能需要特殊处理，因为 show-edit="false" -->
      <template #action-button="{ row, handleRowUpdate, openingReviewPage, openingReviewPageId, checkEditable, permEdit }">
        <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleRowUpdate(row)"
            :disabled="openingReviewPage"
            :loading="openingReviewPage && String(openingReviewPageId) === String(row?.achievementId)"
        >修改</el-button>
      </template>
    </AchievementManageIndex>
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