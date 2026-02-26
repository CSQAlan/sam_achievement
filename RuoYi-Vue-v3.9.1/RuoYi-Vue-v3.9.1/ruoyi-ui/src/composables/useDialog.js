/**
 * 对话框通用逻辑 Composable
 * 用于处理弹窗的打开、关闭、提交等逻辑
 */
import { ref, computed } from 'vue'

export function useDialog(options = {}) {
  const {
    title = '对话框',
    addTitle = '新增',
    editTitle = '编辑',
    onOpen,
    onClose,
    onSubmit
  } = options

  const visible = ref(false)
  const formData = ref({})
  const isEdit = ref(false)
  const loading = ref(false)

  // 动态标题
  const dialogTitle = computed(() => {
    if (typeof title === 'function') {
      return title(isEdit.value)
    }
    return isEdit.value ? editTitle : addTitle
  })

  // 打开对话框
  const open = async (data = null) => {
    visible.value = true
    isEdit.value = !!data

    if (data) {
      formData.value = { ...data }
    } else {
      formData.value = {}
    }

    if (onOpen) {
      await onOpen(formData.value, isEdit.value)
    }
  }

  // 关闭对话框
  const close = () => {
    visible.value = false
    formData.value = {}
    isEdit.value = false

    if (onClose) {
      onClose()
    }
  }

  // 提交表单
  const submit = async (formRef) => {
    if (!formRef) {
      console.warn('useDialog: formRef is required for submit')
      return
    }

    try {
      await formRef.validate()
      loading.value = true

      if (onSubmit) {
        await onSubmit(formData.value, isEdit.value)
      }

      close()
      return true
    } catch (error) {
      console.error('表单验证失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    visible,
    formData,
    isEdit,
    loading,
    dialogTitle,
    open,
    close,
    submit
  }
}
