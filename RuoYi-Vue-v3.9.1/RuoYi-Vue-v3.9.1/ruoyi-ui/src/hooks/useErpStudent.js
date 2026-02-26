/**
 * ERP 学生相关业务逻辑 Hook
 */
import { ref } from 'vue'
import { getStudent } from '@/api/erp/student'

export function useErpStudent() {
  const loading = ref(false)

  /**
   * 根据学号获取学生信息
   */
  const getStudentByCode = async (studentCode) => {
    if (!studentCode || !studentCode.trim()) {
      return null
    }

    loading.value = true
    try {
      const res = await getStudent({ studentCode: studentCode.trim() })
      return res.data || null
    } catch (error) {
      console.error('获取学生信息失败:', error)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 自动填充学生信息
   */
  const autoFillStudent = async (row) => {
    if (!row.studentCode) {
      return
    }

    const student = await getStudentByCode(row.studentCode)
    if (student) {
      row.studentName = student.studentName || ''
      row.deptId = student.deptId || null
    }
  }

  return {
    loading,
    getStudentByCode,
    autoFillStudent
  }
}
