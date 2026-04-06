<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果ID" prop="achievementId">
        <el-input
          v-model="queryParams.achievementId"
          placeholder="请输入成果ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="届次 " prop="sessionId">
        <el-input
          v-model="queryParams.sessionId"
          placeholder="请输入届次 "
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
          <el-option
            v-for="dict in achievement_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="作品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入作品名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="团队名称" prop="teamName">
        <el-input
          v-model="queryParams.teamName"
          placeholder="请输入团队名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择获奖级别" clearable>
          <el-option
            v-for="dict in award_level_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖等级" prop="grade">
        <el-select v-model="queryParams.grade" placeholder="请选择获奖等级" clearable>
          <el-option
            v-for="dict in award_rank"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="赛道" prop="track">
        <el-input
          v-model="queryParams.track"
          placeholder="请输入赛道"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证书编号" prop="certificateNo">
        <el-input
          v-model="queryParams.certificateNo"
          placeholder="请输入证书编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="组别" prop="groupId">
        <el-select v-model="queryParams.groupId" placeholder="请选择组别" clearable>
          <el-option
            v-for="dict in group_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获奖时间" prop="awardTime">
        <el-date-picker clearable
          v-model="queryParams.awardTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择获奖时间">
        </el-date-picker>
      </el-form-item>

       <!-- 新增：报销相关查询条件 -->
  <el-form-item label="报销时间" prop="reimbursementTime">
    <el-date-picker clearable
      v-model="queryParams.reimbursementTime"
      type="date"
      value-format="YYYY-MM-DD"
      placeholder="请选择报销时间">
    </el-date-picker>
  </el-form-item>
  <el-form-item label="报销状态" prop="status">
    <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
      <el-option label="待审核" value="0" />
      <el-option label="已通过" value="1" />
      <el-option label="已拒绝" value="2" />
    </el-select>
  </el-form-item>
   <!-- 新增：成果主表的报销相关查询条件 -->
  <el-form-item label="是否报销" prop="isReimburse">
    <el-select v-model="queryParams.isReimburse" placeholder="请选择" clearable>
      <el-option label="已报销" value="1" />
      <el-option label="未报销" value="0" />
    </el-select>
  </el-form-item>
  <el-form-item label="报销时间" prop="reimbursementDate">
    <el-date-picker clearable
      v-model="queryParams.reimbursementDate"
      type="date"
      value-format="YYYY-MM-DD"
      placeholder="请选择报销时间">
    </el-date-picker>
  </el-form-item>
  <el-form-item label="报销比例" prop="reimbursementRatio">
    <el-input
      v-model="queryParams.reimbursementRatio"
      placeholder="请输入报销比例"
      clearable
      @keyup.enter="handleQuery"
    />
  </el-form-item>


      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 统计卡片 -->
    <el-row :gutter="10" class="mb8" v-if="reimbursementItemId">
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <div class="card-content">
            <div class="card-label">成果数量</div>
            <div class="card-value">{{ stats.productCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <div class="card-content">
            <div class="card-label">总金额</div>
            <div class="card-value">¥{{ stats.totalAmount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <div class="card-content">
            <div class="card-label">已发放金额</div>
            <div class="card-value">¥{{ stats.paidAmount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <div class="card-content">
            <div class="card-label">待发放金额</div>
            <div class="card-value">¥{{ (stats.totalAmount || 0) - (stats.paidAmount || 0) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="10" class="mb8">
      
      
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:Reimbursement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:Reimbursement:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5" v-if="reimbursementItemId">
        <el-button
          type="info"
          plain
          icon="Refresh"
          :loading="calculating"
          :disabled="calculateDisabled"
          @click="handleRecalculate"
          v-hasPermi="['system:Reimbursement:calculate']"
        >计算报销金额</el-button>
      </el-col>
      <el-col :span="1.5" v-if="reimbursementItemId">
        <el-tooltip :content="associateButtonDisabled ? (isProjectConfirmed ? '项目已确认，无法关联成果' : '请先选择报销项目') : '关联成果'" placement="top">
          <el-button
            type="primary"
            plain
            icon="Link"
            :disabled="associateButtonDisabled"
            @click="handleOpenAssociateDialog"
            v-hasPermi="['system:Reimbursement:edit']"
          >关联成果</el-button>
        </el-tooltip>
      </el-col>
      <el-col :span="1.5" v-if="reimbursementItemId">
        <el-button
          type="warning"
          plain
          icon="Link"
          :disabled="batchCancelDisabled"
          @click="handleBatchCancelAssociation"
          v-hasPermi="['system:Reimbursement:edit']"
        >批量取消关联</el-button>
      </el-col>
      <el-col :span="1.5" v-if="reimbursementItemId">
        <el-tooltip 
          :content="currentProjectStatus === '1' ? '项目已确认，清单已锁定' : '确认后清单将被锁定，不可再修改'" 
          placement="top"
        >
          <el-button
            :type="currentProjectStatus === '1' ? 'success' : 'danger'"
            plain
            :icon="currentProjectStatus === '1' ? 'Lock' : 'Edit'"
            :loading="confirmLoading"
            @click="handleToggleStatus"
            v-hasPermi="['system:Reimbursement:confirm']"
          >
            {{ currentProjectStatus === '1' ? '已确认报销清单' : '确认报销清单' }}
          </el-button>
        </el-tooltip>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ReimbursementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="届次 " align="center" prop="sessionId" />
      <el-table-column label="类别" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="achievement_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="作品名称" align="center" prop="name" />
      <el-table-column label="团队名称" align="center" prop="teamName" />
      <el-table-column label="获奖级别" align="center" prop="level">
        <template #default="scope">
          <dict-tag :options="award_level_type" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="获奖等级" align="center" prop="grade">
        <template #default="scope">
          <dict-tag :options="award_rank" :value="scope.row.grade"/>
        </template>
      </el-table-column>
      <el-table-column label="赛道" align="center" prop="track" />
      <el-table-column label="证书编号" align="center" prop="certificateNo" />
      <el-table-column label="组别" align="center" prop="groupId">
        <template #default="scope">
          <dict-tag :options="group_type" :value="scope.row.groupId"/>
        </template>
      </el-table-column>
      <el-table-column label="获奖时间" align="center" prop="awardTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.awardTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>


      <!-- 使用成果主表字段：报销相关 -->
  <el-table-column label="需报销金额" align="center" prop="fee" width="120">
    <template #default="scope">
      <span>{{ scope.row.fee ? '¥' + scope.row.fee : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="报销时间" align="center" prop="reimbursementDate" width="180">
    <template #default="scope">
      <span>{{ scope.row.reimbursementDate ? parseTime(scope.row.reimbursementDate, '{y}-{m}-{d}') : '-' }}</span>
    </template>
  </el-table-column>
  <el-table-column label="报销比例" align="center" prop="reimbursementRatio" width="100">
    <template #default="scope">
      <span>{{ scope.row.reimbursementRatio ? scope.row.reimbursementRatio + '%' : '-' }}</span>
    </template>
  </el-table-column>
  <!-- 实际报销金额 -->
  <el-table-column label="实际报销金额" align="center" width="120">
    <template #default="scope">
      <span v-if="scope.row.reimbursementFee || scope.row.reimbursement_fee">
        ¥{{ formatMoney(scope.row.reimbursementFee || scope.row.reimbursement_fee) }}
      </span>
      <span v-else>-</span>
    </template>
  </el-table-column>

  <!-- 是否报销 -->
  <el-table-column label="是否报销" align="center" width="100">
    <template #default="scope">
      <!-- 兼容 isReimburse 和 is_reimburse -->
      <el-tag v-if="scope.row.isReimburse === 1 || scope.row.is_reimburse === 1" type="success">已报销</el-tag>
      <el-tag v-else-if="scope.row.isReimburse === 0 || scope.row.is_reimburse === 0" type="info">未报销</el-tag>
      <span v-else>-</span>
    </template>
  </el-table-column>

  <!-- 是否补录 -->
  <el-table-column label="是否补录" align="center" width="100">
    <template #default="scope">
      <el-tag v-if="scope.row.isSupplement === 1 || scope.row.is_supplement === 1" type="warning">是</el-tag>
      <el-tag v-else-if="scope.row.isSupplement === 0 || scope.row.is_supplement === 0" type="info">否</el-tag>
      <span v-else>-</span>
    </template>
  </el-table-column>
  




      <el-table-column label="操作" align="center" width="280" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
          
          <el-button 
            link 
            type="danger" 
            icon="Delete" 
            :disabled="isProjectConfirmed"
            @click="handleDelete(scope.row)" 
            v-hasPermi="['system:Reimbursement:remove']"
          >删除</el-button>
          <!-- 取消关联按钮 - 仅当项目未确认时显示 -->
          <el-button 
            v-if="!isProjectConfirmed"
            link 
            type="warning" 
            icon="Link" 
            @click="handleCancelAssociation(scope.row)"
            v-hasPermi="['system:Reimbursement:edit']"
          >取消关联</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改报销项目详情对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ReimbursementRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="届次 " prop="sessionId">
          <el-input v-model="form.sessionId" placeholder="请输入届次 " />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别">
            <el-option
              v-for="dict in achievement_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入作品名称" />
        </el-form-item>
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="获奖级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择获奖级别">
            <el-option
              v-for="dict in award_level_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择获奖等级">
            <el-option
              v-for="dict in award_rank"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="赛道" prop="track">
          <el-input v-model="form.track" placeholder="请输入赛道" />
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNo">
          <el-input v-model="form.certificateNo" placeholder="请输入证书编号" />
        </el-form-item>
        <el-form-item label="组别" prop="groupId">
          <el-select v-model="form.groupId" placeholder="请选择组别">
            <el-option
              v-for="dict in group_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker clearable
            v-model="form.awardTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择获奖时间">
          </el-date-picker>
        </el-form-item>
        <el-divider content-position="center">报销项目信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddSamReimbursementItems">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteSamReimbursementItems">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="samReimbursementItemsList" :row-class-name="rowSamReimbursementItemsIndex" @selection-change="handleSamReimbursementItemsSelectionChange" ref="samReimbursementItems">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="报销时间" prop="reimbursementTime" width="240">
            <template #default="scope">
              <el-date-picker clearable
                v-model="scope.row.reimbursementTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择报销时间">
              </el-date-picker>
            </template>
          </el-table-column>

          
  <el-divider content-position="center">报销信息</el-divider>
    
    <el-form-item label="需报销金额" prop="fee">
      <el-input-number 
        v-model="form.fee" 
        :precision="2" 
        :min="0" 
        placeholder="请输入需报销金额"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="报销比例" prop="reimbursementRatio">
      <el-input-number 
        v-model="form.reimbursementRatio" 
        :min="0" 
        :max="100" 
        :precision="2"
        placeholder="请输入报销比例"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="实际报销金额" prop="reimbursementFee">
      <el-input-number 
        v-model="form.reimbursementFee" 
        :precision="2" 
        :min="0" 
        placeholder="请输入实际报销金额"
        style="width: 100%"
      />
    </el-form-item>
    
    <el-form-item label="报销时间" prop="reimbursementDate">
      <el-date-picker clearable
        v-model="form.reimbursementDate"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="请选择报销时间"
        style="width: 100%">
      </el-date-picker>
    </el-form-item>
    
    <el-form-item label="是否报销" prop="isReimburse">
      <el-radio-group v-model="form.isReimburse">
        <el-radio label="1">已报销</el-radio>
        <el-radio label="0">未报销</el-radio>
      </el-radio-group>
    </el-form-item>
    
    <el-form-item label="是否补录" prop="isSupplement">
      <el-radio-group v-model="form.isSupplement">
        <el-radio label="1">是</el-radio>
        <el-radio label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>

          <el-table-column label="需报销金额" prop="totalFee" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.totalFee" placeholder="请输入需报销金额" />
            </template>
          </el-table-column>
          <el-table-column label="已发放金额" prop="paidFee" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.paidFee" placeholder="请输入已发放金额" />
            </template>
          </el-table-column>
          <el-table-column label="报销项目数量" prop="amount" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.amount" placeholder="请输入报销项目数量" />
            </template>
          </el-table-column>
          <el-table-column label="归属学院" prop="ownerDepId" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.ownerDepId" placeholder="请输入归属学院" />
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 关联成果弹窗 -->
    <el-dialog 
      title="可报销未关联成果列表" 
      v-model="associateDialogVisible" 
      width="900px" 
      append-to-body
      :close-on-click-modal="false"
      @close="handleCloseAssociateDialog"
    >
      <!-- 筛选表单 -->
      <el-form :model="associateQueryParams" ref="associateQueryRef" :inline="true" label-width="80px">
        <el-form-item label="作品名称" prop="name">
          <el-input 
            v-model="associateQueryParams.name" 
            placeholder="请输入作品名称" 
            clearable 
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="associateQueryParams.category" placeholder="请选择类别" clearable style="width: 120px">
            <el-option 
              v-for="dict in achievement_category" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖级别" prop="level">
          <el-select v-model="associateQueryParams.level" placeholder="请选择级别" clearable style="width: 120px">
            <el-option 
              v-for="dict in award_level_type" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级" prop="grade">
          <el-select v-model="associateQueryParams.grade" placeholder="请选择等级" clearable style="width: 120px">
            <el-option 
              v-for="dict in award_rank" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleAssociateQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetAssociateQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 成果列表表格 -->
      <el-table 
        v-loading="associateLoading" 
        :data="unassociatedList" 
        @selection-change="handleAssociateSelectionChange"
        height="400"
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="成果ID" align="center" prop="achievementId" width="100" />
        <el-table-column label="作品名称" align="center" prop="name" min-width="180" show-overflow-tooltip />
        <el-table-column label="类别" align="center" prop="category" width="100">
          <template #default="scope">
            <dict-tag :options="achievement_category" :value="scope.row.category"/>
          </template>
        </el-table-column>
        <el-table-column label="获奖级别" align="center" prop="level" width="100">
          <template #default="scope">
            <dict-tag :options="award_level_type" :value="scope.row.level"/>
          </template>
        </el-table-column>
        <el-table-column label="获奖等级" align="center" prop="grade" width="100">
          <template #default="scope">
            <dict-tag :options="award_rank" :value="scope.row.grade"/>
          </template>
        </el-table-column>
        <el-table-column label="团队名称" align="center" prop="teamName" min-width="150" show-overflow-tooltip />
        <el-table-column label="报名费" align="center" prop="fee" width="100">
          <template #default="scope">
            <span>{{ scope.row.fee ? '¥' + formatMoney(scope.row.fee) : '-' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="associateTotal > 0"
        :total="associateTotal"
        v-model:page="associateQueryParams.pageNum"
        v-model:limit="associateQueryParams.pageSize"
        @pagination="getUnassociatedList"
      />

      <template #footer>
        <div class="dialog-footer">
          <el-button 
            type="primary" 
            :loading="associateSubmitLoading" 
            :disabled="selectedAchievements.length === 0"
            @click="handleConfirmAssociate"
          >确认关联至当前报销项目</el-button>
          <el-button @click="associateDialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 成果详情组件 -->
    <AchievementForm
      ref="achievementFormRef"
      :get-fn="getManage"
      :read-only="true"
      :show-submit="false"
      cancel-text="关闭"
    />
  </div>
</template>

<script setup name="Reimbursement">
import { ref, reactive, toRefs, getCurrentInstance, onMounted, computed } from 'vue'
import { listReimbursement, getReimbursement, delReimbursement, addReimbursement, updateReimbursement, recalculateReimbursementAmount, listUnassociatedProduct, associateAchievements, cancelAssociation, batchCancelAssociation, getReimbursementProjectInfo, updateProjectStatus } from "@/api/system/Reimbursement"
// 导入图标
import { View, Link, Lock, Edit } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import AchievementForm from '@/views/achievement/component/AchievementForm.vue'
import { getManage } from '@/api/achievement/manage'


const route = useRoute()
const { proxy } = getCurrentInstance()

// 接收从报销项目列表传来的参数
const reimbursementItemId = ref(null)
const projectName = ref('')


const ReimbursementList = ref([])
const samReimbursementItemsList = ref([])
const open = ref(false)
const loading = ref(true)
const calculating = ref(false)
const showSearch = ref(true)
const ids = ref([])
const checkedSamReimbursementItems = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const stats = ref({
  productCount: 0,
  totalAmount: 0,
  paidAmount: 0
})

// 关联成果相关变量
const associateDialogVisible = ref(false)      // 弹窗显示状态
const associateLoading = ref(false)            // 弹窗表格加载状态
const associateSubmitLoading = ref(false)      // 确认按钮加载状态
const unassociatedList = ref([])               // 未关联成果列表
const associateTotal = ref(0)                  // 未关联成果总数
const selectedAchievements = ref([])           // 选中的成果列表

// 当前项目状态（需要从路由或接口获取）
const currentProjectStatus = ref('0')          // '0'-进行中(未确认), '1'-已完成(已确认)
const confirmLoading = ref(false)

// 计算按钮禁用状态
const isProjectConfirmed = computed(() => {
  return currentProjectStatus.value === '1'
})

// 关联按钮是否禁用
const associateButtonDisabled = computed(() => {
  return isProjectConfirmed.value || !reimbursementItemId.value
})

// 批量取消关联按钮禁用
const batchCancelDisabled = computed(() => {
  return isProjectConfirmed.value || multiple.value || !reimbursementItemId.value
})

// 计算按钮禁用
const calculateDisabled = computed(() => {
  return isProjectConfirmed.value || !reimbursementItemId.value
})

// 详情组件引用
const achievementFormRef = ref(null)

/**
 * 查看成果详情
 */
const handleViewDetail = (row) => {
  achievementFormRef.value.open(row.achievementId)
}

/**
 * 切换项目状态（确认/取消确认报销清单）
 */
const handleToggleStatus = async () => {
  const isConfirming = currentProjectStatus.value !== '1'
  const actionText = isConfirming ? '确认' : '取消确认'
  
  try {
    await proxy.$modal.confirm(
      isConfirming 
        ? '确认报销清单后，将无法再关联成果、修改报销金额等操作。是否确认？'
        : '取消确认后，清单将恢复可编辑状态。是否继续？',
      `${actionText}报销清单`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }
  
  confirmLoading.value = true
  
  try {
    const newStatus = isConfirming ? '1' : '0'
    const res = await updateProjectStatus(reimbursementItemId.value, newStatus)
    
    if (res.code === 200) {
      currentProjectStatus.value = newStatus
      proxy.$modal.msgSuccess(`${actionText}成功！`)
      
      // 刷新列表
      await loadDetailByReimbursementItemId()
    } else {
      throw new Error(res.msg || `${actionText}失败`)
    }
  } catch (error) {
    console.error(`${actionText}失败:`, error)
    proxy.$modal.msgError(error.message || `${actionText}失败，请稍后重试`)
  } finally {
    confirmLoading.value = false
  }
}

// 关联弹窗查询参数
const associateQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  category: null,
  level: null,
  grade: null,
  ownerDepId: null
})


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    reimbursementItemId: null,  // 这个字段用于筛选已关联的成果
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
    // 新增报销相关查询参数
    reimbursementTime: null,
    status: null,
  },
  rules: {
    achievementId: [
      { required: true, message: "成果ID不能为空", trigger: "blur" }
    ],
    sessionId: [
      { required: true, message: "届次不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "类别不能为空", trigger: "change" }
    ],
    level: [
      { required: true, message: "获奖级别不能为空", trigger: "change" }
    ],
    grade: [
      { required: true, message: "获奖等级不能为空", trigger: "change" }
    ],
    certificateNo: [
      { required: true, message: "证书编号不能为空", trigger: "blur" }
    ],
    awardTime: [
      { required: true, message: "获奖时间不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 字典数据 - 使用 proxy.useDict 方式
const { 
  achievement_category, 
  group_type, 
  award_rank, 
  award_level_type 
} = proxy.useDict('achievement_category', 'group_type', 'award_rank', 'award_level_type')


// 页面加载时执行
onMounted(() => {
  // 获取URL参数
  reimbursementItemId.value = route.query.reimbursementItemId
  projectName.value = route.query.name || '报销项目详情'
  
  // 从路由state获取项目状态（如果有）
  if (route.query.status) {
    currentProjectStatus.value = route.query.status
  }
  
  console.log('接收到的项目ID:', reimbursementItemId.value)
  console.log('项目名称:', projectName.value)
  console.log('项目状态:', currentProjectStatus.value)
  
  if (reimbursementItemId.value) {
    // 如果有项目ID，只加载该项目的详情
    loadDetailByReimbursementItemId()
    // 可选：修改页面标题
    title.value = projectName.value + ' - 报销详情'
  } else {
    // 否则加载全部列表
    getList()
  }
})

// 根据报销项目ID加载详情
const loadDetailByReimbursementItemId = async () => {
  loading.value = true
  try {
    // 获取项目信息（包含状态）
    if (reimbursementItemId.value) {
      const projectRes = await getReimbursementProjectInfo(reimbursementItemId.value)
      if (projectRes.code === 200) {
        currentProjectStatus.value = projectRes.data.status || '0'
      }
    }
    
    // 获取成果列表
    const listRes = await listReimbursement({
      reimbursementItemId: reimbursementItemId.value,
      pageNum: queryParams.value.pageNum,
      pageSize: queryParams.value.pageSize
    })
    
    ReimbursementList.value = listRes.rows
    total.value = listRes.total
    loading.value = false
    console.log('加载的详情数据:', ReimbursementList.value)
    updateStatsFromList(listRes.rows)  // 添加这行，更新统计卡片
  } catch (error) {
    console.error('加载失败:', error)
    loading.value = false
  }
}

/** 查询报销项目详情列表 */
function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  
  // 只有在详情页面（有 reimbursementItemId）时才传递该参数
  if (reimbursementItemId.value) {
    params.reimbursementItemId = reimbursementItemId.value
  }
  
  // 移除空值参数
  Object.keys(params).forEach(key => {
    if (params[key] === null || params[key] === '' || params[key] === undefined) {
      delete params[key]
    }
  })
  
  console.log('搜索参数:', params)
  
  listReimbursement(params).then(response => {
    ReimbursementList.value = response.rows
    total.value = response.total
    loading.value = false
    updateStatsFromList(response.rows)  // 添加这行，更新统计卡片
  }).catch(error => {
    console.error("获取列表失败:", error)
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
    isReimburse: null,
    reimbursementDate: null,
    reimbursementRatio: null,
    reimbursementFee: null,
    fee: null,
    isSupplement: null,
    year: null,
    ownerDepId: null,
    isReimburse: null,
    isSupplement: null,
    fee: null,
    reimbursementFee: null,
    reimbursementRatio: null,
    reimbursementItemId: null,
    reimbursementDate: null,
    itemIndex: null,
    qualityIndex: null,
    submittedAt: null,
    reviewedAt: null,
    schoolReviewedAt: null,
    reviewResult: null,
    schooiReviewResult: null,
    reviewReason: null,
    schoolReviewReason: null,
    auditBy: null,
    schoolAuditBy: null,
    createBy: null,
    updateBy: null,
    createTime: null,
    updateTime: null,
    delFlag: null,
    remark: null
  }
  samReimbursementItemsList.value = []
  proxy.resetForm("ReimbursementRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  console.log('搜索参数:', queryParams.value)
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  // 重置查询参数
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    reimbursementItemId: reimbursementItemId.value,  // 保留项目ID
    achievementId: null,
    sessionId: null,
    category: null,
    name: null,
    teamName: null,
    level: null,
    grade: null,
    track: null,
    certificateNo: null,
    groupId: null,
    awardTime: null,
    reimbursementTime: null,
    status: null
  }
  
  // 重置表单
  if (proxy.$refs.queryRef) {
    proxy.$refs.queryRef.resetFields()
  }
  
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.achievementId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加报销项目详情"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _achievementId = row.achievementId || ids.value
  getReimbursement(_achievementId).then(response => {
    form.value = response.data
    samReimbursementItemsList.value = response.data.samReimbursementItemsList || []
    open.value = true
    title.value = "修改报销项目详情"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ReimbursementRef"].validate(valid => {
    if (valid) {
      form.value.samReimbursementItemsList = samReimbursementItemsList.value
      if (form.value.achievementId != null) {
        updateReimbursement(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReimbursement(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _achievementIds = row.achievementId || ids.value
  proxy.$modal.confirm('是否确认删除报销项目详情编号为"' + _achievementIds + '"的数据项？').then(function() {
    return delReimbursement(_achievementIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 报销项目序号 */
function rowSamReimbursementItemsIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 报销项目添加按钮操作 */
function handleAddSamReimbursementItems() {
  let obj = {
    reimbursementTime: "",
    totalFee: "",
    paidFee: "",
    amount: "",
    ownerDepId: "",
    status: "",
    remark: ""
  }
  samReimbursementItemsList.value.push(obj)
}

/** 报销项目删除按钮操作 */
function handleDeleteSamReimbursementItems() {
  if (checkedSamReimbursementItems.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的报销项目数据")
  } else {
    const samReimbursementItemss = samReimbursementItemsList.value
    const checkedSamReimbursementItemss = checkedSamReimbursementItems.value
    samReimbursementItemsList.value = samReimbursementItemss.filter(function(item) {
      return checkedSamReimbursementItemss.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleSamReimbursementItemsSelectionChange(selection) {
  checkedSamReimbursementItems.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/Reimbursement/export', {
    ...queryParams.value
  }, `Reimbursement_${new Date().getTime()}.xlsx`)
}

/** 计算报销金额 */
async function handleRecalculate() {
  if (!reimbursementItemId.value) {
    proxy.$modal.msgError('请先选择报销项目')
    return
  }
  
  calculating.value = true
  try {
    const res = await recalculateReimbursementAmount(reimbursementItemId.value)
    console.log('计算返回结果:', res)
    
    if (res.code === 200) {
      const data = res.data
      proxy.$modal.msgSuccess(`计算完成！共处理 ${data?.productCount || 0} 个成果，总金额：¥${data?.totalAmount || 0}`)
      
      // 直接使用后端返回的统计信息
      stats.value = {
        productCount: data?.productCount || 0,
        totalAmount: (data?.totalAmount || 0).toFixed(2),
        paidAmount: (data?.paidAmount || 0).toFixed(2)
      }
      
      // 重新加载列表
      await loadDetailByReimbursementItemId()
    } else {
      proxy.$modal.msgError(res.msg || '计算失败')
    }
  } catch (error) {
    console.error('计算失败:', error)
    proxy.$modal.msgError(error.message || '计算失败，请稍后重试')
  } finally {
    calculating.value = false
  }
}

// 获取未关联的成果列表
const getUnassociatedList = async () => {
  associateLoading.value = true
  try {
    // 设置归属学院ID（从当前用户或项目获取）
    associateQueryParams.ownerDepId = form.value.ownerDepId
    
    const res = await listUnassociatedProduct(associateQueryParams)
    unassociatedList.value = res.rows || []
    associateTotal.value = res.total || 0
  } catch (error) {
    console.error('获取未关联成果失败:', error)
    proxy.$modal.msgError('获取未关联成果列表失败')
  } finally {
    associateLoading.value = false
  }
}

// 打开关联成果弹窗
const handleOpenAssociateDialog = () => {
  // 重置选中状态
  selectedAchievements.value = []
  // 重置查询参数
  resetAssociateQuery()
  // 打开弹窗
  associateDialogVisible.value = true
  // 加载数据
  getUnassociatedList()
}

// 关闭关联成果弹窗
const handleCloseAssociateDialog = () => {
  // 重置选中状态
  selectedAchievements.value = []
  // 重置查询参数
  resetAssociateQuery()
}

// 关联成果搜索
const handleAssociateQuery = () => {
  associateQueryParams.pageNum = 1
  getUnassociatedList()
}

// 重置关联搜索
const resetAssociateQuery = () => {
  associateQueryParams.name = null
  associateQueryParams.category = null
  associateQueryParams.level = null
  associateQueryParams.grade = null
  associateQueryParams.pageNum = 1
  handleAssociateQuery()
}

// 弹窗中选择变化
const handleAssociateSelectionChange = (selection) => {
  selectedAchievements.value = selection
}

// 确认关联成果
const handleConfirmAssociate = async () => {
  if (selectedAchievements.value.length === 0) {
    proxy.$modal.msgWarning("请选择要关联的成果")
    return
  }
  
  // 确认对话框
  try {
    await proxy.$modal.confirm(
      `是否确认关联选中的 ${selectedAchievements.value.length} 个成果？`,
      '关联确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
  } catch {
    return
  }
  
  associateSubmitLoading.value = true
  
  try {
    const achievementIds = selectedAchievements.value.map(item => item.achievementId)
    const reimbursementItemIdValue = reimbursementItemId.value
    
    const res = await associateAchievements(achievementIds, reimbursementItemIdValue)
    
    if (res.code === 200) {
      proxy.$modal.msgSuccess(`关联成功！已关联 ${res.data?.successCount || achievementIds.length} 个成果`)
      
      associateDialogVisible.value = false
      selectedAchievements.value = []
      
      // 刷新列表和统计
      await loadDetailByReimbursementItemId()  // 使用这个方法，它会同时更新列表和统计
      
      // 可选：自动计算金额
      await handleRecalculate()
    } else {
      throw new Error(res.msg || "关联失败")
    }
  } catch (error) {
    console.error("关联失败:", error)
    proxy.$modal.msgError(error.message || "关联失败，请稍后重试")
  } finally {
    associateSubmitLoading.value = false
  }
}

// 格式化金额
const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return parseFloat(value).toFixed(2)
}

/**
 * 从成果列表计算统计信息
 */
const updateStatsFromList = (list) => {
  if (!list || list.length === 0) {
    stats.value = {
      productCount: 0,
      totalAmount: 0,
      paidAmount: 0
    }
    return
  }
  
  let totalAmount = 0
  let paidAmount = 0
  
  list.forEach(item => {
    // 获取需报销金额（报名费）或实际报销金额
    // 根据业务，总金额应该是需报销金额的总和
    const fee = parseFloat(item.fee) || 0  // 需报销金额（报名费）
    totalAmount += fee
    
    // 已发放金额：已报销的成果的实际报销金额
    const isReimburse = item.isReimburse === '1' || item.is_reimburse === '1'
    if (isReimburse) {
      const reimbursementFee = parseFloat(item.reimbursementFee) || parseFloat(item.reimbursement_fee) || 0
      paidAmount += reimbursementFee
    }
  })
  
  stats.value = {
    productCount: list.length,
    totalAmount: totalAmount.toFixed(2),
    paidAmount: paidAmount.toFixed(2)
  }
  
  console.log('统计已更新:', stats.value)
}

/**
 * 取消单个成果关联
 */
const handleCancelAssociation = async (row) => {
  // 确认对话框
  try {
    await proxy.$modal.confirm(
      `确定要将成果"${row.name}"从当前报销项目中取消关联吗？`,
      '取消关联确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }
  
  try {
    const res = await cancelAssociation(row.achievementId, reimbursementItemId.value)
    
    if (res.code === 200) {
      proxy.$modal.msgSuccess("取消关联成功")
      // 刷新列表和统计
      await loadDetailByReimbursementItemId()
      await handleRecalculate()
    } else {
      throw new Error(res.msg || "取消关联失败")
    }
  } catch (error) {
    console.error("取消关联失败:", error)
    proxy.$modal.msgError(error.message || "取消关联失败，请稍后重试")
  }
}

/**
 * 批量取消关联
 */
const handleBatchCancelAssociation = async () => {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning("请选择要取消关联的成果")
    return
  }
  
  try {
    await proxy.$modal.confirm(
      `确定要将选中的 ${ids.value.length} 个成果从当前报销项目中取消关联吗？`,
      '批量取消关联确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }
  
  try {
    const res = await batchCancelAssociation(ids.value, reimbursementItemId.value)
    
    if (res.code === 200) {
      proxy.$modal.msgSuccess(res.msg)
      // 清空选中
      ids.value = []
      // 刷新列表和统计
      await loadDetailByReimbursementItemId()
      await handleRecalculate()
    } else {
      throw new Error(res.msg || "批量取消关联失败")
    }
  } catch (error) {
    console.error("批量取消关联失败:", error)
    proxy.$modal.msgError(error.message || "批量取消关联失败，请稍后重试")
  }
}

// 初始化加载数据
getList()
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-tooltip__trigger {
  display: inline-block;
}

/* 抽屉样式 */
.drawer-content {
  padding: 0 20px;
  height: 100%;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.section-title .el-icon {
  font-size: 18px;
  color: #409eff;
}

.empty-tip {
  text-align: center;
  padding: 20px;
  color: #909399;
  background: #f5f7fa;
  border-radius: 4px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.attachment-item .file-name {
  flex: 1;
  color: #303133;
}

.attachment-item .file-type {
  color: #909399;
  font-size: 12px;
}

.drawer-footer {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #e4e7ed;
  margin-top: 20px;
}
</style>