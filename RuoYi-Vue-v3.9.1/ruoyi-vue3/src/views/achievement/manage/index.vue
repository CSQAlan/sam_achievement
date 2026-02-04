<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果ID" prop="achievementId">
        <el-input
            v-model="queryParams.achievementId"
            placeholder="请输入成果ID"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="比赛" prop="track">
        <el-input
            v-model="queryParams.track"
            placeholder="请输入比赛"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="届次" prop="sessionId">
        <el-input
            v-model="queryParams.sessionId"
            placeholder="请输入届次"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option
              v-for="dict in achievement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="作品名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入作品名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="reviewStatus">
        <el-select v-model="queryParams.reviewStatus" placeholder="请选择审核状态" clearable>
          <el-option
              v-for="dict in auditStatus"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker
            v-model="queryParams.awardTimeStart"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
        />
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
    </el-row>

    <el-table v-loading="loading" :data="listData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="比赛" align="center" prop="track" />
      <el-table-column label="届次" align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="name" />
      <el-table-column label="级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade" />
        </template>
      </el-table-column>
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="组别" align="center" prop="groupId">
        <template #default="scope">
          <dict-tag :options="group_type" :value="scope.row.groupId" />
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="审核状态" align="center" prop="reviewStatus">
        <template #default="scope">
          <dict-tag :options="auditStatus" :value="scope.row.reviewStatus" />
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Review" @click="handleReview(scope.row)" v-hasPermi="['achievement:manage:review']">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:manage:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:manage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <AchievementForm
        ref="achievementFormRef"
        :get-fn="getManage"
        :add-fn="addManage"
        :update-fn="updateManage"
        @ok="getList"
    />
  </div>
</template>

<script setup>
// 字典数据
const { achievement_category, group_type, award_rank, award_level_type, college_audit_status, school_audit_status } = useDict(
    'achievement_category', 'group_type', 'award_rank', 'award_level_type', 'college_audit_status', 'school_audit_status'
);

// 数据和状态管理
const queryParams = reactive({
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
  awardTime: null
});

const listData = ref([]);
const loading = ref(false);
const total = ref(0);
const showSearch = ref(true);
const single = ref(true);
const multiple = ref(true);
const ids = ref([]);

// 动态获取审核状态
const auditStatus = computed(() => {
  return queryParams.reviewStatus === 'college'
      ? college_audit_status
      : school_audit_status;
});

// 获取列表数据
function getList() {
  loading.value = true;
  listManage(queryParams).then(response => {
    listData.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 搜索操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询条件
const resetQuery = () => {
  Object.keys(queryParams).forEach(key => {
    queryParams[key] = null;
  });
  getList();
};

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

// 新增操作
function handleAdd() {
  if (achievementFormRef.value) {
    achievementFormRef.value.open();
  }
}

// 修改操作
function handleUpdate(row) {
  const _achievementId = row.achievementId || ids.value[0];
  if (achievementFormRef.value) {
    achievementFormRef.value.open(_achievementId);
  }
}

// 删除操作
function handleDelete(row) {
  const _achievementIds = row.achievementId || ids.value;
  proxy.$modal
      .confirm(`是否确认删除成果编号为 "${_achievementIds}" 的数据项？`)
      .then(() => delManage(_achievementIds))
      .then(() => {
        proxy.$modal.msgSuccess("删除成功");
        getList();
      })
      .catch(() => {});
}

getList();
</script>
