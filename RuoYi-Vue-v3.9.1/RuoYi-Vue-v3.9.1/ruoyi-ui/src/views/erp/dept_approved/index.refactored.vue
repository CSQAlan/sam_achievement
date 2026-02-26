<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="主键" prop="id">
        <el-input v-model="queryParams.id" placeholder="请输入主键" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="所属年份" prop="year">
        <el-input v-model="queryParams.year" placeholder="请输入所属年份" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="成果类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择成果类别" clearable>
          <el-option v-for="dict in erp_outcome_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="级别类型" prop="levelType">
        <el-select v-model="queryParams.levelType" placeholder="请选择级别类型" clearable>
          <el-option v-for="dict in erp_award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="奖项等级" prop="awardLevel">
        <el-select v-model="queryParams.awardLevel" placeholder="请选择奖项等级" clearable>
          <el-option v-for="dict in erp_award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="证书编号" prop="certNo">
        <el-input v-model="queryParams.certNo" placeholder="请输入证书编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="组别" prop="groupType">
        <el-select v-model="queryParams.groupType" placeholder="请选择组别" clearable>
          <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="所属学院" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择所属学院" clearable filterable>
          <el-option v-for="dept in deptOptions" :key="dept.value" :label="dept.label" :value="dept.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option v-for="dict in dept_audit_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
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
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="所属年份" align="center" prop="year" />
      <el-table-column label="成果类别" align="center" prop="category">
        <template #default="scope"><dict-tag :options="erp_outcome_category" :value="scope.row.category" /></template>
      </el-table-column>
      <el-table-column label="负责人姓名" align="center" prop="workName" min-width="180" show-overflow-tooltip />
      <el-table-column label="级别类型" align="center" prop="levelType">
        <template #default="scope"><dict-tag :options="erp_award_level_type" :value="scope.row.levelType" /></template>
      </el-table-column>
      <el-table-column label="奖项等级" align="center" prop="awardLevel">
        <template #default="scope"><dict-tag :options="erp_award_rank" :value="scope.row.awardLevel" /></template>
      </el-table-column>
      <el-table-column label="赛道" align="center" prop="track" />
      <el-table-column label="证书编号" align="center" prop="certNo" />
      <el-table-column label="组别" align="center" prop="groupType">
        <template #default="scope"><dict-tag :options="erp_group_type" :value="scope.row.groupType" /></template>
      </el-table-column>
      <el-table-column label="所属学院" align="center" prop="deptId">
        <template #default="scope"><dict-tag :options="deptOptions" :value="scope.row.deptId" /></template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="120">
        <template #default="scope"><span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template #default="scope"><dict-tag :options="dept_audit_status" :value="scope.row.auditStatus" /></template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button link type="primary" icon="Search" @click="handleReview(scope.row)">审阅</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['erp:dept_approved:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['erp:dept_approved:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 复用弹窗 CRUD -->
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

<script setup name="DeptApproved">
import { ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { useCrud, useDictData } from '@/composables'
import BaseOutcomeDialog from '@/views/erp/BaseOutcomeDialog.vue'
import { listDept } from '@/api/system/dept'
import {
  listDept_approved,
  getDept_approved,
  addDept_approved,
  updateDept_approved,
  delDept_approved
} from '@/api/erp/dept_approved'

const { proxy } = getCurrentInstance()
const router = useRouter()

// 使用字典数据
const {
  erp_outcome_category,
  dept_audit_status,
  erp_award_level_type,
  erp_award_rank,
  erp_group_type
} = useDictData(
  'erp_outcome_category',
  'dept_audit_status',
  'erp_award_level_type',
  'erp_award_rank',
  'erp_group_type'
)

// 使用 CRUD Composable
const {
  dataList,
  loading,
  total,
  single,
  multiple,
  queryParams,
  getList,
  handleQuery,
  resetQuery,
  handleSelectionChange,
  handlePagination,
  handleDelete,
  handleExport
} = useCrud({
  listApi: listDept_approved,
  deleteApi: delDept_approved,
  exportApi: 'erp/dept_approved/export',
  defaultQueryParams: {
    id: null,
    year: null,
    category: null,
    levelType: null,
    awardLevel: null,
    track: null,
    certNo: null,
    groupType: null,
    deptId: null,
    auditStatus: null
  }
})

// 其他状态
const dlgRef = ref(null)
const queryRef = ref(null)
const showSearch = ref(true)
const deptOptions = ref([])

// 获取部门选项
const getDeptOptions = async () => {
  try {
    const res = await listDept({})
    deptOptions.value = (res.data || []).map(item => ({
      value: item.deptId,
      label: item.deptName
    }))
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 新增
const handleAdd = () => {
  dlgRef.value?.open()
}

// 修改
const handleUpdate = (row) => {
  const id = row?.id || (single.value ? null : queryParams.pageNum)
  if (!id) {
    proxy.$modal.msgWarning('请选择要修改的数据')
    return
  }
  dlgRef.value?.open(id)
}

// 审阅
const handleReview = (row) => {
  router.push({
    path: '/erp/reviewPage',
    query: { id: row.id, source: 'dept_approved' }
  })
}

// 初始化
getDeptOptions()
getList()
</script>
