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
      <!-- 查询表单中，把 v-model 改为绑定到 List 字段 -->
      <el-form-item label="赛事类别" prop="category">
        <el-select v-model="queryParams.categoryList" placeholder="请选择赛事类别" clearable multiple>
          <el-option v-for="dict in sys_competition_category" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="赛事级别" prop="level">
        <el-select v-model="queryParams.levelList" placeholder="请选择赛事级别" clearable multiple>
          <el-option v-for="dict in sys_competition_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="标签" prop="tags">
        <el-select v-model="queryParams.tagsList" placeholder="请选择赛事标签" clearable multiple>
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
          <el-option v-for="dict in competitionStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
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
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleOpenImport"
          v-hasPermi="['competition:competition:edit']">PDF导入</el-button>
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
            <el-radio v-for="dict in competitionStatusOptions" :key="dict.value" :label="dict.value">{{ dict.label
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

    <!-- PDF导入对话框 -->
    <el-dialog title="PDF竞赛清单导入" v-model="importOpen" width="1000px" append-to-body>
      <div v-loading="importLoading">
        <el-form label-width="100px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="上传PDF">
                <el-upload
                  ref="uploadRef"
                  :limit="1"
                  accept=".pdf"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :on-remove="handleFileRemove"
                  action="#"
                >
                  <template #trigger>
                    <el-button type="primary">选取文件</el-button>
                  </template>
                  <template #tip>
                    <div class="el-upload__tip">只能上传 PDF 文件</div>
                  </template>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目标标签">
                <el-select v-model="importTags" placeholder="请选择目标标签" style="width: 100%" multiple collapse-tags>
                  <el-option v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="相似度阈值">
                <el-slider v-model="threshold" :min="0.1" :max="1.0" :step="0.05" show-input />
                <div class="el-upload__tip">相似度高于此值的竞赛将被列出</div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row style="margin-bottom: 10px;">
            <el-col :span="24" style="text-align: right;">
              <el-button type="success" :disabled="!importFile" @click="handleAnalyzePdf">开始解析与匹配</el-button>
            </el-col>
          </el-row>
        </el-form>

        <el-tabs v-if="matchResults" v-model="activeTab">
          <el-tab-pane :label="`精确匹配 (${matchResults.exactMatches.length})`" name="exact">
            <el-table :data="matchResults.exactMatches" max-height="400">
              <el-table-column label="PDF中的名称" prop="pdfName" />
              <el-table-column label="系统主名称" prop="competitionName" />
              <el-table-column label="匹配项" prop="matchedName">
                <template #default="scope">
                  <el-tag v-if="scope.row.competitionName !== scope.row.matchedName" size="small" type="warning">别名: {{ scope.row.matchedName }}</el-tag>
                  <span v-else>{{ scope.row.matchedName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="匹配相似度" prop="similarity" width="100" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane :label="`高相似度 (${matchResults.highSimilarityMatches.length})`" name="high">
            <el-table :data="matchResults.highSimilarityMatches" max-height="400">
              <el-table-column label="PDF中的名称" prop="pdfName" />
              <el-table-column label="系统主名称" prop="competitionName" />
              <el-table-column label="匹配项" prop="matchedName">
                <template #default="scope">
                  <el-tag v-if="scope.row.competitionName !== scope.row.matchedName" size="small" type="warning">别名: {{ scope.row.matchedName }}</el-tag>
                  <span v-else>{{ scope.row.matchedName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="匹配相似度" prop="similarity" width="100" />
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-button link type="primary" @click="handleRemoveMatch('highSimilarityMatches', scope.$index)">排除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane :label="`相似匹配 (${matchResults.similarMatches.length})`" name="similar">
            <el-table :data="matchResults.similarMatches" max-height="400">
              <el-table-column label="PDF中的名称" prop="pdfName" />
              <el-table-column label="系统主名称" prop="competitionName" />
              <el-table-column label="匹配项" prop="matchedName">
                <template #default="scope">
                  <el-tag v-if="scope.row.competitionName !== scope.row.matchedName" size="small" type="warning">别名: {{ scope.row.matchedName }}</el-tag>
                  <span v-else>{{ scope.row.matchedName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="匹配相似度" prop="similarity" width="100" />
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-button link type="primary" @click="handleRemoveMatch('similarMatches', scope.$index)">排除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane :label="`未匹配 (${matchResults.unmatchedNames.length})`" name="unmatched">
            <el-row style="margin-bottom: 10px;">
              <el-col :span="24" style="text-align: right;">
                <el-button type="warning" link icon="Download" @click="handleExportUnmatched">导出未匹配名单</el-button>
              </el-col>
            </el-row>
            <el-table :data="matchResults.unmatchedNames" max-height="400">
              <el-table-column label="PDF中的名称">
                <template #default="scope">
                  {{ scope.row }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">
                  <el-button link type="primary" @click="handleManualMatch(scope.row, scope.$index)">手动关联并学习</el-button>
                  <el-button link type="success" @click="handleQuickCreate(scope.row, scope.$index)">快速新增</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleConfirmImport" :disabled="!matchResults || totalMatchedCount === 0 || importTags.length === 0">确认关联 ({{ totalMatchedCount }}条)</el-button>
          <el-button @click="importOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 手动关联选择对话框 -->
    <el-dialog title="选择关联竞赛" v-model="manualOpen" width="600px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="PDF名称">
          <el-input :value="currentUnmatchedName" disabled />
        </el-form-item>
        <el-form-item label="选择竞赛">
          <el-select v-model="selectedCompId" filterable remote :remote-method="remoteSearchComp" placeholder="搜索竞赛名称" style="width: 100%">
            <el-option v-for="item in compOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitManualMatch">确 定</el-button>
          <el-button @click="manualOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="Competition">
import { listCompetition, getCompetition, delCompetition, addCompetition, updateCompetition, analyzeCompetitionPdf, linkCompetitionPdf, manualLinkCompetitionPdf } from "@/api/competition/competition"
import { listDept } from "@/api/system/dept"
// 【新增】引入ref，已有可忽略
import { ref, computed } from 'vue'

const { proxy } = getCurrentInstance()
const { sys_competition_level, sys_competition_tag, sys_competition_status, sys_competition_del_flag, sys_competition_scope_type, sys_competition_category } = proxy.useDict('sys_competition_level', 'sys_competition_tag', 'sys_competition_status', 'sys_competition_del_flag', 'sys_competition_scope_type', 'sys_competition_category')
const competitionStatusOptions = computed(() => (sys_competition_status.value || []).filter(d => String(d?.value) !== "2"))

const competitionList = ref([])
const competitionDeptRelList = ref([])
const deptList = ref([])
// 【删除】冗余的 deptNameMap（无需手动映射）
// const deptNameMap = ref({})
// 【删除】冗余的 competitionDeptAllList（无需缓存）
// const competitionDeptAllList = ref([])

const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedCompetitionDeptRel = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// PDF导入相关
const importOpen = ref(false)
const importLoading = ref(false)
const importFile = ref(null)
const importTags = ref([])
const threshold = ref(0.7)
const matchResults = ref(null)
const activeTab = ref("exact")

// 手动关联相关
const manualOpen = ref(false)
const currentUnmatchedName = ref("")
const currentUnmatchedIndex = ref(-1)
const selectedCompId = ref(null)
const compOptions = ref([])


const totalMatchedCount = computed(() => {
  if (!matchResults.value) return 0
  return matchResults.value.exactMatches.length + 
         matchResults.value.highSimilarityMatches.length + 
         matchResults.value.similarMatches.length
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    // category: null,
    organizations: null,
    // level: null,
    // tags: null,
    scopeType: null,
    status: null,
    // 新增：多选接收字段
    categoryList: [],
    levelList: [],
    tagsList: []
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

// 【简化】获取学院列表（仅用于新增/修改时选择学院，无需构建映射）
function getDeptList() {
  deptList.value = []
  listDept({}).then(response => {
    console.log("部门接口返回完整数据：", response)
    deptList.value = response.rows || response.data || []
    console.log("最终学院列表：", deptList.value)
  }).catch(error => {
    console.error("获取学院列表失败：", error)
    proxy.$modal.msgError("获取学院列表失败，请检查接口权限或后台数据")
  })
}

// 【核心修改】直接使用后端返回的 deptNames 字段展示归属学院
function getBelongDeptName(row) {
  if (row.scopeType === '0') {
    return '全校'; // 适用范围为全校时显示
  }
  // 后端已拼接好学院名称，直接展示，无数据则显示未配置
  return row.deptNames || '未配置';
}

/** 【简化】查询总赛事列表（无需缓存关系数据） */
function getList() {
  loading.value = true
  listCompetition(queryParams.value).then(response => {
    competitionList.value = response.rows
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
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  // 手动清空多选数组，确保重置后下拉框无选中值
  queryParams.value.categoryList = []
  queryParams.value.levelList = []
  queryParams.value.tagsList = []
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

// --- PDF导入逻辑 ---

function handleOpenImport() {
  importOpen.value = true
  importFile.value = null
  importTags.value = []
  matchResults.value = null
  if (proxy.$refs.uploadRef) {
    proxy.$refs.uploadRef.clearFiles()
  }
}

function handleFileChange(file) {
  importFile.value = file.raw
}

function handleFileRemove() {
  importFile.value = null
}

function handleAnalyzePdf() {
  if (!importFile.value) return
  
  importLoading.value = true
  const formData = new FormData()
  formData.append("file", importFile.value)
  formData.append("threshold", threshold.value)
  
  analyzeCompetitionPdf(formData).then(response => {
    matchResults.value = response.data
    importLoading.value = false
    // 默认切换到有数据的标签页
    if (matchResults.value.exactMatches.length > 0) activeTab.value = "exact"
    else if (matchResults.value.highSimilarityMatches.length > 0) activeTab.value = "high"
    else if (matchResults.value.similarMatches.length > 0) activeTab.value = "similar"
    else activeTab.value = "unmatched"
  }).catch(() => {
    importLoading.value = false
  })
}

function handleRemoveMatch(type, index) {
  matchResults.value[type].splice(index, 1)
}

function handleConfirmImport() {
  const ids = [
    ...matchResults.value.exactMatches.map(m => m.competitionId),
    ...matchResults.value.highSimilarityMatches.map(m => m.competitionId),
    ...matchResults.value.similarMatches.map(m => m.competitionId)
  ]
  
  if (ids.length === 0) return
  
  proxy.$modal.confirm(`确认将选中的 ${ids.length} 条竞赛关联到标签吗？`).then(() => {
    importLoading.value = true
    return linkCompetitionPdf({
      competitionIds: ids,
      tagCodes: importTags.value,
      filename: importFile.value.name
    })
  }).then(() => {
    proxy.$modal.msgSuccess("导入关联成功")
    importOpen.value = false
    importLoading.value = false
    getList()
  }).catch(() => {
    importLoading.value = false
  })
}

// --- 手动关联逻辑 ---

function handleManualMatch(name, index) {
  currentUnmatchedName.value = name
  currentUnmatchedIndex.value = index
  selectedCompId.value = null
  compOptions.value = []
  manualOpen.value = true
}

function remoteSearchComp(query) {
  if (query !== '') {
    listCompetition({ name: query, pageNum: 1, pageSize: 20 }).then(response => {
      compOptions.value = response.rows
    })
  } else {
    compOptions.value = []
  }
}

function submitManualMatch() {
  if (!selectedCompId.value) return
  
  const comp = compOptions.value.find(c => c.id === selectedCompId.value)
  if (comp) {
    // 调用后端建立关联并学习别名
    manualLinkCompetitionPdf({
      competitionId: comp.id,
      pdfName: currentUnmatchedName.value
    }).then(() => {
      // 将手动匹配的项加入到“相似匹配”中以便后续一键打标签
      matchResults.value.similarMatches.push({
        competitionId: comp.id,
        competitionName: comp.name,
        pdfName: currentUnmatchedName.value,
        similarity: "手动关联",
        matchLevel: 3
      })
      // 从未匹配中移除
      matchResults.value.unmatchedNames.splice(currentUnmatchedIndex.value, 1)
      manualOpen.value = false
      proxy.$modal.msgSuccess("关联成功，系统已自动记录该名称为别名");
    })
  }
}

/** 导出未匹配名单 */
function handleExportUnmatched() {
  if (!matchResults.value || !matchResults.value.unmatchedNames.length) return;
  
  const content = "未匹配竞赛名称\n" + matchResults.value.unmatchedNames.join("\n");
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);
  link.setAttribute("href", url);
  link.setAttribute("download", `未匹配名单_${new Date().getTime()}.csv`);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/** 快速新增 */
function handleQuickCreate(name, index) {
  reset();
  getDeptList();
  form.value.name = name;
  currentUnmatchedIndex.value = index;
  open.value = true;
  title.value = "快速新增竞赛";
  
  // 覆盖提交逻辑，新增成功后自动关联
  const originalSubmit = submitForm;
  submitForm = () => {
    proxy.$refs["competitionRef"].validate(valid => {
      if (valid) {
        form.value.category = form.value.category.join(",");
        form.value.level = form.value.level.join(",");
        form.value.tags = form.value.tags.join(",");
        if (form.value.scopeType === '0') competitionDeptRelList.value = [];
        form.value.competitionDeptRelList = competitionDeptRelList.value;
        
        addCompetition(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          // 自动加入到匹配列表中
          matchResults.value.exactMatches.push({
            competitionId: response.data,
            competitionName: form.value.name,
            pdfName: form.value.name,
            similarity: "100%",
            matchLevel: 1
          });
          matchResults.value.unmatchedNames.splice(currentUnmatchedIndex.value, 1);
          // 恢复原始提交逻辑
          submitForm = originalSubmit;
          getList();
        });
      }
    });
  };
}


// 页面初始化加载
getList()
getDeptList()
</script>
