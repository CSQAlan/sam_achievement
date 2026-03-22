<template>
  <div v-if="isPageMode" class="app-container outcome-page">
    <div class="page-card">
      <div class="page-header">
        <div class="header-left">
          <div class="page-title">{{ title }}</div>
        </div>
        <div class="page-actions" v-if="!readOnly">
          <el-button v-if="showSubmit" type="primary" @click="submitForm">{{ submitTextComputed }}</el-button>
          <el-button @click="handleCancel">{{ cancelText }}</el-button>
        </div>
      </div>
      <el-divider style="margin: 10px 0 20px 0"></el-divider>
      <div class="outcome-body">
        <el-form ref="outcomeRefPage" :model="form" :rules="rules" label-width="110px" :disabled="readOnly">
          <div class="common-form-content">
             <template v-for="(_, slot) in $slots">
                <slot :name="slot"></slot>
             </template>
             <el-row :gutter="20">
                <el-col :span="12">
                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="比赛" prop="competitionId">
                        <el-select 
                          v-model="form.competitionId" 
                          placeholder="请选择赛事" 
                          filterable 
                          clearable 
                          style="width: 100%"
                          @change="handleCompetitionChange"
                        >
                          <el-option v-for="item in competitionOptions" :key="item.competitionId" :label="item.competitionName" :value="item.competitionId" />
                        </el-select>
                        <div style="margin-top: 5px; line-height: 1.2;">
                          <el-link type="primary" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                        </div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="届次" prop="sessionId">
                        <el-select 
                          v-model="form.sessionId" 
                          placeholder="请选择赛事" 
                          filterable 
                          clearable 
                          style="width: 100%"
                          :disabled="readOnly || !form.competitionId"
                        >
                          <el-option v-for="item in sessionOptions" :key="item.id" :label="item.session" :value="item.id" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="24">
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
                        <el-select v-model="form.grade" placeholder="请选择" style="width: 100%">
                          <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                        </el-select>
                        <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="12">
                      <el-form-item label="赛道" prop="track">
                        <el-input v-model="form.track" placeholder="请输入赛道" />
                        <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">例如蓝桥杯有c++，java数学竞赛有数学类与非数A等</div>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="组别" prop="groupId">
                        <el-select v-model="form.groupId" placeholder="请选择组别" style="width: 100%">
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
                        <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
                        <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
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
                        <el-input v-model="form.fee" placeholder="请输入金额"><template #append>元</template></el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
                  <el-form-item label="是否申请报销" prop="isReimburse">
                    <el-radio-group v-model="form.isReimburse">
                      <el-radio :label="1">是 (需要上传凭证)</el-radio>
                      <el-radio :label="0">否</el-radio>
                    </el-radio-group>
                    <div style="color: #F56C6C; font-size: 12px; margin-top: 5px; line-height: 1.2; font-weight: bold;">
                      如果报名者没有通过其他途径报销，请上传发票（PDF）和填写报名金额。注意：同一张发票只能报销一次
                    </div>
                  </el-form-item>

                  <el-divider content-position="center">参赛选手信息</el-divider>
                  <el-row :gutter="10" class="mb8" v-if="!readOnly">
                    <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddParticipantDialog">添加学生</el-button></el-col>
                    <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
                  </el-row>
                  <el-table ref="participantTable" :data="samAchievementParticipantList" border style="width: 100%; margin-bottom: 20px;" :row-class-name="tableRowClassName" @selection-change="handleParticipantSelectionChange">
                    <el-table-column v-if="!readOnly" width="40" align="center">
                      <template #default="scope">
                        <el-icon v-if="!scope.row.isFixed" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                      </template>
                    </el-table-column>
                    <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                    <el-table-column label="学生学号" prop="studentId" align="center" />
                    <el-table-column label="姓名" prop="studentName" align="center" />
                    <el-table-column label="排序" prop="orderNo" width="100" align="center" />
                    <el-table-column label="是否负责人" prop="manager" width="150" align="center">
                      <template #default="scope">
                        <el-tag :type="scope.row.manager == 1 ? 'success' : 'info'">{{ scope.row.manager == 1 ? '是' : '否' }}</el-tag>
                      </template>
                    </el-table-column>
                  </el-table>

                  <el-divider content-position="center">指导老师信息</el-divider>
                  <el-row :gutter="10" class="mb8" v-if="!readOnly">
                    <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddAdvisorDialog">添加老师</el-button></el-col>
                    <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button></el-col>
                  </el-row>
                  <el-table ref="advisorTable" :data="samAchievementAdvisorList" border style="width: 100%;" :row-class-name="tableRowClassName" @selection-change="handleAdvisorSelectionChange">
                   <el-table-column v-if="!readOnly" width="40" align="center">
  <template #default="scope">
    <el-icon v-if="scope.$index !== 0" class="drag-handle" style="cursor: move"><Rank /></el-icon>
  </template>
</el-table-column>
                    <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                    <el-table-column label="教师工号" prop="teacherId" align="center" />
                    <el-table-column label="姓名" prop="teacherName" align="center" />
                    <el-table-column label="排序" prop="orderNo" width="100" align="center" />
                  </el-table>
                </el-col>

                <el-col :span="12">
                  <div class="attach-card">
                    <el-divider content-position="left">附件管理</el-divider>
                    <el-tabs tab-position="left" style="height: 100%; min-height: 700px;" v-model="activeAttachmentTab">
                      <el-tab-pane v-for="item in visibleAttachments" :key="item.name" :label="item.label" :name="item.name">
                        <div class="upload-pane-content">
                          <el-alert v-if="!form[item.prop]" :type="item.type || 'info'" :closable="false" class="mb10">
                            <template #title>
                              {{ item.alert }} 
                              <el-button link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                            </template>
                          </el-alert>
                          
                          <el-form-item label-width="0" :prop="item.prop">
                            <file-upload 
                              v-if="!readOnly && !form[item.prop] && uploadUnlocked[item.name]"
                              v-model="form[item.prop]" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" :upload-url="uploadUrl" 
                            />
                            <div v-if="!readOnly && !form[item.prop] && !uploadUnlocked[item.name]" class="fake-upload-box" @click="handlePreUpload(item.name)">
                               <el-icon :size="30" color="#C0C4CC"><UploadFilled /></el-icon>
                               <div style="color: #606266; margin-top: 10px">点击上传文件</div>
                               <div style="font-size: 12px; color: #E6A23C; margin-top: 5px">(点击后需先阅读示例)</div>
                            </div>
                            <div v-if="previewUrls[item.name]" class="preview-box">
                              <iframe :src="previewUrls[item.name]" width="100%" height="650px" frameborder="0"></iframe>
                            </div>
                            <div v-if="form[item.prop]" class="custom-file-row">
                              <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form[item.prop]) }}</span></div>
                              <div class="file-action">
                                <el-button link type="primary" :disabled="false" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                                <el-button link type="primary" :disabled="false" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
                                <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form[item.prop] = null">删除</el-button>
                              </div>
                            </div>
                          </el-form-item>
                        </div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </el-col>
             </el-row>
          </div>
        </el-form>
      </div>
    </div>
  </div>

  <el-dialog
    v-else
    :title="title"
    v-model="visible"
    width="1200px"
    append-to-body
    :close-on-click-modal="false"
    :before-close="handleBeforeClose"
    @closed="reset"
    top="5vh"
  >
    <div class="outcome-body">
      <el-form ref="outcomeRefDialog" :model="form" :rules="rules" label-width="110px" :disabled="readOnly">
        <el-row :gutter="20">
            <el-col :span="12">
              <el-row>
                <el-col :span="24">
                 <el-form-item label="比赛" prop="competitionId">
                    <el-select 
                      v-model="form.competitionId" 
                      placeholder="请选择赛事" 
                      filterable 
                      clearable 
                      style="width: 100%"
                      @change="handleCompetitionChange"
                    >
                      <el-option v-for="item in competitionOptions" :key="item.competitionId" :label="item.competitionName" :value="item.competitionId" />
                    </el-select>
                    <div style="margin-top: 5px; line-height: 1.2;">
                      <el-link type="primary" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="届次" prop="sessionId">
                    <el-select 
                      v-model="form.sessionId" 
                      placeholder="请先选择赛事" 
                      filterable 
                      clearable 
                      style="width: 100%"
                      :disabled="readOnly || !form.competitionId"
                    >
                      <el-option v-for="item in sessionOptions" :key="item.id" :label="item.session" :value="item.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
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
                    <el-select v-model="form.grade" placeholder="请选择" style="width: 100%">
                      <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                    </el-select>
                    <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="赛道" prop="track">
                    <el-input v-model="form.track" placeholder="请输入赛道" />
                    <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">例如蓝桥杯有c++，java数学竞赛有数学类与非数A等</div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="组别" prop="groupId">
                    <el-select v-model="form.groupId" placeholder="请选择组别" style="width: 100%">
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
                    <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
                    <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
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
                    <el-input v-model="form.fee" placeholder="请输入金额"><template #append>元</template></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
              <el-form-item label="是否申请报销" prop="isReimburse">
                <el-radio-group v-model="form.isReimburse">
                  <el-radio :label="1">是 (需要上传凭证)</el-radio>
                  <el-radio :label="0">否</el-radio>
                </el-radio-group>
                <div style="color: #F56C6C; font-size: 12px;font-weight: bold;">
                  如果报名者没有通过其他途径报销，请上传发票（PDF）和填写报名金额。注意：同一张发票只能报销一次
                </div>
              </el-form-item>

              <el-divider content-position="center">参赛选手信息</el-divider>
              <el-row :gutter="10" class="mb8" v-if="!readOnly">
                <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddParticipantDialog">添加学生</el-button></el-col>
                <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
              </el-row>
              <el-table ref="participantTableDialog" :data="samAchievementParticipantList" border style="width: 100%; margin-bottom: 20px;" :row-class-name="tableRowClassName" @selection-change="handleParticipantSelectionChange">
                <el-table-column v-if="!readOnly" width="40" align="center">
                  <template #default="scope">
                    <el-icon v-if="scope.row.manager !== 1" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                  </template>
                </el-table-column>
                <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                <el-table-column label="学生学号" prop="studentId" align="center" />
                <el-table-column label="姓名" prop="studentName" align="center" />
                <el-table-column label="排序" prop="orderNo" width="100" align="center" />
                <el-table-column label="是否负责人" prop="manager" width="150" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.manager == 1 ? 'success' : 'info'">{{ scope.row.manager == 1 ? '是' : '否' }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>

              <el-divider content-position="center">指导老师信息</el-divider>
              <el-row :gutter="10" class="mb8" v-if="!readOnly">
                <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddAdvisorDialog">添加老师</el-button></el-col>
                <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button></el-col>
              </el-row>
              <el-table ref="advisorTableDialog" :data="samAchievementAdvisorList" border style="width: 100%;" :row-class-name="tableRowClassName" @selection-change="handleAdvisorSelectionChange">
             <el-table-column v-if="!readOnly" width="40" align="center">
  <template #default="scope">
    <el-icon v-if="scope.$index !== 0" class="drag-handle" style="cursor: move"><Rank /></el-icon>
  </template>
</el-table-column>
                <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                <el-table-column label="教师工号" prop="teacherId" align="center" />
                <el-table-column label="姓名" prop="teacherName" align="center" />
                <el-table-column label="排序" prop="orderNo" width="100" align="center" />
              </el-table>
            </el-col>

            <el-col :span="12">
              <div class="attach-card">
                <el-divider content-position="left">附件管理</el-divider>
                <el-tabs tab-position="left" style="height: 100%; min-height: 700px;" v-model="activeAttachmentTab">
                  <el-tab-pane v-for="item in visibleAttachments" :key="item.name" :label="item.label" :name="item.name">
                    <div class="upload-pane-content">
                      <el-alert v-if="!form[item.prop]" :type="item.type || 'info'" :closable="false" class="mb10">
                        <template #title>
                          {{ item.alert }} 
                          <el-button link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                        </template>
                      </el-alert>
                      
                      <el-form-item label-width="0" :prop="item.prop">
                        <file-upload 
                          v-if="!readOnly && !form[item.prop] && uploadUnlocked[item.name]"
                          v-model="form[item.prop]" :limit="1" :fileSize="10" :fileType="['pdf']" class="hide-file-list" :upload-url="uploadUrl" 
                        />
                        <div v-if="!readOnly && !form[item.prop] && !uploadUnlocked[item.name]" class="fake-upload-box" @click="handlePreUpload(item.name)">
                           <el-icon :size="30" color="#C0C4CC"><UploadFilled /></el-icon>
                           <div style="color: #606266; margin-top: 10px">点击上传文件</div>
                           <div style="font-size: 12px; color: #E6A23C; margin-top: 5px">(点击后需先阅读示例)</div>
                        </div>
                        <div v-if="previewUrls[item.name]" class="preview-box">
                          <iframe :src="previewUrls[item.name]" width="100%" height="650px" frameborder="0"></iframe>
                        </div>
                        <div v-if="form[item.prop]" class="custom-file-row">
                          <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form[item.prop]) }}</span></div>
                          <div class="file-action">
                            <el-button link type="primary" :disabled="false" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                            <el-button link type="primary" :disabled="false" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
                            <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form[item.prop] = null">删除</el-button>
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

  <el-dialog
    v-model="exampleVisible"
    title="上传文件要求与示例"
    width="900px"
    append-to-body
    destroy-on-close
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <div style="height: 650px; display: flex; flex-direction: column;">
      <el-alert title="请务必参考以下示例整理您的文件，确认无误后点击下方按钮解锁上传功能。" type="error" :closable="false" class="mb10" show-icon />
      
      <div style="flex: 1; border: 1px solid #dcdfe6; background: #f5f7fa; overflow: hidden; position: relative;">
        <iframe v-if="currentExampleUrl" :src="currentExampleUrl" width="100%" height="100%" frameborder="0"></iframe>
        <div v-else style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #909399;">
          <el-icon size="50"><Document /></el-icon>
          <p style="margin-top: 10px;">该类别暂无示例文件，请直接上传。</p>
        </div>
      </div>
    </div>
    <template #footer>
      <div style="text-align: center; margin-top: 10px;">
        <el-button type="primary" size="large" @click="confirmExampleKnown" style="width: 250px; font-weight: bold;">
          我已阅读示例，继续上传
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog title="添加参赛选手" v-model="addParticipantVisible" width="500px" append-to-body :close-on-click-modal="false">
    <el-form ref="addParticipantRef" :model="participantForm" :rules="addParticipantRules" label-width="80px">
      <el-form-item label="查找学生">
        <el-input v-model="participantSearchKeyword" placeholder="输入学号或姓名后回车搜索" @keyup.enter="handleParticipantSearch" clearable>
          <template #append>
            <el-button @click="handleParticipantSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <div style="font-size: 12px; color: #909399; margin-top: 5px;">支持学号或姓名双向查找</div>
      </el-form-item>
      <el-form-item label="学号" prop="studentId">
        <el-input v-model="participantForm.studentId" placeholder="学号" :disabled="!isParticipantNew" />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input v-model="participantForm.studentName" placeholder="姓名" :disabled="!isParticipantNew" />
      </el-form-item>
      
      <template v-if="isParticipantNew">
        <el-alert title="未匹配到该学号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="所属机构" prop="school">
          <el-cascader
            v-model="participantDeptCascaderValue"
            :options="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            placeholder="请选择学院/院系/专业"
            clearable
            filterable
            style="width: 100%"
            @change="handleParticipantCascaderChange"
          />
        </el-form-item>
        <el-form-item label="班级" prop="class_name">
          <el-input v-model="participantForm.class_name" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="年级" prop="class_year">
          <el-input v-model="participantForm.class_year" placeholder="请输入年级 (例如: 2022)" />
        </el-form-item>
      </template>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitAddParticipant">确 定</el-button>
        <el-button @click="addParticipantVisible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog title="添加指导老师" v-model="addAdvisorVisible" width="500px" append-to-body :close-on-click-modal="false">
    <el-form ref="addAdvisorRef" :model="advisorForm" :rules="addAdvisorRules" label-width="80px">
      <el-form-item label="查找老师">
        <el-input v-model="advisorSearchKeyword" placeholder="输入工号或姓名后回车搜索" @keyup.enter="handleAdvisorSearch" clearable>
          <template #append>
            <el-button @click="handleAdvisorSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <div style="font-size: 12px; color: #909399; margin-top: 5px;">支持工号或姓名双向查找</div>
      </el-form-item>
      <el-form-item label="工号" prop="teacherId">
        <el-input v-model="advisorForm.teacherId" placeholder="工号" :disabled="!isAdvisorNew" />
      </el-form-item>
      <el-form-item label="姓名" prop="teacherName">
        <el-input v-model="advisorForm.teacherName" placeholder="姓名" :disabled="!isAdvisorNew" />
      </el-form-item>
      
      <template v-if="isAdvisorNew">
        <el-alert title="未匹配到该工号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="所属机构" prop="school">
          <el-cascader
            v-model="advisorDeptCascaderValue"
            :options="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            placeholder="请选择学院/院系"
            clearable
            filterable
            style="width: 100%"
            @change="handleAdvisorCascaderChange"
          />
        </el-form-item>
      </template>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitAddAdvisor">确 定</el-button>
        <el-button @click="addAdvisorVisible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog title="选择学生" v-model="studentSelectVisible" width="600px" append-to-body>
    <el-table :data="studentOptions" @row-click="selectStudent" border highlight-current-row>
      <el-table-column label="学号" prop="no" align="center" />
      <el-table-column label="姓名" prop="name" align="center" />
    </el-table>
  </el-dialog>

  <el-dialog title="选择老师" v-model="teacherSelectVisible" width="600px" append-to-body>
    <el-table :data="teacherOptions" @row-click="selectTeacher" border highlight-current-row>
      <el-table-column label="工号" prop="no" align="center" />
      <el-table-column label="姓名" prop="teacherName" align="center" />
    </el-table>
  </el-dialog>
</template>

<script setup name="AchievementForm">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted, watch, nextTick } from "vue";
import { useRoute } from "vue-router";
import { onBeforeRouteLeave } from "vue-router";
import Sortable from "sortablejs";
import useUserStore from "@/store/modules/user";
import { Plus, Delete, Document, Download, View, UploadFilled, Rank } from "@element-plus/icons-vue";
import {
  listStudent,
  getStudent,
  delStudent,
  addStudent,
  updateStudent,
} from "@/api/achievement/student";
import { listTeacher, addTeacher } from "@/api/achievement/teacher";
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import request from '@/utils/request';
import FileUpload from '@/components/FileUpload';

const { proxy } = getCurrentInstance();
const route = useRoute();
const emit = defineEmits(["ok", "cancel"]);

const props = defineProps({
  getFn: { type: Function, default: null },
  addFn: { type: Function, default: null },
  updateFn: { type: Function, default: null },
  pageMode: { type: Boolean, default: false },
  readOnly: { type: Boolean, default: false },
  showSubmit: { type: Boolean, default: true },
  titleAdd: { type: String, default: "新增成果" },
  titleEdit: { type: String, default: "修改成果" },
  submitText: { type: String, default: "" },
  cancelText: { type: String, default: "取 消" },
  userRole: { type: String, default: "student" },
  sourceMode: { type: String, default: "" },
});

const { achievement_category, group_type, award_rank, award_level_type, attach_type } = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type', 'attach_type');
const isPageMode = computed(() => props.pageMode);
const visible = ref(false);
const title = ref("");
const deptOptions = ref([]);
const outcomeRefPage = ref(null);
const outcomeRefDialog = ref(null);
const activeAttachmentTab = ref('award');

const uploadUrl = ref("/dev-api/attachment/upload");
const userStore = useUserStore();

const samAchievementParticipantList = ref([]);
const samAchievementAdvisorList = ref([]);
const participantTable = ref(null);
const advisorTable = ref(null);
const participantTableDialog = ref(null);
const advisorTableDialog = ref(null);
const checkedParticipant = ref([]);
const checkedAdvisor = ref([]);
const competitionOptions = ref([]);
const sessionOptions = ref([]);

const studentSelectVisible = ref(false);
const studentOptions = ref([]);
const participantSearchKeyword = ref("");

const teacherSelectVisible = ref(false);
const teacherOptions = ref([]);
const advisorSearchKeyword = ref("");

const participantDeptCascaderValue = ref([]);
const advisorDeptCascaderValue = ref([]);

function handleParticipantCascaderChange(value) {
  if (value && value.length > 0) {
    participantForm.value.school = value[0] || '';
    participantForm.value.department = value[1] || '';
    participantForm.value.major = value[2] || '';
  } else {
    participantForm.value.school = '';
    participantForm.value.department = '';
    participantForm.value.major = '';
  }
}

function handleAdvisorCascaderChange(value) {
  if (value && value.length > 0) {
    advisorForm.value.school = value[0] || '';
    advisorForm.value.department = value[1] || '';
  } else {
    advisorForm.value.school = '';
    advisorForm.value.department = '';
  }
}

const data = reactive({
  form: { competitionId: null },
  formSnapshot: "",
  rules: {
    competitionId: [{ required: true, message: "比赛不能为空", trigger: "change" }],
    sessionId: [{ required: true, message: "届次不能为空", trigger: "change" }],
    level: [{ required: true, message: "级别不能为空", trigger: "change" }],
    grade: [{ required: true, message: "等级不能为空", trigger: "change" }],
    track: [{ required: true, message: "赛道不能为空", trigger: "blur" }],
    groupId: [{ required: true, message: "组别不能为空", trigger: "change" }],
    certificateNo: [{ required: true, message: "证书编号不能为空", trigger: "blur" }],
    awardTime: [{ required: true, message: "获奖时间不能为空", trigger: "blur" }],
    fee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }]
  }
});
const { form, formSnapshot, rules } = toRefs(data);

const validateCertificateNo = (rule, value, callback) => {
  if (!value) {
    callback();
  } else {
    const params = {
      certificateNo: value,
      competitionId: form.value.competitionId,
      achievementId: form.value.achievementId
    };
    request({
      url: '/achievement/manage/checkCertificateNoUnique',
      method: 'get',
      params: params
    }).then(response => {
      if (response.data === false) {
        callback(new Error("证书编号已存在"));
      } else {
        callback();
      }
    });
  }
};

rules.value.certificateNo = [
  { required: true, message: "证书编号不能为空", trigger: "blur" },
  { validator: validateCertificateNo, trigger: "blur" }
];

// 草稿功能逻辑
const DRAFT_KEY_PREFIX = "ACHIEVEMENT_DRAFT_";
const getDraftKey = () => DRAFT_KEY_PREFIX + route.path;

/** 关闭当前页面或弹窗的通用逻辑 */
function closeCurrentView() {
  if (isPageMode.value) {
    reset();
    if (proxy.$tab) {
      proxy.$tab.closePage(route);
    } else {
      proxy.$router.back();
    }
    emit('cancel');
  } else {
    visible.value = false;
    emit('cancel');
  }
}

function saveDraft(silent = false) {
  const draftData = {
    form: form.value,
    participants: samAchievementParticipantList.value,
    advisors: samAchievementAdvisorList.value
  };
  localStorage.setItem(getDraftKey(), JSON.stringify(draftData));
  
  if (!silent) {
    proxy.$modal.msgSuccess("草稿已保存到本地");
    // 更新快照以避免触发离开时的修改检测
    updateSnapshot();
    closeCurrentView();
  }
}

function loadDraft() {
  const draft = localStorage.getItem(getDraftKey());
  if (draft) {
    const draftData = JSON.parse(draft);
    form.value = { ...form.value, ...draftData.form };
    samAchievementParticipantList.value = draftData.participants || [];
    samAchievementAdvisorList.value = draftData.advisors || [];
    
    if (form.value.competitionId) {
      getSessionList(form.value.competitionId);
    }
    updateSnapshot();
    proxy.$modal.msgSuccess("草稿已恢复");
  }
}

function clearDraft() {
  localStorage.removeItem(getDraftKey());
}

function checkDraft() {
  if (localStorage.getItem(getDraftKey())) {
    proxy.$modal.confirm('检测到您有未完成的草稿，是否恢复？', "提示", {
      confirmButtonText: "恢复草稿",
      cancelButtonText: "开启新表单",
      type: "info"
    }).then(() => {
      loadDraft();
    }).catch(() => {
      clearDraft();
    });
  }
}

const isModified = computed(() => {
  if (props.readOnly) return false;
  const currentData = {
    form: form.value,
    participants: samAchievementParticipantList.value,
    advisors: samAchievementAdvisorList.value
  };
  return JSON.stringify(currentData) !== formSnapshot.value;
});

function updateSnapshot() {
  const currentData = {
    form: form.value,
    participants: samAchievementParticipantList.value,
    advisors: samAchievementAdvisorList.value
  };
  formSnapshot.value = JSON.stringify(currentData);
}

const submitTextComputed = computed(() => {
  if (props.submitText) return props.submitText;
  return form.value?.achievementId ? "保 存" : "确 定";
});

// 学生与老师的级联选择逻辑 (查找与动态过滤)
function findDeptNode(tree, targetVal) {
  if (!tree || targetVal == null || targetVal === '') return null;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.deptId === targetVal || node.deptName === targetVal) return node;
    if (node.children && node.children.length > 0) {
      const found = findDeptNode(node.children, targetVal);
      if (found) return found;
    }
  }
  return null;
}

const participantDepartmentOptions = computed(() => {
  const node = findDeptNode(deptOptions.value, participantForm.value.school);
  return node && node.children ? node.children : [];
});

const participantMajorOptions = computed(() => {
  const node = findDeptNode(deptOptions.value, participantForm.value.department);
  return node && node.children ? node.children : [];
});

function handleParticipantSchoolChange() {
  participantForm.value.department = '';
  participantForm.value.major = '';
}

function handleParticipantDeptChange() {
  participantForm.value.major = '';
}

const advisorDepartmentOptions = computed(() => {
  const node = findDeptNode(deptOptions.value, advisorForm.value.school);
  return node && node.children ? node.children : [];
});

function handleAdvisorSchoolChange() {
  advisorForm.value.department = '';
}

// 弹窗控制与提交逻辑

const addParticipantVisible = ref(false);
const isParticipantNew = ref(false);
const participantForm = ref({ studentId: '', studentName: '', school: '', department: '', major: '', class_name: '', class_year: '' });
const addParticipantRules = {
  studentId: [{ required: true, message: "学号不能为空", trigger: "blur" }],
  studentName: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  school: [{ required: true, message: "学院不能为空", trigger: "blur" }]
};

function openAddParticipantDialog() {
  participantForm.value = { studentId: '', studentName: '', school: '', department: '', major: '', class_name: '', class_year: '' };
  participantDeptCascaderValue.value = [];
  participantSearchKeyword.value = "";
  isParticipantNew.value = false;
  addParticipantVisible.value = true;
}

const addAdvisorVisible = ref(false);
const isAdvisorNew = ref(false);
const advisorForm = ref({ teacherId: '', teacherName: '', school: '', department: '' });
const addAdvisorRules = {
  teacherId: [{ required: true, message: "工号不能为空", trigger: "blur" }],
  teacherName: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  school: [{ required: true, message: "学院不能为空", trigger: "blur" }]
};

function openAddAdvisorDialog() {
  advisorForm.value = { teacherId: '', teacherName: '', school: '', department: '' };
  advisorSearchKeyword.value = "";
  isAdvisorNew.value = false;
  addAdvisorVisible.value = true;
}

const searchingParticipant = ref(false);
function handleParticipantSearch() {
  const keyword = participantSearchKeyword.value;
  if (!keyword) return;
  
  searchingParticipant.value = true;
  // 同时按学号和姓名查
  const queryByNo = listStudent({ no: keyword });
  const queryByName = listStudent({ name: keyword });
  
  Promise.all([queryByNo, queryByName]).then(results => {
    let allStudents = [];
    results.forEach(res => {
      if (res.rows && res.rows.length > 0) {
        allStudents = allStudents.concat(res.rows);
      }
    });
    
    // 去重 (以 no 为准)
    const uniqueStudents = [];
    const ids = new Set();
    allStudents.forEach(s => {
      if (!ids.has(s.no)) {
        ids.add(s.no);
        uniqueStudents.push(s);
      }
    });

    if (uniqueStudents.length === 1) {
      applyStudentInfo(uniqueStudents[0]);
    } else if (uniqueStudents.length > 1) {
      studentOptions.value = uniqueStudents;
      studentSelectVisible.value = true;
    } else {
      isParticipantNew.value = true;
      // 模糊匹配：如果是纯数字或字母，预填到学号；否则预填到姓名
      if (/^[a-zA-Z0-9]+$/.test(keyword)) {
        participantForm.value.studentId = keyword;
        participantForm.value.studentName = "";
      } else {
        participantForm.value.studentId = "";
        participantForm.value.studentName = keyword;
      }
    }
  }).finally(() => {
    searchingParticipant.value = false;
  });
}

function applyStudentInfo(student) {
  participantForm.value.studentId = student.no;
  participantForm.value.studentName = student.name;
  participantForm.value.school = student.school;
  participantForm.value.department = student.department;
  participantForm.value.major = student.major;
  participantForm.value.class_name = student.className;
  participantForm.value.class_year = student.classYear;
  isParticipantNew.value = false;
  studentSelectVisible.value = false;
}

function selectStudent(row) {
  applyStudentInfo(row);
}

const searchingAdvisor = ref(false);
function handleAdvisorSearch() {
  const keyword = advisorSearchKeyword.value;
  if (!keyword) return;

  searchingAdvisor.value = true;
  const queryByNo = listTeacher({ no: keyword });
  const queryByName = listTeacher({ teacherName: keyword });

  Promise.all([queryByNo, queryByName]).then(results => {
    let allTeachers = [];
    results.forEach(res => {
      if (res.rows && res.rows.length > 0) {
        allTeachers = allTeachers.concat(res.rows);
      }
    });

    const uniqueTeachers = [];
    const ids = new Set();
    allTeachers.forEach(t => {
      if (!ids.has(t.no)) {
        ids.add(t.no);
        uniqueTeachers.push(t);
      }
    });

    if (uniqueTeachers.length === 1) {
      applyTeacherInfo(uniqueTeachers[0]);
    } else if (uniqueTeachers.length > 1) {
      teacherOptions.value = uniqueTeachers;
      teacherSelectVisible.value = true;
    } else {
      isAdvisorNew.value = true;
      if (/^[a-zA-Z0-9]+$/.test(keyword)) {
        advisorForm.value.teacherId = keyword;
        advisorForm.value.teacherName = "";
      } else {
        advisorForm.value.teacherId = "";
        advisorForm.value.teacherName = keyword;
      }
    }
  }).finally(() => {
    searchingAdvisor.value = false;
  });
}

function applyTeacherInfo(teacher) {
  advisorForm.value.teacherId = teacher.no;
  advisorForm.value.teacherName = teacher.teacherName;
  advisorForm.value.school = teacher.school;
  advisorForm.value.department = teacher.department;
  isAdvisorNew.value = false;
  teacherSelectVisible.value = false;
}

function selectTeacher(row) {
  applyTeacherInfo(row);
}

function submitAddParticipant() {
  // 如果还在查询中，等待一小会或者直接拦截（通常 blur 会先于 click 触发并完成请求）
  if (searchingParticipant.value) {
    setTimeout(submitAddParticipant, 300);
    return;
  }

  proxy.$refs.addParticipantRef.validate(valid => {
    if (valid) {
      const pushToList = () => {
        samAchievementParticipantList.value.push({
          studentId: participantForm.value.studentId,
          studentName: participantForm.value.studentName,
          school: participantForm.value.school,
          orderNo: samAchievementParticipantList.value.length + 1,
          manager: samAchievementParticipantList.value.length === 0 ? 1 : 0
        });
        reIndexList(samAchievementParticipantList.value, 'participant');
        addParticipantVisible.value = false;
      };

      if (isParticipantNew.value) {
        addStudent({
          no: participantForm.value.studentId,
          name: participantForm.value.studentName,
          school: participantForm.value.school,
          department: participantForm.value.department,
          major: participantForm.value.major,
          className: participantForm.value.class_name,
          classYear: participantForm.value.class_year
        }).then(() => {
          proxy.$modal.msgSuccess("学生信息录入基础库成功");
          pushToList();
        });
      } else {
        pushToList();
      }
    }
  });
}

function submitAddAdvisor() {
  if (searchingAdvisor.value) {
    setTimeout(submitAddAdvisor, 300);
    return;
  }

  proxy.$refs.addAdvisorRef.validate(valid => {
    if (valid) {
      const pushToList = () => {
        samAchievementAdvisorList.value.push({
          teacherId: advisorForm.value.teacherId,
          teacherName: advisorForm.value.teacherName,
          orderNo: samAchievementAdvisorList.value.length + 1
        });
        reIndexList(samAchievementAdvisorList.value);
        addAdvisorVisible.value = false;
      };

      if (isAdvisorNew.value) {
        addTeacher({
          no: advisorForm.value.teacherId,
          teacherName: advisorForm.value.teacherName,
          school: advisorForm.value.school,
          department: advisorForm.value.department
        }).then(() => {
          proxy.$modal.msgSuccess("教师信息录入基础库成功");
          pushToList();
        });
      } else {
        pushToList();
      }
    }
  });
}

function handleDeleteParticipant() {
  if (checkedParticipant.value.length == 0) return proxy.$modal.msgError("请选择删除项");
  if (checkedParticipant.value.some(item => item.isFixed)) {
    return proxy.$modal.msgError("默认填写的负责人无法删除");
  }
  samAchievementParticipantList.value = samAchievementParticipantList.value.filter(item => !checkedParticipant.value.includes(item));
  reIndexList(samAchievementParticipantList.value, 'participant');
}
function handleParticipantSelectionChange(sel) { checkedParticipant.value = sel; }

function handleDeleteAdvisor() {
  if (checkedAdvisor.value.length == 0) return proxy.$modal.msgError("请选择删除项");
  if (checkedAdvisor.value.some(item => item.isFixed)) {
    return proxy.$modal.msgError("默认填写的指导老师无法删除");
  }
  samAchievementAdvisorList.value = samAchievementAdvisorList.value.filter(item => !checkedAdvisor.value.includes(item));
  reIndexList(samAchievementAdvisorList.value);
}
function handleAdvisorSelectionChange(sel) { checkedAdvisor.value = sel; }
// 示例拦截与解锁
// =========================================================
const exampleVisible = ref(false);
const currentExampleUrl = ref("");
const currentUploadType = ref(""); 

const uploadUnlocked = reactive({
  award: false, notice: false, work: false, payment: false, invoice: false, receipt: false
});

const exampleMap = {
  'award': '/image/扫描文件.pdf',   
  'notice': '/image/tongzhi.pdf',   
  'payment': '/image/jilu.pdf',     
  'invoice': '/image/fapiao.pdf',   
};

function handlePreUpload(type) {
  currentUploadType.value = type;
  const fileName = exampleMap[type];
  if (fileName) {
    currentExampleUrl.value = fileName; 
    exampleVisible.value = true;
  } else {
    uploadUnlocked[type] = true;
    proxy.$modal.msgSuccess("准备上传...");
  }
}

function confirmExampleKnown() {
  if (currentUploadType.value) {
    uploadUnlocked[currentUploadType.value] = true; 
    exampleVisible.value = false;
    proxy.$modal.msgSuccess("已解锁，请点击按钮选择文件上传");
  }
}

//替换为自定义的 UUID 转换下载接口
const attachmentConfig = computed(() => {
  const dict = attach_type.value || [];
  const findDictLabel = (val) => {
    const item = dict.find(d => d.value === val);
    return item ? item.label : null;
  };

  return [
    { label: findDictLabel('1') || '获奖证书', name: 'award', prop: 'fileAward', alert: `请上传${findDictLabel('1') || '获奖证书'}` },
    { label: findDictLabel('2') || '比赛通知', name: 'notice', prop: 'fileNotice', alert: `请上传${findDictLabel('2') || '比赛通知'}` },
    { label: findDictLabel('3') || '参赛作品', name: 'work', prop: 'fileWork', alert: `请上传${findDictLabel('3') || '参赛作品'}` },
    { label: findDictLabel('4') || '支付记录', name: 'payment', prop: 'filePayment', alert: `请上传${findDictLabel('4') || '支付记录'}`, type: 'warning', condition: (f) => f.isReimburse === 1 },
    { label: findDictLabel('5') || '正规发票', name: 'invoice', prop: 'fileInvoice', alert: `请上传${findDictLabel('5') || '正规发票'}`, type: 'warning', condition: (f) => f.isReimburse === 1 },
    { label: findDictLabel('6') || '收款码', name: 'receipt', prop: 'fileReceiptCode', alert: `请上传${findDictLabel('6') || '收款码'}`, type: 'warning', condition: (f) => f.isReimburse === 1 },
  ];
});
const visibleAttachments = computed(() => {
  return attachmentConfig.value.filter(item => {
    if (!item.condition) return true;
    return item.condition(form.value);
  });
});

const previewUrls = reactive({ award: "", notice: "", work: "", payment: "", invoice: "", receipt: "" });

function loadSafePreview(uuid, type) {
  if (!uuid) {
    if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
    previewUrls[type] = "";
    return;
  }
  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
    
    // 【关键修复】：兼容若依拦截器，确保拿到的是纯净的二进制数据
    const blobData = blob.data || blob; 
    const blobWithMime = new Blob([blobData], { type: 'application/pdf' });
    previewUrls[type] = window.URL.createObjectURL(blobWithMime);
  }).catch(err => {
    console.error("预览加载失败", err);
    previewUrls[type] = "";
  });
}
function handleOpenDetail(uuid) {
  if (!uuid) return;
  proxy.$modal.loading("正在准备文件...");

  // 【关键修复】：在发请求前先同步打开一个空白页，成功绕过浏览器拦截
  const newWin = window.open('about:blank', '_blank');

  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    proxy.$modal.closeLoading();
    
    const blobData = blob.data || blob;
    const blobWithMime = new Blob([blobData], { type: 'application/pdf' });
    const blobUrl = window.URL.createObjectURL(blobWithMime);

    // 将刚才的空白页重定向到生成的 PDF 链接
    if (newWin) {
      newWin.location.href = blobUrl;
    } else {
      proxy.$modal.msgWarning("详情弹窗被浏览器拦截，请在地址栏允许弹窗后重试");
    }
  }).catch(err => {
    proxy.$modal.closeLoading();
    if (newWin) newWin.close();
    proxy.$modal.msgError("文件获取失败，请稍后重试");
  });
}
function handleDownload(uuid) {
  if (!uuid) return;
  
  proxy.$modal.loading("正在下载文件，请稍候...");

  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob' // 告诉 axios 我们要接收的是二进制数据流
  }).then(blob => {
    proxy.$modal.closeLoading();
    
    // 兼容若依拦截器，提取纯净数据
    const blobData = blob.data || blob;
    // 强制指定为 PDF 类型
    const blobWithMime = new Blob([blobData], { type: 'application/pdf' }); 
    const blobUrl = window.URL.createObjectURL(blobWithMime);
    
    // 创建一个隐藏的 a 标签来触发浏览器原生下载
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = blobUrl;
    a.download = '成果附件_' + uuid + '.pdf'; // 设置下载后的默认文件名
    
    document.body.appendChild(a);
    a.click(); // 模拟点击下载
    
    // 下载完毕后清理 DOM 和内存中的 URL
    document.body.removeChild(a);
    window.URL.revokeObjectURL(blobUrl);
    
  }).catch(err => {
    proxy.$modal.closeLoading();
    console.error("下载异常", err);
    proxy.$modal.msgError("下载失败，请稍后重试");
  });
}
watch(() => form.value.fileAward, (uuid) => loadSafePreview(uuid, 'award'));
watch(() => form.value.fileNotice, (uuid) => loadSafePreview(uuid, 'notice'));
watch(() => form.value.fileWork, (uuid) => loadSafePreview(uuid, 'work'));
watch(() => form.value.filePayment, (uuid) => loadSafePreview(uuid, 'payment'));
watch(() => form.value.fileInvoice, (uuid) => loadSafePreview(uuid, 'invoice'));
watch(() => form.value.fileReceiptCode, (uuid) => loadSafePreview(uuid, 'receipt'));

// =========================================================
// 赛事与届次联动逻辑
// =========================================================
function normalizeId(val) {
  if (val === null || val === undefined || val === "") return null;
  return String(val);
}

function getCompetitionList() {
  request({
    url: '/competition/competition/list',
    method: 'get'
  }).then(response => {
    const list = response.data || response.rows || [];
    competitionOptions.value = list.map(item => ({
      competitionId: item.id || item.competitionId,
      competitionName: item.name || item.competitionName,
      ...item
    }));
  });
}

function getSessionList(competitionId) {
  if (!competitionId) {
    sessionOptions.value = [];
    return;
  }
  request({
    url: '/session/session/list',
    method: 'get',
    params: { competitionId: competitionId, pageNum: 1, pageSize: 100 } 
  }).then(response => {
    sessionOptions.value = response.rows || [];
  });
}

function handleCompetitionChange(val) {
  form.value.sessionId = null;
  sessionOptions.value = [];
  if (val) {
    getSessionList(val);
  }
}

function open(id) {
  if (!isPageMode.value) visible.value = true;
  reset();
  getDeptTree();
  getCompetitionList();
  
  activeAttachmentTab.value = 'award';
  if (id) {
    title.value = props.titleEdit;
    loadDetail(id);
  } else {
    title.value = props.titleAdd;
    // 【核心修改】：根据 sourceMode 进行默认填充
    if (props.sourceMode === 'guided') {
      // 教师端：我指导的成果，默认填入当前教师为第一指导老师
      samAchievementAdvisorList.value.push({
        teacherId: userStore.name,
        teacherName: userStore.nickName,
        orderNo: 1,
        isFixed: true // 标记为固定
      });
      reIndexList(samAchievementAdvisorList.value, 'advisor');
    } else if (props.sourceMode === 'responsible') {
      const roles = userStore.roles || [];
      const isTeacher = roles.includes('teacher');
      const isStudent = roles.includes('student');

      if (isTeacher && !isStudent) {
        // 教师在“我负责的成果”中，应把自己设为第一指导老师
        samAchievementAdvisorList.value.push({
          teacherId: userStore.name,
          teacherName: userStore.nickName,
          orderNo: 1,
          isFixed: true
        });
        reIndexList(samAchievementAdvisorList.value, 'advisor');
      } else {
        // 学生端：我负责的成果，默认填入当前学生为第一负责人
        listStudent({ no: userStore.name }).then(res => {
          let school = null;
          if (res.rows && res.rows.length > 0) {
            school = res.rows[0].school;
          }
          samAchievementParticipantList.value.push({
            studentId: userStore.name,
            studentName: userStore.nickName,
            school: school,
            orderNo: 1,
            manager: 1,
            isFixed: true // 标记为固定
          });
          reIndexList(samAchievementParticipantList.value, 'participant');
          updateSnapshot();
        });
      }
    } else {
      // 其他情况（如参与的成果）进行普通预填
      const roles = userStore.roles || [];
      const isTeacher = roles.includes('teacher');
      const isStudent = roles.includes('student');

      if (isTeacher && roles.length === 1) {
        samAchievementAdvisorList.value.push({
          teacherId: userStore.name,
          teacherName: userStore.nickName,
          orderNo: 1
        });
        reIndexList(samAchievementAdvisorList.value, 'advisor');
      } else if (isStudent && roles.length === 1) {
        listStudent({ no: userStore.name }).then(res => {
          let school = null;
          if (res.rows && res.rows.length > 0) {
            school = res.rows[0].school;
          }
          samAchievementParticipantList.value.push({
            studentId: userStore.name,
            studentName: userStore.nickName,
            school: school,
            orderNo: 1,
            manager: 1
          });
          reIndexList(samAchievementParticipantList.value, 'participant');
          updateSnapshot();
        });
      }
    }

    updateSnapshot();
    // 新增模式下检查草稿
    checkDraft();
  }
  initSortable();
}
function getForm() { return form.value; }
defineExpose({ open, getForm, activeAttachmentTab });

onMounted(() => {
  if (isPageMode.value) {
    getDeptTree();
    getCompetitionList();
    initSortable();
    
    // 页面模式且没有 ID 时认为是新增，检查草稿
    const id = route.query.achievementId || route.params.id;
    if (!id) {
       checkDraft();
    }
  }
});

function initSortable() {
  setTimeout(() => {
    const pTable = participantTable.value || participantTableDialog.value;
    if (pTable) {
      const el = pTable.$el.querySelector('.el-table__body-wrapper tbody') || pTable.$el.querySelector('tbody');
      if (el) {
        Sortable.create(el, {
          handle: '.drag-handle',
          filter: '.fixed-row', // 禁止拖动带 fixed-row 类的行
          onMove: (evt) => {
            // 禁止拖动到带 fixed-row 类的行上方（即禁止覆盖索引为0的位置）
            return evt.related.className.indexOf('fixed-row') === -1;
          },
          onEnd: ({ newIndex, oldIndex }) => {
            if (newIndex === oldIndex) return;
            const list = [...samAchievementParticipantList.value];
            const currRow = list.splice(oldIndex, 1)[0];
            list.splice(newIndex, 0, currRow);
            samAchievementParticipantList.value = [];
            nextTick(() => {
              samAchievementParticipantList.value = list;
              reIndexList(samAchievementParticipantList.value);
            });
          }
        });
      }
    }

    const aTable = advisorTable.value || advisorTableDialog.value;
    if (aTable) {
      const el = aTable.$el.querySelector('.el-table__body-wrapper tbody') || aTable.$el.querySelector('tbody');
      if (el) {
        Sortable.create(el, {
          handle: '.drag-handle',
          filter: '.fixed-row', // 禁止拖动带 fixed-row 类的行
          onMove: (evt) => {
            // 禁止拖动到带 fixed-row 类的行上方
            return evt.related.className.indexOf('fixed-row') === -1;
          },
          onEnd: ({ newIndex, oldIndex }) => {
            if (newIndex === oldIndex) return;
            const list = [...samAchievementAdvisorList.value];
            const currRow = list.splice(oldIndex, 1)[0];
            list.splice(newIndex, 0, currRow);
            samAchievementAdvisorList.value = [];
            nextTick(() => {
              samAchievementAdvisorList.value = list;
              reIndexList(samAchievementAdvisorList.value);
            });
          }
        });
      }
    }
  }, 500);
}

// === 【核心修复】：增强的数据回显逻辑 ===
function loadDetail(id) {
  if (!props.getFn) return;
  props.getFn(id).then(response => {
    const d = response.data;
    
    if (d.category != null) d.category = String(d.category);
    if (d.level != null) d.level = String(d.level);
    if (d.grade != null) d.grade = String(d.grade);
    if (d.groupId != null) d.groupId = String(d.groupId);

    if (d.competitionId != null) d.competitionId = Number(d.competitionId);
    if (d.sessionId != null) d.sessionId = Number(d.sessionId);
    if (d.ownerDepId != null) d.ownerDepId = Number(d.ownerDepId);

    // 预先占位，确保 Vue 的模板监听能够完美挂载并触发渲染
    d.fileAward = null;
    d.fileNotice = null;
    d.fileWork = null;
    d.filePayment = null;
    d.fileInvoice = null;
    d.fileReceiptCode = null;

    if (d.samAchievementAttachmentList) {
       d.samAchievementAttachmentList.forEach(item => {
          const typeStr = String(item.type);
          const uuid = item.fileUuid || item.file_uuid; // 兼容不同数据库下划线配置
          if (typeStr === '1') d.fileAward = uuid;
          if (typeStr === '2') d.fileNotice = uuid;
          if (typeStr === '3') d.fileWork = uuid;
          if (typeStr === '4') d.filePayment = uuid;
          if (typeStr === '5') d.fileInvoice = uuid;
          if (typeStr === '6') d.fileReceiptCode = uuid;
       });
    }

    form.value = d; // 一次性赋值给响应式对象
    
    if (d.competitionId) {
        getSessionList(d.competitionId);
    }
    
    samAchievementParticipantList.value = d.samAchievementParticipantList || [];
    samAchievementAdvisorList.value = d.samAchievementAdvisorList || [];
    reIndexList(samAchievementParticipantList.value);
    reIndexList(samAchievementAdvisorList.value);
    
    if (form.value.isReimburse == null) form.value.isReimburse = 0;
    
    updateSnapshot();
  });
}

function reset() {
  form.value = {
    competitionId: null, achievementId: null, sessionId: null, category: "3", name: null, teamName: null,
    level: null, grade: null, track: null, certificateNo: null, groupId: null, ownerDepId: null,
    awardTime: null, fee: null, isReimburse: 0,
    fileAward: null, fileNotice: null, fileWork: null, filePayment: null, fileInvoice: null, fileReceiptCode: null
  };
  samAchievementParticipantList.value = [];
  samAchievementAdvisorList.value = [];
  sessionOptions.value = []; 
  
  Object.keys(uploadUnlocked).forEach(k => uploadUnlocked[k] = false);
  
  Object.keys(previewUrls).forEach(key => {
    if (previewUrls[key]) {
      window.URL.revokeObjectURL(previewUrls[key]);
      previewUrls[key] = "";
    }
  });
  if(outcomeRefPage.value) outcomeRefPage.value.resetFields();
  if(outcomeRefDialog.value) outcomeRefDialog.value.resetFields();
  updateSnapshot();
}

function validatePDFUpload() {
  const f = form.value;
  const findLabel = (val) => {
    const item = attach_type.value.find(d => d.value === val);
    return item ? item.label : null;
  };
  
  const awardLabel = findLabel('1') || '获奖证书';
  const noticeLabel = findLabel('2') || '比赛通知';
  const workLabel = findLabel('3') || '参赛作品';
  const paymentLabel = findLabel('4') || '支付记录';
  const invoiceLabel = findLabel('5') || '正规发票';
  const receiptLabel = findLabel('6') || '收款码';

  if (!f.fileAward) { proxy.$modal.msgWarning(`请上传【${awardLabel}】PDF文件！`); activeAttachmentTab.value = 'award'; return false; }
  if (!f.fileNotice) { proxy.$modal.msgWarning(`请上传【${noticeLabel}】PDF文件！`); activeAttachmentTab.value = 'notice'; return false; }
  if (!f.fileWork) { proxy.$modal.msgWarning(`请上传【${workLabel}】PDF文件！`); activeAttachmentTab.value = 'work'; return false; }

  if (f.isReimburse === 1) {
    if (!f.filePayment) { proxy.$modal.msgWarning(`申请报销必须上传【${paymentLabel}】PDF文件！`); activeAttachmentTab.value = 'payment'; return false; }
    if (!f.fileInvoice) { proxy.$modal.msgWarning(`申请报销必须上传【${invoiceLabel}】PDF文件！`); activeAttachmentTab.value = 'invoice'; return false; }
    if (!f.fileReceiptCode) { proxy.$modal.msgWarning(`申请报销必须上传【${receiptLabel}】PDF文件！`); activeAttachmentTab.value = 'receipt'; return false; }
  }
  return true;
}

function submitForm() {
  if (props.readOnly) return;
  const activeRef = isPageMode.value ? outcomeRefPage.value : outcomeRefDialog.value;
  
  activeRef.validate(valid => {
    if (valid) {
      if (!validatePDFUpload()) return;

      let attachments = [];
      const pushFile = (type, path) => { 
        if (path) { attachments.push({ type: type, fileUuid: path, fileType: 1 }); } 
      };
      pushFile(1, form.value.fileAward);
      pushFile(2, form.value.fileNotice);
      pushFile(3, form.value.fileWork);
      pushFile(4, form.value.filePayment);
      pushFile(5, form.value.fileInvoice);
      pushFile(6, form.value.fileReceiptCode);

      form.value.samAchievementAttachmentList = attachments;
      
      form.value.samAchievementParticipantList = samAchievementParticipantList.value.map(p => ({
        ...p,
        studentNo: p.studentId, 
        manager: String(p.manager) 
      }));
      
      form.value.samAchievementAdvisorList = samAchievementAdvisorList.value.map(a => ({
        ...a,
        teacherNo: a.teacherId,
        manager: a.manager // 确保 manager 字段也传给后端
      }));

      const isEdit = form.value.achievementId != null;
      const apiFn = isEdit ? props.updateFn : props.addFn;
      
      if (apiFn) {
        apiFn(form.value).then(response => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
          // 提交成功清除草稿
          clearDraft();
          updateSnapshot(); 
          if (!isPageMode.value) visible.value = false;
          emit('ok');
        });
      } else {
        proxy.$modal.msgError("未配置保存接口");
      }
    }
  });
}

function handleBeforeClose(done) {
  if (!props.readOnly && isModified.value) {
    proxy.$confirm('是否保存草稿并退出？', '提示', {
      confirmButtonText: '保存草稿并退出',
      cancelButtonText: '不保存直接退出',
      type: 'warning',
      distinguishCancelAndClose: true
    }).then(() => {
      saveDraft(true);
      proxy.$modal.msgSuccess("草稿已保存并退出");
      done();
    }).catch(action => {
      if (action === 'cancel') {
        clearDraft();
        done();
      }
    });
  } else {
    done();
  }
}

function handleCancel() {
  const doExit = () => {
    if (isPageMode.value) {
      reset();
      emit('cancel');
    } else {
      visible.value = false;
      emit('cancel');
    }
  };

  if (!props.readOnly && isModified.value) {
    proxy.$confirm('是否保存草稿并退出？', '提示', {
      confirmButtonText: '保存草稿并退出',
      cancelButtonText: '不保存直接退出',
      type: 'warning',
      distinguishCancelAndClose: true
    }).then(() => {
      saveDraft(true);
      proxy.$modal.msgSuccess("草稿已保存并退出");
      doExit();
    }).catch(action => {
      if (action === 'cancel') {
        clearDraft();
        doExit();
      }
    });
  } else {
    doExit();
  }
}

function getDeptTree() {
  listDept().then(response => {
    deptOptions.value = handleTree(response.data, "deptId");
  });
}

function reIndexList(list, type) {
  list.forEach((item, index) => {
    item.orderNo = index + 1;
    item.manager = (index === 0) ? 1 : 0;
  });

  // 自动绑定逻辑
  if (type === 'participant' && list.length > 0) {
    const first = list[0];
    if (first.school) {
      form.value.ownerDepId = Number(first.school);
    } else if (userStore.deptId) {
      // 兜底：如果第一负责人没学院信息，取当前登录人的部门
      form.value.ownerDepId = userStore.deptId;
    }
  }
}

function tableRowClassName({ row }) {
  if (row.isFixed) {
    return 'fixed-row';
  }
  return '';
}

function getFileName(url) { return url ? url.substring(url.lastIndexOf("/") + 1) : ""; }

onBeforeRouteLeave((to, from, next) => {
  if (isPageMode.value && !props.readOnly && isModified.value) {
    proxy.$confirm('是否保存草稿并退出？', '提示', {
      confirmButtonText: '保存草稿并退出',
      cancelButtonText: '不保存直接退出',
      type: 'warning',
      distinguishCancelAndClose: true
    }).then(() => {
      saveDraft(true);
      proxy.$modal.msgSuccess("草稿已保存");
      next();
    }).catch(action => {
      if (action === 'cancel') {
        clearDraft();
        next();
      } else {
        next(false);
      }
    });
  } else {
    next();
  }
});

function goToCompetitionApply() {
  proxy.$router.push('/views/competition-apply/index');
}
</script>

<style scoped>
.outcome-page .page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px;
}
.outcome-page .page-title { font-size: 18px; font-weight: 600; }
.outcome-page .page-actions { display: flex; align-items: center; gap: 10px; }
.dialog-footer-wrapper { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  position: relative;
  z-index: 999; 
}
.footer-right {
  position: relative;
  z-index: 1000;
}
:deep(.el-dialog__footer) {
  position: relative;
  z-index: 99;
}
.attach-card { background: #f8f8f9; padding: 10px; border-radius: 4px; border: 1px solid #d9d9d9; height: 100%; }
.upload-pane-content { padding: 5px 10px; }
.mb10 { margin-bottom: 15px; }
.mr5 { margin-right: 5px; }
:deep(.el-tabs__content) { height: 100%; }

.hide-file-list :deep(.el-upload-list) { display: none !important; }

.fake-upload-box {
  width: 100%;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  text-align: center;
  padding: 30px 0;
  background-color: #fff;
  transition: border-color .3s;
}
.fake-upload-box:hover {
  border-color: #409EFF;
}

.custom-file-row {
  margin-top: 10px;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  border: 1px solid #e4e7ed;
  display: block; 
}

.file-name {
  display: flex; 
  align-items: center; 
  width: 100%;
  margin-bottom: 8px;
  font-weight: 500;
  white-space: nowrap; 
  overflow: hidden; 
  text-overflow: ellipsis;
}

.file-action {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.preview-box {
  margin-top: 5px; line-height: 1.2;
  border: 1px solid #ddd;
  padding: 2px;
  background-color: #fff;
  width: 100%;
  box-sizing: border-box;
}
</style>