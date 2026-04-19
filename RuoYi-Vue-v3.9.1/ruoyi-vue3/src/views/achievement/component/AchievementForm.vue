<template>
  <div v-if="isPageMode" class="app-container outcome-page">
    <div class="page-card" v-loading="detailLoading">
      <div class="page-header">
        <div class="header-left">
          <slot name="header-left"></slot>
          <div class="page-title">
            {{
              isPageMode
                ? `${title}${
                    displayAchievementId
                      ? ` - 成果编号: ${displayAchievementId}`
                      : ""
                  }`
                : title
            }}
          </div>
        </div>
        <div class="page-actions" v-if="!displayAchievementId">
          <slot name="page-actions">
            <el-button v-if="showSubmit" type="primary" @click="submitForm">{{ submitTextComputed }}</el-button>
            <el-button @click="handleCancel">{{ cancelText }}</el-button>
          </slot>
        </div>
      </div>
      <el-divider style="margin: 10px 0 20px 0"></el-divider>
      <div class="outcome-body">
        <el-form
          ref="outcomeRefPage"
          :model="form"
          :rules="rules"
          label-width="110px"
          :disabled="readOnly"
        >
          <div class="common-form-content">
             <template v-for="(_, slot) in $slots">
                <slot :name="slot"></slot>
             </template>
             <el-row :gutter="20">
                <el-col :span="12">
                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="参加比赛" prop="competitionId">
                        <el-select
                          v-model="form.competitionId"
                          placeholder="第一步：请选择您参加的赛事"
                          filterable
                          clearable
                          style="width: 100%"
                          @change="handleCompetitionChange"
                        >
                          <el-option v-for="item in competitionOptions" :key="item.competitionId" :label="item.competitionName" :value="item.competitionId" />
                        </el-select>
                        <div style="width: 100%; display: flex; flex-direction: column;">
                          <div style="margin-top: 5px; line-height: 1.2;">
                            <el-link type="primary" :disabled="false" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                          </div>
                        </div>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="获奖时间" prop="awardTime">
                        <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD" placeholder="第二步：请按照证书上的日期选择" style="width: 100%" />
                        <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
                      </el-form-item>
                    </el-col>

                    <template v-if="showReviewExtraFields">
                      <el-col :span="24">
                        <el-form-item label="年份" prop="year">
                          <el-input
                            v-model="form.year"
                            placeholder="请输入年份"
                            clearable
                            maxlength="4"
                            :disabled="readOnly || !canEditReviewYear"
                            style="width: 100%"
                            @input="form.year = String(form.year || '').replace(/\\D/g, '').slice(0, 4)"
                          />
                          <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">
                            根据获奖时间自动带出年份，仅管理员可修改。
                          </div>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-form-item label="是否补录" prop="isSupplement">
                          <el-radio-group v-model="form.isSupplement" :disabled="readOnly || !canEditReviewMeta">
                            <el-radio :label="1">是</el-radio>
                            <el-radio :label="0">否</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                    </template>

                    <el-col :span="24">
                      <el-form-item label="获奖级别" prop="level">
                        <el-select v-model="form.level" placeholder="第三步：请选择获奖级别（国家级/省级等）" style="width: 100%">
                          <el-option v-for="dict in award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                        </el-select>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="比赛届次" prop="sessionId">
                        <el-select
                          v-model="form.sessionId"
                          placeholder="系统将根据前三步自动匹配锁定"
                          filterable
                          clearable
                          style="width: 100%"
                          :disabled="readOnly || !!form.competitionId"
                        >
                          <el-option v-for="item in sessionOptions" :key="item.id" :label="item.session" :value="item.id" />
                        </el-select>
                        <div style="width: 100%; display: flex; flex-direction: column;">
                          <div v-if="isAutoMatched" style="color: #67C23A; font-size: 12px; margin-top: 5px;">
                            <el-icon style="vertical-align: middle;"><CircleCheck /></el-icon> 已根据时间与级别自动锁定届次
                          </div>
                          <div style="margin-top: 5px; line-height: 1.2;">
                            <el-link type="primary" :disabled="false" @click="goToCompetitionApply">届次找不到？点击这里申请赛事届次！</el-link>
                          </div>
                        </div>
                        <div v-if="form.competitionId && form.level && form.awardTime && !form.sessionId" style="color: #F56C6C; font-size: 12px; margin-top: 5px;">
                          <el-icon style="vertical-align: middle;"><Warning /></el-icon> 未找到匹配的届次，请核对获奖时间与级别
                        </div>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="奖项等级" prop="grade">
                        <el-select v-model="form.grade" placeholder="请选择奖项等级（特等奖/一等奖等）" style="width: 100%">
                          <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                        </el-select>
                        <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="参赛赛道" prop="track">
                        <el-autocomplete
                          v-model="form.track"
                          :fetch-suggestions="queryTrackSearch"
                          clearable
                          placeholder="请输入或选择赛道（如：Java组、数学类等）"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="证书编号" prop="certificateNo">
                        <el-input v-model="form.certificateNo" placeholder="请输入证书上的编号" />
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="参赛组别" prop="groupId">
                        <el-select v-model="form.groupId" placeholder="请选择参赛组别" style="width: 100%">
                          <el-option v-for="dict in group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                        </el-select>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="团队名称" prop="teamName">
                        <el-input v-model="form.teamName" placeholder="请输入团队名称（个人参赛可不填）" />
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="作品名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入获奖作品名称" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
                  <el-form-item label="是否申请报销" prop="isReimburse">
                    <div style="display: flex; align-items: center; justify-content: flex-start; width: 100%;">
                      <el-radio-group v-model="form.isReimburse">
                        <el-radio :label="1">是 (需要上传凭证)</el-radio>
                        <el-radio :label="0">否</el-radio>
                      </el-radio-group>
                      <div v-if="form.isReimburse === 1 && form.achievementId" style="margin-left: 15px; display: flex; align-items: center;">
                        <span style="font-size: 12px; color: #909399; margin-right: 8px;">报销状态:</span>
                      <div style="transform: scale(1.5); transform-origin: left center;">
  <dict-tag :options="reimbursement_status" :value="actualReimbursementStatus" />
</div>
                      </div>
                    </div>
                    <div style="color: #F56C6C; font-size: 12px; margin-top: 5px; line-height: 1.2; font-weight: bold;">
                      如果报名者没有通过其他途径报销，请上传发票（PDF）和填写报名金额。注意：同一张发票只能报销一次
                    </div>
                  </el-form-item>

                  <el-row v-if="form.isReimburse === 1">
                    <el-col :span="24">
                      <el-form-item label="报名金额" prop="fee">
                        <el-input v-model="form.fee" placeholder="请输入报名费金额" :disabled="readOnly">
                          <template #append>元</template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-divider content-position="center">参赛选手信息</el-divider>
                  <el-row :gutter="10" class="mb8" v-if="canEditMemberList">
                    <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddParticipantDialog">添加学生</el-button></el-col>
                    <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteParticipant">删除选中</el-button></el-col>
                  </el-row>
                    <el-table ref="participantTable" :data="samAchievementParticipantList" border style="width: 100%; margin-bottom: 20px;" :row-class-name="tableRowClassName" @selection-change="handleParticipantSelectionChange">
                      <el-table-column v-if="canEditMemberList" width="40" align="center">
                        <template #default="scope">
                          <el-icon v-if="!scope.row.isFixed" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                        </template>
                      </el-table-column>
                      <el-table-column v-if="canEditMemberList" type="selection" width="50" align="center" />
                      <el-table-column label="学生学号" prop="studentId" align="center" />
                      <el-table-column label="姓名" prop="studentName" align="center" />
                      <el-table-column label="操作" align="center" width="100" v-if="canEditMemberList">
                        <template #default="scope">
                          <el-button v-if="!scope.row.isFixed" link type="primary" :icon="Edit" @click="handleEditParticipant(scope.row, scope.$index)">修改</el-button>
                        </template>
                      </el-table-column>
                      <el-table-column label="排序" prop="orderNo" width="80" align="center" />
                      <el-table-column label="是否负责人" prop="manager" width="150" align="center">
                        <template #default="scope">
                          <el-tag :type="scope.row.manager == 1 ? 'success' : 'info'">{{ scope.row.manager == 1 ? '是' : '否' }}</el-tag>
                        </template>
                      </el-table-column>
                    </el-table>

                    <el-divider content-position="center">指导老师信息</el-divider>
                    <el-row :gutter="10" class="mb8" v-if="canEditMemberList">
                      <el-col :span="1.5"><el-button type="primary" :icon="Plus" @click="openAddAdvisorDialog">添加老师</el-button></el-col>
                      <el-col :span="1.5"><el-button type="danger" :icon="Delete" @click="handleDeleteAdvisor">删除选中</el-button></el-col>
                    </el-row>
                    <el-table ref="advisorTable" :data="samAchievementAdvisorList" border style="width: 100%;" :row-class-name="tableRowClassName" @selection-change="handleAdvisorSelectionChange">
                     <el-table-column v-if="canEditMemberList" width="40" align="center">
                        <template #default="scope">
                          <el-icon v-if="scope.$index !== 0" class="drag-handle" style="cursor: move"><Rank /></el-icon>
                        </template>
                      </el-table-column>
                      <el-table-column v-if="canEditMemberList" type="selection" width="50" align="center" />
                      <el-table-column label="教师工号" prop="teacherId" align="center" />
                      <el-table-column label="姓名" prop="teacherName" align="center" />
                      <el-table-column label="操作" align="center" width="100" v-if="canEditMemberList">
                        <template #default="scope">
                          <el-button v-if="!scope.row.isFixed" link type="primary" :icon="Edit" @click="handleEditAdvisor(scope.row, scope.$index)">修改</el-button>
                        </template>
                      </el-table-column>
                      <el-table-column label="排序" prop="orderNo" width="80" align="center" />
                    </el-table>
                </el-col>

                <el-col :span="12">
                  <div class="attach-card">
                    <el-divider content-position="left">附件管理</el-divider>
                    <el-tabs tab-position="left" style="height: 100%; min-height: 700px;" v-model="activeAttachmentTab">
                      <el-tab-pane v-for="item in visibleAttachments" :key="item.name" :label="item.label" :name="item.name">
                        <div class="upload-pane-content">
                          <!-- 【修改】：参赛作品与比赛照片的特殊处理 -->
                          <template v-if="item.name === 'work' || item.name === 'photo'">
                            <div style="margin-bottom: 15px; background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #ebeef5;">
                              <div style="font-weight: bold; margin-bottom: 10px; color: #303133;">
                                {{ item.name === 'work' ? '是否有作品照片' : '是否有比赛照片' }}
                              </div>
                              <el-radio-group v-model="form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto']" @change="(val) => handleHasFileChange(item.name, val)">
                                <el-radio :label="1">有 (需上传至少5张PDF)</el-radio>
                                <el-radio :label="0">无 (需上传手写声明PDF)</el-radio>
                              </el-radio-group>

                              <div v-if="form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 0" style="margin-top: 10px; padding: 10px; background: #fdf6ec; border-radius: 4px; border: 1px solid #faecd8;">
                                <el-icon style="vertical-align: middle; color: #e6a23c; margin-right: 5px;"><InfoFilled /></el-icon>
                                <span style="font-size: 13px; color: #e6a23c;">
                                  请在纸上手写声明（包含作品名称/比赛名称、作者姓名、日期、声明无误等信息），拍照并转成PDF上传。
                                </span>
                              </div>
                            </div>
                          </template>

                          <!-- 【修改】：根据是否为多图上传显示不同的提示 -->
                          <el-alert v-if="!item.isMultiple && !form[item.prop]" :type="item.type || 'info'" :closable="false" class="mb10">
                            <template #title>
                              {{ item.alert }}
                              <el-button link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                            </template>
                          </el-alert>
                          <el-alert v-if="item.isMultiple && (!form[item.prop] || (form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 1 ? form[item.prop].length < 5 : form[item.prop].length < 1))" type="warning" :closable="false" class="mb10">
                            <template #title>
                              {{ item.alert }} (当前已上传: {{ form[item.prop] ? form[item.prop].length : 0 }} 张)
                              <el-button v-if="!((item.name === 'work' || item.name === 'photo') && form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 1)" link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                            </template>
                          </el-alert>

                          <el-form-item label-width="0" :prop="item.prop">
                            <file-upload
                              v-if="!readOnly && uploadUnlocked[item.name] && (item.isMultiple || !form[item.prop])"
                              v-model="form[item.prop]"
                              :limit="item.limit || 1"
                              :fileSize="10"
                              :fileType="item.fileType || ['pdf']"
                              class="hide-file-list"
                              :upload-url="uploadUrl"
                            />

                            <div v-if="!readOnly && (item.isMultiple || !form[item.prop]) && !uploadUnlocked[item.name]" class="fake-upload-box" @click="handlePreUpload(item.name)">
                               <el-icon :size="30" color="#C0C4CC"><UploadFilled /></el-icon>
                               <div style="color: #606266; margin-top: 10px">点击上传文件</div>
                               <div style="font-size: 12px; color: #E6A23C; margin-top: 5px">(点击后需先阅读示例)</div>
                            </div>

                            <!-- 循环显示多个文件预览和操作行 -->
                           <template v-if="item.isMultiple && getFileList(form[item.prop]).length > 0">
  <div style="display: flex; flex-wrap: wrap; gap: 10px;">
    <div v-for="(uuid) in getFileList(form[item.prop])" :key="uuid"
                                     :style="{
                                       marginBottom: '20px', 
                                       border: '1px dashed #ccc', 
                                       padding: '10px',
                                      width: ((item.name === 'work' && form.hasFileWork === 1) || (item.name === 'photo' && form.hasFilePhoto === 1)) ? 'calc(50% - 5px)' : '100%',
                                       boxSizing: 'border-box'
                                     }">
                                 <div v-if="previewUrls[item.name] && previewUrls[item.name][uuid]" class="preview-box">
                               <iframe :src="previewUrls[item.name][uuid]" width="100%"
                                            :height="((item.name === 'work' && form.hasFileWork === 1) || (item.name === 'photo' && form.hasFilePhoto === 1)) ? '200px' : '650px'" 
                                            frameborder="0"></iframe>
                                  </div>
                                  <div class="custom-file-row" style="flex-direction: column; align-items: flex-start;">
                                    <div class="file-name" style="font-size: 12px;"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(uuid) }}</span></div>
                                    <div class="file-action" style="justify-content: flex-start; gap: 5px;">
                                      <el-button :disabled="false" link type="primary" :icon="View" @click="handleOpenDetail(uuid)" style="font-size: 12px; padding: 0;">详情</el-button>
                                      <el-button :disabled="false" link type="primary" :icon="Download" @click="handleDownload(uuid)" style="font-size: 12px; padding: 0;">下载</el-button>
                                      <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="handleRemoveFile(item.prop, uuid)" style="font-size: 12px; padding: 0;">删除</el-button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </template>
                            
                            <!-- 单个文件预览 -->
                            <template v-else-if="!item.isMultiple && form[item.prop]">
                                <div v-if="previewUrls[item.name]" class="preview-box">
                                  <iframe :src="previewUrls[item.name]" width="100%" height="650px" frameborder="0"></iframe>
                                </div>
                                <div class="custom-file-row">
                                  <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form[item.prop]) }}</span></div>
                                  <div class="file-action">
                                    <el-button :disabled="false" link type="primary" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                                    <el-button :disabled="false" link type="primary" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
                                    <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form[item.prop] = null">删除</el-button>
                                  </div>
                                </div>
                            </template>
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
    <div class="outcome-body" v-loading="detailLoading">
      <el-form
        ref="outcomeRefDialog"
        :model="form"
        :rules="rules"
        label-width="110px"
        :disabled="readOnly"
      >
        <el-row :gutter="20">
            <el-col :span="12">
              <el-row>
                <el-col :span="24">
                  <el-form-item label="参加比赛" prop="competitionId">
                    <el-select 
                      v-model="form.competitionId" 
                      placeholder="第一步：请选择您参加的赛事" 
                      filterable 
                      clearable 
                      style="width: 100%"
                      @change="handleCompetitionChange"
                    >
                      <el-option v-for="item in competitionOptions" :key="item.competitionId" :label="item.competitionName" :value="item.competitionId" />
                    </el-select>
                    <div style="width: 100%; display: flex; flex-direction: column;">
                      <div style="margin-top: 5px; line-height: 1.2;">
                        <el-link type="primary" :disabled="false" @click="goToCompetitionApply">比赛找不到？点击这里申请赛事！</el-link>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="获奖时间" prop="awardTime">
                    <el-date-picker clearable v-model="form.awardTime" type="date" value-format="YYYY-MM-DD" placeholder="第二步：请按照证书上的日期选择" style="width: 100%" />
                    <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">获奖时间为奖状上日期为准，若只有年月，请填写当月最后一天。</div>
                  </el-form-item>
                </el-col>

                <template v-if="showReviewExtraFields">
                  <el-col :span="24">
                    <el-form-item label="年份" prop="year">
                      <el-input
                        v-model="form.year"
                        placeholder="请输入年份"
                        clearable
                        maxlength="4"
                        :disabled="readOnly || !canEditReviewYear"
                        style="width: 100%"
                        @input="form.year = String(form.year || '').replace(/\\D/g, '').slice(0, 4)"
                      />
                      <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">
                        根据获奖时间自动带出年份，仅管理员可修改。
                      </div>
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="是否补录" prop="isSupplement">
                      <el-radio-group v-model="form.isSupplement" :disabled="readOnly || !canEditReviewMeta">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                </template>

                <el-col :span="24">
                  <el-form-item label="获奖级别" prop="level">
                    <el-select v-model="form.level" placeholder="第三步：请选择获奖级别（国家级/省级等）" style="width: 100%">
                      <el-option v-for="dict in award_level_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                    </el-select>
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="比赛届次" prop="sessionId">
                    <el-select
                      v-model="form.sessionId"
                      placeholder="系统将根据前三步自动匹配锁定"
                      filterable
                      clearable
                      style="width: 100%"
                      :disabled="readOnly || !!form.competitionId"
                    >
                      <el-option v-for="item in sessionOptions" :key="item.id" :label="item.session" :value="item.id" />
                    </el-select>
                    <div style="width: 100%; display: flex; flex-direction: column;">
                      <div v-if="isAutoMatched" style="color: #67C23A; font-size: 12px; margin-top: 5px;">
                        <el-icon style="vertical-align: middle;"><CircleCheck /></el-icon> 已根据时间与级别自动锁定届次
                      </div>
                      <div style="margin-top: 5px; line-height: 1.2;">
                        <el-link type="primary" :disabled="false" @click="goToCompetitionApply">届次找不到？点击这里申请赛事届次！</el-link>
                      </div>
                    </div>
                    <div v-if="form.competitionId && form.level && form.awardTime && !form.sessionId" style="color: #F56C6C; font-size: 12px; margin-top: 5px;">
                      <el-icon style="vertical-align: middle;"><Warning /></el-icon> 未找到匹配的届次，请核对获奖时间与级别
                    </div>
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="奖项等级" prop="grade">
                    <el-select v-model="form.grade" placeholder="请选择奖项等级（特等奖/一等奖等）" style="width: 100%">
                      <el-option v-for="dict in award_rank" :key="dict.value" :label="dict.label" :value="dict.value" />
                    </el-select>
                    <div style="color: #909399; font-size: 12px; margin-top: 5px; line-height: 1.2;">如果比赛或者表彰没有区分等级，请选择一等奖。</div>
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="参赛赛道" prop="track">
                    <el-autocomplete
                      v-model="form.track"
                      :fetch-suggestions="queryTrackSearch"
                      clearable
                      placeholder="请输入或选择赛道（如：Java组、数学类等）"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="证书编号" prop="certificateNo">
                    <el-input v-model="form.certificateNo" placeholder="请输入证书上的编号" />
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="参赛组别" prop="groupId">
                    <el-select v-model="form.groupId" placeholder="请选择参赛组别" style="width: 100%">
                      <el-option v-for="dict in group_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                    </el-select>
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="团队名称" prop="teamName">
                    <el-input v-model="form.teamName" placeholder="请输入团队名称（个人参赛可不填）" />
                  </el-form-item>
                </el-col>

                <el-col :span="24">
                  <el-form-item label="作品名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入获奖作品名称" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-divider content-position="left"><i class="el-icon-money"></i> 报销申请</el-divider>
              <el-form-item label="是否申请报销" prop="isReimburse">
                <div style="display: flex; align-items: center; justify-content: flex-start; width: 100%;">
                  <el-radio-group v-model="form.isReimburse">
                    <el-radio :label="1">是 (需要上传凭证)</el-radio>
                    <el-radio :label="0">否</el-radio>
                  </el-radio-group>
                  <div v-if="form.isReimburse === 1 && form.achievementId" style="margin-left: 15px; display: flex; align-items: center;">
                    <span style="font-size: 12px; color: #909399; margin-right: 8px;">报销状态:</span>
                  <div style="transform: scale(1.5); transform-origin: left center;">
  <dict-tag :options="reimbursement_status" :value="actualReimbursementStatus" />
</div>
                  </div>
                </div>
                <div style="color: #F56C6C; font-size: 12px;font-weight: bold;">
                  如果报名者没有通过其他途径报销，请上传发票（PDF）和填写报名金额。注意：同一张发票只能报销一次且是否报销要按规定最终决定。
                </div>
              </el-form-item>

              <el-row v-if="form.isReimburse === 1">
                <el-col :span="24">
                  <el-form-item label="报名金额" prop="fee">
                    <el-input v-model="form.fee" placeholder="请输入报名费金额" :disabled="readOnly">
                      <template #append>元</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

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
                <el-table-column label="操作" align="center" width="100" v-if="!readOnly">
                  <template #default="scope">
                    <el-button v-if="!scope.row.isFixed" link type="primary" :icon="Edit" @click="handleEditParticipant(scope.row, scope.$index)">修改</el-button>
                  </template>
                </el-table-column>
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
                <el-table-column label="操作" align="center" width="100" v-if="!readOnly">
                  <template #default="scope">
                    <el-button v-if="!scope.row.isFixed" link type="primary" :icon="Edit" @click="handleEditAdvisor(scope.row, scope.$index)">修改</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="排序" prop="orderNo" width="100" align="center" />
              </el-table>
            </el-col>

            <el-col :span="12">
              <div class="attach-card">
                <el-divider content-position="left">附件管理</el-divider>
                <el-tabs tab-position="left" style="height: 100%; min-height: 700px;" v-model="activeAttachmentTab">
                  <el-tab-pane v-for="item in visibleAttachments" :key="item.name" :label="item.label" :name="item.name">
                    <div class="upload-pane-content">
                    <!-- 【修改】：参赛作品与比赛照片的特殊处理 -->
                    <template v-if="item.name === 'work' || item.name === 'photo'">
                      <div style="margin-bottom: 15px; background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #ebeef5;">
                        <div style="font-weight: bold; margin-bottom: 10px; color: #303133;">
                          {{ item.name === 'work' ? '是否有作品照片' : '是否有比赛照片' }}
                        </div>
                        <el-radio-group v-model="form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto']" @change="(val) => handleHasFileChange(item.name, val)">
                          <el-radio :label="1">有 (需上传至少5张PDF)</el-radio>
                          <el-radio :label="0">无 (需上传手写声明PDF)</el-radio>
                        </el-radio-group>
                        
                        <div v-if="form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 0" style="margin-top: 10px; padding: 10px; background: #fdf6ec; border-radius: 4px; border: 1px solid #faecd8;">
                          <el-icon style="vertical-align: middle; color: #e6a23c; margin-right: 5px;"><InfoFilled /></el-icon>
                          <span style="font-size: 13px; color: #e6a23c;">
                            请在纸上手写声明（包含作品名称/比赛名称、作者姓名、日期、声明无误等信息），拍照并转成PDF上传。
                          </span>
                        </div>
                      </div>
                    </template>

                    <el-alert v-if="!item.isMultiple && !form[item.prop]" :type="item.type || 'info'" :closable="false" class="mb10">
                      <template #title>
                        {{ item.alert }} 
                        <el-button link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                      </template>
                    </el-alert>
                    <el-alert v-if="item.isMultiple && (!form[item.prop] || (form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 1 ? form[item.prop].length < 5 : form[item.prop].length < 1))" type="warning" :closable="false" class="mb10">
                      <template #title>
                        {{ item.alert }} (当前已上传: {{ form[item.prop] ? form[item.prop].length : 0 }} 张)
                        <el-button v-if="!((item.name === 'work' || item.name === 'photo') && form[item.name === 'work' ? 'hasFileWork' : 'hasFilePhoto'] === 1)" link type="primary" style="margin-left: 10px" @click="handlePreUpload(item.name)">查看上传示例</el-button>
                      </template>
                    </el-alert>
                      
                      <el-form-item label-width="0" :prop="item.prop">
                        <file-upload 
                          v-if="!readOnly && uploadUnlocked[item.name] && (item.isMultiple || !form[item.prop])"
                          v-model="form[item.prop]" 
                          :limit="item.limit || 1" 
                          :fileSize="10" 
                          :fileType="item.fileType || ['pdf']" 
                          class="hide-file-list" 
                          :upload-url="uploadUrl" 
                        />
                        <div v-if="!readOnly && (item.isMultiple || !form[item.prop]) && !uploadUnlocked[item.name]" class="fake-upload-box" @click="handlePreUpload(item.name)">
                           <el-icon :size="30" color="#C0C4CC"><UploadFilled /></el-icon>
                           <div style="color: #606266; margin-top: 10px">点击上传文件</div>
                           <div style="font-size: 12px; color: #E6A23C; margin-top: 5px">(点击后需先阅读示例)</div>
                        </div>

                        <!--循环显示多个文件预览和操作行 -->
                        <template v-if="item.isMultiple && getFileList(form[item.prop]).length > 0">
  <div style="display: flex; flex-wrap: wrap; gap: 10px;">
    <div v-for="(uuid) in getFileList(form[item.prop])" :key="uuid"
                                 :style="{
                                   marginBottom: '20px', 
                                   border: '1px dashed #ccc', 
                                   padding: '10px',
                                  width: ((item.name === 'work' && form.hasFileWork === 1) || (item.name === 'photo' && form.hasFilePhoto === 1)) ? 'calc(50% - 5px)' : '100%',
                                   boxSizing: 'border-box'
                                 }">
                              <div v-if="previewUrls[item.name] && previewUrls[item.name][uuid]" class="preview-box">
                              <iframe :src="previewUrls[item.name][uuid]" width="100%"
                                        :height="((item.name === 'work' && form.hasFileWork === 1) || (item.name === 'photo' && form.hasFilePhoto === 1)) ? '200px' : '650px'" 
                                        frameborder="0"></iframe>
                              </div>
                              <div class="custom-file-row" style="flex-direction: column; align-items: flex-start;">
                                <div class="file-name" style="font-size: 12px;"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(uuid) }}</span></div>
                                <div class="file-action" style="justify-content: flex-start; gap: 5px;">
                                  <el-button :disabled="false" link type="primary" :icon="View" @click="handleOpenDetail(uuid)" style="font-size: 12px; padding: 0;">详情</el-button>
                                  <el-button :disabled="false" link type="primary" :icon="Download" @click="handleDownload(uuid)" style="font-size: 12px; padding: 0;">下载</el-button>
                                  <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="handleRemoveFile(item.prop, uuid)" style="font-size: 12px; padding: 0;">删除</el-button>
                                </div>
                              </div>
                            </div>
                          </div>
                        </template>

                        <!-- 单个文件预览 -->
                        <template v-else-if="!item.isMultiple && form[item.prop]">
                            <div v-if="previewUrls[item.name]" class="preview-box">
                              <iframe :src="previewUrls[item.name]" width="100%" height="650px" frameborder="0"></iframe>
                            </div>
                            <div v-if="form[item.prop]" class="custom-file-row">
                              <div class="file-name"><el-icon class="mr5"><Document /></el-icon><span>{{ getFileName(form[item.prop]) }}</span></div>
                              <div class="file-action">
                                <el-button :disabled="false" link type="primary" :icon="View" @click="handleOpenDetail(form[item.prop])">详情</el-button>
                                <el-button :disabled="false" link type="primary" :icon="Download" @click="handleDownload(form[item.prop])">下载</el-button>
                                <el-button v-if="!readOnly" link type="danger" :icon="Delete" @click="form[item.prop] = null">删除</el-button>
                              </div>
                            </div>
                        </template>
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
          <el-button
            v-if="showSubmit && !readOnly"
            type="primary"
            @click="submitForm"
          >
            {{ submitTextComputed }}
          </el-button>
          <el-button @click="handleCancel">{{ cancelText }}</el-button>
        </div>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="exampleVisible"
    :fullscreen="isFullscreen"
    :width="isFullscreen ? '100%' : '1200px'"
    append-to-body
    destroy-on-close
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center; padding-right: 30px;">
        <span class="el-dialog__title">上传文件要求与示例</span>
        <el-button link @click="isFullscreen = !isFullscreen">
          <el-icon><component :is="isFullscreen ? 'BottomLeft' : 'FullScreen'" /></el-icon>
          <span style="margin-left: 5px;">{{ isFullscreen ? '缩小' : '全屏' }}</span>
        </el-button>
      </div>
    </template>
    <div :style="{ height: isFullscreen ? 'calc(100vh - 150px)' : '650px', display: 'flex', flexDirection: 'column' }">
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
        <div style="font-size: 12px; color: #909399; margin-top: 5px; display: flex; justify-content: space-between;">
          <span>支持学号或姓名双向查找</span>
          <el-link type="primary" style="font-size: 12px;" @click="isParticipantNew = true">没有找到？手动录入</el-link>
        </div>
      </el-form-item>
      <el-form-item label="学号" prop="studentId">
        <el-input v-model="participantForm.studentId" placeholder="学号" :disabled="!isParticipantNew" />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input v-model="participantForm.studentName" placeholder="姓名" :disabled="!isParticipantNew" />
      </el-form-item>
      
      <el-form-item v-if="!isParticipantNew && participantForm.studentId" label="所属机构">
        <div style="font-size: 13px; color: #606266; line-height: 1.4;">
          {{ getDeptName(participantForm.school) }} / {{ getDeptName(participantForm.department) }} / {{ getDeptName(participantForm.major) }}
        </div>
      </el-form-item>

      <template v-if="isParticipantNew">
        <el-alert v-if="editingParticipantIndex === -1" title="未匹配到该学号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="所属机构" prop="school">
          <el-cascader
            ref="participantCascader"
            v-model="participantDeptCascaderValue"
            :options="studentDeptOptions"
            :props="{ value: 'deptName', label: 'deptName', children: 'children' }"
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
        <div style="font-size: 12px; color: #909399; margin-top: 5px; display: flex; justify-content: space-between;">
          <span>支持工号或姓名双向查找</span>
          <el-link type="primary" style="font-size: 12px;" @click="isAdvisorNew = true">没有找到？手动录入</el-link>
        </div>
      </el-form-item>
      <el-form-item label="工号" prop="teacherId">
        <el-input v-model="advisorForm.teacherId" placeholder="工号" :disabled="!isAdvisorNew" />
      </el-form-item>
      <el-form-item label="姓名" prop="teacherName">
        <el-input v-model="advisorForm.teacherName" placeholder="姓名" :disabled="!isAdvisorNew" />
      </el-form-item>
      
      <el-form-item v-if="!isAdvisorNew && advisorForm.teacherId" label="所属机构">
        <div style="font-size: 13px; color: #606266; line-height: 1.4;">
         {{ getDeptName(advisorForm.school) }}
        </div>
      </el-form-item>

      <template v-if="isAdvisorNew">
        <el-alert v-if="editingAdvisorIndex === -1" title="未匹配到该工号，请完善下方信息完成录入" type="warning" show-icon :closable="false" style="margin-bottom: 15px;" />
        <el-form-item label="所属机构" prop="school">
          <el-cascader
            ref="advisorCascader"
            v-model="advisorDeptCascaderValue"
            :options="advisorDeptOptions"
            :props="{ value: 'deptName', label: 'deptName', children: 'children' }"
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
      <el-table-column label="学号" prop="no" align="center" width="120" />
      <el-table-column label="姓名" prop="name" align="center" width="100" />
      <el-table-column label="所属机构" align="center">
        <template #default="scope">
          <div style="font-size: 12px; color: #606266;">
            {{ getDeptName(scope.row.school) }} / {{ getDeptName(scope.row.department) }} / {{ getDeptName(scope.row.major) }}
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <div style="text-align: center;">
        <el-button @click="studentSelectVisible = false; isParticipantNew = true">以上都不是，创建新学生</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog title="选择老师" v-model="teacherSelectVisible" width="600px" append-to-body>
    <el-table :data="teacherOptions" @row-click="selectTeacher" border highlight-current-row>
      <el-table-column label="工号" prop="no" align="center" width="120" />
      <el-table-column label="姓名" prop="teacherName" align="center" width="100" />
      <el-table-column label="所属机构" align="center">
        <template #default="scope">
          <div style="font-size: 12px; color: #606266;">
            {{ getDeptName(scope.row.school) }} / {{ getDeptName(scope.row.department) }}
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <div style="text-align: center;">
        <el-button @click="teacherSelectVisible = false; isAdvisorNew = true">以上都不是，创建新老师</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="AchievementForm">
import { getCurrentInstance, ref, reactive, toRefs, computed, onMounted, onUnmounted, watch, nextTick } from "vue";
import { useRoute, onBeforeRouteLeave } from "vue-router";
import Sortable from "sortablejs";
import useUserStore from "@/store/modules/user";
import {
  Plus,
  Delete,
  Document,
  Download,
  View,
  UploadFilled,
  Rank,
  InfoFilled,
  Warning,
  Edit,
  Search,
  CircleCheck,
  FullScreen,
  BottomLeft,
} from "@element-plus/icons-vue";
import{
  listStudent,
  getStudent,
  delStudent,
  addStudent,
  updateStudent,
} from "@/api/achievement/student";
import { listTracks } from "@/api/achievement/manage";
import { listTeacher, addTeacher, updateTeacher } from "@/api/achievement/teacher";
import { updateSession } from "@/api/session/session";
import { listDept } from "@/api/system/dept";
import { handleTree, blobValidate } from "@/utils/ruoyi";
import request from "@/utils/request";
import FileUpload from "@/components/FileUpload";

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
  titleDetail: { type: String, default: "成果详情" },
  submitText: { type: String, default: "" },
  cancelText: { type: String, default: "取 消" },
  userRole: { type: String, default: "student" },
  sourceMode: { type: String, default: "" },
  reviewSource: { type: String, default: "" },
});

const {
  achievement_category,
  group_type,
  award_rank,
  award_level_type,
  attach_type,
  reimbursement_status,
} =
  proxy.useDict(
    "achievement_category",
    "group_type",
    "award_rank",
    "award_level_type",
    "attach_type",
    "reimbursement_status"
  );
const isPageMode = computed(() => props.pageMode);
const normalizedUserRoles = computed(() =>
  (userStore.roles || []).map((role) =>
    String(role || "").replace(/^ROLE_/, "").toLowerCase()
  )
);
const isAdminUser = computed(() => normalizedUserRoles.value.includes("admin"));
const currentReviewSource = computed(() =>
  String(props.reviewSource || route.query.source || "").trim().toLowerCase()
);
const showReviewExtraFields = computed(() => !!currentReviewSource.value);
const canEditReviewMeta = computed(
  () => showReviewExtraFields.value && !props.readOnly
);
const canEditReviewYear = computed(
  () => canEditReviewMeta.value && isAdminUser.value
);
const visible = ref(false);
const title = ref("");
const deptOptions = ref([]);
const studentDeptOptions = ref([]);
const advisorDeptOptions = ref([]);
const outcomeRefPage = ref(null);
const outcomeRefDialog = ref(null);
const activeAttachmentTab = ref("award");
const detailLoading = ref(false);
const pendingAchievementId = ref("");
const loadingFormDetail = ref(false);

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
const isAutoMatched = ref(false);

const studentSelectVisible = ref(false);
const studentOptions = ref([]);
const participantSearchKeyword = ref("");


const participantDeptCascaderValue = ref([]);

const editingParticipantIndex = ref(-1);
const editingAdvisorIndex = ref(-1);

function handleEditParticipant(row, index) {
  editingParticipantIndex.value = index;
  isParticipantNew.value = true;
  const studentId = row.studentId || row.studentNo;
  participantForm.value = {
    studentId: studentId, // 业务学号
    dbId: row.id || row.studentId, // 记录数据库主键
    studentName: row.studentName,
    school: row.school,
    department: row.department,
    major: row.major,
    class_name: row.class_name || row.className,
    class_year: row.class_year || row.classYear
  };

  if (studentId) {
    listStudent({ no: studentId }).then(res => {
      if (res.rows && res.rows.length > 0) {
        const s = res.rows[0];
        // 关键：保存后端返回的真实主键 studentId
        participantForm.value.dbId = s.studentId;
        participantForm.value.school = s.school;
        participantForm.value.department = s.department;
        participantForm.value.major = s.major;
        participantForm.value.class_name = s.className || s.class_name;
        participantForm.value.class_year = s.classYear || s.class_year;
        const values = [];
        if (s.school) values.push(s.school);
        if (s.department) values.push(s.department);
        if (s.major) values.push(s.major);
        participantDeptCascaderValue.value = values;
      }
    });
  }
  addParticipantVisible.value = true;
}

function handleEditAdvisor(row, index) {
  editingAdvisorIndex.value = index;
  isAdvisorNew.value = true;
  const teacherId = row.teacherId || row.teacherNo;

  advisorForm.value = {
    teacherId: teacherId,
    dbId: row.id || row.teacherId, // 记录数据库主键
    teacherName: row.teacherName,
    school: row.school,
    department: row.department
  };

  if (teacherId) {
    listTeacher({ no: teacherId }).then(res => {
      if (res.rows && res.rows.length > 0) {
        const t = res.rows[0];
        // 关键修复：兼容获取后端返回的主键 ID
        advisorForm.value.dbId = t.id || t.teacherId;
        advisorForm.value.school = t.school || t.department;
        advisorForm.value.department = t.department || t.major;
        const values = [];
        if (advisorForm.value.school) values.push(advisorForm.value.school);
        if (advisorForm.value.department) values.push(advisorForm.value.department);
        advisorDeptCascaderValue.value = values;
      }
    });
  }
  addAdvisorVisible.value = true;
}

function handleParticipantCascaderChange(value) {
  if (value && value.length >= 3) {
    // Starting from Level 2: value[0] is school (College), value[1] is department (Dept), value[2] is major (Major)
    participantForm.value.school = value[0] || '';
    participantForm.value.department = value[1] || '';
    participantForm.value.major = value[2] || '';

    // Capture labels for display
    const nodes = proxy.$refs.participantCascader.getCheckedNodes();
    if (nodes && nodes.length > 0) {
      const labels = nodes[0].pathLabels; // pathLabels corresponds to the values in 'value'
      participantForm.value.schoolName = labels[0] || '-';
      participantForm.value.deptName = labels[1] || '-';
      participantForm.value.majorName = labels[2] || '-';
    }
  } else {
    participantForm.value.school = '';
    participantForm.value.department = '';
    participantForm.value.major = '';
    participantForm.value.schoolName = '';
    participantForm.value.deptName = '';
    participantForm.value.majorName = '';
  }
}

function handleAdvisorCascaderChange(value) {
  if (value && value.length >= 2) {
    // Starting from Level 2: value[0] is school (College), value[1] is department (Dept)
    advisorForm.value.school = value[0] || '';
    advisorForm.value.department = value[1] || '';

    // Capture labels for display
    const nodes = proxy.$refs.advisorCascader.getCheckedNodes();
    if (nodes && nodes.length > 0) {
      const labels = nodes[0].pathLabels;
      advisorForm.value.schoolName = labels[0] || '-';
      advisorForm.value.deptName = labels[1] || '-';
    }
  } else {
    advisorForm.value.school = '';
    advisorForm.value.department = '';
    advisorForm.value.schoolName = '';
    advisorForm.value.deptName = '';
  }
}

const data = reactive({
  form: {
    competitionId: null,
    achievementId: null,
    sessionId: null,
    category: "3",
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    ownerDepId: null,
    awardTime: null,
    fee: null,
    reimbursementFee: null,
    isReimburse: 0,
    fileAward: null,
    fileNotice: null,
    fileWork: [],
    filePhoto: [],
    filePayment: null,
    fileInvoice: null,
    fileReceiptCode: null,
    hasFileWork: 1,
    hasFilePhoto: 1
  },
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
    fee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }],
    reimbursementFee: [{ pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: "请输入正确的金额" }]
  }
});
const { form, formSnapshot, rules } = toRefs(data);

const actualReimbursementStatus = computed(() => {
  // 有报销时间内容才是已报销 (字典标签中 1 通常对应已报销，0 对应进行中/未报销)
  if (form.value.reimbursementDate) {
    const alreadyReimbursed = (reimbursement_status.value || []).find(d => d.label === '已报销');
    return alreadyReimbursed ? alreadyReimbursed.value : '1';
  }
  const inProgress = (reimbursement_status.value || []).find(d => d.label === '进行中' || d.label === '未报销');
  return inProgress ? inProgress.value : '0';
});

const currentSession = computed(() => {
  if (!form.value.sessionId || !sessionOptions.value.length) return null;
  return sessionOptions.value.find(s => s.id === form.value.sessionId);
});

const isNoticeFromSession = computed(() => {
  return !!(currentSession.value && currentSession.value.uuid);
});

const displayAchievementId = computed(() => {
  const formId = form.value?.achievementId;
  if (formId !== null && formId !== undefined && String(formId).trim() !== "") {
    return String(formId);
  }
  if (
    pendingAchievementId.value !== null &&
    pendingAchievementId.value !== undefined &&
    String(pendingAchievementId.value).trim() !== ""
  ) {
    return String(pendingAchievementId.value);
  }
  const routeId =
    route.query.id || route.query.achievementId || route.params.id;
  if (
    routeId !== null &&
    routeId !== undefined &&
    String(routeId).trim() !== ""
  ) {
    return String(routeId);
  }
  return "";
});

function trimIfString(value) {
  return typeof value === "string" ? value.trim() : value;
}

function trimStringFields(target, fields = []) {
  if (!target || !Array.isArray(fields)) return;
  fields.forEach((field) => {
    if (Object.prototype.hasOwnProperty.call(target, field)) {
      target[field] = trimIfString(target[field]);
    }
  });
}

function extractYearFromAwardTime(value) {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "string") {
    const match = value.match(/^(\d{4})/);
    return match ? Number(match[1]) : null;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return null;
  return date.getFullYear();
}

function normalizeIntegerOrNull(value) {
  if (value === null || value === undefined || value === "") return null;
  const normalized = Number(value);
  return Number.isFinite(normalized) ? normalized : null;
}

function syncYearFromAwardTime(awardTime) {
  form.value.year = extractYearFromAwardTime(awardTime);
}

const trackSuggestions = ref([]);

function normalizeTrackSuggestions(rows = []) {
  return Array.from(
    new Set(
      (rows || [])
        .map((item) => trimIfString(item))
        .filter(Boolean)
    )
  ).map((value) => ({ value }));
}

function loadTrackSuggestions(competitionId, sessionId) {
  if (!competitionId || !sessionId) {
    trackSuggestions.value = [];
    return;
  }
  listTracks(competitionId, sessionId)
    .then((response) => {
      const rows = response?.data || response?.rows || [];
      trackSuggestions.value = normalizeTrackSuggestions(rows);
    })
    .catch(() => {
      trackSuggestions.value = [];
    });
}

function queryTrackSearch(queryString, cb) {
  const keyword = String(queryString || "").trim().toLowerCase();
  const results = keyword
    ? trackSuggestions.value.filter((item) =>
        item.value.toLowerCase().includes(keyword)
      )
    : trackSuggestions.value;
  cb(results);
}

watch(() => [form.value.competitionId, form.value.sessionId], ([compId, sessionId]) => {
  loadTrackSuggestions(compId, sessionId);
  // 当比赛和届次都选择后，尝试从届次信息中提取比赛通知 UUID 并回显
  if (compId && sessionId && sessionOptions.value.length > 0) {
    const session = sessionOptions.value.find(s => s.id === sessionId);
    if (session && session.uuid) {
      form.value.fileNotice = session.uuid;
    }
  }
});

const validateCertificateNo = (rule, value, callback) => {
  const certificateNo = trimIfString(value);
  form.value.certificateNo = certificateNo;
  if (!certificateNo) {
    callback();
  } else {
    const params = {
      certificateNo,
      achievementId: form.value.achievementId,
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

function resolveDeptIdBySchool(schoolValue) {
  const node = findDeptNode(deptOptions.value, schoolValue);
  if (!node || node.deptId == null || node.deptId === '') return null;
  const deptId = Number(node.deptId);
  return Number.isNaN(deptId) ? null : deptId;
}

const participantDepartmentOptions = computed(() => {
  const node = findDeptNode(deptOptions.value, participantForm.value.school);
  return node && node.children ? node.children : [];
});

const participantMajorOptions = computed(() => {
  const node = findDeptNode(
    deptOptions.value,
    participantForm.value.department
  );
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
  editingParticipantIndex.value = -1; // 关键修复：确保是新增模式
  participantForm.value = { studentId: '', studentName: '', school: '', department: '', major: '', class_name: '', class_year: '' };
  participantDeptCascaderValue.value = [];
  participantSearchKeyword.value = "";
  isParticipantNew.value = false;
  addParticipantVisible.value = true;
}

const addAdvisorVisible = ref(false);
const isAdvisorNew = ref(false);
const advisorSearchKeyword = ref("");
const advisorDeptCascaderValue = ref([]);
const teacherOptions = ref([]);
const teacherSelectVisible = ref(false);
const advisorForm = ref({ teacherId: '', teacherName: '', school: '', department: '' });
const addAdvisorRules = {
  teacherId: [{ required: true, message: "工号不能为空", trigger: "blur" }],
  teacherName: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  school: [{ required: true, message: "学院不能为空", trigger: "blur" }]
};

function openAddAdvisorDialog() {
  editingAdvisorIndex.value = -1; // 关键修复：确保是新增模式
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

    // 查找所有精确匹配项（学号或姓名完全一致）
    const exactMatches = uniqueStudents.filter(s => s.no === keyword || s.name === keyword);

    if (exactMatches.length === 1) {
      applyStudentInfo(exactMatches[0]);
    } else if (uniqueStudents.length > 0) {
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
  // 本地列表查重 (只校验学号)
  const isDuplicate = samAchievementParticipantList.value.some(
    p => p.studentId === student.no
  );
  if (isDuplicate) {
    proxy.$modal.msgError("该学号已在参赛选手列表中，请勿重复添加");
    return;
  }

  participantForm.value.studentId = student.no;
  participantForm.value.studentName = student.name;
  // Level 2 -> school, Level 3 -> department, Level 4 -> major
  participantForm.value.school = student.school;
  participantForm.value.department = student.department;
  participantForm.value.major = student.major;
  participantForm.value.class_name = student.className;
  participantForm.value.class_year = student.classYear;
  isParticipantNew.value = false;
  studentSelectVisible.value = false;
  if (!student.school) {
    proxy.$modal.msgWarning("该学生档案未维护学院，请从所属机构中补选");
  }
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

    // 查找所有精确匹配项（工号或姓名完全一致）
    const exactMatches = uniqueTeachers.filter(t => t.no === keyword || t.teacherName === keyword);

    if (exactMatches.length === 1) {
      applyTeacherInfo(exactMatches[0]);
    } else if (uniqueTeachers.length > 0) {
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
  // 本地列表查重
  const isDuplicate = samAchievementAdvisorList.value.some(
    a => a.teacherId === teacher.no
  );
  if (isDuplicate) {
    proxy.$modal.msgError("该老师已在指导老师列表中，请勿重复添加");
    return;
  }

  advisorForm.value.teacherId = teacher.no;
  advisorForm.value.teacherName = teacher.teacherName;
  // Level 2 -> school, Level 3 -> department
  advisorForm.value.school = teacher.department;
  advisorForm.value.department = teacher.major;

  // Re-populate cascader starting from Root (Level 1)
  const values = [];
  if (deptOptions.value && deptOptions.value.length > 0) {
    values.push(deptOptions.value[0].deptId); // Prepend Root ID
  }
  if (advisorForm.value.school) values.push(Number(advisorForm.value.school));
  if (advisorForm.value.department) values.push(Number(advisorForm.value.department));
  advisorDeptCascaderValue.value = values;

  isAdvisorNew.value = false;
  teacherSelectVisible.value = false;
}

function selectTeacher(row) {
  applyTeacherInfo(row);
}

// 判断是否允许编辑成员列表（只有负责人、创建者或正在新增时允许）
const canEditMemberList = computed(() => {
  if (props.readOnly) return false;
  // 如果是新增模式（没有 achievementId），允许编辑
  if (!form.value.achievementId) return true;
  // 如果是修改模式，判断当前用户是否是创建者，或者是否在负责人名单中
  const currentUserId = userStore.name;
  const isCreator = form.value.createBy === currentUserId;
  const isManager = samAchievementParticipantList.value.some(p => p.studentId === currentUserId && p.manager == 1);
  return isCreator || isManager;
});

function submitAddParticipant() {
  // 如果还在查询中，等待一小会或者直接拦截（通常 blur 会先于 click 触发并完成请求）
  if (searchingParticipant.value) {
    setTimeout(submitAddParticipant, 300);
    return;
  }

  if (isParticipantNew.value && participantDeptCascaderValue.value.length < 3) {
    proxy.$modal.msgError("请选择完整的所属机构（需选择到专业）");
    return;
  }

  const localDuplicate = samAchievementParticipantList.value.some((p, idx) =>
    idx !== editingParticipantIndex.value && p.studentId === participantForm.value.studentId
  );
  if (localDuplicate) {
    proxy.$modal.msgError("该学号已在列表中，请勿重复添加");
    return;
  }

  proxy.$refs.addParticipantRef.validate(valid => {
    if (valid) {
      trimStringFields(participantForm.value, [
        "studentId",
        "studentName",
        "className",
        "classYear",
        "class_name",
        "class_year",
      ]);
      const finishAction = () => {
        const itemData = {
          id: participantForm.value.id, // 保持 ID 传递
          studentId: trimIfString(participantForm.value.studentId),
          studentName: trimIfString(participantForm.value.studentName),
          school: trimIfString(participantForm.value.school),
          department: trimIfString(participantForm.value.department),
          major: trimIfString(participantForm.value.major),
          class_name: trimIfString(participantForm.value.class_name),
          class_year: trimIfString(participantForm.value.class_year),
          isNewLocal: true
        };

        if (editingParticipantIndex.value > -1) {
          const oldItem = samAchievementParticipantList.value[editingParticipantIndex.value];
          samAchievementParticipantList.value[editingParticipantIndex.value] = {
            ...oldItem,
            ...itemData
          };
          proxy.$modal.msgSuccess("修改选手信息成功");
        } else {
          samAchievementParticipantList.value.push({
            ...itemData,
            orderNo: samAchievementParticipantList.value.length + 1,
            manager: samAchievementParticipantList.value.length === 0 ? 1 : 0
          });
        }
        reIndexList(samAchievementParticipantList.value, 'participant');
        addParticipantVisible.value = false;
        editingParticipantIndex.value = -1;
      };

      if (isParticipantNew.value) {
        // 构建提交给后台基础库的数据
        const studentData = {
          studentId: participantForm.value.dbId, // 使用后端需要的字段名 studentId
          no: participantForm.value.studentId, // 学号
          name: participantForm.value.studentName,
          school: participantForm.value.school,
          department: participantForm.value.department,
          major: participantForm.value.major,
          className: participantForm.value.class_name,
          classYear: participantForm.value.class_year
        };

        const apiCall = editingParticipantIndex.value > -1 ? updateStudent(studentData) : addStudent(studentData);

        apiCall.then(res => {
          if (res.code === 200 || res.msg === '操作成功') {
            proxy.$modal.msgSuccess("学生基础信息保存成功");
            finishAction();
          } else {
            proxy.$modal.msgError(res.msg || "学生基础信息保存失败");
          }
        }).catch(err => {
          console.error("保存学生失败:", err);
        });
      } else {
        finishAction();
      }
    }
  });
}

function submitAddAdvisor() {
  if (searchingAdvisor.value) {
    setTimeout(submitAddAdvisor, 300);
    return;
  }

  if (isAdvisorNew.value && advisorDeptCascaderValue.value.length < 2) {
    proxy.$modal.msgError("请选择完整的所属机构（需选择到院系）");
    return;
  }

  const localDuplicate = samAchievementAdvisorList.value.some((a, idx) =>
    idx !== editingAdvisorIndex.value && a.teacherId === advisorForm.value.teacherId
  );
  if (localDuplicate) {
    proxy.$modal.msgError("该工号已在列表中，请勿重复添加");
    return;
  }

  proxy.$refs.addAdvisorRef.validate(valid => {
    if (valid) {
      const finishAction = () => {
        const itemData = {
          id: advisorForm.value.dbId,
          teacherId: advisorForm.value.teacherId,
          teacherName: advisorForm.value.teacherName,
          school: advisorForm.value.school,
          department: advisorForm.value.department,
          isNewLocal: true
        };

        if (editingAdvisorIndex.value > -1) {
          const oldItem = samAchievementAdvisorList.value[editingAdvisorIndex.value];
          samAchievementAdvisorList.value[editingAdvisorIndex.value] = {
            ...oldItem,
            ...itemData
          };
          proxy.$modal.msgSuccess("修改指导老师成功");
        } else {
          samAchievementAdvisorList.value.push({
            ...itemData,
            orderNo: samAchievementAdvisorList.value.length + 1
          });
        }
        reIndexList(samAchievementAdvisorList.value);
        addAdvisorVisible.value = false;
        editingAdvisorIndex.value = -1;
      };

      if (isAdvisorNew.value) {
        const teacherData = {
          id: advisorForm.value.dbId, // 关键修复：对应后端 sam_teacher 表的 id 字段
          no: advisorForm.value.teacherId,
          teacherName: advisorForm.value.teacherName,
          school: advisorForm.value.school,
          department: advisorForm.value.department
        };

        const apiCall = editingAdvisorIndex.value > -1 ? updateTeacher(teacherData) : addTeacher(teacherData);
        apiCall.then(() => {
          proxy.$modal.msgSuccess("教师信息保存成功");
          finishAction();
        });
      } else {
        finishAction();
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
  award: false, notice: false, work: false, photo: false, payment: false, invoice: false, receipt: false
});

const previewUrls = reactive({
  award: "",
  notice: "",
  work: {},
  photo: {},
  payment: "",
  invoice: "",
  receipt: "",
});

function revokePreviewUrl(url) {
  if (url) {
    window.URL.revokeObjectURL(url);
  }
}

const exampleMap = {
  'award': '/image/扫描文件.pdf',   
  'notice': '/image/tongzhi.pdf',   
  'payment': '/image/jilu.pdf',     
  'invoice': '/image/fapiao.pdf',   
  'work': '/image/zuopingzhaopian.pdf',
  'photo': '/image/cansaizhaopian.pdf',
};

const isFullscreen = ref(true);

watch(activeAttachmentTab, (newVal) => {
  if (newVal) {
    uploadUnlocked[newVal] = false;
  }
});

function handlePreUpload(type) {
  currentUploadType.value = type;
  const fileName = exampleMap[type];
  if (fileName) {
    currentExampleUrl.value = fileName;
    isFullscreen.value = true;
    exampleVisible.value = true;
  } else {
    uploadUnlocked[type] = true;
    confirmExampleKnown();
  }
}

function confirmExampleKnown() {
  if (currentUploadType.value) {
    uploadUnlocked[currentUploadType.value] = true;
    exampleVisible.value = false;
    
    nextTick(() => {
      // 查找当前激活的标签页内容
      const activePane = document.querySelector('.attach-card .el-tabs__content .el-tab-pane:not([style*="display: none"])');
      if (activePane) {
        // 查找上传按钮并触发点击
        const uploadBtn = activePane.querySelector('.el-upload button') || activePane.querySelector('.el-upload input');
        if (uploadBtn) {
          uploadBtn.click();
        }
      }
    });
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
    { label: findDictLabel('1') || '获奖证书', name: 'award', prop: 'fileAward', alert: `请上传${findDictLabel('1') || '获奖证书'} (PDF)`, fileType: ['pdf'], limit: 1 },
    {
      label: findDictLabel('3') || '参赛作品',
      name: 'work',
      prop: 'fileWork',
      alert: form.value.hasFileWork === 1 ? `请上传${findDictLabel('3') || '参赛作品'} (PDF，5份及以上)` : `请上传手写声明 (PDF，1份)`,
      fileType: ['pdf'],
      limit: form.value.hasFileWork === 1 ? 10 : 1,
      isMultiple: true
    },
    {
      label: findDictLabel('8') || '比赛照片',
      name: 'photo',
      prop: 'filePhoto',
      alert: form.value.hasFilePhoto === 1 ? `请上传${findDictLabel('8') || '比赛照片'} (PDF，5份及以上)` : `请上传手写声明 (PDF，1份)`,
      fileType: ['pdf'],
      limit: form.value.hasFilePhoto === 1 ? 10 : 1,
      isMultiple: true
    },
    { label: findDictLabel('4') || '支付记录', name: 'payment', prop: 'filePayment', alert: `请上传${findDictLabel('4') || '支付记录'} (PDF)`, type: 'warning', condition: (f) => f.isReimburse === 1, fileType: ['pdf'], limit: 1 },
    { label: findDictLabel('5') || '正规发票', name: 'invoice', prop: 'fileInvoice', alert: `请上传${findDictLabel('5') || '正规发票'} (PDF)`, type: 'warning', condition: (f) => f.isReimburse === 1, fileType: ['pdf'], limit: 1 },
    { label: findDictLabel('6') || '收款码', name: 'receipt', prop: 'fileReceiptCode', alert: `请上传${findDictLabel('6') || '收款码'} (PDF)`, type: 'warning', condition: (f) => f.isReimburse === 1, fileType: ['pdf'], limit: 1 },
  ];
});
const visibleAttachments = computed(() => {
  return attachmentConfig.value.filter((item) => {
    if (!item.condition) return true;
    return item.condition(form.value);
  });
});

async function getBlobErrorMessage(blob, fallback = "文件获取失败，请稍后重试") {
  try {
    const text = await blob.text();
    const data = JSON.parse(text);
    return data?.msg || fallback;
  } catch (e) {
    return fallback;
  }
}

// 生成安全的本地预览流
function loadSafePreview(uuid, type, index = null) {
  if (!uuid) return;

  // 多图如果已有预览，跳过
  if (type === 'work' || type === 'photo') {
    if (previewUrls[type][uuid]) return;
  }

  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    const blobData = blob.data || blob;
    let fileName = getFileName(uuid) || '';
    
    // 态识别文件类型，防止把图片强行当成 PDF 导致白屏
    let mimeType = 'application/pdf'; // 默认当做 PDF
    if (fileName.match(/\.(jpg|jpeg|png|gif)$/i) || (blobData.type && blobData.type.startsWith('image/'))) {
      mimeType = blobData.type || 'image/jpeg';
    }
    
    const blobWithMime = new Blob([blobData], { type: mimeType });
    const url = window.URL.createObjectURL(blobWithMime);

    if (type === 'work' || type === 'photo') {
      previewUrls[type][uuid] = url;
    } else {
      if (previewUrls[type]) window.URL.revokeObjectURL(previewUrls[type]);
      previewUrls[type] = url;
    }
  }).catch(err => {
    console.error(`预览加载失败 [${type}]: ${uuid}`, err);
  });
}

/** 获取多文件列表数组 (兼容字符串和数组) */
function getFileList(val) {
  if (!val) return [];
  if (Array.isArray(val)) return val;
  return val.split(',').filter(s => s !== "");
}

function syncMultiPreview(type, value) {
  const uuids = getFileList(value);
  const currentMap = previewUrls[type];

  Object.keys(currentMap).forEach((uuid) => {
    if (!uuids.includes(uuid)) {
      revokePreviewUrl(currentMap[uuid]);
      delete currentMap[uuid];
    }
  });

  uuids.forEach((uuid) => {
    if (!currentMap[uuid]) {
      loadSafePreview(uuid, type);
    }
  });
}

watch(() => form.value.fileAward, (uuid) => loadSafePreview(uuid, 'award'));
watch(() => form.value.fileNotice, (uuid) => loadSafePreview(uuid, 'notice'));
watch(() => form.value.fileWork, (val) => syncMultiPreview('work', val), { deep: true, immediate: true });
watch(() => form.value.filePhoto, (val) => syncMultiPreview('photo', val), { deep: true, immediate: true });
watch(() => form.value.filePayment, (uuid) => loadSafePreview(uuid, 'payment'));
watch(() => form.value.fileInvoice, (uuid) => loadSafePreview(uuid, 'invoice'));
watch(() => form.value.fileReceiptCode, (uuid) => loadSafePreview(uuid, 'receipt'));

function handleHasFileChange(type, val) {
  const prop = type === 'work' ? 'fileWork' : 'filePhoto';
  form.value[prop] = [];

  if (val === 0) {
    // 复用示例弹出逻辑：展示 PDF 内容并要求用户确认后解锁
    currentUploadType.value = type;
    currentExampleUrl.value = type === 'work' ? '/image/zuopingzhaopian.pdf' : '/image/cansaizhaopian.pdf'; 
    exampleVisible.value = true;
    // 重置解锁状态，直到用户在弹出层点击“确认”
    uploadUnlocked[type] = false;
  }
}
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
    // 列表加载完成后，尝试自动匹配一次
    autoMatchSession();
  });
}

/**
 * 自动匹配届次逻辑
 * 根据：赛事(competitionId) + 获奖级别(level) + 获奖时间(awardTime) 智能推断
 */
function autoMatchSession() {
  const { competitionId, level, awardTime } = form.value;
  
  // 核心条件不全，或候选项还没加载好，则跳过
  if (!competitionId || !level || !awardTime || !sessionOptions.value.length) {
    return;
  }

  // 1. 解析时间
  const date = new Date(awardTime);
  if (isNaN(date.getTime())) return;
  const targetYear = date.getFullYear().toString();

  // 2. 在候选列表中筛选
  const matched = sessionOptions.value.filter(item => {
    // A. 级别必须一致
    const isLevelMatch = item.level == level;

    // B. 年份严格匹配 (要求 session 对象的 year 字段必须与获奖年份一致)
    const sessionYear = item.year;
    const isYearMatch = sessionYear && String(sessionYear) === targetYear;

    return isLevelMatch && isYearMatch;
  });

  // 3. 自动赋值并锁定
  if (matched.length >= 1) {
    form.value.sessionId = matched[0].id;
    isAutoMatched.value = true;
  } else {
    form.value.sessionId = null;
    isAutoMatched.value = false;
  }
}

function handleCompetitionChange(val) {
  form.value.sessionId = null;
  sessionOptions.value = [];
  isAutoMatched.value = false;
  if (val) {
    getSessionList(val);
  }
}

// 监听级别和时间的变化，实时触发匹配逻辑
watch(() => [form.value.level, form.value.awardTime], () => {
  autoMatchSession();
});

watch(
  () => form.value.awardTime,
  (value) => {
    if (loadingFormDetail.value) return;
    syncYearFromAwardTime(value);
  }
);

function open(id) {
  if (!isPageMode.value) visible.value = true;
  pendingAchievementId.value = id ? String(id) : "";
  reset();
  getDeptTree();
  getCompetitionList();
  
  activeAttachmentTab.value = 'award';
  if (id) {
    title.value = props.readOnly ? props.titleDetail : props.titleEdit;
    detailLoading.value = true;
    loadDetail(id);
  } else {
    title.value = props.titleAdd;
    detailLoading.value = false;
    pendingAchievementId.value = "";
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

    reIndexList(samAchievementParticipantList.value, 'participant');
    reIndexList(samAchievementAdvisorList.value);

    updateSnapshot();
    // 只有在弹窗模式下（非页面模式）才在 open 时检查草稿
    // 因为页面模式下 onMounted 已经检查过了，避免重复弹窗
    if (!isPageMode.value) {
      checkDraft();
    }
  }
  initSortable();
}
function getForm() { return form.value; }
defineExpose({ open, getForm, activeAttachmentTab, submitForm, handleCancel });

const sortableInstances = ref([]);
const sortableTimeout = ref(null);

onUnmounted(() => {
  if (sortableTimeout.value) {
    clearTimeout(sortableTimeout.value);
  }
  sortableInstances.value.forEach(instance => {
    instance.destroy();
  });
  sortableInstances.value = [];
});

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
      const el =
        pTable.$el.querySelector(".el-table__body-wrapper tbody") ||
        pTable.$el.querySelector("tbody");
      if (el) {
        Sortable.create(el, {
          handle: ".drag-handle",
          filter: ".fixed-row", // 禁止拖动带 fixed-row 类的行
          onMove: (evt) => {
            // 禁止拖动到带 fixed-row 类的行上方（即禁止覆盖索引为0的位置）
            return evt.related.className.indexOf("fixed-row") === -1;
          },
          onEnd: ({ newIndex, oldIndex }) => {
            if (newIndex === oldIndex) return;
            const list = [...samAchievementParticipantList.value];
            const currRow = list.splice(oldIndex, 1)[0];
            list.splice(newIndex, 0, currRow);
            samAchievementParticipantList.value = [];
            nextTick(() => {
              samAchievementParticipantList.value = list;
              reIndexList(samAchievementParticipantList.value, 'participant');
            });
          },
        });
      }
    }

    const tables = [
      { ref: participantTable.value || participantTableDialog.value, list: samAchievementParticipantList },
      { ref: advisorTable.value || advisorTableDialog.value, list: samAchievementAdvisorList }
    ];

    tables.forEach(({ ref: tableRef, list }) => {
      if (tableRef && tableRef.$el) {
        const el = tableRef.$el.querySelector('.el-table__body-wrapper tbody') || tableRef.$el.querySelector('tbody');
        if (el) {
          const instance = Sortable.create(el, {
            handle: '.drag-handle',
            filter: '.fixed-row',
            onMove: (evt) => {
              return evt.related.className.indexOf('fixed-row') === -1;
            },
            onEnd: ({ newIndex, oldIndex }) => {
              if (newIndex === oldIndex) return;
              const newList = [...list.value];
              const currRow = newList.splice(oldIndex, 1)[0];
              newList.splice(newIndex, 0, currRow);
              list.value = [];
              nextTick(() => {
                list.value = newList;
                reIndexList(list.value);
              });
            }
          });
          sortableInstances.value.push(instance);
        }
      }
    });
  }, 500);
}

// === 【核心修复】：增强的数据回显逻辑 ===
function loadDetail(id) {
  if (!props.getFn) {
    detailLoading.value = false;
    return;
  }
  pendingAchievementId.value = id ? String(id) : pendingAchievementId.value;
  detailLoading.value = true;
  props
    .getFn(id)
    .then((response) => {
      const d = response.data;
      loadingFormDetail.value = true;

      if (d.category != null) d.category = String(d.category);
      if (d.level != null) d.level = String(d.level);
      if (d.grade != null) d.grade = String(d.grade);
      if (d.groupId != null) d.groupId = String(d.groupId);
      if (d.year != null) d.year = Number(d.year);
      if (d.isSupplement != null) d.isSupplement = Number(d.isSupplement);

      if (d.competitionId != null) d.competitionId = Number(d.competitionId);
      if (d.sessionId != null) d.sessionId = Number(d.sessionId);
      if (d.ownerDepId != null) d.ownerDepId = d.ownerDepId;

    // 预先占位，确保 Vue 的模板监听能够完美挂载并触发渲染
    d.fileAward = null;
    d.fileNotice = null;
    d.fileWork = [];
    d.filePhoto = [];
    d.filePayment = null;
    d.fileInvoice = null;
    d.fileReceiptCode = null;

    // 初始化 hasFile 状态
    d.hasFileWork = 1;
    d.hasFilePhoto = 1;

    if (d.samAchievementAttachmentList) {
       let workCount = 0;
       let photoCount = 0;
       d.samAchievementAttachmentList.forEach(item => {
          const typeStr = String(item.type);
          const uuid = item.fileUuid || item.file_uuid; // 兼容不同数据库下划线配置
          if (typeStr === '1') d.fileAward = uuid;
          if (typeStr === '2') d.fileNotice = uuid;
          if (typeStr === '3') { d.fileWork.push(uuid); workCount++; }
          if (typeStr === '8') { d.filePhoto.push(uuid); photoCount++; }
          if (typeStr === '4') d.filePayment = uuid;
          if (typeStr === '5') d.fileInvoice = uuid;
          if (typeStr === '6') d.fileReceiptCode = uuid;
       });
       // 如果只有一份，认为是声明
       if (workCount === 1) d.hasFileWork = 0;
       if (photoCount === 1) d.hasFilePhoto = 0;
    }

    form.value = d; // 一次性赋值给响应式对象
    
    if (d.competitionId) {
        getSessionList(d.competitionId);
      }

      samAchievementParticipantList.value =
        d.samAchievementParticipantList || [];
      samAchievementAdvisorList.value = d.samAchievementAdvisorList || [];
      reIndexList(samAchievementParticipantList.value, 'participant');
      reIndexList(samAchievementAdvisorList.value);

      if (form.value.isReimburse == null) form.value.isReimburse = 0;
      if (form.value.reimbursementStatus == null) form.value.reimbursementStatus = null;

      updateSnapshot();
    })
    .catch(() => {
      // keep pendingAchievementId so the header remains stable while the caller handles the error
      loadingFormDetail.value = false;
    })
    .finally(() => {
      detailLoading.value = false;
    });
}

function reset() {
  form.value = {
    competitionId: null, achievementId: null, sessionId: null, category: "3", name: null, teamName: null,
    level: null, grade: null, track: null, certificateNo: null, groupId: null, ownerDepId: null,
    awardTime: null, fee: null, reimbursementFee: null, isReimburse: 0, reimbursementStatus: null,
    fileAward: null, fileNotice: null, fileWork: [], filePhoto: [], filePayment: null, fileInvoice: null, fileReceiptCode: null,
    hasFileWork: 1, hasFilePhoto: 1
  };
  samAchievementParticipantList.value = [];
  samAchievementAdvisorList.value = [];
  sessionOptions.value = []; 
  
  Object.keys(uploadUnlocked).forEach(k => uploadUnlocked[k] = false);
  
 //区分单文件和多文件，防止多图的对象结构被破坏
  Object.keys(previewUrls).forEach(key => {
    if (typeof previewUrls[key] === 'string') {
      if (previewUrls[key]) {
        window.URL.revokeObjectURL(previewUrls[key]);
        previewUrls[key] = "";
      }
    } else {
      if (previewUrls[key]) {
        Object.values(previewUrls[key]).forEach(url => {
          if (url) window.URL.revokeObjectURL(url);
        });
        previewUrls[key] = {}; // 这里必须保持是对象 {}，绝不能变成 ""
      }
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
  const photoLabel = findLabel('8') || '比赛照片';
  const paymentLabel = findLabel('4') || '支付记录';
  const invoiceLabel = findLabel('5') || '正规发票';
  const receiptLabel = findLabel('6') || '收款码';

  if (!f.fileAward) { proxy.$modal.msgWarning(`请上传【${awardLabel}】PDF文件！`); activeAttachmentTab.value = 'award'; return false; }
  if (!f.fileNotice) { proxy.$modal.msgWarning(`请上传【${noticeLabel}】PDF文件！`); activeAttachmentTab.value = 'notice'; return false; }

  const workFiles = getFileList(f.fileWork);
  const photoFiles = getFileList(f.filePhoto);

  if (f.hasFileWork === 1) {
    if (workFiles.length < 5) {
      proxy.$modal.msgWarning(`【${workLabel}】要求上传至少5份PDF文件！当前已上传 ${workFiles.length} 份`);
      activeAttachmentTab.value = 'work';
      return false;
    }
  } else {
    if (workFiles.length < 1) {
      proxy.$modal.msgWarning(`请上传【${workLabel}】手写声明PDF文件！`);
      activeAttachmentTab.value = 'work';
      return false;
    }
  }

  if (f.hasFilePhoto === 1) {
    if (photoFiles.length < 5) {
      proxy.$modal.msgWarning(`【${photoLabel}】要求上传至少5份PDF文件！当前已上传 ${photoFiles.length} 份`);
      activeAttachmentTab.value = 'photo';
      return false;
    }
  } else {
    if (photoFiles.length < 1) {
      proxy.$modal.msgWarning(`请上传【${photoLabel}】手写声明PDF文件！`);
      activeAttachmentTab.value = 'photo';
      return false;
    }
  }

  if (f.isReimburse === 1) {
    if (!f.filePayment) {
      proxy.$modal.msgWarning("申请报销必须上传【支付记录】PDF文件！");
      activeAttachmentTab.value = "payment";
      return false;
    }
    if (!f.fileInvoice) {
      proxy.$modal.msgWarning("申请报销必须上传【正规发票】PDF文件！");
      activeAttachmentTab.value = "invoice";
      return false;
    }
    if (!f.fileReceiptCode) {
      proxy.$modal.msgWarning("申请报销必须上传【收款码】PDF文件！");
      activeAttachmentTab.value = "receipt";
      return false;
    }
  }
  return true;
}

function submitForm() {
  if (props.readOnly) return Promise.resolve(false);
  const activeRef = isPageMode.value
    ? outcomeRefPage.value
    : outcomeRefDialog.value;

  return new Promise((resolve) => {
    activeRef.validate((valid) => {
      if (!valid) {
        resolve(false);
        return;
      }
      if (!validatePDFUpload()) {
        resolve(false);
        return;
      }

      trimStringFields(form.value, [
        "name",
        "track",
        "certificateNo",
        "teamName",
        "fee",
      ]);
      form.value.year = normalizeIntegerOrNull(form.value.year);
      form.value.isSupplement = normalizeIntegerOrNull(form.value.isSupplement);

      if (!form.value.ownerDepId) {
        proxy.$modal.msgWarning("归属学院不能为空，请先确认第一负责人所属学院");
        resolve(false);
        return;
      }

      let attachments = [];
      const pushFile = (type, path) => {
        if (!path) return;
        if (Array.isArray(path)) {
          path.forEach(p => {
            if (p) attachments.push({ type: type, fileUuid: p, fileType: 1 });
          });
        } else {
          // 兼容可能存在的逗号分隔字符串 (处理某些组件返回拼接字符串的情况)
          const uuids = String(path).split(',');
          uuids.forEach(u => {
            const trimmed = u.trim();
            if (trimmed) attachments.push({ type: type, fileUuid: trimmed, fileType: 1 });
          });
        }
      };
      pushFile(1, form.value.fileAward);
      pushFile(2, form.value.fileNotice);
      pushFile(3, form.value.fileWork);
      pushFile(8, form.value.filePhoto);
      pushFile(4, form.value.filePayment);
      pushFile(5, form.value.fileInvoice);
      pushFile(6, form.value.fileReceiptCode);

      form.value.samAchievementAttachmentList = attachments;

      form.value.samAchievementParticipantList =
        samAchievementParticipantList.value.map((p) => ({
          ...p,
          studentId: trimIfString(p.studentId),
          studentName: trimIfString(p.studentName),
          studentNo: trimIfString(p.studentId),
          manager: String(p.manager),
        }));

      form.value.samAchievementAdvisorList =
        samAchievementAdvisorList.value.map((a) => ({
          ...a,
          teacherId: trimIfString(a.teacherId),
          teacherName: trimIfString(a.teacherName),
          teacherNo: trimIfString(a.teacherId),
          manager: a.manager, // 确保 manager 字段也传给后端
        }));

      const isEdit = form.value.achievementId != null;
      const apiFn = isEdit ? props.updateFn : props.addFn;
      
      if (apiFn) {
        apiFn(form.value).then(response => {
          proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");

          // 如果用户上传了比赛通知，且届次原本没有比赛通知，则同步更新届次信息
          if (form.value.fileNotice && !isNoticeFromSession.value && form.value.sessionId) {
            updateSession({
              id: form.value.sessionId,
              uuid: form.value.fileNotice
            }).catch(err => {
              console.error("同步更新届次比赛通知失败:", err);
            });
          }

          // 提交成功清除草稿
          clearDraft();
          updateSnapshot();
          if (!isPageMode.value) visible.value = false;
          emit('ok');
          resolve(true);
        }).catch(() => {
          resolve(false);
        });
      } else {
        proxy.$modal.msgError("未配置保存接口");
        resolve(false);
      }
    });
  });
}

//兼容若依上传后变成字符串的删除逻辑
function handleRemoveFile(prop, uuid) {
  if (!form.value[prop]) return;

  if (Array.isArray(form.value[prop])) {
    // 如果是数组，直接过滤掉当前 uuid
    form.value[prop] = form.value[prop].filter(id => id !== uuid);
  } else if (typeof form.value[prop] === 'string') {
    // 如果是若依拼接的字符串，先拆成数组，过滤后再拼回去
    let arr = form.value[prop].split(',').filter(id => id && id !== uuid);
    // 如果删完还有剩余，保留字符串；如果全删光了，设为 null 触发必填校验
    form.value[prop] = arr.length > 0 ? arr.join(',') : null;
  } else {
    form.value[prop] = null;
  }
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

function getDeptName(deptId) {
  if (!deptId) return "-";
  const id = Number(deptId);
  if (isNaN(id)) return deptId; // 如果本来就是文字则直接返回

  const findName = (nodes) => {
    for (const node of nodes) {
      if (node.deptId == id) return node.deptName;
      if (node.children) {
        const name = findName(node.children);
        if (name) return name;
      }
    }
    return null;
  };
  return findName(deptOptions.value) || deptId;
}

function getDeptTree() {
  listDept().then(response => {
    const fullTree = handleTree(response.data, "deptId");
    deptOptions.value = fullTree;

    // Skip Level 1 (Root/University) to start directly from Level 2 (College)
    let processedTree = [];
    if (fullTree && fullTree.length > 0 && fullTree[0].children) {
      processedTree = fullTree[0].children;
    } else {
      processedTree = fullTree;
    }

    // Students see Level 2 to Level 4 (College -> Dept -> Major)
    studentDeptOptions.value = JSON.parse(JSON.stringify(processedTree));

    // Teachers see Level 2 to Level 3 only (College -> Dept)
    const teacherTree = JSON.parse(JSON.stringify(processedTree));
    teacherTree.forEach(college => {
      if (college.children) {
        college.children.forEach(dept => {
          // Dept is Level 3, remove its Level 4 children (Majors)
          if (dept.children) {
            delete dept.children;
          }
        });
      }
    });
    advisorDeptOptions.value = teacherTree;
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
      form.value.ownerDepId = first.school;
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
  proxy.$router.push('/competitions/competitionapply');
}
// 查看详情（在新标签页打开文件）
function handleOpenDetail(uuid) {
  if (!uuid) return proxy.$modal.msgError("文件不存在");
  
  // 使用 request 请求，自动携带 token
  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    const blobData = blob.data || blob;
    const blobWithMime = new Blob([blobData], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blobWithMime);
    window.open(url, '_blank');
  }).catch(() => {
    proxy.$modal.msgError("文件获取失败，请重试");
  });
}

// 下载文件
// 下载文件
function handleDownload(uuid) {
  if (!uuid) return proxy.$modal.msgError("文件不存在");

  request({
    url: '/attachment/download',
    method: 'get',
    params: { resource: uuid },
    responseType: 'blob'
  }).then(blob => {
    const blobData = blob.data || blob;
    let fileName = getFileName(uuid) || '下载文件';

    // 【核心修复】：如果文件名没有小数点（没有后缀），强行加上 .pdf
    if (!fileName.includes('.')) {
      fileName += '.pdf';
    }

    // 推断正确的 MIME 类型
    let mimeType = blobData.type;
    if (!mimeType || mimeType === 'application/octet-stream') {
      mimeType = fileName.toLowerCase().endsWith('.pdf') ? 'application/pdf' : 'image/jpeg';
    }

    const url = window.URL.createObjectURL(new Blob([blobData], { type: mimeType }));
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url); // 释放内存
  }).catch(() => {
    proxy.$modal.msgError("文件下载失败，请重试");
  });
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
.attach-card {
  background: #f8f8f9;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  height: 100%;
}
.upload-pane-content {
  padding: 5px 10px;
}
.mb10 {
  margin-bottom: 15px;
}
.mr5 {
  margin-right: 5px;
}
:deep(.el-tabs__content) {
  height: 100%;
}

.hide-file-list :deep(.el-upload-list) {
  display: none !important;
}

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
  transition: border-color 0.3s;
}
.fake-upload-box:hover {
  border-color: #409eff;
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
  line-height: 1.2;
  border: 1px solid #ddd;
  padding: 2px;
  background-color: #fff;
  width: 100%;
  box-sizing: border-box;
}

.full-width-cascader {
  width: 100% !important;
}
</style>
