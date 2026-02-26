<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="获奖等级" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
          <el-option
            v-for="dict in dict.type.award_grade"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option
            v-for="dict in dict.type.reimbursement_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
            v-for="dict in dict.type.ratio_status"
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
          v-hasPermi="['system:SamReimbursementRatio:add']"
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
          v-hasPermi="['system:SamReimbursementRatio:edit']"
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
          v-hasPermi="['system:SamReimbursementRatio:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:SamReimbursementRatio:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SamReimbursementRatioList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.award_grade" :value="scope.row.grade"/>
        </template>
      </el-table-column>
      <el-table-column label="类别" align="center" prop="category">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.reimbursement_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="报销百分比" align="center" prop="ratio">
        <template slot-scope="scope">
          <span>{{ scope.row.ratio }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="归属学院" align="center" prop="ownerDepId" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ratio_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:SamReimbursementRatio:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:SamReimbursementRatio:remove']"
          >删除</el-button>
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

    <!-- 添加或修改报销比例对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择获奖等级">
            <el-option
              v-for="dict in dict.type.award_grade"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别">
            <el-option
              v-for="dict in dict.type.reimbursement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报销百分比" prop="ratio">
          <el-input-number v-model="form.ratio" :precision="0" :min="0" :max="100" :step="5" placeholder="请输入报销百分比" />
          <span style="margin-left: 10px;">%</span>
        </el-form-item>
        <el-form-item label="归属学院" prop="ownerDepId">
          <treeselect
            v-model="form.ownerDepId"
            :options="deptOptions"
            :normalizer="normalizer"
            placeholder="请选择归属学院（为空表示全校规则）"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.ratio_status"
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
import { listSamReimbursementRatio, getSamReimbursementRatio, delSamReimbursementRatio, addSamReimbursementRatio, updateSamReimbursementRatio } from "@/api/system/SamReimbursementRatio"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"
import { listDept } from "@/api/system/dept"

export default {
  name: "SamReimbursementRatio",
  dicts: ['award_grade', 'reimbursement_category', 'ratio_status'],
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
      // 报销比例表格数据
      SamReimbursementRatioList: [],
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
        grade: null,
        category: null,
        ownerDepId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        grade: [
          { required: true, message: "获奖等级不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
  },
  methods: {
    /** 查询报销比例列表 */
    getList() {
      this.loading = true
      listSamReimbursementRatio(this.queryParams).then(response => {
        this.SamReimbursementRatioList = response.rows
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
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        grade: null,
        category: null,
        ratio: null,
        ownerDepId: null,
        status: "1",
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
      this.title = "添加报销比例"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSamReimbursementRatio(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改报销比例"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSamReimbursementRatio(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSamReimbursementRatio(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除报销比例编号为"' + ids + '"的数据项？').then(function() {
        return delSamReimbursementRatio(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/SamReimbursementRatio/export', {
        ...this.queryParams
      }, `SamReimbursementRatio_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
