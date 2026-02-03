<template>
  <!-- ========== 页面模式：铺满 ========== -->
  <div v-if="isPageMode" class="app-container outcome-page">
    <div class="page-header">
      <div class="page-title">{{ title }}</div>

      <div class="page-actions">
        <!-- ✅ 外置工具条插槽（审阅页会用） -->
        <slot name="footer-left"></slot>

        <el-button v-if="showSubmit && !readOnly" type="primary" @click="submitForm">
          {{ submitTextComputed }}
        </el-button>
        <el-button @click="handleCancel">{{ cancelText }}</el-button>
      </div>
    </div>

    <!-- ✅ 主体表单：编译期模板（页面模式） -->
    <div class="outcome-body">
      <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px" :disabled="props.readOnly">
        <el-row :gutter="20">
          <!-- 左侧 -->
          <el-col :span="12">
            <el-row>
              <el-col :span="12">
                <el-form-item label="所属年份" prop="year">
                  <el-input v-model="form.year" placeholder="例：2026" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属学院" prop="deptId">
                  <el-tree-select
                      v-model="form.deptId"
                      :data="deptOptions"
                      :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                      value-key="deptId"
                      placeholder="请选择所属学院"
                      check-strictly
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="成果类别" prop="category">
                  <el-select v-model="form.category" placeholder="请选择成果类别" clearable>
                    <el-option
                        v-for="dict in erp_outcome_category"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="负责人姓名" prop="workName">
                  <el-input v-model="form.workName" placeholder="系统将自动使用队长姓名" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="级别类型" prop="levelType">
                  <el-select v-model="form.levelType" placeholder="请选择(如国家级)">
                    <el-option
                        v-for="dict in erp_award_level_type"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="奖项等级" prop="awardLevel">
                  <el-select v-model="form.awardLevel" placeholder="请选择(如一等奖)">
                    <el-option
                        v-for="dict in erp_award_rank"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="赛道" prop="track">
                  <el-input v-model="form.track" placeholder="请输入赛道" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="组别" prop="groupType">
                  <el-select v-model="form.groupType" placeholder="请选择组别">
                    <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="证书编号" prop="certNo">
                  <el-input v-model="form.certNo" placeholder="请输入证书编号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="获奖时间" prop="awardTime">
                  <el-date-picker
                      clearable
                      v-model="form.awardTime"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="选择日期"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="提交时间" prop="submitTime">
                  <el-date-picker
                      clearable
                      v-model="form.submitTime"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="选择日期"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>

              <el-col :span="12" v-if="form.isReimburse === 1">
                <el-form-item label="报名费" prop="entryFee">
                  <el-input v-model="form.entryFee" placeholder="请输入金额(元)">
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">报销申请</el-divider>
            <el-form-item label="是否申请报销" prop="isReimburse">
              <el-radio-group v-model="form.isReimburse">
                <el-radio :label="1">是 (需要上传凭证)</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-divider content-position="center">参赛选手信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
              <el-col :span="1.5">
                <el-button type="primary" :icon="Plus" @click="handleAddErpContestant">添加学生</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" :icon="Delete" @click="handleDeleteErpContestant">删除选中</el-button>
              </el-col>
            </el-row>

            <el-table
                :data="erpContestantList"
                :row-class-name="rowErpContestantIndex"
                @selection-change="handleErpContestantSelectionChange"
            >
              <el-table-column v-if="!props.readOnly" type="selection" width="50" align="center" />

              <el-table-column label="学生学号" prop="studentCode">
                <template #default="scope">
                  <el-input
                      v-model="scope.row.studentCode"
                      placeholder="请输入学号"
                      :disabled="props.readOnly"
                      @blur="handleStudentCodeBlur(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="姓名" align="center" prop="studentName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.studentName" placeholder="姓名" :disabled="props.readOnly" />
                </template>
              </el-table-column>

              <el-table-column label="排序" prop="sortOrder" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.sortOrder" disabled style="width: 100%" />
                </template>
              </el-table-column>

              <el-table-column label="是否队长" prop="isLeader" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.isLeader" disabled>
                    <el-option label="是 (队长)" :value="1" />
                    <el-option label="否 (队员)" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>

            <el-divider content-position="center">指导老师信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
              <el-col :span="1.5">
                <el-button type="primary" :icon="Plus" @click="handleAddTeacher">添加老师</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" :icon="Delete" @click="handleDeleteTeacher">删除选中</el-button>
              </el-col>
            </el-row>

            <el-table :data="guideTeacherList" @selection-change="handleTeacherSelectionChange">
              <el-table-column v-if="!props.readOnly" type="selection" width="50" align="center" />

              <el-table-column label="教师工号" prop="teacherCode">
                <template #default="scope">
                  <el-input
                      v-model="scope.row.teacherCode"
                      placeholder="请输入工号"
                      :disabled="props.readOnly"
                      @blur="handleTeacherCodeBlur(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherName" placeholder="姓名" :disabled="props.readOnly" />
                </template>
              </el-table-column>

              <el-table-column label="排序" prop="sortOrder" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.sortOrder" disabled style="width: 100%" />
                </template>
              </el-table-column>

              <el-table-column label="是否第一指导" prop="isLeader" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.isLeader" disabled>
                    <el-option label="是" :value="1" />
                    <el-option label="否" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>
          </el-col>

          <!-- 右侧：附件 -->
          <el-col :span="12">
            <div class="attach-card">
              <el-divider content-position="left">附件管理</el-divider>

              <el-tabs tab-position="left" style="height: 100%; min-height: 500px" v-model="activeAttachmentTab">
                <el-tab-pane label="奖状(证书)" name="award">
                  <div class="upload-pane-content">
                    <el-alert title="请上传获奖证书或奖状扫描件" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileAward">
                      <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileAward" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileAward)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="比赛通知" name="notice">
                  <div class="upload-pane-content">
                    <el-alert title="请上传官方发布的比赛通知文件" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileNotice" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileNotice)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="参赛照片" name="work">
                  <div class="upload-pane-content">
                    <el-alert title="请上传参赛照片或作品文档" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="20" :fileType="['pdf']" />
                      <div v-if="form.fileWork" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileWork)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传报名费转账截图/回单" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.filePayment" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.filePayment)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传正规电子发票" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileInvoice" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileInvoice)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="收款码" name="receipt" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传用于接收报销款的收款码" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileReceiptCode">
                      <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileReceiptCode" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileReceiptCode)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>

  <!-- ========== 弹窗模式：dialog（保留且完整） ========== -->
  <el-dialog v-else :title="title" v-model="visible" width="1200px" append-to-body @close="handleCancel">
    <div class="outcome-body">
      <!-- ✅ 主体表单：编译期模板（弹窗模式） -->
      <el-form ref="outcomeRef" :model="form" :rules="rules" label-width="110px" :disabled="props.readOnly">
        <el-row :gutter="20">
          <!-- 左侧 -->
          <el-col :span="12">
            <el-row>
              <el-col :span="12">
                <el-form-item label="所属年份" prop="year">
                  <el-input v-model="form.year" placeholder="例：2026" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属学院" prop="deptId">
                  <el-tree-select
                      v-model="form.deptId"
                      :data="deptOptions"
                      :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                      value-key="deptId"
                      placeholder="请选择所属学院"
                      check-strictly
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="成果类别" prop="category">
                  <el-select v-model="form.category" placeholder="请选择成果类别" clearable>
                    <el-option
                        v-for="dict in erp_outcome_category"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="负责人姓名" prop="workName">
                  <el-input v-model="form.workName" placeholder="系统将自动使用队长姓名" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="级别类型" prop="levelType">
                  <el-select v-model="form.levelType" placeholder="请选择(如国家级)">
                    <el-option
                        v-for="dict in erp_award_level_type"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="奖项等级" prop="awardLevel">
                  <el-select v-model="form.awardLevel" placeholder="请选择(如一等奖)">
                    <el-option
                        v-for="dict in erp_award_rank"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="赛道" prop="track">
                  <el-input v-model="form.track" placeholder="请输入赛道" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="组别" prop="groupType">
                  <el-select v-model="form.groupType" placeholder="请选择组别">
                    <el-option v-for="dict in erp_group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="证书编号" prop="certNo">
                  <el-input v-model="form.certNo" placeholder="请输入证书编号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="获奖时间" prop="awardTime">
                  <el-date-picker
                      clearable
                      v-model="form.awardTime"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="选择日期"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="提交时间" prop="submitTime">
                  <el-date-picker
                      clearable
                      v-model="form.submitTime"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="选择日期"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>

              <el-col :span="12" v-if="form.isReimburse === 1">
                <el-form-item label="报名费" prop="entryFee">
                  <el-input v-model="form.entryFee" placeholder="请输入金额(元)">
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">报销申请</el-divider>
            <el-form-item label="是否申请报销" prop="isReimburse">
              <el-radio-group v-model="form.isReimburse">
                <el-radio :label="1">是 (需要上传凭证)</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-divider content-position="center">参赛选手信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
              <el-col :span="1.5">
                <el-button type="primary" :icon="Plus" @click="handleAddErpContestant">添加学生</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" :icon="Delete" @click="handleDeleteErpContestant">删除选中</el-button>
              </el-col>
            </el-row>

            <el-table
                :data="erpContestantList"
                :row-class-name="rowErpContestantIndex"
                @selection-change="handleErpContestantSelectionChange"
            >
              <el-table-column v-if="!props.readOnly" type="selection" width="50" align="center" />

              <el-table-column label="学生学号" prop="studentCode">
                <template #default="scope">
                  <el-input
                      v-model="scope.row.studentCode"
                      placeholder="请输入学号"
                      :disabled="props.readOnly"
                      @blur="handleStudentCodeBlur(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="姓名" align="center" prop="studentName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.studentName" placeholder="姓名" :disabled="props.readOnly" />
                </template>
              </el-table-column>

              <el-table-column label="排序" prop="sortOrder" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.sortOrder" disabled style="width: 100%" />
                </template>
              </el-table-column>

              <el-table-column label="是否队长" prop="isLeader" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.isLeader" disabled>
                    <el-option label="是 (队长)" :value="1" />
                    <el-option label="否 (队员)" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>

            <el-divider content-position="center">指导老师信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!props.readOnly">
              <el-col :span="1.5">
                <el-button type="primary" :icon="Plus" @click="handleAddTeacher">添加老师</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" :icon="Delete" @click="handleDeleteTeacher">删除选中</el-button>
              </el-col>
            </el-row>

            <el-table :data="guideTeacherList" @selection-change="handleTeacherSelectionChange">
              <el-table-column v-if="!props.readOnly" type="selection" width="50" align="center" />

              <el-table-column label="教师工号" prop="teacherCode">
                <template #default="scope">
                  <el-input
                      v-model="scope.row.teacherCode"
                      placeholder="请输入工号"
                      :disabled="props.readOnly"
                      @blur="handleTeacherCodeBlur(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherName" placeholder="姓名" :disabled="props.readOnly" />
                </template>
              </el-table-column>

              <el-table-column label="排序" prop="sortOrder" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.sortOrder" disabled style="width: 100%" />
                </template>
              </el-table-column>

              <el-table-column label="是否第一指导" prop="isLeader" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.isLeader" disabled>
                    <el-option label="是" :value="1" />
                    <el-option label="否" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>
          </el-col>

          <!-- 右侧：附件 -->
          <el-col :span="12">
            <div class="attach-card">
              <el-divider content-position="left">附件管理</el-divider>

              <el-tabs tab-position="left" style="height: 100%; min-height: 500px" v-model="activeAttachmentTab">
                <el-tab-pane label="奖状(证书)" name="award">
                  <div class="upload-pane-content">
                    <el-alert title="请上传获奖证书或奖状扫描件" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileAward">
                      <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileAward" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileAward)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="比赛通知" name="notice">
                  <div class="upload-pane-content">
                    <el-alert title="请上传官方发布的比赛通知文件" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileNotice" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileNotice)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="参赛照片" name="work">
                  <div class="upload-pane-content">
                    <el-alert title="请上传参赛照片或作品文档" type="info" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="20" :fileType="['pdf']" />
                      <div v-if="form.fileWork" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileWork)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传报名费转账截图/回单" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.filePayment" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.filePayment)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传正规电子发票" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileInvoice" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileInvoice)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="收款码" name="receipt" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传用于接收报销款的收款码" type="warning" :closable="false" class="mb10" />
                    <el-form-item label-width="0" prop="fileReceiptCode">
                      <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" />
                      <div v-if="form.fileReceiptCode" class="preview-wrapper">
                        <el-popover placement="left" :width="600" trigger="hover">
                          <template #reference>
                            <el-button type="primary" link :icon="View">预览文件内容</el-button>
                          </template>
                          <iframe :src="getPdfUrl(form.fileReceiptCode)" width="100%" height="500px" frameborder="0"></iframe>
                        </el-popover>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer" style="display:flex; align-items:center; justify-content:space-between;">
        <div class="footer-left">
          <slot name="footer-left"></slot>
        </div>

        <div class="footer-right">
          <el-button v-if="showSubmit && !readOnly" type="primary" @click="submitForm">
            {{ submitTextComputed }}
          </el-button>
          <el-button @click="handleCancel">{{ cancelText }}</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="BaseOutcomeDialog">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted } from "vue";
import { Plus, Delete, View } from "@element-plus/icons-vue";
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import request from "@/utils/request";

const { proxy } = getCurrentInstance();
const emit = defineEmits(["ok", "cancel"]);

/**
 * ✅ 兼容两种命名
 * - pageMode（你审阅页现在在用）
 * - asPage（之前版本用过）
 */
const props = defineProps({
  // CRUD
  getFn: { type: Function, required: true },
  addFn: { type: Function, required: true },
  updateFn: { type: Function, required: true },

  // 形态
  pageMode: { type: Boolean, default: false },
  asPage: { type: Boolean, default: false },

  // 标题
  titleAdd: { type: String, default: "新增成果" },
  titleEdit: { type: String, default: "修改成果" },

  // 只读/提交控制
  readOnly: { type: Boolean, default: false },
  showSubmit: { type: Boolean, default: true },
  submitText: { type: String, default: "" },
  cancelText: { type: String, default: "取 消" },

  // 可选：强制 auditStatus（比如未审核页固定“待审核”）
  fixedAuditStatus: { type: [String, Number], default: null }
});

// ✅ 字典（基础字段仍用这些 + 成果类别）
const { erp_outcome_category, erp_group_type, erp_award_level_type, erp_award_rank } = proxy.useDict(
    "erp_outcome_category",
    "erp_group_type",
    "erp_award_level_type",
    "erp_award_rank"
);

const isPageMode = computed(() => props.pageMode || props.asPage);

const visible = ref(false);
const title = ref("");
const deptOptions = ref([]);
const activeAttachmentTab = ref("award");

// ✅ 关键：表单 ref 必须用 script-setup 自己的 ref，不能用 proxy.$refs
const outcomeRef = ref(null);

// 子表
const erpContestantList = ref([]);
const guideTeacherList = ref([]);
const checkedErpContestant = ref([]);
const checkedTeacher = ref([]);

// 表单
const data = reactive({
  form: {},
  rules: {
    year: [{ required: true, message: "所属年份不能为空", trigger: "blur" }],
    deptId: [{ required: true, message: "所属学院不能为空", trigger: "change" }],
    category: [{ required: true, message: "成果类别不能为空", trigger: "change" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "change" }],
    certNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    levelType: [{ required: true, message: "级别类型不能为空", trigger: "change" }],
    awardLevel: [{ required: true, message: "奖项等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupType: [{ required: true, message: "组别不能为空", trigger: "change" }],
    entryFee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额(最多2位小数)", trigger: "blur" }]
  }
});
const { form, rules } = toRefs(data);

const submitTextComputed = computed(() => {
  if (props.submitText) return props.submitText;
  return form.value?.id ? "保 存" : "确 定";
});

/** =========================
 *  ✅ 对外暴露：open / getForm
 * ========================= */
function open(id) {
  if (!isPageMode.value) visible.value = true;

  reset();
  getDeptTree();
  activeAttachmentTab.value = "award";

  if (id) {
    title.value = props.titleEdit;
    loadDetail(id);
  } else {
    title.value = props.titleAdd;
    if (props.fixedAuditStatus != null) form.value.auditStatus = String(props.fixedAuditStatus);
  }
}

function getForm() {
  return form.value;
}
defineExpose({ open, getForm });

onMounted(() => {
  if (isPageMode.value) {
    getDeptTree();
    title.value = props.titleAdd;
    reset();
  }
});

function loadDetail(id) {
  props.getFn(id).then((response) => {
    const d = response?.data || {};
    form.value = d;

    erpContestantList.value = d.erpContestantList || [];
    guideTeacherList.value = d.guideTeacherList || [];

    reIndexList(erpContestantList.value);
    reIndexList(guideTeacherList.value);

    if (form.value.attachmentList) {
      form.value.attachmentList.forEach((item) => {
        if (item.attachType === "award") form.value.fileAward = item.filePath;
        if (item.attachType === "notice") form.value.fileNotice = item.filePath;
        if (item.attachType === "work") form.value.fileWork = item.filePath;
        if (item.attachType === "payment") form.value.filePayment = item.filePath;
        if (item.attachType === "invoice") form.value.fileInvoice = item.fileInvoice || item.filePath;
        if (item.attachType === "receipt_code") form.value.fileReceiptCode = item.filePath;
      });
    }

    if (form.value.isReimburse == null) form.value.isReimburse = 0;
    if (props.fixedAuditStatus != null) form.value.auditStatus = String(props.fixedAuditStatus);

    syncLeaderToWorkName();
  });
}

function reset() {
  form.value = {
    id: null,
    year: null,
    category: null,
    workName: null,
    levelType: null,
    awardLevel: null,
    track: null,
    certNo: null,
    groupType: null,
    deptId: null,
    awardTime: null,
    submitTime: null,
    entryFee: null,

    auditStatus: form.value?.auditStatus ?? null,
    isReimburse: 0,

    fileAward: null,
    fileNotice: null,
    fileWork: null,
    filePayment: null,
    fileInvoice: null,
    fileReceiptCode: null,

    attachmentList: [],
    remark: null
  };

  if (props.fixedAuditStatus != null) form.value.auditStatus = String(props.fixedAuditStatus);

  erpContestantList.value = [];
  guideTeacherList.value = [];
  checkedErpContestant.value = [];
  checkedTeacher.value = [];

  // ✅ resetFields 走自己的 ref
  outcomeRef.value?.resetFields?.();
}

/** =========================
 * ✅ 队长绑定规则：排序1=队长
 * ========================= */
function reIndexList(list) {
  list.forEach((item, index) => {
    item.sortOrder = index + 1;
    item.isLeader = index === 0 ? 1 : 0;
  });
}

function syncLeaderToWorkName() {
  const leader =
      (erpContestantList.value || []).find((r) => String(r.isLeader) === "1") || erpContestantList.value?.[0];
  if (leader?.studentName) form.value.workName = leader.studentName;
}

/** =========================
 * ✅ 自动回填（不再依赖不存在的 api 文件）
 * 默认先按 ruoyi 常见：/erp/student/list /erp/teacher/list
 * ========================= */
function listStudent(params) {
  return request({
    url: "/erp/student/list",
    method: "get",
    params
  });
}
function listTeacher(params) {
  return request({
    url: "/erp/teacher/list",
    method: "get",
    params
  });
}

function handleStudentCodeBlur(row) {
  if (!row?.studentCode) return;
  listStudent({ studentCode: row.studentCode })
      .then((res) => {
        if (res?.rows?.length) row.studentName = res.rows[0].name || res.rows[0].studentName || res.rows[0].nickName;
        else proxy.$modal?.msgWarning?.(`未找到学号为 ${row.studentCode} 的信息，请手动输入姓名`);
        syncLeaderToWorkName();
      })
      .catch(() => {
        proxy.$modal?.msgWarning?.("学生信息接口不可用，请手动输入姓名（不影响保存）");
      });
}

function handleTeacherCodeBlur(row) {
  if (!row?.teacherCode) return;
  listTeacher({ teacherCode: row.teacherCode })
      .then((res) => {
        if (res?.rows?.length) row.teacherName = res.rows[0].name || res.rows[0].teacherName || res.rows[0].nickName;
        else proxy.$modal?.msgWarning?.(`未找到工号为 ${row.teacherCode} 的信息，请手动输入姓名`);
      })
      .catch(() => {
        proxy.$modal?.msgWarning?.("老师信息接口不可用，请手动输入姓名（不影响保存）");
      });
}

/** =========================
 * ✅ 提交：打包附件 + 子表
 * ========================= */
async function submitForm() {
  if (props.readOnly) return;

  if (!outcomeRef.value) {
    proxy.$modal?.msgError?.("表单未渲染完成，请稍后再试");
    return;
  }

  try {
    await outcomeRef.value.validate();
  } catch {
    return;
  }

  syncLeaderToWorkName();

  if (form.value.isReimburse === 1) {
    if (!form.value.fileAward && !form.value.fileInvoice) {
      proxy.$modal?.msgWarning?.("申请报销时，请至少上传一份凭证文件！");
      return;
    }
  }

  const attachments = [];
  const pushFile = (type, path) => {
    if (path) attachments.push({ attachType: type, filePath: path });
  };
  pushFile("award", form.value.fileAward);
  pushFile("notice", form.value.fileNotice);
  pushFile("work", form.value.fileWork);
  pushFile("payment", form.value.filePayment);
  pushFile("invoice", form.value.fileInvoice);
  pushFile("receipt_code", form.value.fileReceiptCode);

  form.value.attachmentList = attachments;
  form.value.erpContestantList = erpContestantList.value;
  form.value.guideTeacherList = guideTeacherList.value;

  if (props.fixedAuditStatus != null) form.value.auditStatus = String(props.fixedAuditStatus);

  const isEdit = form.value.id != null;
  const fn = isEdit ? props.updateFn : props.addFn;

  if (typeof fn !== "function") {
    proxy.$modal?.msgError?.(isEdit ? "updateFn 未传入或不是函数" : "addFn 未传入或不是函数");
    return;
  }

  fn(form.value)
      .then(() => {
        proxy.$modal?.msgSuccess?.(isEdit ? "修改成功" : "新增成功");
        if (!isPageMode.value) visible.value = false;
        emit("ok");
      })
      .catch(() => {});
}

function handleCancel() {
  if (!isPageMode.value) visible.value = false;
  reset();
  emit("cancel");
}

/** 部门树 */
function getDeptTree() {
  listDept({}).then((res) => {
    deptOptions.value = handleTree(res.data, "deptId");
  });
}

/** 子表：学生 */
function rowErpContestantIndex({ row, rowIndex }) {
  row.index = rowIndex + 1;
}
function handleAddErpContestant() {
  if (props.readOnly) return;
  erpContestantList.value.push({ studentCode: "", studentName: "", sortOrder: 0, isLeader: 0 });
  reIndexList(erpContestantList.value);
  syncLeaderToWorkName();
}
function handleDeleteErpContestant() {
  if (props.readOnly) return;
  if (checkedErpContestant.value.length === 0) return proxy.$modal?.msgError?.("请先选择要删除的学生数据");
  const checked = checkedErpContestant.value;
  erpContestantList.value = erpContestantList.value.filter((item) => checked.indexOf(item.index) === -1);
  reIndexList(erpContestantList.value);
  syncLeaderToWorkName();
}
function handleErpContestantSelectionChange(selection) {
  checkedErpContestant.value = selection.map((item) => item.index);
}

/** 子表：老师 */
function handleAddTeacher() {
  if (props.readOnly) return;
  guideTeacherList.value.push({ teacherCode: "", teacherName: "", sortOrder: 0, isLeader: 0 });
  reIndexList(guideTeacherList.value);
}
function handleDeleteTeacher() {
  if (props.readOnly) return;
  if (checkedTeacher.value.length === 0) return proxy.$modal?.msgError?.("请先选择要删除的老师数据");
  guideTeacherList.value = guideTeacherList.value.filter((item) => !checkedTeacher.value.includes(item));
  reIndexList(guideTeacherList.value);
}
function handleTeacherSelectionChange(selection) {
  checkedTeacher.value = selection;
}

/** PDF 预览 */
function getPdfUrl(url) {
  if (!url) return "";
  let fullUrl = url;
  if (url.indexOf("http") === -1 && url.indexOf("https") === -1) {
    fullUrl = import.meta.env.VITE_APP_BASE_API + url;
  }
  return fullUrl + "#toolbar=0&navpanes=0&view=FitH";
}
</script>

<style scoped>
.outcome-page .page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.outcome-page .page-title {
  font-size: 18px;
  font-weight: 600;
}
.outcome-page .page-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.attach-card {
  background: #f8f8f9;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  height: 100%;
}

.upload-pane-content {
  padding: 20px;
}
.mb10 {
  margin-bottom: 15px;
}
.preview-wrapper {
  margin-top: 10px;
}
:deep(.el-tabs__content) {
  height: 100%;
}
</style>
