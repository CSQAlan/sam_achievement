<template>
  <div class="app-container">
    <div class="card-container">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="比赛名称">
            <el-input v-model="searchForm.competitionName" placeholder="请输入比赛名称" class="search-input" />
          </el-form-item>
          <el-form-item label="参赛选手">
            <el-input v-model="searchForm.contestant" placeholder="请输入参赛选手" class="search-input" />
          </el-form-item>
          <el-form-item label="指导老师">
            <el-input v-model="searchForm.instructor" placeholder="请输入指导老师" class="search-input" />
          </el-form-item>
          <el-form-item label="教师工号">
            <el-input v-model="searchForm.teacherId" placeholder="请输入教师工号" class="search-input" />
          </el-form-item>
          <el-form-item label="获奖等级">
            <el-select v-model="searchForm.grade" placeholder="请选择获奖等级" class="search-input">
              <el-option
                v-for="item in gradeOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
            <el-button icon="Refresh" @click="handleReset">重置</el-button>
            <el-button type="success" icon="Download" @click="handleExport">导出</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-container">
        <el-table
          :data="tableData"
          :loading="loading"
          border
          stripe
          :default-sort="{ prop: 'achievementId', order: 'desc' }"
          class="quality-table"
        >
          <el-table-column type="index" label="成果序号" width="80" align="center" />

          <el-table-column prop="competitionName" label="比赛名称" min-width="150">
            <template #default="scope">
              <span class="competition-name">{{ scope.row.competitionName }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="sessionName" label="届次" width="100" align="center" />

          <el-table-column prop="contestant" label="参赛选手" min-width="180">
            <template #default="scope">
              <span :title="scope.row.contestant">{{ scope.row.contestant }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="instructor" label="指导老师" min-width="150">
            <template #default="scope">
              <span :title="scope.row.instructor">{{ scope.row.instructor }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="category" label="类别" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getCategoryTagType(scope.row.category)">{{ getCategoryLabel(scope.row.category) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="level" label="级别" width="120" align="center">
            <template #default="scope">
              <el-tag type="success">{{ getLevelLabel(scope.row.level) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="grade" label="获奖等级" width="110" align="center">
            <template #default="scope">
              <el-tag :type="getGradeTagType(scope.row.grade)">{{ getGradeLabel(scope.row.grade) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="certificateNo" label="证书编号" min-width="150">
            <template #default="scope">
              <span :title="scope.row.certificateNo">{{ scope.row.certificateNo }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="groupId" label="组别" width="100" align="center">
            <template #default="scope">
              <span>{{ getGroupLabel(scope.row.groupId) }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="awardTime" label="获奖时间" width="120" align="center">
            <template #default="scope">
              <span>{{ formatDate(scope.row.awardTime) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button
                link
                type="primary"
                icon="View"
                @click="handleView(scope.row)"
              >详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            :current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <AchievementForm
        ref="achievementDialogRef"
        :get-fn="getManage"
        :read-only="formReadOnly"
        cancel-text="关闭"
        @cancel="() => {}"
    >
    </AchievementForm>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { Search, Refresh, View, Download } from '@element-plus/icons-vue';
import { listQualityAchievementTeacher, getAchievementInfo } from '@/api/system/SamQualityDevelopmentFormTeacher';
import { getDicts } from "@/api/system/dict/data";
import axios from 'axios';
import { getToken } from '@/utils/auth';
import AchievementForm from '@/views/achievement/component/AchievementForm.vue';
import { getManage } from '@/api/achievement/manage';

const loading = ref(false);
const tableData = ref([]);
const achievementDialogRef = ref(null);
const formReadOnly = ref(true);

const categoryOptions = ref([]);
const levelOptions = ref([]);
const gradeOptions = ref([]);
const groupOptions = ref([]);

const searchForm = reactive({
  competitionName: '',
  contestant: '',
  instructor: '',
  teacherId: '',
  grade: ''
});

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const getDictData = async () => {
  try {
    const [categoryRes, levelRes, gradeRes, groupRes] = await Promise.all([
      getDicts('sys_competition_category'),
      getDicts('sys_competition_level'),
      getDicts('award_grade'),
      getDicts('group_type')
    ]);
    categoryOptions.value = formatDictData(categoryRes.data || []);
    levelOptions.value = formatDictData(levelRes.data || []);
    gradeOptions.value = formatDictData(gradeRes.data || []);
    groupOptions.value = formatDictData(groupRes.data || []);
  } catch (error) {
    console.error('获取字典数据失败:', error);
  }
};

const formatDictData = (data) => {
  return data.map(item => ({
    dictValue: item.dictValue !== undefined ? item.dictValue : item.dict_value,
    dictLabel: item.dictLabel !== undefined ? item.dictLabel : item.dict_label
  }));
};

const getLabelByValue = (options, code) => {
  if (!code && code !== 0) return '-';
  const item = options.find(opt => String(opt.dictValue) === String(code));
  return item ? item.dictLabel : (code || '-');
};

const getCategoryLabel = (code) => {
  return getLabelByValue(categoryOptions.value, code);
};

const getLevelLabel = (code) => {
  return getLabelByValue(levelOptions.value, code);
};

const getGradeLabel = (code) => {
  return getLabelByValue(gradeOptions.value, code);
};

const getGroupLabel = (code) => {
  return getLabelByValue(groupOptions.value, code);
};

const getCategoryTagType = (category) => {
  const label = getCategoryLabel(category);
  const types = {
    '政府类': 'primary',
    '学会类': 'success',
    '企业类': 'warning',
    '其他': 'info'
  };
  return types[label] || 'info';
};

const getGradeTagType = (grade) => {
  const label = getGradeLabel(grade);
  const types = {
    '一等奖': 'danger',
    '二等奖': 'warning',
    '三等奖': 'primary',
    '优秀奖': 'success'
  };
  return types[label] || 'info';
};

const getList = () => {
  loading.value = true;
  const params = {
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    competitionName: searchForm.competitionName,
    contestant: searchForm.contestant,
    instructor: searchForm.instructor,
    teacherId: searchForm.teacherId,
    grade: searchForm.grade
  };

  listQualityAchievementTeacher(params).then(response => {
    tableData.value = response.rows || [];
    pagination.total = response.total || 0;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
};

const handleSearch = () => {
  pagination.pageNum = 1;
  getList();
};

const handleReset = () => {
  searchForm.competitionName = '';
  searchForm.contestant = '';
  searchForm.instructor = '';
  searchForm.teacherId = '';
  searchForm.grade = '';
  pagination.pageNum = 1;
  getList();
};

const handleSizeChange = (size) => {
  pagination.pageSize = size;
  getList();
};

const handleCurrentChange = (page) => {
  pagination.pageNum = page;
  getList();
};

const handleView = (row) => {
  if (row && row.achievementId) {
    formReadOnly.value = true;
    nextTick(() => {
      achievementDialogRef.value?.open(row.achievementId);
    });
  }
};

const handleExport = () => {
  const baseURL = import.meta.env.VITE_APP_BASE_API;
  axios({
    method: 'post',
    url: baseURL + '/achievement/manage/export-quality-teacher',
    data: {
      competitionName: searchForm.competitionName,
      contestant: searchForm.contestant,
      instructor: searchForm.instructor,
      teacherId: searchForm.teacherId,
      grade: searchForm.grade
    },
    responseType: 'blob',
    headers: { 'Authorization': 'Bearer ' + getToken() }
  }).then((res) => {
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '教师指导素质提升奖成果统计.xlsx';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }).catch((error) => {
    console.error('导出失败:', error);
  });
};

const formatDate = (date) => {
  if (!date) return '';
  return date.substring(0, 10);
};

onMounted(() => {
  getDictData();
  getList();
});
</script>

<style lang="scss" scoped>
.app-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 20px;
}

.card-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.search-bar {
  padding: 20px 30px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-input {
  width: 200px;
}

.table-container {
  padding: 20px 30px;
}

.quality-table {
  width: 100%;

  :deep(.el-table__header) {
    background: #f8f9fa;

    th {
      color: #495057;
      font-weight: 600;
      text-align: center;
      padding: 14px 8px;
      border-bottom: 2px solid #e9ecef;
    }
  }

  :deep(.el-table__body) {
    tr {
      transition: all 0.2s ease;

      &:hover {
        background: #f8fafc;
      }
    }

    td {
      padding: 12px 8px;
      text-align: center;
      border-bottom: 1px solid #f0f0f0;
    }
  }

  :deep(.el-tag) {
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 12px;
  }
}

.competition-name {
  font-weight: 500;
  color: #667eea;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>