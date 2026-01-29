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
      <!-- ✅ 审核工具条：下拉选择审核后状态 + 审核按钮 -->
      <template #footer-left>
        <span style="margin-right: 8px;">审核后状态</span>

        <el-select
            v-model="selectedAuditStatus"
            placeholder="请选择"
            style="width: 200px; margin-right: 8px;"
        >
          <el-option
              v-for="opt in nextStatusOptions"
              :key="String(opt.value)"
              :label="opt.label"
              :value="String(opt.value)"
          />
        </el-select>

        <el-button
            type="primary"
            :disabled="!selectedAuditStatus"
            @click="submitAudit"
        >
          提交审核
        </el-button>
      </template>
    </BaseOutcomeDialog>
  </div>
</template>

<script setup name="ReviewPage">
import { computed, onMounted, ref, getCurrentInstance } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseOutcomeDialog from "@/views/erp/BaseOutcomeDialog.vue";

import { getDept_unapproved, addDept_unapproved, updateDept_unapproved } from "@/api/erp/dept_unapproved";
import { getDept_approved, addDept_approved, updateDept_approved } from "@/api/erp/dept_approved";
import { getSdept_unapproved, addSdept_unapproved, updateSdept_unapproved } from "@/api/erp/Sdept_unapproved";
import { getSdept_approved, addSdept_approved, updateSdept_approved } from "@/api/erp/Sdept_approved";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const dlgRef = ref(null);

// ✅ 字典：业务审核状态
const { erp_audit_status } = proxy.useDict("erp_audit_status");

const apiMap = {
  dept_unapproved: { getFn: getDept_unapproved, addFn: addDept_unapproved, updateFn: updateDept_unapproved },
  dept_approved: { getFn: getDept_approved, addFn: addDept_approved, updateFn: updateDept_approved },
  sdept_unapproved: { getFn: getSdept_unapproved, addFn: addSdept_unapproved, updateFn: updateSdept_unapproved },
  sdept_approved: { getFn: getSdept_approved, addFn: addSdept_approved, updateFn: updateSdept_approved }
};

const source = computed(() => String(route.query.source || "dept_unapproved").toLowerCase());
const currentApi = computed(() => apiMap[source.value] || apiMap.dept_unapproved);

// ✅ 你业务规则：院级审核 => 只能选 3/4；校级审核 => 只能选 6/5
const allowedNextValues = computed(() => {
  if (source.value === "dept_unapproved") return ["3", "4"];      // 院级：通过/驳回
  if (source.value === "sdept_unapproved") return ["6", "5"];     // 校级：通过/驳回
  return [];
});

// ✅ 从字典中过滤出选项
const nextStatusOptions = computed(() => {
  const allow = new Set(allowedNextValues.value);
  return (erp_audit_status?.value || []).filter((d) => allow.has(String(d.value)));
});

const selectedAuditStatus = ref("");

function setDefaultSelected() {
  if (source.value === "dept_unapproved") selectedAuditStatus.value = "3";  // 默认院级通过
  if (source.value === "sdept_unapproved") selectedAuditStatus.value = "6"; // 默认校级通过
}

function handleBack() {
  router.back();
}

// ✅ 审核提交：包含“归档+推送”逻辑
async function submitAudit() {
  const form = dlgRef.value?.getForm?.();
  const id = form?.id ?? route.query.id;
  if (!id) return;
  if (!form || !form.category) {
    proxy.$modal?.msgError?.("成果信息未加载，无法提交审核");
    return;
  }

  const next = selectedAuditStatus.value; // dept: 3/4 , sdept: 6/5

  try {
    // 1) 更新当前待审核表状态（如果你的后端 update 已做归档/推送，可保留或删除）
    await currentApi.value.updateFn({ ...form, id, auditStatus: next });

    proxy.$modal?.msgSuccess?.("审核成功");
    handleBack();
    return;

    // 2) 按你的业务：院级审核 => 归档到院级已审核；通过才推校级待审
    if (source.value === "dept_unapproved") {
      // 2.1 归档到院级已审核（通过=3/驳回=4 都归档）
      await addDept_approved({ ...form, auditStatus: next });

      // 2.2 如果院级通过(3)，再推送到校级待审核（状态=2）
      if (next === "3") {
        await addSdept_unapproved({ ...form, auditStatus: "2" });
      }
    }

    // 3) 校级审核 => 归档到校级已审核（通过=6/驳回=5 都归档）
    if (source.value === "sdept_unapproved") {
      await addSdept_approved({ ...form, auditStatus: next });
    }

    proxy.$modal?.msgSuccess?.("审核成功");
    handleBack();
  } catch (e) {
    proxy.$modal?.msgError?.("审核失败（可能已归档/已推送或接口未支持）");
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
