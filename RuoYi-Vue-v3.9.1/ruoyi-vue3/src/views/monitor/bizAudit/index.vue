<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="业务类型" prop="bizType">
        <el-input v-model="queryParams.bizType" placeholder="请输入业务类型" clearable style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务名称" prop="bizName">
        <el-input v-model="queryParams.bizName" placeholder="请输入业务名称" clearable style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务主键" prop="bizId">
        <el-input v-model="queryParams.bizId" placeholder="请输入业务主键" clearable style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="操作类型" prop="opType">
        <el-select v-model="queryParams.opType" placeholder="请选择操作类型" clearable style="width: 220px">
          <el-option v-for="item in opTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作账号" prop="operUserName">
        <el-input v-model="queryParams.operUserName" placeholder="请输入操作账号" clearable style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="执行结果" prop="success">
        <el-select v-model="queryParams.success" placeholder="请选择结果" clearable style="width: 220px">
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间" style="width: 340px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date(2000,1,1,0,0,0), new Date(2000,1,1,23,59,59)]"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['monitor:bizaudit:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="DeleteFilled" @click="handleClean" v-hasPermi="['monitor:bizaudit:remove']">清理30天前</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['monitor:bizaudit:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bizAuditList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" prop="id" width="90" align="center" />
      <el-table-column label="业务类型" prop="bizType" min-width="120" show-overflow-tooltip />
      <el-table-column label="业务名称" prop="bizName" min-width="160" show-overflow-tooltip />
      <el-table-column label="业务主键" prop="bizId" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作类型" prop="opType" width="110" align="center">
        <template #default="scope">
          <el-tag size="small">{{ formatOpType(scope.row.opType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作账号" prop="operUserName" width="120" align="center" />
      <el-table-column label="结果" prop="success" width="90" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.success === 1 ? 'success' : 'danger'">{{ scope.row.success === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时" prop="durationMs" width="100" align="center">
        <template #default="scope">
          <span>{{ scope.row.durationMs || 0 }} ms</span>
        </template>
      </el-table-column>
      <el-table-column label="操作时间" prop="createTime" width="180" align="center">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['monitor:bizaudit:query']">详情</el-button>
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
  </div>
</template>

<script setup name="BizAudit">
import { listBizAudit, delBizAudit, cleanBizAudit } from "@/api/monitor/bizAudit"

const { proxy } = getCurrentInstance()

const bizAuditList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const dateRange = ref([])

const opTypeOptions = [
  { label: "新增", value: "CREATE" },
  { label: "修改", value: "UPDATE" },
  { label: "删除", value: "DELETE" },
  { label: "通过", value: "APPROVE" },
  { label: "驳回", value: "REJECT" },
  { label: "导入", value: "IMPORT" },
  { label: "导出", value: "EXPORT" },
  { label: "批量", value: "BATCH" }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    bizType: undefined,
    bizName: undefined,
    bizId: undefined,
    opType: undefined,
    operUserName: undefined,
    success: undefined
  }
})

const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listBizAudit(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    bizAuditList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function formatOpType(value) {
  const target = opTypeOptions.find(item => item.value === value)
  return target ? target.label : value
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

function handleView(row) {
  proxy.$router.push(`/monitor/biz-audit/detail/${row.id}`)
}

function handleDelete(row) {
  const auditIds = row.id || ids.value
  proxy.$modal.confirm(`是否确认删除业务审计日志编号为"${auditIds}"的数据项？`).then(function () {
    return delBizAudit(auditIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleClean() {
  proxy.$modal.confirm("是否确认清理30天前的业务审计日志？").then(function () {
    return cleanBizAudit(30)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("清理成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download("monitor/bizAudit/export", proxy.addDateRange({
    ...queryParams.value
  }, dateRange.value), `biz_audit_${new Date().getTime()}.xlsx`)
}

getList()
</script>
