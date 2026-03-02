<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="获奖等级" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
          <el-option
              v-for="dict in awardGradeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option
              v-for="dict in reimbursementCategoryOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
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
              v-for="dict in ratioStatusOptions"
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
            v-hasPermi="['system:SamReimbursementRatio:add']"
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
            v-hasPermi="['system:SamReimbursementRatio:edit']"
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
            v-hasPermi="['system:SamReimbursementRatio:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            :icon="Download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:SamReimbursementRatio:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SamReimbursementRatioList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="awardGradeOptions" :value="scope.row.grade"/>
        </template>
      </el-table-column>
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="reimbursementCategoryOptions" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="报销百分比" align="center" prop="ratio">
        <template #default="scope">
          <span>{{ scope.row.ratio }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="归属学院" align="center" prop="ownerDepId" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="ratioStatusOptions" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
              size="mini"
              type="text"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:SamReimbursementRatio:edit']"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:SamReimbursementRatio:remove']"
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

    <!-- 添加或修改报销比例对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择获奖等级">
            <el-option
                v-for="dict in awardGradeOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别">
            <el-option
                v-for="dict in reimbursementCategoryOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报销百分比" prop="ratio">
          <el-input-number v-model="form.ratio" :precision="0" :min="0" :max="100" :step="5" placeholder="请输入报销百分比" />
          <span style="margin-left: 10px;">%</span>
        </el-form-item>
        <el-form-item label="归属学院" prop="ownerDepId">
          <treeselect
              v-model="form.ownerDepId"
              :options="deptOptions"
              :normalizer="normalizer"
              placeholder="请选择归属学院（为空表示全校规则）"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in ratioStatusOptions"
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
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import {
  listSamReimbursementRatio,
  getSamReimbursementRatio,
  delSamReimbursementRatio,
  addSamReimbursementRatio,
  updateSamReimbursementRatio
} from "@/api/system/SamReimbursementRatio"
import Treeselect from "vue3-treeselect"
import "vue3-treeselect/dist/vue3-treeselect.css"
import { listDept } from "@/api/system/dept"

// 定义响应式数据
const loading = ref(false)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const SamReimbursementRatioList = ref([])
const title = ref('')
const open = ref(false)

// 字典选项
const awardGradeOptions = ref([])
const reimbursementCategoryOptions = ref([])
const ratioStatusOptions = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  grade: null,
  category: null,
  ownerDepId: null,
  status: null,
})

// 表单参数
const form = reactive({
  id: null,
  grade: null,
  category: null,
  ratio: null,
  ownerDepId: null,
  status: "1",
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
  grade: [
    { required: true, message: "获奖等级不能为空", trigger: "blur" }
  ]
})

// 表单ref
const formRef = ref(null)
const queryFormRef = ref(null)

/** 查询报销比例列表 */
const getList = async () => {
  loading.value = true
  try {
    const response = await listSamReimbursementRatio(queryParams)
    SamReimbursementRatioList.value = response.rows
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

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.id = null
  form.grade = null
  form.category = null
  form.ratio = null
  form.ownerDepId = null
  form.status = "1"
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
  title.value = "添加报销比例"
}

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset()
  const id = row.id || ids.value[0]
  try {
    const response = await getSamReimbursementRatio(id)
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改报销比例"
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
      await updateSamReimbursementRatio(form)
      ElMessage.success("修改成功")
    } else {
      await addSamReimbursementRatio(form)
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
    await ElMessageBox.confirm('是否确认删除报销比例编号为"' + deleteIds + '"的数据项？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await delSamReimbursementRatio(deleteIds)
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
    download('system/SamReimbursementRatio/export', query, `SamReimbursementRatio_${new Date().getTime()}.xlsx`)
  } else {
    ElMessage.warning('导出功能暂未实现')
  }
}

// 获取字典数据
const getDictData = async () => {
  try {
    // 这里需要根据实际项目中的字典获取方法调整
    // 如果项目中有 useDict 方法，可以使用
    const awardGrade = await useDict('award_grade')
    awardGradeOptions.value = awardGrade.data || []

    const reimbursementCategory = await useDict('reimbursement_category')
    reimbursementCategoryOptions.value = reimbursementCategory.data || []

    const ratioStatus = await useDict('ratio_status')
    ratioStatusOptions.value = ratioStatus.data || []
  } catch (error) {
    console.error('获取字典失败:', error)
    // 临时默认数据
    awardGradeOptions.value = [
      { value: '1', label: '一等奖' },
      { value: '2', label: '二等奖' },
      { value: '3', label: '三等奖' }
    ]
    reimbursementCategoryOptions.value = [
      { value: '1', label: '会议' },
      { value: '2', label: '交通' },
      { value: '3', label: '住宿' }
    ]
    ratioStatusOptions.value = [
      { value: '0', label: '禁用' },
      { value: '1', label: '启用' }
    ]
  }
}

// 初始化
onMounted(() => {
  getList()
  getDeptTree()
  getDictData()
})

// 注意：handleTree 函数需要从 '@/utils/ruoyi' 导入
// 如果不存在，需要添加或实现
const handleTree = (data, id) => {
  // 简单的树形数据处理
  return data || []
}
</script>