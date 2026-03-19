<template>
  <div class="achievement-manage-root">
    <div v-show="!pageModeActive" class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" v-show="showSearch" label-width="68px" class="achievement-search-form">
        <el-row :gutter="10" class="search-row search-row-primary">

          <el-col :span="5">
            <el-form-item label="比赛" prop="track" class="search-item" label-width="40px">
              <el-input
                  v-model="queryParams.track"
                  placeholder="请输入比赛"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="届次" prop="sessionId" class="search-item" label-width="40px">
              <el-input
                  v-model="queryParams.sessionId"
                  placeholder="请输入届次"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>

          <el-col :span="4">
            <el-form-item label="证书编号" prop="certificateNo" class="search-item">
              <el-input
                  v-model="queryParams.certificateNo"
                  placeholder="请输入证书编号"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="9" class="search-action-col">
            <el-form-item class="search-item search-action-item">
              <div class="search-action-row">
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                <el-button link type="primary" @click="toggleAdvancedSearch">
                  {{ advancedSearchExpanded ? '收起筛选' : '展开筛选' }}
                </el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-show="advancedSearchExpanded" :gutter="16" class="search-row search-row-advanced">
          <el-col :span="4">
            <el-form-item label="成果编号" prop="achievementId" class="search-item">
              <el-input
                  v-model="queryParams.achievementId"
                  placeholder="请输入成果编号"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="类别" prop="category" class="search-item">
              <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
                <el-option
                    v-for="dict in achievement_category"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="参赛选手" prop="contestant" class="search-item">
              <el-input
                  v-model="queryParams.contestant"
                  placeholder="请输入参赛选手"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="指导老师" prop="instructor" class="search-item">
              <el-input
                  v-model="queryParams.instructor"
                  placeholder="请输入指导老师"
                  clearable
                  @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="级别" prop="level" class="search-item">
              <el-select v-model="queryParams.level" placeholder="请选择级别" clearable>
                <el-option
                    v-for="dict in award_level_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="获奖等级" prop="grade" class="search-item">
              <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
                <el-option
                    v-for="dict in award_rank"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="4">
            <el-form-item label="组别" prop="groupId" class="search-item">
              <el-select v-model="queryParams.groupId" placeholder="请选择组别" clearable>
                <el-option
                    v-for="dict in group_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="获奖时间" prop="awardTime" class="search-item">
              <el-date-picker
                  v-model="queryParams.awardTimeStart"
                  type="daterange"
                  value-format="YYYY-MM-DD"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4" v-if="showReviewStatusFilter">
            <el-form-item label="审核状态" prop="reviewStatus" class="search-item">
              <el-select v-model="queryParams.reviewStatus" placeholder="请选择审核状态" clearable>
                <el-option
                    v-for="dict in auditStatusFilterOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

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
          >修改</el-button>
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
        <el-col v-if="canBatchReview" :span="2">
          <el-button
              type="success"
              plain
              :disabled="!listData.length"
              @click="handleSelectCurrentPage"
          >
            {{ allCurrentPageSelected ? '取消本页' : '全选本页' }}
          </el-button>
        </el-col>

        <el-col v-if="canBatchReview" :span="2">
          <el-button
              type="warning"
              plain
              :loading="selectAllLoading"
              @click="handleSelectAllResults"
          >
            {{ allResultsSelected ? '取消全选' : '全选全部' }}
          </el-button>
        </el-col>

        <el-col v-if="canBatchReview && allResultsSelected" :span="3">
          <el-tag type="warning">
            已选全部 {{ allResultsCount }} 条
          </el-tag>
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
          >
            批量审核
          </el-button>
        </el-col>

        <el-col v-if="showBatchRejectReason" :span="6">
          <div class="batch-reason-group">
            <el-select
                v-model="batchRejectReason"
                multiple
                filterable
                clearable
                :placeholder="batchRejectReasonPlaceholder"
                style="width: 100%;"
            >
              <el-option
                  v-for="opt in batchRejectReasonOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
              />
            </el-select>
            <el-input
                v-model="batchRejectReasonCustom"
                clearable
                :placeholder="batchRejectReasonCustomPlaceholder"
                style="margin-top: 8px;"
            />
          </div>
        </el-col>
      </el-row>

      <el-table ref="tableRef" v-loading="loading" :data="listData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="成果编号" width="80" align="center" prop="achievementId" />
        <el-table-column label="比赛" width="120" align="center" prop="competitionName">
          <template #default="scope">
            <span>{{ scope.row.competitionName || scope.row.competition_name || scope.row.track || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="届次" width="50" align="center" prop="sessionId" />
        <el-table-column label="证书编号" width="120" align="center" prop="certificateNo" />
        <el-table-column label="获奖等级" align="center" prop="grade" width="80">
          <template #default="scope">
            <dict-tag :options="award_rank" :value="scope.row.grade" />
          </template>
        </el-table-column>
        <el-table-column label="参赛选手" align="center" prop="contestant" width="120">
          <template #default="scope">
            <div class="ellipsis-cell" :title="scope.row.contestant || '-'">
              {{ scope.row.contestant || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="指导老师" align="center" prop="instructor" width="120">
          <template #default="scope">
            <div class="ellipsis-cell" :title="scope.row.instructor || '-'">
              {{ scope.row.instructor || '-' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="级别" width="120" align="center" prop="level">
          <template #default="scope">
            <dict-tag :options="award_level_type" :value="scope.row.level" />
          </template>
        </el-table-column>

        <el-table-column label="审核状态" width="130"  align="center" prop="reviewStatus">
          <template #default="scope">
            <div style="display: flex; align-items: center; justify-content: center;">
              <template v-if="!reviewSource">
                <el-tag v-if="scope.row.reviewResult === null || scope.row.reviewResult === undefined || String(scope.row.reviewResult) === '0'" type="primary">待审核</el-tag>
                <el-tag v-else-if="String(scope.row.reviewResult) === '1'" type="danger">院级驳回</el-tag>
                <template v-else-if="String(scope.row.reviewResult) === '2'">
                  <el-tag v-if="scope.row.schooiReviewResult === null || scope.row.schooiReviewResult === undefined || String(scope.row.schooiReviewResult) === '2'" type="warning">待校级审核</el-tag>
                  <el-tag v-else-if="String(scope.row.schooiReviewResult) === '0'" type="danger">校级驳回</el-tag>
                  <el-tag v-else-if="String(scope.row.schooiReviewResult) === '1'" type="success">校级审核通过</el-tag>
                </template>
              </template>
              <dict-tag v-else :options="auditStatus" :value="scope.row.reviewStatus" />

              <el-tooltip
                  v-if="String(scope.row.reviewResult) === '1' && scope.row.reviewReason"
                  :content="'院级驳回原因：' + (scope.row.reviewReasonDisplay || scope.row.reviewReason)"
                  placement="top"
              >
                <el-icon style="margin-left: 5px; color: #F56C6C; cursor: pointer;"><Warning /></el-icon>
              </el-tooltip>
              <el-tooltip
                  v-if="String(scope.row.schooiReviewResult) === '0' && scope.row.schoolReviewReason"
                  :content="'校级驳回原因：' + (scope.row.schoolReviewReasonDisplay || scope.row.schoolReviewReason)"
                  placement="top"
              >
                <el-icon style="margin-left: 5px; color: #F56C6C; cursor: pointer;"><Warning /></el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="获奖时间" align="center" prop="awardTime" width="100">
          <template #default="scope">
            <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleReview(scope.row)" v-hasPermi="[...permReview, ...permQuery]">详情</el-button>

            <el-button
                v-if="showEdit && canUseEditAction"
                link
                type="primary"
                icon="Edit"
                @click="handleRowUpdate(scope.row)"
                v-hasPermi="permEdit"
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
        :source-mode="sourceMode"
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
        :source-mode="sourceMode"
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
import { Warning } from '@element-plus/icons-vue';
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
  sourceMode: { type: String, default: '' }
});

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const REJECT_REASON_SEPARATOR = '；';

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
const permQuery = computed(() => [`${permissionPrefix.value}:query`]);
const permReview = computed(() => [`${permissionPrefix.value}:review`]);
const canUseEditAction = computed(() => props.showEdit);
const canBatchReview = computed(() => !!reviewSource.value);
const isUnreviewedPage = computed(() => {
  return reviewSource.value === 'college_level_unreviewed' || reviewSource.value === 'school_level_unreviewed';
});
const showReviewStatusFilter = computed(() => !isUnreviewedPage.value);

const { achievement_category, group_type, award_rank, award_level_type, college_audit_status, school_audit_status, college_reason, school_reason } = useDict(
    'achievement_category',
    'group_type',
    'award_rank',
    'award_level_type',
    'college_audit_status',
    'school_audit_status',
    'college_reason',
    'school_reason'
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

const batchRejectReason = ref([]);
const batchRejectReasonCustom = ref('');
const achievementFormRef = ref(null);
const achievementDialogRef = ref(null);
const pageModeActive = ref(false);
const pageModeKey = ref(0);
const advancedSearchExpanded = ref(false);
const formReadOnly = ref(false);
const formShowSubmit = ref(true);
const isStudentUser = computed(() => (userStore.roles || []).map(role => String(role).replace(/^ROLE_/, '').toLowerCase()).includes('student'));
const canEditSelected = computed(() => selectedRows.value.length === 1 && checkEditable(selectedRows.value[0]));
const isCollegeBatchReject = computed(() => reviewSource.value.startsWith('college') && String(batchReviewStatus.value) === '1');
const isSchoolBatchReject = computed(() => reviewSource.value.startsWith('school') && String(batchReviewStatus.value) === '0');
const showBatchRejectReason = computed(() => isCollegeBatchReject.value || isSchoolBatchReject.value);
const batchRejectReasonPlaceholder = computed(() => isCollegeBatchReject.value ? '请选择院级批量驳回原因' : '请选择校级批量驳回原因');
const batchRejectReasonCustomPlaceholder = computed(() => isCollegeBatchReject.value ? '请输入其他院级批量驳回原因' : '请输入其他校级批量驳回原因');
const selectAllLoading = ref(false);
const allResultsSelected = ref(false);
const allResultsCount = ref(0);
const hasBatchSelection = computed(() => ids.value.length > 0);

function normalizeLooseText(value) {
  if (value === null || value === undefined) return '';
  const text = String(value).trim();
  if (!text) return '';
  const lower = text.toLowerCase();
  if (lower === 'null' || lower === 'undefined') return '';
  return text;
}

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

function formatRejectReasonText(values, customText = '') {
  const manualTexts = splitRejectReasonText(customText);
  return Array.from(new Set([...(Array.isArray(values) ? values : [])
      .map((item) => normalizeLooseText(item))
      .filter(Boolean), ...manualTexts]))
      .join(REJECT_REASON_SEPARATOR);
}

const collegeRejectReasonOptions = computed(() => normalizeRejectReasonOptions(college_reason.value || []));
const schoolRejectReasonOptions = computed(() => normalizeRejectReasonOptions(school_reason.value || []));

const baseBatchRejectReasonOptions = computed(() => (
    reviewSource.value.startsWith('college') ? collegeRejectReasonOptions.value : schoolRejectReasonOptions.value
));

function formatRejectReasonDisplayText(value, options = []) {
  return Array.from(new Set(splitRejectReasonText(value)
      .map((item) => {
        const matched = findRejectReasonOption(options, item);
        return matched ? matched.label : item;
      })
      .filter(Boolean)))
      .join(REJECT_REASON_SEPARATOR);
}

const batchRejectReasonOptions = computed(() => baseBatchRejectReasonOptions.value);

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
  if (reviewSource.value === 'college_level_unreviewed') {
    return (college_audit_status.value || []).filter((d) => ['1', '2'].includes(String(d.value)));
  }
  if (reviewSource.value === 'college_level_reviewed') {
    return college_audit_status.value || [];
  }
  if (reviewSource.value === 'school_level_unreviewed' || reviewSource.value === 'school_level_reviewed') {
    return (school_audit_status.value || []).filter((d) => ['0', '1'].includes(String(d.value)));
  }
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

function buildListParams() {
  normalizeReviewStatusBySource();

  const finalParams = { ...queryParams };

  if (props.sourceMode === 'guided') {
    finalParams.firstInstructorId = userStore.name;
  } else if (props.sourceMode === 'participated') {
    finalParams.anyInstructorId = userStore.name;
  }

  return finalParams;
}

function applyRoutePageState() {
  const pageNum = Number(route.query.pageNum);
  const pageSize = Number(route.query.pageSize);

  if (Number.isFinite(pageNum) && pageNum > 0) {
    queryParams.pageNum = pageNum;
  }

  if (Number.isFinite(pageSize) && pageSize > 0) {
    queryParams.pageSize = pageSize;
  }
}

function getList() {
  loading.value = true;
  const finalParams = buildListParams();

  listFn.value(finalParams).then(response => {
    const rows = (response.rows || []).map((row) => normalizeRowStatus(row));
    let filtered = filterRowsBySource(rows);
    const hasClientStatusFilter = queryParams.reviewStatus !== null && queryParams.reviewStatus !== '' && queryParams.reviewStatus !== undefined;
    if (hasClientStatusFilter) {
      filtered = filtered.filter((row) => String(row.reviewStatus) === String(queryParams.reviewStatus));
    }
    listData.value = filtered;
    total.value = hasClientStatusFilter ? filtered.length : (Number.isFinite(Number(response.total)) ? Number(response.total) : filtered.length);
    if (!allResultsSelected.value) {
      tableRef.value?.clearSelection?.();
      selectedRows.value = [];
      ids.value = [];
      single.value = true;
      multiple.value = true;
    }
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
  row.reviewReasonDisplay = formatRejectReasonDisplayText(row.reviewReason, collegeRejectReasonOptions.value);
  row.schoolReviewReasonDisplay = formatRejectReasonDisplayText(row.schoolReviewReason, schoolRejectReasonOptions.value);

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

  // 3. 提取参赛选手和指导老师名称用于显示
  if (row.samAchievementParticipantList && Array.isArray(row.samAchievementParticipantList)) {
    row.contestant = row.samAchievementParticipantList
        .sort((a, b) => (a.orderNo || 0) - (b.orderNo || 0))
        .map(p => {
          const name = p.studentName || '-';
          const no = p.studentId || p.studentNo || '-';
          return `${name}(${no})`;
        })
        .join('，');
  }

  if (row.samAchievementAdvisorList && Array.isArray(row.samAchievementAdvisorList)) {
    row.instructor = row.samAchievementAdvisorList
        .sort((a, b) => (a.orderNo || 0) - (b.orderNo || 0))
        .map(a => {
          const name = a.teacherName || '-';
          const no = a.teacherId || a.teacherNo || '-';
          return `${name}(${no})`;
        })
        .join('，');
  }

  return row;
}

watch(
    [collegeRejectReasonOptions, schoolRejectReasonOptions],
    () => {
      if (!listData.value?.length) return;
      listData.value = listData.value.map((row) => normalizeRowStatus({ ...row }));
    },
    { deep: true }
);

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
  clearSelectionState();
  queryParams.pageNum = 1;
  getList();
};

const resetQuery = () => {
  clearSelectionState();
  Object.keys(queryParams).forEach(key => {
    queryParams[key] = null;
  });
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  getList();
};

function toggleAdvancedSearch() {
  advancedSearchExpanded.value = !advancedSearchExpanded.value;
}

function handleSelectionChange(selection) {
  // 只要用户手动勾选表格，就退出“全选全部”模式
  if (allResultsSelected.value) {
    allResultsSelected.value = false;
    allResultsCount.value = 0;
  }

  selectedRows.value = selection;
  ids.value = selection.map(i => i.achievementId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleSelectCurrentPage() {
  const rows = listData.value || [];
  if (!rows.length) {
    proxy.$modal?.msgWarning?.('当前页没有可选成果');
    return;
  }

  allResultsSelected.value = false;
  allResultsCount.value = 0;

  nextTick(() => {
    if (!tableRef.value) return;

    if (allCurrentPageSelected.value) {
      rows.forEach(row => {
        tableRef.value.toggleRowSelection(row, false);
      });
      return;
    }

    tableRef.value.clearSelection();
    rows.forEach(row => {
      tableRef.value.toggleRowSelection(row, true);
    });
  });
}
function normalizeIdList(values) {
  return Array.from(new Set((values || [])
      .map((value) => value === null || value === undefined ? '' : String(value).trim())
      .filter(Boolean)));
}

async function fetchReviewNavigationSnapshot(options = {}) {
  const pageSize = Math.max(Number(options.pageSize || queryParams.pageSize) || 10, 1);
  let pageNum = 1;
  const baseParams = buildListParams();
  const pageGroups = [];

  while (true) {
    const response = await listFn.value({
      ...baseParams,
      pageNum,
      pageSize
    });

    const rows = (response.rows || []).map(row => normalizeRowStatus(row));
    let filtered = filterRowsBySource(rows);

    const hasClientStatusFilter =
        queryParams.reviewStatus !== null &&
        queryParams.reviewStatus !== '' &&
        queryParams.reviewStatus !== undefined;

    if (hasClientStatusFilter) {
      filtered = filtered.filter(
          row => String(row.reviewStatus) === String(queryParams.reviewStatus)
      );
    }

    pageGroups.push(normalizeIdList(filtered.map(row => row.achievementId)));

    if (rows.length < pageSize) break;

    const totalCount = Number(response.total || 0);
    if (totalCount > 0 && pageNum * pageSize >= totalCount) break;

    pageNum += 1;
  }

  return {
    pageGroups,
    allIds: normalizeIdList(pageGroups.flat())
  };
}

async function fetchAllResultIds() {
  const snapshot = await fetchReviewNavigationSnapshot({ pageSize: 500 });
  return snapshot.allIds;
}

async function handleSelectAllResults() {
  if (!canBatchReview.value) return;

  if (allResultsSelected.value) {
    clearSelectionState();
    return;
  }

  selectAllLoading.value = true;
  try {
    const allIds = await fetchAllResultIds();

    ids.value = allIds;
    selectedRows.value = [];
    single.value = true;
    multiple.value = allIds.length === 0;
    allResultsSelected.value = true;
    allResultsCount.value = allIds.length;

    tableRef.value?.clearSelection?.();

    proxy.$modal?.msgSuccess?.(`已选中当前筛选条件下全部 ${allIds.length} 条成果`);
  } catch (e) {
    proxy.$modal?.msgError?.('全选全部失败，请稍后重试');
  } finally {
    selectAllLoading.value = false;
  }
}

function clearSelectionState() {
  selectedRows.value = [];
  ids.value = [];
  single.value = true;
  multiple.value = true;
  allResultsSelected.value = false;
  allResultsCount.value = 0;
  tableRef.value?.clearSelection?.();
}
function handleAdd() {
  openDialog();
}

async function handleUpdate() {
  const _achievementId = ids.value[0];
  if (!canEditSelected.value) {
    proxy.$modal?.msgWarning?.('当前成果仅在待审核或驳回时允许本人修改');
    return;
  }
  if (!_achievementId) return;
  if (reviewSource.value) {
    await openReviewPage(_achievementId, 'edit');
    return;
  }
  openDialog(_achievementId, { readOnly: false });
}

/** 行内审核按钮操作 */
async function handleRowUpdate(row) {
  const _achievementId = row?.achievementId;
  if (!_achievementId) return;

  if (reviewSource.value) {
    await openReviewPage(_achievementId, 'edit');
  } else {
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
  const rejectReasonText = showBatchRejectReason.value ? formatRejectReasonText(batchRejectReason.value, batchRejectReasonCustom.value) : '';

  if (showBatchRejectReason.value && !rejectReasonText) {
    proxy.$modal?.msgWarning?.(isCollegeBatchReject.value ? '请输入院级批量驳回原因' : '请输入校级批量驳回原因');
    return;
  }

  // 执行批量更新
  proxy.$modal
      .confirm(`确认将选中的 ${ids.value.length} 条数据批量改为当前状态吗？`)
      .then(() => batchUpdateReviewStatus(reviewSource.value, {
        achievementIds: ids.value,
        reviewStatus,
        rejectReason: rejectReasonText
      }))
      .then(() => {
        proxy.$modal?.msgSuccess?.('批量审核成功');
        batchReviewStatus.value = null;
        batchRejectReason.value = [];
        batchRejectReasonCustom.value = '';
        clearSelectionState();
        getList();
      })
      .catch(() => {});
}

watch(showBatchRejectReason, (visible) => {
  if (!visible) {
    batchRejectReason.value = [];
    batchRejectReasonCustom.value = '';
  }
});

async function handleReview(row) {
  const id = row?.achievementId;
  if (!id) return;
  if (reviewSource.value) {
    await openReviewPage(id, 'view');
  } else {
    openDialog(id, { readOnly: true });
  }
}

function refreshFromRoute() {
  pageModeActive.value = false;
  formReadOnly.value = false;
  formShowSubmit.value = true;
  normalizeReviewStatusBySource();
  applyRoutePageState();
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

async function openReviewPage(id, mode) {
  try {
    const currentPageIds = normalizeIdList((listData.value || []).map((row) => row?.achievementId));
    const snapshot = await fetchReviewNavigationSnapshot();
    const allIds = snapshot.allIds || [];
    const pageGroups = snapshot.pageGroups || [];
    const normalizedId = String(id);
    const pageIndex = pageGroups.findIndex((group) => group.some((item) => String(item) === normalizedId));
    const pageKey = buildPageKey();
    if (pageKey) {
      try {
        sessionStorage.setItem(`review_page_${pageKey}`, JSON.stringify(allIds));
        sessionStorage.setItem(`review_page_groups_${pageKey}`, JSON.stringify(pageGroups));
        sessionStorage.setItem(`review_current_page_${pageKey}`, JSON.stringify(currentPageIds));
      } catch (e) {
        // ignore storage errors
      }
    }
    const query = {
      id,
      source: reviewSource.value,
      mode,
      pageKey,
      currentPageIds: currentPageIds.join(','),
      pageIndex: pageIndex >= 0 ? pageIndex : Math.max(Number(queryParams.pageNum || 1) - 1, 0),
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    };
    if (!pageKey && allIds.length > 0) {
      query.pageIds = allIds.join(',');
    }
    if (route?.name) query.fromName = String(route.name);
    if (route?.path) query.fromPath = String(route.path);
    await router.push({
      path: reviewRoute.value,
      query
    });
  } catch (e) {
    proxy.$modal?.msgError?.('进入审核页失败，请稍后重试');
  }
}

function handleFormOk() {
  pageModeActive.value = false;
  getList();
}

function handleFormCancel() {
  pageModeActive.value = false;
}

function checkEditable(row) {
  if (reviewSource.value) return true;
  if (!row) return false;
  const collegeStatus = String(row.reviewResult);
  const schoolStatus = String(row.schooiReviewResult);
  if (schoolStatus === '0') {
    return true;
  }
  if (collegeStatus === '2' ) {
    return false;
  }
  return true;
}
getList();

const allCurrentPageSelected = computed(() => {
  const rows = listData.value || [];
  if (!rows.length) return false;

  return rows.every(row =>
      selectedRows.value.some(item => String(item.achievementId) === String(row.achievementId))
  );
});

</script>

<script>
export default {
  name: 'AchievementManageIndex'
}
</script>

<style scoped>
.achievement-search-form {
  margin-bottom: 18px;
}

.search-row + .search-row {
  margin-top: 2px;
}

.search-item {
  margin-bottom: 16px;
}

.search-action-item :deep(.el-form-item__content) {
  justify-content: flex-start;
}

.search-action-col {
  display: flex;
}

.search-action-row {
  min-height: 32px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.search-item :deep(.el-input),
.search-item :deep(.el-select),
.search-item :deep(.el-date-editor) {
  width: 100%;
}

.batch-reason-group {
  width: 100%;
}

.ellipsis-cell {
  display: block;
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

</style>

