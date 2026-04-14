<template>
  <div class="app-container home">
    <!-- 1. 顶部个性化欢迎语 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover" class="welcome-card">
          <div class="welcome-header">
            <div class="user-greeting">
              <h2>{{ greetingText }}，{{ userStore.nickName }}！</h2>
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

    <!-- 2. 动态统计卡片 -->
    <el-row :gutter="20" class="mb20">
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
          <template #header><div class="card-header"><span>年度成果产出趋势</span></div></template>
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="10">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>获奖级别分布占比</span></div></template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 4. 底部功能区 -->
    <el-row :gutter="20">
      <el-col :sm="24" :lg="14">
        <el-card shadow="hover" class="notice-card">
          <template #header>
            <div class="card-header">
              <span class="title-with-icon"><el-icon><Notification /></el-icon> 最新通知公告</span>
              <el-link type="primary" :underline="false" @click="$router.push('/system/notice')">查看更多 <el-icon><ArrowRight /></el-icon></el-link>
            </div>
          </template>
          <div class="notice-body">
            <el-timeline v-if="noticeList.length > 0">
              <el-timeline-item 
                v-for="(item, index) in noticeList" 
                :key="item.noticeId"
                :timestamp="proxy.parseTime(item.createTime, '{y}-{m}-{d}')" 
                :type="index === 0 ? 'primary' : ''"
                :hollow="index !== 0"
              >
                <div class="notice-item-title" @click="handleNoticeClick(item)">
                  <el-tag size="small" :type="getNoticeTag(item.noticeType)" class="mr5">{{ getNoticeTypeName(item.noticeType) }}</el-tag>
                  <span class="text-main">{{ item.noticeTitle }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无通知公告" :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="10">
        <el-card shadow="hover" class="quick-card">
          <template #header><div class="card-header"><span><el-icon><Menu /></el-icon> 快速入口</span></div></template>
          <div class="link-grid">
            <el-button type="primary" icon="Plus" plain @click="handleAchievementApply">成果申请</el-button>
            <el-button type="warning" icon="UserFilled" plain @click="handleMyAchievement">我的成果</el-button>
            <el-button v-if="isReviewer" type="success" icon="Checked" plain @click="$router.push('/achievement/college_level_unreviewed')">成果审核</el-button>
            <el-button type="info" icon="Document" plain @click="$router.push('/competitions/competitionapply')">赛事申请</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import * as echarts from 'echarts';
import useUserStore from '@/store/modules/user';
import { useRouter } from 'vue-router';
import { onMounted, ref, computed, onUnmounted, getCurrentInstance } from 'vue';
import { listManage } from "@/api/achievement/manage";
import { listStudent } from "@/api/achievement/student";
import { listCollege_level_unreviewed } from "@/api/achievement/college_level_unreviewed";
import { listSchool_level_unreviewed } from "@/api/achievement/school_level_unreviewed";
import { listNotice } from "@/api/system/notice";

const { proxy } = getCurrentInstance();
const router = useRouter();
const userStore = useUserStore();
const trendChartRef = ref(null);
const pieChartRef = ref(null);
let trendChart = null;
let pieChart = null;

const studentStats = ref({ certified: 0, pending: 0, rejected: 0 });
const reviewerStats = ref({ totalCount: 0, monthCount: 0, studentCount: 0, pendingCount: 0 });
const chartData = ref({ years: [], yearCounts: [], levels: [] });
const noticeList = ref([]);

const roles = computed(() => userStore.roles || []);
const permissions = computed(() => userStore.permissions || []);
const isAdmin = computed(() => roles.value.includes('admin') || permissions.value.includes('*:*:*'));
const isStudent = computed(() => roles.value.includes('student'));
const isTeacher = computed(() => roles.value.includes('teacher'));
const isCollegeReviewer = computed(() => permissions.value.includes('achievement:college_level_unreviewed:list'));
const isSchoolReviewer = computed(() => permissions.value.includes('achievement:school_level_unreviewed:list'));
const isReviewer = computed(() => isCollegeReviewer.value || isSchoolReviewer.value || isAdmin.value);
const isOnlyStudent = computed(() => (isStudent.value || isTeacher.value) && !isReviewer.value);

const handleAchievementApply = () => {
  if (isTeacher.value) {
    router.push({ path: '/achievement/teacher', query: { sourceMode: 'guided' } });
  } else if (isStudent.value) {
    router.push('/achievement/student');
  } else {
    router.push('/achievement/manage');
  }
};

const handleMyAchievement = () => {
  router.push({ path: '/achievement/participated', query: { sourceMode: 'participated' } });
};

const getStats = async () => {
  if (isStudent.value || isTeacher.value) {
    try {
      const res = await listManage({ pageNum: 1, pageSize: 999 });
      const rows = res.rows || [];
      studentStats.value.certified = rows.filter(r => String(r.schooiReviewResult) === '1').length;
      studentStats.value.pending = rows.filter(r => String(r.reviewResult) === '0' || (String(r.reviewResult) === '2' && String(r.schooiReviewResult) === '2')).length;
      studentStats.value.rejected = rows.filter(r => String(r.reviewResult) === '1' || String(r.schooiReviewResult) === '0').length;
    } catch (e) { console.error(e); }
  }
  if (isReviewer.value) {
    try {
      const resTotal = await listManage({ pageNum: 1, pageSize: 1 });
      reviewerStats.value.totalCount = resTotal.total || 0;
      const now = new Date();
      const firstDay = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
      const lastDay = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
      const resMonth = await listManage({ pageNum: 1, pageSize: 1, params: { beginTime: firstDay, endTime: lastDay } });
      reviewerStats.value.monthCount = resMonth.total || 0;
      const resStudent = await listStudent({ pageNum: 1, pageSize: 1 });
      reviewerStats.value.studentCount = resStudent.total || 0;
      reviewerStats.value.pendingCount = 0;
      if (isCollegeReviewer.value || isAdmin.value) {
        const res = await listCollege_level_unreviewed({ pageNum: 1, pageSize: 1 });
        reviewerStats.value.pendingCount += (res.total || 0);
      }
      if (isSchoolReviewer.value || isAdmin.value) {
        const res = await listSchool_level_unreviewed({ pageNum: 1, pageSize: 1 });
        reviewerStats.value.pendingCount += (res.total || 0);
      }
    } catch (e) { console.error(e); }
  }
};

const getChartsData = async () => {
  try {
    const currentYear = new Date().getFullYear();
    const years = [currentYear - 2, currentYear - 1, currentYear];
    const yearRes = await Promise.all(years.map(y => listManage({ pageNum: 1, pageSize: 1, year: y })));
    chartData.value.years = years.map(y => `${y}年`);
    chartData.value.yearCounts = yearRes.map(res => res.total || 0);

    const levelConfigs = [
      { label: '国家级', value: '0', color: '#ff4d4f' },
      { label: '省市级', value: '1', color: '#ffa940' },
      { label: '校级', value: '3', color: '#40a9ff' }
    ];
    const levelRes = await Promise.all(levelConfigs.map(c => listManage({ pageNum: 1, pageSize: 1, level: c.value })));
    chartData.value.levels = levelConfigs.map((c, i) => ({ name: c.label, value: levelRes[i].total || 0, itemStyle: { color: c.color } })).filter(item => item.value > 0);
    if (chartData.value.levels.length === 0) chartData.value.levels = [{ name: '暂无数据', value: 0, itemStyle: { color: '#eee' } }];
    initCharts();
  } catch (e) { console.error(e); }
};

const getNoticeList = async () => {
  try {
    const res = await listNotice({ 
      pageNum: 1, 
      pageSize: 6,
      orderByColumn: 'createTime',
      isAsc: 'descending'
    });
    noticeList.value = res.rows || [];
  } catch (e) { console.error(e); }
};

const getNoticeTag = (type) => {
  const map = { '1': 'success', '2': 'warning', '3': 'danger' };
  return map[type] || 'info';
};

const getNoticeTypeName = (type) => {
  const map = { '1': '通知', '2': '公告', '3': '紧急' };
  return map[type] || '其他';
};

const handleNoticeClick = (notice) => {
  router.push(`/system/notice`); // 实际项目中通常跳转到详情页
};

const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好'; if (hour < 9) return '早上好'; if (hour < 12) return '上午好';
  if (hour < 14) return '中午好'; if (hour < 17) return '下午好'; if (hour < 19) return '傍晚好';
  return '晚上好';
});
const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
const weekDay = '星期' + '日一二三四五六'.charAt(new Date().getDay());

const initCharts = () => {
  if (!trendChartRef.value || !pieChartRef.value) return;
  if (!trendChart) trendChart = echarts.init(trendChartRef.value);
  trendChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { top: '15%', left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: chartData.value.years.length ? chartData.value.years : ['2024', '2025', '2026'], axisTick: { alignWithLabel: true } },
    yAxis: { type: 'value', name: '数量' },
    series: [{ name: '成果数', type: 'bar', barWidth: '35%', data: chartData.value.yearCounts, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#1890ff' }, { offset: 1, color: '#36cfc9' }]), borderRadius: [4, 4, 0, 0] } }]
  });
  if (!pieChart) pieChart = echarts.init(pieChartRef.value);
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{ name: '获奖级别', type: 'pie', radius: ['45%', '70%'], center: ['55%', '45%'], avoidLabelOverlap: true, itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 }, data: chartData.value.levels }]
  });
};

onMounted(() => { 
  getStats(); 
  getChartsData(); 
  getNoticeList();
  window.addEventListener('resize', () => { trendChart && trendChart.resize(); pieChart && pieChart.resize(); }); 
});
onUnmounted(() => { if (trendChart) trendChart.dispose(); if (pieChart) pieChart.dispose(); });
</script>

<style scoped lang="scss">
.home {
  background-color: #f5f7f9; padding: 20px; min-height: calc(100vh - 84px);
  .mb20 { margin-bottom: 20px; }
  .mr5 { margin-right: 5px; }
  .welcome-card {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); color: #fff; border: none;
    .welcome-header { display: flex; justify-content: space-between; align-items: center;
      .user-greeting { h2 { font-size: 26px; margin: 0 0 10px 0; font-weight: 600; } .sub-text { opacity: 0.9; font-size: 15px; } .warning-text { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 4px; margin-top: 5px; display: inline-flex; align-items: center; gap: 5px; } }
      .date-box { text-align: right; .date { display: block; font-size: 18px; font-weight: bold; } .week { font-size: 14px; opacity: 0.8; } }
    }
  }
  .stat-card {
    .card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
    .stat-body { padding: 15px 0; .number { font-size: 36px; font-weight: bold; color: #303133; } .label { margin-left: 10px; color: #909399; font-size: 16px; } }
    &.blue { border-top: 4px solid #1890ff; } &.green { border-top: 4px solid #52c41a; } &.orange { border-top: 4px solid #e6a23c; } &.red { border-top: 4px solid #f56c6c; } &.purple { border-top: 4px solid #722ed1; }
  }
  .notice-card {
    height: 100%;
    .notice-body { padding: 5px 0; min-height: 250px; }
    .notice-item-title { cursor: pointer; display: inline-flex; align-items: center; transition: all 0.2s; &:hover { color: #1890ff; transform: translateX(5px); } }
  }
  .quick-card { height: 100%; }
  .link-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; .el-button { margin: 0; height: 80px; font-size: 16px; display: flex; flex-direction: column; gap: 10px; &:hover { transform: scale(1.02); } } }
  .card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold;
    .title-with-icon { display: flex; align-items: center; gap: 8px; }
  }
}
</style>
