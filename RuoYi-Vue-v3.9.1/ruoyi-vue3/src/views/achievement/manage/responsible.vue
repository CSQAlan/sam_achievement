<template>
  <div class="app-container">
    <AchievementManageIndex
      :list-fn="listResponsibleAchievement"
      :get-fn="getResponsibleManage"
      :show-add="false"
      :show-edit="true"
      :show-delete="false"
      :show-export="false"
      permission-prefix="achievement:manage"
      self-edit-scene="responsible"
    />
  </div>
</template>

<script setup name="ManageResponsible">
import AchievementManageIndex from '../component/index.vue'
import { getManage, listResponsibleAchievement } from '@/api/achievement/manage'

const getResponsibleManage = (achievementId) => getManage(achievementId, { selfEditScene: 'responsible' })
</script>

<style scoped lang="scss">
</style>
