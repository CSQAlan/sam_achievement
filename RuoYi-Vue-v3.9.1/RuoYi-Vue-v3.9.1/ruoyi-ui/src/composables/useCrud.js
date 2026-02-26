/**
 * CRUD 通用逻辑 Composable
 * 整合增删改查的完整逻辑
 */
import { getCurrentInstance } from 'vue'
import { useTable } from './useTable'
import { useDialog } from './useDialog'

export function useCrud(options = {}) {
  const {
    // API 方法
    listApi,
    getApi,
    addApi,
    updateApi,
    deleteApi,
    exportApi,

    // 配置
    defaultQueryParams = {},
    dialogOptions = {},
    tableOptions = {},

    // 回调
    beforeAdd,
    afterAdd,
    beforeEdit,
    afterEdit,
    beforeDelete,
    afterDelete
  } = options

  const { proxy } = getCurrentInstance()

  // 使用表格逻辑
  const table = useTable({
    fetchDataFn: listApi,
    defaultQueryParams,
    ...tableOptions
  })

  // 使用对话框逻辑
  const dialog = useDialog({
    ...dialogOptions,
    onOpen: async (formData, isEdit) => {
      if (isEdit && getApi) {
        const id = formData.id
        const res = await getApi(id)
        Object.assign(formData, res.data)
      }

      if (isEdit && beforeEdit) {
        await beforeEdit(formData)
      } else if (!isEdit && beforeAdd) {
        await beforeAdd(formData)
      }
    },
    onSubmit: async (formData, isEdit) => {
      try {
        if (isEdit) {
          await updateApi(formData)
          proxy.$modal.msgSuccess('修改成功')
          if (afterEdit) await afterEdit(formData)
        } else {
          await addApi(formData)
          proxy.$modal.msgSuccess('新增成功')
          if (afterAdd) await afterAdd(formData)
        }
        table.getList()
      } catch (error) {
        throw error
      }
    }
  })

  // 新增
  const handleAdd = () => {
    dialog.open()
  }

  // 修改
  const handleUpdate = (row) => {
    const id = row?.id || table.ids.value[0]
    if (!id) {
      proxy.$modal.msgWarning('请选择要修改的数据')
      return
    }
    dialog.open({ id })
  }

  // 删除
  const handleDelete = async (row) => {
    const deleteIds = row?.id ? [row.id] : table.ids.value

    if (!deleteIds.length) {
      proxy.$modal.msgWarning('请选择要删除的数据')
      return
    }

    try {
      await proxy.$modal.confirm(`是否确认删除编号为"${deleteIds}"的数据项？`)

      if (beforeDelete) {
        await beforeDelete(deleteIds)
      }

      await deleteApi(deleteIds)
      proxy.$modal.msgSuccess('删除成功')

      if (afterDelete) {
        await afterDelete(deleteIds)
      }

      table.getList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除失败:', error)
      }
    }
  }

  // 导出
  const handleExport = () => {
    if (!exportApi) {
      proxy.$modal.msgWarning('导出功能未配置')
      return
    }

    const filename = `export_${new Date().getTime()}.xlsx`
    proxy.download(exportApi, { ...table.queryParams }, filename)
  }

  return {
    // 表格相关
    ...table,

    // 对话框相关
    dialog,

    // CRUD 操作
    handleAdd,
    handleUpdate,
    handleDelete,
    handleExport
  }
}
