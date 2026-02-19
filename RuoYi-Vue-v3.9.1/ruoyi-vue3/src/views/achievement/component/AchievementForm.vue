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
    :disabled="!form.competitionId"
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
                    <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddParticipant">添加学生</el-button></el-col>
                    <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
                  </el-row>
                  <el-table ref="participantTable" :data="samAchievementParticipantList" :row-class-name="rowParticipantIndex" @selection-change="handleParticipantSelectionChange">
                    <el-table-column v-if="!readOnly" width="40" align="center">
                      <template #default="scope">
                        <el-icon v-if="scope.row.manager !== 1" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                      </template>
                    </el-table-column>
                    <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                    <el-table-column label="学生学号" prop="studentId">
                      <template #default="scope">
                        <el-input 
                          v-model="scope.row.studentId" 
                          placeholder="请输入学号" 
                          @blur="handleStudentBlur(scope.row)" 
                          :disabled="readOnly"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="姓名" align="center" prop="studentName" width="120">
                      <template #default="scope">
                        <el-input 
                          v-model="scope.row.studentName" 
                          :disabled="!scope.row.isManual" 
                          :placeholder="scope.row.isManual ? '手动输入' : '自动回显'"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="排序" prop="orderNo" width="100">
                      <template #default="scope"><el-input v-model="scope.row.orderNo" disabled /></template>
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
                  <el-table ref="advisorTable" :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
                    <el-table-column v-if="!readOnly" width="40" align="center">
                      <template #default="scope">
                        <el-icon class="drag-handle" style="cursor: move"><Rank /></el-icon>
                      </template>
                    </el-table-column>
                    <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                    <el-table-column label="教师工号" prop="teacherId">
                      <template #default="scope">
                        <el-input 
                          v-model="scope.row.teacherId" 
                          placeholder="请输入工号" 
                          @blur="handleTeacherBlur(scope.row)" 
                          :disabled="readOnly"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                      <template #default="scope">
                        <el-input 
                          v-model="scope.row.teacherName" 
                          :disabled="!scope.row.isManual" 
                          :placeholder="scope.row.isManual ? '手动输入' : '自动回显'"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="排序" prop="orderNo" width="100">
                      <template #default="scope"><el-input v-model="scope.row.orderNo" disabled /></template>
                    </el-table-column>
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
    :disabled="!form.competitionId"
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
                <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="handleAddParticipant">添加学生</el-button></el-col>
                <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
              </el-row>
              <el-table ref="participantTableDialog" :data="samAchievementParticipantList" :row-class-name="rowParticipantIndex" @selection-change="handleParticipantSelectionChange">
                <el-table-column v-if="!readOnly" width="40" align="center">
                  <template #default="scope">
                    <el-icon v-if="scope.row.manager !== 1" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                  </template>
                </el-table-column>
                <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                <el-table-column label="学生学号" prop="studentId">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.studentId" 
                      placeholder="请输入学号" 
                      @blur="handleStudentBlur(scope.row)" 
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="姓名" align="center" prop="studentName" width="120">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.studentName" 
                      :disabled="!scope.row.isManual" 
                      :placeholder="scope.row.isManual ? '手动输入' : '自动回显'"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="排序" prop="orderNo" width="100">
                  <template #default="scope"><el-input v-model="scope.row.orderNo" disabled /></template>
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
              <el-table ref="advisorTableDialog" :data="samAchievementAdvisorList" @selection-change="handleAdvisorSelectionChange">
                <el-table-column v-if="!readOnly" width="40" align="center">
                  <template #default="scope">
                    <el-icon class="drag-handle" style="cursor: move"><Rank /></el-icon>
                  </template>
                </el-table-column>
                <el-table-column v-if="!readOnly" type="selection" width="50" align="center" />
                <el-table-column label="教师工号" prop="teacherId">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.teacherId" 
                      placeholder="请输入工号" 
                      @blur="handleTeacherBlur(scope.row)" 
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="姓名" align="center" prop="teacherName" width="120">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.teacherName" 
                      :disabled="!scope.row.isManual" 
                      :placeholder="scope.row.isManual ? '手动输入' : '自动回显'"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="排序" prop="orderNo" width="100">
                  <template #default="scope"><el-input v-model="scope.row.orderNo" disabled /></template>
                </el-table-column>
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

  <!-- 学生信息补全弹窗 -->
  <el-dialog title="完善学生信息" v-model="studentRegVisible" width="500px" append-to-body :close-on-click-modal="false">
    <el-form ref="studentRegRef" :model="studentRegForm" :rules="studentRegRules" label-width="80px">
      <el-form-item label="学号" prop="no">
        <el-input v-model="studentRegForm.no" disabled />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="studentRegForm.name" placeholder="请输入学生姓名" />
      </el-form-item>
      <el-form-item label="学院" prop="school">
        <el-input v-model="studentRegForm.school" placeholder="请输入学院" />
      </el-form-item>
      <el-form-item label="院系" prop="department">
        <el-input v-model="studentRegForm.department" placeholder="请输入院系" />
      </el-form-item>
      <el-form-item label="专业" prop="major">
        <el-input v-model="studentRegForm.major" placeholder="请输入专业" />
      </el-form-item>
      <el-form-item label="班级" prop="class_name">
        <el-input v-model="studentRegForm.class_name" placeholder="请输入班级" />
      </el-form-item>
      <el-form-item label="年级" prop="class_year">
        <el-input v-model="studentRegForm.class_year" placeholder="请输入年级 (例如: 2022)" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitStudentReg">确 定</el-button>
        <el-button @click="studentRegVisible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="AchievementForm">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted, watch } from "vue";
import { onBeforeRouteLeave } from "vue-router";
import draggable from "vuedraggable";
import Sortable from "sortablejs";
import useUserStore from "@/store/modules/user";
import { Plus, Delete, Document, Download, View, UploadFilled, Rank } from "@element-plus/icons-vue";
import { listStudent, addStudent } from "@/api/achievement/student";
import { listTeacher } from "@/api/achievement/teacher";
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
const studentRegVisible = ref(false);
const studentRegForm = ref({ no: "", name: "", school: "", department: "", major: "", class_name: "", class_year: "" });
const studentRegRules = {
  name: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  school: [{ required: true, message: "学院不能为空", trigger: "blur" }]
};
let currentPendingRow = null; // 记录当前正在录入的学生行

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
// 【核心逻辑】示例拦截与解锁
// =========================================================
const exampleVisible = ref(false);
const currentExampleUrl = ref("");
const currentUploadType = ref(""); 

// 记录各个模块是否已解锁
const uploadUnlocked = reactive({
  award: false,
  notice: false,
  work: false,
  payment: false,
  invoice: false,
  receipt: false
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
// 【核心逻辑】手动添加学生/老师
// =========================================================

function handleStudentBlur(row) {
  if (!row.studentId) { 
    row.studentName = ""; 
    row.isManual = false; 
    return; 
  }
  
  listStudent({ no: row.studentId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.studentName = response.rows[0].name;
      row.isManual = false; 
    } else {
      // 未找到学生，弹出补全窗口
      currentPendingRow = row;
      studentRegForm.value = { 
        no: row.studentId, 
        name: "", 
        school: "", 
        department: "", 
        major: "", 
        class_name: "", 
        class_year: "" 
      };
      studentRegVisible.value = true;
    }
  });
}

function submitStudentReg() {
  proxy.$refs.studentRegRef.validate(valid => {
    if (valid) {
      addStudent(studentRegForm.value).then(response => {
        proxy.$modal.msgSuccess("学生信息已录入");
        if (currentPendingRow) {
          currentPendingRow.studentName = studentRegForm.value.name;
          currentPendingRow.isManual = false;
        }
        studentRegVisible.value = false;
      });
    }
  });
}

function handleAddParticipant() {
  samAchievementParticipantList.value.push({ 
    studentId: "", 
    studentName: "", 
    orderNo: 0, 
    manager: 0,
    isManual: false 
  });
  reIndexList(samAchievementParticipantList.value);
}

function handleTeacherBlur(row) {
  if (!row.teacherId) { 
    row.teacherName = ""; 
    row.isManual = false; 
    return; 
  }
  
  listTeacher({ no: row.teacherId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      row.teacherName = response.rows[0].teacherName;
      row.isManual = false;
    } else {
      row.teacherName = "";
      row.isManual = true;
      proxy.$modal.msgInfo(`未找到工号 ${row.teacherId}，请手动输入姓名`);
    }
  });
}

function handleAddAdvisor() {
  samAchievementAdvisorList.value.push({ 
    teacherId: "", 
    teacherName: "", 
    orderNo: 0,
    isManual: false 
  }); 
  reIndexList(samAchievementAdvisorList.value);
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
// 附件配置与安全预览
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
    url: '/common/download/resource',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
    previewUrls[type] = window.URL.createObjectURL(blob);
  }).catch(err => {
    console.error("预览加载失败", err);
    previewUrls[type] = "";
  });
}

watch(() => form.value.fileAward, (uuid) => loadSafePreview(uuid, 'award'));
watch(() => form.value.fileNotice, (uuid) => loadSafePreview(uuid, 'notice'));
watch(() => form.value.fileWork, (uuid) => loadSafePreview(uuid, 'work'));
watch(() => form.value.filePayment, (uuid) => loadSafePreview(uuid, 'payment'));
watch(() => form.value.fileInvoice, (uuid) => loadSafePreview(uuid, 'invoice'));
watch(() => form.value.fileReceiptCode, (uuid) => loadSafePreview(uuid, 'receipt'));

// =========================================================
// 赛事与届次联动逻辑 (核心修改)
// =========================================================

// 获取比赛列表
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

// 修改：获取届次列表 (带 competitionId 参数)
function getSessionList(competitionId) {
  if (!competitionId) {
    sessionOptions.value = [];
    return;
  }
  request({
    url: '/session/session/list',
    method: 'get',
    // 关键：将赛事ID作为参数传递给后端，后端根据此ID过滤数据
    params: { competitionId: competitionId, pageNum: 1, pageSize: 100 } 
  }).then(response => {
    sessionOptions.value = response.rows || [];
  });
}

// 新增：监听赛事变化，级联加载届次
function handleCompetitionChange(val) {
  // 1. 清空当前已选的届次
  form.value.sessionId = null;
  // 2. 清空下拉选项
  sessionOptions.value = [];
  // 3. 如果选了赛事，加载对应届次
  if (val) {
    getSessionList(val);
  }
}

// =========================================================

function handleOpenDetail(uuid) {
  if (!uuid) return;
  proxy.$modal.loading("正在打开文件...");
  request({
    url: '/common/download/resource',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob' 
  }).then(blob => {
    proxy.$modal.closeLoading();
    const blobUrl = window.URL.createObjectURL(blob);
    window.open(blobUrl);
  }).catch(err => {
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("文件打开失败，请稍后重试");
  });
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
    // 自动填充当前用户为负责人
    samAchievementParticipantList.value.push({
      studentId: userStore.name,
      studentName: userStore.nickName,
      orderNo: 1,
      manager: 1,
      isManual: false
    });
    updateSnapshot();
  }
  initSortable(); // 开启拖拽
}

function getForm() { return form.value; }
defineExpose({ open, getForm, handleAddParticipant, handleDeleteParticipant, handleParticipantSelectionChange, handleStudentBlur, handleAddAdvisor, handleDeleteAdvisor, handleAdvisorSelectionChange, handleTeacherBlur, activeAttachmentTab });

onMounted(() => {
  if (isPageMode.value) {
    getDeptTree();
    getCompetitionList();
    initSortable();
  }
});

/** 初始化拖拽 */
function initSortable() {
  setTimeout(() => {
    // 1. 尝试获取参与者表格（支持页面或弹窗模式）
    const pTable = participantTable.value || participantTableDialog.value;
    if (pTable) {
      const el = pTable.$el.querySelector('.el-table__body-wrapper tbody') || pTable.$el.querySelector('tbody');
      if (el) {
        Sortable.create(el, {
          handle: '.drag-handle',
          filter: '.el-table__row:first-child', // 负责人不动
          onEnd: ({ newIndex, oldIndex }) => {
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

    // 2. 尝试获取老师表格
    const aTable = advisorTable.value || advisorTableDialog.value;
    if (aTable) {
      const el = aTable.$el.querySelector('.el-table__body-wrapper tbody') || aTable.$el.querySelector('tbody');
      if (el) {
        Sortable.create(el, {
          handle: '.drag-handle',
          onEnd: ({ newIndex, oldIndex }) => {
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

function loadDetail(id) {
  if (!props.getFn) return;
  props.getFn(id).then(response => {
    const d = response.data;
    form.value = d;
    
    // 修改：回显时，如果存在 competitionId，需要手动触发加载届次列表，否则下拉框只会显示 ID
    if (d.competitionId) {
        getSessionList(d.competitionId);
    }
    
    samAchievementParticipantList.value = d.samAchievementParticipantList || [];
    samAchievementAdvisorList.value = d.samAchievementAdvisorList || [];
    reIndexList(samAchievementParticipantList.value);
    reIndexList(samAchievementAdvisorList.value);
    
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
    updateSnapshot();
  });
}

function reset() {
  form.value = {
    competitionId: null,
    achievementId: null, sessionId: null, category: null, name: null, teamName: null,
    level: null, grade: null, track: null, certificateNo: null, groupId: null, ownerDepId: null,
    awardTime: null, fee: null, isReimburse: 0,
    fileAward: null, fileNotice: null, fileWork: null, filePayment: null, fileInvoice: null, fileReceiptCode: null
  };
  samAchievementParticipantList.value = [];
  samAchievementAdvisorList.value = [];
  // 重置届次选项
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
      const pushFile = (type, path) => { 
        if (path) {
          attachments.push({ 
            type: type, 
            fileUuid: path, 
            fileType: 1 // 增加默认文件类型，防止数据库非空约束报错
          }); 
        } 
      };
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
          updateSnapshot(); // 保存后更新快照
          if (!isPageMode.value) visible.value = false;
          emit('ok');
        });
      } else {
        proxy.$modal.msgError("未配置保存接口");
      }
    }
  });
}

/** 拦截右上角叉号或点击遮罩层 */
function handleBeforeClose(done) {
  if (isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }).then(() => {
      done(); // 执行 done 才会真正关闭
    }).catch(() => {
      // 点击取消不执行 done，弹窗保持打开
    });
  } else {
    done();
  }
}

function handleCancel() {
  if (isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }).then(() => {
      if (isPageMode.value) {
         reset();
         emit('cancel');
      } else {
         visible.value = false; // 这将触发 @closed="reset"
         emit('cancel');
      }
    }).catch(() => {});
  } else {
    if (isPageMode.value) {
       reset();
       emit('cancel');
    } else {
       visible.value = false;
       emit('cancel');
    }
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
    // 只有第一个始终是负责人
    item.manager = (index === 0) ? 1 : 0;
  });
}

// 拖拽结束后的处理函数
function onDragEnd() {
  reIndexList(samAchievementParticipantList.value);
}

function onAdvisorDragEnd() {
  reIndexList(samAchievementAdvisorList.value);
}

function getFileName(url) { return url ? url.substring(url.lastIndexOf("/") + 1) : ""; }
function handleDownload(uuid) {
  if (uuid) proxy.$download.resource(uuid);
}

onBeforeRouteLeave((to, from, next) => {
  if (isPageMode.value && isModified.value) {
    proxy.$modal.confirm('系统检测到您有未保存的修改，确定要离开吗？', "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }).then(() => {
      next();
    }).catch(() => {
      next(false);
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
.dialog-footer-wrapper { display: flex; justify-content: space-between; align-items: center; }
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
  width: 100%; /* 设置宽度为 100%，使其占满容器 */
  box-sizing: border-box; /* 确保 padding 和 border 不会影响总宽度 */
}

</style>