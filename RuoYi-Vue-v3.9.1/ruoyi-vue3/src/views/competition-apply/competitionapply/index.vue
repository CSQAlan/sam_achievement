<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="申请人学号" prop="applicantUserId">
        <el-input v-model="queryParams.applicantUserId" placeholder="请输入申请人学号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="申请人学院" prop="applicantDepId">
        <el-input v-model="queryParams.applicantDepId" placeholder="请输入申请人学院" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入赛事名称" clearable @keyup.enter="handleQuery" />
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
      <el-form-item label="适用范围" prop="scopeType">
        <el-select v-model="queryParams.scopeType" placeholder="请选择适用范围" clearable>
          <el-option v-for="dict in sys_competition_scope_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择审核状态" clearable>
          <el-option v-for="dict in sys_shenhe_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['competition-apply:competitionapply:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['competition-apply:competitionapply:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['competition-apply:competitionapply:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['competition-apply:competitionapply:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="competitionapplyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请人学号" align="center" prop="userName" />
      <el-table-column label="申请人学院" align="center" prop="deptName" />
      <el-table-column label="赛事名称" align="center" prop="name" />
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
      <el-table-column label="适用范围" align="center" prop="scopeType">
        <template #default="scope">
          <dict-tag :options="sys_competition_scope_type" :value="scope.row.scopeType" />
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_shenhe_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="标签" width="150" align="center" prop="tags">
        <template #default="scope">
          <dict-tag :options="sys_competition_tag" :value="scope.row.tags ? scope.row.tags.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="赛事说明" align="center" prop="memo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['competition-apply:competitionapply:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['competition-apply:competitionapply:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改赛事申请对话框（加宽为1200px，左右分栏） -->
    <el-dialog :title="title" v-model="open" width="1200px" append-to-body>
      <el-row :gutter="20">
        <!-- 左侧：基本信息 + 上传区域（占50%宽度） -->
        <el-col :span="11">
          <el-form ref="competitionapplyRef" :model="form" :rules="rules" label-width="100px">
            <!-- 赛事名称 -->
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入赛事名称" />
            </el-form-item>
            <!-- 赛事类别 -->
            <el-form-item label="赛事类别" prop="category">
              <el-radio-group v-model="form.category">
                <el-radio v-for="dict in sys_competition_category" :key="dict.value" :label="dict.value">{{ dict.label
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 盖章单位 -->
            <el-form-item label="盖章单位" prop="organizations">
              <el-input v-model="form.organizations" placeholder="请输入盖章单位" />
            </el-form-item>
            <!-- 赛事级别 -->
            <el-form-item label="赛事级别" prop="level">
              <el-radio-group v-model="form.level">
                <el-radio v-for="dict in sys_competition_level" :key="dict.value" :label="dict.value">{{ dict.label
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 标签 -->
            <el-form-item label="标签" prop="tags">
              <el-checkbox-group v-model="form.tags">
                <el-checkbox v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.value">{{ dict.label
                }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <!-- 赛事说明 -->
            <el-form-item label="赛事说明" prop="memo">
              <el-input v-model="form.memo" type="textarea" placeholder="请输入赛事说明" />
            </el-form-item>
          </el-form>
          <!-- 上传区域 -->
          <div class="upload-area">
            <el-divider content-position="center">赛事申请附件（仅支持PDF）</el-divider>
            <el-form-item label="佐证文件" prop="attachments">
              <upload-file v-model="form.attachmentUrls" :limit="5" :file-size="50" :file-type="['pdf']"
                :disabled="false" @change="handleFileChange"></upload-file>
            </el-form-item>
          </div>
        </el-col>

        <!-- 右侧：文件列表 + 预览区域（占50%宽度） -->
        <el-col :span="12">
          <el-divider content-position="center">文件预览</el-divider>

          <!-- 文件列表 -->
          <div class="file-list" v-if="fileList.length > 0">
            <div v-for="file in fileList" :key="file.id || file.name" class="file-item"
              :class="{ active: currentPreviewId === (file.id || file.name) }" @click="handlePreviewFile(file)">
              <span class="file-name">{{ file.documentName || file.name }}</span>
              <el-icon class="preview-icon">
                <Document />
              </el-icon>
            </div>
          </div>
          <div v-else class="no-file-tips">
            <el-empty description="暂无上传文件，请先上传PDF文件预览"></el-empty>
          </div>

          <!-- 预览区域 -->
          <div class="preview-area" v-if="previewUrl">
            <div ref="pdfContainer" class="pdf-container"></div>
            <!-- 备用方案：新标签页预览 -->
            <div class="preview-backup">
              <el-button type="text" @click="openNewTabPreview(previewUrl)">
                预览异常？点击在新标签页打开
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Competitionapply">
import { ref, reactive, toRefs, getCurrentInstance, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
// 引入RuoYi封装的下载方法
import { download } from '@/utils/request'
// 引入RuoYi封装的上传组件
import UploadFile from "@/components/FileUpload"
// 引入接口方法
import { listCompetitionapply, getCompetitionapply, delCompetitionapply, addCompetitionapply, updateCompetitionapply } from "@/api/competition-apply/competitionapply"
// 引入用户store
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
// 获取字典数据
const { sys_competition_tag, sys_competition_del_flag, sys_shenhe_status, sys_competition_scope_type, sys_competition_category, sys_competition_level } = proxy.useDict(
  'sys_competition_tag', 'sys_competition_del_flag', 'sys_shenhe_status', 'sys_competition_scope_type', 'sys_competition_category', 'sys_competition_level'
)

// 用户信息
const userStore = useUserStore()

// 响应式数据
const competitionapplyList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 预览相关响应式数据
const previewUrl = ref('')
const fileList = ref([])          // 所有上传/已存在的文件列表
const currentPreviewId = ref(null) // 当前预览的文件ID/名称
const pdfContainer = ref(null)    // PDF渲染容器
let pdfjsLib = null               // PDF.js 实例

// 挂载时加载PDF.js CDN
onMounted(() => {
  // 动态加载PDF.js
  const loadPDFJS = () => {
    return new Promise((resolve) => {
      if (window.pdfjsLib) {
        pdfjsLib = window.pdfjsLib
        resolve()
        return
      }
      // 加载PDF.js主库
      const script = document.createElement('script')
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js'
      script.onload = () => {
        // 配置worker
        window.pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js'
        pdfjsLib = window.pdfjsLib
        resolve()
      }
      script.onerror = () => {
        proxy.$modal.msgWarning("PDF预览组件加载失败，将使用新标签页预览")
        resolve()
      }
      document.head.appendChild(script)
    })
  }
  loadPDFJS()
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    applicantUserId: null,
    applicantDepId: null,
    name: null,
    category: null,
    level: null,
    scopeType: null,
    status: null,
    memo: null,
  },
  rules: {
    name: [{ required: true, message: "赛事名称不能为空", trigger: "blur" }],
    category: [{ required: true, message: "赛事类别不能为空", trigger: "change" }],
    organizations: [{ required: true, message: "盖章单位不能为空", trigger: "blur" }],
    level: [{ required: true, message: "赛事级别不能为空", trigger: "change" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 处理文件上传/变更，生成文件列表（新增页面）
function handleFileChange(fileUrls) {
  if (!fileUrls || fileUrls.length === 0) {
    fileList.value = []
    previewUrl.value = ''
    currentPreviewId.value = null
    return
  }

  // 解析上传的文件地址，生成文件列表
  const urls = fileUrls.split(',').filter(Boolean)
  fileList.value = urls.map((url, index) => {
    const fileName = url.split('/').pop() || `文件${index + 1}`
    return {
      name: fileName,
      url: `${import.meta.env.VITE_APP_BASE_API}${url.startsWith('/') ? url : `/${url}`}`
    }
  })

  // 默认预览第一个文件
  if (fileList.value.length > 0) {
    handlePreviewFile(fileList.value[0])
  }
}

// 新标签页预览（备用方案）
function openNewTabPreview(url) {
  window.open(url, '_blank')
}

// 预览文件（兼容PDF.js和新标签页）
async function handlePreviewFile(file) {
  currentPreviewId.value = file.id || file.name
  const baseApi = import.meta.env.VITE_APP_BASE_API || '';
  const finalBaseApi = baseApi.endsWith('/') ? baseApi.slice(0, -1) : baseApi;
  let url = '';

  if (file.path) {
    url = `${finalBaseApi}${file.path}`;
  } else if (file.url) {
    url = file.url;
  }

  if (url) {
    previewUrl.value = url;
    // 如果PDF.js加载成功，使用canvas渲染
    if (pdfjsLib && pdfContainer.value) {
      try {
        // 清空容器
        pdfContainer.value.innerHTML = '';
        // 加载PDF
        const pdf = await pdfjsLib.getDocument(url).promise;
        const page = await pdf.getPage(1);
        const viewport = page.getViewport({ scale: 1.2 });

        // 创建canvas
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.height = viewport.height;
        canvas.width = viewport.width;
        // 适配容器宽度
        canvas.style.maxWidth = '100%';
        canvas.style.height = 'auto';
        pdfContainer.value.appendChild(canvas);

        // 渲染PDF
        await page.render({ canvasContext: context, viewport }).promise;
      } catch (error) {
        console.error('PDF预览失败：', error);
        proxy.$modal.msgInfo("PDF内置预览失败，将自动在新标签页打开");
        openNewTabPreview(url);
      }
    } else {
      // PDF.js加载失败，直接新标签页打开
      openNewTabPreview(url);
    }
  }
}

/** 查询列表 */
function getList() {
  loading.value = true
  listCompetitionapply(queryParams.value).then(response => {
    competitionapplyList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消弹窗 */
function cancel() {
  open.value = false
  previewUrl.value = ''
  fileList.value = []
  currentPreviewId.value = null
  reset()
}

/** 重置表单 */
function reset() {
  form.value = {
    id: null,
    applicantUserId: userStore.id,      // 自动填充当前用户ID
    applicantDepId: userStore.deptId || '', // 自动填充当前部门ID
    name: null,
    category: null,
    organizations: null,
    level: null,
    scopeType: "0",                     // 默认：全校
    status: "0",                        // 默认：待审
    tags: [],
    memo: null,
    attachmentUrls: "",                 // 附件地址（逗号分隔）
    auditBy: null,
    auditTime: null,
    auditRemark: null,
    competitionId: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    delFlag: null
  }
  previewUrl.value = ''
  fileList.value = []
  currentPreviewId.value = null
  proxy.resetForm("competitionapplyRef")
}

/** 搜索操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置搜索 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中事件 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增操作 */
function handleAdd() {
  reset()
  fileList.value = []
  currentPreviewId.value = null
  previewUrl.value = ''
  open.value = true
  title.value = "添加赛事申请"
}

/** 修改操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCompetitionapply(_id).then(response => {
    form.value = response.data
    form.value.tags = form.value.tags ? form.value.tags.split(",") : []

    // 加载子表文件列表
    if (response.data.competitionApplyAttachmentList && response.data.competitionApplyAttachmentList.length > 0) {
      fileList.value = response.data.competitionApplyAttachmentList
      // 默认预览第一个文件
      handlePreviewFile(fileList.value[0])
    } else {
      fileList.value = []
      currentPreviewId.value = null
      previewUrl.value = ''
    }

    open.value = true
    title.value = "修改赛事申请"
  })
}

/** 提交表单 */
function submitForm() {
  proxy.$refs["competitionapplyRef"].validate(valid => {
    if (valid) {
      // 核心修复：先判断tags是否为数组，再处理
      form.value.tags = Array.isArray(form.value.tags) ? form.value.tags.join(",") : "";
      // 构造提交数据（直接传纯 JSON）
      const submitData = { ...form.value }

      if (form.value.id) {
        // 更新逻辑不变
        updateCompetitionapply(submitData).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        // 新增：直接调用旧的 addCompetitionapply（纯 JSON）
        addCompetitionapply(submitData).then(res => {
          if (res && res.code === 200) {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          } else {
            proxy.$modal.msgError(res?.msg || "新增失败");
          }
        }).catch(err => {
          proxy.$modal.msgWarning("新增提示失败，但数据可能已入库，请刷新列表确认");
          console.error("新增请求异常：", err);
          open.value = false;
          getList();
        });
      }
    }
  })
}

/** 删除操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除赛事申请编号为"' + _ids + '"的数据项？').then(() => {
    delCompetitionapply(_ids).then(() => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
    })
  })
}

/** 导出操作（使用RuoYi封装的download方法） */
function handleExport() {
  download('competition-apply/competitionapply/export', queryParams.value, `competitionapply_${new Date().getTime()}.xlsx`)
}

// 初始化列表
getList()
</script>

<style scoped>
/* 预览区域样式优化 */
.upload-area {
  margin-top: 20px;
  margin-bottom: 20px;
}

/* 文件列表样式 */
.file-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  margin-bottom: 16px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background-color: #f5f7fa;
}

.file-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-icon {
  margin-left: 8px;
  font-size: 16px;
}

/* 预览区域样式 */
.preview-area {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  overflow: hidden;
  min-height: 500px;
}

.pdf-container {
  width: 100%;
  height: 480px;
  overflow: auto;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 10px;
}

.preview-backup {
  text-align: center;
  padding: 8px 0;
  border-top: 1px solid #e6e6e6;
}

.no-file-tips {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #e6e6e6;
  border-radius: 4px;
  margin-bottom: 16px;
}

/* 对话框内边距优化 */
:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

:deep(.el-col) {
  height: 100%;
}

/* 调整分割线样式 */
:deep(.el-divider__text) {
  font-size: 14px;
  font-weight: 500;
}
</style>