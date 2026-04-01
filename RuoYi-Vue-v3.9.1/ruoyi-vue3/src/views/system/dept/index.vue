<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:dept:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['system:dept:add']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="deptName" label="部门名称" width="260" />
      <el-table-column prop="orderNum" label="排序" width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:dept:edit']">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['system:dept:add']">新增</el-button>
          <el-button v-if="scope.row.parentId != 0" link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:dept:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="deptRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="选择上级部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="upload.title" v-model="upload.open" width="460px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        action="#"
        accept=".xlsx,.xls"
        :disabled="upload.isUploading"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport">是否更新已经存在的部门数据</el-checkbox>
            </div>
            <span>仅允许导入 xls、xlsx 格式文件，支持通过“部门全路径”自动识别层级。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="downloadImportTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <div v-if="upload.checkedResult" class="import-check-summary">
        <el-alert
          :title="upload.checkedResult.message"
          :type="upload.checkedResult.failureCount > 0 ? 'warning' : 'success'"
          :closable="false"
          show-icon
        />
        <div class="summary-grid">
          <span>总数：{{ upload.checkedResult.totalCount }}</span>
          <span>新增：{{ upload.checkedResult.insertCount }}</span>
          <span>更新：{{ upload.checkedResult.updateCount }}</span>
          <span>失败：{{ upload.checkedResult.failureCount }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCheckImport" :loading="upload.checking">先校验</el-button>
          <el-button @click="handleExportFailDetails" :disabled="!canExportFailDetails" :loading="upload.exporting">导出失败明细</el-button>
          <el-button type="primary" :loading="upload.isUploading" @click="submitFileForm">确定导入</el-button>
          <el-button @click="closeImportDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="导入校验结果" v-model="preview.open" width="900px" append-to-body>
      <div v-if="preview.result" class="preview-wrapper">
        <el-descriptions :column="4" border>
          <el-descriptions-item label="总数">{{ preview.result.totalCount }}</el-descriptions-item>
          <el-descriptions-item label="新增">{{ preview.result.insertCount }}</el-descriptions-item>
          <el-descriptions-item label="更新">{{ preview.result.updateCount }}</el-descriptions-item>
          <el-descriptions-item label="失败">{{ preview.result.failureCount }}</el-descriptions-item>
        </el-descriptions>
        <div class="preview-message">{{ preview.result.message }}</div>
        <el-table :data="preview.result.rows" max-height="420">
          <el-table-column prop="rowNum" label="行号" width="80" />
          <el-table-column prop="deptName" label="部门名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="parentName" label="上级部门名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="resolvedParentName" label="解析后上级部门" min-width="150" show-overflow-tooltip />
          <el-table-column prop="deptPath" label="部门全路径" min-width="220" show-overflow-tooltip />
          <el-table-column prop="action" label="动作" width="90" />
          <el-table-column label="结果" width="90">
            <template #default="scope">
              <el-tag :type="scope.row.success ? 'success' : 'danger'">{{ scope.row.success ? '通过' : '失败' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="说明" min-width="220" show-overflow-tooltip />
        </el-table>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleExportFailDetails" :disabled="!canExportFailDetails" :loading="upload.exporting">导出失败明细</el-button>
          <el-button type="primary" @click="preview.open = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dept">
import { saveAs } from 'file-saver'
import {
  addDept,
  checkDeptImport,
  delDept,
  exportDeptImportFailDetails,
  getDept,
  importDeptData,
  listDept,
  listDeptExcludeChild,
  updateDept
} from '@/api/system/dept'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const deptList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const title = ref('')
const deptOptions = ref([])
const isExpandAll = ref(true)
const refreshTable = ref(true)
const uploadRef = ref(null)
const preview = reactive({
  open: false,
  result: null
})
const upload = reactive({
  open: false,
  title: '',
  isUploading: false,
  checking: false,
  exporting: false,
  updateSupport: false,
  selectedFile: null,
  checkedResult: null
})

const data = reactive({
  form: {},
  queryParams: {
    deptName: undefined,
    status: undefined
  },
  rules: {
    parentId: [{ required: true, message: '上级部门不能为空', trigger: 'blur' }],
    deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
    orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
    email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
    phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const canExportFailDetails = computed(() => {
  return !!upload.selectedFile?.raw && !!upload.checkedResult && upload.checkedResult.failureCount > 0
})

function getList() {
  loading.value = true
  listDept(queryParams.value).then(response => {
    deptList.value = proxy.handleTree(response.data, 'deptId')
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    deptId: undefined,
    parentId: undefined,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: '0'
  }
  proxy.resetForm('deptRef')
}

function handleQuery() {
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function resetImportState() {
  upload.isUploading = false
  upload.checking = false
  upload.exporting = false
  upload.selectedFile = null
  upload.checkedResult = null
  preview.result = null
  preview.open = false
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

function handleImport() {
  upload.title = '部门导入'
  upload.open = true
  upload.updateSupport = false
  resetImportState()
}

function closeImportDialog() {
  upload.open = false
  resetImportState()
}

function downloadImportTemplate() {
  proxy.download('system/dept/importTemplate', {}, `部门导入模板_${new Date().getTime()}.xlsx`)
}

function handleAdd(row) {
  reset()
  listDept().then(response => {
    deptOptions.value = proxy.handleTree(response.data, 'deptId')
  })
  if (row !== undefined) {
    form.value.parentId = row.deptId
  }
  open.value = true
  title.value = '添加部门'
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleFileChange(file) {
  upload.selectedFile = file
  upload.checkedResult = null
  preview.result = null
}

function handleFileRemove() {
  upload.selectedFile = null
  upload.checkedResult = null
  preview.result = null
}

function buildImportFormData() {
  const rawFile = upload.selectedFile?.raw
  if (!rawFile) {
    proxy.$modal.msgWarning('请选择要导入的 Excel 文件')
    return null
  }
  const fileName = rawFile.name?.toLowerCase() || ''
  if (!fileName.endsWith('.xls') && !fileName.endsWith('.xlsx')) {
    proxy.$modal.msgError('请选择后缀为 .xls 或 .xlsx 的文件')
    return null
  }
  const formData = new FormData()
  formData.append('file', rawFile)
  return formData
}

function handleCheckImport() {
  const formData = buildImportFormData()
  if (!formData) {
    return
  }
  upload.checking = true
  checkDeptImport(formData, upload.updateSupport).then(response => {
    upload.checkedResult = response.data
    preview.result = response.data
    preview.open = true
    if (response.data.failureCount > 0) {
      proxy.$modal.msgWarning('校验完成，存在失败数据，请先处理后再导入')
    } else {
      proxy.$modal.msgSuccess('校验通过，可以直接导入')
    }
  }).catch(error => {
    const message = error?.msg || error?.message || '校验失败，请检查文件格式和内容'
    proxy.$modal.msgError(message)
  }).finally(() => {
    upload.checking = false
  })
}

function handleExportFailDetails() {
  const formData = buildImportFormData()
  if (!formData) {
    return
  }
  upload.exporting = true
  exportDeptImportFailDetails(formData, upload.updateSupport).then(data => {
    saveAs(new Blob([data]), `部门导入失败明细_${new Date().getTime()}.xlsx`)
  }).catch(() => {
    proxy.$modal.msgError('导出失败明细失败，请稍后重试')
  }).finally(() => {
    upload.exporting = false
  })
}

function submitFileForm() {
  const formData = buildImportFormData()
  if (!formData) {
    return
  }
  upload.isUploading = true
  importDeptData(formData, upload.updateSupport).then(response => {
    closeImportDialog()
    proxy.$alert(
      `<div style='overflow:auto;overflow-x:hidden;max-height:70vh;padding:10px 20px 0;'>${response.msg}</div>`,
      '导入结果',
      { dangerouslyUseHTMLString: true }
    )
    getList()
  }).catch(error => {
    const message = error?.msg || error?.message || '导入失败，请检查文件格式和内容'
    proxy.$alert(
      `<div style='overflow:auto;overflow-x:hidden;max-height:70vh;padding:10px 20px 0;'>${message}</div>`,
      '导入失败',
      { dangerouslyUseHTMLString: true }
    )
  }).finally(() => {
    upload.isUploading = false
  })
}

function handleUpdate(row) {
  reset()
  listDeptExcludeChild(row.deptId).then(response => {
    deptOptions.value = proxy.handleTree(response.data, 'deptId')
  })
  getDept(row.deptId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改部门'
  })
}

function submitForm() {
  proxy.$refs.deptRef.validate(valid => {
    if (valid) {
      if (form.value.deptId !== undefined) {
        updateDept(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addDept(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  proxy.$modal.confirm(`是否确认删除名称为“${row.deptName}”的数据项？`).then(() => {
    return delDept(row.deptId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>

<style scoped>
.import-check-summary {
  margin-top: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-top: 12px;
  color: var(--el-text-color-regular);
}

.preview-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-message {
  color: var(--el-text-color-regular);
}
</style>
