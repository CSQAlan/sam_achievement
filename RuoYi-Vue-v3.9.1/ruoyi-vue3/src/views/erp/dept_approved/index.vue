<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果ID" prop="id">
        <el-input v-model="queryParams.id" placeholder="请输入成果ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <!-- ✅ 比赛：暂不设置字典 -->
      <el-form-item label="比赛" prop="competition">
        <el-input v-model="queryParams.competition" placeholder="请输入比赛名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="届次" prop="year">
        <el-input v-model="queryParams.year" placeholder="请输入届次" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="参赛选手" prop="participant">
        <el-input v-model="queryParams.participant" placeholder="请输入参赛选手" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="指导老师" prop="teacher">
        <el-input v-model="queryParams.teacher" placeholder="请输入指导老师" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <!-- ✅ 成果类别：字典下拉 -->
      <el-form-item label="成果类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择成果类别" clearable>
          <el-option v-for="dict in erp_outcome_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <!-- ✅ 作品名称 -->
      <el-form-item label="作品名称" prop="workName">
        <el-input v-model="queryParams.workName" placeholder="请输入作品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <!-- ✅ 级别：字典下拉 -->
      <el-form-item label="级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择级别" clearable>
          <el-option v-for="dict in erp_award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <!-- ✅ 获奖等级：字典下拉 -->
      <el-form-item label="获奖等级" prop="awardLevel">
        <el-select v-model="queryParams.awardLevel" placeholder="请选择获奖等级" clearable>
          <el-option v-for="dict in erp_award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <!-- ✅ 证书编号 -->
      <el-form-item label="证书编号" prop="certNo">
        <el-input v-model="queryParams.certNo" placeholder="请输入证书编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <!-- ✅ 组别：字典下拉 -->
      <el-form-item label="组别" prop="groupType">
        <el-select v-model="queryParams.groupType" placeholder="请选择组别" clearable>
          <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <!-- ✅ 审核状态：院级字典 -->
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option v-for="dict in dept_audit_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <!-- ✅ 获奖时间：时间段选择 -->
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker
            v-model="queryParams.awardTime"
            type="daterange"
            placeholder="选择获奖时间"
            clearable
            @change="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['erp:dept_approved:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['erp:dept_approved:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['erp:dept_approved:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['erp:dept_approved:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dept_approvedList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="id" />
      <el-table-column label="比赛" align="center" prop="competition" />
      <el-table-column label="届次" align="center" prop="year" />
      <el-table-column label="参赛选手" align="center" prop="participant" />
      <el-table-column label="指导老师" align="center" prop="teacher" />
      <el-table-column label="成果类别" align="center" prop="category">
        <template #default="scope"><dict-tag :options="erp_outcome_category" :value="scope.row.category" /></template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="workName" />
      <el-table-column label="级别" align="center" prop="level">
        <template #default="scope"><dict-tag :options="erp_award_level_type" :value="scope.row.level" /></template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="awardLevel">
        <template #default="scope"><dict-tag :options="erp_award_rank" :value="scope.row.awardLevel" /></template>
      </el-table-column>
      <el-table-column label="证书编号" align="center" prop="certNo" />
      <el-table-column label="组别" align="center" prop="groupType">
        <template #default="scope"><dict-tag :options="erp_group_type" :value="scope.row.groupType" /></template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template #default="scope"><dict-tag :options="dept_audit_status" :value="scope.row.auditStatus" /></template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="120">
        <template #default="scope"><span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" icon="Search" @click="handleReview(scope.row)">审阅</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['erp:dept_approved:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['erp:dept_approved:remove']">删除</el-button>
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

    <BaseOutcomeDialog
        ref="dlgRef"
        :getFn="getDept_approved"
        :addFn="addDept_approved"
        :updateFn="updateDept_approved"
        titleAdd="新增成果"
        titleEdit="修改成果"
        @ok="getList"
    />
  </div>
</template>

<script setup name="dept_approved">
import { getCurrentInstance, reactive, ref, toRefs } from "vue";
import { useRouter } from "vue-router";
import BaseOutcomeDialog from "@/views/erp/BaseOutcomeDialog.vue";
import { listDept } from "@/api/system/dept";

import {
  listDept_approved,
  getDept_approved,
  addDept_approved,
  updateDept_approved,
  delDept_approved
} from "@/api/erp/dept_approved";

const { proxy } = getCurrentInstance();
const router = useRouter();

const {
  erp_outcome_category,
  dept_audit_status,
  erp_award_level_type,
  erp_award_rank,
  erp_group_type
} = proxy.useDict(
    "erp_outcome_category",
    "dept_audit_status",
    "erp_award_level_type",
    "erp_award_rank",
    "erp_group_type"
);

const dept_approvedList = ref([]);
const dlgRef = ref(null);

const loading = ref(true);
const showSearch = ref(true);

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const deptOptions = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    id: null,
    competition: null,
    year: null,
    participant: null,
    teacher: null,
    category: null,
    workName: null,
    level: null,
    awardLevel: null,
    certNo: null, // ✅ 保留
    groupType: null,
    auditStatus: null,
    awardTime: null, // 时间段选择
  }
});
const { queryParams } = toRefs(data);

function getList() {
  loading.value = true;
  listDept_approved(queryParams.value).then((res) => {
    dept_approvedList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getDeptOptions() {
  listDept({}).then((res) => {
    deptOptions.value = (res.data || []).map((item) => ({
      value: item.deptId,
      label: item.deptName
    }));
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map((i) => i.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  dlgRef.value?.open();
}

function handleUpdate(row) {
  dlgRef.value?.open(row?.id || ids.value?.[0]);
}

function handleDelete(row) {
  const _ids = row?.id ? row.id : ids.value;
  proxy.$modal
      .confirm(`是否确认删除院级已审核的成果编号为 "${_ids}" 的数据项？`)
      .then(() => delDept_approved(_ids))
      .then(() => {
        proxy.$modal.msgSuccess("删除成功");
        getList();
      })
      .catch(() => {});
}

function handleExport() {
  proxy.download("erp/dept_approved/export", { ...queryParams.value }, `dept_approved_${new Date().getTime()}.xlsx`);
}

/** ✅ 审阅按钮点击后，跳转到审核页 */
function handleReview(row) {
  router.push({ path: "/erp/reviewPage", query: { id: row.id, source: "dept_approved" } });
}

getDeptOptions();
getList();
</script>
