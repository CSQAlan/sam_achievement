<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="报销项目名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入报销项目名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报销时间" prop="reimbursementTime">
        <el-date-picker
            v-model="reimbursementTimeRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            clearable
            @change="handleReimbursementTimeChange">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="归属学院" prop="ownerDepId">
        <treeselect
            v-model="queryParams.ownerDepId"
            :options="deptOptions"
            :normalizer="normalizer"
            placeholder="请选择归属学院"
            clearable
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
              v-for="dict in reimbursementStatusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            :icon="Plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:SamReimbursementItems:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :icon="Edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['system:SamReimbursementItems:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :icon="Delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:SamReimbursementItems:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            :icon="Download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:SamReimbursementItems:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SamReimbursementItemsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="报销项目名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="报销时间" align="center" prop="reimbursementTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.reimbursementTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总金额" align="center" prop="totalFee">
        <template #default="scope">
          <span>{{ formatMoney(scope.row.totalFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已发放金额" align="center" prop="paidFee">
        <template #default="scope">
          <span>{{ formatMoney(scope.row.paidFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报销项目数量" align="center" prop="amount" />
      <el-table-column label="归属学院" align="center" prop="ownerDepId" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="reimbursementStatusOptions" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-button
              size="mini"
              type="text"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:SamReimbursementItems:edit']"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:SamReimbursementItems:remove']"
          >删除</el-button>
          <el-button
              size="mini"
              type="text"
              :icon="Document"
              @click="handleExportPdf(scope.row)"
              v-hasPermi="['system:SamReimbursementItems:export']"
          >导出PDF</el-button>
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

    <!-- 添加或修改报销项目对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报销项目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入报销项目名称" />
        </el-form-item>
        <el-form-item label="报销时间" prop="reimbursementTime">
          <el-date-picker clearable
                          v-model="form.reimbursementTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择报销时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="总金额" prop="totalFee">
          <el-input-number v-model="form.totalFee" :precision="2" :min="0" :max="9999999" placeholder="请输入总金额"  />
        </el-form-item>
        <el-form-item label="已发放金额" prop="paidFee">
          <el-input-number v-model="form.paidFee" :precision="2" :min="0" :max="9999999" placeholder="请输入已发放金额" />
        </el-form-item>
        <el-form-item label="报销项目数量" prop="amount">
          <el-input-number v-model="form.amount" :precision="0" :min="0" placeholder="报销项目数量"  />
        </el-form-item>
        <el-form-item label="归属学院" prop="ownerDepId">
          <treeselect
              v-model="form.ownerDepId"
              :options="deptOptions"
              :normalizer="normalizer"
              placeholder="请选择归属学院"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in reimbursementStatusOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download, Document } from '@element-plus/icons-vue'
import {
  listSamReimbursementItems,
  getSamReimbursementItems,
  delSamReimbursementItems,
  addSamReimbursementItems,
  updateSamReimbursementItems
} from "@/api/system/SamReimbursementItems"
import Treeselect from "vue3-treeselect"
import "vue3-treeselect/dist/vue3-treeselect.css"
import { listDept } from "@/api/system/dept"
import useUserStore from '@/store/modules/user'

// 定义响应式数据
const loading = ref(false)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const SamReimbursementItemsList = ref([])
const title = ref('')
const open = ref(false)

// 字典选项
const reimbursementStatusOptions = ref([])

// 日期范围处理
const reimbursementTimeRange = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  ownerDepId: null,
  status: null,
  beginReimbursementTime: null,
  endReimbursementTime: null
})

// 表单参数
const form = reactive({
  id: null,
  name: null,
  reimbursementTime: null,
  totalFee: null,
  paidFee: null,
  amount: null,
  ownerDepId: null,
  status: "0",
  createBy: null,
  createTime: null,
  updateBy: null,
  updateTime: null,
  remark: null,
  delFlag: null
})

// 部门树选项
const deptOptions = ref([])

// 表单校验规则
const rules = reactive({
  name: [
    { required: true, message: "报销项目名称不能为空", trigger: "blur" }
  ],
  reimbursementTime: [
    { required: true, message: "报销时间不能为空", trigger: "blur" }
  ],
  totalFee: [
    { required: true, message: "总金额不能为空", trigger: "blur" }
  ],
  ownerDepId: [
    { required: true, message: "归属学院不能为空", trigger: "blur" }
  ]
})

// 表单ref
const formRef = ref(null)
const queryFormRef = ref(null)

// 获取用户store
const userStore = useUserStore()

/** 查询报销项目列表 */
const getList = async () => {
  loading.value = true
  try {
    const response = await listSamReimbursementItems(queryParams)
    SamReimbursementItemsList.value = response.rows
    total.value = response.total
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

/** 查询部门下拉树结构 */
const getDeptTree = async () => {
  try {
    const response = await listDept()
    deptOptions.value = handleTree(response.data, "deptId")
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

/** 转换部门数据结构 */
const normalizer = (node) => {
  if (node.children && !node.children.length) {
    delete node.children
  }
  return {
    id: node.deptId,
    label: node.deptName,
    children: node.children
  }
}

/** 格式化金额 */
const formatMoney = (money) => {
  if (money == null) return '¥0.00'
  return '¥' + parseFloat(money).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

/** 处理报销时间范围变化 */
const handleReimbursementTimeChange = (value) => {
  if (value && value.length === 2) {
    queryParams.beginReimbursementTime = value[0]
    queryParams.endReimbursementTime = value[1]
  } else {
    queryParams.beginReimbursementTime = null
    queryParams.endReimbursementTime = null
  }
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.id = null
  form.name = null
  form.reimbursementTime = null
  form.totalFee = null
  form.paidFee = null
  form.amount = null
  form.ownerDepId = null
  form.status = "0"
  form.createBy = null
  form.createTime = null
  form.updateBy = null
  form.updateTime = null
  form.remark = null
  form.delFlag = null

  // 重置表单校验
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  reimbursementTimeRange.value = []
  queryParams.beginReimbursementTime = null
  queryParams.endReimbursementTime = null

  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset()
  open.value = true
  title.value = "添加报销项目"
}

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset()
  const id = row.id || ids.value[0]
  try {
    const response = await getSamReimbursementItems(id)
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改报销项目"
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

/** 提交按钮 */
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    if (form.id) {
      await updateSamReimbursementItems(form)
      ElMessage.success("修改成功")
    } else {
      await addSamReimbursementItems(form)
      ElMessage.success("新增成功")
    }
    open.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

/** 删除按钮操作 */
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  try {
    await ElMessageBox.confirm('是否确认删除报销项目编号为"' + deleteIds + '"的数据项？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await delSamReimbursementItems(deleteIds)
    getList()
    ElMessage.success("删除成功")
  } catch (error) {
    console.log('取消删除')
  }
}

/** 导出按钮操作 */
const handleExport = () => {
  const query = { ...queryParams }
  // 这里需要根据实际情况调整导出方法
  if (typeof download === 'function') {
    download('system/SamReimbursementItems/export', query, `SamReimbursementItems_${new Date().getTime()}.xlsx`)
  } else {
    ElMessage.warning('导出功能暂未实现')
  }
}

/** 导出PDF按钮操作 */
const handleExportPdf = async (row) => {
  try {
    await ElMessageBox.confirm('是否确认导出报销项目"' + row.name + '"的PDF清单？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    loading.value = true
    const token = userStore.token
    const url = import.meta.env.VITE_APP_BASE_API + '/system/SamReimbursementItems/exportPdf/' + row.id

    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })

      if (response.ok) {
        const blob = await response.blob()
        // 创建下载链接
        const downloadUrl = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = downloadUrl
        a.download = `报销清单_${row.name}_${new Date().getTime()}.pdf`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(downloadUrl)

        ElMessage.success("PDF导出成功")
      } else {
        throw new Error('下载失败')
      }
    } catch (error) {
      ElMessage.error("PDF导出失败：" + error.message)
    } finally {
      loading.value = false
    }
  } catch (error) {
    console.log('取消导出')
  }
}

// 获取字典数据
const getDictData = async () => {
  try {
    // 这里需要根据实际项目中的字典获取方法调整
    // 如果项目中有 useDict 方法，可以使用
    const { data } = await useDict('reimbursement_status')
    reimbursementStatusOptions.value = data
  } catch (error) {
    console.error('获取字典失败:', error)
    // 临时默认数据
    reimbursementStatusOptions.value = [
      { value: '0', label: '草稿' },
      { value: '1', label: '待审核' },
      { value: '2', label: '已通过' },
      { value: '3', label: '已拒绝' }
    ]
  }
}

// 初始化
onMounted(() => {
  getList()
  getDeptTree()
  getDictData()
})

// 注意：handleTree 函数需要从 '@utils/ruoyi' 导入
// 如果不存在，需要添加或实现
const handleTree = (data, id) => {
  // 简单的树形数据处理
  return data || []
}
</script>