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
    >
      <!-- ✅ 外置审核工具条：下拉 + 提交审核（手动流转） -->
      <template v-if="showAuditToolbar" #footer-left>
        <div class="audit-toolbar">
          <span class="audit-label">审核后状态</span>

          <el-select
              v-model="selectedAuditStatus"
              placeholder="请选择"
              style="width: 220px;"
          >
            <el-option
                v-for="opt in nextStatusOptions"
                :key="String(opt.value)"
                :label="opt.label"
                :value="String(opt.value)"
            />
          </el-select>

          <el-input
              v-if="showRejectReason"
              v-model="rejectReason"
              type="textarea"
              :rows="1"
              maxlength="200"
              show-word-limit
              class="audit-reason"
              :placeholder="rejectReasonPlaceholder"
          />

          <el-button type="primary" :disabled="!selectedAuditStatus" @click="submitAudit">
            提交审核
          </el-button>
        </div>
      </template>
    </AchievementForm>
  </div>
</template>

<script setup name="ReviewPage">
import { computed, onMounted, onActivated, ref, getCurrentInstance, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";

import AchievementForm from "@/views/achievement/component/AchievementForm.vue";

// ====== 四套 API（你已有）======
import { getCollege_level_unreviewed, addCollege_level_unreviewed, updateCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import { getCollege_level_reviewed, addCollege_level_reviewed, updateCollege_level_reviewed } from "@/api/achievement/college_level_reviewed";
import { getSchool_level_unreviewed, addSchool_level_unreviewed, updateSchool_level_unreviewed } from "@/api/achievement/school_level_unreviewed";
import { getSchool_level_reviewed, addSchool_level_reviewed, updateSchool_level_reviewed } from "@/api/achievement/school_level_reviewed";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

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
  router.back();
}

/**
 * ✅ 手动流转归档推送（前端负责）：
 * 1) 院级未审核(college_level_unreviewed)
 *    - updateCollege_level_unreviewed：auditStatus 0 -> 1/2
 *    - addCollege_level_reviewed：归档到院级已审核
 *    - 若通过(2)：addSchool_level_unreviewed：推送到校级未审核（auditStatus=2 待校级审核）
 *
 * 2) 校级未审核(school_level_unreviewed)
 *    - updateSchool_level_unreviewed：auditStatus 2 -> 0/1
 *    - addSchool_level_reviewed：归档到校级已审核
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

  try {
    // ===== 院级审核 =====
    if (source.value === "college_level_unreviewed") {
      // 1) 更新院级未审核状态（0 -> 1/2）
      await updateCollege_level_unreviewed({
        ...baseForm,
        achievementId: id,
        reviewResult: next,
        reviewReason: collegeAuditReason,
        reviewedAt: now,
        auditBy: auditUser
      });

      // 2) 院级通过(2) -> 推送到校级未审核（待校级审核=2）
      if (next === "2") {
        await updateSchool_level_unreviewed({
          achievementId: id,
          schooiReviewResult: "2"
        });
      }

      proxy.$modal?.msgSuccess?.("院级审核成功");
      handleBack();
      return;
    }

    // 重新审核已经审核的院级记录
    if (source.value === "college_level_reviewed") {
      await updateCollege_level_reviewed({
        ...baseForm,
        achievementId: id,
        reviewResult: next,
        reviewReason: collegeAuditReason,
        reviewedAt: now,
        auditBy: auditUser
      });

      if (next === "2" && (baseForm?.schooiReviewResult == null || baseForm?.schooiReviewResult === "")) {
        await updateSchool_level_unreviewed({
          achievementId: id,
          schooiReviewResult: "2"
        });
      }

      proxy.$modal?.msgSuccess?.("院级审核成功");
      handleBack();
      return;
    }

    // ===== 校级审核 =====
    if (source.value === "school_level_unreviewed" || source.value === "school_level_reviewed") {
      // 1) 更新校级审核状态（2 -> 0/1）
      await updateSchool_level_unreviewed({
        ...baseForm,
        achievementId: id,
        schooiReviewResult: next,
        schoolReviewReason: schoolAuditReason,
        schoolReviewedAt: now,
        schoolAuditBy: auditUser
      });

      proxy.$modal?.msgSuccess?.("校级审核成功");
      handleBack();
      return;
    }

    proxy.$modal?.msgError?.("当前来源(source)不支持审核（请从未审核列表进入）");
  } catch (e) {
    proxy.$modal?.msgError?.("审核失败（可能已归档/已推送或接口限制）");
  }
}

function loadCurrent() {
  formKey.value = Date.now();
  auditInitialized.value = false;
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
  () => [route.query.id, route.query.source],
  () => {
    loadCurrent();
  }
);
</script>

<style scoped>
.review-page {
  padding: 0;
}
.audit-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.audit-label {
  margin-right: 4px;
}
.audit-reason {
  width: 260px;
}
</style>
