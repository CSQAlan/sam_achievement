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
      <el-form-item label="届次 " prop="sessionId">
        <el-input
          v-model="queryParams.sessionId"
          placeholder="请输入届次 "
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
      <el-form-item label="团队名称" prop="teamName">
        <el-input
          v-model="queryParams.teamName"
          placeholder="请输入团队名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择获奖级别" clearable>
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
      <el-form-item label="赛道" prop="track">
        <el-input
          v-model="queryParams.track"
          placeholder="请输入赛道"
          clearable
          @keyup.enter="handleQuery"
        />
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
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker clearable
          v-model="queryParams.awardTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择获奖时间">
        </el-date-picker>
      </el-form-item>

       <!-- 新增：报销相关查询条件 -->
  <el-form-item label="报销时间" prop="reimbursementTime">
    <el-date-picker clearable
      v-model="queryParams.reimbursementTime"
      type="date"
      value-format="YYYY-MM-DD"
      placeholder="请选择报销时间">
    </el-date-picker>
  </el-form-item>
  <el-form-item label="报销状态" prop="status">
    <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
      <el-option label="待审核" value="0" />
      <el-option label="已通过" value="1" />
      <el-option label="已拒绝" value="2" />
    </el-select>
  </el-form-item>
   <!-- 新增：成果主表的报销相关查询条件 -->
  <el-form-item label="是否报销" prop="isReimburse">
    <el-select v-model="queryParams.isReimburse" placeholder="请选择" clearable>
      <el-option label="已报销" value="1" />
      <el-option label="未报销" value="0" />
    </el-select>
  </el-form-item>
  <el-form-item label="报销时间" prop="reimbursementDate">
    <el-date-picker clearable
      v-model="queryParams.reimbursementDate"
      type="date"
      value-format="YYYY-MM-DD"
      placeholder="请选择报销时间">
    </el-date-picker>
  </el-form-item>
  <el-form-item label="报销比例" prop="reimbursementRatio">
    <el-input
      v-model="queryParams.reimbursementRatio"
      placeholder="请输入报销比例"
      clearable
      @keyup.enter="handleQuery"
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
          v-hasPermi="['system:Reimbursement:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:Reimbursement:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:Reimbursement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:Reimbursement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ReimbursementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="届次 " align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="name" />
      <el-table-column label="团队名称" align="center" prop="teamName" />
      <el-table-column label="获奖级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade"/>
        </template>
      </el-table-column>
      <el-table-column label="赛道" align="center" prop="track" />
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="组别" align="center" prop="groupId">
        <template #default="scope">
          <dict-tag :options="group_type" :value="scope.row.groupId"/>
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>


      <!-- 使用成果主表字段：报销相关 -->
  <el-table-column label="需报销金额" align="center" prop="fee" width="120">
    <template #default="scope">
      <span>{{ scope.row.fee ? '¥' + scope.row.fee : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="报销时间" align="center" prop="reimbursementDate" width="180">
    <template #default="scope">
      <span>{{ scope.row.reimbursementDate ? parseTime(scope.row.reimbursementDate, '{y}-{m}-{d}') : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="报销比例" align="center" prop="reimbursementRatio" width="100">
    <template #default="scope">
      <span>{{ scope.row.reimbursementRatio ? scope.row.reimbursementRatio + '%' : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="实际报销金额" align="center" prop="reimbursementFee" width="120">
    <template #default="scope">
      <span>{{ scope.row.reimbursementFee ? '¥' + scope.row.reimbursementFee : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="是否报销" align="center" prop="isReimburse" width="100">
    <template #default="scope">
      <el-tag v-if="scope.row.isReimburse === '1'" type="success">已报销</el-tag>
      <el-tag v-else-if="scope.row.isReimburse === '0'" type="info">未报销</el-tag>
      <span v-else>-</span>
    </template>
  </el-table-column>
  <el-table-column label="是否补录" align="center" prop="isSupplement" width="100">
    <template #default="scope">
      <el-tag v-if="scope.row.isSupplement === '1'" type="warning">是</el-tag>
      <el-tag v-else-if="scope.row.isSupplement === '0'" type="info">否</el-tag>
      <span v-else>-</span>
    </template>
  </el-table-column>
  




      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:Reimbursement:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:Reimbursement:remove']">删除</el-button>
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

    <!-- 添加或修改报销项目详情对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ReimbursementRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="届次 " prop="sessionId">
          <el-input v-model="form.sessionId" placeholder="请输入届次 " />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别">
            <el-option
              v-for="dict in achievement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入作品名称" />
        </el-form-item>
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="获奖级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择获奖级别">
            <el-option
              v-for="dict in award_level_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择获奖等级">
            <el-option
              v-for="dict in award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="赛道" prop="track">
          <el-input v-model="form.track" placeholder="请输入赛道" />
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
              :value="parseInt(dict.value)"
            ></el-option>
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
        <el-divider content-position="center">报销项目信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddSamReimbursementItems">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteSamReimbursementItems">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="samReimbursementItemsList" :row-class-name="rowSamReimbursementItemsIndex" @selection-change="handleSamReimbursementItemsSelectionChange" ref="samReimbursementItems">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="报销时间" prop="reimbursementTime" width="240">
            <template #default="scope">
              <el-date-picker clearable
                v-model="scope.row.reimbursementTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择报销时间">
              </el-date-picker>
            </template>
          </el-table-column>

          
  <el-divider content-position="center">报销信息</el-divider>
    
    <el-form-item label="需报销金额" prop="fee">
      <el-input-number 
        v-model="form.fee" 
        :precision="2" 
        :min="0" 
        placeholder="请输入需报销金额"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="报销比例" prop="reimbursementRatio">
      <el-input-number 
        v-model="form.reimbursementRatio" 
        :min="0" 
        :max="100" 
        :precision="2"
        placeholder="请输入报销比例"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="实际报销金额" prop="reimbursementFee">
      <el-input-number 
        v-model="form.reimbursementFee" 
        :precision="2" 
        :min="0" 
        placeholder="请输入实际报销金额"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="报销时间" prop="reimbursementDate">
      <el-date-picker clearable
        v-model="form.reimbursementDate"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="请选择报销时间"
        style="width: 100%">
      </el-date-picker>
    </el-form-item>
    
    <el-form-item label="是否报销" prop="isReimburse">
      <el-radio-group v-model="form.isReimburse">
        <el-radio label="1">已报销</el-radio>
        <el-radio label="0">未报销</el-radio>
      </el-radio-group>
    </el-form-item>
    
    <el-form-item label="是否补录" prop="isSupplement">
      <el-radio-group v-model="form.isSupplement">
        <el-radio label="1">是</el-radio>
        <el-radio label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>

          <el-table-column label="需报销金额" prop="totalFee" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.totalFee" placeholder="请输入需报销金额" />
            </template>
          </el-table-column>
          <el-table-column label="已发放金额" prop="paidFee" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.paidFee" placeholder="请输入已发放金额" />
            </template>
          </el-table-column>
          <el-table-column label="报销项目数量" prop="amount" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.amount" placeholder="请输入报销项目数量" />
            </template>
          </el-table-column>
          <el-table-column label="归属学院" prop="ownerDepId" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.ownerDepId" placeholder="请输入归属学院" />
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态">
                <el-option label="请选择字典生成" value="" />
              </el-select>
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

<script setup name="Reimbursement">
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { listReimbursement, getReimbursement, delReimbursement, addReimbursement, updateReimbursement } from "@/api/system/Reimbursement"
import { useRoute } from 'vue-router'


const route = useRoute()
const { proxy } = getCurrentInstance()

// 接收从报销项目列表传来的参数
const reimbursementItemId = ref(null)
const projectName = ref('')


const ReimbursementList = ref([])
const samReimbursementItemsList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedSamReimbursementItems = ref([])
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
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
    // 新增报销相关查询参数
    reimbursementTime: null,
    status: null,
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

// 字典数据 - 使用 proxy.useDict 方式
const { 
  achievement_category, 
  group_type, 
  award_rank, 
  award_level_type 
} = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type')


// 页面加载时执行
onMounted(() => {
  // 获取URL参数
  reimbursementItemId.value = route.query.reimbursementItemId
  projectName.value = route.query.name || '报销项目详情'
  
  console.log('接收到的项目ID:', reimbursementItemId.value)
  console.log('项目名称:', projectName.value)
  
  if (reimbursementItemId.value) {
    // 如果有项目ID，只加载该项目的详情
    loadDetailByReimbursementItemId()
    // 可选：修改页面标题
    title.value = projectName.value + ' - 报销详情'
  } else {
    // 否则加载全部列表
    getList()
  }
})

// 根据报销项目ID加载详情
const loadDetailByReimbursementItemId = () => {
  loading.value = true
  listReimbursement({
    reimbursementItemId: reimbursementItemId.value,
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize
  }).then(response => {
    ReimbursementList.value = response.rows
    total.value = response.total
    loading.value = false
    console.log('加载的详情数据:', ReimbursementList.value)
  }).catch(error => {
    console.error('加载失败:', error)
    loading.value = false
  })
}

/** 查询报销项目详情列表 */
function getList() {
  loading.value = true
  listReimbursement(queryParams.value).then(response => {
    ReimbursementList.value = response.rows
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
    isReimburse: null,
    reimbursementDate: null,
    reimbursementRatio: null,
    reimbursementFee: null,
    fee: null,
    isSupplement: null,
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
  samReimbursementItemsList.value = []
  proxy.resetForm("ReimbursementRef")
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
  title.value = "添加报销项目详情"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _achievementId = row.achievementId || ids.value
  getReimbursement(_achievementId).then(response => {
    form.value = response.data
    samReimbursementItemsList.value = response.data.samReimbursementItemsList || []
    open.value = true
    title.value = "修改报销项目详情"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ReimbursementRef"].validate(valid => {
    if (valid) {
      form.value.samReimbursementItemsList = samReimbursementItemsList.value
      if (form.value.achievementId != null) {
        updateReimbursement(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReimbursement(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除报销项目详情编号为"' + _achievementIds + '"的数据项？').then(function() {
    return delReimbursement(_achievementIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 报销项目序号 */
function rowSamReimbursementItemsIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 报销项目添加按钮操作 */
function handleAddSamReimbursementItems() {
  let obj = {
    reimbursementTime: "",
    totalFee: "",
    paidFee: "",
    amount: "",
    ownerDepId: "",
    status: "",
    remark: ""
  }
  samReimbursementItemsList.value.push(obj)
}

/** 报销项目删除按钮操作 */
function handleDeleteSamReimbursementItems() {
  if (checkedSamReimbursementItems.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的报销项目数据")
  } else {
    const samReimbursementItemss = samReimbursementItemsList.value
    const checkedSamReimbursementItemss = checkedSamReimbursementItems.value
    samReimbursementItemsList.value = samReimbursementItemss.filter(function(item) {
      return checkedSamReimbursementItemss.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleSamReimbursementItemsSelectionChange(selection) {
  checkedSamReimbursementItems.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/Reimbursement/export', {
    ...queryParams.value
  }, `Reimbursement_${new Date().getTime()}.xlsx`)
}

// 初始化加载数据
getList()
</script>