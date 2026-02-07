<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入赛事名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="盖章单位" prop="organizations">
        <el-input v-model="queryParams.organizations" placeholder="请输入盖章单位" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择赛事类别" clearable>
          <el-option v-for="dict in sys_competition_category" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择赛事级别" clearable>
          <el-option v-for="dict in sys_competition_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <el-select v-model="queryParams.tags" placeholder="请选择赛事标签" clearable>
          <el-option v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="适用范围" prop="scopeType">
        <el-select v-model="queryParams.scopeType" placeholder="请选择适用范围" clearable>
          <el-option v-for="dict in sys_competition_scope_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in sys_competition_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['competition:competition:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['competition:competition:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['competition:competition:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['competition:competition:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 展示表格 -->
    <el-table v-loading="loading" :data="competitionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="赛事主键" align="center" prop="id" /> -->
      <el-table-column label="赛事名称" align="center" prop="name" />
      <el-table-column label="赛事类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="sys_competition_category"
            :value="scope.row.category ? scope.row.category.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="盖章单位" align="center" prop="organizations" />
      <el-table-column label="赛事级别" width="130" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="sys_competition_level" :value="scope.row.level ? scope.row.level.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="赛事标签" width="150" align="center" prop="tags">
        <template #default="scope">
          <dict-tag :options="sys_competition_tag" :value="scope.row.tags ? scope.row.tags.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="适用范围" align="center" prop="scopeType">
        <template #default="scope">
          <dict-tag :options="sys_competition_scope_type" :value="scope.row.scopeType" />
        </template>
      </el-table-column>
      <!-- 【新增】归属学院列：核心展示拼接后的学院名 -->
      <el-table-column label="归属学院" align="center" min-width="100">
        <template #default="scope">
          {{ getBelongDeptName(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_competition_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="赛事说明" width="100" align="center" prop="memo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['competition:competition:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['competition:competition:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改总赛事对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="competitionRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="赛事名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入赛事名称" />
        </el-form-item>
        <el-form-item label="赛事类别" prop="category">
          <el-checkbox-group v-model="form.category">
            <el-checkbox v-for="dict in sys_competition_category" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="盖章单位" prop="organizations">
          <el-input v-model="form.organizations" placeholder="请输入盖章单位" />
        </el-form-item>
        <el-form-item label="赛事级别" prop="level">
          <el-checkbox-group v-model="form.level">
            <el-checkbox v-for="dict in sys_competition_level" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="赛事标签" prop="tags">
          <el-checkbox-group v-model="form.tags">
            <el-checkbox v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="适用范围" prop="scopeType">
          <el-radio-group v-model="form.scopeType">
            <el-radio v-for="dict in sys_competition_scope_type" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_competition_status" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="赛事说明" prop="memo">
          <el-input v-model="form.memo" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <!-- <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item> -->
        <el-divider content-position="center">赛事-部门关系信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddCompetitionDeptRel"
              :disabled="form.scopeType === '0'">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteCompetitionDeptRel"
              :disabled="form.scopeType === '0' || !checkedCompetitionDeptRel.length">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="competitionDeptRelList" :row-class-name="rowCompetitionDeptRelIndex"
          @selection-change="handleCompetitionDeptRelSelectionChange" ref="competitionDeptRel">
          <el-table-column type="selection" width="50" align="center" :disabled="form.scopeType === '0'" />
          <el-table-column label="序号" align="center" prop="index" width="50" />
          <el-table-column label="学院" align="center" prop="deptId" width="360">
            <template #default="scope">
              <el-select v-model="scope.row.deptId" placeholder="请选择学院" clearable style="width:100%;"
                :disabled="form.scopeType === '0'">
                <el-option v-for="dept in deptList" :key="dept.deptId" :label="dept.deptName" :value="dept.deptId" />
              </el-select>
            </template>
          </el-table-column>
          <!-- <el-table-column label="排序" prop="sort" width="150">
            <template #default="scope">
              <el-input v-model.number="scope.row.sort" placeholder="请输入排序" :disabled="form.scopeType === '0'" />
            </template>
          </el-table-column> -->
        </el-table>
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

<script setup name="Competition">
import { listCompetition, getCompetition, delCompetition, addCompetition, updateCompetition } from "@/api/competition/competition"
import { listDept } from "@/api/system/dept"
// 【新增】引入ref，已有可忽略
import { ref } from 'vue'

const { proxy } = getCurrentInstance()
const { sys_competition_level, sys_competition_tag, sys_competition_status, sys_competition_del_flag, sys_competition_scope_type, sys_competition_category } = proxy.useDict('sys_competition_level', 'sys_competition_tag', 'sys_competition_status', 'sys_competition_del_flag', 'sys_competition_scope_type', 'sys_competition_category')

const competitionList = ref([])
const competitionDeptRelList = ref([])
const deptList = ref([])
// 【新增】学院键值对映射：{deptId: deptName}，快速通过id匹配名称
const deptNameMap = ref({})
// 【新增】赛事-学院关系列表：缓存所有赛事的关联学院数据，避免重复查询
const competitionDeptAllList = ref([])

const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedCompetitionDeptRel = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    category: null,
    organizations: null,
    level: null,
    tags: null,
    scopeType: null,
    status: null,
  },
  rules: {
    name: [
      { required: true, message: "赛事名称不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "赛事类别不能为空", trigger: "blur" }
    ],
    organizations: [
      { required: true, message: "盖章单位不能为空", trigger: "blur" }
    ],
    level: [
      { required: true, message: "赛事级别不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 【修改】获取学院列表 + 构建学院名键值对映射
function getDeptList() {
  deptList.value = []
  deptNameMap.value = {} // 清空旧映射
  listDept({}).then(response => {
    console.log("部门接口返回完整数据：", response)
    deptList.value = response.rows || response.data || []
    // 【新增】构建deptId -> deptName的映射，快速匹配
    deptList.value.forEach(dept => {
      deptNameMap.value[dept.deptId] = dept.deptName
    })
    console.log("学院名映射：", deptNameMap.value)
    console.log("最终学院列表：", deptList.value)
  }).catch(error => {
    console.error("获取学院列表失败：", error)
    proxy.$modal.msgError("获取学院列表失败，请检查接口权限或后台数据")
  })
}

// 【新增】核心方法：根据赛事行数据，获取并拼接归属学院名
function getBelongDeptName(row) {
  if (row.scopeType === '0') {
    return '全校'
  }
  // 关键修改：把 row.dept_ids 改为 row.deptIds
  let relateDeptIds = []
  if (row.deptIds) {
    if (typeof row.deptIds === 'string') {
      relateDeptIds = row.deptIds.split(',').filter(id => id)
    } else if (Array.isArray(row.deptIds)) {
      relateDeptIds = row.deptIds
    }
  }
  const deptNames = relateDeptIds
    .filter(deptId => deptId && deptNameMap.value[deptId])
    .map(deptId => deptNameMap.value[deptId])
  return deptNames.length > 0 ? deptNames.join('、') : '未配置'
}

/** 【修改】查询总赛事列表：新增缓存全量赛事-学院关系 */
function getList() {
  loading.value = true
  listCompetition(queryParams.value).then(response => {
    competitionList.value = response.rows
    total.value = response.total
    // 【新增】缓存全量赛事-学院关系（若后台接口返回了全量数据）
    competitionList.value.forEach(item => {
      if (item.competitionDeptRelList) {
        competitionDeptAllList.value.push(...item.competitionDeptRelList)
      }
    })
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    category: [],
    organizations: null,
    level: [],
    tags: [],
    scopeType: '0',
    status: null,
    memo: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    // remark: null,
    delFlag: null
  }
  competitionDeptRelList.value = []
  proxy.resetForm("competitionRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  // 【新增】搜索时清空旧的关系缓存
  competitionDeptAllList.value = []
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getDeptList()
  open.value = true
  title.value = "添加总赛事"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getDeptList()
  const _id = row.id || ids.value
  getCompetition(_id).then(response => {
    form.value = response.data
    form.value.category = form.value.category?.split(",").filter(v => v) || []
    form.value.level = form.value.level?.split(",").filter(v => v) || []
    form.value.tags = form.value.tags?.split(",").filter(v => v) || []
    competitionDeptRelList.value = response.data.competitionDeptRelList || []
    open.value = true
    title.value = "修改总赛事"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["competitionRef"].validate(valid => {
    if (valid) {
      form.value.category = form.value.category.join(",")
      form.value.level = form.value.level.join(",")
      form.value.tags = form.value.tags.join(",")
      // 全校时清空子表，避免提交无效数据
      if (form.value.scopeType === '0') {
        competitionDeptRelList.value = []
      }
      form.value.competitionDeptRelList = competitionDeptRelList.value
      if (form.value.id != null) {
        updateCompetition(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCompetition(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除总赛事编号为"' + _ids + '"的数据项？').then(function () {
    return delCompetition(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 赛事-部门关系序号 */
function rowCompetitionDeptRelIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 赛事-部门关系添加按钮操作 */
function handleAddCompetitionDeptRel() {
  let obj = {}
  obj.deptId = ""
  obj.sort = ""
  // obj.remark = ""
  competitionDeptRelList.value.push(obj)
}

/** 赛事-部门关系删除按钮操作 */
function handleDeleteCompetitionDeptRel() {
  if (checkedCompetitionDeptRel.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的赛事-部门关系数据")
  } else {
    const competitionDeptRels = competitionDeptRelList.value
    const checkedCompetitionDeptRels = checkedCompetitionDeptRel.value
    competitionDeptRelList.value = competitionDeptRels.filter(function (item) {
      return checkedCompetitionDeptRels.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleCompetitionDeptRelSelectionChange(selection) {
  checkedCompetitionDeptRel.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('competition/competition/export', {
    ...queryParams.value
  }, `competition_${new Date().getTime()}.xlsx`)
}

// 页面初始化加载
getList()
getDeptList()
</script>