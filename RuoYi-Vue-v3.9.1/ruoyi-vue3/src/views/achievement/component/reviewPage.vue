<template>
  <div class="app-container review-page">
    <AchievementForm
        :key="formKey"
        ref="dlgRef"
        :getFn="currentApi.getFn"
        :addFn="currentApi.addFn"
        :updateFn="currentApi.updateFn"
        :pageMode="true"
        :readOnly="isView"
        :showSubmit="isEdit"
        cancelText="返回"
        :titleAdd="reviewPageTitle"
        :titleEdit="reviewPageTitle"
        @cancel="handleBack"
        @ok="handleFormSaved"
    />

    <!-- ✅ 固定底部工具条：详情/审核页面都显示导航，审核页额外显示审核操作 -->
    <div v-if="showBottomDock" class="review-fixed-dock">
      <div class="review-fixed-inner">
        <div class="audit-toolbar" :class="{ 'audit-toolbar--nav-only': !showAuditControls }">
          <div class="audit-toolbar-main">
            <div class="nav-toolbar">

              <el-button class="nav-btn back-btn" plain @click="handleBack">返回列表</el-button>
              <el-button class="nav-btn prev-btn" plain @click="handlePrev">上一个</el-button>
              <el-button class="nav-btn next-btn" plain @click="handleNext">下一个</el-button>
            </div>

            <div v-if="showAuditControls" class="audit-field status-field">
              <span class="audit-label">审核后状态</span>
              <el-select
                  v-model="selectedAuditStatus"
                  placeholder="请选择"
                  class="audit-status-select"
              >
                <el-option
                    v-for="opt in nextStatusOptions"
                    :key="String(opt.value)"
                    :label="opt.label"
                    :value="String(opt.value)"
                />
              </el-select>
            </div>
            <div v-if="showAuditControls" class="toolbar-actions">
              <el-button class="save-btn" :loading="saveFormLoading" @click="saveFormChanges">
                保存修改
              </el-button>
              <el-button class="submit-btn" type="primary" :disabled="!selectedAuditStatus" @click="submitAudit">
                提交审核
              </el-button>
            </div>
            <div v-if="showAuditControls && showRejectReason" class="audit-field reason-field">
              <span class="audit-label">驳回原因</span>
              <div class="audit-reason-group">
                <el-select
                    v-model="rejectReason"
                    multiple
                    filterable
                    clearable
                    class="audit-reason-select"
                    :placeholder="rejectReasonPlaceholder"
                >
                  <el-option
                      v-for="opt in currentRejectReasonOptions"
                      :key="opt.value"
                      :label="opt.label"
                      :value="opt.value"
                  />
                </el-select>
                <el-input
                    v-model="rejectReasonCustom"
                    clearable
                    class="audit-reason-input"
                    :placeholder="rejectReasonCustomPlaceholder"
                />
              </div>
            </div>
          </div>

          <el-button class="submit-btn" type="primary" :disabled="!selectedAuditStatus" @click="submitAudit">
            提交审核
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ReviewPage">
import { computed, onMounted, onActivated, onBeforeUnmount, ref, getCurrentInstance, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";

import AchievementForm from "@/views/achievement/component/AchievementForm.vue";

// ====== 四套 API（你已有）======
import { getCollege_level_unreviewed, addCollege_level_unreviewed, updateCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import { getCollege_level_reviewed, addCollege_level_reviewed, updateCollege_level_reviewed } from "@/api/achievement/college_level_reviewed";
import { getSchool_level_unreviewed, addSchool_level_unreviewed, updateSchool_level_unreviewed } from "@/api/achievement/school_level_unreviewed";
import { getSchool_level_reviewed, addSchool_level_reviewed, updateSchool_level_reviewed } from "@/api/achievement/school_level_reviewed";
import { submitReview as submitReviewAction } from "@/api/achievement/review_batch";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const AUTO_SCHOOL_REJECT_REASON = "因院级状态变更自动驳回";
const LEGACY_AUTO_SCHOOL_REJECT_REASON = "院级状态变更，校级自动驳回";
const REJECT_REASON_SEPARATOR = "；";
const REVIEW_NAV_READY_EVENT = "achievement-review-navigation-ready";
const REVIEW_RESULT_APPLIED_EVENT = "achievement-review-result-applied";

const dlgRef = ref(null);
const formKey = ref(0);

// ✅ 两套字典（按你最新定义）
// 院级：college_audit_status 0待院级审核 1院级驳回 2院级审核通过
// 校级：school_audit_status 2待校级审核 1校级审核通过 0校级驳回
const { college_audit_status, school_audit_status, college_reason, school_reason } = proxy.useDict(
    "college_audit_status",
    "school_audit_status",
    "college_reason",
    "school_reason"
);

// ✅ API 映射
const apiMap = {
  college_level_unreviewed: { getFn: getCollege_level_unreviewed, addFn: addCollege_level_unreviewed, updateFn: updateCollege_level_unreviewed },
  college_level_reviewed: { getFn: getCollege_level_reviewed, addFn: addCollege_level_reviewed, updateFn: updateCollege_level_reviewed },
  school_level_unreviewed: { getFn: getSchool_level_unreviewed, addFn: addSchool_level_unreviewed, updateFn: updateSchool_level_unreviewed },
  school_level_reviewed: { getFn: getSchool_level_reviewed, addFn: addSchool_level_reviewed, updateFn: updateSchool_level_reviewed }
};

// ✅ 来源（由列表页点击审阅传 query.source）
const source = computed(() => String(route.query.source || "college_level_unreviewed").toLowerCase());
const mode = computed(() => {
  const normalized = String(route.query.mode || "edit").toLowerCase();
  return normalized === "review" ? "edit" : normalized;
});
const reviewPageTitle = computed(() => {
  if (source.value === "college_level_unreviewed") return "院级成果审核（待审核）";
  if (source.value === "college_level_reviewed") return "院级成果审核（已审核）";
  if (source.value === "school_level_unreviewed") return "校级成果审核（待审核）";
  if (source.value === "school_level_reviewed") return "校级成果审核（已审核）";
  return "成果审核";
});

const isEdit = computed(() => mode.value === "edit");
const isView = computed(() => mode.value === "view");
const showBottomDock = computed(() => isEdit.value || isView.value);
const showAuditControls = computed(() => isEdit.value);
const currentApi = computed(() => apiMap[source.value] || apiMap.college_level_unreviewed);

const backRouteMap = {
  college_level_unreviewed: "CollegeLevelUnreviewed",
  college_level_reviewed: "CollegeLevelReviewed",
  school_level_unreviewed: "SchoolLevelUnreviewed",
  school_level_reviewed: "SchoolLevelReviewed"
};

// ✅ 根据来源决定使用哪套字典
const currentAuditDict = computed(() => {
  if (source.value.startsWith("college")) return college_audit_status?.value || [];
  if (source.value.startsWith("school")) return school_audit_status?.value || [];
  return [];
});

const nextStatusOptions = computed(() => {
  const options = currentAuditDict.value || [];

  if (source.value === "college_level_unreviewed") {
    return options.filter((d) => ["1", "2"].includes(String(d.value)));
  }

  if (source.value === "college_level_reviewed") {
    return options.filter((d) => ["0", "1", "2"].includes(String(d.value)));
  }

  if (source.value === "school_level_unreviewed" || source.value === "school_level_reviewed") {
    return options.filter((d) => ["0", "1"].includes(String(d.value)));
  }

  return [];
});

const selectedAuditStatus = ref("");
const rejectReason = ref([]);
const rejectReasonCustom = ref("");

function normalizeRejectReasonOptions(dictItems = []) {
  return (dictItems || [])
      .map((item) => {
        const label = normalizeLooseText(item?.label);
        const value = normalizeLooseText(item?.value);
        if (!label && !value) return null;
        return { label: label || value, value: value || label };
      })
      .filter(Boolean);
}

function splitRejectReasonText(value) {
  const text = normalizeLooseText(value);
  if (!text) return [];
  return Array.from(new Set(text
      .split(/[；;\n]+/)
      .map((item) => normalizeLooseText(item))
      .filter(Boolean)));
}

function findRejectReasonOption(options = [], token) {
  const text = normalizeLooseText(token);
  if (!text) return null;
  return (options || []).find((opt) => opt.value === text || opt.label === text) || null;
}

function parseRejectReasonState(value, options = []) {
  const selectedValues = [];
  const customTexts = [];
  splitRejectReasonText(value).forEach((item) => {
    const matched = findRejectReasonOption(options, item);
    if (matched) {
      selectedValues.push(matched.value);
    } else {
      customTexts.push(item);
    }
  });
  return {
    selectedValues: Array.from(new Set(selectedValues)),
    customText: Array.from(new Set(customTexts)).join(REJECT_REASON_SEPARATOR)
  };
}

function formatRejectReasonText(values, customText = "") {
  const manualTexts = splitRejectReasonText(customText);
  return Array.from(new Set([...(Array.isArray(values) ? values : [])
      .map((item) => normalizeLooseText(item))
      .filter(Boolean), ...manualTexts]))
      .join(REJECT_REASON_SEPARATOR);
}

const baseRejectReasonOptions = computed(() => {
  const dictItems = isCollegeSource.value ? college_reason?.value : school_reason?.value;
  return normalizeRejectReasonOptions(dictItems || []);
});

const currentRejectReasonOptions = computed(() => baseRejectReasonOptions.value);

const auditInitialized = ref(false);
const saveFormLoading = ref(false);

function isStatusSelectable(status) {
  if (status === null || status === undefined || status === "") {
    return false;
  }
  return (nextStatusOptions.value || []).some((opt) => String(opt.value) === String(status));
}

function normalizeLooseText(value) {
  if (value === null || value === undefined) return "";
  const text = String(value).trim();
  if (!text) return "";
  const lower = text.toLowerCase();
  if (lower === "null" || lower === "undefined") return "";
  if (text === LEGACY_AUTO_SCHOOL_REJECT_REASON) return AUTO_SCHOOL_REJECT_REASON;
  return text;
}

const pageKey = computed(() => String(route.query.pageKey || ""));
const pageIdsRef = ref([]);
const pageGroupsRef = ref([]);
const currentPageIdsRef = ref([]);
const currentPageIndexRef = ref(-1);

function readJson(key, fallback) {
  try {
    const raw = sessionStorage.getItem(key);
    if (!raw) return fallback;
    return JSON.parse(raw);
  } catch (e) {
    return fallback;
  }
}

function parsePageIds(value) {
  if (!value) return [];
  return Array.from(new Set(String(value)
      .split(",")
      .map((v) => String(v).trim())
      .filter(Boolean)));
}

function findPageIndexById(id) {
  const normalizedId = String(id || "");
  if (!normalizedId) return -1;
  return pageGroupsRef.value.findIndex((group) => group.some((item) => String(item) === normalizedId));
}

function resolveCurrentPageIdsByIndex(index) {
  if (index < 0 || index >= pageGroupsRef.value.length) return [];
  return pageGroupsRef.value[index] || [];
}

function loadPageIds() {
  currentPageIndexRef.value = -1;

  const fromQuery = parsePageIds(route.query.pageIds);
  if (fromQuery.length > 0) {
    pageIdsRef.value = fromQuery;
    if (pageKey.value) {
      try {
        sessionStorage.setItem(`review_page_${pageKey.value}`, JSON.stringify(fromQuery));
      } catch (e) {
        // ignore storage errors
      }
    }
  } else {
    pageIdsRef.value = pageKey.value ? parsePageIds(readJson(`review_page_${pageKey.value}`, [])) : [];
  }

  const groupFromQuery = parsePageIds(route.query.currentPageIds);
  if (groupFromQuery.length > 0) {
    currentPageIdsRef.value = groupFromQuery;
    if (pageKey.value) {
      try {
        sessionStorage.setItem(`review_current_page_${pageKey.value}`, JSON.stringify(groupFromQuery));
      } catch (e) {
        // ignore storage errors
      }
    }
  } else {
    currentPageIdsRef.value = pageKey.value ? parsePageIds(readJson(`review_current_page_${pageKey.value}`, [])) : [];
  }

  const storedGroups = pageKey.value ? readJson(`review_page_groups_${pageKey.value}`, []) : [];
  pageGroupsRef.value = Array.isArray(storedGroups)
      ? storedGroups.map((group) => parsePageIds(Array.isArray(group) ? group.join(",") : group)).filter((group) => group.length > 0)
      : [];

  const routePageIndex = Number(route.query.pageIndex);
  if (Number.isFinite(routePageIndex) && routePageIndex >= 0) {
    currentPageIndexRef.value = routePageIndex;
  } else {
    const routeId = String(route.query.id || "");
    currentPageIndexRef.value = findPageIndexById(routeId);
  }

  if ((!currentPageIdsRef.value || currentPageIdsRef.value.length === 0) && currentPageIndexRef.value >= 0) {
    currentPageIdsRef.value = resolveCurrentPageIdsByIndex(currentPageIndexRef.value);
  }
}

function persistPageIds() {
  if (!pageKey.value) return;
  try {
    sessionStorage.setItem(`review_page_${pageKey.value}`, JSON.stringify(pageIdsRef.value));
    sessionStorage.setItem(`review_current_page_${pageKey.value}`, JSON.stringify(currentPageIdsRef.value));
    sessionStorage.setItem(`review_page_groups_${pageKey.value}`, JSON.stringify(pageGroupsRef.value));
  } catch (e) {
    // ignore storage errors
  }
}

function getReviewIndexById(id) {
  const normalizedId = String(id || "");
  if (!normalizedId) return -1;
  return pageIdsRef.value.findIndex((item) => String(item) === normalizedId);
}

function buildReviewQuery(targetId, targetPageIndex) {
  const fallbackPageNum = currentPageIndexRef.value >= 0 ? currentPageIndexRef.value + 1 : 1;
  const targetPageNum = targetPageIndex >= 0
      ? targetPageIndex + 1
      : Number(route.query.pageNum || fallbackPageNum);
  const query = {
    id: targetId,
    source: source.value,
    mode: mode.value,
    pageNum: targetPageNum
  };
  if (route.query.fromName) query.fromName = String(route.query.fromName);
  if (route.query.fromPath) query.fromPath = String(route.query.fromPath);
  if (route.query.from) query.from = String(route.query.from);
  if (pageKey.value) query.pageKey = pageKey.value;
  if (!pageKey.value && pageIdsRef.value.length > 0) query.pageIds = pageIdsRef.value.join(",");
  const pageSize = Number(route.query.pageSize || 0);
  if (pageSize > 0) query.pageSize = pageSize;
  if (targetPageIndex >= 0) {
    query.pageIndex = targetPageIndex;
    const targetPageIds = resolveCurrentPageIdsByIndex(targetPageIndex);
    if (targetPageIds.length > 0) query.currentPageIds = targetPageIds.join(",");
  } else if (currentPageIdsRef.value.length > 0) {
    query.currentPageIds = currentPageIdsRef.value.join(",");
  }
  return query;
}

function navigateToReview(targetId, targetPageIndex) {
  return router.push({ path: route.path, query: buildReviewQuery(targetId, targetPageIndex) });
}

function confirmNoNextResult() {
  return ElMessageBox.confirm(
      "当前已经是最后一个成果，没有下一个成果了。\n\n请选择返回列表，或继续留在当前页面。",
      "系统提示",
      {
        confirmButtonText: "返回列表",
        cancelButtonText: "留在当前页面",
        type: "warning"
      }
  )
      .then(() => {
        handleBack();
        return true;
      })
      .catch(() => false);
}

function confirmNoPrevResult() {
  return ElMessageBox.confirm(
      "当前已经是第一个成果，没有上一个成果了。\n\n请选择返回列表，或继续留在当前页面。",
      "系统提示",
      {
        confirmButtonText: "返回列表",
        cancelButtonText: "留在当前页面",
        type: "warning"
      }
  )
      .then(() => {
        handleBack();
        return true;
      })
      .catch(() => false);
}

function handlePrev() {
  loadPageIds();
  const currentId = String(route.query.id || "");
  const currentIndex = getReviewIndexById(currentId);
  if (currentIndex <= 0) {
    confirmNoPrevResult();
    return;
  }
  const prevId = pageIdsRef.value[currentIndex - 1];
  const prevPageIndex = findPageIndexById(prevId);
  const isFirstOfCurrentPage = currentPageIdsRef.value.length > 0
      && String(currentPageIdsRef.value[0]) === String(currentId)
      && prevPageIndex !== currentPageIndexRef.value;

  if (isFirstOfCurrentPage) {
    proxy.$modal
        ?.confirm?.("当前已到本页第一条成果，是否开始查看上一页成果？")
        ?.then(() => {
          navigateToReview(prevId, prevPageIndex);
        })
        ?.catch(() => {});
    return;
  }

  navigateToReview(prevId, prevPageIndex);
}

function handleNext() {
  loadPageIds();
  const currentId = String(route.query.id || "");
  const currentIndex = getReviewIndexById(currentId);
  if (currentIndex < 0 || currentIndex >= pageIdsRef.value.length - 1) {
    confirmNoNextResult();
    return;
  }
  const nextId = pageIdsRef.value[currentIndex + 1];
  const nextPageIndex = findPageIndexById(nextId);
  const isLastOfCurrentPage = currentPageIdsRef.value.length > 0
      && String(currentPageIdsRef.value[currentPageIdsRef.value.length - 1]) === String(currentId)
      && nextPageIndex !== currentPageIndexRef.value;

  if (isLastOfCurrentPage) {
    proxy.$modal
        ?.confirm?.("当前页面成果已审核完毕，是否开始审核下一页？")
        ?.then(() => {
          navigateToReview(nextId, nextPageIndex);
        })
        ?.catch(() => {});
    return;
  }

  navigateToReview(nextId, nextPageIndex);
}

// 是否为院级来源
const isCollegeSource = computed(() => source.value.startsWith("college"));
// 是否为校级来源
const isSchoolSource = computed(() => source.value.startsWith("school"));

const isCollegeReject = computed(() => isCollegeSource.value && String(selectedAuditStatus.value) === "1");
const isSchoolReject = computed(() => isSchoolSource.value && String(selectedAuditStatus.value) === "0");

const showRejectReason = computed(() => isCollegeReject.value || isSchoolReject.value);
const rejectReasonPlaceholder = computed(() =>
    isCollegeReject.value ? "请选择院级驳回原因" : "请选择校级驳回原因"
);
const rejectReasonCustomPlaceholder = computed(() =>
    isCollegeReject.value ? "请输入其他院级驳回原因" : "请输入其他校级驳回原因"
);

// ✅ 默认优先选“通过”
// - 院级通过 = 2
// - 校级通过 = 1
function setDefaultSelected() {
  if (selectedAuditStatus.value) return;
  const options = nextStatusOptions.value || [];
  if (!options.length) {
    selectedAuditStatus.value = "";
    return;
  }

  const preferValue = source.value === "college_level_unreviewed" ? "2" : "1";
  const prefer = options.find((opt) => String(opt.value) === preferValue);
  selectedAuditStatus.value = String((prefer || options[0]).value);
}

watch(
    nextStatusOptions,
    () => {
      selectedAuditStatus.value = "";
      setDefaultSelected();
    },
    { immediate: true }
);

watch(
    () => selectedAuditStatus.value,
    () => {
      if (!showRejectReason.value) {
        rejectReason.value = [];
        rejectReasonCustom.value = "";
      }
    }
);

function syncAuditFromForm() {
  if (auditInitialized.value) return;
  const form = dlgRef.value?.getForm?.();
  const id = route.query.id;
  if (!form || !id || !form.achievementId) return;
  if (String(form.achievementId) !== String(id)) return;

  if (isCollegeSource.value) {
    const status = form.reviewResult ?? form.review_result;
    if (isStatusSelectable(status)) {
      selectedAuditStatus.value = String(status);
    } else {
      setDefaultSelected();
    }

    if (String(selectedAuditStatus.value) === "1") {
      const parsedReason = parseRejectReasonState(form.reviewReason, baseRejectReasonOptions.value);
      rejectReason.value = parsedReason.selectedValues;
      rejectReasonCustom.value = parsedReason.customText;
    } else {
      rejectReason.value = [];
      rejectReasonCustom.value = "";
    }
  } else if (isSchoolSource.value) {
    const status = form.schooiReviewResult ?? form.schooi_review_result ?? form.schoolReviewResult ?? form.school_review_result;
    if (isStatusSelectable(status)) {
      selectedAuditStatus.value = String(status);
    } else {
      setDefaultSelected();
    }

    if (String(selectedAuditStatus.value) === "0") {
      const parsedReason = parseRejectReasonState(form.schoolReviewReason || form.school_review_reason, baseRejectReasonOptions.value);
      rejectReason.value = parsedReason.selectedValues;
      rejectReasonCustom.value = parsedReason.customText;
    } else {
      rejectReason.value = [];
      rejectReasonCustom.value = "";
    }
  }

  auditInitialized.value = true;
}

watch(
    baseRejectReasonOptions,
    (options) => {
      if (!options?.length) return;
      if (!dlgRef.value?.getForm?.()) return;
      auditInitialized.value = false;
      syncAuditFromForm();
    }
);

watch(
    () => {
      const form = dlgRef.value?.getForm?.();
      return [
        form?.achievementId,
        form?.reviewResult,
        form?.schooiReviewResult,
        form?.reviewReason,
        form?.schoolReviewReason
      ];
    },
    () => {
      syncAuditFromForm();
    },
    { immediate: true }
);
function buildBackQuery() {
  const query = {};
  const currentPageNum = currentPageIndexRef.value >= 0
      ? currentPageIndexRef.value + 1
      : Number(route.query.pageNum || 1);

  if (currentPageNum > 0) query.pageNum = currentPageNum;

  const pageSize = Number(route.query.pageSize || 0);
  if (pageSize > 0) query.pageSize = pageSize;

  return query;
}

function handleBack() {
  const backQuery = buildBackQuery();
  const fromPath = route.query.fromPath;
  if (fromPath && String(fromPath) !== String(route.path)) {
    router.push({
      path: String(fromPath),
      query: backQuery
    }).catch(() => router.back());
    return;
  }

  const fromName = route.query.fromName || route.query.from;
  if (fromName && String(fromName) !== String(route.name || "")) {
    router.push({
      name: String(fromName),
      query: backQuery
    }).catch(() => router.back());
    return;
  }

  const mappedName = backRouteMap[source.value];
  if (mappedName) {
    router.push({
      name: mappedName,
      query: backQuery
    }).catch(() => router.back());
    return;
  }

  router.back();
}

function handleFormSaved() {
  // 审核页面的“保存修改”只保存表单内容，不自动返回列表
}

async function saveFormChanges() {
  if (!isEdit.value || isView.value) {
    proxy.$modal?.msgWarning?.("当前页面为查看模式，不能保存修改");
    return;
  }
  if (!dlgRef.value?.submitForm) {
    proxy.$modal?.msgError?.("当前表单不支持保存");
    return;
  }
  if (saveFormLoading.value) {
    return;
  }

  saveFormLoading.value = true;
  try {
    await dlgRef.value.submitForm();
  } catch (e) {
    // 具体错误提示由表单内部统一处理
  } finally {
    saveFormLoading.value = false;
  }
}

function jumpToNextAfterAudit(currentId) {
  loadPageIds();
  const currentIndex = getReviewIndexById(currentId);
  if (currentIndex < 0) {
    proxy.$modal?.msgWarning?.("当前页面没有需要审核的成果");
    return Promise.resolve(false);
  }

  const nextId = pageIdsRef.value[currentIndex + 1];
  if (!nextId) {
    return confirmNoNextResult();
  }

  const nextPageIndex = findPageIndexById(nextId);
  const isLastOfCurrentPage = currentPageIdsRef.value.length > 0
      && String(currentPageIdsRef.value[currentPageIdsRef.value.length - 1]) === String(currentId)
      && nextPageIndex !== currentPageIndexRef.value;

  const confirmText = isLastOfCurrentPage
      ? "当前已完成本页全部成果审核，是否开始审核下一页成果？"
      : "当前成果已审核完成，是否继续审核下一个成果？";

  return proxy.$modal
      .confirm(confirmText)
      .then(() => {
        navigateToReview(nextId, nextPageIndex);
        return true;
      })
      .catch(() => {
        proxy.$modal?.msgSuccess?.("已提交审核，留在当前页面");
        return false;
      });
}


async function submitAudit() {
  if (!isEdit.value || isView.value) {
    proxy.$modal?.msgWarning?.("当前页面为查看模式，不能提交审核");
    return;
  }

  const baseForm = dlgRef.value?.getForm?.();
  const id = baseForm?.achievementId ?? route.query.id;

  if (!id) {
    proxy.$modal?.msgError?.("未获取到成果ID，无法审核");
    return;
  }
  if (!baseForm) {
    proxy.$modal?.msgError?.("成果信息未加载完成，无法审核");
    return;
  }

  const next = String(selectedAuditStatus.value || "");
  if (!next) return;

  const rejectReasonText = showRejectReason.value ? formatRejectReasonText(rejectReason.value, rejectReasonCustom.value) : "";

  if (showRejectReason.value && !rejectReasonText) {
    proxy.$modal?.msgError?.(isCollegeReject.value ? "请填写院级驳回原因" : "请填写校级驳回原因");
    return;
  }

  // ✅ 尽可能拿到当前登录用户（按你项目实际字段可能不同）
  const auditUser =
      proxy?.$store?.state?.user?.userName ||
      proxy?.$store?.state?.user?.nickName ||
      proxy?.$store?.state?.user?.name ||
      "";

  const now = new Date();
  const collegeAuditReason = isCollegeReject.value ? rejectReason.value.trim() : "";
  const schoolAuditReason = isSchoolReject.value ? rejectReason.value.trim() : "";

  try {
    const supportedSources = [
      "college_level_unreviewed",
      "college_level_reviewed",
      "school_level_unreviewed",
      "school_level_reviewed"
    ];
    if (!supportedSources.includes(source.value)) {
      proxy.$modal?.msgError?.("当前来源(source)不支持审核（请从审核列表进入）");
      return;
    }

    await submitReviewAction({
      source: source.value,
      achievementId: String(id),
      reviewStatus: Number(next),
      rejectReason: rejectReasonText
    });

    notifyReviewResultApplied(id, Number(next), rejectReasonText);
    proxy.$modal?.msgSuccess?.(isCollegeSource.value ? "院级审核成功" : "校级审核成功");
    await jumpToNextAfterAudit(id);
  } catch (e) {
    proxy.$modal?.msgError?.("审核失败（可能已归档/已推送或接口限制）");
    const errMsg =
        e?.response?.data?.msg ||
        e?.message ||
        "审核失败";
    proxy.$modal?.msgError?.(errMsg);
  }
}

function loadCurrent() {
  formKey.value = Date.now();
  auditInitialized.value = false;
  loadPageIds();
  persistPageIds();
  nextTick(() => {
    const id = route.query.id;
    dlgRef.value?.open(id);
    setDefaultSelected();
  });
}

function handleReviewNavigationReady(event) {
  const detail = event?.detail || {};
  if (detail.pageKey && String(detail.pageKey) !== String(pageKey.value || "")) return;
  if (detail.source && String(detail.source) !== String(source.value || "")) return;
  loadPageIds();
  persistPageIds();
}

function notifyReviewResultApplied(achievementId, reviewStatus, rejectReasonText = "") {
  if (!achievementId || typeof window === "undefined" || typeof window.dispatchEvent !== "function") return;
  window.dispatchEvent(new CustomEvent(REVIEW_RESULT_APPLIED_EVENT, {
    detail: {
      achievementId: String(achievementId),
      source: source.value,
      reviewStatus,
      rejectReason: rejectReasonText
    }
  }));
}

onMounted(() => {
  if (typeof window !== "undefined") {
    window.addEventListener(REVIEW_NAV_READY_EVENT, handleReviewNavigationReady);
  }
  loadCurrent();
});

onActivated(() => {
  loadCurrent();
});

onBeforeUnmount(() => {
  if (typeof window !== "undefined") {
    window.removeEventListener(REVIEW_NAV_READY_EVENT, handleReviewNavigationReady);
  }
});

watch(
    () => [route.query.id, route.query.source, route.query.pageKey, route.query.pageIndex, route.query.currentPageIds],
    () => {
      loadCurrent();
    }
);

watch(
    () => [route.query.pageKey, route.query.pageIds, route.query.currentPageIds, route.query.pageIndex],
    () => {
      loadPageIds();
    }
);
</script>

<style scoped>
.review-page {
  padding: 0;
  padding-bottom: 92px;
}
.review-fixed-dock {
  position: fixed;
  left: auto;
  right: 18px;
  bottom: 10px;
  transform: none;
  width: calc(100% - 200px - 36px);
  max-width: none;
  z-index: 2000;
  pointer-events: none;
  display: flex;
  justify-content: center;
}
/* 左侧菜单收起时 */
:global(.hideSidebar) .review-fixed-dock {
  left: auto;
  width: calc(100% - 54px - 36px);
}

/* 菜单完全隐藏时 */
:global(.sidebarHide) .review-fixed-dock {
  left: auto;
  width: calc(100% - 36px);
}

.review-fixed-inner {
  width: fit-content;
  max-width: 100%;
  margin: 0 auto;
  pointer-events: auto;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--el-border-color-light);
  border-radius: 10px;
  backdrop-filter: blur(8px);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.12);
  display: flex;
  justify-content: center;
}
.audit-toolbar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
  padding: 8px 16px;
  border: 0;
  background: transparent;
}
.audit-toolbar--nav-only {
  padding: 10px 16px;
}

.audit-toolbar-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  flex: 0 1 auto;
  min-width: 0;
  flex-wrap: wrap;
}
.nav-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-btn {
  min-width: 82px;
}
.back-btn {
  --el-button-text-color: #6b4d12;
  --el-button-border-color: #f0cf8a;
  --el-button-hover-text-color: #5d3f0a;
  --el-button-hover-bg-color: #fff6df;
  --el-button-hover-border-color: #e8be63;
  --el-button-active-bg-color: #ffefc6;
}
.audit-field {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  padding-left: 14px;
}
.status-field::before,
.reason-field::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  width: 1px;
  height: 24px;
  background: var(--el-border-color-lighter);
  transform: translateY(-50%);
}
.status-field {
  flex: 0 0 auto;
}
.reason-field {
  flex: 0 0 100%;
  width: 100%;
  min-width: 0;
  padding-left: 0;
}
.reason-field::before {
  display: none;
}
.audit-reason-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}
.audit-label {
  color: #4e5969;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}
.audit-status-select {
  width: 210px;
}
.audit-reason-select {
  width: 100%;
  min-width: 260px;
}
.audit-reason-select :deep(.el-select__wrapper) {
  box-shadow: 0 0 0 1px #d6e4ff inset;
}

.audit-reason-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #7ea8ff inset, 0 0 0 3px rgba(31, 111, 255, 0.12);
}
.audit-reason-input {
  width: 100%;
}
.submit-btn {
  min-width: 108px;
}
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.save-btn {
  min-width: 108px;
}

.prev-btn {
  --el-button-text-color: #2f5fd4;
  --el-button-border-color: #b9ccff;
  --el-button-hover-text-color: #1d4fc9;
  --el-button-hover-bg-color: #eef4ff;
  --el-button-hover-border-color: #8aabff;
  --el-button-active-bg-color: #e3ecff;
}

.next-btn {
  --el-button-text-color: #0f7a62;
  --el-button-border-color: #9ddbc8;
  --el-button-hover-text-color: #0a6a54;
  --el-button-hover-bg-color: #eaf9f4;
  --el-button-hover-border-color: #73ceb1;
  --el-button-active-bg-color: #ddf3ec;
}

.submit-btn {
  --el-button-bg-color: #1f6fff;
  --el-button-border-color: #1f6fff;
  --el-button-hover-bg-color: #3a82ff;
  --el-button-hover-border-color: #3a82ff;
  --el-button-active-bg-color: #195fe0;
  --el-button-active-border-color: #195fe0;
  box-shadow: 0 6px 14px rgba(31, 111, 255, 0.24);
}

.submit-btn.is-disabled {
  box-shadow: none;
}

.audit-status-select :deep(.el-select__wrapper) {
  box-shadow: 0 0 0 1px #d8e5ff inset;
}

.audit-status-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #7ea8ff inset, 0 0 0 3px rgba(31, 111, 255, 0.12);
}

.audit-reason :deep(.el-textarea__inner) {
  border-color: #d6e4ff;
}

.audit-reason :deep(.el-textarea__inner:focus) {
  border-color: #7ea8ff;
  box-shadow: 0 0 0 3px rgba(31, 111, 255, 0.12);
}
.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

@media (max-width: 992px) {
  .audit-toolbar {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 14px;
    flex-wrap: wrap;
    padding: 8px 16px;
    border: 0;
    background: transparent;
  }

  .review-page {
    padding-bottom: 138px;
  }

  .review-fixed-dock {
    position: fixed;
    left: auto;
    right: 18px;
    bottom: 10px;
    transform: none;
    width: calc(100% - 200px - 36px);
    max-width: none;
    z-index: 2000;
    pointer-events: none;
    display: flex;
    justify-content: center;
  }

  :global(.hideSidebar) .review-fixed-dock {
    left: auto;
    width: calc(100% - 54px - 36px);
  }

  :global(.mobile) .review-fixed-dock,
  :global(.sidebarHide) .review-fixed-dock {
    left: auto;
    width: calc(100% - 36px);
  }
  .audit-toolbar-main {
    width: 100%;
  }
  .nav-toolbar {
    width: 100%;
  }
  .nav-btn {
    flex: 1;
    min-width: 0;
  }
  .status-field,
  .reason-field {
    width: 100%;
    min-width: 0;
    padding-left: 0;
  }
  .status-field::before,
  .reason-field::before {
    display: none;
  }
  .audit-status-select {
    width: 100%;
  }
  .submit-btn {
    width: 100%;
  }
  .toolbar-actions {
    width: 100%;
    flex-direction: column;
  }
  .save-btn {
    width: 100%;
  }
}
.header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}

.success-id {
  font-size: 14px;
  color: #6c757d;
  margin-left: 10px;
}
</style>
