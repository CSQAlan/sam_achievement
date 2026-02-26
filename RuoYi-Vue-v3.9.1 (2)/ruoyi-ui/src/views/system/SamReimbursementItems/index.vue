<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="报销项目名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入报销项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报销时间" prop="reimbursementTime">
        <el-date-picker
          v-model="queryParams.reimbursementTime"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          clearable>
        </el-date-picker>
      </el-form-item>
      <el-form-item label="归属学院" prop="ownerDepId">
        <treeselect
          v-model="queryParams.ownerDepId"
          :options="deptOptions"
          :normalizer="normalizer"
          placeholder="请选择归属学院"
          clearable
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.reimbursement_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:SamReimbursementItems:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:SamReimbursementItems:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:SamReimbursementItems:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:SamReimbursementItems:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SamReimbursementItemsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="报销项目名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="报销时间" align="center" prop="reimbursementTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reimbursementTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总金额" align="center" prop="totalFee">
        <template slot-scope="scope">
          <span>{{ formatMoney(scope.row.totalFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已发放金额" align="center" prop="paidFee">
        <template slot-scope="scope">
          <span>{{ formatMoney(scope.row.paidFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报销项目数量" align="center" prop="amount" />
      <el-table-column label="归属学院" align="center" prop="ownerDepId" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.reimbursement_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:SamReimbursementItems:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:SamReimbursementItems:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleExportPdf(scope.row)"
            v-hasPermi="['system:SamReimbursementItems:export']"
          >导出PDF</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改报销项目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报销项目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入报销项目名称" />
        </el-form-item>
        <el-form-item label="报销时间" prop="reimbursementTime">
          <el-date-picker clearable
            v-model="form.reimbursementTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择报销时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="总金额" prop="totalFee">
          <el-input-number v-model="form.totalFee" :precision="2" :min="0" :max="9999999" placeholder="请输入总金额"  />
        </el-form-item>
        <el-form-item label="已发放金额" prop="paidFee">
          <el-input-number v-model="form.paidFee" :precision="2" :min="0" :max="9999999" placeholder="请输入已发放金额" />
        </el-form-item>
        <el-form-item label="报销项目数量" prop="amount">
          <el-input-number v-model="form.amount" :precision="0" :min="0" placeholder="报销项目数量"  />
        </el-form-item>
        <el-form-item label="归属学院" prop="ownerDepId">
          <treeselect
            v-model="form.ownerDepId"
            :options="deptOptions"
            :normalizer="normalizer"
            placeholder="请选择归属学院"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.reimbursement_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSamReimbursementItems, getSamReimbursementItems, delSamReimbursementItems, addSamReimbursementItems, updateSamReimbursementItems } from "@/api/system/SamReimbursementItems"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"
import { listDept } from "@/api/system/dept"

export default {
  name: "SamReimbursementItems",
  dicts: ['reimbursement_status'],
  components: {
    Treeselect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 报销项目表格数据
      SamReimbursementItemsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 部门树选项
      deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        reimbursementTime: null,
        ownerDepId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "报销项目名称不能为空", trigger: "blur" }
        ],
        reimbursementTime: [
          { required: true, message: "报销时间不能为空", trigger: "blur" }
        ],
        totalFee: [
          { required: true, message: "总金额不能为空", trigger: "blur" }
        ],
        ownerDepId: [
          { required: true, message: "归属学院不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
  },
  methods: {
    /** 查询报销项目列表 */
    getList() {
      this.loading = true
      listSamReimbursementItems(this.queryParams).then(response => {
        this.SamReimbursementItemsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      listDept().then(response => {
        this.deptOptions = this.handleTree(response.data, "deptId")
      })
    },
    /** 转换部门数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      }
    },
    /** 格式化金额 */
    formatMoney(money) {
      if (money == null) return '¥0.00'
      return '¥' + parseFloat(money).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        reimbursementTime: null,
        totalFee: null,
        paidFee: null,
        amount: null,
        ownerDepId: null,
        status: "0",
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加报销项目"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSamReimbursementItems(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改报销项目"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSamReimbursementItems(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSamReimbursementItems(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除报销项目编号为"' + ids + '"的数据项？').then(function() {
        return delSamReimbursementItems(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/SamReimbursementItems/export', {
        ...this.queryParams
      }, `SamReimbursementItems_${new Date().getTime()}.xlsx`)
    },
    /** 导出PDF按钮操作 */
    handleExportPdf(row) {
      this.$modal.confirm('是否确认导出报销项目"' + row.name + '"的PDF清单？').then(() => {
        this.loading = true
        const token = this.$store.getters.token
        const url = process.env.VUE_APP_BASE_API + '/system/SamReimbursementItems/exportPdf/' + row.id

        // 使用 fetch 下载文件，可以设置 header
        fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': 'Bearer ' + token
          }
        }).then(response => {
          if (response.ok) {
            return response.blob()
          }
          throw new Error('下载失败')
        }).then(blob => {
          // 创建下载链接
          const downloadUrl = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = downloadUrl
          a.download = `报销清单_${row.name}_${new Date().getTime()}.pdf`
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(downloadUrl)

          this.loading = false
          this.$modal.msgSuccess("PDF导出成功")
        }).catch(error => {
          this.loading = false
          this.$modal.msgError("PDF导出失败：" + error.message)
        })
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
