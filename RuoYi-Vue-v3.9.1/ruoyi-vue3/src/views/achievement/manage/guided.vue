<template>
  <div class="app-container">
    <AchievementManageIndex
      :list-fn="listGuidedAchievement"
      :get-fn="getGuidedManage"
      :show-add="false"
      :show-edit="true"
      :show-delete="false"
      :show-export="false"
      permission-prefix="achievement:manage"
      self-edit-scene="guided"
    />
  </div>
</template>

<script setup name="ManageGuided">
import AchievementManageIndex from '../component/index.vue'
import { getManage, listGuidedAchievement } from '@/api/achievement/manage'

const getGuidedManage = (achievementId) => getManage(achievementId, { selfEditScene: 'guided' })
</script>

<style scoped lang="scss">
</style>
