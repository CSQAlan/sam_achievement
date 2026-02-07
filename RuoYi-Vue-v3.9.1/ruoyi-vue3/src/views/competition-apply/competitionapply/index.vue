<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请编号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入申请编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人用户ID" prop="applicantUserId">
        <el-input
          v-model="queryParams.applicantUserId"
          placeholder="请输入申请人用户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人学院" prop="applicantDepId">
        <el-input
          v-model="queryParams.applicantDepId"
          placeholder="请输入申请人学院"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择赛事类别" clearable>
          <el-option
            v-for="dict in sys_competition_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择赛事级别" clearable>
          <el-option
            v-for="dict in sys_competition_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="适用范围" prop="scopeType">
        <el-select v-model="queryParams.scopeType" placeholder="请选择适用范围" clearable>
          <el-option
            v-for="dict in sys_competition_scope_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择审核状态" clearable>
          <el-option
            v-for="dict in sys_shenhe_status"
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
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['competition-apply:competitionapply:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['competition-apply:competitionapply:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['competition-apply:competitionapply:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['competition-apply:competitionapply:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="competitionapplyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请主键" align="center" prop="id" />
      <el-table-column label="申请编号" align="center" prop="applyNo" />
      <el-table-column label="申请人用户ID" align="center" prop="applicantUserId" />
      <el-table-column label="申请人学院" align="center" prop="applicantDepId" />
      <el-table-column label="赛事名称" align="center" prop="name" />
      <el-table-column label="赛事类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="sys_competition_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="盖章单位" align="center" prop="organizations" />
      <el-table-column label="赛事级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="sys_competition_level" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="适用范围" align="center" prop="scopeType">
        <template #default="scope">
          <dict-tag :options="sys_competition_scope_type" :value="scope.row.scopeType"/>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_shenhe_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="标签" align="center" prop="tags">
        <template #default="scope">
          <dict-tag :options="sys_competition_tag" :value="scope.row.tags ? scope.row.tags.split(',') : []"/>
        </template>
      </el-table-column>
      <el-table-column label="赛事说明" align="center" prop="memo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['competition-apply:competitionapply:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['competition-apply:competitionapply:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改赛事申请对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="competitionapplyRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="申请编号" prop="applyNo">
          <el-input v-model="form.applyNo" placeholder="请输入申请编号" />
        </el-form-item>
        <el-form-item label="申请人用户ID" prop="applicantUserId">
          <el-input v-model="form.applicantUserId" placeholder="请输入申请人用户ID" />
        </el-form-item>
        <el-form-item label="申请人学院" prop="applicantDepId">
          <el-input v-model="form.applicantDepId" placeholder="请输入申请人学院" />
        </el-form-item>
        <el-form-item label="赛事名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入赛事名称" />
        </el-form-item>
        <el-form-item label="赛事类别" prop="category">
          <el-radio-group v-model="form.category">
            <el-radio
              v-for="dict in sys_competition_category"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="盖章单位" prop="organizations">
          <el-input v-model="form.organizations" placeholder="请输入盖章单位" />
        </el-form-item>
        <el-form-item label="赛事级别" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio
              v-for="dict in sys_competition_level"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="适用范围" prop="scopeType">
          <el-radio-group v-model="form.scopeType">
            <el-radio
              v-for="dict in sys_competition_scope_type"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_shenhe_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-checkbox-group v-model="form.tags">
            <el-checkbox
              v-for="dict in sys_competition_tag"
              :key="dict.value"
              :label="dict.value">
              {{dict.label}}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="赛事说明" prop="memo">
          <el-input v-model="form.memo" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-divider content-position="center">赛事申请附件信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddCompetitionApplyAttachment">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteCompetitionApplyAttachment">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="competitionApplyAttachmentList" :row-class-name="rowCompetitionApplyAttachmentIndex" @selection-change="handleCompetitionApplyAttachmentSelectionChange" ref="competitionApplyAttachment">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="附件类型" prop="type" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.type" placeholder="请选择附件类型">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="文件类型" prop="fileType" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.fileType" placeholder="请选择文件类型">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="原始文件名" prop="originName" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.originName" placeholder="请输入原始文件名" />
            </template>
          </el-table-column>
          <el-table-column label="存储文件名" prop="storeName" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.storeName" placeholder="请输入存储文件名" />
            </template>
          </el-table-column>
          <el-table-column label="文件大小" prop="fileSize" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.fileSize" placeholder="请输入文件大小" />
            </template>
          </el-table-column>
          <el-table-column label="上传人" prop="uploadBy" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.uploadBy" placeholder="请输入上传人" />
            </template>
          </el-table-column>
          <el-table-column label="上传时间" prop="uploadTime" width="240">
            <template #default="scope">
              <el-date-picker clearable
                v-model="scope.row.uploadTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择上传时间">
              </el-date-picker>
            </template>
          </el-table-column>
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

<script setup name="Competitionapply">
import { listCompetitionapply, getCompetitionapply, delCompetitionapply, addCompetitionapply, updateCompetitionapply } from "@/api/competition-apply/competitionapply"

const { proxy } = getCurrentInstance()
const { sys_competition_tag, sys_competition_del_flag, sys_shenhe_status, sys_competition_scope_type, sys_competition_category, sys_competition_level } = proxy.useDict('sys_competition_tag', 'sys_competition_del_flag', 'sys_shenhe_status', 'sys_competition_scope_type', 'sys_competition_category', 'sys_competition_level')

const competitionapplyList = ref([])
const competitionApplyAttachmentList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedCompetitionApplyAttachment = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    applyNo: null,
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
    applyNo: [
      { required: true, message: "申请编号不能为空", trigger: "blur" }
    ],
    applicantUserId: [
      { required: true, message: "申请人用户ID不能为空", trigger: "blur" }
    ],
    applicantDepId: [
      { required: true, message: "申请人学院不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "赛事名称不能为空", trigger: "blur" }
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
    scopeType: [
      { required: true, message: "适用范围不能为空", trigger: "change" }
    ],
    status: [
      { required: true, message: "审核状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询赛事申请列表 */
function getList() {
  loading.value = true
  listCompetitionapply(queryParams.value).then(response => {
    competitionapplyList.value = response.rows
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
    applyNo: null,
    applicantUserId: null,
    applicantDepId: null,
    name: null,
    category: null,
    organizations: null,
    level: null,
    scopeType: null,
    status: null,
    tags: [],
    memo: null,
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
  competitionApplyAttachmentList.value = []
  proxy.resetForm("competitionapplyRef")
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
  open.value = true
  title.value = "添加赛事申请"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCompetitionapply(_id).then(response => {
    form.value = response.data
    form.value.tags = form.value.tags.split(",")
    competitionApplyAttachmentList.value = response.data.competitionApplyAttachmentList
    open.value = true
    title.value = "修改赛事申请"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["competitionapplyRef"].validate(valid => {
    if (valid) {
      form.value.tags = form.value.tags.join(",")
      form.value.competitionApplyAttachmentList = competitionApplyAttachmentList.value
      if (form.value.id != null) {
        updateCompetitionapply(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCompetitionapply(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除赛事申请编号为"' + _ids + '"的数据项？').then(function() {
    return delCompetitionapply(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 赛事申请附件序号 */
function rowCompetitionApplyAttachmentIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 赛事申请附件添加按钮操作 */
function handleAddCompetitionApplyAttachment() {
  let obj = {}
  obj.type = ""
  obj.fileType = ""
  obj.originName = ""
  obj.storeName = ""
  obj.filePath = ""
  obj.fileUrl = ""
  obj.fileSize = ""
  obj.uploadBy = ""
  obj.uploadTime = ""
  obj.remark = ""
  competitionApplyAttachmentList.value.push(obj)
}

/** 赛事申请附件删除按钮操作 */
function handleDeleteCompetitionApplyAttachment() {
  if (checkedCompetitionApplyAttachment.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的赛事申请附件数据")
  } else {
    const competitionApplyAttachments = competitionApplyAttachmentList.value
    const checkedCompetitionApplyAttachments = checkedCompetitionApplyAttachment.value
    competitionApplyAttachmentList.value = competitionApplyAttachments.filter(function(item) {
      return checkedCompetitionApplyAttachments.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleCompetitionApplyAttachmentSelectionChange(selection) {
  checkedCompetitionApplyAttachment.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('competition-apply/competitionapply/export', {
    ...queryParams.value
  }, `competitionapply_${new Date().getTime()}.xlsx`)
}

getList()
</script>
