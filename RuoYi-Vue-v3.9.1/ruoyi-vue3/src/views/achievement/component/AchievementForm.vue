<template>
  <div v-if="isPageMode" class="app-container outcome-page">
    <div class="page-card">
      <div class="page-header">
        <div class="header-left">
          <div class="page-title">{{ title }}</div>
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
                        <div style="margin-top: 5px;">
                          <el-link type="primary" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                        </div>
                      </el-form-item>
                    </el-col>
                  </el-row>
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
                    <el-col :span="12">
                      <el-form-item label="所属学院" prop="ownerDepId">
                        <el-tree-select v-model="form.ownerDepId" :data="deptOptions" :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId" placeholder="请选择所属学院" check-strictly />
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
                        <el-select v-model="form.grade" placeholder="请选择" style="width: 100%">
                          <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                        </el-select>
                        <div style="color: #909399; font-size: 12px; margin-top: 5px;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="12">
                      <el-form-item label="赛道" prop="track">
                        <el-input v-model="form.track" placeholder="请输入赛道" />
                        <div style="color: #909399; font-size: 12px; margin-top: 5px;">例如蓝桥杯有c++，java数学竞赛有数学类与非数A等</div>
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
                        <div style="color: #909399; font-size: 12px; margin-top: 5px;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
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
                    <div style="color: #F56C6C; font-size: 12px; margin-top: 5px; font-weight: bold;">
                      如果报名者没有通过其他途径报销，请上传发票（PDF）和填写报名金额。注意：同一张发票只能报销一次
                    </div>
                  </el-form-item>

                  <el-divider content-position="center">参赛选手信息</el-divider>
                  <el-row :gutter="10" class="mb8" v-if="!readOnly">
                    <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddParticipantDialog">添加学生</el-button></el-col>
                    <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
                  </el-row>
                  <el-table ref="participantTable" :data="samAchievementParticipantList" @selection-change="handleParticipantSelectionChange">
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
                  <el-table ref="advisorTable" :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
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
                                <el-button link type="primary" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                                <el-button link type="primary" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
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
                      placeholder="请选择关联的赛事" 
                      filterable 
                      clearable 
                      style="width: 100%"
                      @change="handleCompetitionChange"
                    >
                      <el-option v-for="item in competitionOptions" :key="item.competitionId" :label="item.competitionName" :value="item.competitionId" />
                    </el-select>
                    <div style="margin-top: 5px;">
                      <el-link type="primary" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
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
                <el-col :span="12">
                  <el-form-item label="所属学院" prop="ownerDepId">
                    <el-tree-select v-model="form.ownerDepId" :data="deptOptions" :props="{ value: 'deptId', label: 'deptName', children: 'children' }" value-key="deptId" placeholder="请选择所属学院" check-strictly />
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
                    <el-select v-model="form.grade" placeholder="请选择" style="width: 100%">
                      <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                    </el-select>
                    <div style="color: #909399; font-size: 12px; margin-top: 5px;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="赛道" prop="track">
                    <el-input v-model="form.track" placeholder="请输入赛道" />
                    <div style="color: #909399; font-size: 12px; margin-top: 5px;">例如蓝桥杯有c++，java数学竞赛有数学类与非数A等</div>
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
                    <div style="color: #909399; font-size: 12px; margin-top: 5px;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
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
              <el-table ref="participantTableDialog" :data="samAchievementParticipantList" @selection-change="handleParticipantSelectionChange">
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
              <el-table ref="advisorTableDialog" :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
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
                            <el-button link type="primary" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                            <el-button link type="primary" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
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
      <el-form-item label="学号" prop="studentId">
        <el-input v-model="participantForm.studentId" placeholder="请输入学号后点击空白处" @blur="handleParticipantIdBlur" clearable />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input v-model="participantForm.studentName" placeholder="自动带出，或手动输入" :disabled="!isParticipantNew" />
      </el-form-item>
      
      <template v-if="isParticipantNew">
        <el-alert title="未匹配到该学号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="学院" prop="school">
          <el-tree-select 
            v-model="participantForm.school" 
            :data="deptOptions" 
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }" 
            value-key="deptId" 
            placeholder="请选择学院" 
            check-strictly 
            style="width: 100%" 
            @change="handleParticipantSchoolChange"
          />
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-tree-select 
            v-model="participantForm.department" 
            :data="participantDepartmentOptions" 
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }" 
            value-key="deptId" 
            placeholder="请先选择学院，再选择院系" 
            check-strictly 
            style="width: 100%" 
            @change="handleParticipantDeptChange"
          />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-tree-select 
            v-model="participantForm.major" 
            :data="participantMajorOptions" 
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }" 
            value-key="deptId" 
            placeholder="请先选择院系，再选择专业" 
            check-strictly 
            style="width: 100%" 
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
      <el-form-item label="工号" prop="teacherId">
        <el-input v-model="advisorForm.teacherId" placeholder="请输入工号后点击空白处" @blur="handleAdvisorIdBlur" clearable />
      </el-form-item>
      <el-form-item label="姓名" prop="teacherName">
        <el-input v-model="advisorForm.teacherName" placeholder="自动带出，或手动输入" :disabled="!isAdvisorNew" />
      </el-form-item>
      
      <template v-if="isAdvisorNew">
        <el-alert title="未匹配到该工号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="学院" prop="school">
          <el-tree-select 
            v-model="advisorForm.school" 
            :data="deptOptions" 
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }" 
            value-key="deptId" 
            placeholder="请选择学院" 
            check-strictly 
            style="width: 100%" 
            @change="handleAdvisorSchoolChange"
          />
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-tree-select 
            v-model="advisorForm.department" 
            :data="advisorDepartmentOptions" 
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }" 
            value-key="deptId" 
            placeholder="请先选择学院，再选择院系" 
            check-strictly 
            style="width: 100%" 
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
</template>

<script setup name="AchievementForm">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted, watch, nextTick } from "vue";
import { onBeforeRouteLeave } from "vue-router";
import Sortable from "sortablejs";
import useUserStore from "@/store/modules/user";
import { Plus, Delete, Document, Download, View, UploadFilled, Rank } from "@element-plus/icons-vue";
import { listStudent, addStudent } from "@/api/achievement/student";
import { listTeacher, addTeacher } from "@/api/achievement/teacher";
import { listDept } from "@/api/system/dept";
import { handleTree } from "@/utils/ruoyi";
import request from '@/utils/request';
import FileUpload from '@/components/FileUpload';

const { proxy } = getCurrentInstance();
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
});

const { achievement_category, group_type, award_rank, award_level_type } = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type');
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

const data = reactive({
  form: { competitionId: null },
  formSnapshot: "",
  rules: {
    competitionId: [{ required: true, message: "比赛不能为空", trigger: "change" }],
    category: [{ required: true, message: "类别不能为空", trigger: "change" }],
    sessionId: [{ required: true, message: "届次不能为空", trigger: "change" }],
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
const { form, formSnapshot, rules } = toRefs(data);

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

// =========================================================
// 新版：学生与老师的级联选择逻辑 (查找与动态过滤)
// =========================================================

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

// =========================================================
// 弹窗控制与提交逻辑
// =========================================================

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
  isParticipantNew.value = false;
  addParticipantVisible.value = true;
}

function handleParticipantIdBlur() {
  const id = participantForm.value.studentId;
  if (!id) return;
  listStudent({ no: id }).then(res => {
    if (res.rows && res.rows.length > 0) {
      participantForm.value.studentName = res.rows[0].name;
      isParticipantNew.value = false;
    } else {
      proxy.$modal.msgWarning("系统未找到该学号，请补充完善下方信息");
      participantForm.value.studentName = '';
      isParticipantNew.value = true;
    }
  });
}

function submitAddParticipant() {
  proxy.$refs.addParticipantRef.validate(valid => {
    if (valid) {
      const pushToList = () => {
        samAchievementParticipantList.value.push({
          studentId: participantForm.value.studentId,
          studentName: participantForm.value.studentName,
          orderNo: samAchievementParticipantList.value.length + 1,
          manager: samAchievementParticipantList.value.length === 0 ? 1 : 0
        });
        reIndexList(samAchievementParticipantList.value);
        addParticipantVisible.value = false;
      };

      if (isParticipantNew.value) {
        addStudent({
          no: participantForm.value.studentId,
          name: participantForm.value.studentName,
          school: participantForm.value.school,
          department: participantForm.value.department,
          major: participantForm.value.major,
          class_name: participantForm.value.class_name,
          class_year: participantForm.value.class_year
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
  isAdvisorNew.value = false;
  addAdvisorVisible.value = true;
}

function handleAdvisorIdBlur() {
  const id = advisorForm.value.teacherId;
  if (!id) return;
  listTeacher({ no: id }).then(res => {
    if (res.rows && res.rows.length > 0) {
      advisorForm.value.teacherName = res.rows[0].teacherName;
      isAdvisorNew.value = false;
    } else {
      proxy.$modal.msgWarning("系统未找到该工号，请补充完善下方信息");
      advisorForm.value.teacherName = '';
      isAdvisorNew.value = true;
    }
  });
}

function submitAddAdvisor() {
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
  samAchievementParticipantList.value = samAchievementParticipantList.value.filter(item => !checkedParticipant.value.includes(item));
  reIndexList(samAchievementParticipantList.value);
}
function handleParticipantSelectionChange(sel) { checkedParticipant.value = sel; }

function handleDeleteAdvisor() {
  if (checkedAdvisor.value.length == 0) return proxy.$modal.msgError("请选择删除项");
  samAchievementAdvisorList.value = samAchievementAdvisorList.value.filter(item => !checkedAdvisor.value.includes(item));
  reIndexList(samAchievementAdvisorList.value);
}
function handleAdvisorSelectionChange(sel) { checkedAdvisor.value = sel; }

// =========================================================
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

// =========================================================
// 【核心修复】：替换为自定义的 UUID 转换下载接口
// =========================================================
const attachmentConfig = [
  { label: '奖状(证书)', name: 'award', prop: 'fileAward', alert: '请上传获奖证书' },
  { label: '比赛通知', name: 'notice', prop: 'fileNotice', alert: '请上传比赛通知' },
  { label: '参赛作品', name: 'work', prop: 'fileWork', alert: '请上传参赛作品' },
  { label: '支付记录', name: 'payment', prop: 'filePayment', alert: '请上传转账截图', type: 'warning', condition: (f) => f.isReimburse === 1 },
  { label: '正规发票', name: 'invoice', prop: 'fileInvoice', alert: '请上传电子发票', type: 'warning', condition: (f) => f.isReimburse === 1 },
  { label: '收款码', name: 'receipt', prop: 'fileReceiptCode', alert: '请上传用于接收报销款的收款码', type: 'warning', condition: (f) => f.isReimburse === 1 },
];

const visibleAttachments = computed(() => {
  return attachmentConfig.filter(item => {
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
    // 【核心修改】：根据传入角色判断默认填入学生还是老师
    if (props.userRole === 'teacher') {
      samAchievementAdvisorList.value.push({
        teacherId: userStore.name,
        teacherName: userStore.nickName,
        orderNo: 1
      });
    } else {
      samAchievementParticipantList.value.push({
        studentId: userStore.name,
        studentName: userStore.nickName,
        orderNo: 1,
        manager: 1
      });
    }
    updateSnapshot();
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
          onEnd: ({ newIndex, oldIndex }) => {
            let targetIndex = newIndex;
            // 【拦截拦截】：绝不允许拖到索引 0 的位置
            if (targetIndex === 0) targetIndex = 1; 

            const list = [...samAchievementParticipantList.value];
            const currRow = list.splice(oldIndex, 1)[0];
            list.splice(targetIndex, 0, currRow);
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
          onEnd: ({ newIndex, oldIndex }) => {
            let targetIndex = newIndex;
            // 【拦截拦截】：绝不允许拖到索引 0 的位置
            if (targetIndex === 0) targetIndex = 1; 

            const list = [...samAchievementAdvisorList.value];
            const currRow = list.splice(oldIndex, 1)[0];
            list.splice(targetIndex, 0, currRow);
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
    competitionId: null, achievementId: null, sessionId: null, category: null, name: null, teamName: null,
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
  if (!f.fileAward) { proxy.$modal.msgWarning("请上传【奖状(证书)】PDF文件！"); activeAttachmentTab.value = 'award'; return false; }
  if (!f.fileNotice) { proxy.$modal.msgWarning("请上传【比赛通知】PDF文件！"); activeAttachmentTab.value = 'notice'; return false; }
  if (!f.fileWork) { proxy.$modal.msgWarning("请上传【参赛作品】PDF文件！"); activeAttachmentTab.value = 'work'; return false; }

  if (f.isReimburse === 1) {
    if (!f.filePayment) { proxy.$modal.msgWarning("申请报销必须上传【支付记录】PDF文件！"); activeAttachmentTab.value = 'payment'; return false; }
    if (!f.fileInvoice) { proxy.$modal.msgWarning("申请报销必须上传【正规发票】PDF文件！"); activeAttachmentTab.value = 'invoice'; return false; }
    if (!f.fileReceiptCode) { proxy.$modal.msgWarning("申请报销必须上传【收款码】PDF文件！"); activeAttachmentTab.value = 'receipt'; return false; }
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
        teacherNo: a.teacherId 
      }));

      const isEdit = form.value.achievementId != null;
      const apiFn = isEdit ? props.updateFn : props.addFn;
      
      if (apiFn) {
        apiFn(form.value).then(response => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
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
  if (isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定", cancelButtonText: "取消", type: "warning"
    }).then(() => { done(); }).catch(() => {});
  } else {
    done();
  }
}

function handleCancel() {
  if (isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定", cancelButtonText: "取消", type: "warning"
    }).then(() => {
      if (isPageMode.value) { reset(); emit('cancel'); } else { visible.value = false; emit('cancel'); }
    }).catch(() => {});
  } else {
    if (isPageMode.value) { reset(); emit('cancel'); } else { visible.value = false; emit('cancel'); }
  }
}

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

function getFileName(url) { return url ? url.substring(url.lastIndexOf("/") + 1) : ""; }

onBeforeRouteLeave((to, from, next) => {
  if (isPageMode.value && isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定", cancelButtonText: "取消", type: "warning"
    }).then(() => { next(); }).catch(() => { next(false); });
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
  margin-top: 5px;
  border: 1px solid #ddd;
  padding: 2px;
  background-color: #fff;
  width: 100%;
  box-sizing: border-box;
}
</style>