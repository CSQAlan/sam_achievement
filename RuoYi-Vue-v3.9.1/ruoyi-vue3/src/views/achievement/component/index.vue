<template>
  <div class="achievement-manage-root">
  <div v-show="!pageModeActive" class="app-container">
    <!-- 鎼滅储琛ㄥ崟 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="鎴愭灉ID" prop="achievementId">
        <el-input
            v-model="queryParams.achievementId"
            placeholder="璇疯緭鍏ユ垚鏋淚D"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姣旇禌" prop="track">
        <el-input
            v-model="queryParams.track"
            placeholder="璇疯緭鍏ユ瘮璧?
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="灞婃" prop="sessionId">
        <el-input
            v-model="queryParams.sessionId"
            placeholder="璇疯緭鍏ュ眾娆?
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="鍙傝禌閫夋墜" prop="contestant">
        <el-input
            v-model="queryParams.contestant"
            placeholder="璇疯緭鍏ュ弬璧涢€夋墜"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="鎸囧鑰佸笀" prop="instructor">
        <el-input
            v-model="queryParams.instructor"
            placeholder="璇疯緭鍏ユ寚瀵艰€佸笀"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绫诲埆" prop="category">
        <el-select v-model="queryParams.category" placeholder="璇烽€夋嫨绫诲埆" clearable>
          <el-option
              v-for="dict in achievement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="浣滃搧鍚嶇О" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="璇疯緭鍏ヤ綔鍝佸悕绉?
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绾у埆" prop="level">
        <el-select v-model="queryParams.level" placeholder="璇烽€夋嫨绾у埆" clearable>
          <el-option
              v-for="dict in award_level_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="鑾峰绛夌骇" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="璇烽€夋嫨鑾峰绛夌骇" clearable>
          <el-option
              v-for="dict in award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="璇佷功缂栧彿" prop="certificateNo">
        <el-input
            v-model="queryParams.certificateNo"
            placeholder="璇疯緭鍏ヨ瘉涔︾紪鍙?
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="缁勫埆" prop="groupId">
        <el-select v-model="queryParams.groupId" placeholder="璇烽€夋嫨缁勫埆" clearable>
          <el-option
              v-for="dict in group_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="鑾峰鏃堕棿" prop="awardTime">
        <el-date-picker
            v-model="queryParams.awardTimeStart"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="鑷?
            start-placeholder="寮€濮嬫椂闂?
            end-placeholder="缁撴潫鏃堕棿"
        />
      </el-form-item>
      <el-form-item label="瀹℃牳鐘舵€? prop="reviewStatus">
        <el-select v-model="queryParams.reviewStatus" placeholder="璇烽€夋嫨瀹℃牳鐘舵€? clearable>
          <el-option
              v-for="dict in auditStatus"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">鎼滅储</el-button>
        <el-button icon="Refresh" @click="resetQuery">閲嶇疆</el-button>
      </el-form-item>
    </el-form>

    <!-- 鎿嶄綔鎸夐挳 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="permAdd"
        >鏂板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="permEdit"
        >淇敼</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="permRemove"
        >鍒犻櫎</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="permExport"
        >瀵煎嚭</el-button>
      </el-col>
    </el-row>

    <!-- 鏁版嵁琛ㄦ牸 -->
    <el-table v-loading="loading" :data="listData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="鎴愭灉ID" align="center" prop="achievementId" />
      <el-table-column label="姣旇禌" align="center" prop="track"`r`n        <template #default="scope">
  <template #default="scope">
    <span>{{ scope.row.competitionName || scope.row.competition_name || scope.row.track || scope.row.competitionId || '' }}</span>
  </template>
</el-table-column>
      <el-table-column label="灞婃" align="center" prop="sessionId"`r`n        <template #default="scope">
  <template #default="scope">
    <span>{{ scope.row.session || scope.row.sessionName || scope.row.session_name || scope.row.sessionId || '' }}</span>
  </template>
</el-table-column>
      <el-table-column label="鍙傝禌閫夋墜" align="center" prop="contestant" />
      <el-table-column label="鎸囧鑰佸笀" align="center" prop="instructor" />
      <el-table-column label="绫诲埆" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="浣滃搧鍚嶇О" align="center" prop="name" />
      <el-table-column label="绾у埆" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="鑾峰绛夌骇" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade" />
        </template>
      </el-table-column>
      <el-table-column label="璇佷功缂栧彿" align="center" prop="certificateNo" />
      <el-table-column label="缁勫埆" align="center" prop="groupId" min-width="100">
        <template #default="scope">
          <dict-tag :options="group_type" :value="scope.row.groupId" />
        </template>
      </el-table-column>
      <el-table-column label="瀹℃牳鐘舵€? align="center" prop="reviewStatus">
        <template #default="scope">
          <dict-tag :options="auditStatus" :value="scope.row.reviewStatus" />
        </template>
      </el-table-column>
      <el-table-column label="鑾峰鏃堕棿" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
 <el-table-column label="鎿嶄綔" align="center" class-name="small-padding fixed-width">
  <template #default="scope">
    <el-button link type="primary" icon="View" @click="handleReview(scope.row)" v-hasPermi="permReview">璇︽儏</el-button>
    
    <el-button 
      link 
      type="primary" 
      icon="Edit" 
      @click="handleRowUpdate(scope.row)" 
      v-hasPermi="permEdit"
      :disabled="!checkEditable(scope.row)" 
    >淇敼</el-button>
    
    <el-button 
      link 
      type="primary" 
      icon="Delete" 
      @click="handleDelete(scope.row)" 
      v-hasPermi="permRemove"
      :disabled="!checkEditable(scope.row)"
    >鍒犻櫎</el-button>
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
      cancel-text="杩斿洖"
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
      cancel-text="杩斿洖"
      @ok="handleFormOk"
      @cancel="handleFormCancel"
  />
  </div>
</template>

<script setup>
import { ref, reactive, computed, getCurrentInstance, nextTick, watch, onActivated } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useDict } from '@/utils/dict';
import AchievementForm from '../component/AchievementForm.vue';
import { listManage, getManage, addManage, updateManage, delManage } from '@/api/achievement/manage';

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
  reviewSource: { type: String, default: '' }
});

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

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
const permReview = computed(() => [`${permissionPrefix.value}:review`]);

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
const achievementFormRef = ref(null);
const achievementDialogRef = ref(null);
const pageModeActive = ref(false);
const pageModeKey = ref(0);
const formReadOnly = ref(false);
const formShowSubmit = ref(true);

const auditStatus = computed(() => {
  if (props.auditDict && Array.isArray(props.auditDict)) return props.auditDict;
  return queryParams.reviewStatus === 'college'
    ? college_audit_status.value
    : school_audit_status.value;
});

function getList() {
  loading.value = true;
  listFn.value(queryParams).then(response => {
    const rows = (response.rows || []).map((row) => normalizeRowStatus(row));
    let filtered = filterRowsBySource(rows);
    if (queryParams.reviewStatus !== null && queryParams.reviewStatus !== '' && queryParams.reviewStatus !== undefined) {
      filtered = filtered.filter((row) => String(row.reviewStatus) === String(queryParams.reviewStatus));
    }
    listData.value = filtered;
    total.value = filtered.length;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function normalizeRowStatus(row) {
  // 1. 鑾峰彇鍚庣鍙兘杩斿洖鐨勫悇绉嶅瓧娈靛悕
  const reviewResult = row.reviewResult ?? row.review_result ?? row.reviewStatus;
  const schoolResult = row.schooiReviewResult ?? row.schoolReviewResult ?? row.schooi_review_result ?? row.school_review_result;

  row.reviewResult = reviewResult;
  row.schooiReviewResult = schoolResult;

  // 2. 鏍规嵁鍦烘櫙鍐冲畾琛ㄦ牸閲岀殑鈥滃鏍哥姸鎬?reviewStatus)鈥濆垪鍒板簳鏄剧ず鍝釜鍊?
  if (reviewSource.value.startsWith('college')) {
    // 鍦烘櫙A锛氶櫌绾у鏍稿憳 -> 鏄剧ず闄㈢骇缁撴灉
    row.reviewStatus = reviewResult ?? 0;
  } else if (reviewSource.value.startsWith('school')) {
    // 鍦烘櫙B锛氭牎绾у鏍稿憳 -> 鏄剧ず鏍＄骇缁撴灉
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
    return rows.filter(r => String(r.schooiReviewResult) === '2');
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
  ids.value = selection.map(i => i.achievementId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  openDialog();
}

function handleUpdate() {
  const _achievementId = ids.value[0];
  if (_achievementId) openDialog(_achievementId, { readOnly: false });
}

/** 琛屽唴淇敼鎸夐挳鎿嶄綔 */
function handleRowUpdate(row) {
  const _achievementId = row?.achievementId;
  if (!_achievementId) return;

  // 銆愭牳蹇冧慨鏀广€戯細濡傛灉鏄鏍告ā寮忥紝鍙兘闇€瑕佽烦杞幓瀹℃牳淇敼锛堜繚鐣欏悓瀛﹂€昏緫锛?
  if (reviewSource.value) {
    openReviewPage(_achievementId, 'edit');
  } else {
    // 鍚﹀垯鏄櫘閫氱敤鎴凤紝鐩存帴鎵撳紑鏈湴寮圭獥杩涜缂栬緫
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
  router.push({
    path: reviewRoute.value,
    query: {
      id,
      source: reviewSource.value,
      mode
    }
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
 * 鏍稿績閫昏緫锛氭鏌ユ槸鍚﹀彲浠ヤ慨鏀?
 */
function checkEditable(row) {
  if (reviewSource.value) return true; 
  // 闃茬┖鍒ゆ柇
  if (!row) return false;
  // 鑾峰彇鐘舵€佸€?
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
</script>

<script>
export default {
  name: 'AchievementManageIndex'
}
</script>



