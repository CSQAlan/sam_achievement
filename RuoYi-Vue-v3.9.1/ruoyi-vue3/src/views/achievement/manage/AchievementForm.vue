<template>
  <div v-if="isPageMode" class="app-container outcome-page">
    <div class="page-header">
      <div class="page-title">{{ title }}</div>

      <div class="page-actions">
        <slot name="footer-left" :form="form"></slot>

        <el-button v-if="showSubmit && !readOnly" type="primary" @click="submitForm">
          {{ submitTextComputed }}
        </el-button>
        <el-button @click="handleCancel">{{ cancelText }}</el-button>
      </div>
    </div>

    <div class="outcome-body">
      <el-form ref="outcomeRefPage" :model="form" :rules="rules" label-width="110px" :disabled="readOnly">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-row>
              <el-col :span="12">
                <el-form-item label="类别" prop="category">
                  <el-select v-model="form.category" placeholder="请选择类别" filterable>
                    <el-option v-for="dict in achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="届次" prop="sessionId">
                  <el-input v-model="form.sessionId" placeholder="例：15" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="所属学院" prop="ownerDepId">
                  <el-tree-select v-model="form.ownerDepId" :data="deptOptions"
                                  :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId"
                                  placeholder="请选择所属学院" check-strictly />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="作品名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入作品名称(选填)" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="获奖级别" prop="level">
                  <el-select v-model="form.level" placeholder="请选择">
                    <el-option v-for="dict in award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="奖项等级" prop="grade">
                  <el-select v-model="form.grade" placeholder="请选择">
                    <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
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
                <el-form-item label="组别" prop="groupId">
                  <el-select v-model="form.groupId" placeholder="请选择组别">
                    <el-option v-for="dict in group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="证书编号" prop="certificateNo">
                  <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="获奖时间" prop="awardTime">
                  <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="团队名称" prop="teamName">
                  <el-input v-model="form.teamName" placeholder="请输入团队名称(选填)" />
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.isReimburse === 1">
                <el-form-item label="报名费" prop="fee">
                  <el-input v-model="form.fee" placeholder="请输入金额">
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
            <el-form-item label="是否申请报销" prop="isReimburse">
              <el-radio-group v-model="form.isReimburse">
                <el-radio :label="1">是 (需要上传凭证)</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-divider content-position="center">参赛选手信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!readOnly">
              <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddParticipant">添加学生</el-button></el-col>
              <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
            </el-row>
            <el-table :data="samAchievementParticipantList" :row-class-name="rowParticipantIndex" @selection-change="handleParticipantSelectionChange">
              <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
              <el-table-column label="学生学号" prop="studentId">
                <template #default="scope">
                  <el-input v-model="scope.row.studentId" placeholder="请输入学号" @blur="handleStudentBlur(scope.row)" :disabled="readOnly"/>
                </template>
              </el-table-column>
              <el-table-column label="姓名" align="center" prop="studentName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.studentName" disabled />
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="orderNo" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.orderNo" disabled />
                </template>
              </el-table-column>
              <el-table-column label="是否负责人" prop="manager" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.manager" disabled>
                    <el-option :label="'是'" :value="1" />
                    <el-option :label="'否'" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>

            <el-divider content-position="center">指导老师信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!readOnly">
              <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddAdvisor">添加老师</el-button></el-col>
              <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button></el-col>
            </el-row>
            <el-table :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
              <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
              <el-table-column label="教师工号" prop="teacherId">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherId" placeholder="请输入工号" @blur="handleTeacherBlur(scope.row)" :disabled="readOnly"/>
                </template>
              </el-table-column>
              <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherName" disabled />
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="orderNo" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.orderNo" disabled />
                </template>
              </el-table-column>
            </el-table>
          </el-col>

          <el-col :span="12">
            <div class="attach-card">
              <el-divider content-position="left">附件管理</el-divider>
              <el-tabs tab-position="left" style="height: 100%; min-height: 500px;" v-model="activeAttachmentTab">
                <el-tab-pane label="奖状(证书)" name="award">
                  <div class="upload-pane-content">
                    <el-alert title="请上传获奖证书" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileAward">
                      <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.award" class="preview-box">
                        <iframe :src="previewUrls.award" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileAward" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileAward) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileAward)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileAward = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="比赛通知" name="notice">
                  <div class="upload-pane-content">
                    <el-alert title="请上传比赛通知" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.notice" class="preview-box">
                        <iframe :src="previewUrls.notice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileNotice" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileNotice) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileNotice)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileNotice = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="参赛作品" name="work">
                  <div class="upload-pane-content">
                    <el-alert title="请上传参赛作品" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.work" class="preview-box">
                        <iframe :src="previewUrls.work" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileWork" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileWork) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileWork)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileWork = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传转账截图" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.payment" class="preview-box">
                        <iframe :src="previewUrls.payment" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.filePayment" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.filePayment) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.filePayment)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.filePayment = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传电子发票" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.invoice" class="preview-box">
                        <iframe :src="previewUrls.invoice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileInvoice" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileInvoice) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileInvoice)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileInvoice = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="收款码" name="receipt" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传收款码" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileReceiptCode">
                      <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.receipt" class="preview-box">
                        <iframe :src="previewUrls.receipt" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileReceiptCode" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileReceiptCode) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileReceiptCode)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileReceiptCode = null">删除</el-button>
                        </div>
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

  <el-dialog
      v-else
      :title="title"
      v-model="visible"
      width="1200px"
      append-to-body
      :close-on-click-modal="false"
      @close="handleCancel"
  >
    <div class="outcome-body">
      <el-form ref="outcomeRefDialog" :model="form" :rules="rules" label-width="110px" :disabled="readOnly">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-row>
              <el-col :span="12">
                <el-form-item label="类别" prop="category">
                  <el-select v-model="form.category" placeholder="请选择类别" filterable>
                    <el-option v-for="dict in achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="届次" prop="sessionId">
                  <el-input v-model="form.sessionId" placeholder="例：15" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="所属学院" prop="ownerDepId">
                  <el-tree-select v-model="form.ownerDepId" :data="deptOptions"
                                  :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId"
                                  placeholder="请选择所属学院" check-strictly />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="作品名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入作品名称(选填)" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="获奖级别" prop="level">
                  <el-select v-model="form.level" placeholder="请选择">
                    <el-option v-for="dict in award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="奖项等级" prop="grade">
                  <el-select v-model="form.grade" placeholder="请选择">
                    <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
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
                <el-form-item label="组别" prop="groupId">
                  <el-select v-model="form.groupId" placeholder="请选择组别">
                    <el-option v-for="dict in group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="证书编号" prop="certificateNo">
                  <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="获奖时间" prop="awardTime">
                  <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD"
                                  placeholder="选择日期" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <el-form-item label="团队名称" prop="teamName">
                  <el-input v-model="form.teamName" placeholder="请输入团队名称(选填)" />
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.isReimburse === 1">
                <el-form-item label="报名费" prop="fee">
                  <el-input v-model="form.fee" placeholder="请输入金额">
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
            <el-form-item label="是否申请报销" prop="isReimburse">
              <el-radio-group v-model="form.isReimburse">
                <el-radio :label="1">是 (需要上传凭证)</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-divider content-position="center">参赛选手信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!readOnly">
              <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddParticipant">添加学生</el-button></el-col>
              <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
            </el-row>
            <el-table :data="samAchievementParticipantList" :row-class-name="rowParticipantIndex" @selection-change="handleParticipantSelectionChange">
              <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
              <el-table-column label="学生学号" prop="studentId">
                <template #default="scope">
                  <el-input v-model="scope.row.studentId" placeholder="请输入学号" @blur="handleStudentBlur(scope.row)" :disabled="readOnly"/>
                </template>
              </el-table-column>
              <el-table-column label="姓名" align="center" prop="studentName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.studentName" disabled />
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="orderNo" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.orderNo" disabled />
                </template>
              </el-table-column>
              <el-table-column label="是否负责人" prop="manager" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.manager" disabled>
                    <el-option :label="'是'" :value="1" />
                    <el-option :label="'否'" :value="0" />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>

            <el-divider content-position="center">指导老师信息</el-divider>
            <el-row :gutter="10" class="mb8" v-if="!readOnly">
              <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddAdvisor">添加老师</el-button></el-col>
              <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button></el-col>
            </el-row>
            <el-table :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
              <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
              <el-table-column label="教师工号" prop="teacherId">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherId" placeholder="请输入工号" @blur="handleTeacherBlur(scope.row)" :disabled="readOnly"/>
                </template>
              </el-table-column>
              <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                <template #default="scope">
                  <el-input v-model="scope.row.teacherName" disabled />
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="orderNo" width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.orderNo" disabled />
                </template>
              </el-table-column>
            </el-table>
          </el-col>

          <el-col :span="12">
            <div class="attach-card">
              <el-divider content-position="left">附件管理</el-divider>
              <el-tabs tab-position="left" style="height: 100%; min-height: 500px;" v-model="activeAttachmentTab">
                <el-tab-pane label="奖状(证书)" name="award">
                  <div class="upload-pane-content">
                    <el-alert title="请上传获奖证书" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileAward">
                      <file-upload v-model="form.fileAward" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.award" class="preview-box">
                        <iframe :src="previewUrls.award" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileAward" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileAward) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileAward)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileAward = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="比赛通知" name="notice">
                  <div class="upload-pane-content">
                    <el-alert title="请上传比赛通知" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileNotice">
                      <file-upload v-model="form.fileNotice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.notice" class="preview-box">
                        <iframe :src="previewUrls.notice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileNotice" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileNotice) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileNotice)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileNotice = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="参赛作品" name="work">
                  <div class="upload-pane-content">
                    <el-alert title="请上传参赛作品" type="info" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileWork">
                      <file-upload v-model="form.fileWork" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.work" class="preview-box">
                        <iframe :src="previewUrls.work" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileWork" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileWork) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileWork)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileWork = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="支付记录" name="payment" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传转账截图" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="filePayment">
                      <file-upload v-model="form.filePayment" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.payment" class="preview-box">
                        <iframe :src="previewUrls.payment" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.filePayment" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.filePayment) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.filePayment)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.filePayment = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="正规发票" name="invoice" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传电子发票" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileInvoice">
                      <file-upload v-model="form.fileInvoice" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.invoice" class="preview-box">
                        <iframe :src="previewUrls.invoice" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileInvoice" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileInvoice) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileInvoice)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileInvoice = null">删除</el-button>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="收款码" name="receipt" v-if="form.isReimburse === 1">
                  <div class="upload-pane-content">
                    <el-alert title="请上传收款码" type="warning" :closable="false" class="mb10"/>
                    <el-form-item label-width="0" prop="fileReceiptCode">
                      <file-upload v-model="form.fileReceiptCode" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" v-if="!readOnly"/>
                      <div v-if="previewUrls.receipt" class="preview-box">
                        <iframe :src="previewUrls.receipt" width="100%" height="450px" frameborder="0"></iframe>
                      </div>
                      <div v-if="form.fileReceiptCode" class="custom-file-row">
                        <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form.fileReceiptCode) }}</span></div>
                        <div class="file-action">
                          <el-button link type="primary" :icon="Download" @click="handleDownload(form.fileReceiptCode)">下载</el-button>
                          <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form.fileReceiptCode = null">删除</el-button>
                        </div>
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
      <div class="dialog-footer-wrapper">
        <div class="footer-left">
          <slot name="footer-left" :form="form"></slot>
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

<script setup name="AchievementForm">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted, watch } from "vue";
import { Plus, Delete, Document, Download } from "@element-plus/icons-vue";
// ✅ API 路径修正
import { listStudent } from "@/api/achievement/student";
import { listTeacher } from "@/api/achievement/teacher";
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const emit = defineEmits(["ok", "cancel"]);

// ==========================================
// Props 定义：复用 AchievementForm
// ==========================================
const props = defineProps({
  // CRUD
  getFn: { type: Function, required: false },
  addFn: { type: Function, required: false },
  updateFn: { type: Function, required: false },

  // ????
  auditSource: { type: String, default: "" },

  // 模式：pageMode=true 为全屏，false 为弹窗
  pageMode: { type: Boolean, default: false },

  // 标题
  titleAdd: { type: String, default: "新增成果" },
  titleEdit: { type: String, default: "修改成果" },

  // 只读/提交控制
  readOnly: { type: Boolean, default: false },
  showSubmit: { type: Boolean, default: true },
  submitText: { type: String, default: "" },
  cancelText: { type: String, default: "取 消" },
});

// ✅ 字典：使用 index.vue 中的正确字典
const { achievement_category, group_type, award_rank, award_level_type, college_audit_status, school_audit_status } = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type', 'college_audit_status', 'school_audit_status');

const auditSource = computed(() => String(props.auditSource || "").toLowerCase());
const auditEnabled = computed(() => auditSource.value.includes("unreviewed"));
const isCollegeAudit = computed(() => auditSource.value.includes("college"));
const isSchoolAudit = computed(() => auditSource.value.includes("school"));

const selectedAuditStatus = ref("");
const rejectReason = ref("");

const currentAuditDict = computed(() => {
  if (isCollegeAudit.value) return college_audit_status.value || [];
  if (isSchoolAudit.value) return school_audit_status.value || [];
  return [];
});

const nextStatusOptions = computed(() => {
  const options = currentAuditDict.value || [];
  if (isCollegeAudit.value) return options.filter(d => ["1", "2"].includes(String(d.value)));
  if (isSchoolAudit.value) return options.filter(d => ["0", "1"].includes(String(d.value)));
  return [];
});

const isCollegeReject = computed(() => isCollegeAudit.value && String(selectedAuditStatus.value) === "1");
const isSchoolReject = computed(() => isSchoolAudit.value && String(selectedAuditStatus.value) === "0");
const showAuditSection = computed(() => auditEnabled.value);
const showRejectReason = computed(() => auditEnabled.value && (isCollegeReject.value || isSchoolReject.value));


const isPageMode = computed(() => props.pageMode);

const visible = ref(false);
const title = ref("");
const deptOptions = ref([]);
const activeAttachmentTab = ref("award");

// 表单 Ref
const outcomeRefPage = ref(null);
const outcomeRefDialog = ref(null);

// 子表数据
const samAchievementParticipantList = ref([]);
const samAchievementAdvisorList = ref([]);
const checkedParticipant = ref([]);
const checkedAdvisor = ref([]);

// 表单数据 (字段已匹配 sam_achievement)
const data = reactive({
  form: {},
  rules: {
    category: [{ required: true, message: "类别不能为空", trigger: "change" }],
    sessionId: [{ required: true, message: "届次不能为空", trigger: "blur" }],
    ownerDepId: [{ required: true, message: "所属学院不能为空", trigger: "change" }],
    level: [{ required: true, message: "级别不能为空", trigger: "change" }],
    grade: [{ required: true, message: "等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupId: [{ required: true, message: "组别不能为空", trigger: "change" }],
    certificateNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "blur" }],
    fee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }]
  }
});
const { form, rules } = toRefs(data);

function initAuditFromForm() {
  if (!auditEnabled.value) return;
  const currentStatus = form.value?.auditStatus;
  if (currentStatus !== null && currentStatus !== undefined && currentStatus !== '') {
    selectedAuditStatus.value = String(currentStatus);
  } else {
    selectedAuditStatus.value = '';
  }
  if (isCollegeAudit.value) {
    rejectReason.value = form.value?.collegeAuditReason || '';
  } else if (isSchoolAudit.value) {
    rejectReason.value = form.value?.schoolAuditReason || '';
  } else {
    rejectReason.value = '';
  }
}

watch(
  nextStatusOptions,
  () => {
    if (!auditEnabled.value) return;
    if (selectedAuditStatus.value) return;
    const options = nextStatusOptions.value || [];
    if (!options.length) {
      selectedAuditStatus.value = '';
      return;
    }
    const preferValue = isCollegeAudit.value ? '2' : '1';
    const prefer = options.find(opt => String(opt.value) === preferValue);
    selectedAuditStatus.value = String((prefer || options[0]).value);
  },
  { immediate: true }
);

watch(
  () => selectedAuditStatus.value,
  () => {
    if (!showRejectReason.value) rejectReason.value = '';
  }
);


const submitTextComputed = computed(() => {
  if (props.submitText) return props.submitText;
  return form.value?.achievementId ? "保 存" : "确 定";
});


// --- 安全预览 ---
const previewUrls = reactive({ award: "", notice: "", work: "", payment: "", invoice: "", receipt: "" });

function loadSafePreview(filePath, type) {
  if (!filePath) {
    if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
    previewUrls[type] = "";
    return;
  }
  request({
    url: '/common/download/resource',
    method: 'get',
    params: { resource: filePath },
    responseType: 'blob'
  }).then(blob => {
    if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
    previewUrls[type] = window.URL.createObjectURL(blob);
  }).catch(err => {
    console.error("预览加载失败", err);
    previewUrls[type] = "";
  });
}

// 监听文件
watch(() => form.value.fileAward, (val) => loadSafePreview(val, 'award'));
watch(() => form.value.fileNotice, (val) => loadSafePreview(val, 'notice'));
watch(() => form.value.fileWork, (val) => loadSafePreview(val, 'work'));
watch(() => form.value.filePayment, (val) => loadSafePreview(val, 'payment'));
watch(() => form.value.fileInvoice, (val) => loadSafePreview(val, 'invoice'));
watch(() => form.value.fileReceiptCode, (val) => loadSafePreview(val, 'receipt'));

// --- 对外方法 ---
function open(id) {
  if (!isPageMode.value) visible.value = true;
  reset();
  getDeptTree();
  activeAttachmentTab.value = 'award';
  initAuditFromForm();
  if (id) {
    title.value = props.titleEdit;
    loadDetail(id);
  } else {
    title.value = props.titleAdd;
  }
}

function getForm() { return form.value; }
defineExpose({ open, getForm });

onMounted(() => {
  if (isPageMode.value) {
    getDeptTree();
  }
});

function loadDetail(id) {
  if (!props.getFn) return;
  props.getFn(id).then(response => {
    const d = response.data;
    form.value = d;
    samAchievementParticipantList.value = d.samAchievementParticipantList || [];
    samAchievementAdvisorList.value = d.samAchievementAdvisorList || [];
    reIndexList(samAchievementParticipantList.value);
    reIndexList(samAchievementAdvisorList.value);

    // 附件回显
    if (d.samAchievementAttachmentList) {
      d.samAchievementAttachmentList.forEach(item => {
        if (item.type === 1) form.value.fileAward = item.fileUuid;
        if (item.type === 2) form.value.fileNotice = item.fileUuid;
        if (item.type === 3) form.value.fileWork = item.fileUuid;
        if (item.type === 4) form.value.filePayment = item.fileUuid;
        if (item.type === 5) form.value.fileInvoice = item.fileUuid;
        if (item.type === 6) form.value.fileReceiptCode = item.fileUuid;
      });
    }
    if (form.value.isReimburse == null) form.value.isReimburse = 0;
    initAuditFromForm();
  });
}

function reset() {
  form.value = {
    achievementId: null, sessionId: null, category: null, name: null, teamName: null,
    level: null, grade: null, track: null, certificateNo: null, groupId: null, ownerDepId: null,
    awardTime: null, fee: null, isReimburse: 0,
    fileAward: null, fileNotice: null, fileWork: null, filePayment: null, fileInvoice: null, fileReceiptCode: null
  };
  samAchievementParticipantList.value = [];
  samAchievementAdvisorList.value = [];
  selectedAuditStatus.value = "";
  rejectReason.value = "";
  Object.keys(previewUrls).forEach(key => {
    if (previewUrls[key]) {
      window.URL.revokeObjectURL(previewUrls[key]);
      previewUrls[key] = "";
    }
  });
  if(outcomeRefPage.value) outcomeRefPage.value.resetFields();
  if(outcomeRefDialog.value) outcomeRefDialog.value.resetFields();
}

function submitForm() {
  if (props.readOnly) return;
  const activeRef = isPageMode.value ? outcomeRefPage.value : outcomeRefDialog.value;

  activeRef.validate(valid => {
    if (valid) {
      if (form.value.isReimburse === 1) {
        if (!form.value.fileReceiptCode) {
          proxy.$modal.msgWarning("申请报销必须上传【收款码】！");
          return;
        }
        if (!form.value.fileAward && !form.value.fileInvoice) {
          proxy.$modal.msgWarning("申请报销时，请至少上传一份凭证(奖状或发票)！");
          return;
        }
      }

      let attachments = [];
      const pushFile = (type, path) => { if (path) attachments.push({ type: type, fileUuid: path }); };
      pushFile(1, form.value.fileAward);
      pushFile(2, form.value.fileNotice);
      pushFile(3, form.value.fileWork);
      pushFile(4, form.value.filePayment);
      pushFile(5, form.value.fileInvoice);
      pushFile(6, form.value.fileReceiptCode);

      form.value.samAchievementAttachmentList = attachments;
      form.value.samAchievementParticipantList = samAchievementParticipantList.value;
      form.value.samAchievementAdvisorList = samAchievementAdvisorList.value;

      const isEdit = form.value.achievementId != null;
      const apiFn = isEdit ? props.updateFn : props.addFn;

      if (apiFn) {
        apiFn(form.value).then(response => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
          if (!isPageMode.value) visible.value = false;
          emit('ok');
        });
      } else {
        proxy.$modal.msgError("未配置保存接口");
      }
    }
  });
}

function handleCancel() {
  if (!isPageMode.value) visible.value = false;
  reset();
  emit('cancel');
}

// 辅助逻辑
function getDeptTree() {
  listDept().then(response => {
    deptOptions.value = handleTree(response.data, "deptId");
  });
}

function reIndexList(list) {
  list.forEach((item, index) => {
    item.orderNo = index + 1;
    item.manager = (index === 0) ? 1 : 0;
  });
}

function handleStudentBlur(row) {
  if (!row.studentId) return;
  listStudent({ no: row.studentId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.studentName = response.rows[0].name;
    } else {
      proxy.$modal.msgWarning(`未找到学号信息`);
      row.studentName = "";
    }
  });
}

function handleTeacherBlur(row) {
  if (!row.teacherId) return;
  listTeacher({ no: row.teacherId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.teacherName = response.rows[0].teacherName;
    } else {
      proxy.$modal.msgWarning(`未找到工号信息`);
      row.teacherName = "";
    }
  });
}

function handleAddParticipant() {
  samAchievementParticipantList.value.push({ studentId: "", studentName: "", orderNo: 0, manager: 0 });
  reIndexList(samAchievementParticipantList.value);
}
function handleDeleteParticipant() {
  if (checkedParticipant.value.length == 0) return proxy.$modal.msgError("请选择删除项");
  samAchievementParticipantList.value = samAchievementParticipantList.value.filter(item => !checkedParticipant.value.includes(item));
  reIndexList(samAchievementParticipantList.value);
}
function handleParticipantSelectionChange(sel) { checkedParticipant.value = sel; }

function handleAddAdvisor() {
  samAchievementAdvisorList.value.push({ teacherId: "", teacherName: "", orderNo: 0 });
  reIndexList(samAchievementAdvisorList.value);
}
function handleDeleteAdvisor() {
  if (checkedAdvisor.value.length == 0) return proxy.$modal.msgError("请选择删除项");
  samAchievementAdvisorList.value = samAchievementAdvisorList.value.filter(item => !checkedAdvisor.value.includes(item));
  reIndexList(samAchievementAdvisorList.value);
}
function handleAdvisorSelectionChange(sel) { checkedAdvisor.value = sel; }

function getFileName(url) { return url ? url.substring(url.lastIndexOf("/") + 1) : ""; }
function handleDownload(url) { if (url) proxy.$download.resource(url); }
</script>

<style scoped>
.outcome-page .page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px;
}
.outcome-page .page-title { font-size: 18px; font-weight: 600; }
.outcome-page .page-actions { display: flex; align-items: center; gap: 10px; }
.dialog-footer-wrapper { display: flex; justify-content: space-between; align-items: center; }
.attach-card { background: #f8f8f9; padding: 10px; border-radius: 4px; border: 1px solid #d9d9d9; height: 100%; }
.upload-pane-content { padding: 20px; }
.mb10 { margin-bottom: 15px; }
.mr5 { margin-right: 5px; }
:deep(.el-tabs__content) { height: 100%; }

/* ✅ 隐藏原生列表，只显示自定义行 */
.hide-file-list :deep(.el-upload-list) { display: none !important; }

.custom-file-row {
  margin-top: 10px; display: flex; align-items: center; justify-content: space-between;
  background-color: #f5f7fa; padding: 8px 10px; border-radius: 4px; font-size: 13px; color: #606266;
  border: 1px solid #e4e7ed; transition: all 0.3s;
}
.custom-file-row:hover { background-color: #ecf5ff; }
.preview-box { margin-top: 15px; border: 1px solid #ddd; padding: 5px; background-color: #fff; }
.file-name { display: flex; align-items: center; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 280px; }
.file-action { margin-left: 10px; flex-shrink: 0; }
</style>
