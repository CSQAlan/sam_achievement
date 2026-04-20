import request from '@/utils/request'


// 查询报销项目详情详细
export function getReimbursement(achievementId) {
  return request({
    url: '/system/Reimbursement/' + achievementId,
    method: 'get'
  })
}

// 新增报销项目详情
export function addReimbursement(data) {
  return request({
    url: '/system/Reimbursement',
    method: 'post',
    data: data
  })
}

// 修改报销项目详情
export function updateReimbursement(data) {
  return request({
    url: '/system/Reimbursement',
    method: 'put',
    data: data
  })
}

// 删除报销项目详情
export function delReimbursement(achievementId) {
  return request({
    url: '/system/Reimbursement/' + achievementId,
    method: 'delete'
  })
}
// 查询报销项目详情列表（支持按报销项目ID查询）
export function listReimbursement(query) {
  return request({
    url: '/system/Reimbursement/list',
    method: 'get',
    params: query  // 现在支持传递 reimbursementItemId
  })
}

export default {
  data() {
    return {
      competitionId: null, // 当前比赛的ID
      allExpenses: [], // 所有报销数据
      filteredExpenses: [], // 筛选后的数据
      currentCompetition: null // 当前比赛信息
    }
  },
  
  created() {
    // 获取传递过来的比赛ID
    // 方式1：从query中获取
    this.competitionId = this.$route.query.competitionId
    
    // 方式2：从params中获取
    // this.competitionId = this.$route.params.competitionId
    
    // 加载数据并筛选
    this.loadAndFilterData()
  },
  
  methods: {
    // 加载数据并筛选
    async loadAndFilterData() {
      // 获取所有报销数据（从你的API或store中获取）
      this.allExpenses = await this.fetchAllExpenses()
      
      // 根据比赛ID筛选数据
      this.filteredExpenses = this.allExpenses.filter(
        expense => expense.competitionId === this.competitionId
      )
      
      // 获取当前比赛信息
      this.currentCompetition = this.getCompetitionById(this.competitionId)
    },
    
    // 获取所有报销数据的方法
    fetchAllExpenses() {
      // 这里替换成你实际的数据获取方式
      // 可能是API调用、Vuex state等
      return axios.get('/api/expenses').then(res => res.data)
    },
    
    // 根据ID获取比赛信息
    getCompetitionById(id) {
      return this.allExpenses.find(exp => exp.competitionId === id)?.competition
    }
  }
}