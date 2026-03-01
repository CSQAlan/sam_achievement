<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事名称" prop="competitionId">
        <el-select v-model="queryParams.competitionId" placeholder="请选择赛事名称" clearable>
          <el-option v-for="(name, id) in competitionNameMap" :key="id" :label="name" :value="id * 1" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="赛事名称" prop="competitionName">
        <el-input v-model="queryParams.competitionName" placeholder="请输入赛事名称" clearable @keyup.enter="handleQuery" />
      </el-form-item> -->
      <el-form-item label="届次" prop="session">
        <el-input v-model="queryParams.session" placeholder="请输入届次" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option v-for="dict in sys_competition_category" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="盖章单位" prop="organizationsUnit">
        <el-input v-model="queryParams.organizationsUnit" placeholder="请输入盖章单位" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="比赛级别" prop="competitionLevel">
        <el-select v-model="queryParams.competitionLevel" placeholder="请选择比赛级别" clearable>
          <el-option v-for="dict in sys_competition_level" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['test_competition:test_session:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['test_competition:test_session:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['test_competition:test_session:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['test_competition:test_session:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 展示表格 -->
    <el-table v-loading="loading" :data="test_sessionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="届次主键ID" align="center" prop="id" />
      <!-- 修正：prop改为competitionId，渲染用competitionNameMap匹配 -->
      <el-table-column label="赛事名称" align="center" prop="competitionId">
        <template #default="scope">
          <!-- 关键：ID转字符串匹配（接口返回id是字符串"1"，表格里是数字1） -->
          {{ competitionNameMap[scope.row.competitionId + ''] || scope.row.competitionId }}
        </template>
      </el-table-column>
      <!-- <el-table-column label="赛事名称" align="center" prop="competitionName" /> -->
      <el-table-column label="届次" align="center" prop="session" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="sys_competition_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="盖章单位" align="center" prop="organizationsUnit" />
      <el-table-column label="比赛级别" align="center" prop="competitionLevel">
        <template #default="scope">
          <dict-tag :options="sys_competition_level" :value="scope.row.competitionLevel" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['test_competition:test_session:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['test_competition:test_session:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改赛事届次对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="test_sessionRef" :model="form" :rules="rules" label-width="80px">
        <!-- 关键改造：把输入框改成下拉框 -->
        <el-form-item label="赛事名称" prop="competitionId">
          <el-select v-model="form.competitionId" placeholder="请选择赛事名称" clearable>
            <el-option v-for="(name, id) in competitionNameMap" :key="id" :label="name" :value="id * 1" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="赛事名称" prop="competitionName">
          <el-input v-model="form.competitionName" placeholder="请输入赛事名称" />
        </el-form-item> -->
        <el-form-item label="届次" prop="session">
          <el-input v-model="form.session" placeholder="请输入届次" />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-radio-group v-model="form.category">
            <el-radio v-for="dict in sys_competition_category" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="盖章单位" prop="organizationsUnit">
          <el-input v-model="form.organizationsUnit" placeholder="请输入盖章单位" />
        </el-form-item>
        <el-form-item label="比赛级别" prop="competitionLevel">
          <el-radio-group v-model="form.competitionLevel">
            <el-radio v-for="dict in sys_competition_level" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-divider content-position="center">标签（届次的子）信息</el-divider>
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
          <el-table-column label="标签名称" prop="tagValue" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.tagValue" placeholder="请选择标签">
                <el-option v-for="dict in sys_competition_tag" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
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

<script setup name="Test_session">
import { listTest_session, getTest_session, delTest_session, addTest_session, updateTest_session } from "@/api/test_competition/test_session"
// 导入你实际的赛事主表接口
import { listAllcompetition } from "@/api/competition/allcompetition"
const { proxy } = getCurrentInstance()
const { sys_competition_category, sys_competition_level, sys_competition_tag } = proxy.useDict('sys_competition_category', 'sys_competition_level', 'sys_competition_tag')

const test_sessionList = ref([])
const tagList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedTag = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 赛事名称映射表
const competitionNameMap = ref({})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    competitionId: null,
    competitionName: null,
    session: null,
    category: null,
    organizationsUnit: null,
    competitionLevel: null,
  },
  rules: {
    competitionId: [
      { required: true, message: "关联赛事主表ID不能为空", trigger: "blur" }
    ],
    // competitionName: [
    //   { required: true, message: "赛事名称不能为空", trigger: "blur" }
    // ],
    session: [
      { required: true, message: "届次不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "类别不能为空", trigger: "change" }
    ],
    organizationsUnit: [
      { required: true, message: "盖章单位不能为空", trigger: "blur" }
    ],
    competitionLevel: [
      { required: true, message: "比赛级别不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 修正：获取赛事名称映射（字段名改为name，ID转字符串）
async function getCompetitionNameMap() {
  try {
    const res = await listAllcompetition({})
    const map = {}
    console.log("赛事主表返回数据：", res);
    // 你的接口返回是{rows: [], total: xxx}格式
    if (res.rows && Array.isArray(res.rows)) {
      res.rows.forEach(item => {
        // 关键：字段名是name，不是competitionName；ID转字符串存储
        if (item.id && item.name) {
          map[item.id] = item.name;
        }
      });
    }
    competitionNameMap.value = map;
    console.log("最终映射表：", competitionNameMap.value); // 现在能看到{ "1": "蓝桥杯", "3": "挑战杯" }
  } catch (err) {
    console.error("获取赛事主表映射失败：", err);
  }
}

/** 查询赛事届次列表 */
async function getList() {
  loading.value = true
  await getCompetitionNameMap()
  listTest_session(queryParams.value).then(response => {
    test_sessionList.value = response.rows
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
    competitionName: null,
    session: null,
    category: null,
    organizationsUnit: null,
    competitionLevel: null,
    tags: null
  }
  tagList.value = []
  proxy.resetForm("test_sessionRef")
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
  title.value = "添加赛事届次"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getTest_session(_id).then(response => {
    form.value = response.data
    if (response.data.tagList && response.data.tagList.length > 0) {
      tagList.value = response.data.tagList.map(tag => {
        if (typeof tag.tagValue !== 'undefined') {
          return {
            ...tag,
            tagValues: Array.isArray(tag.tagValue) ? tag.tagValue : [tag.tagValue]
          }
        }
        return {
          ...tag,
          tagValues: []
        }
      })
    } else {
      tagList.value = []
    }
    open.value = true
    title.value = "修改赛事届次"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["test_sessionRef"].validate(valid => {
    if (valid) {
      form.value.tagList = tagList.value
      if (form.value.id != null) {
        updateTest_session(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addTest_session(form.value).then(response => {
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
    return delTest_session(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 标签序号 */
function rowTagIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 标签添加 */
function handleAddTag() {
  let obj = {}
  obj.tagName = ""
  obj.tagValues = []
  tagList.value.push(obj)
}

/** 标签删除 */
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

/** 标签复选框选中 */
function handleTagSelectionChange(selection) {
  checkedTag.value = selection.map(item => item.index)
}

/** 导出操作 */
function handleExport() {
  proxy.download('test_competition/test_session/export', {
    ...queryParams.value
  }, `test_session_${new Date().getTime()}.xlsx`)
}

// 初始化
getList()
</script>