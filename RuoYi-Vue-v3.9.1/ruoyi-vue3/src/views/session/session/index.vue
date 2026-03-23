<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="届次" prop="session">
        <el-input v-model="queryParams.session" placeholder="请输入届次" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="年份" prop="year">
        <el-input-number v-model="queryParams.year" :min="2000" :max="2100" controls-position="right"
          placeholder="请输入年份" style="width: 140px" />
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
        <el-select v-model="queryParams.competitionId" placeholder="请选择赛事名称" clearable filterable>
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
      <el-table-column label="赛事名称" align="center" prop="competitionName">
        <template #default="scope">
          {{ scope.row.competitionName || getCompetitionName(scope.row.competitionId) || scope.row.competitionId }}
        </template>
      </el-table-column>
      <el-table-column label="届次" align="center" prop="session" />
      <el-table-column label="年份" align="center" prop="year" width="100" />
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
    <el-dialog :title="title" v-model="open" width="1100px" append-to-body>
      <el-form ref="sessionRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="11">
            <el-form-item label="赛事名称" prop="competitionId">
              <el-select v-model="form.competitionId" placeholder="请选择赛事名称" clearable filterable style="width:100%;">
                <el-option v-for="comp in competitionList" :key="comp.id" :label="comp.name" :value="comp.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="年份" prop="year">
              <el-input-number v-model="form.year" :min="2000" :max="2100" controls-position="right"
                placeholder="请输入年份" style="width: 180px" />
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
          </el-col>

          <el-col :span="13">
            <div class="attach-card">
              <el-divider content-position="left">参赛通知（PDF）</el-divider>

              <el-form-item label-width="0" prop="uuid">
                <upload-file v-if="!form.uuid" v-model="form.uuid" :limit="1" :fileSize="50" :fileType="['pdf']"
                  class="hide-file-list" @update:modelValue="handleNoticeUuidChange" />
              </el-form-item>

              <div v-if="form.uuid" class="custom-file-row">
                <div class="file-name">
                  <el-icon class="mr5"><Document /></el-icon>
                  <span>{{ form.uuid }}</span>
                </div>
                <div class="file-action">
                  <el-button link type="primary" :icon="View" @click="handleOpenNoticeDetail(form.uuid)">预览</el-button>
                  <el-button link type="primary" :icon="Download" @click="handleDownloadNotice(form.uuid)">下载</el-button>
                  <el-button link type="danger" :icon="Delete" @click="handleDeleteNotice">删除</el-button>
                </div>
              </div>

              <div v-if="noticePreviewUrl" class="preview-box">
                <iframe :src="noticePreviewUrl" width="100%" height="520px" frameborder="0"></iframe>
              </div>
            </div>
          </el-col>
        </el-row>
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
import request from "@/utils/request"
import UploadFile from "@/components/FileUpload"
import { Delete, Document, Download, View } from "@element-plus/icons-vue"

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
    year: null,
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
    year: [
      { required: true, message: "年份不能为空", trigger: "change" }
    ],
    uuid: [
      { required: true, message: "参赛通知不能为空（仅PDF）", trigger: "change" }
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

const noticePreviewUrl = ref("")

function normalizeUuid(value) {
  if (Array.isArray(value)) return normalizeUuid(value[0])
  if (value === null || value === undefined) return ""
  const normalized = String(value).trim()
  if (!normalized) return ""
  return normalized.includes(",") ? normalized.split(",")[0].trim() : normalized
}

function revokeNoticePreview() {
  if (noticePreviewUrl.value) {
    window.URL.revokeObjectURL(noticePreviewUrl.value)
    noticePreviewUrl.value = ""
  }
}

async function fetchPdfObjectUrl(uuid) {
  const blob = await request({
    url: "/attachment/download",
    method: "get",
    params: { resource: uuid },
    responseType: "blob",
  })
  const blobData = blob?.data || blob
  return window.URL.createObjectURL(new Blob([blobData], { type: "application/pdf" }))
}

async function loadNoticePreview(uuid) {
  revokeNoticePreview()
  const normalizedUuid = normalizeUuid(uuid)
  if (!normalizedUuid) return
  try {
    noticePreviewUrl.value = await fetchPdfObjectUrl(normalizedUuid)
  } catch (e) {
    console.error("参赛通知预览加载失败", e)
    noticePreviewUrl.value = ""
  }
}

function handleNoticeUuidChange(val) {
  const uuid = normalizeUuid(val)
  form.value.uuid = uuid || null
  if (uuid) {
    loadNoticePreview(uuid)
  } else {
    revokeNoticePreview()
  }
}

async function handleOpenNoticeDetail(uuid) {
  const normalizedUuid = normalizeUuid(uuid)
  if (!normalizedUuid) {
    proxy.$modal.msgError("文件标识无效")
    return
  }
  proxy.$modal.loading("正在准备文件...")
  const previewWindow = window.open("about:blank", "_blank")
  try {
    const pdfUrl = await fetchPdfObjectUrl(normalizedUuid)
    proxy.$modal.closeLoading()
    if (previewWindow) {
      previewWindow.location.href = pdfUrl
    } else {
      window.URL.revokeObjectURL(pdfUrl)
      proxy.$modal.msgWarning("浏览器拦截了弹窗，请允许弹窗后重试")
    }
  } catch (e) {
    proxy.$modal.closeLoading()
    if (previewWindow) previewWindow.close()
    proxy.$modal.msgError("文件获取失败，请稍后重试")
  }
}

async function handleDownloadNotice(uuid) {
  const normalizedUuid = normalizeUuid(uuid)
  if (!normalizedUuid) {
    proxy.$modal.msgError("文件标识无效")
    return
  }
  proxy.$modal.loading("正在下载文件...")
  try {
    const blobResp = await request({
      url: "/attachment/download",
      method: "get",
      params: { resource: normalizedUuid },
      responseType: "blob",
    })
    const blobData = blobResp?.data || blobResp
    const url = window.URL.createObjectURL(new Blob([blobData]))
    const link = document.createElement("a")
    link.href = url
    link.download = `${normalizedUuid}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } finally {
    proxy.$modal.closeLoading()
  }
}

function handleDeleteNotice() {
  proxy.$modal.confirm("是否确认删除参赛通知附件？").then(() => {
    form.value.uuid = null
    revokeNoticePreview()
    proxy.$modal.msgSuccess("已删除")
  }).catch(() => { })
}

// 获取赛事主表列表并去重
// 获取赛事主表列表（修复去重逻辑，保留全量数据）
function getCompetitionList() {
  // 关键：传入分页参数，拉取全量数据
  listCompetition({ pageNum: 1, pageSize: 9999 }).then(response => {
    const originList = response.rows || response.data || []
    competitionList.value = originList.filter(item => item.name && item.id)
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
  revokeNoticePreview()
  form.value = {
    id: null,
    competitionId: null,
    year: new Date().getFullYear(),
    uuid: null,
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
  getCompetitionList() // 新增：搜索前刷新赛事列表
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
  const _id = row.id || ids.value // 1. 先获取要修改的记录ID
  getSession(_id).then(response => {
    form.value = response.data
    // 2. 强制把ID赋值回去，这是最关键的一步！
    form.value.id = _id
    // 处理tags字段，将字符串转为数组
    if (form.value.tags && typeof form.value.tags === 'string') {
      form.value.tags = form.value.tags.split(',')
    } else {
      form.value.tags = []
    }
    tagList.value = response.data.tagList
    if (form.value.uuid) {
      loadNoticePreview(form.value.uuid)
    }
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
          getCompetitionList() // 新增：刷新赛事列表
        })
      } else {
        addSession(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
          getCompetitionList() // 新增：刷新赛事列表
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

onBeforeUnmount(() => {
  revokeNoticePreview()
})

// 页面初始化
getList()
getCompetitionList()
</script>

<style scoped>
.attach-card {
  height: 100%;
  min-height: 620px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.preview-box {
  margin-top: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.custom-file-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
  margin-top: 8px;
}

.file-name {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.file-name span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-action {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mr5 {
  margin-right: 5px;
}
</style>
