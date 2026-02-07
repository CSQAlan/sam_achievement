<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="届次" prop="session">
        <el-input v-model="queryParams.session" placeholder="请输入届次" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择赛事类别" clearable>
          <el-option v-for="dict in sys_competition_category" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="盖章单位" prop="organizations">
        <el-input v-model="queryParams.organizations" placeholder="请输入盖章单位" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择赛事级别" clearable>
          <el-option v-for="dict in sys_competition_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in sys_competition_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事名称" prop="competitionId">
        <el-select v-model="queryParams.competitionId" placeholder="请选择赛事名称" clearable>
          <el-option v-for="comp in competitionList" :key="comp.id" :label="comp.name" :value="comp.id" />
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
          v-hasPermi="['session:session:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['session:session:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['session:session:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['session:session:export']">导出</el-button>
      </el-col>
      <!-- 新增：导出模板按钮 -->
      <el-col :span="1.8">
        <el-button type="info" plain icon="DocumentCopy" @click="handleExportTemplate"
          v-hasPermi="['session:session:exportTemplate']">导出模板</el-button>
      </el-col>
      <!-- 新增：批量导入按钮 -->
      <el-col :span="1.8">
        <el-button type="primary" plain icon="UploadFilled" @click="handleImportOpen"
          v-hasPermi="['session:session:import']">批量导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sessionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="赛事名称" align="center" prop="competitionId">
        <template #default="scope">
          {{ getCompetitionName(scope.row.competitionId) || scope.row.competitionId }}
        </template>
      </el-table-column>
      <el-table-column label="届次" align="center" prop="session" />
      <el-table-column label="赛事类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="sys_competition_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="盖章单位" align="center" prop="organizations" />
      <el-table-column label="赛事级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="sys_competition_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="赛事标签" align="center" prop="tags">
        <template #default="scope">
          <dict-tag :options="sys_competition_tag" :value="scope.row.tags ? scope.row.tags.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_competition_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['session:session:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['session:session:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改赛事届次对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="sessionRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="赛事名称" prop="competitionId">
          <el-select v-model="form.competitionId" placeholder="请选择赛事名称" clearable style="width:100%;">
            <el-option v-for="comp in competitionList" :key="comp.id" :label="comp.name" :value="comp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="届次" prop="session">
          <el-input v-model="form.session" placeholder="请输入届次" />
        </el-form-item>
        <el-form-item label="赛事类别" prop="category">
          <el-radio-group v-model="form.category">
            <el-radio v-for="dict in sys_competition_category" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="盖章单位" prop="organizations">
          <el-input v-model="form.organizations" placeholder="请输入盖章单位" />
        </el-form-item>
        <el-form-item label="赛事级别" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio v-for="dict in sys_competition_level" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_competition_status" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="赛事标签" prop="tags">
          <el-checkbox-group v-model="form.tags">
            <el-checkbox v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增：批量导入弹窗 -->
    <el-dialog title="批量导入赛事届次" v-model="importOpen" width="400px" append-to-body>
      <el-form ref="importRef" :model="importForm" label-width="80px">
        <el-form-item label="Excel文件" prop="file">
          <!-- 若依通用文件上传组件，适配Excel导入 -->
          <el-upload ref="uploadRef" :auto-upload="false" :on-change="handleFileChange" :file-list="fileList"
            accept=".xlsx,.xls" drag style="width:100%;">
            <i class="el-icon-upload" />
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">仅支持.xlsx/.xls格式文件，且需按模板填写</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="导入模式">
          <!-- 新增：更新模式选择，对应后端updateSupport参数 -->
          <el-radio-group v-model="importForm.updateSupport" style="width:100%;">
            <el-radio label="false" border style="width:70%;">仅新增（存在则跳过）</el-radio>
            <el-radio label="true" border style="width:70%;">覆盖更新（存在则修改）</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleImportClose">取 消</el-button>
          <el-button type="primary" @click="handleImportSubmit" :disabled="!fileList.length">确 定 导 入</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Session">
import { listSession, getSession, delSession, addSession, updateSession } from "@/api/session/session"
// 核心新增：导入/导出模板接口（和后端Controller接口对应）
import { exportTemplateSession, importDataSession } from "@/api/session/session"
import { listCompetition } from "@/api/competition/competition"

const { proxy } = getCurrentInstance()
const { sys_competition_tag, sys_competition_status, sys_competition_del_flag, sys_competition_category, sys_competition_level } = proxy.useDict('sys_competition_tag', 'sys_competition_status', 'sys_competition_del_flag', 'sys_competition_category', 'sys_competition_level')

const sessionList = ref([])
const tagList = ref([])
const competitionList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedTag = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 新增：导入相关响应式变量
const importOpen = ref(false) // 导入弹窗开关
const importRef = ref(null)   // 导入表单ref
const uploadRef = ref(null)   // 文件上传组件ref
const fileList = ref([])      // 上传文件列表
// 导入表单：updateSupport对应后端参数（是否更新已存在数据）
const importForm = reactive({
  updateSupport: "false" // 默认仅新增
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    competitionId: null,
    session: null,
    category: null,
    organizations: null,
    level: null,
    tags: null,
    status: null,
  },
  rules: {
    competitionId: [
      { required: true, message: "赛事名称不能为空", trigger: "change" }
    ],
    session: [
      { required: true, message: "届次不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "赛事类别不能为空", trigger: "change" }
    ],
    organizations: [
      { required: true, message: "盖章单位不能为空", trigger: "blur" }
    ],
    level: [
      { required: true, message: "赛事级别不能为空", trigger: "change" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取赛事主表列表并去重
function getCompetitionList() {
  listCompetition({}).then(response => {
    const originList = response.rows || response.data || []
    const uniqueMap = new Map()
    originList.forEach(item => {
      if (item.name && !uniqueMap.has(item.name)) {
        uniqueMap.set(item.name, item)
      }
    })
    competitionList.value = Array.from(uniqueMap.values())
  }).catch(error => {
    console.error("获取赛事主表列表失败：", error)
    proxy.$modal.msgError("获取赛事列表失败，请先添加赛事主表数据")
  })
}

// 根据赛事ID获取名称
function getCompetitionName(competitionId) {
  if (!competitionId) return ''
  const target = competitionList.value.find(item => item.id == competitionId)
  return target?.name || ''
}

/** 查询赛事届次列表 */
function getList() {
  loading.value = true
  listSession(queryParams.value).then(response => {
    sessionList.value = response.rows
    total.value = response.total
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
    competitionId: null,
    session: null,
    category: null,
    organizations: null,
    level: null,
    tags: [],
    status: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  tagList.value = []
  proxy.resetForm("sessionRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
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
  getCompetitionList()
  open.value = true
  title.value = "添加赛事届次"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getCompetitionList()
  const _id = row.id || ids.value
  getSession(_id).then(response => {
    form.value = response.data
    if (form.value.tags && typeof form.value.tags === 'string') {
      form.value.tags = form.value.tags.split(',')
    } else {
      form.value.tags = []
    }
    tagList.value = response.data.tagList
    open.value = true
    title.value = "修改赛事届次"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["sessionRef"].validate(valid => {
    if (valid) {
      form.value.tags = form.value.tags.join(",")
      if (form.value.id != null) {
        updateSession(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSession(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除赛事届次编号为"' + _ids + '"的数据项？').then(function () {
    return delSession(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 标签序号 */
function rowTagIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 标签添加按钮操作 */
function handleAddTag() {
  let obj = {}
  obj.tagName = ""
  obj.depId = ""
  tagList.value.push(obj)
}

/** 标签删除按钮操作 */
function handleDeleteTag() {
  if (checkedTag.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的标签数据")
  } else {
    const tags = tagList.value
    const checkedTags = checkedTag.value
    tagList.value = tags.filter(function (item) {
      return checkedTags.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleTagSelectionChange(selection) {
  checkedTag.value = selection.map(item => item.index)
}

/** 原有导出按钮操作（导出已有数据） */
function handleExport() {
  proxy.download('session/session/export', {
    ...queryParams.value
  }, `session_${new Date().getTime()}.xlsx`)
}

// ========== 新增：导出模板核心方法 ==========
function handleExportTemplate() {
  proxy.download('session/session/exportTemplate', {}, `赛事届次导入模板_${new Date().getTime()}.xlsx`)
}

// ========== 新增：批量导入相关方法 ==========
// 打开导入弹窗
function handleImportOpen() {
  importOpen.value = true
  // 重置上传状态
  fileList.value = []
  importForm.updateSupport = "false"
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 关闭导入弹窗
function handleImportClose() {
  importOpen.value = false
  fileList.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 监听文件选择变化
function handleFileChange(file) {
  // 只保留最后一个选择的文件
  fileList.value = [file]
}

// 提交导入操作（核心）
function handleImportSubmit() {
  if (!fileList.value.length) {
    proxy.$modal.msgWarning("请选择要导入的Excel文件！")
    return
  }
  // 构造FormData（适配文件上传）
  const formData = new FormData()
  formData.append("file", fileList.value[0].raw) // 文件流
  formData.append("updateSupport", importForm.updateSupport) // 导入模式
  // 调用导入接口
  importDataSession(formData).then(response => {
    proxy.$modal.msgSuccess(response.msg || "导入成功！")
    handleImportClose()
    getList() // 刷新列表
    getCompetitionList()
  }).catch(error => {
    proxy.$modal.msgError(error.msg || "导入失败，请检查文件格式和内容！")
  })
}

// 页面初始化
getList()
getCompetitionList()
</script>