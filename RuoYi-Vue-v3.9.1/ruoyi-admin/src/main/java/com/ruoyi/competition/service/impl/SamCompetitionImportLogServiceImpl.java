package com.ruoyi.competition.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.domain.SamCompetitionImportLog;
import com.ruoyi.competition.mapper.SamCompetitionImportLogMapper;
import com.ruoyi.competition.service.ISamCompetitionImportLogService;
import com.ruoyi.competition.service.ICompetitionService;

/**
 * 竞赛导入日志Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
@Service
public class SamCompetitionImportLogServiceImpl implements ISamCompetitionImportLogService {
    private static final Logger log = LoggerFactory.getLogger(SamCompetitionImportLogServiceImpl.class);

    @Autowired
    private SamCompetitionImportLogMapper samCompetitionImportLogMapper;

    @Autowired
    private ICompetitionService competitionService;

    @Override
    public SamCompetitionImportLog selectSamCompetitionImportLogById(Long id) {
        return samCompetitionImportLogMapper.selectSamCompetitionImportLogById(id);
    }

    @Override
    public List<SamCompetitionImportLog> selectSamCompetitionImportLogList(SamCompetitionImportLog samCompetitionImportLog) {
        return samCompetitionImportLogMapper.selectSamCompetitionImportLogList(samCompetitionImportLog);
    }

    @Override
    public int insertSamCompetitionImportLog(SamCompetitionImportLog samCompetitionImportLog) {
        return samCompetitionImportLogMapper.insertSamCompetitionImportLog(samCompetitionImportLog);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int undoImport(Long logId) {
        SamCompetitionImportLog importLog = samCompetitionImportLogMapper.selectSamCompetitionImportLogById(logId);
        if (importLog == null) {
            throw new ServiceException("导入日志不存在");
        }
        if ("1".equals(importLog.getStatus())) {
            throw new ServiceException("该导入已撤销，请勿重复操作");
        }

        String modifiedIdsStr = importLog.getModifiedIds();
        if (StringUtils.isBlank(modifiedIdsStr)) {
            return 0;
        }

        String tagToRemove = importLog.getTagCode();
        String[] ids = modifiedIdsStr.split(",");
        int undoCount = 0;

        for (String idStr : ids) {
            Long id = Long.parseLong(idStr);
            Competition competition = competitionService.selectCompetitionById(id);
            if (competition != null && StringUtils.isNotBlank(competition.getTags())) {
                String currentTags = competition.getTags();
                String[] tagArray = currentTags.split(",");
                StringBuilder newTags = new StringBuilder();
                boolean removed = false;

                for (String tag : tagArray) {
                    if (tag.equals(tagToRemove)) {
                        removed = true;
                        continue;
                    }
                    if (newTags.length() > 0) {
                        newTags.append(",");
                    }
                    newTags.append(tag);
                }

                if (removed) {
                    Competition updateComp = new Competition();
                    updateComp.setId(id);
                    updateComp.setTags(newTags.toString());
                    competitionService.updateCompetition(updateComp);
                    undoCount++;
                }
            }
        }

        // 更新日志状态为已撤销
        importLog.setStatus("1");
        samCompetitionImportLogMapper.updateSamCompetitionImportLog(importLog);

        log.info("导入撤销完成：日志ID={}, 恢复记录数={}", logId, undoCount);
        return undoCount;
    }
}
