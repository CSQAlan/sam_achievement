<!-- BaseOutcomeDialog.vue -->
<template>
  <!-- ✅ 统一容器：pageMode=true => 页面；否则 => 弹窗 -->
  <component
      :is="props.pageMode ? PageContainer : DialogContainer"
      :title="dialogTitle"
      :showSubmit="props.showSubmit"
      :readOnly="props.readOnly"
      :submitText="submitTextComputed"
      :showCancel="props.showCancel"
      :cancelText="props.cancelText"

      :modelValue="visible"
      :width="'800px'"
      :fullscreen="dialogFullscreen"
      :modal="dialogModal"
      :appendToBody="dialogAppendToBody"
      :showClose="dialogShowClose"
      :closeOnClickModal="dialogCloseOnClickModal"

      @submit="submitForm"
      @cancel="cancel"
      @close="cancel"
      @update:modelValue="(v) => (visible = v)"
  >
    <!-- ✅ 让外部把“审核工具条”等塞进 header(页面模式) / footer(弹窗模式) -->
    <template #footer-left>
      <slot name="footer-left"></slot>
    </template>

    <!-- ✅ 表单核心内容只写一份，弹窗/页面共用 -->
    <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px" :disabled="props.readOnly">
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
              <el-option v-for="dict in erp_award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="奖项等级" prop="awardLevel">
            <el-select v-model="form.awardLevel" placeholder="请选择(如一等奖)">
              <el-option v-for="dict in erp_award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
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
              <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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

      <!-- 允许外部插扩展字段 -->
      <slot name="extra-form" :form="form"></slot>

      <el-divider content-position="left">报销申请</el-divider>

      <el-form-item prop="isReimburse" class="no-for-label-item">
        <div class="inline-form-item">
          <div class="inline-label">是否申请报销</div>
          <el-radio-group v-model="form.isReimburse">
            <el-radio :label="1">是 (需要上传凭证)</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </div>
      </el-form-item>

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
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="发票" prop="fileInvoice">
                <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="收款码" prop="fileReceiptCode">
                <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </div>

      <el-divider content-position="center">参赛选手信息</el-divider>
      <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
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
        <el-table-column type="selection" width="50" align="center" v-if="!props.readOnly" />
        <el-table-column label="序号" align="center" prop="index" width="50" />
        <el-table-column label="学生学号" prop="studentCode">
          <template #default="scope">
            <el-input v-model="scope.row.studentCode" placeholder="请输入学号" :disabled="props.readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.sortOrder" :min="1" controls-position="right" :disabled="props.readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="是否队长" prop="isLeader" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.isLeader" placeholder="请选择" :disabled="props.readOnly">
              <el-option label="是 (第一负责人)" :value="1" />
              <el-option label="否 (队员)" :value="0" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="center">指导老师信息</el-divider>
      <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
        <el-col :span="1.5">
          <el-button type="primary" icon="Plus" @click="handleAddTeacher">添加老师</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="Delete" @click="handleDeleteTeacher">删除选中</el-button>
        </el-col>
      </el-row>

      <el-table :data="guideTeacherList" @selection-change="handleTeacherSelectionChange">
        <el-table-column type="selection" width="50" align="center" v-if="!props.readOnly" />
        <el-table-column label="工号" prop="teacherCode">
          <template #default="scope">
            <el-input v-model="scope.row.teacherCode" placeholder="请输入教师工号" :disabled="props.readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.sortOrder" :min="1" controls-position="right" :disabled="props.readOnly" />
          </template>
        </el-table-column>
        <el-table-column label="是否第一指导" prop="isLeader" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.isLeader" placeholder="请选择" :disabled="props.readOnly">
              <el-option label="是" :value="1" />
              <el-option label="否" :value="0" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-form>

    <!-- ✅ 弹窗模式才渲染 footer -->
    <template v-if="!props.pageMode" #footer>
      <div class="dialog-footer">
        <slot name="footer-left"></slot>
        <el-button v-if="props.showSubmit && !props.readOnly" type="primary" @click="submitForm">
          {{ submitTextComputed }}
        </el-button>
        <el-button v-if="props.showCancel" @click="cancel">{{ props.cancelText }}</el-button>
      </div>
    </template>
  </component>
</template>

<script setup name="BaseOutcomeDialog">
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import { getCurrentInstance, ref, reactive, toRefs, computed, defineComponent, h, resolveComponent } from "vue";

const { proxy } = getCurrentInstance();
const emit = defineEmits(["ok", "cancel"]);

const props = defineProps({
  getFn: { type: Function, required: true },
  addFn: { type: Function, required: true },
  updateFn: { type: Function, required: true },

  titleAdd: { type: String, default: "新增成果" },
  titleEdit: { type: String, default: "修改成果" },

  readOnly: { type: Boolean, default: false },
  showSubmit: { type: Boolean, default: true },
  showCancel: { type: Boolean, default: true },
  submitText: { type: String, default: "" },
  cancelText: { type: String, default: "取 消" },

  fixedAuditStatus: { type: [String, Number], default: null },

  pageMode: { type: Boolean, default: false },

  fullscreen: { type: Boolean, default: false },
  modal: { type: Boolean, default: true },
  appendToBody: { type: Boolean, default: true },
  showClose: { type: Boolean, default: true },
  closeOnClickModal: { type: Boolean, default: true }
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

    levelType: [{ required: true, message: "级别类型不能为空", trigger: "change" }],
    awardLevel: [{ required: true, message: "奖项等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupType: [{ required: true, message: "组别不能为空", trigger: "change" }],
    certNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "change" }],
    entryFee: [
      { required: true, message: "报名费不能为空", trigger: "blur" },
      { pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额(最多2位小数)", trigger: "blur" }
    ]
  }
});
const { form, rules } = toRefs(data);

// 标题
const dialogTitle = computed(() => title.value);

// 提交按钮文字（弹窗模式用）
const submitTextComputed = computed(() => {
  if (props.submitText) return props.submitText;
  return form.value?.id ? "保 存" : "确 定";
});

// 弹窗属性
const dialogFullscreen = computed(() => props.fullscreen);
const dialogModal = computed(() => props.modal);
const dialogAppendToBody = computed(() => props.appendToBody);
const dialogShowClose = computed(() => props.showClose);
const dialogCloseOnClickModal = computed(() => props.closeOnClickModal);

/** 页面容器 */
const PageContainer = defineComponent({
  name: "OutcomePageContainer",
  props: {
    title: String,
    showSubmit: Boolean,
    readOnly: Boolean,
    submitText: String,
    showCancel: Boolean,
    cancelText: String
  },
  emits: ["submit", "cancel"],
  setup(p, { slots, emit }) {
    const ElButton = resolveComponent("el-button");
    return () =>
        h("div", { class: "outcome-page" }, [
          h("div", { class: "outcome-page__header" }, [
            h("div", { class: "outcome-page__title" }, p.title || ""),
            h("div", { class: "outcome-page__actions" }, [
              slots["footer-left"]?.(),
              !p.readOnly && p.showSubmit
                  ? h(ElButton, { type: "primary", onClick: () => emit("submit") }, () => p.submitText || "确 定")
                  : null,
              p.showCancel
                  ? h(ElButton, { onClick: () => emit("cancel") }, () => p.cancelText || "取 消")
                  : null
            ].filter(Boolean))
          ]),
          h("div", { class: "outcome-page__body" }, slots.default?.())
        ]);
  }
});

/** 弹窗容器 */
const DialogContainer = defineComponent({
  name: "OutcomeDialogContainer",
  props: {
    title: String,
    modelValue: Boolean,
    width: String,
    fullscreen: Boolean,
    modal: Boolean,
    appendToBody: Boolean,
    showClose: Boolean,
    closeOnClickModal: Boolean
  },
  emits: ["update:modelValue", "close"],
  setup(p, { slots, emit }) {
    const ElDialog = resolveComponent("el-dialog");
    return () =>
        h(
            ElDialog,
            {
              title: p.title,
              modelValue: p.modelValue,
              "onUpdate:modelValue": (v) => emit("update:modelValue", v),
              width: p.width || "800px",
              fullscreen: p.fullscreen,
              modal: p.modal,
              appendToBody: p.appendToBody,
              showClose: p.showClose,
              closeOnClickModal: p.closeOnClickModal,
              onClose: () => emit("close")
            },
            {
              default: () => slots.default?.(),
              footer: () => slots.footer?.()
            }
        );
  }
});

// 打开
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
    if (props.readOnly) return;

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

    if (props.fixedAuditStatus != null) form.value.auditStatus = props.fixedAuditStatus;

    const isEdit = form.value.id != null;
    const fn = isEdit ? props.updateFn : props.addFn;

    if (typeof fn !== "function") {
      proxy.$modal.msgError(isEdit ? "updateFn 未传入或不是函数，请检查页面传参/接口导入" : "addFn 未传入或不是函数，请检查页面传参/接口导入");
      return;
    }

    fn(form.value)
        .then(() => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
          if (!props.pageMode) {
            visible.value = false;
            reset();
          }
          emit("ok");
        })
        .catch(() => {});
  });
}

function cancel() {
  if (props.pageMode) {
    emit("cancel");
    return;
  }
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

// ✅ 审核页需要拿到当前表单数据
function getForm() {
  return form.value;
}

defineExpose({ open, getForm });
</script>

<style scoped>
.voucher-box {
  background: #f8f8f9;
  padding: 15px;
  border-radius: 4px;
  border: 1px dashed #d9d9d9;
  margin-top: 10px;
}

/* pageMode 页面样式 */
.outcome-page {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
}

.outcome-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.outcome-page__title {
  font-size: 18px;
  font-weight: 600;
}

.outcome-page__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.outcome-page__body {
  min-height: 200px;
}
</style>
