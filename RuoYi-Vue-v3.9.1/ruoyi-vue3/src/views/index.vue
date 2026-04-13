<template>
  <div class="app-container home">
    <!-- 1. 顶部个性化欢迎语 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover" class="welcome-card">
          <div class="welcome-header">
            <div class="user-greeting">
              <h2>{{ greetingText }}，{{ userStore.nickName }}！</h2>
              <!-- 仅在没有学院信息时显示精简提醒 -->
              <p v-if="!userStore.deptName" class="sub-text warning-text">
                <el-icon><Warning /></el-icon> 检测到您尚未关联学院，请前往 
                <el-link type="warning" @click="$router.push('/user/profile')">个人中心</el-link> 完善信息。
              </p>
            </div>
            <div class="date-box">
              <span class="date">{{ currentDate }}</span>
              <span class="week">{{ weekDay }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 动态统计卡片 (根据角色自动切换) -->
    <el-row :gutter="20" class="mb20">
      <!-- 学生视野：展示个人申报状态 -->
      <template v-if="isOnlyStudent">
        <el-col :sm="24" :lg="8">
          <el-card shadow="hover" class="stat-card blue">
            <template #header><div class="card-header"><span>已认定成果</span><el-icon><Checked /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ studentStats.certified }}</span><span class="label">项</span></div>
          </el-card>
        </el-col>
        <el-col :sm="24" :lg="8">
          <el-card shadow="hover" class="stat-card green">
            <template #header><div class="card-header"><span>正在审核中</span><el-icon><Loading /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ studentStats.pending }}</span><span class="label">项</span></div>
          </el-card>
        </el-col>
        <el-col :sm="24" :lg="8">
          <el-card shadow="hover" class="stat-card orange">
            <template #header><div class="card-header"><span>需修改退回</span><el-icon><EditPen /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ studentStats.rejected }}</span><span class="label">项</span></div>
          </el-card>
        </el-col>
      </template>

      <!-- 管理员/审核人/老师视野：展示系统/审核状态 -->
      <template v-else>
        <el-col :sm="24" :lg="6">
          <el-card shadow="hover" class="stat-card blue">
            <template #header><div class="card-header"><span>系统成果总数</span><el-icon><DataLine /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ reviewerStats.totalCount }}</span><span class="label">项</span></div>
          </el-card>
        </el-col>
        <el-col :sm="24" :lg="6">
          <el-card shadow="hover" class="stat-card red">
            <template #header><div class="card-header"><span>待审核任务</span><el-icon><Bell /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ reviewerStats.pendingCount }}</span><span class="label">条</span></div>
          </el-card>
        </el-col>
        <el-col :sm="24" :lg="6">
          <el-card shadow="hover" class="stat-card green">
            <template #header><div class="card-header"><span>本月新增数</span><el-icon><TrendCharts /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ reviewerStats.monthCount }}</span><span class="label">项</span></div>
          </el-card>
        </el-col>
        <el-col :sm="24" :lg="6">
          <el-card shadow="hover" class="stat-card purple">
            <template #header><div class="card-header"><span>参与学生数</span><el-icon><User /></el-icon></div></template>
            <div class="stat-body"><span class="number">{{ reviewerStats.studentCount }}</span><span class="label">人</span></div>
          </el-card>
        </el-col>
      </template>
    </el-row>

    <!-- 3. 数据图表区 -->
    <el-row :gutter="20" class="mb20">
      <el-col :sm="24" :lg="14">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>成果增长趋势 (2024-2026)</span></div></template>
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="10">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>成果类型分布占比</span></div></template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 4. 底部功能区 -->
    <el-row :gutter="20">
      <el-col :sm="24" :lg="16">
        <el-card shadow="hover" style="height: 100%">
          <template #header><div class="card-header"><span>最新通知公告</span></div></template>
          <el-timeline>
            <el-timeline-item timestamp="2026-04-12" type="primary">
              2026年度“互联网+”大学生创新创业大赛申报工作启动
            </el-timeline-item>
            <el-timeline-item timestamp="2026-04-10">
              各学院成果认定管理办法（2026修订版）公示
            </el-timeline-item>
            <el-timeline-item timestamp="2026-04-05">
              关于系统新增“附件批量下载”功能的说明
            </el-timeline-item>
            <el-timeline-item timestamp="2026-04-01">
              成果管理系统正式上线运行通知
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="8">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>快速入口</span></div></template>
          <div class="link-grid">
            <el-button type="primary" icon="Plus" plain @click="$router.push('/achievement/apply')">成果申报</el-button>
            <el-button v-if="isReviewer" type="success" icon="Checked" plain @click="$router.push('/achievement/audit')">成果审核</el-button>
            <el-button type="info" icon="Document" plain @click="$router.push('/achievement/my')">我的申报</el-button>
            <el-button v-if="isAdmin" type="warning" icon="PieChart" plain @click="$router.push('/achievement/stats')">统计分析</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import * as echarts from 'echarts';
import useUserStore from '@/store/modules/user';
import { onMounted, ref, computed, onUnmounted } from 'vue';
import { listManage } from "@/api/achievement/manage";
import { listStudent } from "@/api/achievement/student";
import { listCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import { listSchool_level_unreviewed } from "@/api/achievement/school_level_unreviewed";

const userStore = useUserStore();
const trendChartRef = ref(null);
const pieChartRef = ref(null);
let trendChart = null;
let pieChart = null;

// 统计数据
const studentStats = ref({ certified: 0, pending: 0, rejected: 0 });
const reviewerStats = ref({ 
  totalCount: 0, 
  monthCount: 0, 
  studentCount: 0, 
  pendingCount: 0 
});

// 角色判断
const roles = computed(() => userStore.roles || []);
const permissions = computed(() => userStore.permissions || []);

const isAdmin = computed(() => roles.value.includes('admin') || permissions.value.includes('*:*:*'));
const isStudent = computed(() => roles.value.includes('student'));
const isTeacher = computed(() => roles.value.includes('teacher'));
const isCollegeReviewer = computed(() => permissions.value.includes('achievement:college_level_unreviewed:list'));
const isSchoolReviewer = computed(() => permissions.value.includes('achievement:school_level_unreviewed:list'));
const isReviewer = computed(() => isCollegeReviewer.value || isSchoolReviewer.value || isAdmin.value);

// 用于切换首页展示模式：如果是纯学生（或者是老师但没有审核权限），展示个人视野
const isOnlyStudent = computed(() => (isStudent.value || isTeacher.value) && !isReviewer.value);

// 获取统计数据
const getStats = async () => {
  // 1. 学生视野统计
  if (isStudent.value || isTeacher.value) {
    try {
      const res = await listManage({ pageNum: 1, pageSize: 999 });
      const rows = res.rows || [];
      studentStats.value.certified = rows.filter(r => String(r.schooiReviewResult) === '1').length;
      studentStats.value.pending = rows.filter(r => 
        String(r.reviewResult) === '0' || 
        (String(r.reviewResult) === '2' && String(r.schooiReviewResult) === '2')
      ).length;
      studentStats.value.rejected = rows.filter(r => 
        String(r.reviewResult) === '1' || 
        String(r.schooiReviewResult) === '0'
      ).length;
    } catch (e) {
      console.error("Failed to fetch student stats", e);
    }
  }

  // 2. 审核人/管理员视野统计
  if (isReviewer.value) {
    try {
      // 获取成果总数
      const resTotal = await listManage({ pageNum: 1, pageSize: 1 });
      reviewerStats.value.totalCount = resTotal.total || 0;

      // 获取本月新增 (计算日期范围)
      const now = new Date();
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
      const formatDate = (date) => {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
      };
      const resMonth = await listManage({ 
        pageNum: 1, 
        pageSize: 1, 
        params: { 
          beginTime: formatDate(firstDay), 
          endTime: formatDate(now) 
        } 
      });
      reviewerStats.value.monthCount = resMonth.total || 0;

      // 获取学生总数
      const resStudent = await listStudent({ pageNum: 1, pageSize: 1 });
      reviewerStats.value.studentCount = resStudent.total || 0;

      // 待办审核数
      reviewerStats.value.pendingCount = 0;
      if (isCollegeReviewer.value || isAdmin.value) {
        const resCol = await listCollege_level_unreviewed({ pageNum: 1, pageSize: 1 });
        reviewerStats.value.pendingCount += (resCol.total || 0);
      }
      if (isSchoolReviewer.value || isAdmin.value) {
        const resSch = await listSchool_level_unreviewed({ pageNum: 1, pageSize: 1 });
        reviewerStats.value.pendingCount += (resSch.total || 0);
      }
    } catch (e) {
      console.error("Failed to fetch reviewer stats", e);
    }
  }
};

// 2. 动态问候语
const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  return '晚上好';
});

const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
const weekDay = '星期' + '日一二三四五六'.charAt(new Date().getDay());

// 3. 图表初始化
const initCharts = () => {
  // 趋势图 (年度成果产出)
  trendChart = echarts.init(trendChartRef.value);
  trendChart.setOption({
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      axisPointer: { type: 'shadow' }
    },
    grid: { top: '15%', left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { 
      type: 'category', 
      data: ['2024年', '2025年', '2026年 (当前)'],
      axisTick: { alignWithLabel: true }
    },
    yAxis: { 
      type: 'value',
      name: '成果数量 (项)',
      splitLine: { lineStyle: { type: 'dashed' } }
    },
    series: [
      { 
        name: '成果总数', 
        type: 'bar', 
        barWidth: '35%', 
        data: [820, 1050, 1248], 
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#1890ff' },
            { offset: 1, color: '#36cfc9' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      { 
        name: '同比增长', 
        type: 'line', 
        data: [600, 900, 1100], 
        smooth: true,
        lineStyle: { color: '#ff7a45', width: 3 },
        symbol: 'circle',
        symbolSize: 8
      }
    ]
  });

  // 饼图 (竞赛获奖级别分布)
  pieChart = echarts.init(pieChartRef.value);
  pieChart.setOption({
    title: {
      text: '竞赛获奖级别分布',
      left: 'center',
      top: 'bottom',
      textStyle: { fontSize: 14, color: '#666', fontWeight: 'normal' }
    },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', data: ['国家级', '省级', '市级', '校级'] },
    series: [
      {
        name: '获奖级别',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['55%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: '18', fontWeight: 'bold' }
        },
        data: [
          { value: 120, name: '国家级', itemStyle: { color: '#ff4d4f' } },
          { value: 350, name: '省级', itemStyle: { color: '#ffa940' } },
          { value: 280, name: '市级', itemStyle: { color: '#36cfc9' } },
          { value: 498, name: '校级', itemStyle: { color: '#40a9ff' } }
        ]
      }
    ]
  });
};

onMounted(() => {
  initCharts();
  getStats();
  window.addEventListener('resize', () => {
    trendChart && trendChart.resize();
    pieChart && pieChart.resize();
  });
});

onUnmounted(() => {
  if (trendChart) trendChart.dispose();
  if (pieChart) pieChart.dispose();
});
</script>

<style scoped lang="scss">
.home {
  background-color: #f5f7f9;
  padding: 20px;
  min-height: calc(100vh - 84px);

  .mb20 { margin-bottom: 20px; }

  .welcome-card {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    color: #fff;
    border: none;
    .welcome-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .user-greeting {
        h2 { font-size: 26px; margin: 0 0 10px 0; font-weight: 600; }
        .sub-text { opacity: 0.9; font-size: 15px; }
        .warning-text { 
          background: rgba(255, 255, 255, 0.2); 
          padding: 4px 12px; 
          border-radius: 4px;
          margin-top: 5px;
          display: inline-flex;
          align-items: center;
          gap: 5px;
        }
      }
      .date-box {
        text-align: right;
        .date { display: block; font-size: 18px; font-weight: bold; }
        .week { font-size: 14px; opacity: 0.8; }
      }
    }
  }

  .stat-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
    .stat-body {
      padding: 15px 0;
      .number { font-size: 36px; font-weight: bold; color: #303133; }
      .label { margin-left: 10px; color: #909399; font-size: 16px; }
    }
    &.blue { border-top: 4px solid #1890ff; }
    &.green { border-top: 4px solid #52c41a; }
    &.orange { border-top: 4px solid #e6a23c; }
    &.red { border-top: 4px solid #f56c6c; }
    &.purple { border-top: 4px solid #722ed1; }
  }

  .link-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    .el-button { 
      margin: 0; 
      height: 80px; 
      font-size: 16px; 
      display: flex; 
      flex-direction: column; 
      gap: 10px;
      &:hover { transform: scale(1.02); }
    }
  }
}
</style>
