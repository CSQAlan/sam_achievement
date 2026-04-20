<template>
  <div class="audit-detail-page" v-loading="loading">

    <!-- 顶部基础信息卡片 -->
    <el-card shadow="never" class="info-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><DocumentChecked /></el-icon>
            <span class="header-title">业务审计详情</span>
          </div>
          <el-button link type="primary" icon="ArrowLeft" @click="goBack">返回列表</el-button>
        </div>
      </template>

      <el-descriptions :column="3" border class="base-desc">
        <el-descriptions-item label="日志ID" label-class-name="desc-label">
          <span class="mono-text">{{ log.id }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="业务类型" label-class-name="desc-label">
          <el-tag type="info" size="small">{{ log.bizType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业务名称" label-class-name="desc-label">
          <strong>{{ log.bizName }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="业务主键" label-class-name="desc-label">
          <span class="mono-text">{{ log.bizId }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作类型" label-class-name="desc-label">
          <el-tag :type="opTagType(log.opType)" size="small">{{ formatOpType(log.opType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行结果" label-class-name="desc-label">
          <el-tag :type="log.success === 1 ? 'success' : 'danger'" size="small">
            {{ log.success === 1 ? '✓ 成功' : '✗ 失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人" label-class-name="desc-label">
          {{ log.operNickName || log.operUserName }}
          <span v-if="log.operDeptName" class="dept-text">（{{ log.operDeptName }}）</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作账号" label-class-name="desc-label">
          <span class="mono-text">{{ log.operUserName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" label-class-name="desc-label">
          {{ log.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="客户端IP" label-class-name="desc-label">
          <span class="mono-text">{{ log.operIp }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="浏览器" label-class-name="desc-label">
          {{ log.operBrowser }} / {{ log.operOs }}
        </el-descriptions-item>
        <el-descriptions-item label="耗时" label-class-name="desc-label">
          <el-tag :type="log.durationMs > 2000 ? 'warning' : 'success'" size="small">
            {{ log.durationMs || 0 }} ms
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="log.errorMsg" label="错误信息" :span="3" label-class-name="desc-label">
          <span class="error-msg">{{ log.errorMsg }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 变更内容卡片 -->
    <el-card shadow="never" class="diff-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Edit /></el-icon>
            <span class="header-title">变更内容</span>
            <el-tag v-if="fieldDiffs.length" type="warning" size="small" class="count-tag">
              {{ fieldDiffs.length }} 项字段变更
            </el-tag>
            <el-tag v-if="detailRows.length" type="info" size="small" class="count-tag">
              {{ detailRows.length }} 项明细变更
            </el-tag>
          </div>
          <div class="tab-switch">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="diff">变更对比</el-radio-button>
              <el-radio-button label="before">变更前快照</el-radio-button>
              <el-radio-button label="after">变更后快照</el-radio-button>
              <el-radio-button label="request">请求参数</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <!-- 变更对比视图 -->
      <template v-if="viewMode === 'diff'">
        <el-empty v-if="!fieldDiffs.length && !detailRows.length" description="本次操作无字段变更记录" :image-size="80" />

        <!-- 字段级别变更 -->
        <template v-if="fieldDiffs.length">
          <div class="section-title">
            <el-icon><Switch /></el-icon>
            字段变更明细
          </div>
          <div class="field-diff-list">
            <div v-for="item in fieldDiffs" :key="item.path"
                 :class="['field-diff-row', `change-${item.changeType?.toLowerCase()}`]">
              <div class="field-label">
                <el-icon class="change-icon">
                  <Plus v-if="item.changeType === 'ADDED'" />
                  <Minus v-else-if="item.changeType === 'REMOVED'" />
                  <Edit v-else />
                </el-icon>
                <span>{{ item.fieldLabel || item.field }}</span>
                <el-tag :type="changeTagType(item.changeType)" size="small" class="change-badge">
                  {{ formatChangeType(item.changeType) }}
                </el-tag>
              </div>
              <div class="field-values">
                <div class="value-before" v-if="item.changeType !== 'ADDED'">
                  <span class="value-badge before-badge">前</span>
                  <span class="value-text">{{ item.beforeText || item.before || '（空）' }}</span>
                </div>
                <div class="value-arrow" v-if="item.changeType === 'UPDATED'">
                  <el-icon><ArrowRight /></el-icon>
                </div>
                <div class="value-after" v-if="item.changeType !== 'REMOVED'">
                  <span class="value-badge after-badge">后</span>
                  <span class="value-text">{{ item.afterText || item.after || '（空）' }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- 明细项变更（数组对比） -->
        <template v-if="detailRows.length">
          <div class="section-title" style="margin-top: 20px">
            <el-icon><List /></el-icon>
            明细项变更
          </div>
          <div class="detail-list">
            <el-collapse accordion>
              <el-collapse-item v-for="row in detailRows" :key="row.id" :name="row.id">
                <template #title>
                  <div class="detail-item-header">
                    <el-tag :type="changeTagType(row.changeType)" size="small">
                      {{ formatChangeType(row.changeType) }}
                    </el-tag>
                    <span class="detail-item-name">
                      {{ row.itemName || row.itemKey || '未知数据' }}
                      <span class="detail-item-id" v-if="row.itemBizId && row.itemBizId !== row.itemName">
                        (ID: {{ row.itemBizId }})
                      </span>
                    </span>
                  </div>
                </template>
                <!-- 明细内部字段变更 -->
                <div v-if="getDetailFieldDiffs(row).length" class="detail-inner-diffs">
                  <div v-for="f in getDetailFieldDiffs(row)" :key="f.path"
                       :class="['inner-diff-row', `change-${f.changeType?.toLowerCase()}`]">
                    <span class="inner-label">{{ f.fieldLabel || f.field }}</span>
                    <span class="inner-before" v-if="f.changeType !== 'ADDED'">{{ f.beforeText || f.before || '（空）' }}</span>
                    <el-icon v-if="f.changeType === 'UPDATED'" class="inner-arrow"><ArrowRight /></el-icon>
                    <span class="inner-after" v-if="f.changeType !== 'REMOVED'">{{ f.afterText || f.after || '（空）' }}</span>
                  </div>
                </div>
                <!-- 整项新增或删除：直接展示对象快照代码 -->
                <div v-else-if="row.changeType === 'ADDED' || row.changeType === 'REMOVED'" class="detail-json-view">
                  <pre class="json-pre mini-pre" v-html="highlightJson(parseJson(row.changeType === 'ADDED' ? row.afterJson : row.beforeJson))"></pre>
                </div>
                <el-empty v-else description="该条目无字段级差异" :image-size="40" />
              </el-collapse-item>
            </el-collapse>
          </div>
        </template>
      </template>

      <!-- 快照/请求参数 浅色代码视图 -->
      <template v-else>
        <div class="snapshot-viewer">
          <div class="snapshot-toolbar">
            <span class="snapshot-title">
              <el-icon><Document /></el-icon>
              {{ viewTitleMap[viewMode] }}
            </span>
            <el-button size="small" :icon="CopyDocument" @click="copyContent" :disabled="!getCurrentJson()">
              复制
            </el-button>
          </div>
          <div class="snapshot-body">
            <pre v-if="getCurrentJson()" class="json-pre" v-html="highlightJson(getCurrentJson())"></pre>
            <div v-else class="snapshot-empty">
              <el-icon><InfoFilled /></el-icon>
              暂无数据
            </div>
          </div>
        </div>
      </template>
    </el-card>

  </div>
</template>

<script setup name="BizAuditDetail">
import { getBizAudit } from "@/api/monitor/bizAudit"
import { ElMessage } from "element-plus"
import { DocumentChecked, Edit, Switch, ArrowRight, ArrowLeft, Plus, Minus, List, Document, CopyDocument, InfoFilled } from "@element-plus/icons-vue"


const route = useRoute()
const router = useRouter()

const loading = ref(true)
const viewMode = ref("diff")
const log = ref({})
const detailRows = ref([])
const fieldDiffs = ref([])

const viewTitleMap = { before: '变更前快照', after: '变更后快照', request: '请求参数' }

const opTypeMap = {
  ADD:    { label: "新增",   type: "success"  },
  CREATE: { label: "创建",   type: "success"  },
  UPDATE: { label: "修改",   type: "warning"  },
  DELETE: { label: "删除",   type: "danger"   },
  APPROVE:{ label: "审核通过",type: "success"  },
  REJECT: { label: "审核驳回",type: "danger"   },
  IMPORT: { label: "导入",   type: "info"     },
  EXPORT: { label: "导出",   type: "info"     },
  BATCH:  { label: "批量操作",type: "warning"  },
  OTHER:  { label: "其他",   type: "info"     }
}

function formatOpType(value) {
  return opTypeMap[value]?.label || value
}

function opTagType(value) {
  return opTypeMap[value]?.type || "info"
}

function formatChangeType(type) {
  const map = { ADDED: "新增", REMOVED: "删除", UPDATED: "修改", MODIFIED: "修改" }
  return map[type] || type
}

function changeTagType(type) {
  const map = { ADDED: "success", REMOVED: "danger", UPDATED: "warning", MODIFIED: "warning" }
  return map[type] || "info"
}

function getDetailFieldDiffs(row) {
  try {
    const diffObj = typeof row.diffJson === "string" ? JSON.parse(row.diffJson) : row.diffJson
    return diffObj?.fieldDiffs || []
  } catch (e) {
    return []
  }
}

function getViewContent() {
  const viewMap = {
    before:  log.value.beforeJson,
    after:   log.value.afterJson,
    request: log.value.requestParams
  }
  return pretty(viewMap[viewMode.value])
}

function getCurrentJson() {
  const viewMap = {
    before:  log.value.beforeJson,
    after:   log.value.afterJson,
    request: log.value.requestParams
  }
  return parseJson(viewMap[viewMode.value])
}

async function copyContent() {
  const json = getCurrentJson()
  if (!json) return
  try {
    await navigator.clipboard.writeText(JSON.stringify(json, null, 2))
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    ElMessage.error('复制失败，请手动选择')
  }
}

/**
 * 将 JSON 对象转为带语法着色 span 的 HTML 字符串（专业编辑器风格）
 * 增加了行号显示支持
 */
function highlightJson(obj) {
  if (!obj) return ''
  const jsonStr = JSON.stringify(obj, null, 2)
  const escaped = jsonStr
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // 语法着色正则
  const colored = escaped.replace(
    /("(?:[^"\\]|\\.)*"\s*:)|("(?:[^"\\]|\\.)*")|(true|false)|(null)|(-?\d+(?:\.\d+)?(?:[eE][+-]?\d+)?)/g,
    (match, key, str, bool, nil, num) => {
      if (key) return `<span class="jh-key">${match}</span>`
      if (str) return `<span class="jh-str">${match}</span>`
      if (bool) return `<span class="jh-bool">${match}</span>`
      if (nil) return `<span class="jh-null">${match}</span>`
      if (num) return `<span class="jh-num">${match}</span>`
      return match
    }
  )

  // 按行分割并添加行号
  const lines = colored.split('\n')
  return lines.map((line, index) => {
    return `<div class="json-line"><span class="line-num">${index + 1}</span><span class="line-content">${line || ' '}</span></div>`
  }).join('')
}

function parseJson(text) {
  if (!text) return null
  try { return typeof text === "string" ? JSON.parse(text) : text }
  catch (e) { return null }
}

function pretty(text) {
  const json = parseJson(text)
  return json ? JSON.stringify(json, null, 2) : (text || "（暂无数据）")
}

function goBack() {
  router.back()
}

function getInfo() {
  loading.value = true
  getBizAudit(route.params.id).then(res => {
    log.value = res.log || {}
    detailRows.value = res.details || []
    const diff = parseJson(log.value.diffJson)
    fieldDiffs.value = diff?.fieldDiffs || []
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

getInfo()
</script>

<style scoped>
.audit-detail-page {
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.header-icon {
  color: var(--el-color-primary);
  font-size: 18px;
}
.header-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
.count-tag {
  margin-left: 4px;
}

/* 描述列表 */
:deep(.desc-label) {
  width: 90px;
  font-weight: 500;
  background: var(--el-fill-color-light) !important;
}
.mono-text {
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.dept-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.error-msg {
  color: var(--el-color-danger);
  font-size: 13px;
}

/* 标题分隔 */
.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

/* 字段变更行 */
.field-diff-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.field-diff-row {
  border-radius: 8px;
  padding: 10px 14px;
  border-left: 4px solid transparent;
  background: var(--el-fill-color-extra-light);
  transition: background 0.2s;
}
.field-diff-row:hover {
  background: var(--el-fill-color-light);
}
.field-diff-row.change-added {
  border-left-color: var(--el-color-success);
  background: #f0fdf4;
}
.field-diff-row.change-removed {
  border-left-color: var(--el-color-danger);
  background: #fff5f5;
}
.field-diff-row.change-updated {
  border-left-color: var(--el-color-warning);
  background: #fffbeb;
}

.field-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}
.change-icon {
  font-size: 14px;
}
.change-badge {
  margin-left: auto;
}

.field-values {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.value-before,
.value-after {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 160px;
}
.value-badge {
  display: inline-block;
  padding: 1px 7px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}
.before-badge {
  background: #fee2e2;
  color: #dc2626;
}
.after-badge {
  background: #dcfce7;
  color: #16a34a;
}
.value-text {
  font-size: 13px;
  color: var(--el-text-color-regular);
  word-break: break-all;
}
.value-arrow {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

/* 明细项折叠 */
.detail-list {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
}
.detail-item-header {
  display: flex;
  align-items: center;
  gap: 10px;
}
.detail-item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

/* 明细内部字段 */
.detail-inner-diffs {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;
}
.inner-diff-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 13px;
  background: var(--el-fill-color-extra-light);
}
.inner-diff-row.change-added    { background: #f0fdf4; }
.inner-diff-row.change-removed  { background: #fff5f5; }
.inner-diff-row.change-updated,
.inner-diff-row.change-modified { background: #fffbeb; }

.inner-label {
  font-weight: 600;
  color: var(--el-text-color-primary);
  min-width: 80px;
  flex-shrink: 0;
}
.inner-before {
  color: #dc2626;
  text-decoration: line-through;
  opacity: 0.8;
}
.inner-after {
  color: #16a34a;
  font-weight: 500;
}
.inner-arrow {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

/* 快照结构化视图容器 */
.snapshot-viewer {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
  overflow: hidden;
}
.snapshot-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.snapshot-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
.snapshot-body {
  padding: 0;
  background: #fff;
  min-height: 120px;
  max-height: 600px;
  overflow-y: auto;
  font-size: 13px;
  line-height: normal;
}
.snapshot-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 120px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  padding: 20px;
}


/* json-pre 语法高亮代码区 */
.json-pre {
  margin: 0;
  padding: 0;
  background: #ffffff;
  border: none;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  line-height: 20px;
  white-space: pre;
  overflow-x: auto;
  color: #24292e;
}

.json-line {
  display: flex;
  min-width: 100%;
}
.json-line:hover {
  background-color: #f1f8ff;
}

.line-num {
  width: 45px;
  min-width: 45px;
  padding-right: 12px;
  text-align: right;
  color: #bbb;
  user-select: none;
  background-color: #fafbfc;
  border-right: 1px solid #e1e4e8;
  margin-right: 12px;
}

.line-content {
  padding-right: 20px;
}

/* 明细项内部的 mini-pre 样式微调，使其在折叠卡片中更美观 */
.mini-pre {
  background: #fafbfc;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  line-height: 18px;
  padding: 8px 0;
  margin: 8px 0;
  max-height: 380px;
}
.mini-pre .line-num {
  background-color: transparent;
  width: 35px;
  min-width: 35px;
}

/* 语法着色风格 —— GitHub Light 风格 */
:deep(.jh-key)  { color: #005cc5; font-weight: 500; }   /* 键名 */
:deep(.jh-str)  { color: #032f62; }                      /* 字符串 */
:deep(.jh-num)  { color: #e36209; }                      /* 数字 */
:deep(.jh-bool) { color: #d73a49; font-weight: 600; }   /* 布尔值 */
:deep(.jh-null) { color: #6a737d; font-style: italic; } /* null */

/* 切换按钮区 */
.tab-switch {
  flex-shrink: 0;
}

/* 卡片间距 */
.info-card,
.diff-card {
  border-radius: 8px;
}
</style>


