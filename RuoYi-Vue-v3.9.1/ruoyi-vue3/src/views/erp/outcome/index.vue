<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="作品名称" prop="workName">
        <el-input v-model="queryParams.workName" placeholder="请输入比赛/作品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="所属年份" prop="year">
        <el-input v-model="queryParams.year" placeholder="请输入年份" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="成果类别" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入成果类别" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="奖项等级" prop="awardLevel">
        <el-select v-model="queryParams.awardLevel" placeholder="请选择奖项等级" clearable>
          <el-option v-for="dict in erp_award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="证书编号" prop="certNo">
        <el-input v-model="queryParams.certNo" placeholder="请输入证书编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker v-model="queryParams.awardTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择获奖时间" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['erp:outcome:add']">新增成果</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['erp:outcome:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['erp:outcome:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['erp:outcome:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="outcomeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="id" width="60" />
      <el-table-column label="比赛名称" align="center" prop="workName" :show-overflow-tooltip="true" />
      <el-table-column label="届次(年份)" align="center" prop="year" width="100" />
      <el-table-column label="类别" align="center" prop="category" />
      <el-table-column label="获奖等级" align="center" prop="awardLevel">
        <template #default="scope">
          <dict-tag :options="erp_award_rank" :value="scope.row.awardLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="所属学院" align="center" prop="deptId" />
      
      <el-table-column label="审核情况" align="center" prop="auditStatus">
        <template #default="scope">
          <dict-tag :options="erp_audit_status" :value="scope.row.auditStatus"/>
        </template>
      </el-table-column>

      <el-table-column label="报销情况" align="center" width="120">
        <template #default="scope">
           <span v-if="scope.row.isReimburse === 1">
             <span v-if="scope.row.reimburseAuditStatus === '1'">已发放</span>
             <span v-else style="color: orange">待审核</span>
           </span>
           <span v-else style="color: #909399">未申请</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['erp:outcome:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['erp:outcome:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page="queryParams.pageNum" :limit="queryParams.pageSize" @pagination="getList" />
  
    <AddOutcome ref="addOutcomeRef" @ok="getList" />

  </div>
</template>

<script setup name="Outcome">
// 只保留列表查询和删除的API，新增/修改API已移至子组件
import { listOutcome, delOutcome } from "@/api/erp/outcome"
// 引入你的子组件
import AddOutcome from './add_outcome'

const { proxy } = getCurrentInstance()

// 字典：列表显示还需要用到这些字典，所以保留
const { erp_audit_status, erp_award_rank } = proxy.useDict('erp_audit_status', 'erp_award_rank')

// 变量定义：保留列表相关的变量
const outcomeList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)

// 子组件引用
const addOutcomeRef = ref(null)

// 搜索参数：保留
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    year: null,
    category: null,
    workName: null,
    awardLevel: null,
    certNo: null,
    awardTime: null
  }
})

const { queryParams } = toRefs(data)

// --- 下面这些函数逻辑完全保留，不用动 ---

// 查询列表
function getList() {
  loading.value = true
  listOutcome(queryParams.value).then(response => {
    outcomeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 搜索
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置搜索
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选处理
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

// --- 重点修改：handleAdd 和 handleUpdate ---
// 以前这里是手动 reset() 然后 open.value = true
// 现在改成调用子组件的 open 方法

function handleAdd() {
  addOutcomeRef.value.open(); // 调用子组件的新增
}

function handleUpdate(row) {
  const _id = row.id || ids.value[0]
  addOutcomeRef.value.open(_id); // 调用子组件的修改，传入ID
}

// --- 删除和导出逻辑完全保留 ---
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除？').then(function() {
    return delOutcome(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download('erp/outcome/export', { ...queryParams.value }, `outcome_${new Date().getTime()}.xlsx`)
}

// 初始化
getList();
</script>