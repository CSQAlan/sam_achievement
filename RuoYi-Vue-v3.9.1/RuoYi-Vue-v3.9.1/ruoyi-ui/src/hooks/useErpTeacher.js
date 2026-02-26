/**
 * ERP 教师相关业务逻辑 Hook
 */
import { ref } from 'vue'
import { getTeacher } from '@/api/erp/teacher'

export function useErpTeacher() {
  const loading = ref(false)

  /**
   * 根据工号获取教师信息
   */
  const getTeacherByCode = async (teacherCode) => {
    if (!teacherCode || !teacherCode.trim()) {
      return null
    }

    loading.value = true
    try {
      const res = await getTeacher({ teacherCode: teacherCode.trim() })
      return res.data || null
    } catch (error) {
      console.error('获取教师信息失败:', error)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 自动填充教师信息
   */
  const autoFillTeacher = async (row) => {
    if (!row.teacherCode) {
      return
    }

    const teacher = await getTeacherByCode(row.teacherCode)
    if (teacher) {
      row.teacherName = teacher.teacherName || ''
      row.deptId = teacher.deptId || null
    }
  }

  return {
    loading,
    getTeacherByCode,
    autoFillTeacher
  }
}
