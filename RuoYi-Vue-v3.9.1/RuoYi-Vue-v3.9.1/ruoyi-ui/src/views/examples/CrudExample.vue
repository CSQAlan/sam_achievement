<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
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
    <el-dialog :title="dialog.dialogTitle" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="formRef" :model="dialog.formData" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialog.formData.name" placeholder="请输入名称" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialog.formData.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialog.close">取 消</el-button>
        <el-button type="primary" @click="dialog.submit(formRef)" :loading="dialog.loading">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CrudExample">
import { ref } from 'vue'
import { useCrud } from '@/composables'
// import { listData, getData, addData, updateData, delData } from '@/api/example'

// 模拟 API（实际使用时替换为真实 API）
const mockApi = {
  list: (params) => Promise.resolve({ rows: [], total: 0 }),
  get: (id) => Promise.resolve({ data: {} }),
  add: (data) => Promise.resolve({}),
  update: (data) => Promise.resolve({}),
  delete: (ids) => Promise.resolve({})
}

// 使用 CRUD Composable
const {
  // 表格数据
  dataList,
  loading,
  total,
  single,
  multiple,
  queryParams,
  
  // 对话框
  dialog,
  
  // 方法
  getList,
  handleQuery,
  resetQuery,
  handleSelectionChange,
  handlePagination,
  handleAdd,
  handleUpdate,
  handleDelete,
  handleExport
} = useCrud({
  listApi: mockApi.list,
  getApi: mockApi.get,
  addApi: mockApi.add,
  updateApi: mockApi.update,
  deleteApi: mockApi.delete,
  defaultQueryParams: {
    name: null,
    status: null
  },
  dialogOptions: {
    addTitle: '新增数据',
    editTitle: '修改数据'
  }
})

// 表单引用
const formRef = ref(null)
const queryRef = ref(null)
const showSearch = ref(true)

// 状态选项
const statusOptions = ref([
  { label: '启用', value: '0' },
  { label: '停用', value: '1' }
])

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
}

// 初始化
getList()
</script>
