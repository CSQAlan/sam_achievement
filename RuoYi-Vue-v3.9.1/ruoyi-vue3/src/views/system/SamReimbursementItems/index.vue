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
      <!-- Element UI 日期选择器 -->
<el-form-item label="报销时间" prop="reimbursementTime">
  <el-date-picker
    v-model="queryParams.reimbursementTime"  
    type="date"
    placeholder="选择日期"
    format="yyyy-MM-dd"
    value-format="yyyy-MM-dd"  
    :clearable="false">
  </el-date-picker>
</el-form-item>
      <el-form-item label="归属学院" prop="ownerDepId" style="width: 570px;" >
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
      <el-table-column label="报销项目名称" align="center" prop="name" width="200">
        <template #default="scope">
          <el-link 
            type="primary" 
            @click="handleViewDetail(scope.row)"
            :underline="false"
          >
      {{ scope.row.name }}
    </el-link>
  </template>
</el-table-column>
      <el-table-column label="届次" align="center" prop="sessionName" width="150">
        <template #default="scope">
          <span>{{ scope.row.sessionName || scope.row.sessionId || '-' }}</span>
        </template>
      </el-table-column>
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
      <el-table-column label="归属学院" align="center" prop="ownerDepId">
        <template #default="scope">
          <span>{{ getDeptName(scope.row.ownerDepId) }}</span>
        </template>
        </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="reimbursementStatusOptions" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <!-- 新增的关联成果按钮
          <el-button
             size="mini"
             type="primary"
             :icon="Document"
             @click="viewAchievements(scope.row)"
             v-hasPermi="['system:SamReimbursementItems:view']"
          >详细信息</el-button>
          -->
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
        <el-form-item label="所属届次" prop="sessionId">
          <el-select v-model="form.sessionId" placeholder="请选择届次">
            <el-option
              v-for="item in sessionOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="reimbursementTime">
  <el-date-picker
    v-model="form.reimbursementTime"
    type="date"
    placeholder="请选择创建时间"
    format="yyyy-MM-dd"
    value-format="yyyy-MM-dd"
    :clearable="false">
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download, Document, Calendar } from '@element-plus/icons-vue'
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
import { listSession } from "@/api/session/session"
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
const router = useRouter()  // 需要创建实例

// 字典选项
const reimbursementStatusOptions = ref([])

// 届次选项
const sessionOptions = ref([])

// 日期范围处理
const reimbursementTimeRange = ref([])

// 查看关联成果
const viewAchievements = (row) => {
 

  // 使用完整的路径跳转
  router.push({
    path: '/reimbursement/achievement',
    query: { 
      reimbursementId: row.id,
      reimbursementName: row.name 
    }
  })
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  sessionId: null,
  ownerDepId: null,
  status: null,
  beginReimbursementTime: null,
  endReimbursementTime: null
})

// 表单参数
const form = reactive({
  id: null,
  name: null,
  sessionId: null,
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

// 选中的比赛和届次信息
const selectedCompetition = ref({
  name: '中国大学生计算机设计大赛'
})
const selectedSession = ref({
  session: '2026届'
})

// 表单校验规则
const rules = reactive({
  name: [
    { required: true, message: "报销项目名称不能为空", trigger: "blur" }
  ],
  sessionId: [
    { required: true, message: "所属届次不能为空", trigger: "blur" }
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
    console.log('部门树原始数据:', response)
    
    // 处理可能的数据结构
    let deptData = []
    if (response.data) {
      deptData = response.data
    } else if (response.rows) {
      deptData = response.rows
    } else if (Array.isArray(response)) {
      deptData = response
    }
    
    console.log('处理前的部门数据:', deptData)
    
    // 关键修复：使用 handleTree 处理数据
    deptOptions.value = handleTree(deptData, "deptId")
    console.log('处理后的deptOptions:', deptOptions.value)
    
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.error('获取部门列表失败')
    deptOptions.value = []
  }
}

/** 获取届次列表 */
const getSessionList = async () => {
  try {
    const response = await listSession()
    console.log('届次列表数据:', response)
    
    // 处理可能的数据结构
    let sessionData = []
    if (response.data) {
      sessionData = response.data
    } else if (response.rows) {
      sessionData = response.rows
    } else if (Array.isArray(response)) {
      sessionData = response
    }
    
    // 转换为下拉选项格式
    sessionOptions.value = sessionData.map(item => ({
      id: item.id,
      label: item.session
    }))
    console.log('处理后的sessionOptions:', sessionOptions.value)
    
  } catch (error) {
    console.error('获取届次列表失败:', error)
    ElMessage.error('获取届次列表失败')
    sessionOptions.value = []
  }
}

/** 转换部门数据结构 */
const normalizer = (node) => {
  console.log('normalizer处理节点:', node)
  
  // 如果节点没有children或children为空，删除children属性
  if (node.children && !node.children.length) {
    delete node.children
  }
  
  // 返回treeselect需要的格式
  return {
    id: node.deptId || node.id,
    label: node.deptName || node.label || '未知',
    children: node.children
  }
}

/** 格式化金额 */
const formatMoney = (money) => {
  if (money == null) return '¥0.00'
  return '¥' + parseFloat(money).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

/** 根据部门ID获取部门名称 */
const getDeptName = (deptId) => {
  console.log('正在查找部门ID:', deptId);
  console.log('当前deptOptions:', deptOptions.value);
  
  if (!deptId || !deptOptions.value) {
    console.log('deptOptions为空或deptId为空');
    return deptId;
  }
  
  console.log('deptOptions长度:', deptOptions.value.length);
  
  // 递归查找部门名称
  const findDeptName = (nodes, id) => {
    for (const node of nodes) {
      console.log('检查节点:', node);
      if (node.id === id) {
        console.log('找到匹配:', node.label);
        return node.label;
      }
      if (node.children && node.children.length > 0) {
        const found = findDeptName(node.children, id);
        if (found) return found;
      }
    }
    return null;
  }
  
  const deptName = findDeptName(deptOptions.value, deptId);
  console.log('最终结果:', deptName || deptId);
  return deptName || deptId;
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
  form.sessionId = null
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
  // reimbursementTimeRange.value = []  // 注释掉这行，或者删除
  queryParams.reimbursementTime = null  // 添加这行
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
    
    // 添加日期格式验证和处理
    if (form.reimbursementTime) {
      // 确保日期格式为 yyyy-MM-dd
      if (typeof form.reimbursementTime === 'string') {
        // 检查是否是 yyyy-03-Mo 这种格式
        if (/[A-Za-z]/.test(form.reimbursementTime)) {
          // 如果是特殊格式，转换为有效日期
          const match = form.reimbursementTime.match(/(\d{4})-(\d{2})-/);
          if (match) {
            const year = match[1];
            const month = match[2];
            // 默认为当月第一天
            form.reimbursementTime = `${year}-${month}-01`;
          } else {
            // 如果解析失败，使用当前日期
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const day = String(today.getDate()).padStart(2, '0');
            form.reimbursementTime = `${year}-${month}-${day}`;
          }
        }
      }
    }
    
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

// 跳转到详情页，并传递项目ID
const handleViewDetail = (row) => {
  // 🔴 关键：先打印 row 对象，看看ID字段叫什么
  console.log('=== 点击的项目数据 ===');
  console.log('完整row:', row);
  console.log('row的所有字段:', Object.keys(row));
  
  // 尝试找出ID字段
  const projectId = row.itemId || row.id || row.reimbursementItemId;
  console.log('找到的项目ID:', projectId);
  
  if (!projectId) {
    console.error('错误：找不到项目ID！');
    alert('无法获取项目ID，请检查数据');
    return;
  }
  
  // 跳转时传递ID和届次ID
  router.push({
    path: '/reimbursement/Reimbursement',
    query: {
      reimbursementItemId: projectId,
      name: row.name,
      sessionId: row.sessionId
    }
  });
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
      { value: '0', label: '已报销' },
      { value: '1', label: '未报销' }
     
    ]
  }
}

// 初始化
onMounted(() => {
  getList()
  getDeptTree()
  getSessionList()
  getDictData()
})

/** 处理树形结构数据 */
const handleTree = (data, id) => {
  if (!data) return []
  
  // 将数据转换为 treeselect 需要的格式
  const convertToTreeSelect = (nodes) => {
    return nodes.map(node => ({
      id: node.deptId,      // 使用 deptId 作为 id
      label: node.deptName,  // 使用 deptName 作为 label
      children: node.children ? convertToTreeSelect(node.children) : undefined
    }))
  }
  
  return convertToTreeSelect(data)
}
</script>

<style scoped>
/* 比赛信息样式 */
.competition-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.competition-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.session-info {
  margin-top: 10px;
}

.session-text {
  font-size: 18px;
  font-weight: 500;
  margin-left: 5px;
}

/* 调整届次标签样式 */
.el-tag--medium {
  font-size: 18px;
  padding: 6px 12px;
}

/* 调整届次图标大小 */
.el-icon {
  font-size: 18px;
}
</style>