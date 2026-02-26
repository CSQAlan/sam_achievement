# 前端代码结构优化说明

## 📁 新增目录结构

```
src/
├── composables/          # 组合式函数（可复用逻辑）
│   ├── useTable.js      # 表格通用逻辑
│   ├── useDialog.js     # 对话框通用逻辑
│   ├── useCrud.js       # CRUD 完整逻辑
│   ├── useForm.js       # 表单通用逻辑
│   ├── useDictData.js   # 字典数据处理
│   └── index.js         # 统一导出
├── hooks/               # 业务逻辑钩子
│   ├── useErpStudent.js # 学生相关业务
│   ├── useErpTeacher.js # 教师相关业务
│   └── index.js         # 统一导出
├── constants/           # 常量定义
│   └── index.js         # 全局常量
└── views/
    └── examples/        # 示例页面
        └── CrudExample.vue
```

## 🎯 核心 Composables 说明

### 1. useTable - 表格通用逻辑

处理列表页面的通用逻辑：分页、搜索、选择等。

```javascript
import { useTable } from '@/composables'

const {
  dataList,      // 表格数据
  loading,       // 加载状态
  total,         // 总数
  ids,           // 选中的 ID 列表
  single,        // 是否单选
  multiple,      // 是否多选
  queryParams,   // 查询参数
  getList,       // 获取列表
  handleQuery,   // 搜索
  resetQuery,    // 重置搜索
  handleSelectionChange,  // 选择变化
  handlePagination       // 分页变化
} = useTable({
  fetchDataFn: listApi,
  defaultPageSize: 10,
  defaultQueryParams: {}
})
```

### 2. useDialog - 对话框通用逻辑

处理弹窗的打开、关闭、提交等逻辑。

```javascript
import { useDialog } from '@/composables'

const dialog = useDialog({
  addTitle: '新增',
  editTitle: '编辑',
  onOpen: async (formData, isEdit) => {
    // 打开时的回调
  },
  onSubmit: async (formData, isEdit) => {
    // 提交时的回调
  }
})

// 使用
dialog.open()        // 打开新增
dialog.open({ id })  // 打开编辑
dialog.close()       // 关闭
dialog.submit(formRef) // 提交
```

### 3. useCrud - CRUD 完整逻辑

整合增删改查的完整逻辑，是 useTable 和 useDialog 的组合。

```javascript
import { useCrud } from '@/composables'

const {
  // 表格相关
  dataList,
  loading,
  total,
  single,
  multiple,
  queryParams,
  getList,
  handleQuery,
  resetQuery,
  handleSelectionChange,
  handlePagination,
  
  // 对话框相关
  dialog,
  
  // CRUD 操作
  handleAdd,
  handleUpdate,
  handleDelete,
  handleExport
} = useCrud({
  listApi,      // 列表 API
  getApi,       // 获取详情 API
  addApi,       // 新增 API
  updateApi,    // 更新 API
  deleteApi,    // 删除 API
  exportApi,    // 导出 API
  defaultQueryParams: {},
  dialogOptions: {}
})
```

### 4. useForm - 表单通用逻辑

处理表单验证、重置、提交等逻辑。

```javascript
import { useForm } from '@/composables'

const {
  formRef,
  formData,
  formRules,
  loading,
  resetForm,
  validate,
  submitForm,
  setFormData,
  getFormData
} = useForm({
  initialValues: {},
  rules: {},
  onSubmit: async (data) => {
    // 提交逻辑
  }
})
```

### 5. useDictData - 字典数据处理

简化字典数据的使用。

```javascript
import { useDictData, getDictLabel } from '@/composables'

// 获取字典数据
const { sys_user_sex, sys_normal_disable } = useDictData(
  'sys_user_sex',
  'sys_normal_disable'
)

// 获取字典标签
const label = getDictLabel(sys_user_sex, '0')
```

## 🔧 业务 Hooks 说明

### useErpStudent - 学生相关业务

```javascript
import { useErpStudent } from '@/hooks'

const { getStudentByCode, autoFillStudent } = useErpStudent()

// 根据学号获取学生信息
const student = await getStudentByCode('20210001')

// 自动填充学生信息
await autoFillStudent(row)
```

### useErpTeacher - 教师相关业务

```javascript
import { useErpTeacher } from '@/hooks'

const { getTeacherByCode, autoFillTeacher } = useErpTeacher()

// 根据工号获取教师信息
const teacher = await getTeacherByCode('T001')

// 自动填充教师信息
await autoFillTeacher(row)
```

## 📝 使用示例

### 简单的 CRUD 页面

```vue
<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="名称" prop="name" />
      <!-- 更多列... -->
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 编辑对话框 -->
    <el-dialog :title="dialog.dialogTitle" v-model="dialog.visible">
      <el-form ref="formRef" :model="dialog.formData" :rules="formRules">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialog.formData.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.close">取消</el-button>
        <el-button type="primary" @click="dialog.submit(formRef)">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Example">
import { ref } from 'vue'
import { useCrud } from '@/composables'
import { listData, getData, addData, updateData, delData } from '@/api/example'

// 使用 CRUD Composable
const {
  dataList,
  loading,
  total,
  single,
  multiple,
  queryParams,
  dialog,
  getList,
  handleQuery,
  resetQuery,
  handleSelectionChange,
  handlePagination,
  handleAdd,
  handleUpdate,
  handleDelete
} = useCrud({
  listApi: listData,
  getApi: getData,
  addApi: addData,
  updateApi: updateData,
  deleteApi: delData,
  defaultQueryParams: { name: null },
  dialogOptions: {
    addTitle: '新增数据',
    editTitle: '修改数据'
  }
})

const formRef = ref(null)
const queryRef = ref(null)

const formRules = {
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }]
}

// 初始化
getList()
</script>
```

## 🎨 优势

### 1. 代码复用
- 通用逻辑抽取到 composables，避免重复代码
- 业务逻辑抽取到 hooks，便于维护

### 2. 类型安全
- 统一的常量定义，避免魔法值
- 清晰的函数签名和返回值

### 3. 易于测试
- 逻辑与视图分离
- 纯函数易于单元测试

### 4. 可维护性
- 代码结构清晰
- 职责分明
- 易于扩展

### 5. 开发效率
- 减少样板代码
- 统一的开发模式
- 快速搭建 CRUD 页面

## 📚 迁移指南

### 旧代码（传统方式）

```vue
<script setup>
import { ref, reactive, toRefs } from 'vue'

const loading = ref(false)
const dataList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null
  }
})
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listData(queryParams.value).then(res => {
    dataList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// ... 更多重复代码
</script>
```

### 新代码（使用 Composables）

```vue
<script setup>
import { useCrud } from '@/composables'
import { listData, getData, addData, updateData, delData } from '@/api/example'

const {
  dataList,
  loading,
  total,
  single,
  multiple,
  queryParams,
  dialog,
  getList,
  handleQuery,
  handleSelectionChange,
  handleAdd,
  handleUpdate,
  handleDelete
} = useCrud({
  listApi: listData,
  getApi: getData,
  addApi: addData,
  updateApi: updateData,
  deleteApi: delData,
  defaultQueryParams: { name: null }
})

getList()
</script>
```

## 🚀 最佳实践

1. **优先使用 Composables**：对于通用逻辑，优先使用已有的 composables
2. **业务逻辑抽取**：将业务相关的逻辑抽取到 hooks 目录
3. **常量统一管理**：使用 constants 目录管理常量，避免硬编码
4. **组件拆分**：将大组件拆分为更小的子组件
5. **命名规范**：使用清晰的命名，遵循 Vue 3 风格指南

- 
