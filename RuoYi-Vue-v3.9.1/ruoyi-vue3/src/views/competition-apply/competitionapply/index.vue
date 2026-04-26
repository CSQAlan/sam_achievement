<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      label-width="90px"
      v-show="showSearch"
    >
      <el-form-item label="赛事名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事类别" prop="category">
        <el-select
          v-model="queryParams.category"
          placeholder="请选择赛事类别"
          clearable
        >
          <el-option
            v-for="dict in sys_competition_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事级别" prop="level">
        <el-select
          v-model="queryParams.level"
          placeholder="请选择赛事级别"
          clearable
        >
          <el-option
            v-for="dict in sys_competition_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="适用范围" prop="scopeType">
        <el-select
          v-model="queryParams.scopeType"
          placeholder="请选择适用范围"
          clearable
        >
          <el-option
            v-for="dict in sys_competition_scope_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择审核状态"
          clearable
        >
          <el-option
            v-for="dict in sys_shenhe_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['competition-apply:competitionapply:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single || !canEditSelection"
          @click="handleUpdate()"
          v-hasPermi="['competition-apply:competitionapply:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple || !canDeleteSelection"
          @click="handleDelete()"
          v-hasPermi="['competition-apply:competitionapply:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['competition-apply:competitionapply:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="competitionapplyList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请人学号" align="center" prop="userName" />
      <el-table-column label="申请人学院" align="center" prop="deptName" />
      <el-table-column label="赛事名称" align="center" prop="name" />
      <el-table-column label="年份" align="center" prop="year" width="100" />
      <el-table-column label="届次" align="center" prop="session" width="120" />
      <el-table-column label="赛事类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag
            :options="sys_competition_category"
            :value="scope.row.category"
          />
        </template>
      </el-table-column>
      <el-table-column label="盖章单位" align="center" prop="organizations" />
      <el-table-column label="赛事级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="sys_competition_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="适用范围" align="center" prop="scopeType">
        <template #default="scope">
          <dict-tag
            :options="sys_competition_scope_type"
            :value="scope.row.scopeType"
          />
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_shenhe_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="标签" width="150" align="center" prop="tags">
        <template #default="scope">
          <dict-tag
            :options="sys_competition_tag"
            :value="scope.row.tags ? String(scope.row.tags).split(',') : []"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="赛事说明"
        align="center"
        prop="memo"
        show-overflow-tooltip
      />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            v-if="String(scope.row.status) !== '1'"
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['competition-apply:competitionapply:edit']"
          >
            修改
          </el-button>
          <el-button
            v-if="String(scope.row.status) !== '1'"
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['competition-apply:competitionapply:remove']"
          >
            删除
          </el-button>
          <span v-else class="text-muted">已通过</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="1200px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="11">
          <el-form
            ref="competitionapplyRef"
            :model="form"
            :rules="rules"
            label-width="100px"
          >
            <el-form-item label="赛事名称" prop="name">
              <el-select
                v-model="form.name"
                filterable
                allow-create
                default-first-option
                placeholder="请选择已有赛事或直接输入新的赛事名称"
                style="width: 100%"
                :class="{ 'new-comp-select': isNewCompetition }"
                @blur="handleCompetitionBlur"
              >
                <el-option
                  v-for="item in competitionOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
              <div v-if="isNewCompetition" class="new-competition-hint">
                <el-icon><WarningFilled /></el-icon> 
                <span>
                  检测到新赛事。
                  <template v-if="suggestedCompetition">
                    您指的是不是“<el-link type="warning" @click="form.name = suggestedCompetition">{{ suggestedCompetition }}</el-link>”？
                  </template>
                  <template v-else>请确保名称准确。</template>
                </span>
              </div>
            </el-form-item>
            <el-form-item label="年份" prop="year">
              <el-input-number
                v-model="form.year"
                :min="2000"
                :max="2100"
                controls-position="right"
                placeholder="请输入年份"
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item label="届次" prop="session">
              <el-input v-model="form.session" placeholder="例如：2025、十二届" />
              <div class="input-tip">请按照奖状/证书上的届次填写（如：第十五届）</div>
            </el-form-item>
           
            
            <el-alert v-if="preRecordedNote" type="success" show-icon :closable="false" class="mb20">
              <template #title>
                {{ preRecordedNote }}
              </template>
            </el-alert>

            <el-alert v-if="duplicateNote" type="error" show-icon :closable="false" class="mb20">
              <template #title>
                {{ duplicateNote }}
              </template>
            </el-alert>


          </el-form>
        </el-col>

        <el-col :span="12">
          <div class="attach-card">
            <el-divider content-position="left">
              {{ hasRouteUuid ? "上传附件" : "附件管理" }}
            </el-divider>

            <div v-if="hasRouteUuid" class="upload-pane-content">
              <el-alert
                type="info"
                :closable="false"
                class="mb10"
                title="检测到 URL 中存在 uuid，右侧显示 PDF 预览。"
              />
              <div v-if="routePreviewUrl" class="preview-box">
                <iframe
                  :src="routePreviewUrl"
                  width="100%"
                  height="500px"
                  frameborder="0"
                />
              </div>
              <div v-else class="empty-preview-box">参考成果预览加载中...</div>
              <div class="custom-file-row">
                <div class="file-name">
                  <el-icon class="mr5"><Document /></el-icon>
                  <span>{{ routeUuid }}</span>
                </div>
                <div class="file-action">
                  <el-button
                    link
                    type="primary"
                    :icon="View"
                    @click="handleOpenDetail(routeUuid)"
                  >
                    详情
                  </el-button>
                  <el-button
                    link
                    type="primary"
                    :icon="Download"
                    @click="handleDownloadAttachment(routeUuid, '')"
                  >
                    下载
                  </el-button>
                </div>
              </div>
            </div>

            <el-tabs
              v-else
              tab-position="left"
              v-model="activeAttachmentTab"
              style="height: 100%; min-height: 600px"
            >
              <el-tab-pane
                v-for="item in visibleAttachments"
                :key="item.name"
                :name="item.name"
              >
                <template #label>
                  <span class="tab-label">
                    {{ item.label }}
                    <el-icon v-if="form[item.prop]" class="success-dot"><CircleCheckFilled /></el-icon>
                    <span v-else class="pending-dot"></span>
                  </span>
                </template>
                <div class="upload-pane-content">
                  <el-alert
                    v-if="!form[item.prop]"
                    type="info"
                    :closable="false"
                    class="mb10"
                  >
                    <template #title>{{ item.alert }}</template>
                  </el-alert>

                  <el-form-item
                    v-if="!form[item.prop]"
                    label-width="0"
                    :prop="item.prop"
                  >
                    <upload-file
                      :key="`upload-${item.name}`"
                      v-model="form[item.prop]"
                      :limit="1"
                      :fileSize="50"
                      :fileType="['pdf']"
                      class="hide-file-list"
                      @update:modelValue="
                        (val) => handleAttachmentModelChange(val, item)
                      "
                    />
                  </el-form-item>

                  <div v-if="previewUrls[item.name]" class="preview-box">
                    <iframe
                      :src="previewUrls[item.name]"
                      width="100%"
                      height="500px"
                      frameborder="0"
                    />
                  </div>

                  <div v-if="form[item.prop]" class="custom-file-row">
                    <div class="file-name">
                      <el-icon class="mr5"><Document /></el-icon>
                      <span>{{
                        attachmentNames[item.name] ||
                        getFileName(form[item.prop])
                      }}</span>
                    </div>
                    <div class="file-action">
                      <el-button
                        link
                        type="primary"
                        :icon="View"
                        @click="handleOpenDetail(form[item.prop])"
                      >
                        详情
                      </el-button>
                      <el-button
                        link
                        type="primary"
                        :icon="Download"
                        @click="
                          handleDownloadAttachment(
                            form[item.prop],
                            attachmentNames[item.name]
                          )
                        "
                      >
                        下载
                      </el-button>
                      <el-button
                        link
                        type="danger"
                        :icon="Delete"
                        @click="handleDeleteAttachment(item)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Competitionapply">
import {
  computed,
  getCurrentInstance,
  onBeforeUnmount,
  reactive,
  ref,
  watch,
  toRefs
} from "vue";
import { useRoute } from "vue-router";
import { 
  Delete, 
  Document, 
  Download, 
  View, 
  QuestionFilled, 
  WarningFilled, 
  CircleCheckFilled 
} from "@element-plus/icons-vue";
import request, { download } from "@/utils/request";
import UploadFile from "@/components/FileUpload";
import { listSession } from "@/api/session/session";
import {
  addCompetitionapply,
  delCompetitionapply,
  getCompetitionapply,
  listCompetitionapply,
  updateCompetitionapply,
} from "@/api/competition-apply/competitionapply";
import useUserStore from "@/store/modules/user";

const { proxy } = getCurrentInstance();
const route = useRoute();
const userStore = useUserStore();

const {
  sys_competition_tag,
  sys_shenhe_status,
  sys_competition_scope_type,
  sys_competition_category,
  sys_competition_level,
  attach_type,
} = proxy.useDict(
  "sys_competition_tag",
  "sys_shenhe_status",
  "sys_competition_scope_type",
  "sys_competition_category",
  "sys_competition_level",
  "attach_type"
);

const showSearch = ref(true);
const loading = ref(false);
const competitionapplyList = ref([]);
const ids = ref([]);
const selectedRows = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const canEditSelection = ref(false);
const canDeleteSelection = ref(false);
const activeAttachmentTab = ref("certificate");
const routePreviewUrl = ref("");
const competitionOptions = ref([]);

const preRecordedNote = ref("");
const duplicateNote = ref("");

const isNewCompetition = computed(() => {
  if (!form.value.name) return false;
  return !competitionOptions.value.some(opt => opt.name === form.value.name);
});

const suggestedCompetition = computed(() => {
  if (!isNewCompetition.value || !form.value.name) return null;
  const query = form.value.name.toLowerCase();
  const match = competitionOptions.value.find(opt => opt.name.toLowerCase().includes(query));
  return match ? match.name : null;
});

function fetchCompetitionOptions() {
  request({
    url: '/competition/competition/optionList',
    method: 'get'
  }).then(res => {
    competitionOptions.value = res.data || [];
  });
}

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: null,
  category: null,
  level: null,
  scopeType: null,
  status: null,
  memo: null,
});

const rules = {
  name: [{ required: true, message: "赛事名称不能为空", trigger: "blur" }],
  year: [{ required: true, message: "年份不能为空", trigger: "change" }],
  session: [{ required: true, message: "届次不能为空", trigger: "blur" }],
};

const previewUrls = reactive({
  certificate: "",
  notice: "",
});

const attachmentNames = reactive({
  certificate: "",
  notice: "",
});

const attachmentFieldMap = {
  certificate: "certificateFile",
  notice: "noticeFile",
};

const defaultAttachmentConfig = [
// ...
  {
    name: "certificate",
    dictValue: "1",
    fallbackLabel: "奖状",
    alert: "请上传奖状 PDF 文件",
  },
  {
    name: "notice",
    dictValue: "2",
    fallbackLabel: "通知文件",
    alert: "请上传通知文件 PDF 文件",
  },
];

const visibleAttachments = computed(() =>
  defaultAttachmentConfig.map((item) => {
    const dictItem = (attach_type.value || []).find(
      (dict) => String(dict.value) === item.dictValue
    );
    return {
      ...item,
      prop: attachmentFieldMap[item.name],
      label: dictItem?.label || item.fallbackLabel,
    };
  })
);

const routeUuid = computed(() => normalizeUuid(route.query.uuid));
const hasRouteUuid = computed(() => Boolean(routeUuid.value));

const form = ref(createDefaultForm());

function createDefaultForm() {
  return {
    id: null,
    applicantUserId: userStore.id,
    applicantDepId: userStore.deptId || "",
    name: null,
    year: new Date().getFullYear(),
    session: null,
    category: null,
    organizations: null,
    level: null,
    scopeType: "0",
    status: "0",
    tags: [],
    memo: null,
    certificateFile: null,
    noticeFile: null,
    competitionApplyAttachmentList: [],
    attachmentUrls: "",
    auditBy: null,
    auditTime: null,
    auditRemark: null,
    competitionId: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    delFlag: null,
  };
}

function normalizeUuid(value) {
  if (Array.isArray(value)) {
    return normalizeUuid(value[0]);
  }
  if (value === null || value === undefined) {
    return "";
  }
  const normalized = String(value).trim();
  if (!normalized) {
    return "";
  }
  return normalized.includes(",")
    ? normalized.split(",")[0].trim()
    : normalized;
}

function getFileName(value) {
  const normalized = normalizeUuid(value);
  if (!normalized) {
    return "";
  }
  return normalized.split("/").pop();
}

function revokePreview(key) {
  if (previewUrls[key]) {
    window.URL.revokeObjectURL(previewUrls[key]);
    previewUrls[key] = "";
  }
}

function revokeRoutePreview() {
  if (routePreviewUrl.value) {
    window.URL.revokeObjectURL(routePreviewUrl.value);
    routePreviewUrl.value = "";
  }
}

function resetAttachmentState() {
  Object.keys(previewUrls).forEach((key) => revokePreview(key));
  Object.keys(attachmentNames).forEach((key) => {
    attachmentNames[key] = "";
  });
}

async function fetchPdfObjectUrl(uuid) {
  const blob = await request({
    url: "/attachment/download",
    method: "get",
    params: { resource: uuid },
    responseType: "blob",
  });
  const blobData = blob?.data || blob;
  return window.URL.createObjectURL(
    new Blob([blobData], { type: "application/pdf" })
  );
}

async function loadAttachmentPreview(uuid, attachmentName) {
  revokePreview(attachmentName);
  const normalizedUuid = normalizeUuid(uuid);
  if (!normalizedUuid) {
    return;
  }
  try {
    previewUrls[attachmentName] = await fetchPdfObjectUrl(normalizedUuid);
  } catch (error) {
    console.error("附件预览加载失败", error);
    previewUrls[attachmentName] = "";
  }
}

async function loadRoutePdfPreview(uuid) {
  revokeRoutePreview();
  const normalizedUuid = normalizeUuid(uuid);
  if (!normalizedUuid) {
    return;
  }
  try {
    routePreviewUrl.value = await fetchPdfObjectUrl(normalizedUuid);
  } catch (error) {
    console.error("参考成果预览加载失败", error);
    routePreviewUrl.value = "";
  }
}

function handleAttachmentModelChange(value, item) {
  const uuid = normalizeUuid(value);
  form.value[item.prop] = uuid || null;
  attachmentNames[item.name] = getFileName(uuid);
  loadAttachmentPreview(uuid, item.name);
}

async function handleOpenDetail(uuid) {
  const normalizedUuid = normalizeUuid(uuid);
  if (!normalizedUuid) {
    proxy.$modal.msgError("文件标识无效");
    return;
  }
  proxy.$modal.loading("正在准备文件...");
  const previewWindow = window.open("about:blank", "_blank");
  try {
    const pdfUrl = await fetchPdfObjectUrl(normalizedUuid);
    proxy.$modal.closeLoading();
    if (previewWindow) {
      previewWindow.location.href = pdfUrl;
    } else {
      window.URL.revokeObjectURL(pdfUrl);
      proxy.$modal.msgWarning("浏览器拦截了弹窗，请允许弹窗后重试");
    }
  } catch (error) {
    proxy.$modal.closeLoading();
    if (previewWindow) {
      previewWindow.close();
    }
    proxy.$modal.msgError("文件获取失败，请稍后重试");
  }
}

async function handleDownloadAttachment(uuid, fileName) {
  const normalizedUuid = normalizeUuid(uuid);
  if (!normalizedUuid) {
    proxy.$modal.msgError("文件标识无效");
    return;
  }
  proxy.$modal.loading("正在下载文件，请稍候...");
  try {
    const blob = await request({
      url: "/attachment/download",
      method: "get",
      params: { resource: normalizedUuid },
      responseType: "blob",
    });
    proxy.$modal.closeLoading();
    const blobData = blob?.data || blob;
    const blobUrl = window.URL.createObjectURL(
      new Blob([blobData], { type: "application/pdf" })
    );
    const a = document.createElement("a");
    a.style.display = "none";
    a.href = blobUrl;
    a.download = fileName || `${normalizedUuid}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(blobUrl);
  } catch (error) {
    proxy.$modal.closeLoading();
    console.error("下载附件失败", error);
    proxy.$modal.msgError("下载失败，请稍后重试");
  }
}

function handleDeleteAttachment(item) {
  proxy.$modal
    .confirm("是否确认删除该附件？")
    .then(() => {
      form.value[item.prop] = null;
      attachmentNames[item.name] = "";
      revokePreview(item.name);
      proxy.$modal.msgSuccess("附件已删除");
    })
    .catch(() => {});
}

function resolveAttachmentItem(type) {
  const normalizedType = String(type ?? "");
  return visibleAttachments.value.find(
    (item) => item.dictValue === normalizedType || item.name === normalizedType
  );
}

function fillAttachmentData(list = []) {
  resetAttachmentState();
  list.forEach((attachment) => {
    const item = resolveAttachmentItem(
      attachment.attachmentType || attachment.type
    );
    if (!item) {
      return;
    }
    const uuid = normalizeUuid(
      // 后端子表只存 uuid；这里兼容其它字段名
      attachment.uuid ||
        attachment.fileUuid ||
        attachment.file_uuid ||
        attachment.path ||
        attachment.url
    );
    if (!uuid) {
      return;
    }
    form.value[item.prop] = uuid;
    attachmentNames[item.name] = attachment.documentName || getFileName(uuid);
    loadAttachmentPreview(uuid, item.name);
  });
}

function buildAttachmentList() {
  return visibleAttachments.value
    .map((item) => {
      const uuid = normalizeUuid(form.value[item.prop]);
      if (!uuid) {
        return null;
      }
      return {
        id: null,
        competitionApplyId: form.value.id,
        // 子表只存 uuid，真实路径由后端通过 uuid 反查
        uuid,
        documentName: attachmentNames[item.name] || getFileName(uuid),
        attachmentType: item.dictValue,
        delFlag: "0",
      };
    })
    .filter(Boolean);
}

function getList() {
  loading.value = true;
  listCompetitionapply(queryParams.value)
    .then((response) => {
      competitionapplyList.value = response.rows || [];
      total.value = response.total || 0;
    })
    .finally(() => {
      loading.value = false;
    });
}

function reset() {
  proxy.resetForm("competitionapplyRef");
  form.value = createDefaultForm();
  activeAttachmentTab.value = "certificate";
  resetAttachmentState();
}

function cancel() {
  open.value = false;
  reset();
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  selectedRows.value = selection || [];
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;

  // 已通过(1)的申请：不允许修改/删除
  canEditSelection.value =
    selection.length === 1 && String(selection[0]?.status) !== "1";
  canDeleteSelection.value =
    selection.length > 0 &&
    selection.every((row) => String(row?.status) !== "1");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加赛事申请";
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0];
  if (!id) {
    return;
  }
  const status = row?.status ?? selectedRows.value?.[0]?.status;
  if (String(status) === "1") {
    proxy.$modal.msgWarning("已审核通过，不能再修改");
    return;
  }
  reset();
  getCompetitionapply(id).then((response) => {
    const data = { ...createDefaultForm(), ...(response.data || {}) };
    data.tags = data.tags ? String(data.tags).split(",") : [];
    data.certificateFile = null;
    data.categoryFile = null;
    data.noticeFile = null;
    form.value = data;
    fillAttachmentData(response.data?.competitionApplyAttachmentList || []);
    open.value = true;
    title.value = "修改赛事申请";
  });
}

function submitForm() {
  proxy.$refs.competitionapplyRef.validate((valid) => {
    if (!valid) {
      return;
    }

    const submitAction = () => {
      const submitData = {
        ...form.value,
        tags: Array.isArray(form.value.tags)
          ? form.value.tags.join(",")
          : form.value.tags || "",
        competitionApplyAttachmentList: buildAttachmentList(),
      };
      const requestFn = submitData.id
        ? updateCompetitionapply
        : addCompetitionapply;
      requestFn(submitData).then(() => {
        proxy.$modal.msgSuccess(submitData.id ? "修改成功" : "新增成功");
        open.value = false;
        getList();
      });
    };

    // 如果检测到重复申请，提交前强弹窗提醒
    if (duplicateNote.value) {
      proxy.$modal.confirm(`系统检测到该赛事届次已有申请，确认继续提交吗？`, "重复申请提醒", {
        confirmButtonText: "确定提交",
        cancelButtonText: "取消并核实",
        type: "warning"
      }).then(() => {
        submitAction();
      }).catch(() => {});
    } else {
      submitAction();
    }
  });
}

function handleDelete(row) {
  const targetIds = row?.id || ids.value;
  if (!targetIds || (Array.isArray(targetIds) && targetIds.length === 0)) {
    return;
  }
  proxy.$modal
    .confirm("是否确认删除该赛事申请？删除后相关附件也会一并删除。")
    .then(() => delCompetitionapply(targetIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    });
}

function handleExport() {
  download(
    "competition-apply/competitionapply/export",
    queryParams.value,
    `competitionapply_${new Date().getTime()}.xlsx`
  );
}

watch(
  () => routeUuid.value,
  (uuid) => {
    loadRoutePdfPreview(uuid);
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  resetAttachmentState();
  revokeRoutePreview();
});

fetchCompetitionOptions();
getList();
// 监听名/年/届，检测预录状态和重复申请
watch([() => form.value.name, () => form.value.year, () => form.value.session], ([name, year, session]) => {
  if (name && year && String(session).trim()) {
    checkSessionStatus(name, year, session);
    checkDuplicateApplication(name, year, session);
  } else {
    preRecordedNote.value = "";
    duplicateNote.value = "";
  }
});

async function checkSessionStatus(name, year, session) {
  try {
    const res = await listSession({ competitionName: name, year, session: String(session).trim() });
    const list = res.rows || [];
    if (list.length > 0) {
      const s = list[0];
      if (String(s.status) === '2') {
        if (!s.uuid) {
          preRecordedNote.value = "该比赛尚未上传正式通知，请您在“通知文件”页签中补齐官方 PDF 通知，以便管理员审核并通过。";
          activeAttachmentTab.value = "notice";
        } else {
          preRecordedNote.value = "该比赛已有预设通知，如您有更准确的正式版本，欢迎在“通知文件”页签中更新。";
        }
        // 自动带入一些信息
        if (s.category && !form.value.category) form.value.category = s.category;
        if (s.level && !form.value.level) form.value.level = s.level;
        if (s.organizations && !form.value.organizations) form.value.organizations = s.organizations;
      } else {
        preRecordedNote.value = "";
      }
    } else {
      preRecordedNote.value = "";
    }
  } catch (e) {
    preRecordedNote.value = "";
  }
}

async function checkDuplicateApplication(name, year, session) {
  try {
    // 查询同名、同年、同届的申请
    const res = await listCompetitionapply({ 
      name, 
      year, 
      session: String(session).trim(),
      pageNum: 1,
      pageSize: 10
    });
    const list = res.rows || [];
    // 排除掉当前正在编辑的这一条
    const others = list.filter(item => item.id !== form.value.id);
    if (others.length > 0) {
      duplicateNote.value = `系统检测到已有用户申请了该赛事的此届次（${session}），请勿重复申请。如有疑问请联系管理员。`;
    } else {
      duplicateNote.value = "";
    }
  } catch (e) {
    console.error("检查重复申请失败", e);
    duplicateNote.value = "";
  }
}

/** 赛事名称失焦处理：保护用户输入的文字，防止失焦后值丢失，但不强制纠偏 */
function handleCompetitionBlur(e) {
  // 针对 Element Plus el-select allow-create 的增强处理
  // 如果失焦时 v-model 为空，但搜索框(input)里其实有文字，则手动同步
  if (!form.value.name && e.target.value) {
    form.value.name = e.target.value;
  }
}
</script>

<style scoped>
.attach-card {
  height: 100%;
  min-height: 600px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.upload-pane-content {
  padding: 16px 8px;
}

.preview-box,
.empty-preview-box {
  margin-bottom: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.empty-preview-box {
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #606266;
}

.custom-file-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
}

.new-competition-hint {
  margin-top: 5px;
  font-size: 12px;
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 4px;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-2px); }
  75% { transform: translateX(2px); }
}

.new-comp-select :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #e6a23c inset !important;
  background-color: #fdf6ec !important;
}

/* 附件 Tab 样式增强 */
.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.success-dot {
  color: #67c23a;
  font-size: 16px;
}

.pending-dot {
  width: 8px;
  height: 8px;
  background-color: #f56c6c;
  border-radius: 50%;
  box-shadow: 0 0 4px rgba(245, 108, 108, 0.5);
}

.input-tip {
  font-size: 11px;
  color: #909399;
  line-height: 1.5;
  margin-top: 4px;
}

.mb20 {
  margin-bottom: 20px;
}

.text-muted {
  color: var(--el-text-color-secondary);
}

.file-name {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.file-name span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #1f2937;
}

.file-action {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mb10 {
  margin-bottom: 10px;
}

.mr5 {
  margin-right: 5px;
}

:deep(.el-dialog__body) {
  max-height: 80vh;
  overflow-y: auto;
  padding: 20px;
}

:deep(.hide-file-list .upload-file-list),
:deep(.hide-file-list .el-upload-list) {
  display: none;
}

:deep(.el-tabs--left) {
  display: flex;
}

:deep(.el-tabs__nav-wrap) {
  border-right: 1px solid #e5e7eb;
}
</style>
