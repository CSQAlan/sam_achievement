<template>
  <div class="app-container">
    <AchievementManageIndex
      :list-fn="listParticipatedAchievement"
      :get-fn="getManage"
      :show-add="false"
      :show-edit="false"
      :show-delete="false"
      :show-export="false"
      permission-prefix="achievement:manage"
    />
  </div>
</template>

<script setup name="ManageParticipated">
import AchievementManageIndex from '../component/index.vue';
import { listParticipatedAchievement, getManage } from '@/api/achievement/manage';
</script>

<style scoped lang="scss">
</style>
