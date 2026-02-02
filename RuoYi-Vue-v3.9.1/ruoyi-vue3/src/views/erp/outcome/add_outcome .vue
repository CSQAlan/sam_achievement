<template>
  <el-dialog :title="title" v-model="visible" width="1200px" append-to-body @close="cancel">
    <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-row>
            <el-col :span="12">
              <el-form-item label="所属年份" prop="year">
                <el-input v-model="form.year" placeholder="例：2026" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属学院" prop="deptId">
                <el-tree-select v-model="form.deptId" :data="deptOptions"
                  :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId"
                  placeholder="请选择所属学院" check-strictly />
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
                  <el-option v-for="dict in erp_award_level_type" :key="dict.value" :label="dict.label"
                    :value="dict.value"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="奖项等级" prop="awardLevel">
                <el-select v-model="form.awardLevel" placeholder="请选择(如一等奖)">
                  <el-option v-for="dict in erp_award_rank" :key="dict.value" :label="dict.label"
                    :value="dict.value"></el-option>
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
                  <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label"
                    :value="dict.value"></el-option>
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
                <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD"
                  placeholder="选择日期" style="width: 100%"></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="提交时间" prop="submitTime">
                <el-date-picker clearable v-model="form.submitTime" type="date" value-format="YYYY-MM-DD"
                  placeholder="选择日期" style="width: 100%"></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.isReimburse === 1">
              <el-form-item label="报名费" prop="entryFee">
                <el-input v-model="form.entryFee" placeholder="请输入金额(元)">
                  <template #append>元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>

          <el-form-item label="是否申请报销" prop="isReimburse">
            <el-radio-group v-model="form.isReimburse">
              <el-radio :label="1">是 (需要上传凭证)</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-divider content-position="center">参赛选手信息</el-divider>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" icon="Plus" @click="handleAddErpContestant">添加学生</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" icon="Delete" @click="handleDeleteErpContestant">删除选中</el-button>
            </el-col>
          </el-row>
          <el-table :data="erpContestantList" :row-class-name="rowErpContestantIndex"
            @selection-change="handleErpContestantSelectionChange" ref="erpContestant">
            <el-table-column type="selection" width="50" align="center" />
            
            <el-table-column label="学生学号" prop="studentCode">
              <template #default="scope">
                <el-input v-model="scope.row.studentCode" placeholder="请输入学号"
                  @blur="handleStudentCodeBlur(scope.row)" />
              </template>
            </el-table-column>
            
            <el-table-column label="姓名" align="center" prop="studentName" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.studentName" placeholder="姓名" />
              </template>
            </el-table-column>

            <el-table-column label="排序" prop="sortOrder" width="100">
              <template #default="scope">
                <el-input v-model="scope.row.sortOrder" disabled style="width: 100%" />
              </template>
            </el-table-column>
            
            <el-table-column label="是否队长" prop="isLeader" width="150">
              <template #default="scope">
                <el-select v-model="scope.row.isLeader" placeholder="系统自动判定" disabled>
                  <el-option label="是 (队长)" :value="1" />
                  <el-option label="否 (队员)" :value="0" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>

          <el-divider content-position="center">指导老师信息</el-divider>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" icon="Plus" @click="handleAddTeacher">添加老师</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" icon="Delete" @click="handleDeleteTeacher">删除选中</el-button>
            </el-col>
          </el-row>
          <el-table :data="guideTeacherList" @selection-change="handleTeacherSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            
            <el-table-column label="教师工号" prop="teacherCode">
              <template #default="scope">
                <el-input v-model="scope.row.teacherCode" placeholder="请输入工号"
                  @blur="handleTeacherCodeBlur(scope.row)" />
              </template>
            </el-table-column>
            
            <el-table-column label="姓名" align="center" prop="teacherName" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.teacherName" placeholder="姓名" />
              </template>
            </el-table-column>

            <el-table-column label="排序" prop="sortOrder" width="100">
              <template #default="scope">
                <el-input v-model="scope.row.sortOrder" disabled style="width: 100%"/>
              </template>
            </el-table-column>
            
            <el-table-column label="是否第一指导" prop="isLeader" width="150">
              <template #default="scope">
                <el-select v-model="scope.row.isLeader" placeholder="系统自动判定" disabled>
                  <el-option label="是" :value="1" />
                  <el-option label="否" :value="0" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-col>

        <el-col :span="12">
          <div style="background: #f8f8f9; padding: 10px; border-radius: 4px; border: 1px solid #d9d9d9; height: 100%;">
            <el-divider content-position="left">附件管理</el-divider>
            
            <el-tabs tab-position="left" style="height: 100%; min-height: 500px;" v-model="activeAttachmentTab">
              <el-tab-pane label="奖状(证书)" name="award">
                 <div class="upload-pane-content">
                    <el-alert title="请上传获奖证书或奖状扫描件" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileAward">
                       <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" />
                       <div v-if="form.fileAward" class="preview-wrapper">
                          <el-popover placement="left" :width="600" trigger="hover">
                            <template #reference>
                              <el-button type="primary" link icon="View">预览文件内容</el-button>
                            </template>
                            <iframe :src="getPdfUrl(form.fileAward)" width="100%" height="500px" frameborder="0"></iframe>
                          </el-popover>
                       </div>
                    </el-form-item>
                 </div>
              </el-tab-pane>

              <el-tab-pane label="比赛通知" name="notice">
                <div class="upload-pane-content">
                   <el-alert title="请上传官方发布的比赛通知文件" type="info" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileNotice" class="preview-wrapper">
                         <el-popover placement="left" :width="600" trigger="hover">
                           <template #reference>
                             <el-button type="primary" link icon="View">预览文件内容</el-button>
                           </template>
                           <iframe :src="getPdfUrl(form.fileNotice)" width="100%" height="500px" frameborder="0"></iframe>
                         </el-popover>
                      </div>
                   </el-form-item>
                </div>
              </el-tab-pane>

              <el-tab-pane label="参赛照片" name="work">
                <div class="upload-pane-content">
                   <el-alert title="请上传参赛照片或作品文档" type="info" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="20" :fileType="['pdf']" />
                      <div v-if="form.fileWork" class="preview-wrapper">
                         <el-popover placement="left" :width="600" trigger="hover">
                           <template #reference>
                             <el-button type="primary" link icon="View">预览文件内容</el-button>
                           </template>
                           <iframe :src="getPdfUrl(form.fileWork)" width="100%" height="500px" frameborder="0"></iframe>
                         </el-popover>
                      </div>
                   </el-form-item>
                </div>
              </el-tab-pane>

              <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                 <div class="upload-pane-content">
                   <el-alert title="请上传报名费转账截图/回单" type="warning" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.filePayment" class="preview-wrapper">
                         <el-popover placement="left" :width="600" trigger="hover">
                           <template #reference>
                             <el-button type="primary" link icon="View">预览文件内容</el-button>
                           </template>
                           <iframe :src="getPdfUrl(form.filePayment)" width="100%" height="500px" frameborder="0"></iframe>
                         </el-popover>
                      </div>
                   </el-form-item>
                 </div>
              </el-tab-pane>

              <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                 <div class="upload-pane-content">
                   <el-alert title="请上传正规电子发票" type="warning" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileInvoice" class="preview-wrapper">
                         <el-popover placement="left" :width="600" trigger="hover">
                           <template #reference>
                             <el-button type="primary" link icon="View">预览文件内容</el-button>
                           </template>
                           <iframe :src="getPdfUrl(form.fileInvoice)" width="100%" height="500px" frameborder="0"></iframe>
                         </el-popover>
                      </div>
                   </el-form-item>
                 </div>
              </el-tab-pane>

              <el-tab-pane label="收款码" name="receipt" v-if="form.isReimburse === 1">
                 <div class="upload-pane-content">
                   <el-alert title="请上传用于接收报销款的收款码" type="warning" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileReceiptCode">
                      <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileReceiptCode" class="preview-wrapper">
                         <el-popover placement="left" :width="600" trigger="hover">
                           <template #reference>
                             <el-button type="primary" link icon="View">预览文件内容</el-button>
                           </template>
                           <iframe :src="getPdfUrl(form.fileReceiptCode)" width="100%" height="500px" frameborder="0"></iframe>
                         </el-popover>
                      </div>
                   </el-form-item>
                 </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="AddOutcome">
// 引入必要的 API
import { getOutcome, addOutcome, updateOutcome } from "@/api/erp/outcome"
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import { listStudent } from "@/api/erp/student";
import { listTeacher } from "@/api/erp/teacher";
import { getCurrentInstance, ref, reactive, toRefs } from 'vue';

const { proxy } = getCurrentInstance()
const emit = defineEmits(['ok'])

// 1. 字典
const { erp_group_type, erp_dept_id, erp_audit_status, erp_award_level_type, erp_award_rank } = proxy.useDict('erp_group_type', 'erp_dept_id', 'erp_audit_status', 'erp_award_level_type', 'erp_award_rank')

// 2. 状态变量
const visible = ref(false)
const title = ref("")
const deptOptions = ref([])
const activeAttachmentTab = ref('award') 

// 列表数据
const erpContestantList = ref([])
const guideTeacherList = ref([])
// 选中数据
const checkedErpContestant = ref([])
const checkedTeacher = ref([])

// 3. 表单模型与规则
const data = reactive({
  form: {},
  rules: {
    year: [{ required: true, message: "所属年份不能为空", trigger: "blur" }],
    category: [{ required: true, message: "成果类别不能为空", trigger: "blur" }],
    workName: [{ required: true, message: "作品名称不能为空", trigger: "blur" }],
    deptId: [{ required: true, message: "所属学院不能为空", trigger: "change" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "blur" }],
    entryFee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }]
  }
})
const { form, rules } = toRefs(data)

// 4. 打开弹窗
function open(id) {
  visible.value = true;
  reset();
  getDeptTree();
  activeAttachmentTab.value = 'award'; 

  if (id) {
    title.value = "修改成果";
    getOutcome(id).then(response => {
      form.value = response.data;
      erpContestantList.value = response.data.erpContestantList || [];
      guideTeacherList.value = response.data.guideTeacherList || [];
      
      // 打开时也重新计算一次排序规则，确保旧数据符合当前逻辑
      reIndexList(erpContestantList.value);
      reIndexList(guideTeacherList.value);

      // 附件回显
      if (form.value.attachmentList) {
        form.value.attachmentList.forEach(item => {
          if (item.attachType === 'award') form.value.fileAward = item.filePath;
          if (item.attachType === 'notice') form.value.fileNotice = item.filePath;
          if (item.attachType === 'work') form.value.fileWork = item.filePath;
          if (item.attachType === 'payment') form.value.filePayment = item.filePath;
          if (item.attachType === 'invoice') form.value.fileInvoice = item.filePath;
          if (item.attachType === 'receipt_code') form.value.fileReceiptCode = item.filePath;
        });
      }
      if (form.value.isReimburse == null) form.value.isReimburse = 0;
    });
  } else {
    title.value = "新增成果";
  }
}

// 5. 重置表单
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
    auditStatus: '1',
    isReimburse: 0,
    fileAward: null,
    fileNotice: null,
    fileWork: null,
    filePayment: null,
    fileInvoice: null,
    fileReceiptCode: null,
    attachmentList: [],
    remark: null
  }
  erpContestantList.value = []
  guideTeacherList.value = [];
  proxy.resetForm("outcomeRef")
}

// ---------------- 核心逻辑修改：自动重排与锁定 ----------------

/**
 * 核心方法：重置列表排序和身份
 * 规则：index=0 (排序1) -> isLeader=1 (是)，其他为0 (否)
 * @param {Array} list 学生或老师列表
 */
function reIndexList(list) {
  list.forEach((item, index) => {
    item.sortOrder = index + 1; // 排序从1开始
    // 强制绑定：排序1就是队长，排序>1就是队员
    item.isLeader = (index === 0) ? 1 : 0;
  });
}

function handleStudentCodeBlur(row) {
  if (!row.studentCode) return;
  listStudent({ studentCode: row.studentCode }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.studentName = response.rows[0].name;
    } else {
      proxy.$modal.msgWarning(`未找到学号为 ${row.studentCode} 的信息，请手动输入姓名`);
    }
  });
}

function handleTeacherCodeBlur(row) {
  if (!row.teacherCode) return;
  listTeacher({ teacherCode: row.teacherCode }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.teacherName = response.rows[0].name;
    } else {
      proxy.$modal.msgWarning(`未找到工号为 ${row.teacherCode} 的信息，请手动输入姓名`);
    }
  });
}

// ---------------- 提交与通用逻辑 ----------------

function submitForm() {
  proxy.$refs["outcomeRef"].validate(valid => {
    if (valid) {
      if (form.value.isReimburse === 1) {
        if (!form.value.fileAward && !form.value.fileInvoice) {
          proxy.$modal.msgWarning("申请报销时，请至少上传一份凭证文件！");
          return;
        }
      }

      let attachments = [];
      const pushFile = (type, path) => {
        if (path) attachments.push({ attachType: type, filePath: path });
      };
      pushFile('award', form.value.fileAward);
      pushFile('notice', form.value.fileNotice);
      pushFile('work', form.value.fileWork);
      pushFile('payment', form.value.filePayment);
      pushFile('invoice', form.value.fileInvoice);
      pushFile('receipt_code', form.value.fileReceiptCode);

      form.value.attachmentList = attachments;
      form.value.erpContestantList = erpContestantList.value
      form.value.guideTeacherList = guideTeacherList.value;

      if (form.value.id != null) {
        updateOutcome(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          visible.value = false
          emit('ok')
        })
      } else {
        addOutcome(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          visible.value = false
          emit('ok')
        })
      }
    }
  })
}

function cancel() {
  visible.value = false
  reset()
}

function getDeptTree() {
  listDept().then(response => {
    deptOptions.value = handleTree(response.data, "deptId");
  });
}

// --- 表格操作逻辑 (修改：调用 reIndexList) ---
function rowErpContestantIndex({ row, rowIndex }) { row.index = rowIndex + 1 }

// 添加学生：添加后自动重排
function handleAddErpContestant() {
  let obj = { studentCode: "", studentName: "", sortOrder: 0, isLeader: 0 }
  erpContestantList.value.push(obj)
  reIndexList(erpContestantList.value) // 触发重排
}

// 删除学生：删除后自动重排（此时原来的第2名会自动变成第1名+队长）
function handleDeleteErpContestant() {
  if (checkedErpContestant.value.length == 0) return proxy.$modal.msgError("请先选择要删除的学生数据");
  const checked = checkedErpContestant.value
  erpContestantList.value = erpContestantList.value.filter(item => checked.indexOf(item.index) == -1)
  reIndexList(erpContestantList.value) // 触发重排
}
function handleErpContestantSelectionChange(selection) { checkedErpContestant.value = selection.map(item => item.index) }

// 添加老师：自动重排
function handleAddTeacher() {
  let obj = { teacherCode: "", teacherName: "", sortOrder: 0, isLeader: 0 };
  guideTeacherList.value.push(obj);
  reIndexList(guideTeacherList.value); // 触发重排
}

// 删除老师：自动重排
function handleDeleteTeacher() {
  if (checkedTeacher.value.length == 0) return proxy.$modal.msgError("请先选择要删除的老师数据");
  guideTeacherList.value = guideTeacherList.value.filter(item => !checkedTeacher.value.includes(item));
  reIndexList(guideTeacherList.value); // 触发重排
}
function handleTeacherSelectionChange(selection) { checkedTeacher.value = selection; }

function getPdfUrl(url) {
  if (!url) return "";
  let fullUrl = url;
  if (url.indexOf("http") === -1 && url.indexOf("https") === -1) {
    fullUrl = import.meta.env.VITE_APP_BASE_API + url;
  }
  return fullUrl + "#toolbar=0&navpanes=0&view=FitH";
}

defineExpose({ open })
</script>

<style scoped>
.upload-pane-content {
  padding: 20px;
}
.mb10 {
  margin-bottom: 15px;
}
.preview-wrapper {
  margin-top: 10px;
}
:deep(.el-tabs__content) {
  height: 100%;
}
</style>