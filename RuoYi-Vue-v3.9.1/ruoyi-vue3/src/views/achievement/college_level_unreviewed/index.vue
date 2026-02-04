<template>
  <div class="app-container">
    <!-- 查询表单 -->
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
      <el-form-item label="级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择级别" clearable>
          <el-option
              v-for="dict in award_level_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖等级" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
          <el-option
              v-for="dict in award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="证书编号" prop="certificateNo">
        <el-input
            v-model="queryParams.certificateNo"
            placeholder="请输入证书编号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="组别" prop="groupId">
        <el-select v-model="queryParams.groupId" placeholder="请选择组别" clearable>
          <el-option
              v-for="dict in group_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学号" prop="studentId">
        <el-input
            v-model="queryParams.studentId"
            placeholder="请输入学号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="reviewStatus">
        <el-select v-model="queryParams.reviewStatus" placeholder="请选择审核状态" clearable>
          <el-option
              v-for="dict in college_audit_status"
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

    <!-- 新增、修改、删除按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['achievement:college_level_unreviewed:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['achievement:college_level_unreviewed:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['achievement:college_level_unreviewed:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['achievement:college_level_unreviewed:export']"
        >导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="college_level_unreviewedList" @selection-change="handleSelectionChange">
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
          <dict-tag :options="college_audit_status" :value="scope.row.reviewStatus" />
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:college_level_unreviewed:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:college_level_unreviewed:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改院级未审核对话框 -->
    <BaseachievementDialog
        :getFn="getCollege_level_unreviewed"
        :addFn="addCollege_level_unreviewed"
        :updateFn="updateCollege_level_unreviewed"
        :pageMode="false"
        :titleAdd="'新增院级未审核'"
        :titleEdit="'修改院级未审核'"
        v-model:visible="open"
        @ok="handleFormSubmit"
        @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup>
import { ref, reactive, toRefs } from 'vue';
import { listCollege_level_unreviewed, getCollege_level_unreviewed, delCollege_level_unreviewed, addCollege_level_unreviewed, updateCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import BaseachievementDialog from '../BaseOutcomeDialog.vue';  // 导入BaseachievementDialog组件

// 其他状态和数据
const college_level_unreviewedList = ref([]);
const open = ref(false);  // 控制弹窗显示
const title = ref("");    // 用于设置标题
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  achievementId: null,
  sessionId: null,
  // 更多查询字段...
});
const loading = ref(false);
const total = ref(0);

// 处理新增操作
function handleAdd() {
  open.value = true;
  title.value = "新增院级未审核";
}

// 处理修改操作
function handleUpdate(row) {
  open.value = true;
  title.value = "修改院级未审核";
  getCollege_level_unreviewed(row.achievementId).then(response => {
    // 处理获取的数据
    console.log(response);
  });
}

// 提交表单操作
function handleFormSubmit() {
  // 提交表单后的操作
  getList();
  open.value = false;
}

// 取消操作
function handleFormCancel() {
  open.value = false;
}

// 获取列表数据
function getList() {
  loading.value = true;
  listCollege_level_unreviewed(queryParams).then(response => {
    college_level_unreviewedList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 删除操作
function handleDelete(row) {
  const ids = [row.achievementId];  // 你可以根据选择的多行数据来传递
  delCollege_level_unreviewed(ids).then(() => {
    getList();
  });
}
</script>
