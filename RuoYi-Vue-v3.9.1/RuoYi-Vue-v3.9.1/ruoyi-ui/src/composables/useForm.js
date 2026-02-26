/**
 * 表单通用逻辑 Composable
 * 用于处理表单验证、重置、提交等逻辑
 */
import { ref, reactive, nextTick } from 'vue'

export function useForm(options = {}) {
  const {
    initialValues = {},
    rules = {},
    onSubmit,
    onReset
  } = options

  const formRef = ref(null)
  const formData = reactive({ ...initialValues })
  const formRules = reactive(rules)
  const loading = ref(false)

  // 重置表单
  const resetForm = async () => {
    if (formRef.value) {
      formRef.value.resetFields()
    }

    Object.assign(formData, initialValues)

    if (onReset) {
      await onReset()
    }
  }

  // 清空验证
  const clearValidate = (props) => {
    if (formRef.value) {
      formRef.value.clearValidate(props)
    }
  }

  // 验证表单
  const validate = async () => {
    if (!formRef.value) {
      console.warn('useForm: formRef is not ready')
      return false
    }

    try {
      await formRef.value.validate()
      return true
    } catch (error) {
      return false
    }
  }

  // 验证指定字段
  const validateField = async (props) => {
    if (!formRef.value) {
      return false
    }

    try {
      await formRef.value.validateField(props)
      return true
    } catch (error) {
      return false
    }
  }

  // 提交表单
  const submitForm = async () => {
    const isValid = await validate()
    if (!isValid) {
      return false
    }

    loading.value = true
    try {
      if (onSubmit) {
        await onSubmit(formData)
      }
      return true
    } catch (error) {
      console.error('表单提交失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 设置表单值
  const setFormData = (data) => {
    Object.assign(formData, data)
  }

  // 获取表单值
  const getFormData = () => {
    return { ...formData }
  }

  return {
    formRef,
    formData,
    formRules,
    loading,
    resetForm,
    clearValidate,
    validate,
    validateField,
    submitForm,
    setFormData,
    getFormData
  }
}
