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
      <!-- 新增：批量启用“预录” -->
      <el-col :span="2.2">
        <el-button type="success" plain icon="CircleCheck" :disabled="!hasPreRecordSelection" @click="handleEnablePreRecord"
          v-hasPermi="['session:session:edit']">批量启用预录</el-button>
      </el-col>
      <el-col :span="2.4">
        <el-button type="primary" plain icon="CopyDocument" :disabled="!selectedRows.length" @click="handleBatchCopySelected"
          v-hasPermi="['session:session:add']">批量复制（所选）</el-button>
      </el-col>
      <el-col :span="1.8">
        <el-button type="info" plain icon="Checked" @click="handleSelectAllFiltered"
          v-hasPermi="['session:session:list']">全选当前结果</el-button>
      </el-col>
      <el-col :span="1.8">
        <el-button type="info" plain icon="Calendar" @click="handleSelectByYear"
          v-hasPermi="['session:session:list']">按年份全选</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-alert v-if="isAllSelected" type="info" show-icon :closable="false" class="mb10">
      <template #title>
        已全选所有符合条件的记录（共 {{ ids.length }} 条）
        <el-button link type="primary" @click="toggleReviewMode" style="margin-left: 10px">
          {{ queryParams.ids ? '显示全部' : '查看已选' }}
        </el-button>
        <el-button link type="danger" @click="handleClearSelection" style="margin-left: 10px">取消全选</el-button>
      </template>
    </el-alert>

    <el-table v-loading="loading" :data="sessionList" @selection-change="handleSelectionChange" row-key="id" ref="sessionTableRef">
      <el-table-column type="selection" width="55" align="center" reserve-selection />
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
          <el-button link type="primary" icon="DocumentCopy" @click="handleCopyTemplate(scope.row)"
            v-hasPermi="['session:session:add']">复制模板</el-button>
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
              <el-input v-model="form.organizations" placeholder="请输入盖章单位">
                <template #append>
                  <el-tooltip content="多个单位请使用英文逗号(,)或中文逗号(，)隔开" placement="top">
                    <el-icon><QuestionFilled /></el-icon>
                  </el-tooltip>
                </template>
              </el-input>
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

              <div v-if="templateUuid" class="template-block">
                <el-divider content-position="left">模板通知（只读）</el-divider>
                <div class="custom-file-row">
                  <div class="file-name">
                    <el-icon class="mr5"><Document /></el-icon>
                    <span>{{ templateUuid }}</span>
                  </div>
                  <div class="file-action">
                    <el-button link type="primary" :icon="View" @click="handleOpenNoticeDetail(templateUuid)">预览</el-button>
                    <el-button link type="primary" :icon="Download" @click="handleDownloadNotice(templateUuid)">下载</el-button>
                  </div>
                </div>

                <div v-if="templatePreviewUrl" class="preview-box template-preview">
                  <iframe :src="templatePreviewUrl" width="100%" height="240px" frameborder="0"></iframe>
                </div>

                <el-divider content-position="left">新参赛通知文件</el-divider>
              </div>

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

    <el-dialog title="批量启用预录（必须补齐参赛通知）" v-model="batchOpen" width="90vw" top="5vh" append-to-body class="batch-copy-dialog">
      <el-row :gutter="20">
        <el-col :span="14">
          <el-table :data="batchItems" highlight-current-row @row-click="handleBatchRowClick" max-height="520px">
            <el-table-column type="index" width="55" align="center" label="#" />
            <el-table-column label="赛事" min-width="160" align="center" prop="competitionName" />
            <el-table-column label="年份" width="100" align="center" prop="year" />
            <el-table-column label="届次" min-width="160" align="center" prop="session" />
            <el-table-column label="新参赛通知(必传PDF)" min-width="260" align="center">
              <template #default="scope">
                <el-tag v-if="!scope.row.uuid" type="danger">尚未上传(必须上传)</el-tag>
                <span v-else class="ellipsis">{{ scope.row.uuid }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>

        <el-col :span="10">
          <div class="attach-card batch-attach">
            <el-divider content-position="left">当前选中记录：新通知（必传PDF）</el-divider>
            <div v-if="activeBatchItem && !activeBatchItem.uuid" class="batch-upload">
              <upload-file v-model="batchItems[batchActiveIndex].uuid" :limit="1" :fileSize="50" :fileType="['pdf']"
                :isShowTip="false" buttonText="上传PDF" class="hide-file-list"
                @update:modelValue="val => handleBatchItemUuidChange(batchActiveIndex, val)" />
              <div class="text-muted">成功上传后需点击下方提交以完成启用</div>
            </div>

            <div v-if="activeBatchItem?.uuid" class="custom-file-row">
              <div class="file-name">
                <el-icon class="mr5"><Document /></el-icon>
                <span>{{ activeBatchItem.uuid }}</span>
              </div>
              <div class="file-action">
                <el-button link type="primary" :icon="View" @click="handleBatchSelectAndPreview(batchActiveIndex)">预览</el-button>
                <el-button link type="primary" :icon="Download" @click="handleDownloadNotice(activeBatchItem.uuid)">下载</el-button>
                <el-button link type="danger" :icon="Delete" @click="handleBatchItemDeleteUuid(batchActiveIndex)">删除</el-button>
              </div>
            </div>

            <div v-if="batchPreviewUrl" class="preview-box">
              <iframe :src="batchPreviewUrl" width="100%" height="300px" frameborder="0"></iframe>
            </div>
            <div v-else class="text-muted">点击左侧表格行切换当前行</div>
          </div>
        </el-col>
      </el-row>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleBatchCancel">取 消</el-button>
          <el-button type="primary" @click="handleBatchSubmit">保 存 并 批 量 开 启</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增：年份选择弹窗（带加减器） -->
    <el-dialog title="按年份一键选择" v-model="yearSelectOpen" width="400px" append-to-body>
      <div style="text-align: center; padding: 20px 0;">
        <span style="margin-right: 15px;">选择年份:</span>
        <el-input-number v-model="quickYear" :min="2000" :max="2100" controls-position="right" placeholder="年份" style="width: 180px" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="yearSelectOpen = false">取消</el-button>
          <el-button type="primary" @click="executeSelectByYear">确认全选</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Session">
import { listSession, getSession, delSession, addSession, updateSession, exportTemplateSession, importDataSession, updateSessionStatusByIds, batchCopySession, allIdsSession } from "@/api/session/session"
import { listCompetition } from "@/api/competition/competition"
import request from "@/utils/request"
import UploadFile from "@/components/FileUpload"
import { Delete, Document, Download, View, QuestionFilled, Checked, Calendar } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { sys_competition_tag, sys_competition_status, sys_competition_del_flag, sys_competition_category, sys_competition_level } = proxy.useDict('sys_competition_tag', 'sys_competition_status', 'sys_competition_del_flag', 'sys_competition_category', 'sys_competition_level')

const sessionList = ref([])
const tagList = ref([])
const competitionList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const selectedRows = ref([])
const checkedTag = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isAllSelected = ref(false)
const sessionTableRef = ref(null)
const yearSelectOpen = ref(false)
const quickYear = ref(new Date().getFullYear())

const hasPreRecordSelection = computed(() => selectedRows.value.some(item => String(item?.status) === "2"))

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
      {
        validator: (rule, value, callback) => {
          if (data.form.status === '1' && !value) {
            callback(new Error("启用状态下参赛通知不能为空（仅PDF）"))
          } else {
            callback()
          }
        },
        trigger: "change"
      }
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
const templateUuid = ref("")
const templatePreviewUrl = ref("")

const batchOpen = ref(false)
const batchItems = ref([])
const batchActiveIndex = ref(0)
const batchPreviewUrl = ref("")

const activeBatchItem = computed(() => batchItems.value?.[batchActiveIndex.value] || null)


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

function revokeTemplatePreview() {
  if (templatePreviewUrl.value) {
    window.URL.revokeObjectURL(templatePreviewUrl.value)
    templatePreviewUrl.value = ""
  }
}

function revokeBatchPreview() {
  if (batchPreviewUrl.value) {
    window.URL.revokeObjectURL(batchPreviewUrl.value)
    batchPreviewUrl.value = ""
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

async function loadTemplatePreview(uuid) {
  revokeTemplatePreview()
  const normalizedUuid = normalizeUuid(uuid)
  if (!normalizedUuid) return
  try {
    templatePreviewUrl.value = await fetchPdfObjectUrl(normalizedUuid)
  } catch (e) {
    console.error("模板通知预览加载失败", e)
    templatePreviewUrl.value = ""
  }
}

async function loadBatchPreview(uuid) {
  revokeBatchPreview()
  const normalizedUuid = normalizeUuid(uuid)
  if (!normalizedUuid) return
  try {
    batchPreviewUrl.value = await fetchPdfObjectUrl(normalizedUuid)
  } catch (e) {
    console.error("加载通知预览失败", e)
    batchPreviewUrl.value = ""
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
    
    // 如果处于全选模式，加载数据后自动勾选当前页
    if (isAllSelected.value) {
      nextTick(() => {
        sessionList.value.forEach(row => {
          sessionTableRef.value.toggleRowSelection(row, true)
        })
      })
    }
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
  revokeTemplatePreview()
  templateUuid.value = ""
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
    status: "1",
    templateSessionId: null,
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
  isAllSelected.value = false
  queryParams.value.ids = null
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  selectedRows.value = selection || []
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
  
  // 如果手动取消勾选了某些项，则关闭“全选模式”的状态，改为普通多选状态
  if (isAllSelected.value && selection.length < ids.value.length) {
    // 这里其实不需要特别处理，因为 ids.value 已经由 map 得到了
  }
}

/** 全选当前筛选条件下的所有ID */
async function handleSelectAllFiltered() {
  // 1. 先清空已选，避免混合
  handleClearSelection()
  
  proxy.$modal.loading("正在获取符合条件的全部数据...")
  try {
    const res = await allIdsSession(queryParams.value)
    const allIds = res.data || []
    if (allIds.length === 0) {
      proxy.$modal.msgWarning("当前筛选条件下无数据")
      return
    }
    
    proxy.$modal.confirm(`确认要选择当前条件下全部 ${allIds.length} 条记录进行批量操作吗？`).then(() => {
      ids.value = allIds
      isAllSelected.value = true
      selectedRows.value = allIds.map(id => ({ id, status: '?' }))
      multiple.value = false
      
      // 立即勾选当前页
      sessionList.value.forEach(row => {
        sessionTableRef.value.toggleRowSelection(row, true)
      })
      
      proxy.$modal.msgSuccess(`已全选 ${allIds.length} 条记录`)
    }).catch(() => {})
  } catch (error) {
    console.error(error)
    proxy.$modal.msgError("获取数据失败")
  } finally {
    proxy.$modal.closeLoading()
  }
}

/** 切换“查看已选”模式 */
function toggleReviewMode() {
  if (queryParams.value.ids) {
    // 退出查看已选：清除 ids 过滤
    queryParams.value.ids = null
  } else {
    // 进入查看已选：设置 ids 过滤
    queryParams.value.ids = ids.value
  }
  handleQuery()
}

/** 取消全选 */
function handleClearSelection() {
  isAllSelected.value = false
  queryParams.value.ids = null
  ids.value = []
  selectedRows.value = []
  sessionTableRef.value.clearSelection()
  multiple.value = true
}

/** 按年份快速全选 - 打开弹窗 */
function handleSelectByYear() {
  quickYear.value = new Date().getFullYear() // 默认当年
  yearSelectOpen.value = true
}

/** 执行按年份全选逻辑 */
function executeSelectByYear() {
  const value = quickYear.value
  if (!value) return
  
  // 1. 先清空已选，避免混合
  handleClearSelection()
  
  yearSelectOpen.value = false
  proxy.$modal.loading(`正在获取 ${value} 年的所有届次ID...`)
  allIdsSession({ year: value }).then(res => {
    const allIds = res.data || []
    if (allIds.length === 0) {
      proxy.$modal.msgWarning(`${value} 年暂无相关记录`)
      return
    }
    ids.value = allIds
    isAllSelected.value = true
    selectedRows.value = allIds.map(id => ({ id, status: '?' }))
    multiple.value = false
    
    // 立即勾选当前页（如果当前页确实是这个年份的）
    sessionList.value.forEach(row => {
      if (row.year == value) {
        sessionTableRef.value.toggleRowSelection(row, true)
      }
    })
    
    proxy.$modal.msgSuccess(`已成功全选 ${value} 年共 ${allIds.length} 条记录`)
  }).catch(() => {
  }).finally(() => {
    proxy.$modal.closeLoading()
  })
}

function resetBatchCopyState() {
  batchItems.value = []
  batchActiveIndex.value = 0
  revokeBatchPreview()
}

async function handleEnablePreRecord() {
  const preRecordRows = selectedRows.value.filter(item => String(item?.status) === "2")
  if (!preRecordRows.length) {
    proxy.$modal.msgWarning("请先选择状态为“预录”的届次")
    return
  }

  resetBatchCopyState()
  batchOpen.value = true

  proxy.$modal.loading("正在加载届次数据...")
  try {
    const ids = preRecordRows.map(r => r.id).filter(Boolean)
    const details = await Promise.all(ids.map(id => getSession(id)))
    const items = details.map((resp) => {
      const row = resp?.data || {}
      return {
        ...row, // Preserve full row properties for full payload submission
        competitionName: row.competitionName || getCompetitionName(row.competitionId) || "",
        year: row.year,
        session: row.session,
        uuid: normalizeUuid(row.uuid) || null,
      }
    })

    batchItems.value = items
    batchActiveIndex.value = 0
    if (activeBatchItem.value?.uuid) {
      await loadBatchPreview(activeBatchItem.value.uuid)
    }
  } catch (e) {
    console.error("加载届次数据失败", e)
    proxy.$modal.msgError("加载失败，请重试")
    batchOpen.value = false
  } finally {
    proxy.$modal.closeLoading()
  }
}

function chineseToNumber(text) {
  if (!text) return null
  const digitMap = { 零: 0, 一: 1, 二: 2, 两: 2, 三: 3, 四: 4, 五: 5, 六: 6, 七: 7, 八: 8, 九: 9 }
  const unitMap = { 十: 10, 百: 100, 千: 1000, 万: 10000 }
  let result = 0
  let section = 0
  let number = 0

  for (const ch of String(text).trim()) {
    if (Object.prototype.hasOwnProperty.call(digitMap, ch)) {
      number = digitMap[ch]
      continue
    }
    if (!Object.prototype.hasOwnProperty.call(unitMap, ch)) return null
    const unit = unitMap[ch]
    if (unit === 10000) {
      section = (section + number) * unit
      result += section
      section = 0
      number = 0
      continue
    }
    if (number === 0) number = 1
    section += number * unit
    number = 0
  }

  return result + section + number
}

function numberToChinese(num) {
  const n = Number(num)
  if (!Number.isFinite(n) || n <= 0) return ""
  const digits = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九"]
  const units = ["", "十", "百", "千"]
  let value = Math.floor(n)
  let str = ""
  let unitIndex = 0
  let zero = false

  while (value > 0 && unitIndex < units.length) {
    const d = value % 10
    if (d === 0) {
      if (str && !zero) {
        str = digits[0] + str
        zero = true
      }
    } else {
      str = digits[d] + units[unitIndex] + str
      zero = false
    }
    value = Math.floor(value / 10)
    unitIndex += 1
  }

  str = str.replace(/零+/g, "零").replace(/零$/g, "")
  if (str.startsWith("一十")) str = str.slice(1)
  return str
}

function incrementSessionText(text) {
  const raw = String(text || "").trim()
  if (!raw) return ""

  if (/^\d+$/.test(raw)) {
    return String(Number(raw) + 1)
  }

  const arabicMatch = raw.match(/第\s*(\d+)\s*届/)
  if (arabicMatch) {
    const next = Number(arabicMatch[1]) + 1
    return raw.replace(arabicMatch[0], `第${next}届`)
  }

  const chineseMatch = raw.match(/第\s*([零一二三四五六七八九十百千万两]+)\s*届/)
  if (chineseMatch) {
    const current = chineseToNumber(chineseMatch[1])
    if (current != null) {
      const next = numberToChinese(current + 1)
      if (next) return raw.replace(chineseMatch[0], `第${next}届`)
    }
  }

  const matches = Array.from(raw.matchAll(/(\d+)/g))
  if (matches.length) {
    const last = matches[matches.length - 1]
    const start = last.index ?? -1
    if (start >= 0) {
      const next = String(Number(last[1]) + 1)
      return raw.slice(0, start) + next + raw.slice(start + last[1].length)
    }
  }

  return raw + " (新)"
}

async function handleCopyTemplate(row) {
  const templateId = row?.id
  if (!templateId) {
    proxy.$modal.msgError("模板ID无效")
    return
  }

  reset()
  getCompetitionList()

  proxy.$modal.loading("正在加载模板...")
  try {
    const resp = await getSession(templateId)
    const template = resp?.data || {}

    templateUuid.value = normalizeUuid(template.uuid)
    if (templateUuid.value) {
      await loadTemplatePreview(templateUuid.value)
    }

    const templateYear = Number(template.year)
    form.value.competitionId = template.competitionId ?? null
    form.value.category = template.category ?? null
    form.value.organizations = template.organizations ?? null
    form.value.level = template.level ?? null
    form.value.year = Number.isFinite(templateYear) && templateYear > 0 ? templateYear + 1 : new Date().getFullYear()
    form.value.session = incrementSessionText(template.session) || template.session || null
    form.value.status = "2" // 预录：后端会强制覆盖
    form.value.templateSessionId = templateId
    form.value.uuid = null // 强制重新上传通知

    if (template.tags && typeof template.tags === "string") {
      form.value.tags = template.tags.split(",").map(s => s.trim()).filter(Boolean)
    } else {
      form.value.tags = []
    }

    open.value = true
    title.value = "复制赛事届次模板"
  } catch (e) {
    console.error("复制模板失败", e)
    proxy.$modal.msgError("复制模板失败，请稍后重试")
  } finally {
    proxy.$modal.closeLoading()
  }
}

function incrementSessionTextBy(text, steps) {
  let value = String(text || "").trim()
  if (!value) return ""
  const count = Number(steps) || 0
  for (let i = 0; i < count; i++) {
    value = incrementSessionText(value)
  }
  return value
}

function handleBatchCopySelected() {
  if (!selectedRows.value.length) {
    proxy.$modal.msgWarning("请先勾选要复制的届次模板")
    return
  }
  if (selectedRows.value.length > 50) {
    proxy.$modal.msgWarning("单次批量复制最多支持50条")
    return
  }

  proxy.$modal.confirm(`将基于您选中的 ${selectedRows.value.length} 条模板自动进行批量复制（生成+1届的新记录）。\n复制产出的届次默认都是“预录”状态，确定执行吗？`).then(async () => {
    proxy.$modal.loading("正在自动复制并生成届次...")
    try {
      const idsToCopy = selectedRows.value.map(r => r.id).filter(Boolean)
      const details = await Promise.all(idsToCopy.map(id => getSession(id)))
      
      const items = details.map((resp, idx) => {
        const template = resp?.data || {}
        const templateYear = Number(template.year)
        const year = Number.isFinite(templateYear) && templateYear > 0 ? templateYear + 1 : new Date().getFullYear()
        const templateSession = template.session || ""

        return {
          templateSessionId: template.id || idsToCopy[idx],
          year,
          session: incrementSessionText(templateSession) || templateSession || "",
          uuid: null,
        }
      })
      
      return batchCopySession({ items })
    } catch (e) {
      console.error(e)
      throw e
    }
  }).then(resp => {
    proxy.$modal.closeLoading()
    proxy.$modal.msgSuccess(resp?.msg || "批量复制完成，状态已设为预录！")
    getList()
  }).catch(() => {
    proxy.$modal.closeLoading()
  })
}

function handleBatchRowClick(row) {
  const idx = batchItems.value.indexOf(row)
  if (idx >= 0) {
    batchActiveIndex.value = idx
    if (row?.uuid) {
      loadBatchPreview(row.uuid)
    } else {
      revokeBatchPreview()
    }
  }
}

function handleBatchItemUuidChange(index, val) {
  const uuid = normalizeUuid(val)
  if (!batchItems.value[index]) return
  batchItems.value[index].uuid = uuid || null
  if (index === batchActiveIndex.value) {
    if (uuid) loadBatchPreview(uuid)
    else revokeBatchPreview()
  }
}

function handleBatchItemDeleteUuid(index) {
  if (!batchItems.value[index]) return
  batchItems.value[index].uuid = null
  if (index === batchActiveIndex.value) {
    revokeBatchPreview()
  }
}

function handleBatchSelectAndPreview(index) {
  const item = batchItems.value?.[index]
  if (!item) return
  batchActiveIndex.value = index
  if (!item.uuid) {
    proxy.$modal.msgWarning("请先上传该行的参赛通知PDF")
    return
  }
  loadBatchPreview(item.uuid)
}

function handleBatchCancel() {
  batchOpen.value = false
  resetBatchCopyState()
}

async function handleBatchSubmit() {
  if (!batchItems.value.length) {
    proxy.$modal.msgError("当前列表为空")
    return
  }

  for (let i = 0; i < batchItems.value.length; i++) {
    const rowNo = i + 1
    const item = batchItems.value[i]
    if (!String(item?.uuid || "").trim()) {
      proxy.$modal.msgError(`第${rowNo}行：尚未上传新参赛通知（必传PDF）`)
      return
    }
  }

  try {
    await proxy.$modal.confirm(`确认将这${batchItems.value.length}条届次全部补充通知文件，并立刻启用？`)
    proxy.$modal.loading("正在逐条保存并开启...")
    
    // We will update each session and then trigger batch status update
    // Update uuid for each first
    await Promise.all(batchItems.value.map(item => {
      const updateData = { ...item }
      updateData.status = "1" // Set target status
      // make sure tags is properly transformed just like regular submitForm
      if (Array.isArray(updateData.tags)) {
        updateData.tags = updateData.tags.join(",")
      }
      return updateSession(updateData)
    }))

    proxy.$modal.msgSuccess("已全部成功开启！")
    batchOpen.value = false
    resetBatchCopyState()
    getList()
  } catch (e) {
    console.error(e)
    if (e !== 'cancel') {
      proxy.$modal.msgError("执行过程中出错，请刷新列表重试")
    }
  } finally {
    proxy.$modal.closeLoading()
  }
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
  revokeTemplatePreview()
  revokeBatchPreview()
  revokeBatchTemplatePreview()
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

.template-block {
  margin-bottom: 12px;
}

.template-preview {
  background: #ffffff;
}

.batch-attach {
  min-height: 620px;
}

.ml10 {
  margin-left: 10px;
}

.text-muted {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}

.ellipsis {
  display: inline-block;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
}

.batch-upload {
  margin-bottom: 12px;
}

:deep(.batch-copy-dialog .el-dialog__body) {
  max-height: 82vh;
  overflow-y: auto;
}
</style>
