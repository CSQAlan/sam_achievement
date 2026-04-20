<template>
  <div class="app-container">
    <!-- 返回按钮 -->
    <el-row>
      <el-col>
        <el-button 
          type="primary" 
          :icon="ArrowLeft" 
          size="small" 
          @click="goBack"
          style="margin-bottom: 10px;"
        >返回报销项目列表</el-button>
      </el-col>
    </el-row>

    <!-- 项目信息卡片 -->
    <el-card v-if="reimbursementId" class="box-card" style="margin-bottom: 15px;">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold; font-size: 16px;">报销项目信息</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="12">
          <div><span class="info-label">项目名称：</span>{{ reimbursementName || '加载中...' }}</div>
        </el-col>
        <el-col :span="6">
          <div><span class="info-label">项目ID：</span>{{ reimbursementId }}</div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="90px">
      <el-row>
        <el-col :span="8">
          <el-form-item label="比赛名称" prop="achievementName">
            <el-input
              v-model="queryParams.achievementName"
              placeholder="请输入比赛名称"
              clearable
              @keyup.enter="handleQuery"
              style="width: 100%;"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="团队名称" prop="teamName">
            <el-input
              v-model="queryParams.teamName"
              placeholder="请输入团队名称"
              clearable
              @keyup.enter="handleQuery"
              style="width: 100%;"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input
              v-model="queryParams.certificateNo"
              placeholder="请输入证书编号"
              clearable
              @keyup.enter="handleQuery"
              style="width: 100%;"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="获奖时间" prop="awardTime">
            <el-date-picker
              v-model="queryParams.awardTime"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="获奖等级" prop="grade">
            <el-select v-model="queryParams.grade" placeholder="请选择" clearable style="width: 100%;">
              <el-option label="一等奖" :value="1" />
              <el-option label="二等奖" :value="2" />
              <el-option label="三等奖" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="级别" prop="level">
            <el-select v-model="queryParams.level" placeholder="请选择" clearable style="width: 100%;">
              <el-option label="国家级" value="国家级" />
              <el-option label="省级" value="省级" />
              <el-option label="市级" value="市级" />
              <el-option label="校级" value="校级" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="项目状态" prop="reimbursementStatus">
            <el-select v-model="queryParams.reimbursementStatus" placeholder="请选择" clearable style="width: 100%;">
              <el-option label="待提交" :value="0" />
              <el-option label="审核中" :value="1" />
              <el-option label="已通过" :value="2" />
              <el-option label="已驳回" :value="3" />
              <el-option label="已报销" :value="4" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="审核结果" prop="reviewResult">
            <el-select v-model="queryParams.reviewResult" placeholder="请选择" clearable style="width: 100%;">
              <el-option label="待审核" :value="0" />
              <el-option label="驳回" :value="1" />
              <el-option label="通过" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8" style="text-align: right;">
          <el-form-item>
            <el-button type="primary" @click="handleQuery">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="achievementList" border>
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="比赛名称" prop="achievementName" min-width="180" show-overflow-tooltip />
      <el-table-column label="团队名称" prop="teamName" min-width="150" show-overflow-tooltip />
      <el-table-column label="证书编号" prop="certificateNo" min-width="150" show-overflow-tooltip />
      <el-table-column label="获奖时间" prop="awardTime" width="120" " />
      <el-table-column label="获奖等级" prop="grade" width="100" ">
        <template #default="scope">
          {{ getGradeText(scope.row.grade) }}
        </template>
      </el-table-column>
      <el-table-column label="级别" prop="level" width="100" align="center" />
      <el-table-column label="项目状态" prop="reimbursementStatus" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.reimbursementStatus)">
            {{ getStatusText(scope.row.reimbursementStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核结果" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getReviewResultType(scope.row.reviewResult)">
            {{ getReviewResultText(scope.row.reviewResult) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名费" prop="fee" width="100" align="right">
        <template #default="scope">￥{{ scope.row.fee }}</template>
      </el-table-column>
      <el-table-column label="报销金额" prop="reimbursementFee" width="100" align="right">
        <template #default="scope">￥{{ scope.row.reimbursementFee }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="scope">
          <el-button type="primary" link size="small" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情对话框 -->
    <el-dialog :title="'成果详情'" v-model="dialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="比赛名称" :span="2">{{ currentAchievement.achievementName }}</el-descriptions-item>
        <el-descriptions-item label="团队名称">{{ currentAchievement.teamName }}</el-descriptions-item>
        <el-descriptions-item label="证书编号">{{ currentAchievement.certificateNo }}</el-descriptions-item>
        <el-descriptions-item label="获奖时间">{{ currentAchievement.awardTime }}</el-descriptions-item>
        <el-descriptions-item label="获奖等级">{{ getGradeText(currentAchievement.grade) }}</el-descriptions-item>
        <el-descriptions-item label="级别">{{ currentAchievement.level }}</el-descriptions-item>
        <el-descriptions-item label="项目状态">
          <el-tag :type="getStatusType(currentAchievement.reimbursementStatus)">
            {{ getStatusText(currentAchievement.reimbursementStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核结果">
          <el-tag :type="getReviewResultType(currentAchievement.reviewResult)">
            {{ getReviewResultText(currentAchievement.reviewResult) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报名费">￥{{ currentAchievement.fee }}</el-descriptions-item>
        <el-descriptions-item label="报销金额">￥{{ currentAchievement.reimbursementFee }}</el-descriptions-item>
        <el-descriptions-item label="报销项目" :span="2">{{ currentAchievement.reimbursementName }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="ReimbursementAchievement">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { listAchievement } from '@/api/system/view'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// ========== 添加调试代码 ==========
console.log('========== 页面开始加载 ==========')
console.log('路由对象:', route)
console.log('路由参数:', route.query)
console.log('reimbursementId:', route.query.reimbursementId)
console.log('reimbursementName:', route.query.reimbursementName)
console.log('API函数listAchievement:', listAchievement)
// ========== 调试代码结束 ==========

// 数据定义
const loading = ref(false)
const achievementList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const currentAchievement = ref({})

// 从路由获取参数
const reimbursementId = ref(route.query.reimbursementId)
const reimbursementName = ref(route.query.reimbursementName || '')

// 查询参数 - 增加到8个条件
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  reimbursementId: route.query.reimbursementId,
  achievementName: undefined,      // 比赛名称
  teamName: undefined,              // 团队名称
  certificateNo: undefined,         // 证书编号
  awardTime: undefined,             // 获奖时间范围
  grade: undefined,                 // 获奖等级
  level: undefined,                 // 级别
  reimbursementStatus: undefined,   // 项目状态
  reviewResult: undefined           // 审核结果
})

const queryFormRef = ref()

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    // 处理获奖时间范围
    const params = { ...queryParams }
    if (params.awardTime && Array.isArray(params.awardTime)) {
      params.beginAwardTime = params.awardTime[0]
      params.endAwardTime = params.awardTime[1]
      delete params.awardTime
    }
    
    const res = await listAchievement(params)
    achievementList.value = res.rows
    total.value = res.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  queryParams.achievementName = undefined
  queryParams.teamName = undefined
  queryParams.certificateNo = undefined
  queryParams.awardTime = undefined
  queryParams.grade = undefined
  queryParams.level = undefined
  queryParams.reimbursementStatus = undefined
  queryParams.reviewResult = undefined
  handleQuery()
}

// 查看详情
const handleView = (row) => {
  currentAchievement.value = row
  dialogVisible.value = true
}

// 返回
const goBack = () => {
   // 方式1：返回上一页（推荐）
   router.back()
}
// 获奖等级文本转换
const getGradeText = (grade) => {
  const texts = {
    1: '一等奖',
    2: '二等奖',
    3: '三等奖'
  }
  return texts[grade] || grade
}

// 项目状态样式和文本
const getStatusType = (status) => {
  const types = {
    0: 'info',      // 待提交
    1: 'warning',   // 审核中
    2: 'success',   // 已通过
    3: 'danger',    // 已驳回
    4: 'success'    // 已报销
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待提交',
    1: '审核中',
    2: '已通过',
    3: '已驳回',
    4: '已报销'
  }
  return texts[status] || '未知'
}

// 审核结果样式和文本
const getReviewResultType = (result) => {
  const types = {
    '0': 'info',    // 待审核
    '1': 'danger',  // 驳回
    '2': 'success'  // 通过
  }
  return types[result] || 'info'
}

const getReviewResultText = (result) => {
  const texts = {
    '0': '待审核',
    '1': '驳回',
    '2': '通过'
  }
  return texts[result] || '未知'
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.info-label {
  font-weight: bold;
  color: #666;
  margin-right: 10px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>