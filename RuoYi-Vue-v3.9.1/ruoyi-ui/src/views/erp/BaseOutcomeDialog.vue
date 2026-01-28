<!-- src/components/outcome/BaseOutcomeDialog.vue -->
<template>
  <el-dialog :title="dialogTitle" v-model="visible" width="800px" append-to-body @close="cancel">
    <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px" :disabled="readOnly">
      <!-- 基础字段 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="所属年份" prop="year">
            <el-input v-model="form.year" placeholder="例：2026" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属学院" prop="deptId">
            <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="请选择所属学院"
                check-strictly
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="成果类别" prop="category">
            <el-input v-model="form.category" placeholder="例：学科竞赛" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="作品名称" prop="workName">
            <el-input v-model="form.workName" placeholder="请输入比赛或作品名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="级别类型" prop="levelType">
            <el-select v-model="form.levelType" placeholder="请选择(如国家级)">
              <el-option
                  v-for="dict in erp_award_level_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="奖项等级" prop="awardLevel">
            <el-select v-model="form.awardLevel" placeholder="请选择(如一等奖)">
              <el-option
                  v-for="dict in erp_award_rank"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
              />
            </el-select>
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
          <el-form-item label="组别" prop="groupType">
            <el-select v-model="form.groupType" placeholder="请选择组别">
              <el-option
                  v-for="dict in erp_group_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="证书编号" prop="certNo">
            <el-input v-model="form.certNo" placeholder="请输入证书编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="获奖时间" prop="awardTime">
            <el-date-picker
                clearable
                v-model="form.awardTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="报名费" prop="entryFee">
            <el-input v-model="form.entryFee" placeholder="请输入金额(元)">
              <template #append>元</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 允许 B 往基础表单中插扩展字段（可选） -->
      <slot name="extra-form" :form="form"></slot>

      <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>

      <!-- 报销申请 -->
      <el-form-item prop="isReimburse" class="no-for-label-item">
        <div class="inline-form-item">
          <div class="inline-label">是否申请报销</div>

          <el-radio-group v-model="form.isReimburse">
            <el-radio :label="1">是 (需要上传凭证)</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </div>
      </el-form-item>


      <!-- 凭证（这里把重复结构合并，只根据 isReimburse 决定是否显示财务凭证） -->
      <div class="voucher-box">
        <el-divider content-position="left">必要凭证</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="奖状(证书)" prop="fileAward">
              <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" />
              <div class="el-upload__tip">获奖证书或奖状扫描件</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="比赛通知" prop="fileNotice">
              <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" />
              <div class="el-upload__tip">官方发布的比赛通知文件</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="参赛照片" prop="fileWork">
              <file-upload v-model="form.fileWork" :limit="1" :fileSize="20" :fileType="['pdf']" />
              <div class="el-upload__tip">参赛照片</div>
            </el-form-item>
          </el-col>
        </el-row>

        <template v-if="form.isReimburse === 1">
          <el-divider content-position="left">财务凭证</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="支付记录" prop="filePayment">
                <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" />
                <div class="el-upload__tip">报名费转账截图/回单</div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="发票" prop="fileInvoice">
                <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                <div class="el-upload__tip">正规电子发票</div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="收款码" prop="fileReceiptCode">
                <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" />
                <div class="el-upload__tip">用于接收报销款</div>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </div>

      <el-divider content-position="center">参赛选手信息</el-divider>
      <el-row :gutter="10" class="mb8" v-if="!readOnly">
        <el-col :span="1.5">
          <el-button type="primary" icon="Plus" @click="handleAddErpContestant">添加学生</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="Delete" @click="handleDeleteErpContestant">删除选中</el-button>
        </el-col>
      </el-row>

      <el-table
          :data="erpContestantList"
          :row-class-name="rowErpContestantIndex"
          @selection-change="handleErpContestantSelectionChange"
          ref="erpContestant"
      >
        <el-table-column type="selection" width="50" align="center" v-if="!readOnly" />
        <el-table-column label="序号" align="center" prop="index" width="50" />
        <el-table-column label="学生学号" prop="studentCode">
          <template #default="scope">
            <el-input v-model="scope.row.studentCode" placeholder="请输入学号" :disabled="readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.sortOrder" :min="1" controls-position="right" :disabled="readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="是否队长" prop="isLeader" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.isLeader" placeholder="请选择" :disabled="readOnly">
              <el-option label="是 (第一负责人)" :value="1" />
              <el-option label="否 (队员)" :value="0" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="center">指导老师信息</el-divider>
      <el-row :gutter="10" class="mb8" v-if="!readOnly">
        <el-col :span="1.5">
          <el-button type="primary" icon="Plus" @click="handleAddTeacher">添加老师</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="Delete" @click="handleDeleteTeacher">删除选中</el-button>
        </el-col>
      </el-row>

      <el-table :data="guideTeacherList" @selection-change="handleTeacherSelectionChange">
        <el-table-column type="selection" width="50" align="center" v-if="!readOnly" />
        <el-table-column label="工号" prop="teacherCode">
          <template #default="scope">
            <el-input v-model="scope.row.teacherCode" placeholder="请输入教师工号" :disabled="readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.sortOrder" :min="1" controls-position="right" :disabled="readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="是否第一指导" prop="isLeader" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.isLeader" placeholder="请选择" :disabled="readOnly">
              <el-option label="是" :value="1" />
              <el-option label="否" :value="0" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-form>

    <!-- 统一 footer：CRUD 按钮一致 -->
    <template #footer>
      <div class="dialog-footer">
        <slot name="footer-left"></slot>

        <el-button
            v-if="showSubmit && !readOnly"
            type="primary"
            @click="submitForm"
        >
          {{ submitTextComputed }}
        </el-button>

        <el-button @click="cancel">{{ cancelText }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="BaseOutcomeDialog">
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import { getCurrentInstance, ref, reactive, toRefs, computed } from "vue";

const { proxy } = getCurrentInstance();

// 事件
const emit = defineEmits(["ok"]);

// props：全部业务差异从 B 传入
const props = defineProps({
  getFn: { type: Function, required: true },
  addFn: { type: Function, required: true },
  updateFn: { type: Function, required: true },

  titleAdd: { type: String, default: "新增成果" },
  titleEdit: { type: String, default: "修改成果" },

  readOnly: { type: Boolean, default: false },       // 已审核页面可只读
  showSubmit: { type: Boolean, default: true },      // 已审核页面可隐藏提交按钮
  submitText: { type: String, default: "" },         // 可自定义：保存/提交
  cancelText: { type: String, default: "取 消" },

  fixedAuditStatus: { type: [String, Number], default: null } // 强制 auditStatus（可选）
});

// 字典
const { erp_group_type, erp_award_level_type, erp_award_rank } =
    proxy.useDict("erp_group_type", "erp_award_level_type", "erp_award_rank");

// 状态
const visible = ref(false);
const title = ref("");
const deptOptions = ref([]);

const erpContestantList = ref([]);
const guideTeacherList = ref([]);

const checkedErpContestant = ref([]);
const checkedTeacher = ref([]);

// 表单
const data = reactive({
  form: {},
  rules: {
    year: [{ required: true, message: "所属年份不能为空", trigger: "blur" }],
    deptId: [{ required: true, message: "所属学院不能为空", trigger: "change" }],
    category: [{ required: true, message: "成果类别不能为空", trigger: "blur" }],

    // workName 不必填：不写 required

    levelType: [{ required: true, message: "级别类型不能为空", trigger: "change" }],
    awardLevel: [{ required: true, message: "奖项等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupType: [{ required: true, message: "组别不能为空", trigger: "change" }],
    certNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "change" }],

    // submitTime 不必填：不写 required

    entryFee: [
      { required: true, message: "报名费不能为空", trigger: "blur" },
      { pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额(最多2位小数)", trigger: "blur" }
    ]
  }

});
const { form, rules } = toRefs(data);

// 统一标题
const dialogTitle = computed(() => title.value);

// 统一提交按钮文字
const submitTextComputed = computed(() => {
  if (props.submitText) return props.submitText;
  return form.value?.id ? "保 存" : "确 定";
});

// 打开弹窗（父组件/业务组件调用）
function open(id) {
  visible.value = true;
  reset();
  getDeptTree();

  if (id) {
    title.value = props.titleEdit;
    props.getFn(id).then((response) => {
      form.value = response.data || {};
      erpContestantList.value = response.data?.erpContestantList || [];
      guideTeacherList.value = response.data?.guideTeacherList || [];

      // 附件回显
      if (form.value.attachmentList) {
        form.value.attachmentList.forEach((item) => {
          if (item.attachType === "award") form.value.fileAward = item.filePath;
          if (item.attachType === "notice") form.value.fileNotice = item.filePath;
          if (item.attachType === "work") form.value.fileWork = item.filePath;
          if (item.attachType === "payment") form.value.filePayment = item.filePath;
          if (item.attachType === "invoice") form.value.fileInvoice = item.filePath;
          if (item.attachType === "receipt_code") form.value.fileReceiptCode = item.filePath;
        });
      }
      if (form.value.isReimburse == null) form.value.isReimburse = 0;

      // 若业务要求强制 auditStatus（例如未审核页永远写 1）
      if (props.fixedAuditStatus != null) form.value.auditStatus = props.fixedAuditStatus;
    });
  } else {
    title.value = props.titleAdd;
    if (props.fixedAuditStatus != null) form.value.auditStatus = props.fixedAuditStatus;
  }
}

function reset() {
  form.value = {
    id: null,
    year: null,
    category: null,
    workName: null,
    levelType: null,
    awardLevel: null,
    track: null,
    certNo: null,
    groupType: null,
    deptId: null,
    awardTime: null,
    submitTime: null,
    entryFee: null,
    auditStatus: "1",
    isReimburse: 0,

    fileAward: null,
    fileNotice: null,
    fileWork: null,
    filePayment: null,
    fileInvoice: null,
    fileReceiptCode: null,

    attachmentList: [],
    remark: null
  };
  erpContestantList.value = [];
  guideTeacherList.value = [];
  proxy.resetForm("outcomeRef");
}

function submitForm() {
  proxy.$refs["outcomeRef"].validate((valid) => {
    if (!valid) return;

    // 只读模式禁止提交（双保险）
    if (props.readOnly) return;

    // 报销校验：你原来逻辑是 “申请报销至少上传一份凭证”，这里保持一致
    if (form.value.isReimburse === 1) {
      if (!form.value.fileAward && !form.value.fileInvoice) {
        proxy.$modal.msgWarning("申请报销时，请至少上传一份凭证文件！");
        return;
      }
    }

    // 打包附件
    const attachments = [];
    const pushFile = (type, path) => {
      if (path) attachments.push({ attachType: type, filePath: path });
    };
    pushFile("award", form.value.fileAward);
    pushFile("notice", form.value.fileNotice);
    pushFile("work", form.value.fileWork);
    pushFile("payment", form.value.filePayment);
    pushFile("invoice", form.value.fileInvoice);
    pushFile("receipt_code", form.value.fileReceiptCode);

    form.value.attachmentList = attachments;
    form.value.erpContestantList = erpContestantList.value;
    form.value.guideTeacherList = guideTeacherList.value;

    // 强制 auditStatus（如果传了）
    if (props.fixedAuditStatus != null) form.value.auditStatus = props.fixedAuditStatus;

// 1) 先选用哪一个函数
    const isEdit = form.value.id != null;
    const fn = isEdit ? props.updateFn : props.addFn;

// 2) 防呆：没传/传错直接提示，不要 TypeError
    if (typeof fn !== "function") {
      proxy.$modal.msgError(isEdit ? "updateFn 未传入或不是函数，请检查页面传参/接口导入" : "addFn 未传入或不是函数，请检查页面传参/接口导入");
      return;
    }

// 3) 调用
    fn(form.value)
        .then(() => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
          visible.value = false;
          emit("ok");
        })
        .catch(() => {
          // 这里不强制提示，保持你项目现有的全局错误处理也行
        });

  });
}

function cancel() {
  visible.value = false;
  reset();
}

// 部门树
function getDeptTree() {
  listDept().then((response) => {
    deptOptions.value = handleTree(response.data, "deptId");
  });
}

// 子表：学生
function rowErpContestantIndex({ row, rowIndex }) {
  row.index = rowIndex + 1;
}
function handleAddErpContestant() {
  erpContestantList.value.push({
    studentCode: "",
    sortOrder: erpContestantList.value.length + 1,
    isLeader: 0
  });
}
function handleDeleteErpContestant() {
  if (checkedErpContestant.value.length === 0) return proxy.$modal.msgError("请先选择要删除的学生数据");
  const checked = checkedErpContestant.value;
  erpContestantList.value = erpContestantList.value.filter((item) => checked.indexOf(item.index) === -1);
}
function handleErpContestantSelectionChange(selection) {
  checkedErpContestant.value = selection.map((item) => item.index);
}

// 子表：老师
function handleAddTeacher() {
  guideTeacherList.value.push({
    teacherCode: "",
    sortOrder: guideTeacherList.value.length + 1,
    isLeader: 0
  });
}
function handleDeleteTeacher() {
  if (checkedTeacher.value.length === 0) return proxy.$modal.msgError("请先选择要删除的老师数据");
  guideTeacherList.value = guideTeacherList.value.filter((item) => !checkedTeacher.value.includes(item));
}
function handleTeacherSelectionChange(selection) {
  checkedTeacher.value = selection;
}

defineExpose({ open });
</script>

<style scoped>
.voucher-box {
  background: #f8f8f9;
  padding: 15px;
  border-radius: 4px;
  border: 1px dashed #d9d9d9;
  margin-top: 10px;
}
</style>
