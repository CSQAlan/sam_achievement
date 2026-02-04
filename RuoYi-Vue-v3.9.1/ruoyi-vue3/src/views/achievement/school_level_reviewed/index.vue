<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果ID" prop="achievementId">
        <el-input
            v-model="queryParams.achievementId"
            placeholder="请输入成果ID"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="比赛" prop="track">
        <el-input
            v-model="queryParams.track"
            placeholder="请输入比赛"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="届次" prop="sessionId">
        <el-input
            v-model="queryParams.sessionId"
            placeholder="请输入届次"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option
              v-for="dict in achievement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="作品名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入作品名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择级别" clearable>
          <el-option
              v-for="dict in award_level_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖等级" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
          <el-option
              v-for="dict in award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="证书编号" prop="certificateNo">
        <el-input
            v-model="queryParams.certificateNo"
            placeholder="请输入证书编号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="组别" prop="groupId">
        <el-select v-model="queryParams.groupId" placeholder="请选择组别" clearable>
          <el-option
              v-for="dict in group_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学号" prop="studentId">
        <el-input
            v-model="queryParams.studentId"
            placeholder="请输入学号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="reviewStatus">
        <el-select v-model="queryParams.reviewStatus" placeholder="请选择审核状态" clearable>
          <el-option
              v-for="dict in school_audit_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker
            v-model="queryParams.awardTimeStart"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
        />
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
            v-hasPermi="['achievement:school_level_reviewed:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['achievement:school_level_reviewed:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['achievement:school_level_reviewed:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['achievement:school_level_reviewed:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="school_level_reviewedList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="比赛" align="center" prop="track" />
      <el-table-column label="届次" align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="name" />
      <el-table-column label="级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade" />
        </template>
      </el-table-column>
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="组别" align="center" prop="groupId">
        <template #default="scope">
          <dict-tag :options="group_type" :value="scope.row.groupId" />
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="审核状态" align="center" prop="reviewStatus">
        <template #default="scope">
          <dict-tag :options="school_audit_status" :value="scope.row.reviewStatus" />
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:school_level_reviewed:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:school_level_reviewed:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="school_level_reviewedRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="成果ID" prop="achievementId">
          <el-input v-model="form.achievementId" placeholder="请输入成果ID" />
        </el-form-item>
        <el-form-item label="比赛" prop="track">
          <el-input v-model="form.track" placeholder="请输入比赛" />
        </el-form-item>
        <el-form-item label="届次" prop="sessionId">
          <el-input v-model="form.sessionId" placeholder="请输入届次" />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别">
            <el-option
                v-for="dict in achievement_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入作品名称" />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择级别">
            <el-option
                v-for="dict in award_level_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择获奖等级">
            <el-option
                v-for="dict in award_rank"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNo">
          <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
        </el-form-item>
        <el-form-item label="组别" prop="groupId">
          <el-select v-model="form.groupId" placeholder="请选择组别">
            <el-option
                v-for="dict in group_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="审核状态" prop="reviewStatus">
          <el-select v-model="form.reviewStatus" placeholder="请选择审核状态">
            <el-option
                v-for="dict in school_audit_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker clearable
                          v-model="form.awardTime"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择获奖时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-input
              v-model="form.year"
              placeholder="请输入年份"
              clearable
          />
        </el-form-item>
        <el-divider content-position="center">参赛选手信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddSamAchievementParticipant">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteSamAchievementParticipant">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="samAchievementParticipantList" :row-class-name="rowSamAchievementParticipantIndex" @selection-change="handleSamAchievementParticipantSelectionChange" ref="samAchievementParticipant">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="学号" prop="studentId" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.studentId" placeholder="请输入学号" />
            </template>
          </el-table-column>
          <el-table-column label="排序" prop="orderNo" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.orderNo" placeholder="请输入排序" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="School_level_reviewed">
import { listSchool_level_reviewed, getSchool_level_reviewed, delSchool_level_reviewed, addSchool_level_reviewed, updateSchool_level_reviewed } from "@/api/achievement/school_level_reviewed"

const { proxy } = getCurrentInstance()
const { achievement_category, group_type, award_rank, award_level_type, school_audit_status } = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type', 'school_audit_status')

const school_level_reviewedList = ref([])
const samAchievementParticipantList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedSamAchievementParticipant = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    studentId: null,
    reviewStatus: null,
    awardTimeStart: null,
    awardTimeEnd: null
  },
  rules: {
    achievementId: [
      { required: true, message: "成果ID不能为空", trigger: "blur" }
    ],
    sessionId: [
      { required: true, message: "届次不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "类别不能为空", trigger: "change" }
    ],
    level: [
      { required: true, message: "获奖级别不能为空", trigger: "change" }
    ],
    grade: [
      { required: true, message: "获奖等级不能为空", trigger: "change" }
    ],
    certificateNo: [
      { required: true, message: "证书编号不能为空", trigger: "blur" }
    ],
    awardTime: [
      { required: true, message: "获奖时间不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询校级已审核列表 */
function getList() {
  loading.value = true
  listSchool_level_reviewed(queryParams.value).then(response => {
    school_level_reviewedList.value = response.rows
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
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
    year: null,
    ownerDepId: null,
    isReimburse: null,
    isSupplement: null,
    fee: null,
    reimbursementFee: null,
    reimbursementRatio: null,
    reimbursementItemId: null,
    reimbursementDate: null,
    itemIndex: null,
    qualityIndex: null,
    submittedAt: null,
    reviewedAt: null,
    schoolReviewedAt: null,
    reviewResult: null,
    schooiReviewResult: null,
    reviewReason: null,
    schoolReviewReason: null,
    auditBy: null,
    schoolAuditBy: null,
    createBy: null,
    updateBy: null,
    createTime: null,
    updateTime: null,
    delFlag: null,
    remark: null
  }
  samAchievementParticipantList.value = []
  proxy.resetForm("school_level_reviewedRef")
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
  ids.value = selection.map(item => item.achievementId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加校级已审核"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _achievementId = row.achievementId || ids.value
  getSchool_level_reviewed(_achievementId).then(response => {
    form.value = response.data
    samAchievementParticipantList.value = response.data.samAchievementParticipantList
    open.value = true
    title.value = "修改校级已审核"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["school_level_reviewedRef"].validate(valid => {
    if (valid) {
      form.value.samAchievementParticipantList = samAchievementParticipantList.value
      if (form.value.achievementId != null) {
        updateSchool_level_reviewed(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSchool_level_reviewed(form.value).then(response => {
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
  const _achievementIds = row.achievementId || ids.value
  proxy.$modal.confirm('是否确认删除校级已审核编号为"' + _achievementIds + '"的数据项？').then(function() {
    return delSchool_level_reviewed(_achievementIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 参赛选手序号 */
function rowSamAchievementParticipantIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 参赛选手添加按钮操作 */
function handleAddSamAchievementParticipant() {
  let obj = {}
  obj.achievementId = ""
  obj.studentId = ""
  obj.orderNo = ""
  obj.manager = ""
  obj.remark = ""
  samAchievementParticipantList.value.push(obj)
}

/** 参赛选手删除按钮操作 */
function handleDeleteSamAchievementParticipant() {
  if (checkedSamAchievementParticipant.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的参赛选手数据")
  } else {
    const samAchievementParticipants = samAchievementParticipantList.value
    const checkedSamAchievementParticipants = checkedSamAchievementParticipant.value
    samAchievementParticipantList.value = samAchievementParticipants.filter(function(item) {
      return checkedSamAchievementParticipants.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleSamAchievementParticipantSelectionChange(selection) {
  checkedSamAchievementParticipant.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('achievement/school_level_reviewed/export', {
    ...queryParams.value
  }, `school_level_reviewed_${new Date().getTime()}.xlsx`)
}

getList()
</script>
