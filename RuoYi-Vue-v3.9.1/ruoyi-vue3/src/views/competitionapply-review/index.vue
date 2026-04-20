<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      label-width="90px"
      v-show="showSearch"
    >
      <el-form-item label="申请人学号" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入申请人学号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人学院" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入申请人学院"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
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
      <el-form-item label="审核范围" prop="reviewedFlag">
        <el-select
          v-model="queryParams.reviewedFlag"
          placeholder="请选择审核范围"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="未审核" value="0" />
          <el-option label="已审核" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择审核状态"
          clearable
          :disabled="String(queryParams.reviewedFlag) === '0'"
        >
          <el-option
            v-for="dict in statusOptions"
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
            link
            type="primary"
            icon="Edit"
            @click="handleReview(scope.row)"
            v-hasPermi="['competition-apply:competitionapply:edit']"
          >
            审核
          </el-button>
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
              <el-input v-model="form.name" placeholder="请输入赛事名称" />
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
            </el-form-item>
            <el-form-item label="赛事类别" prop="category">
              <el-radio-group v-model="form.category">
                <el-radio
                  v-for="dict in sys_competition_category"
                  :key="dict.value"
                  :label="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="盖章单位" prop="organizations">
              <el-input
                v-model="form.organizations"
                placeholder="请输入盖章单位"
              />
            </el-form-item>
            <el-form-item label="赛事级别" prop="level">
              <el-radio-group v-model="form.level">
                <el-radio
                  v-for="dict in sys_competition_level"
                  :key="dict.value"
                  :label="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="标签" prop="tags">
              <el-checkbox-group v-model="form.tags">
                <el-checkbox
                  v-for="dict in sys_competition_tag"
                  :key="dict.value"
                  :label="dict.value"
                >
                  {{ dict.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="赛事说明" prop="memo">
              <el-input
                v-model="form.memo"
                type="textarea"
                :rows="6"
                placeholder="请输入赛事说明"
              />
            </el-form-item>
          </el-form>

          <el-form :model="form" label-width="100px" style="margin-top: 12px">
            <el-form-item label="审核意见" prop="auditRemark">
              <el-input
                v-model="form.auditRemark"
                type="textarea"
                :rows="4"
                placeholder="驳回必填；通过可选"
              />
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="12">
          <div class="attach-card">
            <el-divider content-position="left">
              {{ hasRouteUuid ? "参考成果预览" : "附件管理" }}
            </el-divider>

            <div v-if="hasRouteUuid" class="upload-pane-content">
              <el-alert
                type="info"
                :closable="false"
                class="mb10"
                title="检测到 URL 中存在 uuid，右侧显示参考成果 PDF 预览。"
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
                    @click="
                      handleDownloadAttachment(routeUuid, '参考成果附件.pdf')
                    "
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
                :label="item.label"
              >
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
                      :disabled="true"
                      class="hide-file-list"
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
                        disabled
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
          <el-button type="success" @click="handleApprove">通过</el-button>
          <el-button type="danger" @click="handleReject">驳回</el-button>
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
} from "vue";
import { useRoute } from "vue-router";
import { Delete, Document, Download, View } from "@element-plus/icons-vue";
import request, { download } from "@/utils/request";
import UploadFile from "@/components/FileUpload";
import {
  getCompetitionapply,
  listCompetitionapply,
  reviewCompetitionapply,
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
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const activeAttachmentTab = ref("certificate");
const routePreviewUrl = ref("");

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  userName: null,
  deptName: null,
  name: null,
  category: null,
  level: null,
  scopeType: null,
  // 审核页面默认只看“未审核（待审）”
  reviewedFlag: "0",
  status: "0",
  memo: null,
});

const statusOptions = computed(() => {
  const options = sys_shenhe_status.value || [];
  const reviewedFlag = String(queryParams.value.reviewedFlag ?? "");
  if (reviewedFlag === "1") {
    return options.filter((item) => ["1", "2"].includes(String(item.value)));
  }
  if (reviewedFlag === "0") {
    return options.filter((item) => String(item.value) === "0");
  }
  return options;
});

const rules = {
  name: [{ required: true, message: "赛事名称不能为空", trigger: "blur" }],
  category: [
    { required: true, message: "赛事类别不能为空", trigger: "change" },
  ],
  organizations: [
    { required: true, message: "盖章单位不能为空", trigger: "blur" },
  ],
  level: [{ required: true, message: "赛事级别不能为空", trigger: "change" }],
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

watch(
  () => queryParams.value.reviewedFlag,
  (flag) => {
    const reviewedFlag = String(flag ?? "");
    // 未审核：强制待审；已审核：默认不过滤具体结果；全部：不过滤
    if (reviewedFlag === "0") {
      queryParams.value.status = "0";
      return;
    }
    queryParams.value.status = null;
  },
  { immediate: true }
);

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleReview(row) {
  const id = row?.id || ids.value[0];
  if (!id) {
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
    title.value = "赛事申请审核";
  });
}

function handleApprove() {
  if (!form.value?.id) {
    return;
  }
  proxy.$refs["competitionapplyRef"].validate((valid) => {
    if (valid) {
      proxy.$modal
        .confirm("确认审核通过？通过后将自动生成赛事。")
        .then(() => {
          const payload = { ...form.value };
          payload.status = "1";
          payload.auditRemark = form.value.auditRemark || "";
          payload.tags = Array.isArray(form.value.tags) ? form.value.tags.join(",") : form.value.tags;
          return reviewCompetitionapply(form.value.id, payload);
        })
        .then(() => {
          proxy.$modal.msgSuccess("审核通过");
          open.value = false;
          getList();
        });
    }
  });
}

function handleReject() {
  if (!form.value?.id) {
    return;
  }
  if (!form.value.auditRemark || !String(form.value.auditRemark).trim()) {
    proxy.$modal.msgError("请输入驳回原因");
    return;
  }
  proxy.$modal
    .confirm("确认驳回该申请？")
    .then(() =>
      reviewCompetitionapply(form.value.id, {
        status: "2",
        auditRemark: String(form.value.auditRemark).trim(),
      })
    )
    .then(() => {
      proxy.$modal.msgSuccess("已驳回");
      open.value = false;
      getList();
    });
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

getList();
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
