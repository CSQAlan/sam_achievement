/**
 * 表格通用逻辑 Composable
 * 用于处理列表页面的通用逻辑：分页、搜索、选择等
 */
import { ref, reactive, toRefs } from 'vue'

export function useTable(options = {}) {
  const {
    fetchDataFn,
    defaultPageSize = 10,
    defaultQueryParams = {}
  } = options

  // 表格数据
  const dataList = ref([])
  const loading = ref(false)
  const total = ref(0)

  // 选择相关
  const ids = ref([])
  const single = ref(true)
  const multiple = ref(true)

  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: defaultPageSize,
    ...defaultQueryParams
  })

  // 获取列表数据
  const getList = async () => {
    if (!fetchDataFn) {
      console.warn('useTable: fetchDataFn is required')
      return
    }

    loading.value = true
    try {
      const res = await fetchDataFn(queryParams)
      dataList.value = res.rows || []
      total.value = res.total || 0
    } catch (error) {
      console.error('获取列表数据失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const handleQuery = () => {
    queryParams.pageNum = 1
    getList()
  }

  // 重置搜索
  const resetQuery = (formRef) => {
    if (formRef) {
      formRef.resetFields()
    }
    handleQuery()
  }

  // 处理选择变化
  const handleSelectionChange = (selection, idField = 'id') => {
    ids.value = selection.map(item => item[idField])
    single.value = selection.length !== 1
    multiple.value = !selection.length
  }

  // 分页变化
  const handlePagination = ({ page, limit }) => {
    queryParams.pageNum = page
    queryParams.pageSize = limit
    getList()
  }

  return {
    // 数据
    dataList,
    loading,
    total,
    ids,
    single,
    multiple,
    queryParams,

    // 方法
    getList,
    handleQuery,
    resetQuery,
    handleSelectionChange,
    handlePagination
  }
}
