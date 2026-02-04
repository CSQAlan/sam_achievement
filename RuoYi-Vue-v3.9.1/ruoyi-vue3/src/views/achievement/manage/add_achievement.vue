<template>
  <el-dialog :title="title" v-model="visible" width="1200px" append-to-body @close="cancel">
    <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-row>
            <el-col :span="12">
              <el-form-item label="类别" prop="category">
                <el-select v-model="form.category" placeholder="请选择类别" filterable>
                  <el-option v-for="dict in achievement_category" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="届次" prop="sessionId">
                <el-input v-model="form.sessionId" placeholder="例：15" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="所属学院" prop="ownerDepId">
                <el-tree-select v-model="form.ownerDepId" :data="deptOptions"
                  :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId"
                  placeholder="请选择所属学院" check-strictly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="作品名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入作品名称(选填)" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="获奖级别" prop="level">
                <el-select v-model="form.level" placeholder="请选择(如国家级)">
                  <el-option v-for="dict in award_level_type" :key="dict.value" :label="dict.label"
                    :value="dict.value"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="奖项等级" prop="grade">
                <el-select v-model="form.grade" placeholder="请选择(如一等奖)">
                  <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label"
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
              <el-form-item label="组别" prop="groupId">
                <el-select v-model="form.groupId" placeholder="请选择组别">
                  <el-option v-for="dict in group_type" :key="dict.value" :label="dict.label"
                    :value="dict.value"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="证书编号" prop="certificateNo">
                <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
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
              <el-form-item label="团队名称" prop="teamName">
                <el-input v-model="form.teamName" placeholder="请输入团队名称(选填)" />
              </el-form-item>
            </el-col>
             <el-col :span="12" v-if="form.isReimburse === 1">
              <el-form-item label="报名费" prop="fee">
                <el-input v-model="form.fee" placeholder="请输入金额(元)">
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
              <el-button type="primary" icon="Plus" @click="handleAddParticipant">添加学生</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" icon="Delete" @click="handleDeleteParticipant">删除选中</el-button>
            </el-col>
          </el-row>
          <el-table :data="samAchievementParticipantList" :row-class-name="rowParticipantIndex"
            @selection-change="handleParticipantSelectionChange" ref="participantTable">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="学生学号" prop="studentId">
              <template #default="scope">
                <el-input v-model="scope.row.studentId" placeholder="请输入学号"
                  @blur="handleStudentBlur(scope.row)" />
              </template>
            </el-table-column>
            <el-table-column label="姓名" align="center" prop="studentName" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.studentName" placeholder="姓名(自动回显)" disabled />
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="orderNo" width="100">
              <template #default="scope">
                <el-input v-model="scope.row.orderNo" disabled style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="是否负责人" prop="manager" width="150">
              <template #default="scope">
                <el-select v-model="scope.row.manager" placeholder="系统自动判定" disabled>
                  <el-option label="是" :value="1" />
                  <el-option label="否" :value="0" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>

          <el-divider content-position="center">指导老师信息</el-divider>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" icon="Plus" @click="handleAddAdvisor">添加老师</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button>
            </el-col>
          </el-row>
          <el-table :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="教师工号" prop="teacherId">
              <template #default="scope">
                <el-input v-model="scope.row.teacherId" placeholder="请输入工号"
                  @blur="handleTeacherBlur(scope.row)" />
              </template>
            </el-table-column>
            <el-table-column label="姓名" align="center" prop="teacherName" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.teacherName" placeholder="姓名(自动回显)" disabled/>
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="orderNo" width="100">
              <template #default="scope">
                <el-input v-model="scope.row.orderNo" disabled style="width: 100%"/>
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
                       <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf', 'jpg', 'png']" class="hide-file-list" />
                       
                       <div v-if="previewUrls.award" class="preview-box">
                         <iframe :src="previewUrls.award" width="100%" height="450px" frameborder="0"></iframe>
                       </div>
                       <div v-if="form.fileAward" class="custom-file-row">
                          <div class="file-name">
                            <el-icon class="mr5"><Document /></el-icon>
                            <span>{{ getFileName(form.fileAward) }}</span>
                          </div>
                          <div class="file-action">
                            <el-button link type="primary" icon="Download" @click="handleDownload(form.fileAward)">下载</el-button>
                            <el-button link type="danger" icon="Delete" @click="form.fileAward = null" style="margin-left: 10px;">删除</el-button>
                          </div>
                       </div>
                    </el-form-item>
                 </div>
              </el-tab-pane>

              <el-tab-pane label="比赛通知" name="notice">
                <div class="upload-pane-content">
                   <el-alert title="请上传官方发布的比赛通知文件" type="info" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" />
                      
                      <div v-if="previewUrls.notice" class="preview-box">
                         <iframe :src="previewUrls.notice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>

                      <div v-if="form.fileNotice" class="custom-file-row">
                          <div class="file-name">
                            <el-icon class="mr5"><Document /></el-icon>
                            <span>{{ getFileName(form.fileNotice) }}</span>
                          </div>
                          <div class="file-action">
                             <el-button link type="primary" icon="Download" @click="handleDownload(form.fileNotice)">下载</el-button>
                             <el-button link type="danger" icon="Delete" @click="form.fileNotice = null" style="margin-left: 10px;">删除</el-button>
                          </div>
                       </div>
                   </el-form-item>
                </div>
              </el-tab-pane>

              <el-tab-pane label="参赛照片" name="work">
                <div class="upload-pane-content">
                   <el-alert title="请上传参赛照片或作品文档" type="info" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="20" :fileType="['pdf', 'jpg']" class="hide-file-list" />
                      
                      <div v-if="previewUrls.work" class="preview-box">
                         <iframe :src="previewUrls.work" width="100%" height="450px" frameborder="0"></iframe>
                      </div>

                      <div v-if="form.fileWork" class="custom-file-row">
                          <div class="file-name">
                            <el-icon class="mr5"><Document /></el-icon>
                            <span>{{ getFileName(form.fileWork) }}</span>
                          </div>
                          <div class="file-action">
                             <el-button link type="primary" icon="Download" @click="handleDownload(form.fileWork)">下载</el-button>
                             <el-button link type="danger" icon="Delete" @click="form.fileWork = null" style="margin-left: 10px;">删除</el-button>
                          </div>
                       </div>
                   </el-form-item>
                </div>
              </el-tab-pane>

              <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                 <div class="upload-pane-content">
                   <el-alert title="请上传报名费转账截图/回单" type="warning" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf', 'jpg', 'png']" class="hide-file-list" />
                      <div v-if="previewUrls.payment" class="preview-box">
                         <iframe :src="previewUrls.payment" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.filePayment" class="custom-file-row">
                          <div class="file-name">
                            <el-icon class="mr5"><Document /></el-icon>
                            <span>{{ getFileName(form.filePayment) }}</span>
                          </div>
                          <div class="file-action">
                             <el-button link type="primary" icon="Download" @click="handleDownload(form.filePayment)">下载</el-button>
                             <el-button link type="danger" icon="Delete" @click="form.filePayment = null" style="margin-left: 10px;">删除</el-button>
                          </div>
                       </div>
                   </el-form-item>
                 </div>
              </el-tab-pane>

              <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                 <div class="upload-pane-content">
                   <el-alert title="请上传正规电子发票" type="warning" :closable="false" class="mb10"/>
                   <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" />
                      <div v-if="previewUrls.invoice" class="preview-box">
                         <iframe :src="previewUrls.invoice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileInvoice" class="custom-file-row">
                          <div class="file-name">
                            <el-icon class="mr5"><Document /></el-icon>
                            <span>{{ getFileName(form.fileInvoice) }}</span>
                          </div>
                          <div class="file-action">
                             <el-button link type="primary" icon="Download" @click="handleDownload(form.fileInvoice)">下载</el-button>
                             <el-button link type="danger" icon="Delete" @click="form.fileInvoice = null" style="margin-left: 10px;">删除</el-button>
                          </div>
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
// 引入新生成的API，替换旧的API
import { getManage, addManage, updateManage } from "@/api/achievement/manage"
// 引入你刚刚生成的 student 和 teacher 接口
import { listStudent } from "@/api/achievement/student";
import { listTeacher } from "@/api/achievement/teacher";
import { listDept } from "@/api/system/dept"; // 部门接口 (若依系统自带)
import { handleTree } from "@/utils/ruoyi";
import request from '@/utils/request' // 用于安全预览
import { getCurrentInstance, ref, reactive, toRefs, watch } from 'vue';

const { proxy } = getCurrentInstance()
const emit = defineEmits(['ok'])

// 1. 字典 (使用 index.vue 同款字典)
const { 
  achievement_category, 
  group_type, 
  award_rank, 
  award_level_type 
} = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type')

// 2. 状态变量
const visible = ref(false)
const title = ref("")
const deptOptions = ref([]) // 部门树数据
const activeAttachmentTab = ref('award') 

// 列表数据 (新字段名: samAchievementParticipantList)
const samAchievementParticipantList = ref([])
const samAchievementAdvisorList = ref([]) // 假设你也需要指导老师表
// 选中数据
const checkedParticipant = ref([])
const checkedAdvisor = ref([])

// 3. 表单模型
const data = reactive({
  form: {},
  rules: {
    category: [{ required: true, message: "类别不能为空", trigger: "change" }],
    sessionId: [{ required: true, message: "届次不能为空", trigger: "blur" }],
    ownerDepId: [{ required: true, message: "所属学院不能为空", trigger: "change" }],
    level: [{ required: true, message: "级别不能为空", trigger: "change" }],
    grade: [{ required: true, message: "等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupId: [{ required: true, message: "组别不能为空", trigger: "change" }],
    certificateNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "blur" }],
    fee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }]
  }
})
const { form, rules } = toRefs(data)

// --- 安全预览核心逻辑 (保留原版) Start ---
const previewUrls = reactive({
  award: "",
  notice: "",
  work: "",
  payment: "",
  invoice: ""
})

/**
 * 安全加载预览：使用 blob 方式加载，请求头会自动带上 Token
 */
function loadSafePreview(filePath, type) {
  if (!filePath) {
    if (previewUrls[type]) {
      window.URL.revokeObjectURL(previewUrls[type]); 
    }
    previewUrls[type] = "";
    return;
  }
  // 若依通用下载接口
  request({
    url: '/common/download/resource',
    method: 'get',
    params: { resource: filePath },
    responseType: 'blob'
  }).then(blob => {
    if (previewUrls[type]) {
      window.URL.revokeObjectURL(previewUrls[type]);
    }
    const blobUrl = window.URL.createObjectURL(blob);
    previewUrls[type] = blobUrl;
  }).catch(err => {
    console.error("预览加载失败", err);
    previewUrls[type] = ""; 
  })
}

// 监听文件路径变化，自动触发安全预览
watch(() => form.value.fileAward, (val) => loadSafePreview(val, 'award'));
watch(() => form.value.fileNotice, (val) => loadSafePreview(val, 'notice'));
watch(() => form.value.fileWork, (val) => loadSafePreview(val, 'work'));
watch(() => form.value.filePayment, (val) => loadSafePreview(val, 'payment'));
watch(() => form.value.fileInvoice, (val) => loadSafePreview(val, 'invoice'));
// --- 安全预览核心逻辑 End ---


// 4. 打开弹窗
function open(id) {
  visible.value = true;
  reset();
  getDeptTree();
  activeAttachmentTab.value = 'award'; 

  if (id) {
    title.value = "修改成果";
    getManage(id).then(response => {
      form.value = response.data;
      samAchievementParticipantList.value = response.data.samAchievementParticipantList || [];
      samAchievementAdvisorList.value = response.data.samAchievementAdvisorList || [];
      
      reIndexList(samAchievementParticipantList.value);
      reIndexList(samAchievementAdvisorList.value);

      // 附件回显逻辑 (根据你后端怎么存的来写，这里保留你原版的逻辑思路)
      // 假设后端返回了 attachmentList
      if (form.value.samAchievementAttachmentList) {
         // 这里你需要遍历 attachmentList 并赋值给 form.value.fileAward 等临时变量
         // 示例：
         // form.value.samAchievementAttachmentList.forEach(item => {
         //    if (item.type == 1) form.value.fileAward = item.fileUuid; // 需根据实际字段调整
         // })
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
    ownerDepId: null,
    awardTime: null,
    fee: null,
    isReimburse: 0,
    // 临时文件字段
    fileAward: null,
    fileNotice: null,
    fileWork: null,
    filePayment: null,
    fileInvoice: null
  }
  samAchievementParticipantList.value = []
  samAchievementAdvisorList.value = []
  
  // 清理所有 blob 预览内存
  Object.keys(previewUrls).forEach(key => {
    if (previewUrls[key]) {
      window.URL.revokeObjectURL(previewUrls[key]);
      previewUrls[key] = "";
    }
  });

  proxy.resetForm("outcomeRef")
}

// ---------------- 核心逻辑 ----------------

// 重新排序
function reIndexList(list) {
  list.forEach((item, index) => {
    item.orderNo = index + 1; // 适配新字段 orderNo
    item.manager = (index === 0) ? 1 : 0; // 适配新字段 manager
  });
}

// 关联查询：输入学号查姓名
function handleStudentBlur(row) {
  if (!row.studentId) return;
  // 调用新API listStudent，参数 no (学号)
  listStudent({ no: row.studentId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.studentName = response.rows[0].name; // 回显姓名
    } else {
      proxy.$modal.msgWarning(`未找到学号为 ${row.studentId} 的信息，请手动输入姓名`);
      row.studentName = "";
    }
  });
}

// 关联查询：输入工号查姓名
function handleTeacherBlur(row) {
  if (!row.teacherId) return;
  // 调用新API listTeacher，参数 no (工号)
  listTeacher({ no: row.teacherId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.teacherName = response.rows[0].teacherName; // 回显姓名
    } else {
      proxy.$modal.msgWarning(`未找到工号为 ${row.teacherId} 的信息，请手动输入姓名`);
      row.teacherName = "";
    }
  });
}

function submitForm() {
  proxy.$refs["outcomeRef"].validate(valid => {
    if (valid) {
      // 校验报销凭证
      if (form.value.isReimburse === 1) {
        if (!form.value.fileAward && !form.value.fileInvoice) {
          proxy.$modal.msgWarning("申请报销时，请至少上传一份凭证文件！");
          return;
        }
      }

      // 组装附件 (你需要跟后端确认 SamAchievementAttachment 的字段)
      // 这里生成一个 list 传给后端
      let attachments = [];
      const pushFile = (type, path) => {
        // type: 1=奖状, 2=通知, 3=作品, 4=支付凭证, 5=发票
        // 注意：这里的 fileUuid 或 path 字段名要和你后端实体类一致
        if (path) attachments.push({ type: type, fileUuid: path }); 
      };
      pushFile(1, form.value.fileAward);
      pushFile(2, form.value.fileNotice);
      pushFile(3, form.value.fileWork);
      pushFile(4, form.value.filePayment);
      pushFile(5, form.value.fileInvoice);

      form.value.samAchievementAttachmentList = attachments;
      form.value.samAchievementParticipantList = samAchievementParticipantList.value;
      form.value.samAchievementAdvisorList = samAchievementAdvisorList.value;

      if (form.value.achievementId != null) {
        updateManage(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          visible.value = false
          emit('ok')
        })
      } else {
        addManage(form.value).then(response => {
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

// 获取部门树
function getDeptTree() {
  listDept().then(response => {
    deptOptions.value = handleTree(response.data, "deptId");
  });
}

// --- 表格操作逻辑 ---
function rowParticipantIndex({ row, rowIndex }) { row.index = rowIndex + 1 }

function handleAddParticipant() {
  // 适配新字段结构
  let obj = { studentId: "", studentName: "", orderNo: 0, manager: 0 }
  samAchievementParticipantList.value.push(obj)
  reIndexList(samAchievementParticipantList.value)
}
function handleDeleteParticipant() {
  if (checkedParticipant.value.length == 0) return proxy.$modal.msgError("请先选择要删除的学生数据");
  // 简单过滤删除
  const checked = checkedParticipant.value
  samAchievementParticipantList.value = samAchievementParticipantList.value.filter(item => !checked.includes(item))
  reIndexList(samAchievementParticipantList.value)
}
function handleParticipantSelectionChange(selection) { checkedParticipant.value = selection } // 存整个对象方便删除

function handleAddAdvisor() {
  let obj = { teacherId: "", teacherName: "", orderNo: 0 };
  samAchievementAdvisorList.value.push(obj);
  reIndexList(samAchievementAdvisorList.value);
}
function handleDeleteAdvisor() {
  if (checkedAdvisor.value.length == 0) return proxy.$modal.msgError("请先选择要删除的老师数据");
  samAchievementAdvisorList.value = samAchievementAdvisorList.value.filter(item => !checkedAdvisor.value.includes(item));
  reIndexList(samAchievementAdvisorList.value);
}
function handleAdvisorSelectionChange(selection) { checkedAdvisor.value = selection; }

// --- 工具函数 ---

function getFileName(url) {
  if (!url) return "";
  const name = url.substring(url.lastIndexOf("/") + 1);
  return name;
}

function handleDownload(url) {
  if (!url) return;
  proxy.$download.resource(url);
}

defineExpose({ open })
</script>

<style scoped>
/* 保持你原版的样式，完全没动 */
.upload-pane-content {
  padding: 20px;
}
.mb10 {
  margin-bottom: 15px;
}
.mr5 {
  margin-right: 5px;
}
:deep(.el-tabs__content) {
  height: 100%;
}
.hide-file-list :deep(.el-upload-list) {
  display: none !important;
}
.custom-file-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #f5f7fa;
  padding: 8px 10px;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}
.custom-file-row:hover {
  background-color: #ecf5ff;
}
.preview-box {
  margin-top: 15px; 
  border: 1px solid #ddd;
  padding: 5px;
  background-color: #fff;
}
.file-name {
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 280px;
}
.file-action {
  margin-left: 10px;
  flex-shrink: 0;
}
</style>