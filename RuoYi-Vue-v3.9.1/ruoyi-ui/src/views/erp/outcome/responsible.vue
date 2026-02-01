<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="作品名称" prop="workName">
        <el-input
          v-model="queryParams.workName"
          placeholder="请输入作品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖项等级" prop="awardLevel">
        <el-input
          v-model="queryParams.awardLevel"
          placeholder="请输入奖项等级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证书编号" prop="certNo">
        <el-input
          v-model="queryParams.certNo"
          placeholder="请输入证书编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker clearable
          v-model="queryParams.awardTime"
          type="daterange" 
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd">
        </el-date-picker>
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
          v-hasPermi="['erp:outcome:add']"
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
          v-hasPermi="['erp:outcome:edit']"
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
          v-hasPermi="['erp:outcome:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['erp:outcome:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="outcomeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="作品名称" align="center" prop="workName" />
      <el-table-column label="所属年份" align="center" prop="year" width="80" />
      <el-table-column label="成果类别" align="center" prop="category" />
      <el-table-column label="奖项等级" align="center" prop="awardLevel" />
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属学院" align="center" prop="deptId" />
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.erp_audit_status" :value="scope.row.auditStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:outcome:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['erp:outcome:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="作品名称" prop="workName">
              <el-input v-model="form.workName" placeholder="请输入作品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属年份" prop="year">
              <el-input v-model="form.year" placeholder="请输入所属年份" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
           <el-col :span="12">
              <el-form-item label="所属学院" prop="deptId">
                <treeselect
                  v-model="form.deptId"
                  :options="deptOptions"
                  :show-count="true"
                  placeholder="请选择所属学院"
                />
              </el-form-item>
           </el-col>
           <el-col :span="12">
              <el-form-item label="成果类别" prop="category">
                 <el-input v-model="form.category" placeholder="请输入成果类别" />
              </el-form-item>
           </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="奖项等级" prop="awardLevel">
              <el-input v-model="form.awardLevel" placeholder="请输入奖项等级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="级别类型" prop="levelType">
                <el-input v-model="form.levelType" placeholder="请输入级别类型" />
             </el-form-item>
          </el-col>
        </el-row>

        <el-row>
           <el-col :span="12">
              <el-form-item label="获奖时间" prop="awardTime">
                <el-date-picker clearable
                  v-model="form.awardTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择获奖时间">
                </el-date-picker>
              </el-form-item>
           </el-col>
           <el-col :span="12">
              <el-form-item label="组别" prop="groupType">
                  <el-input v-model="form.groupType" placeholder="请输入组别" />
              </el-form-item>
           </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="赛道" prop="track">
              <el-input v-model="form.track" placeholder="请输入赛道" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书编号" prop="certNo">
              <el-input v-model="form.certNo" placeholder="请输入证书编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
           <el-col :span="12">
             <el-form-item label="提交时间" prop="submitTime">
                <el-date-picker clearable
                  v-model="form.submitTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择提交时间">
                </el-date-picker>
             </el-form-item>
           </el-col>
           <el-col :span="12">
             <el-form-item label="报名费" prop="entryFee">
                <el-input v-model="form.entryFee" placeholder="请输入报名费" />
             </el-form-item>
           </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否报销" prop="isReimburse">
               <el-input v-model="form.isReimburse" placeholder="请输入是否申请报销" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="center">参赛选手信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddErpContestant">添加学生</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteErpContestant">删除学生</el-button>
          </el-col>
        </el-row>
        <el-table :data="erpContestantList" :row-class-name="rowErpContestantIndex" @selection-change="handleErpContestantSelectionChange" ref="erpContestant">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="学生学号" prop="studentCode">
            <template slot-scope="scope">
              <el-input v-model="scope.row.studentCode" placeholder="请输入学号" />
            </template>
          </el-table-column>
          <el-table-column label="排序" prop="sortOrder">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sortOrder" placeholder="1" />
            </template>
          </el-table-column>
          <el-table-column label="是否队长" prop="isLeader">
            <template slot-scope="scope">
              <el-input v-model="scope.row.isLeader" placeholder="0/1" />
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="center">指导老师信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddErpGuideTeacher">添加老师</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteErpGuideTeacher">删除老师</el-button>
          </el-col>
        </el-row>
        <el-table :data="erpGuideTeacherList" :row-class-name="rowErpGuideTeacherIndex" @selection-change="handleErpGuideTeacherSelectionChange" ref="erpGuideTeacher">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="教师工号" prop="teacherCode">
            <template slot-scope="scope">
              <el-input v-model="scope.row.teacherCode" placeholder="请输入工号" />
            </template>
          </el-table-column>
          <el-table-column label="排序" prop="sortOrder">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sortOrder" placeholder="1" />
            </template>
          </el-table-column>
          <el-table-column label="是否第一指导" prop="isLeader">
            <template slot-scope="scope">
               <el-input v-model="scope.row.isLeader" placeholder="0/1" />
            </template>
          </el-table-column>
        </el-table>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 注意：这里引入的API是通用的，后端会根据DataScope自动过滤出“我负责的”数据
import { listOutcome, getOutcome, delOutcome, addOutcome, updateOutcome } from "@/api/erp/outcome"
import { listDept } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "MyOutcome", // 组件名称建议改为 MyOutcome
  components: { Treeselect },
  dicts: ['erp_audit_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedErpContestant: [],
      checkedErpGuideTeacher: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 成果管理表格数据
      outcomeList: [],
      // 参赛选手表格数据
      erpContestantList: [],
      // 指导老师表格数据
      erpGuideTeacherList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        workName: null,
        awardLevel: null,
        certNo: null,
        awardTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        workName: [{ required: true, message: "作品名称不能为空", trigger: "blur" }],
        awardLevel: [{ required: true, message: "奖项等级不能为空", trigger: "blur" }],
        deptId: [{ required: true, message: "所属学院不能为空", trigger: "blur" }],
        year: [{ required: true, message: "年份不能为空", trigger: "blur" }],
        category: [{ required: true, message: "类别不能为空", trigger: "blur" }],
        levelType: [{ required: true, message: "级别类型不能为空", trigger: "blur" }],
        groupType: [{ required: true, message: "组别不能为空", trigger: "blur" }],
        awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "blur" }],
        submitTime: [{ required: true, message: "提交时间不能为空", trigger: "blur" }],
      }
    }
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询部门下拉树结构 */
    getDeptTree() {
      listDept().then(response => {
        this.deptOptions = this.handleTree(response.data, "deptId");
      });
    },
    /** 查询成果管理列表 */
    getList() {
      this.loading = true
      listOutcome(this.queryParams).then(response => {
        this.outcomeList = response.rows
        this.total = response.total
        this.loading = false
      })
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
        year: null,
        category: null,
        workName: null,
        levelType: null,
        awardLevel: null,
        track: null,
        certNo: null,
        groupType: null,
        contestId: null,
        deptId: null,
        awardTime: null,
        submitTime: null,
        entryFee: null,
        isReimburse: null,
      }
      this.erpContestantList = []
      this.erpGuideTeacherList = [] // 重置老师列表
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
      this.title = "添加成果 (负责人)"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getOutcome(id).then(response => {
        this.form = response.data
        this.erpContestantList = response.data.erpContestantList
        // 回显老师信息
        this.erpGuideTeacherList = response.data.erpGuideTeacherList || [] 
        this.open = true
        this.title = "修改成果 (负责人)"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.erpContestantList = this.erpContestantList
          // 提交时带上老师信息
          this.form.erpGuideTeacherList = this.erpGuideTeacherList 
          if (this.form.id != null) {
            updateOutcome(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addOutcome(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除？').then(function() {
        return delOutcome(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('erp/outcome/export', {
        ...this.queryParams
      }, `outcome_${new Date().getTime()}.xlsx`)
    },
    /** 参赛选手序号 */
    rowErpContestantIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 参赛选手添加按钮操作 */
    handleAddErpContestant() {
      let obj = {}; obj.studentCode = ""; obj.sortOrder = ""; obj.isLeader = "0";
      this.erpContestantList.push(obj)
    },
    /** 参赛选手删除按钮操作 */
    handleDeleteErpContestant() {
      if (this.checkedErpContestant.length == 0) {
        this.$modal.msgError("请先选择要删除的参赛选手数据")
      } else {
        const erpContestantList = this.erpContestantList
        const checkedErpContestant = this.checkedErpContestant
        this.erpContestantList = erpContestantList.filter(function(item) {
          return checkedErpContestant.indexOf(item.index) == -1
        })
      }
    },
    /** 参赛选手多选框 */
    handleErpContestantSelectionChange(selection) {
      this.checkedErpContestant = selection.map(item => item.index)
    },
    /** 指导老师序号 */
    rowErpGuideTeacherIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 指导老师添加按钮操作 */
    handleAddErpGuideTeacher() {
      let obj = {}; obj.teacherCode = ""; obj.sortOrder = ""; obj.isLeader = "0";
      this.erpGuideTeacherList.push(obj);
    },
    /** 指导老师删除按钮操作 */
    handleDeleteErpGuideTeacher() {
      if (this.checkedErpGuideTeacher.length == 0) {
        this.$modal.msgError("请先选择要删除的指导老师数据");
      } else {
        const erpGuideTeacherList = this.erpGuideTeacherList;
        const checkedErpGuideTeacher = this.checkedErpGuideTeacher;
        this.erpGuideTeacherList = erpGuideTeacherList.filter(function(item) {
          return checkedErpGuideTeacher.indexOf(item.index) == -1
        });
      }
    },
    /** 指导老师多选框 */
    handleErpGuideTeacherSelectionChange(selection) {
      this.checkedErpGuideTeacher = selection.map(item => item.index)
    },
  }
}
</script>