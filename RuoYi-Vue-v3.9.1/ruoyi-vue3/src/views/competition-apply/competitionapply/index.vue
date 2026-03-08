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
          <!-- 上传区域（若依标准写法） -->
          <div class="upload-area">
            <el-divider content-position="center">赛事申请附件（仅支持PDF）</el-divider>
            <el-form-item label="佐证文件">
              <upload-file v-model="form.attachmentUrls" :limit="5" :file-size="50" :file-type="['pdf']"
                :disabled="false" @change="handleFileUrlChange">
              </upload-file>
            </el-form-item>
          </div>
        </el-col>

        <!-- 右侧：文件列表 + 预览提示区域（优化样式） -->
        <el-col :span="12">
          <el-divider content-position="center">文件操作</el-divider>

          <!-- 文件列表（优化样式） -->
          <div class="file-list-container" v-if="fileList.length > 0">
            <div class="file-list-header">
              <span class="file-list-title">已上传文件</span>
              <span class="file-tip-text">点击文件名在新标签页预览</span>
            </div>
            <div class="file-list">
              <div v-for="(file, index) in fileList" :key="index" class="file-item" @click="handlePreviewFile(file)">
                <el-icon class="file-icon">
                  <Document />
                </el-icon>
                <span class="file-name">{{ file.name }}</span>
                <el-icon class="delete-icon" color="#F56C6C" @click.stop="handleDeleteFile(file, index)">
                  <Delete />
                </el-icon>
              </div>
            </div>
          </div>
          <div v-else class="no-file-tips">
            <el-empty description="暂无上传文件，请先上传PDF文件"></el-empty>
          </div>

          <!-- 预览提示区域（优化样式，固定显示） -->
          <div class="preview-tips-area">
            <el-card shadow="hover" class="preview-card">
              <div class="preview-content">
                <el-icon size="48" color="#409EFF">
                  <Document />
                </el-icon>
                <h3>文件预览说明</h3>
                <p>点击左侧文件列表中的文件名，将在新标签页中打开PDF文件预览</p>
                <p class="tips-detail">新标签页预览支持完整的PDF功能（翻页、缩放、下载等）</p>
              </div>
            </el-card>
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
import { ref, getCurrentInstance } from 'vue'
import { Document, Delete } from '@element-plus/icons-vue'
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

// ========== 核心修复：独立声明响应式变量（解决响应式失效） ==========
const competitionapplyList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const fileList = ref([])

// 表单数据（若依标准写法，独立声明ref）
const form = ref({
  id: null,
  applicantUserId: userStore.id,
  applicantDepId: userStore.deptId || '',
  name: null,
  category: null,
  organizations: null,
  level: null,
  scopeType: "0",
  status: "0",
  tags: [],
  memo: null,
  attachmentUrls: "", // 若依upload-file核心绑定字段
  competitionApplyAttachmentList: [], // 附件列表
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
})

// 查询参数
const queryParams = ref({
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
})

// 表单校验规则
const rules = ref({
  name: [{ required: true, message: "赛事名称不能为空", trigger: "blur" }],
  category: [{ required: true, message: "赛事类别不能为空", trigger: "change" }],
  organizations: [{ required: true, message: "盖章单位不能为空", trigger: "blur" }],
  level: [{ required: true, message: "赛事级别不能为空", trigger: "change" }],
})

// ========== 文件上传核心方法 ==========
// 监听附件路径变化，同步文件列表（若依标准）
function handleFileUrlChange() {
  if (!form.value.attachmentUrls) {
    fileList.value = []
    form.value.competitionApplyAttachmentList = []
    return
  }
  // 解析路径为文件列表
  const urls = form.value.attachmentUrls.split(',').filter(url => url)
  // 更新显示列表
  fileList.value = urls.map(url => ({
    name: url.split('/').pop(),
    path: url,
    url: `${import.meta.env.VITE_APP_BASE_API}${url.startsWith('/') ? url : `/${url}`}`
  }))
  // 更新提交列表
  form.value.competitionApplyAttachmentList = urls.map(url => ({
    id: null,
    competitionApplyId: form.value.id,
    path: url,
    documentName: url.split('/').pop(),
    attachmentType: 1,
    delFlag: '0'
  }))
  console.log("文件列表同步完成：", form.value.competitionApplyAttachmentList)
}

// 删除文件（联动若依upload-file组件）
function handleDeleteFile(file, index) {
  proxy.$modal.confirm('确认删除该附件吗？删除后不可恢复！').then(() => {
    // 从attachmentUrls移除路径（核心：联动组件）
    const urls = form.value.attachmentUrls.split(',').filter(url => url !== file.path)
    form.value.attachmentUrls = urls.join(',')
    // 自动同步列表
    handleFileUrlChange()
    proxy.$modal.msgSuccess("附件删除成功")
  })
}

// 文件预览
function openNewTabPreview(url) {
  window.open(url, '_blank')
}

function handlePreviewFile(file) {
  let url = '';
  if (file.path) {
    url = `${import.meta.env.VITE_APP_BASE_API}${file.path.startsWith('/') ? file.path : `/${file.path}`}`;
  } else if (file.url) {
    url = file.url;
  }

  if (url) {
    openNewTabPreview(url);
    proxy.$modal.msgSuccess("正在新标签页打开文件预览...");
  } else {
    proxy.$modal.msgError("文件地址无效，无法预览");
  }
}

// ========== 核心修复：重置/新增/修改逻辑 ==========
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
  fileList.value = []
  reset()
}

/** 重置表单（核心修复：先resetForm，再强制清空ID） */
function reset() {
  // 第一步：调用若依resetForm清空表单
  proxy.resetForm("competitionapplyRef")
  // 第二步：强制重置所有字段，确保ID为null
  form.value = {
    id: null,
    applicantUserId: userStore.id,
    applicantDepId: userStore.deptId || '',
    name: null,
    category: null,
    organizations: null,
    level: null,
    scopeType: "0",
    status: "0",
    tags: [],
    memo: null,
    attachmentUrls: "",
    competitionApplyAttachmentList: [],
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
  fileList.value = []
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

/** 新增操作（核心修复：强制清空ID） */
function handleAdd() {
  reset() // 先重置表单
  open.value = true
  title.value = "添加赛事申请"
  // 额外保险：强制清空ID
  form.value.id = null
  form.value.attachmentUrls = ""
  fileList.value = []
  console.log("新增表单初始化完成，ID：", form.value.id) // 控制台验证ID是否为null
}

/** 修改操作（修复：同步旧文件到attachmentUrls） */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCompetitionapply(_id).then(response => {
    form.value = response.data
    form.value.tags = form.value.tags ? form.value.tags.split(",") : []

    // 核心：同步旧文件到attachmentUrls（若依upload-file需要）
    if (response.data.competitionApplyAttachmentList && response.data.competitionApplyAttachmentList.length > 0) {
      const urls = response.data.competitionApplyAttachmentList.map(f => f.path)
      form.value.attachmentUrls = urls.join(',')
      handleFileUrlChange() // 触发列表同步
    }

    open.value = true
    title.value = "修改赛事申请"
    console.log("修改表单初始化完成，ID：", form.value.id) // 控制台验证ID是否存在
  })
}

/** 提交表单（核心修复：严格区分新增/修改） */
function submitForm() {
  proxy.$refs["competitionapplyRef"].validate(valid => {
    if (valid) {
      // 强制同步文件列表
      handleFileUrlChange()
      // 处理tags数组转字符串
      form.value.tags = Array.isArray(form.value.tags) ? form.value.tags.join(",") : "";
      // 构造提交数据
      const submitData = { ...form.value }

      console.log("提交数据ID：", submitData.id) // 控制台验证提交的ID

      if (submitData.id) {
        // 更新逻辑
        updateCompetitionapply(submitData).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        // 新增逻辑
        addCompetitionapply(submitData).then(res => {
          if (res && res.code === 200) {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false
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
  proxy.$modal.confirm('是否确认删除该赛事申请？删除后相关附件也将一并删除！').then(() => {
    delCompetitionapply(_ids).then(() => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
    })
  })
}

/** 导出操作 */
function handleExport() {
  download('competition-apply/competitionapply/export', queryParams.value, `competitionapply_${new Date().getTime()}.xlsx`)
}

// 初始化列表
getList()
</script>

<style scoped>
/* 基础样式优化 */
.upload-area {
  margin-top: 20px;
  margin-bottom: 20px;
}

/* 文件列表容器样式优化 */
.file-list-container {
  margin-bottom: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9fafb;
}

.file-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
}

.file-list-title {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.file-tip-text {
  font-size: 12px;
  color: #6b7280;
}

/* 文件列表样式优化 */
.file-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 8px 0;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background-color: #eff6ff;
}

.file-icon {
  margin-right: 10px;
  color: #4f46e5;
  font-size: 16px;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  color: #1f2937;
}

.delete-icon {
  margin-left: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.2s;
}

.delete-icon:hover {
  color: #dc2626;
}

/* 无文件提示样式 */
.no-file-tips {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  margin-bottom: 20px;
  background-color: #f9fafb;
}

/* 预览提示区域样式 */
.preview-tips-area {
  width: 100%;
}

.preview-card {
  border: none;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.preview-content {
  text-align: center;
  padding: 20px 0;
}

.preview-content h3 {
  margin: 16px 0 8px 0;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}

.preview-content p {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
  margin: 4px 0;
}

.tips-detail {
  font-size: 12px !important;
  color: #9ca3af !important;
  margin-top: 8px !important;
}

/* 对话框样式优化 */
:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

:deep(.el-col) {
  height: 100%;
}

:deep(.el-divider__text) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-card) {
  border-radius: 8px;
}
</style>