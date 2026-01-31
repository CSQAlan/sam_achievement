<template>
  <div class="app-container review-page">
    <BaseOutcomeDialog
        ref="dlgRef"
        :getFn="currentApi.getFn"
        :addFn="currentApi.addFn"
        :updateFn="currentApi.updateFn"
        :pageMode="true"
        :readOnly="true"
        :showSubmit="false"
        cancelText="返回"
        titleAdd="成果审核"
        titleEdit="成果审核"
        @cancel="handleBack"
        @ok="handleBack"
    >
      <!-- ✅ 外置审核工具条：下拉 + 提交审核（手动流转） -->
      <template #footer-left>
        <span style="margin-right: 8px;">审核后状态</span>

        <el-select
            v-model="selectedAuditStatus"
            placeholder="请选择"
            style="width: 220px; margin-right: 8px;"
        >
          <el-option
              v-for="opt in nextStatusOptions"
              :key="String(opt.value)"
              :label="opt.label"
              :value="String(opt.value)"
          />
        </el-select>

        <el-button type="primary" :disabled="!selectedAuditStatus" @click="submitAudit">
          提交审核
        </el-button>
      </template>
    </BaseOutcomeDialog>
  </div>
</template>

<script setup name="ReviewPage">
import { computed, onMounted, ref, getCurrentInstance, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseOutcomeDialog from "@/views/erp/BaseOutcomeDialog.vue";

// ====== 四套 API（你已有）======
import { getDept_unapproved, addDept_unapproved, updateDept_unapproved } from "@/api/erp/dept_unapproved";
import { getDept_approved, addDept_approved, updateDept_approved } from "@/api/erp/dept_approved";
import { getSdept_unapproved, addSdept_unapproved, updateSdept_unapproved } from "@/api/erp/Sdept_unapproved";
import { getSdept_approved, addSdept_approved, updateSdept_approved } from "@/api/erp/Sdept_approved";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

const dlgRef = ref(null);

// ✅ 两套字典（按你最新定义）
// 院级：dept_audit_status  0待院级审核 1院级驳回 2院级审核通过
// 校级：shool_audit_status 2待校级审核 1校级审核通过 0校级驳回
const { dept_audit_status, shool_audit_status } = proxy.useDict("dept_audit_status", "shool_audit_status");

// ✅ API 映射
const apiMap = {
  dept_unapproved: { getFn: getDept_unapproved, addFn: addDept_unapproved, updateFn: updateDept_unapproved },
  dept_approved: { getFn: getDept_approved, addFn: addDept_approved, updateFn: updateDept_approved },
  sdept_unapproved: { getFn: getSdept_unapproved, addFn: addSdept_unapproved, updateFn: updateSdept_unapproved },
  sdept_approved: { getFn: getSdept_approved, addFn: addSdept_approved, updateFn: updateSdept_approved }
};

// ✅ 来源（由列表页点击审阅传 query.source）
const source = computed(() => String(route.query.source || "dept_unapproved").toLowerCase());
const currentApi = computed(() => apiMap[source.value] || apiMap.dept_unapproved);

// ✅ 根据来源决定使用哪套字典
const currentAuditDict = computed(() => {
  if (source.value.startsWith("dept_")) return dept_audit_status?.value || [];
  if (source.value.startsWith("sdept_")) return shool_audit_status?.value || [];
  return [];
});

// ✅ 审核下拉可选项：
// - 院级未审核(dept_unapproved)：只能选 1(驳回) / 2(通过)
// - 校级未审核(sdept_unapproved)：只能选 0(驳回) / 1(通过)
const nextStatusOptions = computed(() => {
  const options = currentAuditDict.value || [];

  if (source.value === "dept_unapproved") {
    return options.filter((d) => ["1", "2"].includes(String(d.value)));
  }

  if (source.value === "sdept_unapproved") {
    return options.filter((d) => ["0", "1"].includes(String(d.value)));
  }

  return [];
});

const selectedAuditStatus = ref("");

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

  const preferValue = source.value === "dept_unapproved" ? "2" : "1";
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

function handleBack() {
  router.back();
}

/**
 * ✅ 手动流转归档推送（前端负责）：
 * 1) 院级未审核(dept_unapproved)
 *    - updateDept_unapproved：auditStatus 0 -> 1/2
 *    - addDept_approved：归档到院级已审核
 *    - 若通过(2)：addSdept_unapproved：推送到校级未审核（auditStatus=2 待校级审核）
 *
 * 2) 校级未审核(sdept_unapproved)
 *    - updateSdept_unapproved：auditStatus 2 -> 0/1
 *    - addSdept_approved：归档到校级已审核
 *
 * ✅ 关键修复：
 * - 归档到校级已审核时必须写 schoolAuditTime（你的 mapper 有 school_audit_time is not null 的过滤）
 */
async function submitAudit() {
  const baseForm = dlgRef.value?.getForm?.();
  const id = baseForm?.id ?? route.query.id;

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

  // ✅ 尽可能拿到当前登录用户（按你项目实际字段可能不同）
  const auditUser =
      proxy?.$store?.state?.user?.userName ||
      proxy?.$store?.state?.user?.nickName ||
      proxy?.$store?.state?.user?.name ||
      "";

  const now = new Date();

  try {
    // ===== 院级审核 =====
    if (source.value === "dept_unapproved") {
      // 1) 更新院级未审核状态（0 -> 1/2）
      await updateDept_unapproved({ ...baseForm, id, auditStatus: next });

      // 2) 归档到院级已审核（✅更稳：去掉 id 字段，而不是 id:null）
      const { id: _omit1, ...payload1 } = baseForm;
      await addDept_approved({ ...payload1, auditStatus: next });

      // 3) 院级通过(2) -> 推送到校级未审核（待校级审核=2）
      if (next === "2") {
        await addSdept_unapproved({ ...payload1, auditStatus: "2" });
      }

      proxy.$modal?.msgSuccess?.("院级审核成功");
      handleBack();
      return;
    }

    // ===== 校级审核 =====
    if (source.value === "sdept_unapproved") {
      // 1) 更新校级未审核状态（2 -> 0/1），✅补齐校级审核信息
      await updateSdept_unapproved({
        ...baseForm,
        id,
        auditStatus: next,
        schoolAuditTime: now,
        schoolAuditUser: auditUser,
        schoolAuditReason: baseForm.schoolAuditReason || ""
      });

      // 2) 归档到校级已审核（✅必须写 schoolAuditTime，否则 selectByAuditStatuses 会过滤）
      const { id: _omit2, ...payload2 } = baseForm;
      await addSdept_approved({
        ...payload2,
        auditStatus: next,
        schoolAuditTime: now,
        schoolAuditUser: auditUser,
        schoolAuditReason: payload2.schoolAuditReason || ""
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

onMounted(() => {
  const id = route.query.id;
  dlgRef.value?.open(id);
  setDefaultSelected();
});
</script>

<style scoped>
.review-page {
  padding: 0;
}
</style>
