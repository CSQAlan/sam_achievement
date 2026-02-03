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
      <!-- 可选优化：查询栏也加赛事名称下拉，方便按赛事筛选 -->
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sessionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="届次主键" align="center" prop="id" /> -->
      <el-table-column label="赛事名称" align="center" prop="competitionId">
        <!-- 核心修改1：表格中ID映射为赛事名称，无匹配则显示原ID -->
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
          <!-- 核心修改2：把赛事主键输入框改成下拉选择框，显示去重后的赛事名称 -->
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
        <!-- <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item> -->
        <!-- <el-divider content-position="center">标签信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddTag">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteTag">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="tagList" :row-class-name="rowTagIndex" @selection-change="handleTagSelectionChange" ref="tag">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50" />
          <el-table-column label="标签名称" prop="tagName" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.tagName" placeholder="请输入标签名称" />
            </template>
          </el-table-column>
          <el-table-column label="适用学院" prop="depId" width="150">
            <template #default="scope">
             
              <el-input v-model="scope.row.depId" placeholder="请输入适用学院" />
            </template>
          </el-table-column>
        </el-table> -->
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

<script setup name="Session">
import { listSession, getSession, delSession, addSession, updateSession } from "@/api/session/session"
// 核心引入：赛事主表接口，用于获取所有赛事列表
import { listCompetition } from "@/api/competition/competition"

const { proxy } = getCurrentInstance()
const { sys_competition_tag, sys_competition_status, sys_competition_del_flag, sys_competition_category, sys_competition_level } = proxy.useDict('sys_competition_tag', 'sys_competition_status', 'sys_competition_del_flag', 'sys_competition_category', 'sys_competition_level')

const sessionList = ref([])
const tagList = ref([])
// 核心定义：赛事主表列表（去重后），供下拉框和表格映射使用
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
      // 校验提示修改为“赛事名称不能为空”，更直观
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

// 核心方法1：获取赛事主表列表并做去重处理（根据赛事名称去重）
function getCompetitionList() {
  // 传空参数获取所有赛事
  listCompetition({}).then(response => {
    // 适配若依不同接口返回格式：rows / data
    const originList = response.rows || response.data || []
    // 按赛事名称去重：利用Map的key唯一性，name为key，保留第一个匹配项
    const uniqueMap = new Map()
    originList.forEach(item => {
      if (item.name && !uniqueMap.has(item.name)) {
        uniqueMap.set(item.name, item)
      }
    })
    // 转换为数组赋值给下拉框
    competitionList.value = Array.from(uniqueMap.values())
    console.log("去重后的赛事列表：", competitionList.value)
  }).catch(error => {
    console.error("获取赛事主表列表失败：", error)
    proxy.$modal.msgError("获取赛事列表失败，请先添加赛事主表数据")
  })
}

// 核心方法2：根据赛事ID获取对应的赛事名称（用于表格映射）
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
    // remark: null,
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

/** 新增按钮操作 - 重新获取赛事列表，确保数据最新 */
function handleAdd() {
  reset()
  getCompetitionList()
  open.value = true
  title.value = "添加赛事届次"
}

/** 修改按钮操作 - 重新获取赛事列表，适配回显 */
function handleUpdate(row) {
  reset()
  getCompetitionList()
  const _id = row.id || ids.value
  getSession(_id).then(response => {
    form.value = response.data
    // 重点：将后台返回的tags字符串（如"标签1,标签2"）转为数组
    if (form.value.tags && typeof form.value.tags === 'string') {
      form.value.tags = form.value.tags.split(',')
    } else {
      form.value.tags = [] // 无数据时给空数组
    }
    tagList.value = response.data.tagList
    open.value = true
    title.value = "修改赛事届次"
  })
}

/** 提交按钮 - 无需修改，提交的还是competitionId（ID值），后台兼容 */
function submitForm() {
  proxy.$refs["sessionRef"].validate(valid => {
    if (valid) {
      form.value.tags = form.value.tags.join(",")
      // form.value.tagList = tagList.value
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
  // obj.remark = ""
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

/** 导出按钮操作 */
function handleExport() {
  proxy.download('session/session/export', {
    ...queryParams.value
  }, `session_${new Date().getTime()}.xlsx`)
}

// 页面初始化：同时加载届次列表和赛事主表列表
getList()
getCompetitionList()
</script>