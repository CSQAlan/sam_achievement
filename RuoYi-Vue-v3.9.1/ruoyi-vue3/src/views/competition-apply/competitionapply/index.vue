<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="申请编号" prop="applyNo">
        <el-input v-model="queryParams.applyNo" placeholder="请输入申请编号" clearable @keyup.enter="handleQuery" />
      </el-form-item> -->
      <el-form-item label="申请人用户ID" prop="applicantUserId">
        <el-input v-model="queryParams.applicantUserId" placeholder="请输入申请人用户ID" clearable @keyup.enter="handleQuery" />
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

    <!-- 添加或修改赛事申请对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="competitionapplyRef" :model="form" :rules="rules" label-width="100px">
        <!-- 申请编号 -->
        <!-- <el-form-item label="申请编号" prop="applyNo">
          <el-input v-model="form.applyNo" placeholder="请输入申请编号" />
        </el-form-item> -->
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

        <!-- 附件上传：使用RuoYi封装的UploadFile组件 -->
        <el-divider content-position="center">赛事申请附件（仅支持PDF）</el-divider>
        <el-form-item label="佐证文件" prop="attachments">
          <upload-file v-model="form.attachmentUrls" :limit="5" :file-size="50" :file-type="['pdf']"
            :disabled="false"></upload-file>
        </el-form-item>
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
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
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

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    // applyNo: null,
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
    // applyNo: [{ required: true, message: "申请编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "赛事名称不能为空", trigger: "blur" }],
    category: [{ required: true, message: "赛事类别不能为空", trigger: "change" }],
    organizations: [{ required: true, message: "盖章单位不能为空", trigger: "blur" }],
    level: [{ required: true, message: "赛事级别不能为空", trigger: "change" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

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
  reset()
}

/** 重置表单 */
function reset() {
  form.value = {
    id: null,
    // applyNo: "",
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
    // 回显附件（适配附件地址格式）
    if (response.data.attachmentUrls) {
      form.value.attachmentUrls = response.data.attachmentUrls
    }
    open.value = true
    title.value = "修改赛事申请"
  })
}

/** 提交表单 */
/** 提交表单 */
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
        // 新增：修复判断逻辑 + 确保刷新列表
        addCompetitionapply(submitData).then(res => {
          // 正确判断：只要 res.code === 200 就是成功（不管 data 是不是 null）
          if (res && res.code === 200) {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList(); // 新增后强制刷新列表，不用手动刷新页面
          } else {
            proxy.$modal.msgError(res?.msg || "新增失败");
          }
        }).catch(err => {
          // 即使接口抛错，但数据库可能已入库，这里做兼容提示
          proxy.$modal.msgWarning("新增提示失败，但数据可能已入库，请刷新列表确认");
          console.error("新增请求异常：", err);
          open.value = false;
          getList(); // 不管是否报错，都刷新列表
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