<template>
  <manage-index
      :achievement-category="achievement_category"
      :group-type="group_type"
      :award-rank="award_rank"
      :award-level-type="award_level_type"
      :college-audit-status="college_audit_status"
      :show-search="true"
      :page-mode="pageMode"
      @query-table="getList"
      @handle-add="handleAdd"
      @handle-update="handleUpdate"
      @handle-delete="handleDelete"
      @handle-export="handleExport"
  />
</template>

<script setup>
// 引入字典数据
import { useDict } from "@/hooks/useDict";
import ManageIndex from "@/components/manage/Index.vue";

// 获取字典数据
const { achievement_category, group_type, award_rank, award_level_type, college_audit_status } = useDict(
    'achievement_category', 'group_type', 'award_rank', 'award_level_type', 'college_audit_status'
);

// 数据和状态管理
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
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
  awardTime: null
});

const listData = ref([]);
const loading = ref(false);
const total = ref(0);
const ids = ref([]);
const title = ref("");

// 控制表单弹窗的开关，默认是页面模式
const pageMode = ref(false);

// 获取列表数据
function getList() {
  loading.value = true;
  listCollege_level_reviewed(queryParams).then(response => {
    listData.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 搜索操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询条件
const resetQuery = () => {
  Object.keys(queryParams).forEach(key => {
    queryParams[key] = null;
  });
  getList();
};

// 选择操作（多选框）
function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.id);
}

// 新增操作
function handleAdd() {
  reset();
  title.value = "添加院级已审核";
  openFormDialog();  // 调用打开表单的函数
}

// 修改操作
function handleUpdate(row) {
  const _achievementId = row.achievementId || ids.value[0];
  getCollege_level_reviewed(_achievementId).then(response => {
    form.value = response.data;
    openFormDialog();  // 调用打开表单的函数
  });
}

// 删除操作
function handleDelete(row) {
  const _achievementIds = row.achievementId || ids.value;
  proxy.$modal
      .confirm(`是否确认删除成果编号为 "${_achievementIds}" 的数据项？`)
      .then(() => delCollege_level_reviewed(_achievementIds))
      .then(() => {
        getList();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {});
}

// 导出操作
function handleExport() {
  proxy.download('achievement/college_level_reviewed/export', { ...queryParams }, `college_level_reviewed_${new Date().getTime()}.xlsx`);
}

// 用于弹出表单对话框
function openFormDialog() {
  if (!pageMode.value) {
    // 如果是弹窗模式
    open.value = false; // 控制弹窗显示
  } else {
    // 如果是页面模式，直接显示表单
    open.value =true;
  }
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
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
    awardTime: null,
    year: null,
    ownerDepId: null,
    isReimburse: null,
    isSupplement: null,
    fee: null,
    reimbursementFee: null,
    reimbursementRatio: null,
    reimbursementItemId: null,
    reimbursementDate: null,
    itemIndex: null,
    qualityIndex: null,
    submittedAt: null,
    reviewedAt: null,
    schoolReviewedAt: null,
    reviewResult: null,
    schooiReviewResult: null,
    reviewReason: null,
    schoolReviewReason: null,
    auditBy: null,
    schoolAuditBy: null,
    createBy: null,
    updateBy: null,
    createTime: null,
    updateTime: null,
    delFlag: null,
    remark: null
  };
  samAchievementParticipantList.value = [];
  proxy.resetForm("college_level_reviewedRef");
}

// 获取列表数据
getList();
</script>
