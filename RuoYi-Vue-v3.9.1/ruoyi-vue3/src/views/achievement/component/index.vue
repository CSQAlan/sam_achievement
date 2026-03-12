<template>
  <div class="achievement-manage-root">
    <div v-show="!pageModeActive" class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="成果ID" prop="achievementId">
          <el-input
              v-model="queryParams.achievementId"
              placeholder="请输入成果ID"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="比赛" prop="track">
          <el-input
              v-model="queryParams.track"
              placeholder="请输入比赛"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="届次" prop="sessionId">
          <el-input
              v-model="queryParams.sessionId"
              placeholder="请输入届次"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="参赛选手" prop="contestant">
          <el-input
              v-model="queryParams.contestant"
              placeholder="请输入参赛选手"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="指导老师" prop="instructor">
          <el-input
              v-model="queryParams.instructor"
              placeholder="请输入指导老师"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
            <el-option
                v-for="dict in achievement_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作品名称" prop="name">
          <el-input
              v-model="queryParams.name"
              placeholder="请输入作品名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="queryParams.level" placeholder="请选择级别" clearable>
            <el-option
                v-for="dict in award_level_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
            <el-option
                v-for="dict in award_rank"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNo">
          <el-input
              v-model="queryParams.certificateNo"
              placeholder="请输入证书编号"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="组别" prop="groupId">
          <el-select v-model="queryParams.groupId" placeholder="请选择组别" clearable>
            <el-option
                v-for="dict in group_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker
              v-model="queryParams.awardTimeStart"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
          />
        </el-form-item>
        <el-form-item label="审核状态" prop="reviewStatus" v-if="showReviewStatusFilter">
          <el-select v-model="queryParams.reviewStatus" placeholder="请选择审核状态" clearable>
            <el-option
                v-for="dict in auditStatusFilterOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
              v-if="showAdd"
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="permAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              v-if="showEdit && canUseEditAction"
              type="success"
              plain
              icon="Edit"
              :disabled="single || !canEditSelected"
              @click="handleUpdate"
          >审核</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              v-if="showDelete"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="permRemove"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              v-if="showExport"
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="permExport"
          >导出</el-button>
        </el-col>
        <el-col v-if="canBatchReview" :span="3">
          <el-select
              v-model="batchReviewStatus"
              placeholder="请选择批量状态"
              clearable
              style="width: 100%;"
          >
            <el-option
                v-for="dict in auditStatusBatchOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-col>
        <el-col v-if="canBatchReview" :span="2">
          <el-button
              type="primary"
              plain
              icon="Edit"
              :disabled="multiple || batchReviewStatus === null || batchReviewStatus === undefined || batchReviewStatus === ''"
              @click="handleBatchReviewStatus"
              v-hasPermi="permEdit"
          >批量审核</el-button>
        </el-col>
        <el-col v-if="showBatchRejectReason" :span="6">
          <el-input
              v-model="batchRejectReason"
              type="textarea"
              :rows="1"
              maxlength="200"
              show-word-limit
              :placeholder="batchRejectReasonPlaceholder"
          />
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table ref="tableRef" v-loading="loading" :data="listData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="成果ID" align="center" prop="achievementId" />
        <el-table-column label="比赛" align="center" prop="competitionName">
          <template #default="scope">
            <span>{{ scope.row.competitionName || scope.row.competition_name || scope.row.track || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="届次" align="center" prop="sessionId" />
        <el-table-column label="参赛选手" align="center" prop="contestant" />
        <el-table-column label="指导老师" align="center" prop="instructor" />
        <el-table-column label="类别" align="center" prop="category">
          <template #default="scope">
            <dict-tag :options="achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="作品名称" align="center" prop="name" />
        <el-table-column label="级别" align="center" prop="level">
          <template #default="scope">
            <dict-tag :options="award_level_type" :value="scope.row.level" />
          </template>
        </el-table-column>
        <el-table-column label="获奖等级" align="center" prop="grade">
          <template #default="scope">
            <dict-tag :options="award_rank" :value="scope.row.grade" />
          </template>
        </el-table-column>
        <el-table-column label="证书编号" align="center" prop="certificateNo" />
        <el-table-column label="组别" align="center" prop="groupId" min-width="100">
          <template #default="scope">
            <dict-tag :options="group_type" :value="scope.row.groupId" />
          </template>
        </el-table-column>
        <el-table-column label="审核状态" align="center" prop="reviewStatus">
          <template #default="scope">
            <dict-tag :options="auditStatus" :value="scope.row.reviewStatus" />
          </template>
        </el-table-column>
        <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button v-if="canUseReviewAction" link type="primary" icon="View" @click="handleReview(scope.row)">详情</el-button>

            <el-button
                v-if="showEdit && canUseEditAction"
                link
                type="primary"
                icon="Edit"
                @click="handleRowUpdate(scope.row)"
                :disabled="!checkEditable(scope.row)"
            >审核</el-button>

            <el-button
                v-if="showDelete"
                link
                type="primary"
                icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="permRemove"
                :disabled="!checkEditable(scope.row)"
            >删除</el-button>
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

    </div>

    <AchievementForm
        v-if="pageModeActive"
        :key="pageModeKey"
        ref="achievementFormRef"
        :get-fn="getFn"
        :add-fn="addFn"
        :update-fn="updateFn"
        :page-mode="pageModeActive"
        :read-only="formReadOnly"
        :show-submit="formShowSubmit"
        :self-edit-scene="props.selfEditScene"
        cancel-text="返回"
        @ok="handleFormOk"
        @cancel="handleFormCancel"
    />
    <AchievementForm
        ref="achievementDialogRef"
        :get-fn="getFn"
        :add-fn="addFn"
        :update-fn="updateFn"
        :read-only="formReadOnly"
        :show-submit="formShowSubmit"
        :self-edit-scene="props.selfEditScene"
        cancel-text="返回"
        @ok="handleFormOk"
        @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, getCurrentInstance, nextTick, watch, onActivated } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useDict } from '@/utils/dict';
import useUserStore from '@/store/modules/user';
import auth from '@/plugins/auth';
import AchievementForm from '../component/AchievementForm.vue';
import { listManage, getManage, addManage, updateManage, delManage } from '@/api/achievement/manage';
import { batchUpdateReviewStatus } from '@/api/achievement/review_batch';

const props = defineProps({
  listFn: { type: Function, default: null },
  getFn: { type: Function, default: null },
  addFn: { type: Function, default: null },
  updateFn: { type: Function, default: null },
  delFn: { type: Function, default: null },
  exportUrl: { type: String, default: '' },
  permissionPrefix: { type: String, default: 'achievement:manage' },
  auditDict: { type: Array, default: null },
  showSearch: { type: Boolean, default: true },
  pageMode: { type: Boolean, default: false },
  reviewRoute: { type: String, default: '' },
  reviewSource: { type: String, default: '' },
  reviewPerm: { type: Array, default: null },
  showAdd: { type: Boolean, default: true },
  showEdit: { type: Boolean, default: true },
  showDelete: { type: Boolean, default: true },
  showExport: { type: Boolean, default: true },
  selfEditScene: { type: String, default: '' }
});

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const listFn = computed(() => props.listFn || listManage);
const getFn = computed(() => props.getFn || getManage);
const addFn = computed(() => props.addFn || addManage);
const updateFn = computed(() => props.updateFn || updateManage);
const delFn = computed(() => props.delFn || delManage);

const exportUrl = computed(() => props.exportUrl || 'achievement/manage/export');
const showSearch = computed(() => props.showSearch);
const reviewRoute = computed(() => {
  if (props.reviewRoute) return props.reviewRoute;
  const parentRecord = route.matched?.[route.matched.length - 2];
  const base = parentRecord?.path || '';
  if (!base) return '/achievement/reviewPage';
  return base.endsWith('/') ? `${base}reviewPage` : `${base}/reviewPage`;
});
const reviewSource = computed(() => (props.reviewSource || '').toLowerCase());

const permissionPrefix = computed(() => props.permissionPrefix || 'achievement:manage');
const permAdd = computed(() => [`${permissionPrefix.value}:add`]);
const permEdit = computed(() => [`${permissionPrefix.value}:edit`]);
const permRemove = computed(() => [`${permissionPrefix.value}:remove`]);
const permExport = computed(() => [`${permissionPrefix.value}:export`]);
const permReview = computed(() => {
  if (Array.isArray(props.reviewPerm) && props.reviewPerm.length > 0) return props.reviewPerm;
  return [`${permissionPrefix.value}:query`];
});
const isSelfEditPage = computed(() => props.selfEditScene === 'responsible' || props.selfEditScene === 'guided');
const canUseEditAction = computed(() => isSelfEditPage.value || auth.hasPermiOr(permEdit.value));
const canUseReviewAction = computed(() => isSelfEditPage.value || auth.hasPermiOr(permReview.value));
const canBatchReview = computed(() => !!reviewSource.value);
const isUnreviewedPage = computed(() => {
  return reviewSource.value === 'college_level_unreviewed' || reviewSource.value === 'school_level_unreviewed';
});
const showReviewStatusFilter = computed(() => !isUnreviewedPage.value);

const { achievement_category, group_type, award_rank, award_level_type, college_audit_status, school_audit_status } = useDict(
    'achievement_category',
    'group_type',
    'award_rank',
    'award_level_type',
    'college_audit_status',
    'school_audit_status'
);

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
  awardTime: null,
  reviewStatus: null,
  awardTimeStart: null,
  awardTimeEnd: null
});

const listData = ref([]);
const loading = ref(false);
const total = ref(0);
const single = ref(true);
const multiple = ref(true);
const ids = ref([]);
const selectedRows = ref([]);
const tableRef = ref(null);
const batchReviewStatus = ref(null);
const batchRejectReason = ref('');
const achievementFormRef = ref(null);
const achievementDialogRef = ref(null);
const pageModeActive = ref(false);
const pageModeKey = ref(0);
const formReadOnly = ref(false);
const formShowSubmit = ref(true);
const isStudentUser = computed(() => (userStore.roles || []).map(role => String(role).replace(/^ROLE_/, '').toLowerCase()).includes('student'));
const canEditSelected = computed(() => selectedRows.value.length === 1 && checkEditable(selectedRows.value[0]));
const isCollegeBatchReject = computed(() => reviewSource.value.startsWith('college') && String(batchReviewStatus.value) === '1');
const isSchoolBatchReject = computed(() => reviewSource.value.startsWith('school') && String(batchReviewStatus.value) === '0');
const showBatchRejectReason = computed(() => isCollegeBatchReject.value || isSchoolBatchReject.value);
const batchRejectReasonPlaceholder = computed(() => isCollegeBatchReject.value ? '请输入院级批量驳回原因' : '请输入校级批量驳回原因');

function hashString(input) {
  let hash = 0;
  for (let i = 0; i < input.length; i++) {
    hash = ((hash << 5) - hash) + input.charCodeAt(i);
    hash |= 0;
  }
  return String(hash >>> 0);
}

function buildPageKey() {
  const payload = {
    source: reviewSource.value,
    pageNum: queryParams.pageNum,
    pageSize: queryParams.pageSize,
    filters: { ...queryParams }
  };
  return hashString(JSON.stringify(payload));
}

const auditStatus = computed(() => {
  if (props.auditDict && Array.isArray(props.auditDict)) return props.auditDict;
  if (reviewSource.value.startsWith('college')) return college_audit_status.value;
  if (reviewSource.value.startsWith('school')) return school_audit_status.value;
  return (college_audit_status.value && college_audit_status.value.length)
      ? college_audit_status.value
      : school_audit_status.value;
});
const auditStatusFilterOptions = computed(() => {
  const options = auditStatus.value || [];
  if (reviewSource.value === 'college_level_reviewed' || reviewSource.value === 'college_level_unreviewed') {
    return options.filter((d) => ['1', '2'].includes(String(d.value)));
  }
  if (reviewSource.value === 'school_level_reviewed' || reviewSource.value === 'school_level_unreviewed') {
    return options.filter((d) => ['0', '1'].includes(String(d.value)));
  }
  return options;
});
const auditStatusBatchOptions = computed(() => {
  if (reviewSource.value.startsWith('college')) return college_audit_status.value || [];
  if (reviewSource.value.startsWith('school')) return school_audit_status.value || [];
  return auditStatus.value || [];
});

function normalizeReviewStatusBySource() {
  const source = reviewSource.value;
  const current = queryParams.reviewStatus;
  if (current === null || current === undefined || current === '') return;

  const val = String(current);
  if (source === 'college_level_unreviewed' && val !== '0') {
    queryParams.reviewStatus = null;
    return;
  }
  if (source === 'college_level_reviewed' && val !== '1' && val !== '2') {
    queryParams.reviewStatus = null;
    return;
  }
  if (source === 'school_level_unreviewed' && val !== '2') {
    queryParams.reviewStatus = null;
    return;
  }
  if (source === 'school_level_reviewed' && val !== '0' && val !== '1') {
    queryParams.reviewStatus = null;
  }
}

function getList() {
  normalizeReviewStatusBySource();
  loading.value = true;
  listFn.value(queryParams).then(response => {
    const rows = (response.rows || []).map((row) => normalizeRowStatus(row));
    let filtered = filterRowsBySource(rows);
    const hasClientStatusFilter = queryParams.reviewStatus !== null && queryParams.reviewStatus !== '' && queryParams.reviewStatus !== undefined;
    if (hasClientStatusFilter) {
      filtered = filtered.filter((row) => String(row.reviewStatus) === String(queryParams.reviewStatus));
    }
    listData.value = filtered;
    total.value = hasClientStatusFilter ? filtered.length : (Number.isFinite(Number(response.total)) ? Number(response.total) : filtered.length);
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function normalizeRowStatus(row) {
  // 1. 获取后端可能返回的各种字段名
  const reviewResult = row.reviewResult ?? row.review_result ?? row.reviewStatus;
  const schoolResult = row.schooiReviewResult ?? row.schoolReviewResult ?? row.schooi_review_result ?? row.school_review_result;

  row.reviewResult = reviewResult;
  row.schooiReviewResult = schoolResult;

  // 2. 根据场景决定表格里的“审核状态(reviewStatus)”列到底显示哪个值
  if (reviewSource.value.startsWith('college')) {
    // 场景A：院级审核员 -> 显示院级结果
    row.reviewStatus = reviewResult ?? 0;
  } else if (reviewSource.value.startsWith('school')) {
    // 场景B：校级审核员 -> 显示校级结果
    row.reviewStatus = schoolResult ?? 2;
  } else {
    row.reviewStatus = reviewResult;
  }

  return row;
}

function filterRowsBySource(rows) {
  const source = reviewSource.value;
  if (source === 'college_level_unreviewed') {
    return rows.filter(r => r.reviewResult === null || r.reviewResult === undefined || String(r.reviewResult) === '0');
  }
  if (source === 'college_level_reviewed') {
    return rows.filter(r => String(r.reviewResult) === '1' || String(r.reviewResult) === '2');
  }
  if (source === 'school_level_unreviewed') {
    return rows.filter(r => String(r.reviewResult) === '2' && String(r.schooiReviewResult) === '2');
  }
  if (source === 'school_level_reviewed') {
    return rows.filter(r => String(r.schooiReviewResult) === '0' || String(r.schooiReviewResult) === '1');
  }
  return rows;
}

const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

const resetQuery = () => {
  Object.keys(queryParams).forEach(key => {
    queryParams[key] = null;
  });
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  getList();
};

function handleSelectionChange(selection) {
  selectedRows.value = selection;
  ids.value = selection.map(i => i.achievementId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function clearSelectionState() {
  selectedRows.value = [];
  ids.value = [];
  single.value = true;
  multiple.value = true;
  tableRef.value?.clearSelection?.();
}

function handleAdd() {
  openDialog();
}

function handleUpdate() {
  const _achievementId = ids.value[0];
  if (!canEditSelected.value) {
    proxy.$modal?.msgWarning?.('当前成果仅在待审核或驳回时允许本人修改');
    return;
  }
  if (_achievementId) openDialog(_achievementId, { readOnly: false });
}

/** 行内审核按钮操作 */
function handleRowUpdate(row) {
  const _achievementId = row?.achievementId;
  if (!_achievementId) return;

  // 【核心审核】：如果是审核模式，可能需要跳转去审核修改（保留同学逻辑）
  if (reviewSource.value) {
    openReviewPage(_achievementId, 'edit');
  } else {
    // 否则是普通用户，直接打开本地弹窗进行编辑
    openDialog(_achievementId, { readOnly: false });
  }
}

function handleDelete(row) {
  const _achievementIds = row?.achievementId || ids.value;
  if (!_achievementIds || (_achievementIds.length !== undefined && _achievementIds.length === 0)) return;
  proxy.$modal
      .confirm(`Confirm delete achievement id(s): "${_achievementIds}"?`)
      .then(() => delFn.value(_achievementIds))
      .then(() => {
        proxy.$modal.msgSuccess('Delete successful');
        getList();
      })
      .catch(() => {});
}

function handleExport() {
  const parts = exportUrl.value.split('/').filter(Boolean);
  const base = parts.length >= 2 ? parts[parts.length - 2] : 'export';
  proxy.download(exportUrl.value, { ...queryParams }, `${base}_${new Date().getTime()}.xlsx`);
}

function handleBatchReviewStatus() {
  if (!canBatchReview.value) return;
  if (!ids.value.length) {
    proxy.$modal?.msgWarning?.('请先选择要批量审核的数据');
    return;
  }
  if (batchReviewStatus.value === null || batchReviewStatus.value === undefined || batchReviewStatus.value === '') {
    proxy.$modal?.msgWarning?.('请选择批量审核状态');
    return;
  }
  const reviewStatus = Number(batchReviewStatus.value);
  if (Number.isNaN(reviewStatus)) {
    proxy.$modal?.msgWarning?.('审核状态无效');
    return;
  }
  if (showBatchRejectReason.value && !batchRejectReason.value.trim()) {
    proxy.$modal?.msgWarning?.(isCollegeBatchReject.value ? '请输入院级批量驳回原因' : '请输入校级批量驳回原因');
    return;
  }

  // 执行批量更新
  proxy.$modal
      .confirm(`确认将选中的 ${ids.value.length} 条数据批量改为当前状态吗？`)
      .then(() => batchUpdateReviewStatus(reviewSource.value, {
        achievementIds: ids.value,
        reviewStatus,
        rejectReason: showBatchRejectReason.value ? batchRejectReason.value.trim() : ''
      }))
      .then(() => {
        proxy.$modal?.msgSuccess?.('批量审核成功');
        batchReviewStatus.value = null;
        batchRejectReason.value = '';
        clearSelectionState();
        getList();
      })
      .catch(() => {});
}

watch(showBatchRejectReason, (visible) => {
  if (!visible) {
    batchRejectReason.value = '';
  }
});

function handleReview(row) {
  const id = row?.achievementId;
  if (!id) return;
  if (reviewSource.value) {
    openReviewPage(id, 'view');
  } else {
    openDialog(id, { readOnly: true });
  }
}

function refreshFromRoute() {
  pageModeActive.value = false;
  formReadOnly.value = false;
  formShowSubmit.value = true;
  normalizeReviewStatusBySource();
  nextTick(() => {
    getList();
  });
}

watch(() => route.fullPath, () => {
  refreshFromRoute();
});

onActivated(() => {
  refreshFromRoute();
});

function openPageForm(id, options = {}) {
  formReadOnly.value = !!options.readOnly;
  formShowSubmit.value = !formReadOnly.value;
  pageModeActive.value = true;
  pageModeKey.value = Date.now();
  nextTick(() => {
    achievementFormRef.value?.open(id);
  });
}

function openDialog(id, options = {}) {
  formReadOnly.value = !!options.readOnly;
  formShowSubmit.value = !formReadOnly.value;
  nextTick(() => {
    achievementDialogRef.value?.open(id);
  });
}

function openReviewPage(id, mode) {
  const ids = (listData.value || [])
      .map((row) => row?.achievementId)
      .filter((val) => val !== null && val !== undefined && val !== '');
  const uniqueIds = Array.from(new Set(ids))
      .map((val) => Number(val))
      .filter((val) => !Number.isNaN(val))
      .sort((a, b) => a - b);
  const pageIds = uniqueIds.join(',');
  const pageKey = buildPageKey();
  if (pageKey) {
    try {
      sessionStorage.setItem(`review_page_${pageKey}`, pageIds);
    } catch (e) {
      // ignore storage errors
    }
  }
  const query = {
    id,
    source: reviewSource.value,
    mode,
    pageKey,
    pageIds
  };
  if (route?.name) query.fromName = String(route.name);
  if (route?.path) query.fromPath = String(route.path);
  router.push({
    path: reviewRoute.value,
    query
  });
}

function handleFormOk() {
  pageModeActive.value = false;
  getList();
}

function handleFormCancel() {
  pageModeActive.value = false;
}

/**
 * 核心逻辑：检查是否可以修改
 */
function checkEditable(row) {
  if (reviewSource.value) return true;
  // 防空判断
  if (!row) return false;
  // 获取状态值
  const collegeStatus = row.reviewResult == null ? '0' : String(row.reviewResult);
  const schoolStatus = row.schooiReviewResult == null ? '' : String(row.schooiReviewResult);

  // 学生“我负责的成果” / 教师“我指导的成果”
  if (props.selfEditScene === 'responsible' || props.selfEditScene === 'guided') {
    if (collegeStatus === '0' || collegeStatus === '1') {
      return true;
    }
    if (collegeStatus === '2' && (schoolStatus === '' || schoolStatus === '2' || schoolStatus === '0')) {
      return true;
    }
    return false;
  }
  if (isStudentUser.value) {
    return collegeStatus === '1' || schoolStatus === '0';
  }
  if (schoolStatus === '0') {
    return true;
  }
  if (collegeStatus === '2' ) {
    return false;
  }
  return true;
}

getList();
</script>

<script>
export default {
  name: 'AchievementManageIndex'
}
</script>

