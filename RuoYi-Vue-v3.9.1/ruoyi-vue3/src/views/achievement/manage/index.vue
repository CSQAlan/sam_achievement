<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果ID" prop="achievementId">
        <el-input v-model="queryParams.achievementId" placeholder="请输入成果ID" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item label="届次" prop="sessionId">
        <el-input v-model="queryParams.sessionId" placeholder="请输入届次" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option v-for="dict in achievement_category" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="作品名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入作品名称" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item label="团队名称" prop="teamName">
        <el-input v-model="queryParams.teamName" placeholder="请输入团队名称" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['achievement:manage:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['achievement:manage:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['achievement:manage:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['achievement:manage:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="manageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="届次" align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="name" />
      <el-table-column label="团队名称" align="center" prop="teamName" />
      <el-table-column label="获奖级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade"/>
        </template>
      </el-table-column>
      <el-table-column label="赛道" align="center" prop="track" />
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:manage:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:manage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <achievement-form 
      ref="achievementFormRef" 
      :get-fn="getManage"
      :add-fn="addManage"
      :update-fn="updateManage"
      @ok="getList" 
    />

  </div>
</template>

<script setup name="Manage">
// 1. 【重点修改】增加 getManage, addManage, updateManage 的引入，因为新组件需要这些作为 Props
import { listManage, getManage, delManage, addManage, updateManage } from "@/api/achievement/manage"

// 2. 【重点修改】引入同级目录下的 AchievementForm.vue
import AchievementForm from './AchievementForm.vue';

const { proxy } = getCurrentInstance()
const { achievement_category, group_type, award_rank, award_level_type } = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type')

const manageList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)

// 3. 【重点修改】引用名称改为 achievementFormRef
const achievementFormRef = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
  }
})

const { queryParams } = toRefs(data)

/** 查询成果录入列表 */
function getList() {
  loading.value = true
  listManage(queryParams.value).then(response => {
    manageList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.achievementId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 4. 【重点修改】调用新组件的 open 方法 */
function handleAdd() {
  if (achievementFormRef.value) {
    achievementFormRef.value.open();
  }
}

/** 5. 【重点修改】调用新组件的 open 方法并传入 ID */
function handleUpdate(row) {
  const _achievementId = row.achievementId || ids.value
  if (achievementFormRef.value) {
    achievementFormRef.value.open(_achievementId); 
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _achievementIds = row.achievementId || ids.value
  proxy.$modal.confirm('是否确认删除成果录入编号为"' + _achievementIds + '"的数据项？').then(function() {
    return delManage(_achievementIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('achievement/manage/export', {
    ...queryParams.value
  }, `manage_${new Date().getTime()}.xlsx`)
}

getList()
</script>