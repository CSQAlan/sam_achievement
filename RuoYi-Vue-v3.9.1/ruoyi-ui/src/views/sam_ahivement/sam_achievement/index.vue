<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类别" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入类别"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作品/团队名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入作品/团队名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="团队名称" prop="teamName">
        <el-input
          v-model="queryParams.teamName"
          placeholder="请输入团队名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="级别" prop="level">
        <el-input
          v-model="queryParams.level"
          placeholder="请输入级别"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖等级" prop="grade">
        <el-input
          v-model="queryParams.grade"
          placeholder="请输入获奖等级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛道" prop="track">
        <el-input
          v-model="queryParams.track"
          placeholder="请输入赛道"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证书编号" prop="certificateNo">
        <el-input
          v-model="queryParams.certificateNo"
          placeholder="请输入证书编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="组别" prop="groupId">
        <el-input
          v-model="queryParams.groupId"
          placeholder="请输入组别"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker clearable
          v-model="queryParams.awardTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择获奖时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="报名费金额" prop="fee">
        <el-input
          v-model="queryParams.fee"
          placeholder="请输入报名费金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实际报销金额" prop="reimbursementFee">
        <el-input
          v-model="queryParams.reimbursementFee"
          placeholder="请输入实际报销金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报销发放时间" prop="reimbursementDate">
        <el-date-picker clearable
          v-model="queryParams.reimbursementDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报销发放时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="项目序号" prop="itemIndex">
        <el-input
          v-model="queryParams.itemIndex"
          placeholder="请输入项目序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="素质提示序号" prop="qualityIndex">
        <el-input
          v-model="queryParams.qualityIndex"
          placeholder="请输入素质提示序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="submittedAt">
        <el-date-picker clearable
          v-model="queryParams.submittedAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提交时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核时间" prop="reviewedAt">
        <el-date-picker clearable
          v-model="queryParams.reviewedAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核结果" prop="reviewResult">
        <el-input
          v-model="queryParams.reviewResult"
          placeholder="请输入审核结果"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['sam_ahivement:sam_achievement:add']"
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
          v-hasPermi="['sam_ahivement:sam_achievement:edit']"
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
          v-hasPermi="['sam_ahivement:sam_achievement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['sam_ahivement:sam_achievement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sam_achievementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="achievementId" />
      <el-table-column label="届次 id" align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category" />
      <el-table-column label="作品/团队名称" align="center" prop="name" />
      <el-table-column label="团队名称" align="center" prop="teamName" />
      <el-table-column label="级别" align="center" prop="level" />
      <el-table-column label="获奖等级" align="center" prop="grade" />
      <el-table-column label="赛道" align="center" prop="track" />
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="组别" align="center" prop="groupId" />
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名费金额" align="center" prop="fee" />
      <el-table-column label="实际报销金额" align="center" prop="reimbursementFee" />
      <el-table-column label="报销百分比" align="center" prop="reimbursementRatio" />
      <el-table-column label="报销项目 id" align="center" prop="reimbursementItemId" />
      <el-table-column label="报销发放时间" align="center" prop="reimbursementDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reimbursementDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="项目序号" align="center" prop="itemIndex" />
      <el-table-column label="素质提示序号" align="center" prop="qualityIndex" />
      <el-table-column label="提交时间" align="center" prop="submittedAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submittedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="reviewedAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reviewedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核结果" align="center" prop="reviewResult" />
      <el-table-column label="审核不通过原因" align="center" prop="reviewReason" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sam_ahivement:sam_achievement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sam_ahivement:sam_achievement:remove']"
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

    <!-- 添加或修改成果主表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类别" prop="category">
          <el-input v-model="form.category" placeholder="请输入类别" />
        </el-form-item>
        <el-form-item label="作品/团队名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入作品/团队名称" />
        </el-form-item>
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-input v-model="form.level" placeholder="请输入级别" />
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入获奖等级" />
        </el-form-item>
        <el-form-item label="赛道" prop="track">
          <el-input v-model="form.track" placeholder="请输入赛道" />
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNo">
          <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
        </el-form-item>
        <el-form-item label="组别" prop="groupId">
          <el-input v-model="form.groupId" placeholder="请输入组别" />
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker clearable
            v-model="form.awardTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择获奖时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="报名费金额" prop="fee">
          <el-input v-model="form.fee" placeholder="请输入报名费金额" />
        </el-form-item>
        <el-form-item label="实际报销金额" prop="reimbursementFee">
          <el-input v-model="form.reimbursementFee" placeholder="请输入实际报销金额" />
        </el-form-item>
        <el-form-item label="报销发放时间" prop="reimbursementDate">
          <el-date-picker clearable
            v-model="form.reimbursementDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择报销发放时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="项目序号" prop="itemIndex">
          <el-input v-model="form.itemIndex" placeholder="请输入项目序号" />
        </el-form-item>
        <el-form-item label="素质提示序号" prop="qualityIndex">
          <el-input v-model="form.qualityIndex" placeholder="请输入素质提示序号" />
        </el-form-item>
        <el-form-item label="提交时间" prop="submittedAt">
          <el-date-picker clearable
            v-model="form.submittedAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择提交时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核时间" prop="reviewedAt">
          <el-date-picker clearable
            v-model="form.reviewedAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核结果" prop="reviewResult">
          <el-input v-model="form.reviewResult" placeholder="请输入审核结果" />
        </el-form-item>
        <el-form-item label="审核不通过原因" prop="reviewReason">
          <el-input v-model="form.reviewReason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listSam_achievement, getSam_achievement, delSam_achievement, addSam_achievement, updateSam_achievement } from "@/api/sam_ahivement/sam_achievement"

export default {
  name: "Sam_achievement",
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
      // 成果主表表格数据
      sam_achievementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sessionId: null,
        category: null,
        name: null,
        teamName: null,
        level: null,
        grade: null,
        track: null,
        certificateNo: null,
        groupId: null,
        awardTime: null,
        fee: null,
        reimbursementFee: null,
        reimbursementRatio: null,
        reimbursementItemId: null,
        reimbursementDate: null,
        itemIndex: null,
        qualityIndex: null,
        submittedAt: null,
        reviewedAt: null,
        reviewResult: null,
        reviewReason: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        sessionId: [
          { required: true, message: "届次 id不能为空", trigger: "blur" }
        ],
        level: [
          { required: true, message: "级别不能为空", trigger: "blur" }
        ],
        grade: [
          { required: true, message: "获奖等级不能为空", trigger: "blur" }
        ],
        certificateNo: [
          { required: true, message: "证书编号不能为空", trigger: "blur" }
        ],
        awardTime: [
          { required: true, message: "获奖时间不能为空", trigger: "blur" }
        ],
        reviewResult: [
          { required: true, message: "审核结果不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ],
        delFlag: [
          { required: true, message: "删除标志不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询成果主表列表 */
    getList() {
      this.loading = true
      listSam_achievement(this.queryParams).then(response => {
        this.sam_achievementList = response.rows
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
        achievementId: null,
        sessionId: null,
        category: null,
        name: null,
        teamName: null,
        level: null,
        grade: null,
        track: null,
        certificateNo: null,
        groupId: null,
        awardTime: null,
        fee: null,
        reimbursementFee: null,
        reimbursementRatio: null,
        reimbursementItemId: null,
        reimbursementDate: null,
        itemIndex: null,
        qualityIndex: null,
        submittedAt: null,
        reviewedAt: null,
        reviewResult: null,
        reviewReason: null,
        createBy: null,
        updateBy: null,
        createTime: null,
        updateTime: null,
        delFlag: null,
        remark: null
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
      this.ids = selection.map(item => item.achievementId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加成果主表"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const achievementId = row.achievementId || this.ids
      getSam_achievement(achievementId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改成果主表"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.achievementId != null) {
            updateSam_achievement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSam_achievement(this.form).then(response => {
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
      const achievementIds = row.achievementId || this.ids
      this.$modal.confirm('是否确认删除成果主表编号为"' + achievementIds + '"的数据项？').then(function() {
        return delSam_achievement(achievementIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('sam_ahivement/sam_achievement/export', {
        ...this.queryParams
      }, `sam_achievement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
