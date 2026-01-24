<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学院ID" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择学院ID" clearable>
          <el-option
            v-for="dict in erp_dept_id"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="成果类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择成果类别" clearable>
          <el-option
            v-for="dict in erp_outcome_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="奖项等级" prop="awardLevel">
        <el-select v-model="queryParams.awardLevel" placeholder="请选择奖项等级" clearable>
          <el-option
            v-for="dict in erp_award_rank"
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
          v-hasPermi="['erp:ratio:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['erp:ratio:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['erp:ratio:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['erp:ratio:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ratioList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="学院ID" align="center" prop="deptId">
        <template #default="scope">
          <dict-tag :options="erp_dept_id" :value="scope.row.deptId"/>
        </template>
      </el-table-column>
      <el-table-column label="成果类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="erp_outcome_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="奖项等级" align="center" prop="awardLevel">
        <template #default="scope">
          <dict-tag :options="erp_award_rank" :value="scope.row.awardLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="报销比例" align="center" prop="ratio" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['erp:ratio:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['erp:ratio:remove']">删除</el-button>
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

    <!-- 添加或修改报销比例对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ratioRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学院ID" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择学院ID">
            <el-option
              v-for="dict in erp_dept_id"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="成果类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择成果类别">
            <el-option
              v-for="dict in erp_outcome_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="奖项等级" prop="awardLevel">
          <el-select v-model="form.awardLevel" placeholder="请选择奖项等级">
            <el-option
              v-for="dict in erp_award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报销比例" prop="ratio">
          <el-input v-model="form.ratio" placeholder="请输入报销比例" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

<script setup name="Ratio">
import { listRatio, getRatio, delRatio, addRatio, updateRatio } from "@/api/erp/ratio"

const { proxy } = getCurrentInstance()
const { erp_outcome_category, erp_dept_id, erp_award_rank } = proxy.useDict('erp_outcome_category', 'erp_dept_id', 'erp_award_rank')

const ratioList = ref([])
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
    deptId: null,
    category: null,
    awardLevel: null,
  },
  rules: {
    deptId: [
      { required: true, message: "学院ID不能为空", trigger: "change" }
    ],
    category: [
      { required: true, message: "成果类别不能为空", trigger: "change" }
    ],
    awardLevel: [
      { required: true, message: "奖项等级不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询报销比例列表 */
function getList() {
  loading.value = true
  listRatio(queryParams.value).then(response => {
    ratioList.value = response.rows
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
    deptId: null,
    category: null,
    awardLevel: null,
    ratio: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("ratioRef")
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
  title.value = "添加报销比例"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getRatio(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改报销比例"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ratioRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateRatio(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRatio(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除报销比例编号为"' + _ids + '"的数据项？').then(function() {
    return delRatio(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('erp/ratio/export', {
    ...queryParams.value
  }, `ratio_${new Date().getTime()}.xlsx`)
}

getList()
</script>
