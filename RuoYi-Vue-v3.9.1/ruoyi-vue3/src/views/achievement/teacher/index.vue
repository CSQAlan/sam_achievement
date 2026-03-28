<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="教师id" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="请输入教师id"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="教师姓名" prop="teacherName">
        <el-input
          v-model="queryParams.teacherName"
          placeholder="请输入教师姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="教师工号" prop="no">
        <el-input
          v-model="queryParams.no"
          placeholder="请输入教师工号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学院" prop="school">
        <el-input
          v-model="queryParams.school"
          placeholder="请输入学院"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="院系" prop="department">
        <el-input
          v-model="queryParams.department"
          placeholder="请输入院系"
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
          v-hasPermi="['achievement:teacher:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['achievement:teacher:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['achievement:teacher:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Upload"
          @click="handleImport"
          v-hasPermi="['achievement:teacher:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['achievement:teacher:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="teacherList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="教师id" align="center" prop="id" />
      <el-table-column label="教师姓名" align="center" prop="teacherName" />
      <el-table-column label="工号" align="center" prop="no" />
      <el-table-column label="学院" align="center" prop="schoolName" />
      <el-table-column label="院系" align="center" prop="departmentName" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:teacher:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:teacher:remove']">删除</el-button>
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

    <!-- 添加或修改教师档案对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="teacherRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教师姓名" prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-form-item label="工号" prop="no">
          <el-input v-model="form.no" placeholder="请输入工号" />
        </el-form-item>
<el-form-item label="所属机构" prop="school">
  <el-cascader
    v-model="deptCascaderValue"
    :options="deptOptions"
    :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
    placeholder="请选择学院/院系"
    clearable
    filterable
    class="full-width-cascader"
    @change="handleDeptCascaderChange"
  />
</el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 教师导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的教师数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Teacher">
import { getToken } from "@/utils/auth";
import { listTeacher, getTeacher, delTeacher, addTeacher, updateTeacher } from "@/api/achievement/teacher"
import { listDept } from "@/api/system/dept"
import { handleTree } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()

const teacherList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const deptOptions = ref([])
const deptCascaderValue = ref([])

/*** 教师导入参数 */
const upload = reactive({
  // 是否显示弹出层（教师导入）
  open: false,
  // 弹出层标题（教师导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的教师数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/achievement/teacher/importData"
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    id: null,
    teacherName: null,
    no: null,
    school: null,
    department: null
  },
  rules: {
    teacherName: [
      { required: true, message: "教师姓名不能为空", trigger: "blur" }
    ],
    no: [
      { required: true, message: "工号不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询部门树结构 */
function getDeptTree() {
  listDept().then(response => {
    const tree = handleTree(response.data, "deptId");
    // Skip Level 1 (Root/University) to start directly from Level 2 (College)
    let processedTree = [];
    if (tree && tree.length > 0 && tree[0].children) {
      processedTree = tree[0].children;
    } else {
      processedTree = tree;
    }

    // For teachers, truncate at Level 3 (Department). Remove Level 4 (Major) children.
    processedTree.forEach(college => {
      if (college.children) {
        college.children.forEach(dept => {
          // Dept is Level 3, its children are Level 4. Remove them.
          if (dept.children) {
            delete dept.children;
          }
        });
      }
    });
    deptOptions.value = processedTree;
  });
}

/** 部门级联选择器变化处理 */
function handleDeptCascaderChange(value) {
  if (value && value.length >= 2) {
    // Starting from Level 2: value[0] is school (College), value[1] is department (Dept)
    form.value.school = value[0] || '';
    form.value.department = value[1] || '';
  } else {
    form.value.school = '';
    form.value.department = '';
  }
}

/** 查询教师档案列表 */
function getList() {
  loading.value = true
  listTeacher(queryParams.value).then(response => {
    teacherList.value = response.rows
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
    teacherName: null,
    no: null,
    school: null,
    department: null
  }
  deptCascaderValue.value = []
  proxy.resetForm("teacherRef")
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
  getDeptTree()
  open.value = true
  title.value = "添加教师档案"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getDeptTree()
  const _id = row.id || ids.value
  getTeacher(_id).then(response => {
    form.value = response.data
    // 回填级联选择器
    const values = [];
    if (form.value.school) values.push(Number(form.value.school));
    if (form.value.department) values.push(Number(form.value.department));
    deptCascaderValue.value = values;

    open.value = true
    title.value = "修改教师档案"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["teacherRef"].validate(valid => {
    if (valid) {
      if (!form.value.department) {
        proxy.$modal.msgError("请选择完整的所属机构（需选择到院系）");
        return;
      }
      if (form.value.id != null) {
        updateTeacher(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addTeacher(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除教师档案编号为"' + _ids + '"的数据项？').then(function() {
    return delTeacher(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('achievement/teacher/export', {
    ...queryParams.value
  }, `teacher_${new Date().getTime()}.xlsx`)
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "教师导入";
  upload.open = true;
};
/** 下载模板操作 */
function importTemplate() {
  proxy.download("achievement/teacher/importTemplate", {
  }, `teacher_template_${new Date().getTime()}.xlsx`)
};
/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
  getList();
};
/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
};

getList()
</script>
<style scoped>
.full-width-cascader {
  width: 100%;
}

/* 级联选择器容器 */
:deep(.el-cascader) {
  width: 100%;
}

/* 输入框区域 */
:deep(.el-cascader .el-input),
:deep(.el-cascader .el-input__wrapper) {
  height: auto !important;
  min-height: 34px;
}

/* 标签容器 - 允许换行 */
:deep(.el-cascader__tags) {
  flex-wrap: wrap !important;
  max-width: 100% !important;
  margin: 0 !important;
}

/* 单个标签 - 允许换行 */
:deep(.el-cascader__tag) {
  white-space: normal !important;
  word-break: break-all !important;
  overflow: visible !important;
  height: auto !important;
  margin: 2px 0 !important;
}

/* 标签文本 */
:deep(.el-cascader__tag-text) {
  white-space: normal !important;
  word-break: break-all !important;
}
</style>