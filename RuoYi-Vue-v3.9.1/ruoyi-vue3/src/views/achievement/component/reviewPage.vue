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
        titleAdd="成果审核"
        titleEdit="成果审核"
        @cancel="handleBack"
        @ok="handleBack"
    />

    <!-- ✅ 固定底部审核工具条：不随页面滚动消失 -->
    <div v-if="showAuditToolbar" class="review-fixed-dock">
      <div class="review-fixed-inner">
        <div class="audit-toolbar">
          <div class="audit-toolbar-main">
            <div class="nav-toolbar">
              <el-button class="nav-btn back-btn" plain @click="handleBack">返回列表</el-button>
              <el-button class="nav-btn prev-btn" plain @click="handlePrev">上一个</el-button>
              <el-button class="nav-btn next-btn" plain @click="handleNext">下一个</el-button>
            </div>

            <div class="audit-field status-field">
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

            <div v-if="showRejectReason" class="audit-field reason-field">
              <span class="audit-label">驳回原因</span>
              <el-input
                  v-model="rejectReason"
                  type="textarea"
                  :rows="1"
                  maxlength="200"
                  show-word-limit
                  class="audit-reason"
                  :placeholder="rejectReasonPlaceholder"
              />
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
import { computed, onMounted, onActivated, ref, getCurrentInstance, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import useUserStore from "@/store/modules/user";

import AchievementForm from "@/views/achievement/component/AchievementForm.vue";

// ====== 四套 API（你已有）======
import { getCollege_level_unreviewed, addCollege_level_unreviewed, updateCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import { getCollege_level_reviewed, addCollege_level_reviewed, updateCollege_level_reviewed } from "@/api/achievement/college_level_reviewed";
import { getSchool_level_unreviewed, addSchool_level_unreviewed, updateSchool_level_unreviewed } from "@/api/achievement/school_level_unreviewed";
import { getSchool_level_reviewed, addSchool_level_reviewed, updateSchool_level_reviewed } from "@/api/achievement/school_level_reviewed";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const dlgRef = ref(null);
const formKey = ref(0);

// ✅ 两套字典（按你最新定义）
// 院级：college_audit_status 0待院级审核 1院级驳回 2院级审核通过
// 校级：school_audit_status 2待校级审核 1校级审核通过 0校级驳回
const { college_audit_status, school_audit_status } = proxy.useDict("college_audit_status", "school_audit_status");

// ✅ API 映射
const apiMap = {
  college_level_unreviewed: { getFn: getCollege_level_unreviewed, addFn: addCollege_level_unreviewed, updateFn: updateCollege_level_unreviewed },
  college_level_reviewed: { getFn: getCollege_level_reviewed, addFn: addCollege_level_reviewed, updateFn: updateCollege_level_reviewed },
  school_level_unreviewed: { getFn: getSchool_level_unreviewed, addFn: addSchool_level_unreviewed, updateFn: updateSchool_level_unreviewed },
  school_level_reviewed: { getFn: getSchool_level_reviewed, addFn: addSchool_level_reviewed, updateFn: updateSchool_level_reviewed }
};

// ✅ 来源（由列表页点击审阅传 query.source）
const source = computed(() => String(route.query.source || "college_level_unreviewed").toLowerCase());
const mode = computed(() => String(route.query.mode || "review").toLowerCase());
const isEdit = computed(() => mode.value === "edit");
const isView = computed(() => mode.value === "view");
const showAuditToolbar = computed(() => true);
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

  if (source.value === "college_level_unreviewed" || source.value === "college_level_reviewed") {
    return options.filter((d) => ["1", "2"].includes(String(d.value)));
  }

  if (source.value === "school_level_unreviewed" || source.value === "school_level_reviewed") {
    return options.filter((d) => ["0", "1"].includes(String(d.value)));
  }

  return [];
});

const selectedAuditStatus = ref("");
const rejectReason = ref("");
const auditInitialized = ref(false);

function normalizeLooseText(value) {
  if (value === null || value === undefined) return "";
  const text = String(value).trim();
  if (!text) return "";
  const lower = text.toLowerCase();
  if (lower === "null" || lower === "undefined") return "";
  return text;
}

const pageKey = computed(() => String(route.query.pageKey || ""));
const pageIdsRef = ref([]);

const historyRef = ref([]);
const cursorRef = ref(-1);

const historyScope = computed(() => {
  const scopedPageKey = pageKey.value || String(route.query.fromPath || route.path || "default");
  return `${source.value || "unknown"}_${scopedPageKey}`;
});
const historyKey = computed(() => `review_history_${userStore.id || "anon"}_${historyScope.value}`);
const cursorKey = computed(() => `review_history_cursor_${userStore.id || "anon"}_${historyScope.value}`);

function readJson(key, fallback) {
  try {
    const raw = sessionStorage.getItem(key);
    if (!raw) return fallback;
    return JSON.parse(raw);
  } catch (e) {
    return fallback;
  }
}

function writeJson(key, value) {
  try {
    sessionStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    // ignore storage errors
  }
}

function parsePageIds(value) {
  if (!value) return [];
  return Array.from(new Set(String(value)
      .split(",")
      .map((v) => Number(v))
      .filter((v) => !Number.isNaN(v))))
      .sort((a, b) => a - b);
}

function loadPageIds() {
  const fromQuery = parsePageIds(route.query.pageIds);
  if (fromQuery.length > 0) {
    pageIdsRef.value = fromQuery;
    if (pageKey.value) {
      try {
        sessionStorage.setItem(`review_page_${pageKey.value}`, fromQuery.join(","));
      } catch (e) {
        // ignore storage errors
      }
    }
    return;
  }
  if (pageKey.value) {
    const stored = parsePageIds(sessionStorage.getItem(`review_page_${pageKey.value}`));
    pageIdsRef.value = stored;
  } else {
    pageIdsRef.value = [];
  }
}

function persistPageIds() {
  if (!pageKey.value) return;
  try {
    sessionStorage.setItem(`review_page_${pageKey.value}`, pageIdsRef.value.join(","));
  } catch (e) {
    // ignore storage errors
  }
}

function loadHistory() {
  const loaded = readJson(historyKey.value, []);
  historyRef.value = Array.isArray(loaded) ? loaded : [];
  const rawCursor = sessionStorage.getItem(cursorKey.value);
  const parsed = Number(rawCursor);
  cursorRef.value = Number.isFinite(parsed) ? parsed : -1;
  if (cursorRef.value >= historyRef.value.length) {
    cursorRef.value = historyRef.value.length - 1;
  }
}

function saveHistory() {
  writeJson(historyKey.value, historyRef.value);
  try {
    sessionStorage.setItem(cursorKey.value, String(cursorRef.value));
  } catch (e) {
    // ignore storage errors
  }
}

function syncHistoryOnEnter() {
  loadHistory();
  const id = route.query.id;
  if (!id) {
    saveHistory();
    return;
  }

  const nav = String(route.query.nav || "");
  const hIndex = Number(route.query.hIndex);
  if (nav === "history" && Number.isFinite(hIndex)) {
    cursorRef.value = hIndex;
    if (cursorRef.value < 0) cursorRef.value = 0;
    if (cursorRef.value >= historyRef.value.length) {
      cursorRef.value = historyRef.value.length - 1;
    }
    saveHistory();
    return;
  }

  if (cursorRef.value >= 0 && cursorRef.value < historyRef.value.length - 1) {
    historyRef.value = historyRef.value.slice(0, cursorRef.value + 1);
  }

  const entry = {
    id: String(id),
    source: source.value,
    mode: mode.value,
    path: route.path,
    time: Date.now(),
    pageKey: pageKey.value
  };

  const last = historyRef.value[historyRef.value.length - 1];
  if (!last || String(last.id) !== String(entry.id) || String(last.source) !== String(entry.source) ||
      String(last.path) !== String(entry.path) || String(last.mode) !== String(entry.mode)) {
    historyRef.value.push(entry);
  }
  cursorRef.value = historyRef.value.length - 1;
  saveHistory();
}

function handlePrev() {
  loadHistory();
  loadPageIds();
  if (cursorRef.value <= 0 || historyRef.value.length === 0) {
    proxy.$modal?.msgWarning?.("当前页面没有上一个审核记录");
    return;
  }
  const newCursor = cursorRef.value - 1;
  const target = historyRef.value[newCursor];
  if (!target || !target.id) {
    proxy.$modal?.msgWarning?.("当前页面没有上一个审核记录");
    return;
  }
  cursorRef.value = newCursor;
  saveHistory();
  const query = {
    id: target.id,
    source: target.source || source.value,
    mode: target.mode || "view",
    nav: "history",
    hIndex: newCursor
  };
  if (target.pageKey || pageKey.value) query.pageKey = target.pageKey || pageKey.value;
  if (pageIdsRef.value.length > 0) query.pageIds = pageIdsRef.value.join(",");
  router.push({ path: target.path || route.path, query });
}

function handleNext() {
  loadPageIds();
  const currentId = Number(route.query.id);
  const ids = pageIdsRef.value || [];
  if (Number.isNaN(currentId) || ids.length === 0) {
    proxy.$modal?.msgWarning?.("当前页面没有需要审核的成果");
    return;
  }
  const nextId = ids.find((id) => id > currentId);
  if (!nextId) {
    proxy.$modal?.msgWarning?.("当前页面没有需要审核的成果");
    return;
  }
  const query = {
    id: nextId,
    source: source.value,
    mode: mode.value
  };
  if (pageKey.value) query.pageKey = pageKey.value;
  if (pageIdsRef.value.length > 0) query.pageIds = pageIdsRef.value.join(",");
  router.push({ path: route.path, query });
}

// 是否为院级来源
const isCollegeSource = computed(() => source.value.startsWith("college"));
// 是否为校级来源
const isSchoolSource = computed(() => source.value.startsWith("school"));

const isCollegeReject = computed(() => isCollegeSource.value && String(selectedAuditStatus.value) === "1");
const isSchoolReject = computed(() => isSchoolSource.value && String(selectedAuditStatus.value) === "0");

const showRejectReason = computed(() => isCollegeReject.value || isSchoolReject.value);
const rejectReasonPlaceholder = computed(() =>
    isCollegeReject.value ? "请填写院级驳回原因" : "请填写校级驳回原因"
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
      if (!showRejectReason.value) rejectReason.value = "";
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
    if (status === 1 || status === "1" || status === 2 || status === "2") {
      selectedAuditStatus.value = String(status);
    } else {
      setDefaultSelected();
    }

    if (String(selectedAuditStatus.value) === "1") {
      rejectReason.value = form.reviewReason || "";
    } else {
      rejectReason.value = "";
    }
  } else if (isSchoolSource.value) {
    const status = form.schooiReviewResult ?? form.schooi_review_result ?? form.schoolReviewResult ?? form.school_review_result;
    if (status === 0 || status === "0" || status === 1 || status === "1") {
      selectedAuditStatus.value = String(status);
    } else {
      setDefaultSelected();
    }

    if (String(selectedAuditStatus.value) === "0") {
      rejectReason.value = form.schoolReviewReason || "";
    } else {
      rejectReason.value = "";
    }
  }

  auditInitialized.value = true;
}

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

function handleBack() {
  const fromName = route.query.fromName || route.query.from;
  if (fromName) {
    router.push({ name: String(fromName) }).catch(() => router.back());
    return;
  }

  const fromPath = route.query.fromPath;
  if (fromPath) {
    router.push({ path: String(fromPath) }).catch(() => router.back());
    return;
  }

  const mappedName = backRouteMap[source.value];
  if (mappedName) {
    router.push({ name: mappedName }).catch(() => router.back());
    return;
  }

  router.back();
}

function jumpToNextAfterAudit(currentId) {
  loadPageIds();
  const ids = pageIdsRef.value || [];
  const nextId = ids.find((id) => Number(id) > Number(currentId));
  if (!nextId) {
    if (!source.value.endsWith("reviewed")) {
      proxy.$modal?.msgWarning?.("当前页面没有需要审核的成果");
    }
    handleBack();
    return;
  }
  const query = {
    id: nextId,
    source: source.value,
    mode: mode.value
  };
  if (pageKey.value) query.pageKey = pageKey.value;
  if (pageIdsRef.value.length > 0) query.pageIds = pageIdsRef.value.join(",");
  router.push({ path: route.path, query });
}

/**
 * ✅ 手动流转归档推送（前端负责）：
 * 1) 院级未审核(college_level_unreviewed)
 *    - updateCollege_level_unreviewed：auditStatus 0 -> 1/2
 *    - 若通过(2)：updateSchool_level_unreviewed：推送到校级未审核（auditStatus=2 待校级审核）
 *
 * 2) 校级未审核(school_level_unreviewed)
 *    - updateSchool_level_unreviewed：auditStatus 2 -> 0/1
 *
 * ✅ 关键修复：
 * - 归档到校级已审核时必须写 schoolAuditTime（你的 mapper 有 school_audit_time is not null 的过滤）
 */
async function submitAudit() {
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

  if (showRejectReason.value && !rejectReason.value.trim()) {
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
  const currentSchoolStatusRaw =
      baseForm?.schooiReviewResult ?? baseForm?.schooi_review_result;
  const currentSchoolStatus =
      currentSchoolStatusRaw === null || currentSchoolStatusRaw === undefined
          ? ""
          : String(currentSchoolStatusRaw).trim();
  const schoolAuditByRaw =
      baseForm?.schoolAuditBy ?? baseForm?.school_audit_by;
  const schoolReviewReasonRaw =
      baseForm?.schoolReviewReason ?? baseForm?.school_review_reason;
  const schoolAuditBy = normalizeLooseText(schoolAuditByRaw);
  const schoolReviewReason = normalizeLooseText(schoolReviewReasonRaw);
  const hasSchoolReviewTrace = !!schoolAuditBy || !!schoolReviewReason;
  const schoolStatusLooksReviewed = currentSchoolStatus === "0" || currentSchoolStatus === "1";
  const canPushSchoolPending =
      currentSchoolStatus === "" ||
      currentSchoolStatus === "2" ||
      (schoolStatusLooksReviewed && !hasSchoolReviewTrace);

  try {
    // ===== 院级审核 =====
    if (source.value === "college_level_unreviewed") {
      const payload = {
        ...baseForm,
        achievementId: id,
        reviewResult: next,
        reviewReason: collegeAuditReason,
        reviewedAt: now,
        auditBy: auditUser
      };
      // Reject must not carry stale school status from form cache.
      delete payload.schooiReviewResult;
      delete payload.schooi_review_result;
      if (next === "2" && canPushSchoolPending) payload.schooiReviewResult = "2";

      // 1) 更新院级未审核状态（0 -> 1/2）
      await updateCollege_level_unreviewed(payload);

      // 2) 院级通过(2) -> 同表设置校级待审核状态（schooiReviewResult=2）

      if (source.value.endsWith("unreviewed")) {
        pageIdsRef.value = pageIdsRef.value.filter((pid) => String(pid) !== String(id));
        persistPageIds();
      }

      proxy.$modal?.msgSuccess?.("院级审核成功");

      // 在审核完成后，弹出确认是否跳转到下一个成果的弹窗
      // 提交审核后弹出确认是否跳转到下一个成果的弹窗
      proxy.$modal
          .confirm(`审核成功，是否跳转到下一个成果？`)
          .then(() => {
            // 用户点击“是”按钮后跳转到下一个成果
            jumpToNextAfterAudit(id);
          })
          .catch(() => {
            // 用户点击“否”按钮后留在当前页面
            proxy.$modal?.msgSuccess?.("已提交审核，留在当前页面");
          });
      return;
    }

    // 重新审核已经审核的院级记录
    if (source.value === "college_level_reviewed") {
      const payload = {
        ...baseForm,
        achievementId: id,
        reviewResult: next,
        reviewReason: collegeAuditReason,
        reviewedAt: now,
        auditBy: auditUser
      };
      delete payload.schooiReviewResult;
      delete payload.schooi_review_result;
      if (next === "2" && canPushSchoolPending) payload.schooiReviewResult = "2";

      await updateCollege_level_reviewed(payload);

      // 提交审核后弹出确认是否跳转到下一个成果的弹窗
      proxy.$modal
          .confirm(`审核成功，是否跳转到下一个成果？`)
          .then(() => {
            // 用户点击“是”按钮后跳转到下一个成果
            jumpToNextAfterAudit(id);
          })
          .catch(() => {
            // 用户点击“否”按钮后留在当前页面
            proxy.$modal?.msgSuccess?.("已提交审核，留在当前页面");
          });
      return;
    }

    // ===== 校级审核 =====
    if (source.value === "school_level_unreviewed") {
      // 1) 更新校级未审核状态（2 -> 0/1）
      await updateSchool_level_unreviewed({
        ...baseForm,
        achievementId: id,
        schooiReviewResult: next,
        schoolReviewReason: schoolAuditReason,
        schoolReviewedAt: now,
        schoolAuditBy: auditUser
      });

      if (source.value.endsWith("unreviewed")) {
        pageIdsRef.value = pageIdsRef.value.filter((pid) => String(pid) !== String(id));
        persistPageIds();
      }

      proxy.$modal?.msgSuccess?.("校级审核成功");

      // 提交审核后弹出确认是否跳转到下一个成果的弹窗
      proxy.$modal
          .confirm(`审核成功，是否跳转到下一个成果？`)
          .then(() => {
            // 用户点击“是”按钮后跳转到下一个成果
            jumpToNextAfterAudit(id);
          })
          .catch(() => {
            // 用户点击“否”按钮后留在当前页面
            proxy.$modal?.msgSuccess?.("已提交审核，留在当前页面");
          });
      return;
    }

    if (source.value === "school_level_reviewed") {
      // 2) 重新审核已审核记录
      await updateSchool_level_reviewed({
        ...baseForm,
        achievementId: id,
        schooiReviewResult: next,
        schoolReviewReason: schoolAuditReason,
        schoolReviewedAt: now,
        schoolAuditBy: auditUser
      });

      if (source.value.endsWith("unreviewed")) {
        pageIdsRef.value = pageIdsRef.value.filter((pid) => String(pid) !== String(id));
        persistPageIds();
      }

      proxy.$modal?.msgSuccess?.("校级审核成功");

      // 提交审核后弹出确认是否跳转到下一个成果的弹窗
      proxy.$modal
          .confirm(`审核成功，是否跳转到下一个成果？`)
          .then(() => {
            // 用户点击“是”按钮后跳转到下一个成果
            jumpToNextAfterAudit(id);
          })
          .catch(() => {
            // 用户点击“否”按钮后留在当前页面
            proxy.$modal?.msgSuccess?.("已提交审核，留在当前页面");
          });
      return;
    }

    proxy.$modal?.msgError?.("当前来源(source)不支持审核（请从未审核列表进入）");
  } catch (e) {
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
  syncHistoryOnEnter();
  nextTick(() => {
    const id = route.query.id;
    dlgRef.value?.open(id);
    setDefaultSelected();
  });
}

onMounted(() => {
  loadCurrent();
});

onActivated(() => {
  loadCurrent();
});

watch(
    () => [route.query.id, route.query.source, route.query.pageKey],
    () => {
      loadCurrent();
    }
);

watch(
    () => [route.query.pageKey, route.query.pageIds],
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
  left: 50%;
  right: auto;
  bottom: 10px;
  transform: translateX(-50%);
  width: min(1180px, calc(100vw - 36px));
  z-index: 2000;
  pointer-events: none;
}
.review-fixed-inner {
  width: 100%;
  margin: 0;
  pointer-events: auto;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  backdrop-filter: blur(8px);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.12);
}
.audit-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
  padding: 8px 2px;
  border: 0;
  border-radius: 0;
  background: transparent;
}
.audit-toolbar-main {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
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
  border-radius: 999px;
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
  flex: 1;
  min-width: 320px;
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
.audit-reason {
  width: 100%;
  min-width: 260px;
}
.submit-btn {
  min-width: 108px;
  border-radius: 999px;
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
  border-radius: 10px;
  box-shadow: 0 0 0 1px #d8e5ff inset;
}

.audit-status-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #7ea8ff inset, 0 0 0 3px rgba(31, 111, 255, 0.12);
}

.audit-reason :deep(.el-textarea__inner) {
  border-radius: 10px;
  border-color: #d6e4ff;
}

.audit-reason :deep(.el-textarea__inner:focus) {
  border-color: #7ea8ff;
  box-shadow: 0 0 0 3px rgba(31, 111, 255, 0.12);
}

@media (max-width: 992px) {
  .audit-toolbar {
    align-items: stretch;
    padding: 4px 0;
  }
  .review-page {
    padding-bottom: 138px;
  }
  .review-fixed-dock {
    bottom: 8px;
    width: calc(100vw - 12px);
  }
  .review-fixed-inner {
    border-radius: 10px;
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
}
.header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px; /* 增加一些间距使其更明显 */
}

.success-id {
  font-size: 14px;
  color: #6c757d;
  margin-left: 10px;
}
</style>
