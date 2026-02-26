# 快速开始指南

## 🚀 5 分钟创建一个 CRUD 页面

### 步骤 1：创建 API 文件

在 `src/api/` 目录下创建你的 API 文件：

```javascript
// src/api/example/user.js
import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'delete'
  })
}
```

### 步骤 2：创建页面文件

在 `src/views/` 目录下创建你的页面：

```vue
<!-- src/views/example/user/index.vue -->
<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          placeholder="请输入手机号码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
          <el-option
            v-for="dict in sys_normal_disable"
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

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="userId" />
      <el-table-column label="用户名称" align="center" prop="userName" show-overflow-tooltip />
      <el-table-column label="用户昵称" align="center" prop="nickName" show-overflow-tooltip />
      <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">
            删除
          </el-button>
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="dialog.dialogTitle" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="formRef" :model="dialog.formData" :rules="formRules" label-width="80px">
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="dialog.formData.nickName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="dialog.formData.phonenumber" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="dialog.formData.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="dialog.formData.userName" placeholder="请输入用户名称" />
        </el-form-item>
        <el-form-item label="用户密码" prop="password" v-if="!dialog.isEdit">
          <el-input v-model="dialog.formData.password" placeholder="请输入用户密码" type="password" />
        </el-form-item>
        <el-form-item label="用户性别">
          <el-select v-model="dialog.formData.sex" placeholder="请选择性别">
            <el-option
              v-for="dict in sys_user_sex"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dialog.formData.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dialog.formData.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.close">取 消</el-button>
        <el-button type="primary" @click="dialog.submit(formRef)" :loading="dialog.loading">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User">
import { ref } from 'vue'
import { useCrud, useDictData } from '@/composables'
import { listUser, getUser, addUser, updateUser, delUser } from '@/api/example/user'

// 使用字典数据
const { sys_user_sex, sys_normal_disable } = useDictData('sys_user_sex', 'sys_normal_disable')

// 使用 CRUD Composable（核心代码，只需这一段！）
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
  handleDelete,
  handleExport
} = useCrud({
  listApi: listUser,
  getApi: getUser,
  addApi: addUser,
  updateApi: updateUser,
  deleteApi: delUser,
  exportApi: '/system/user/export',
  defaultQueryParams: {
    userName: null,
    phonenumber: null,
    status: null
  },
  dialogOptions: {
    addTitle: '添加用户',
    editTitle: '修改用户'
  }
})

// 其他状态
const formRef = ref(null)
const queryRef = ref(null)
const showSearch = ref(true)

// 表单验证规则
const formRules = {
  userName: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  password: [
    { required: true, message: '用户密码不能为空', trigger: 'blur' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 初始化
getList()
</script>
```

### 步骤 3：配置路由

在路由配置中添加你的页面：

```javascript
// src/router/index.js
{
  path: '/example',
  component: Layout,
  children: [
    {
      path: 'user',
      component: () => import('@/views/example/user/index'),
      name: 'User',
      meta: { title: '用户管理', icon: 'user' }
    }
  ]
}
```

## 🎉 完成！

就这么简单！你已经创建了一个完整的 CRUD 页面，包含：

- ✅ 列表展示
- ✅ 分页功能
- ✅ 搜索功能
- ✅ 新增功能
- ✅ 编辑功能
- ✅ 删除功能
- ✅ 导出功能
- ✅ 批量操作
- ✅ 表单验证

## 💡 进阶技巧

### 1. 自定义对话框行为

```javascript
const { dialog, ... } = useCrud({
  // ... 其他配置
  dialogOptions: {
    addTitle: '添加用户',
    editTitle: '修改用户'
  },
  // 打开前的回调
  beforeAdd: async (formData) => {
    // 设置默认值
    formData.status = '0'
  },
  // 编辑前的回调
  beforeEdit: async (formData) => {
    // 加载额外数据
    const roles = await getRoles()
    formData.roles = roles
  },
  // 添加后的回调
  afterAdd: async (formData) => {
    console.log('添加成功', formData)
  },
  // 编辑后的回调
  afterEdit: async (formData) => {
    console.log('编辑成功', formData)
  }
})
```

### 2. 自定义删除行为

```javascript
const { ... } = useCrud({
  // ... 其他配置
  // 删除前的回调
  beforeDelete: async (ids) => {
    // 自定义确认逻辑
    const hasChildren = await checkHasChildren(ids)
    if (hasChildren) {
      throw new Error('该数据有子节点，不能删除')
    }
  },
  // 删除后的回调
  afterDelete: async (ids) => {
    console.log('删除成功', ids)
  }
})
```

### 3. 使用业务 Hooks

```javascript
import { useErpStudent } from '@/hooks'

const { getStudentByCode, autoFillStudent } = useErpStudent()

// 在表格中使用
const handleStudentCodeBlur = async (row) => {
  await autoFillStudent(row)
}
```

### 4. 使用常量

```javascript
import { PAGINATION, DATE_FORMAT, STATUS } from '@/constants'

// 使用分页常量
const pageSize = PAGINATION.DEFAULT_PAGE_SIZE

// 使用日期格式
const dateFormat = DATE_FORMAT.DATE

// 使用状态常量
const enableStatus = STATUS.ENABLE
```

### 5. 使用配置

```javascript
import config from '@/config'

// 使用表格配置
const tableConfig = config.table

// 使用上传配置
const uploadConfig = config.upload
```

## 📚 更多示例

查看 `src/views/examples/` 目录下的示例文件：

- `CrudExample.vue` - 基础 CRUD 示例
- `src/views/erp/dept_approved/index.refactored.vue` - 复杂业务示例

## 🔗 相关文档

- [代码结构说明](./CODE_STRUCTURE.md)
- [重构总结](./REFACTORING_SUMMARY.md)
- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)

## ❓ 常见问题

### Q: 如何处理复杂的表单验证？

A: 在 `formRules` 中定义验证规则，支持自定义验证器：

```javascript
const formRules = {
  age: [
    { required: true, message: '年龄不能为空', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value < 18) {
          callback(new Error('年龄必须大于18岁'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}
```

### Q: 如何处理级联选择？

A: 使用 `watch` 监听变化：

```javascript
import { watch } from 'vue'

watch(() => dialog.formData.provinceId, (newVal) => {
  if (newVal) {
    // 加载城市列表
    loadCities(newVal)
  }
})
```

### Q: 如何实现树形表格？

A: 使用 Element Plus 的树形表格功能：

```vue
<el-table :data="dataList" row-key="id" :tree-props="{ children: 'children' }">
  <!-- 列定义 -->
</el-table>
```

### Q: 如何自定义导出功能？

A: 传入自定义的导出 API：

```javascript
const { handleExport } = useCrud({
  exportApi: '/custom/export/path'
})
```

## 🎯 下一步

- 阅读 [代码结构说明](./CODE_STRUCTURE.md) 了解更多细节
- 查看 [重构总结](./REFACTORING_SUMMARY.md) 了解重构效果
- 尝试创建你的第一个页面！

---

祝你开发愉快！🚀
