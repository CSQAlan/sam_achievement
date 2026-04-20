<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="年份" prop="year">
        <el-date-picker
            v-model="queryParams.year"
            type="year"
            value-format="yyyy"
            placeholder="请选择年份"
            clearable>
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
            v-hasPermi="['system:SamQualityDevelopmentForm:add']"
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
            v-hasPermi="['system:SamQualityDevelopmentForm:edit']"
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
            v-hasPermi="['system:SamQualityDevelopmentForm:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            :icon="Download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:SamQualityDevelopmentForm:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SamQualityDevelopmentFormList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="年份" align="center" prop="year" />
      <el-table-column label="统计成果数" align="center" prop="amount" />
      <el-table-column label="归属学院" align="center" prop="ownerDepId">
        <template #default="scope">
         <span>{{ getDeptName(scope.row.ownerDepId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="生成时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
              size="mini"
              type="text"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:SamQualityDevelopmentForm:edit']"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:SamQualityDevelopmentForm:remove']"
          >删除</el-button>
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

    <!-- 添加或修改素质提升对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="年份" prop="year">
          <el-date-picker
              v-model="form.year"
              type="year"
              value-format="yyyy"
              placeholder="请选择年份">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="统计成果数" prop="amount">
          <el-input-number v-model="form.amount" :precision="0" :min="0" placeholder="统计成果数" />
        </el-form-item>
        <el-form-item label="归属学院" prop="ownerDepId">
          <treeselect
              v-model="form.ownerDepId"
              :options="deptOptions"
              :normalizer="normalizer"
              placeholder="请选择归属学院（为空表示全校统计）"
          />
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
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import {
  listSamQualityDevelopmentForm,
  getSamQualityDevelopmentForm,
  delSamQualityDevelopmentForm,
  addSamQualityDevelopmentForm,
  updateSamQualityDevelopmentForm
} from "@/api/system/SamQualityDevelopmentForm"
import Treeselect from "vue3-treeselect"
import "vue3-treeselect/dist/vue3-treeselect.css"
import { listDept } from "@/api/system/dept"

// 定义响应式数据
const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const SamQualityDevelopmentFormList = ref([])
const title = ref('')
const open = ref(false)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  year: null,
  ownerDepId: null,
})

// 表单参数
const form = reactive({
  id: null,
  year: null,
  amount: null,
  ownerDepId: null,
  createTime: null
})

// 部门树选项
const deptOptions = ref([])

// 表单校验规则
const rules = reactive({
  year: [
    { required: true, message: "年份不能为空", trigger: "blur" }
  ],
  amount: [
    { required: true, message: "统计成果数不能为空", trigger: "blur" }
  ]
})

// 表单ref
const formRef = ref(null)
const queryFormRef = ref(null)

/** 查询素质提升列表 */
const getList = async () => {
  loading.value = true
  try {
    const response = await listSamQualityDevelopmentForm(queryParams)
    SamQualityDevelopmentFormList.value = response.rows
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
    console.log('原始部门数据:', response.data)
    
    // 直接格式化
    deptOptions.value = handleTree(response.data, "deptId")
    console.log('最终deptOptions:', deptOptions.value)
    
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

/** 转换部门数据结构 - 用于 treeselect */
const normalizer = (node) => {
  return {
    id: node.id,
    label: node.label,
    children: node.children
  }
}

/** 处理树形结构数据 - 专门为 treeselect 格式化 */
const handleTree = (data, id) => {
  if (!data || !Array.isArray(data)) return []
  
  // treeselect 需要的格式：{ id, label, children }
  const formatTreeData = (nodes) => {
    return nodes.map(node => {
      // 基础节点
      const treeNode = {
        id: node.deptId,      // 必须
        label: node.deptName,  // 必须
      }
      
      // 如果有子节点且子节点数组不为空，递归处理
      if (node.children && Array.isArray(node.children) && node.children.length > 0) {
        treeNode.children = formatTreeData(node.children)
      }
      
      return treeNode
    })
  }
  
  const formattedData = formatTreeData(data)
  console.log('treeselect 格式化后的数据:', formattedData)
  return formattedData
}

/** 根据部门ID获取部门名称 */
const getDeptName = (deptId) => {
  if (!deptId || !deptOptions.value) return deptId
  
  const findDeptName = (nodes, id) => {
    for (const node of nodes) {
      if (Number(node.id) === Number(id)) {
        return node.label
      }
      if (node.children && node.children.length > 0) {
        const found = findDeptName(node.children, id)
        if (found) return found
      }
    }
    return null
  }
  
  const deptName = findDeptName(deptOptions.value, deptId)
  return deptName || deptId
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.id = null
  form.year = null
  form.amount = null
  form.ownerDepId = null
  form.createTime = null
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
  title.value = "添加素质提升"
}

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset()
  const id = row.id || ids.value[0]
  try {
    const response = await getSamQualityDevelopmentForm(id)
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改素质提升"
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
      await updateSamQualityDevelopmentForm(form)
      ElMessage.success("修改成功")
    } else {
      await addSamQualityDevelopmentForm(form)
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
    await ElMessageBox.confirm('是否确认删除素质提升编号为"' + deleteIds + '"的数据项？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await delSamQualityDevelopmentForm(deleteIds)
    getList()
    ElMessage.success("删除成功")
  } catch (error) {
    console.log('取消删除')
  }
}

/** 导出按钮操作 */
const handleExport = () => {
  // 注意：这里需要根据实际情况调整导出方法
  const query = { ...queryParams }
  // 假设有一个 download 方法
  if (typeof download === 'function') {
    download('system/SamQualityDevelopmentForm/export', query, `SamQualityDevelopmentForm_${new Date().getTime()}.xlsx`)
  } else {
    ElMessage.warning('导出功能暂未实现')
  }
}

// 初始化
onMounted(() => {
  getList()
  getDeptTree()
})
</script>